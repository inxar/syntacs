/**
 * $Id: LALR1Constructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
 *
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package com.inxar.syntacs.automaton.pushdown;

import java.util.*;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>LRConstructor</code> that builds
 * LALR1 parse tables.  The implementation of this class was coded
 * based directly on: <code> DeRemer, F.; and Pennello, T.  Efficient
 * computation of LALR(1) look-ahead sets, ACM
 * Trans. Program. Lang. Syst. 4 (1982), 615-649.  </code> as well as
 * the simpler explanation of this algorithm given in: <code> The
 * Theory and Practice of Compiler Writing (McGraw-Hill Computer
 * Science Series) by Jean-Paul Tremblay, Paul G. Sorenson </code>.
 */
public class LALR1Constructor
    implements DPAConstructor, Vizualizable
{
    private static final boolean DEBUG = false;

    private static boolean verbose = 
	Mission.control().isTrue("verbose");

    /**
     * Constructs the <code>LALR1Constructor</code>.  The
     * <code>setGrammar(ContextFreeSet)</code> method needs to be
     * called before the <code>construct()</code> method.  
     */
    public LALR1Constructor()
    {
    }
    
    public DPA construct(ContextFreeSet grammar) throws AlgorithmException
    {
	this.grammar = grammar;

	// vector to hold the all important non-terminal transitions.
	this.nt_transitions = new Vector();
	this.t_transitions = new Vector();

	// NOTE: Use of a bitvector set is completely unnecessary
	// since the set of transitions is always a sequence [ 0 .. n
	// ), not a sparse set.  But, this is an optimization for
	// later.
	this.nt_set = new BitSetIntSet(64);

	// the dpa constructor
	this.dpa = new ArrayDPAConstructor(grammar);
	
	if (verbose)
	    log().info()
		.write("Constructing LR(0) Machine (shifts)")
		.time();

	// make the LR(0) Machine
	this.automaton = this.new LR0Automaton();
	this.automaton.init();

	if (verbose)
	    log().info()
		.write("Constructing lookahead sets (reductions)")
		.time();
	
	// make the lookahead sets
	this.lookahead = this.new Lookahead();
	this.lookahead.init();
	this.lookahead.encode();

	if (verbose)
	    log().info()
		.write("Finalizing DPA construction")
		.time();
	
	// return from the dpa
	DPA _dpa = dpa.construct(grammar);

	if (verbose)
	    log().info()
		.touch();

	return _dpa;
    }
    
    private NTTransition newNTTransition(State state, NonTerminal label)
    {
	// make a new ntt
	NTTransition nt_transition =
	    new NTTransition(nt_transitions.size(), state, label);
	// add to the vector
	nt_transitions.addElement(nt_transition);
	// and add to the set.
	nt_set.put(nt_transition.ID);
	// done
	return nt_transition;
    }
    
    private TTransition newTTransition(State state, Terminal label)
    {
	// make a new ntt
	TTransition t_transition =
	    new TTransition(t_transitions.size(), state, label);
	// add to the vector
	t_transitions.addElement(t_transition);
	// done
	return t_transition;
    }
    
    private NTTransition getNonTerminalTransition(int ID)
    {
	return (NTTransition)nt_transitions.elementAt(ID);
    }
    
    private TTransition getTerminalTransition(int ID)
    {
	return (TTransition)t_transitions.elementAt(ID);
    }
    
    public String toString()
    {
	StringBuffer b = new StringBuffer();
	b.append(grammar);
	b.append(automaton);
	line(b);
	printNonTerminalTransitions(b);
	line(b);
	printNonTerminalRelation("READS", lookahead.reads, b);
	line(b);
	printNonTerminalRelation("INCLUDES", lookahead.includes, b);
	line(b);
	printReductionRelation("LOOPBACK", lookahead.loopback, b);
	line(b);
	printReductions(b);
	line(b);
	b.append(dpa.toString());
	line(b);
	return b.toString();
    }
    
    public void vizualize(GraphViz dot)
    {
	dpa.vizualize(dot);
	
	if (Mission.control().isTrue("viz-dpa-hide-loopback-edges"))
	    return;
	
	lookahead.vizualize(dot);
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("lpc", this); // Lalr Pushdown Constructor
	return log;
    }
    
    private ContextFreeSet grammar;
    private LR0Automaton automaton;
    private Lookahead lookahead;
    private ArrayDPAConstructor dpa;
    private Vector t_transitions;
    private Vector nt_transitions;
    private IntSet nt_set;
    private Log log;

    final class LR0Automaton 
    {
	LR0Automaton() throws AlgorithmException, AmbiguityException
	{
	    // repository of states
	    this.states = new Vector();
	    // associates kernel sets with states for the lookup()
	    this.map = new Hashtable();
	}

	public IntSet getState(int ID)
	{
	    return ((State)states.elementAt(ID)).kernel;
	}
	
	void init() throws AmbiguityException
	{
	    // start by taking the closure of the initial item of the
	    // start production
	    IntSet first = new BitSetIntSet(11);
	    // put the start productions initial item in the set
	    first.put( grammar.getStart().getInitialItem().getID() );
	    // make the first state and resolve it
	    State state = lookup(first);
	    // delegate construction to state
	    state.resolve();
	}
	
	/**
	 * This method returns the state of the given set.  If the set
	 * is not already in the canonical collection, a new set is
	 * allocated.  
         */
	State lookup(IntSet kernel)
	{
	    Object o = map.get(kernel);
	    return o == null ? newState(kernel) : (State)o;
	}
	
	State newState(IntSet kernel)
	{
	    if (DEBUG) 
		log().debug()
		    .write("Adding state ")
		    .write(states.size())
		    .write(" for kernel ")
		    .write(kernel)
		    .out();

	    // make a new state from the given kernel set of items
	    // with ID equal to the current vector size (it's
	    // position).
	    State state = new State(states.size(), kernel);
	    states.addElement(state);
	    // associate for future lookups
	    map.put(kernel, state);
	    return state;
	}
	
	/**
	 * Gets a set by ID (position)
	 */
	public State lookup(int ID)
	{
	    return (State)states.elementAt(ID);
	}

	public int size()
	{
	    return states.size();
	}
	
	public String toString()
	{
	    StringBuffer b = new StringBuffer();
	    heading("Automaton", b);
	    for (int i=0; i<states.size(); i++) {
		b.append(states.elementAt(i));
	    }
	    return b.toString();
	}

	private Vector states;
	private Hashtable map;
    }
    
    final class State
    {
	State(int ID, IntSet kernel)
	{
	    this.ID = ID;
	    this.kernel = kernel;
	    this.items = new TreeListIntRelation();
	    this.go = new HashIntFunction();
	    this.og = new HashBitSetIntRelation();

	    // initialize the marked set for use in the classify()
	    // method.
	    this.marked = new BitSetIntSet(32);
	    // get an iterator over the items in the set
	    IntIterator iterator = kernel.iterator();
	    // run the iterator
	    while (iterator.hasNext())
	        // get each item and classify it
		classify( grammar.getItem(iterator.next()) );
	    // no need for marked set anymore
	    this.marked = null;
	}
	
	// classify the items in the set such that subsequent
	// processing may be more effecient
	private void classify(Item item)
	{
	    if (DEBUG) 
		log().debug()
		    .write("starting classification of item #")
		    .write(item.getID())
		    .write(": ")
		    .write(item)
		    .over();

	    // is it an initial item?
	    if (!item.hasPrevious()) {
		if (DEBUG) 
		    log().debug()
			.write("--> item is initial: ")
			.write(item)
			.out();

		// push as root of list of initial items
		initials = new ItemLink(item, initials);
		
		// is this a final item?
	    } if (!item.hasNext()) {
		if (DEBUG) 
		    log().debug()
			.write("--> item is final: ")
			.write(item)
			.out();

		// push as root of list of final items
		finals = new ItemLink(item, finals);
		
	    } else {

		// evaluate on the grammar symbol the dot in the item
		// is facing
		GrammarSymbol symbol = item.nextSymbol();

		// add this item to the tree list such that we sort
		// next items according to their transition symbols
		items.put(symbol.getID(), item.nextItem().getID());

		// if we've already seen this symbol, there's no need
		// to continue classifying it since that information
		// is already 'classified'.
		if ( isMarked(symbol.getID()) )
		    return;

		// if there is a next item, check the nextSymbol and
		// classify according to the transition type
		if (symbol.isTerminal()) {

		    if (DEBUG) 
			log().debug()
			    .write("--> item transitions over a terminal: ")
			    .write(item)
			    .out();

		    // Make a terminal transition.
		    t_links = new TTransitionLink
			(newTTransition(this, (Terminal)symbol), t_links);

		} else {

		    if (DEBUG) 
			log().debug()
			    .write("--> item transitions over a nonterminal: ")
			    .write(item)
			    .out();

		    // Make a nonterminal transition.
		    nt_links = new NTTransitionLink
			(newNTTransition(this, (NonTerminal)symbol), nt_links);
		    
		    // append to the list of nullable transitions if
		    // this is an item of the form [ A -> alpha <> B
		    // theta ] and theta derives epsilon
		    if ( isNullable(item.nextItem()) ) {
			thetas = new ThetaItemLink(nt_links.transition, item, thetas);
		    }
		    
		    // the set upon which this state was built is the
		    // set of kernel items.  However, we still need to
		    // sort and classify the entire set of kernel and
		    // non-kernel items.  Therefore, if we encounter
		    // an item like [ A -> alpha <> B theta ], we need
		    // to evaluate closure on B.
		    closure( (NonTerminal)symbol );
		}
	    }

	    if (DEBUG) 
		log().debug()
		    .write("finished classification of item #")
		    .write(item.getID())
		    .write(": " )
		    .write(item)
		    .back();
	}
	
	public void resolve() throws AmbiguityException
	{
	    // check to make sure we have not been resoved yet. if
	    // not, then mark
	    if (isResolved) return; else isResolved = true;
	    
	    // our goal is to resolve all transitions such that we
	    // form the automaton graph.  We have made a list of
	    // transitions.  Iterate each key, values pair in the
	    // items set map.
	    Reiterator reiterator = items.reiterator();
	    // an arbitrary state
	    State state = null;
	    // iterate the reiterator
	    while (reiterator.hasNext()) {
		// get the state for this transition from the set of
		// items the this node of the reiterator
		state = automaton.lookup(reiterator.values());
		// add a transition to this state for this grammar symbol.
		connect(reiterator.key(), state);
		// make sure the state itself has been resolved
		state.resolve();
		// advance the pointer
		reiterator.next();
	    }
	}
	
	private void connect(int symbol, State state) throws AmbiguityException
	{
	    // fetch the grammar symbol having this ID
	    GrammarSymbol grammarSymbol = grammar.getGrammarSymbol(symbol);
	    // add SHIFT instructions for instances in which symbol is a terminal,
	    // goto if it is a nonterminal
	    if (grammarSymbol.isTerminal()) {
		// now check for the special case in which this
		// grammar symbol is the STOP token in which case an
		// ACCEPT instruction should be signaled
		if ( ((Terminal)grammarSymbol).getToken().getID() == Token.STOP ) {
		    dpa.accept(this.ID, symbol);
                } else {
		    dpa.shift(this.ID, symbol, state.ID);
		}
	    } else {
		dpa.go(this.ID, symbol, state.ID);
	    }
	    
	    // what we are doing here is building the state transition
	    // graph that the graph can be traversed in a forward or
	    // reverse direction.
	    this. go.put(symbol, state.ID);
	    state.og.put(symbol, this. ID);
	}
	
	private boolean isNullable(Item item)
	{
	    /*
	      Note: this information may be cacheable.  Think about
	      how many potential repeat operations this will have and
	      consider a boolean array or bitset across the number of
	      items, true if the thing is nullable (as suggested by
	      DeRemer).
	      
	      Note; need ternary indicator: (yes|no|unknown) since we
	      will be calculating it on the fly.  
             */
	    
	    // test if this is the last item [ A -> alpha <> ]
	    if (!item.hasNext())
		return true;
	    
	    /*
	      do we need another Item here or can we reuse the
	      reference given by the method itself?  
             */
	    
	    // now test each symbol from here until [ A -> alpha <> ]
	    while (item.hasNext()) {
		// break if the next symbol is not nullable
		if (!item.nextSymbol().isNullable())
		    return false;
		// advance
		item = item.nextItem();
	    }
	    // all were nullable
	    return true;
	}

	private void closure(NonTerminal nonTerminal)
	{
	    if (DEBUG) 
		log().debug()
		    .write("--> evaluating closure over " )
		    .write(nonTerminal)
		    .out();

	    // get all the alternatives for this nonterminal
	    IntArray alternatives = nonTerminal.getReductions();
	    // process each alternative and add its initial item to
	    // the current stack
	    for (int i = 0; i < alternatives.length(); i++) {
		// classify each item [ A -> <> alpha ] (indirect
		// recursion here)
		if (DEBUG) 
		    log().debug()
			.write(nonTerminal)
			.write( ": processing reduction ")
			.write(grammar.getProduction( alternatives.at(i)))
			.out();

		classify( grammar.getProduction( alternatives.at(i) ).getInitialItem() );
	    }
	}

	private boolean isMarked(int symbol)
	{
	    if (marked.contains(symbol)) {
		return true;
	    } else {
		marked.put(symbol);
		return false;
	    }
	}
	
	public String toString()
	{
	    StringBuffer b = new StringBuffer();
	    
	    b.append("state ").append(ID).append(" kernel: ").append(StringTools.NEWLINE);
	    //b.append("\tkernel: ").append(StringTools.NEWLINE);
	    IntIterator i = kernel.iterator();
	    while (i.hasNext())
		b.append(' ').append( grammar.getItem(i.next()) ).append(StringTools.NEWLINE);

	    /*
	      b.append("\tterminal transitions:").append(StringTools.NEWLINE);
	      TTransitionLink t_link = t_links;
	      while (t_link != null) {
	      t_link.transition.toBuffer(b.append('\t')).append(StringTools.NEWLINE);
	      t_link = t_link.next;
	      }
	    */
	    /*
	      b.append("\ttheta items:").append(StringTools.NEWLINE);
	      ThetaItemLink link = thetas;
	      int j = 0;
	      while (link != null) {
	      if (j++>0) b.append(", ");
	      b.append('\t').append(link.item).append(StringTools.NEWLINE);
	      link = link.next;
	      }
            */

	    return b.toString();
	}

	// the assigned state ID number.
	int ID;
	// flag to say if this state has already been resolved
	boolean isResolved;
	// the set of kernel items
	IntSet kernel; 
	// a set used by closure to track which nonTerminals we've
	// processed
	IntSet marked; 
	// a data structure which maps grammar-symbol keys to state
	// ID's on the forward or posterior direction
	IntFunction go; 
	// a data structure which maps grammar-symbol keys to sets of
	// state ID's in the reverse or anterior direction
	IntRelation og; 
	// a data structure which groups item ID's into sets by
	// grammarsymbol ID
	IntRelation items;	
	// root to a list of initial items
	ItemLink initials;	
	// root to a list of final items
	ItemLink finals;	
	// root to a list of items like [ B -> lambda <> A theta ]
	// theta => epsilon
	ThetaItemLink thetas;	
	// root to a list of transitions with a terminal path
	TTransitionLink t_links;	
	// root to a list of transitions with a nonTerminal path
	NTTransitionLink nt_links;	
    }
    
    /**
     * This code is partitioned to explicitly separate the Lookahead
     * set code with the LR(0) machine code and other stuff.  Makes it
     * easier to change and perhaps do different implementations to
     * compare them.  
     */
    final class Lookahead implements Vizualizable
    {
	Lookahead() throws AlgorithmException
	{
	    this.reductions = new Vector();
	    // initialize our three relations
	    this.reads    = new HashBitSetIntRelation();
	    this.includes = new HashBitSetIntRelation();
	    this.loopback = new HashBitSetIntRelation();
	}
	
	void init() throws AlgorithmException
	{
	    read();
	    follow();
	    lookahead();
	}
	
	void read() throws LRkViolationException
	{
	    // an arbitrary transition link whoise path is a terminal
	    TTransitionLink t_link = null;
	    // an arbitrary nonTerminal transition
	    NTTransition nt_transition = null;
	    // an arbitrary transition link whoise path is a
	    // nonterminal
	    NTTransitionLink nt_link = null;
	    // a state
	    State next = null;
	    
	    // iterate the list of nonTerminal transitions in the
	    // parent class
	    for (int i=0; i<nt_transitions.size(); i++) {
		
		// fetch each one
		nt_transition = (NTTransition)nt_transitions.elementAt(i);
		// fetch the state obtained by following this
		// nonterminal path to the next state
		next = automaton.lookup
		    (nt_transition.state.go.get(nt_transition.label.getID()));
		
		/* PART 1:	DIRECTLY_READS relation */
		// foreach terminal transition in the list on the
		// state obtained by following this nonTerminal
		// transition, add the ID of the path to the direct
		// read set
		t_link = next.t_links;
		// run through this linked list
		while (t_link != null) {
		    // in the direct read set for the nonterminal
		    // transition we are currently evaluating, add the
		    // ID of the grammarsymbol that is the terminal
		    // path label for the link
		    nt_transition.dread.put( t_link.transition.label.getID() );
		    // advance
		    t_link = t_link.next;
		}
		
		/* PART 2:	INDIRECTLY_READS relation */
		// foreach nonTerminal transition in the next state,
		// see if it is nullable.  If so then add an
		// transition in the reads relation for these two
		// nonterminal trnasitions (p, A) READS (q, B)
		nt_link = next.nt_links;
		// run through this linked list
		while (nt_link != null) {

		    // NOTE [3/27/01]: As I was looking through this
		    // code for other reasons, I noticed that there
		    // was an extra semi-colon which was effectively
		    // making if-statement futile, which seems like a
		    // glaring bug.  The problem is that this code has
		    // been seemingly working fine, so fixing it may
		    // have consequences.  Watch out.

		    // add and transition if the nonterminal is
		    // nullable
		    if ( nt_link.transition.label.isNullable() ) /* ; fixed! */
			reads.put(nt_transition.ID, nt_link.transition.ID);
		    // advance
		    nt_link = nt_link.next;
		}
		
		// union the direct read and read sets
		nt_transition.read.union(nt_transition.dread);
	    }
	    
	    // and evaluate the READS relation if not empty
	    if (!reads.isEmpty())
		reads(reads);
	}

	private void reads(IntRelation reads) throws LRkViolationException
	{
	    // ok, now we have computed all the direct read sets, and
	    // we;ve setup the READS relation for traversal in the
	    // transitive closure algorithm.  Set this up now assuming
	    // the reads relation is not moot.
	    IntRelation input = new DirectReadSetFunction(nt_transitions);
	    IntRelation output = new ReadSetFunction(nt_transitions);

	    // make a new graph algorithm object on these arguments
	    SCCTransitiveClosure graph = 
		new SCCTransitiveClosure(nt_set, reads, input, output);
	    
	    try {
		// ready, go.
		graph.evaluate();
	    } catch (SCCTransitiveClosure.NonTrivialSCCException sccex) {
		throw new LRkViolationException
		    ("The Grammar prescribed is not LR(k) for any k.");
	    }
	}
	
	void follow() throws LRkViolationException
	{
	    // first compute the INCLUDES relation and the LOOPBACK
	    // relation.  We do this in parallel in a loop over the
	    // states since both relations need to test each state.
	    
	    // Two arbitrary states.  Since we will be searching
	    // ('casting') through the finite automaton for (p, A) ->
	    // (q, B) we analogize this to a fishing line and anchor
	    // where the anchor is a and the line is q (or vice
	    // versa).
	    State anchor = null, line = null;

	    // an arbitrary theta item link
	    ThetaItemLink theta = null;
	    // an arbitrary item link
	    ItemLink link = null;
	    // an arbitrary item
	    Item item = null;
	    // an arbitrary nonterminal transition link
	    NTTransitionLink nt_link = null;
	    
	    int len = automaton.states.size();
	    // iterate each state
	    for (int i=0; i<len; i++) {
		
		// fetch state from vector
		anchor = automaton.lookup(i);
		
		//System.out.println("processing state "+anchor);

		// PART 1: INCLUDES -- iterate the list of items like
		// [ A -> lambda B theta ], theta => epsilon.  These
		// have already been sorted for us.
		theta = anchor.thetas;
		// iterate all theta items
		while (theta != null) {
		    // wind backwards through the automaton according
		    // to the symbols on lambda. As we move backwards
		    // however, the automaton is nondeterministic --
		    // at each branchpoint there may be several
		    // possible previous states.  set the line to the
		    // current state
		    line = anchor;
		    // move back until we hit [ A -> <> lambda B theta
		    // ] I guess we'll use recursion (rewind-rewind)
		    // rather than a stack of items for this step.
		    rewind(theta.item, theta.transition.state, theta.transition.ID);
		    // advance to the next theta item
		    theta = theta.next;
		}
		
		// PART 2: LOOPBACK -- iterate the list of initial items
		link = anchor.initials;
		// iterate all initial items [ A -> <> alpha ]
		while (link != null) {
		    // set the item to the first one
		    item = link.item;
		    // set the state to the anchor
		    line = anchor;
		    // trace forward through the automaton until we
		    // hit the reductant item [ A -> alpha <> ]
		    while (item.hasNext()) {
			// set the next state (the tracking line)...
			line = automaton.lookup
			    (
			     // ...to the state given by the automaton
			     // transition function....
			     line.go.get(
					 // ... over the path labeled
					 // by the next symbol.
					 item.nextSymbol().getID()
					 )
			     );
			// advance
			item = item.nextItem();
		    }

		    // now we have found states p and q.  We make a
		    // new reduction object of the form (q, A ->
		    // alpha)
		    Reduction reduction = newReduction(line, item.getProduction());
		    // -- now need to find the nonterminal transition
		    // (p, A) on state p fetch the ID of the
		    // nonteminal for matching
		    int symbol = item.getProduction().getNonTerminal().getID();
		    // linear search the list of nonterminal
		    // transitions rooted on p (not ideal, but
		    // simple...).
		    nt_link = anchor.nt_links;
		    // iterate until we hit the right one
		    while (nt_link != null) {
			// if this the right one?
			if (nt_link.transition.label.getID() == symbol) {
			    // make the connection in the relation now
			    loopback.put(reduction.ID, nt_link.transition.ID);
			    // and break out of the linear search
			    break;
			}
			// advance
			nt_link = nt_link.next;
		    }
		    
		    // advance
		    link = link.next;
		}
	    }
	}
	
	/**
	 * Unwind through the state graph (the automaton) until the
	 * initial item is hit.  When this happens, add an entry in
	 * the includes relation (is there a better way to do this?)  
         */
	private void rewind(Item item, State state, int qB)
	{
	    // if we are at the first position, connect
	    if (item.hasPrevious()) {
		// take an iterator over the set of states the can
		// precede this one by following the previous symbol
		// in lambda
		IntIterator previous = 
		    state.og.get(item.previousSymbol().getID()).iterator();
		
		// iterate all possible previous states
		while (previous.hasNext()) 
		    // wind back line to the previous state
		    rewind(item.previousItem(),
			   automaton.lookup(previous.next()),
			   qB);
		
	    } else {

		// ok, we've arrived at the first item [ B -> <>
		// lambda A theta ] and thus state p by stepping
		// through the automaton backwards across lambda.  We
		// now need to identify the ID number of the
		// nonterminal transition (p, A) here on state p.
		int pA = -1;
		// this is the nonterminal ID we want to match
		int nt = item.getProduction().getNonTerminal().getID();
		
		// start at the beginning of the list
		NTTransitionLink link = state.nt_links;
		// linear search -- ok.
		while (link != null) {
		    if (link.transition.label.getID() == nt) {
			pA = link.transition.ID;
		    }
		    // advance
		    link = link.next;
		}
		
		// check to make sure we got one.  Not sure what this
		// implies...  either subset construction bad
		if (pA == -1)
		    throw new InternalError("Huh?");

		// add an entry to the includes relation
		includes.put(qB, pA);
		//includes.relate(pA, qB);
	    }
	}

	void lookahead() throws LRkViolationException
	{
	    // now we are in the lookahead section, meaning we can
	    // finally get to the point of calculating the LA sets.
	    // There are two tasks here: first is to calculate the
	    // follow sets from the read sets over the INCLUDES
	    // relation, second is to make LA sets over LOOPBACK.
	    
	    /*
	      PART 1: Generation of FOLLOW sets.
	    */
	    IntRelation input = new ReadSetFunction(nt_transitions);
	    IntRelation output = new FollowSetFunction(nt_transitions);

	    // make a new graph algorithm object on these arguments
	    SCCTransitiveClosure graph = 
		new SCCTransitiveClosure(nt_set, includes, input, output);
	    
	    try {
		// ready, go.
		graph.evaluate();
	    } catch (SCCTransitiveClosure.NonTrivialSCCException sccex) {
		throw new LRkViolationException("The Grammar prescribed is not LR(k) for any k.");
	    }
	    
	    /*
	      PART 2: Generation of LOOKAHEAD sets.
	    */
	    // the lookahead sets are the union of follow sets over
	    // the loopback relation.  Thus, we need to iterate each
	    // Reduction in the loopback.
	    
	    // handles
	    Reduction reduction = null;
	    IntSet transitions = null;
	    IntIterator trans = null;
	    NTTransition ntt = null;
	    
	    // get a list of all reduction ID's
	    IntIterator keys = loopback.keys().iterator();
	    
	    // process each one
	    while (keys.hasNext()) {
		// get the reduction
		reduction = getReduction(keys.next());
		// now get the set of contributing FOLLOW set nt trans
		// IDs
		transitions = loopback.get(reduction.ID);
		// get iterator over this set of ntt's
		trans = transitions.iterator();
		// run it
		while (trans.hasNext()) {
		    // fetch each nonterminal trnasition which has the
		    // follow set
		    ntt = getNonTerminalTransition(trans.next());
		    // union it's follow set with this la
		    reduction.lookahead.union(ntt.follow);
		}
	    }
	}
	
	/**
	 * Transfers the lookahead set information to the dpa.
	 */
	private void encode() throws AmbiguityException
	{
            Reduction reduction = null;
            IntIterator iterator = null;
	    
            // iterate each reduction object
            for (int i=0; i<reductions.size(); i++) {
                // fetch each one
                reduction = (Reduction)reductions.elementAt(i);
                // iterate the lookahead set
                iterator = reduction.lookahead.iterator();
                // process each terminal named
                while (iterator.hasNext()) {
                    dpa.reduce(reduction.state.ID, iterator.next(), reduction.production.getID());
                }
            }
	}
	
	private Reduction newReduction(State state, Production production)
	{
	    /*
	      need to make a new reduction, but ONLY IF it has not yet
	      been created!  Thus, reduction object should probably
	      live on the state and be created whence an item implies
	      it.  
             */
	    Reduction reduction = null; /* try to assign this reference */
	    
	    /* before optimizing, we'll just run through the list in
               linear search */
	    
	    // a temporary
	    Reduction r = null;
	    // iterate all existing
	    for (int i=0; i<reductions.size(); i++) {
		// fetch
		r = (Reduction)reductions.elementAt(i);
		// test their production IDs and then state ID's
		if (r.state.ID == state.ID && r.production.getID() == production.getID()) {
		    // assign to permanent
		    reduction = r;
		    // done
		    break;
		}
	    }

	    // did we find one?
	    if (reduction == null) {
		// make a new ntt
		reduction = new Reduction(reductions.size(), state, production);
		// add to the vector
		reductions.addElement(reduction);
	    }
	    
	    // done
	    return reduction;
	}
	
	private Reduction getReduction(int ID)
	{
	    return (Reduction)reductions.elementAt(ID);
	}
	
	public void vizualize(GraphViz dot)
	{
	    LRTranslatorGrammar g = (LRTranslatorGrammar)
		Mission.control().get("_lr-translator-grammar");

	    Reduction reduction;
	    IntSet transitions;
	    IntIterator trans;
	    NTTransition ntt;
	    String label, ec, es;

	    GraphViz.Node node;
	    GraphViz.Edge edge;

	    ec = Mission.control().getString
		("viz-dpa-loopback-edge-color", "midnightblue");

	    es = Mission.control().getString
		("viz-dpa-loopback-edge-style", "dotted");
	    
	    node = dot.node("edge");
	    node.attr("color", ec);
	    node.attr("style", es);
	    
	    // get a list of all reduction ID's
	    IntIterator keys = loopback.keys().iterator();
	    
	    // process each one
	    while (keys.hasNext()) {
		// get the reduction
		reduction = getReduction(keys.next());
		// now get the set of contributing FOLLOW set nt trans
		// IDs
		transitions = loopback.get(reduction.ID);
		// get iterator over this set of ntt's
		trans = transitions.iterator();
		// run it
		while (trans.hasNext()) {
		    // fetch each nonterminal trnasition which has the
		    // follow set
		    ntt = getNonTerminalTransition(trans.next());
		    
		    // At this point we have the reduction (q, A ->
		    // alpha) and the nonterminal transition (p, A).
		    // Want to make a dashed edge from q to p having
		    // label A->alpha.
		    edge = dot.edge("s"+reduction.state.ID, "s"+ntt.state.ID);
		    int pID = reduction.production.getID();
		    label = g.getProduction(pID);
		    //label = reduction.production.toString();
		    edge.attr("label", label);
		}
	    }
	}
	
	Vector reductions;
	IntRelation reads;
	IntRelation includes;
	IntRelation loopback;
    }
    
    private static class LRkViolationException
	extends AlgorithmException
    {
	LRkViolationException(String msg)
	{
	    super(msg);
	}
    }
    
    private static class ItemLink
    {
	ItemLink(Item item, ItemLink next)
	{
	    this.item = item;
	    this.next = next;
	}
	Item item;
	ItemLink next;
    }
    
    private static class ThetaItemLink
    {
	ThetaItemLink(NTTransition transition, Item item, ThetaItemLink next)
	{
	    this.transition = transition;
	    this.item = item;
	    this.next = next;
	}
	Item item;
	ThetaItemLink next;
	NTTransition transition;
    }
    
    private static class Transition
    {
	Transition(int ID, State state, GrammarSymbol path)
	{
	    this.ID = ID;
	    this.state = state;
	    this.path = path;
	}
	
	int ID;
	State state;
	GrammarSymbol path;
    }
    
    private static class TTransition
	extends Transition
    {
	TTransition(int ID, State state, Terminal label)
	{
	    super(ID, state, label);
	    this.label = label;
	}
	
	public String toString()
	{
	    StringBuffer b = new StringBuffer();
	    doublet(new Integer(state.ID), label, b);
	    return b.toString();
	}
	
	Terminal label;
    }
    
    private static class NTTransition
	extends Transition
    {
	NTTransition(int ID, State state, NonTerminal label)
	{
	    super(ID, state, label);
	    this.label = label;
	    this.dread = new BitSetIntSet(5);
	    this.read = new BitSetIntSet(5);
	    this.follow = new BitSetIntSet(5);
	}
	
	public String toString()
	{
	    StringBuffer b = new StringBuffer();
	    doublet(new Integer(state.ID), label, b);
	    return b.toString();
	}
	
	NonTerminal label;
	IntSet dread; // direct read set
	IntSet read; // full read set
	IntSet follow; // the follow set
    }
    
    private static class TTransitionLink
    {
	TTransitionLink(TTransition transition, TTransitionLink next)
	{
	    this.transition = transition;
	    this.next = next;
	}
	TTransition transition;
	TTransitionLink next;
    }
    
    private static class NTTransitionLink
    {
	NTTransitionLink(NTTransition transition, NTTransitionLink next)
	{
	    this.transition = transition;
	    this.next = next;
	}
	NTTransition transition;
	NTTransitionLink next;
    }
    
    private static abstract class VectorSetFunction
	implements IntRelation
    {
	VectorSetFunction(Vector vect)
	{
	    this.vect = vect;
	}
	
	public void put(int key, int value)
	{
	    throw new UnsupportedOperationException();
	}
	
	public void set(int key, IntSet set)
	{
	    throw new UnsupportedOperationException();
	}
	
	public IntSet get(int key)
	{
	    throw new UnsupportedOperationException();
	}
	
	public Reiterator reiterator()
	{
	    throw new UnsupportedOperationException();
	}

	public boolean isEmpty()
	{
	    throw new UnsupportedOperationException();
	}


	public IntSet keys() 
	{
	    throw new UnsupportedOperationException();
	}
	
	Vector vect;
    }
    
    private static class DirectReadSetFunction
	extends VectorSetFunction
    {
	DirectReadSetFunction(Vector nt_transitions)
	{
	    super(nt_transitions);
	}
	
	public IntSet get(int key)
	{
	    return ((NTTransition)vect.elementAt(key)).dread;
	}
    }
    
    private static class ReadSetFunction
	extends VectorSetFunction
    {
	ReadSetFunction(Vector nt_transitions)
	{
	    super(nt_transitions);
	}
	
	public void set(int key, IntSet set)
	{
	    ((NTTransition)vect.elementAt(key)).dread = set;
	}
	
	public IntSet get(int key)
	{
	    return ((NTTransition)vect.elementAt(key)).dread;
	}
    }
    
    private static class FollowSetFunction
	extends VectorSetFunction
    {
	FollowSetFunction(Vector nt_transitions)
	{
	    super(nt_transitions);
	}
	
	public void set(int key, IntSet set)
	{
	    ((NTTransition)vect.elementAt(key)).follow = set;
	}
	
	public IntSet get(int key)
	{
	    return ((NTTransition)vect.elementAt(key)).follow;
	}
    }
    
    /**
     * A Reduction models the ordered pair (q, A -> alpha).  This
     * object can also carry the lookahead sets when the time comes.  
     */
    private static class Reduction
    {
	Reduction(int ID, State state, Production production)
	{
	    this.ID = ID;
	    this.state = state;
	    this.production = production;
	    this.lookahead = new BitSetIntSet(8);
	}
	
	public String toString()
	{
	    StringBuffer b = new StringBuffer();
	    doublet(new Integer(state.ID), production, b);
	    return b.toString();
	}
	
	// needs ID since these are used in relations and are
	// identifiable only by ID.
	int ID;
	// the lookahead set to be completed later
	IntSet lookahead;
	// the state q
	State state;
	// the production A -> alpha
	Production production;
    }

    /*-************************************************************
     * STATIC FORMATTING METHODS
     *-************************************************************/
    
    private void heading(String heading, StringBuffer b)
    {
	b.append(heading).append(':').append(StringTools.NEWLINE);
	line(b);
    }
    
    private void line(StringBuffer b)
    {
	b.append("-----------------------------------").append(StringTools.NEWLINE);
    }
    
    private void printNonTerminalTransitions(StringBuffer b)
    {
	NTTransition nt = null;
	heading("NonTerminal transitions", b);
	for (int i = 0; i < nt_transitions.size(); i++) {
	    nt = (NTTransition)nt_transitions.elementAt(i);
	    b.append(nt.ID).append(": ").append(nt).append("\tDR ");
	    printSet(nt.dread, b);
	    b.append("\tREAD ");
	    printSet(nt.read, b);
	    b.append("\tFOLLOW ");
	    printSet(nt.follow, b);
	    b.append(StringTools.NEWLINE);
	}
    }
    
    private void printReductions(StringBuffer b)
    {
	Reduction red = null;
	heading("REDUCTIONS", b);
	for (int i = 0; i < lookahead.reductions.size(); i++) {
	    red = (Reduction)lookahead.reductions.elementAt(i);
	    b.append(red.ID).append(": ").append(red).append("\tLA ");
	    printSet(red.lookahead, b);
	    b.append(StringTools.NEWLINE);
	}
    }
    
    private static void doublet(Object x, Object y, StringBuffer b)
    {
	b.append('(').append(x).append(',').append(y).append(')');
    }
    
    private void printSet(IntSet set, StringBuffer b)
    {
	b.append('{');
	IntIterator iter = set.iterator();
	int i = 0;
	while (iter.hasNext()) {
	    if (i++>0) b.append(',');
	    b.append( grammar.getTerminal(iter.next()) );
	}
	b.append('}');
    }
    
    private void printNonTerminalRelation(String heading, 
					  IntRelation relation, 
					  StringBuffer b)
    {
	heading(heading, b);
	IntIterator i = relation.keys().iterator();
	while (i.hasNext()) {
	    int key = i.next();
	    IntSet set = relation.get(key);
	    NTTransition x = getNonTerminalTransition(key);
	    IntIterator j = set.iterator();
	    while (j.hasNext()) {
		b.append(x).append("\t"+heading+" ");
		b.append( getNonTerminalTransition(j.next()) );
		b.append(StringTools.NEWLINE);
	    }
	}
    }
    
    private void printReductionRelation(String heading, 
					IntRelation relation, 
					StringBuffer b)
    {
	heading(heading, b);
	IntIterator i = relation.keys().iterator();
	while (i.hasNext()) {
	    int key = i.next();
	    IntSet set = relation.get(key);
	    Reduction x = lookahead.getReduction(key);
	    IntIterator j = set.iterator();
	    while (j.hasNext()) {
		b.append(x).append("\t"+heading+" ");
		b.append( getNonTerminalTransition(j.next()) );
		b.append(StringTools.NEWLINE);
	    }
	}
    }
    
}









