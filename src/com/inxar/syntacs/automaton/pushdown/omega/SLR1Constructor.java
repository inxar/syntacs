/**
 * $Id: SLR1Constructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>LRConstructor</code> that builds
 * simple LR1 parse tables.  The algorithm used by this class was
 * coded based directly on the Dragon Book.  
 */
public class SLR1Constructor extends LRConstructor 
    implements Vizualizable
{
    private static final boolean DEBUG = true;

    /**
     * Constructs an <code>SLR1Constructor</code>.
     */
    public SLR1Constructor()
    {
	super();
    }

    /**
     * This method generates all the states in the grammar and builds
     * the parse table internally.  
     */
    protected void init() throws AmbiguityException
    {
        // make the deterministic pda we will fill in
        dpa = new ArrayDPAConstructor(grammar);

        // make a new ItemSet stack
        java.util.Stack stack = new java.util.Stack();
        java.util.Vector seen = new java.util.Vector();

        // start by taking the closure of the start productions
        // initial item
        IntSet first = new BitSetIntSet(1);

        // put the start productions initial item in the set
        first.put(grammar.getStart().getInitialItem().getID());

        // and take the closure of this
        IntSet initialSet = closure(first);

        // we want to set this as the start state
        State startState = lookup(initialSet);

        if (DEBUG) 
	    log().debug()
		.write("assigning start state ")
		.write(startState)
		.out();

        // put this on the stack...
        stack.push(initialSet);

        // some cached out vars
        int nextSymbolID = -1;
        Item currentItem, nextItem;
        State currentState, nextState;
        IntSet currentSet, nextSet, followSet;
        Production currentProduction;
        NonTerminal currentNonTerminal;
        IntIterator nexts, follows, currentItems;
        GrammarSymbol nextSymbol;

        // now iterate the sets until the stack is exhausted
        while (!stack.isEmpty()) {

	    // pop the top o' the stack
	    currentSet = (IntSet)stack.pop();
            // get the state number
            currentState = lookup(currentSet);

            if (DEBUG) 
		log().debug()
		    .write("currently processing set ")
		    .write(currentSet)
		    .write(" which is state ")
		    .write(currentState)
		    .out();

            // mark this set as processed
            seen.addElement(currentSet);
	    // get a list of items in this set
	    nexts = getNextSet(currentSet).iterator();
	    // now loop over each item in the set and make the goto
	    // sets
	    while (nexts.hasNext()) {

		// grab each symbol...
		nextSymbolID = nexts.next();
		// ... the goto set across this path...
                nextSet = go(currentSet, nextSymbolID);
		// ... and it's state number.
		nextState = lookup(nextSet);

		if (DEBUG) 
		    log().debug()
			.write("next set ")
			.write(nextSet)
			.write(", state ")
			.write(nextState)
			.write(" on transition ")
			.write(nextSymbolID)
			.out();
		
                // now we check if the current input symbol is a
                // nonterminal.  If it is we want to add a goto
                // instruction such that if goto(Ii, A) = Ij; goto[i,
                // A] = j;
                if (!grammar.getGrammarSymbol(nextSymbolID).isTerminal())
		    dpa.go(currentState.getID(), nextSymbolID, nextState.getID());

                // add the path
                currentState.connect(grammar.getGrammarSymbol(nextSymbolID), nextState);
		// we want to test whether this is a new set of items.
		// If it is, then we'll need to process it eventually
		if (!seen.contains(nextSet))
		    stack.push(nextSet);
	    }

	    // now for the construction of the parse table we iterate
	    // over all the items in the set...
	    currentItems = currentSet.iterator();
	    
	    // and make shift and reduce instructions
	    while (currentItems.hasNext()) {
		// need the item
		currentItem = grammar.getItem(currentItems.next());
		// and the grammar symbol this item sees in front if
		// it
		nextSymbol = currentItem.nextSymbol();

		if (DEBUG) 
		    log().debug()
			.write("In state ")
			.write(currentState)
			.write(": item ").write(currentItem)
			.write(" faces ").write(nextSymbol)
			.out();


		// the item is not at the end of the production.
		if (nextSymbol != null) {

		    // now we need to check for two conditions: one is
		    // that the nextSymbol is the EndOfInout, in which
		    // case we want to accept, two is that the
		    // nextSymbol is a terminal, in which case we want
		    // to encode a shift.

		    //if (nextSymbol instanceof EndOfInput)
		    if (nextSymbol.isTerminal()) {
			//Terminal tt = (Terminal)nextSymbol;

//  			if (DEBUG) 
//  			    log().debug()
//  				.write("token Type is ")
//  				.write(tt.getToken())
//  				.out();

			// see if it's the special case of an end
			// terminal
			if ( ((Terminal)nextSymbol).getToken().getID() == Token.STOP ) {
			    dpa.accept(currentState.getID(),
				       nextSymbol.getID());
                        } else {
			    dpa.shift(currentState.getID(),
				       nextSymbol.getID(),
				       currentState.go(nextSymbol).getID());
			}
                    }

		} else {
		    // the item is at the end of the production.

		    // we need to encode reduce actions for all of the
		    // members of the follow set of the nonterminal
		    currentProduction = currentItem.getProduction();
		    followSet = getFollowSet
			(currentProduction.getNonTerminal());

		    follows = followSet.iterator();

		    if (DEBUG)
			log().debug()
			    .write("In state ").write(currentState)
			    .write(": item ").write(currentItem)
			    .write(" is final. The FOLLOW set for the nonterminal ")
			    .write(currentProduction.getNonTerminal().getName())
			    .write(" is ").write(followSet)
			    .out();
		   
                    // now add a reduce action for all grammar symbols
                    // in the follow set on this production
                    while (follows.hasNext()) {
                        dpa.reduce(currentState.getID(),
				   follows.next(),
				   currentProduction.getID());
                    }
		}
	    }
        }
    }

    /**
     * Returns the closure of the set generated by following the given
     * grammar symbol 
     */
    protected IntSet go(IntSet set, int grammarSymbolID)
    {
	// punt back an empty set if this one has no chance to be
	// anything (such a waste...)
	if (set.isEmpty())
	    return EmptyIntSet.EMPTY_SET;

	// make a new set to hold the items which are followable by
	// this grammar symbol
	IntSet gotoSet = new BitSetIntSet(17);

	// cache out the current item
	Item item = null;

	IntIterator items = set.iterator();
	// iterate each item in the set and test if it's nextSymbol is
	// the same as this one.  If it is, then add it to the gotoSet
	while(items.hasNext()) {
	    // get each item
	    item = grammar.getItem(items.next());
	    // test symbol
	    if (item.hasNext())
		if (item.nextSymbol().getID() == grammarSymbolID)
		    gotoSet.put( item.nextItem().getID() );
	}
	// now return the closure of this set.  Of course if it's
	// empty, there's really no need.
	return gotoSet.isEmpty() ? gotoSet : closure(gotoSet);
    }

    /**
     * Calculates and returns the next set of items for the given set
     * of items.  Each member in the set corresponds the the id of a
     * grammar symbol.  
     */
    public IntSet getNextSet(IntSet items)
    {
	// make the result set
	IntSet set = new BitSetIntSet();
	// get the iterator oe'r the set
	IntIterator iter = items.iterator();
	// cached out
	Item item = null;
	// iterate the set and each nextSymbol to the set
	while (iter.hasNext()) {
	    // get the next item
	    item = grammar.getItem(iter.next());
	    // test if this is at the end of production
	    if (item.nextSymbol() != null)
		set.put(item.nextSymbol().getID());
	}
	// give it up...
	return set;
    }

    /**
     * Returns the closure set of the given set of items.
     */
    protected IntSet closure(IntSet set)
    {
	// ok, need to make it.  Here's the container
	IntSet closure = new BitSetIntSet(11);

	// make a temporary stack to track which items we have
	// processed
	IntStack stack = new ArrayIntStack(set.size());

	IntIterator iter = set.iterator();
	// add all the items in this set to the stack
	while( iter.hasNext() ) {
	    // push it
	    stack.push(iter.next());
	}
	// a bitset to track which nonterminals we've processed
	boolean[] nonTerminalsAdded = 
	    new boolean[ grammar.terminals() + grammar.nonTerminals() + 1 ];

	// and a bunch of vars we init here to save alloc times
	Item item = null;
	GrammarSymbol symbol = null;
	NonTerminal nonTerminal = null;
	IntArray alternatives = null;

	// now loop until there are no unprocessed items in the stack
	while ( ! stack.isEmpty() ) {

	    // grab the top item from the stack
	    item = grammar.getItem( stack.pop() );

//  	    if (DEBUG) 
//  		log().debug()
//  		    .write("closure: item is ")
//  		    .write(item)
//  		    .out();

	    // check if this item is at the end of a production
	    if ( item.hasNext() ) {

		// now look at the GrammarSymbol after the item dot
		symbol = item.nextSymbol();

		// introspect whether this symbol is a nonterminal.
		if ( !symbol.isTerminal() ) {

		    // Cast it so.
		    nonTerminal = (NonTerminal)symbol;

		    // have we added it yet?
		    if ( ! nonTerminalsAdded[ nonTerminal.getID() ] ) {

			// no? then get all the alternatives for this
			// nonterminal
			alternatives = nonTerminal.getReductions();

			// process each alternative and add its
			// initial item to the current stack
			for (int i = 0; i < alternatives.length(); i++) {
			    // get the alt, its initial item
			    stack.push(grammar.getProduction( alternatives.at(i) )
				       .getInitialItem().getID());
			}

			// finally mark the fact that we have
			// processed this non-terminal
			nonTerminalsAdded[ nonTerminal.getID() ] = true;
		    }
		}
	    }

	    // finally add this fully processed item to the closure
	    // set
	    closure.put( item.getID() );
	}

	// last thing, return the closure set
	return closure;
    }

    /**
     * Compilation of the FIRST sets.
     */

    protected void first()
    {
	// Goal: We want to make a map from grammar symbol ID to
	// IntSet that lists the set of terminals that follow each
	// grammar symbol.  
	int len = grammar.terminals() + grammar.nonTerminals();
	GrammarSymbol[] firsts = new GrammarSymbol[len];

	IntRelation first = new TreeListIntRelation();

	// PART 1: Set all the first sets for the terminals
	IntArray terminals = grammar.getTerminals();
	int tlen = terminals.length();

	int ID;
	for (int i = 0; i < tlen; i++) {
	    ID = grammar.getTerminal(terminals.at(i)).getID();
	    first.set(ID, new SingletonIntSet(ID));
	}

	// PART 2: Make all the nonterminal dependencies
	IntRelation depends = new TreeListIntRelation();
	IntRelation input = new TreeListIntRelation();
	IntRelation output = new TreeListIntRelation();

	IntArray productions = grammar.getProductions();
	int plen = productions.length();
	
	Production p;
	for (int i = 0; i < plen; i++) {
	    p = grammar.getProduction(productions.at(i));
	    
	}
    }

/* 
 * Note: 6/28/01: It's been a few months since I looked at this code.
 * I was all set to release Syntacs when I found a pretty huge bug in
 * this code, the SLR construction algorithm.  The bug, which I cannot
 * fully remember now, eventually traced back to a fundamental design
 * flaw: the followset algorithm was in the completely wrong place!  I
 * had it in the Grammar object, as though it were inherent to the
 * stucture of a grammar, which is completely false; it's part of the
 * SLR algorithm!  Dumbass.  Anyhow, I set off to change this and rip
 * the followset code from the grammar and reintegrate it here.  Like
 * an ass, I got all caught up with the change and started on a road
 * down reimplemting basically everything from scratch.  This is not
 * such a bad idea even though the current design is pretty good, but
 * I did start off by beginning to break out the IntContainer code
 * into a separate library.  I created all the interfaces for this and
 * think it's a pretty cool project.  But that was awhile ago.  IntLib
 * is NOT done, and then I started ANOTHER project, RHAZR.ORG.  Rhazr
 * is a cool idea though, I'm excited about it.  Of course, while
 * implementing RHAZR I created HotSwap, which I broke out into its
 * own project.  Noticiing a pattern here?  I seem to have trouble
 * finishing what I start!  The Fucking recursion of life.  
 **/

    protected void compileFollowSets()
    {
	//System.out.println("Compiling follow sets... ");
	// get the number of nonTerminals
	final int len = nonTerminals();

	// the main container for follow sets is created to hold or
	// otw deal with the current number of nonterminals.
	FollowSet followSet = new FollowSet( nonTerminals() );

	CFNonTerminal nonTerminal;
	GrammarSymbol currentSymbol, nextSymbol;
        IntArray items;
        Item item;

        // now process each production
        for (int i = 0; i < len; i++) {
	    // get each one
	    nonTerminal = (CFNonTerminal)v_nonterminals.elementAt(i);
	    // make the set container
	    nonTerminal.setFollowSet( new BitSetIntSet(8) );
            // and get its items
	    items = nonTerminal.getProductionItems();
	    // iterate the productions
	    for (int j = 0; j < items.length(); j++) {
		// get each productions grammar symbols
		item = getItem( items.at(j) );

		// now check if this is a rightmost item.  By
		// definition, any items that mention this nonterminal
		// do so in it's nextSymbol.  In other words,
		// item.nextSymbol() == nonTerminal.  This means that
		// we're guaranteed to have at least one more item in
		// the linked list.  We want to know if this
		// nonTerminal is the last symbol in the production,
		// thus we test if there is just one more item.
		if (item.hasNext() /* has a next item */) {

		    // question APR 20 2001: If this item is
		    // guaranteed to have a nextSymbol() ==
		    // nonTerminal, are we not ALSO guaranteed a next
		    // item?  If the current nonterminal is B and the
		    // current item is [ A -> alpha <> B ], then is
		    // there not always a [ A -> alpha B <> ] ?  If
		    // this question is true, this would appear to be
		    // a bug.

		    // We know this so far:
		    // 1 -- this item mentions this nonterminal
		    // 2 -- this item is not the last item in the production.
		    nextSymbol = item.nextSymbol();
		    // now we need to see if the symbol in our path is
		    // a terminal or a nonterminal
		    if (nextSymbol.isTerminal()) {
			// if it is a terminal, then we just add this
			// if to the follow set
			nonTerminal.getFollowSet().put(nextSymbol.getID());
		    } else {
			// which means this is a nonterminal.  We need
			// to record the dependency
			followSet.link(nonTerminal, (NonTerminal)nextSymbol);
		    }

		    // But we also need to check if all the grammar
		    // symbols from here to the end of the production
		    // are 'opaque', or nullable.  This is important
		    // because if they are, then the terminals that
		    // follow the opaques could possibly include those
		    // terminals that would follow a reduction by the
		    // nonterminal.
		item_traversal:

		    // Another potential bug here: if we are
		    // traversing the list of items while nullable and
		    // not final, then should we not also be adding
		    // terminals to the follow set rahter than JUST
		    // recording dependencies?  I would think so.

		    while ( item.nextSymbol().isNullable() ) {
			// get the next item
			item = item.nextItem();
			// check if we've reached the final item
			if (!item.hasNext()) {
			    // we have.  This means that the rest of
			    // the grammar symbols were nullable
			    followSet.link(nonTerminal, item.getProduction().getNonTerminal());
			    // and break the loop
			    break item_traversal;
			}
		    } /* end while */

		} else {
		    // yes, it is the final item.  This means that
		    // everything in the production for this item can
		    // follow this nonterminal
		    followSet.link(nonTerminal, item.getProduction().getNonTerminal());
		}
	    }
	}
    }

    public DPA construct(ContextFreeSet grammar) throws AlgorithmException
    {
	this.grammar = grammar;

	init();

	return dpa.construct(grammar);
    }

    public void vizualize(GraphViz dot)
    {
	dpa.vizualize(dot);
	
	if (Mission.control().isNotTrue("viz-dpa-hide-loopback-edges"))
	    log().warn().write("Warning! Only GraphViz visualization of shift "+
			       "transitions is currently available for SLR1").out();
    }

    public String toString()
    {
	return super.toString() + dpa.toString();
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("spc", this); // Simple Pushdown Constructor
	return log;
    }
    
    private ArrayDPAConstructor dpa;
    private Log log;






    // ================================================================
    // CLASSES HIJACKED FROM CFGRAMMAR
    // ================================================================

    private static class FollowSet
    {
        FollowSet(int size)
        {
	    this.nodes = new Hashtable(size);
        }

        private Node alloc(NonTerminal nonTerminal)
        {
	    Node node = new Node(nonTerminal);
	    nodes.put(nonTerminal, node);
	    return node;
        }

        private Node fetch(NonTerminal nonTerminal)
        {
	    Node node = (Node)nodes.get(nonTerminal);
	    return node == null ? alloc(nonTerminal) : node;
        }

        public void link(NonTerminal _this, NonTerminal _that)
        {
	    Node this_node = fetch(_this);
	    Node that_node = fetch(_that);

            if (that_node.links.contains(this_node))   // O(n)
            	throw new RuntimeException
		    (" Circular follow set dependency between "+_this+" and "+_that+
		     " Either the grammar is not context free, or the method being used" +
		     " to compute the automata is not powerful enough.");
            else
            	this_node.links.addElement(that_node);
        }

        public void resolve()
        {
            // iterate all the nodes and call resolve.  This flattens
            // all the nonTerminal link dependencies
            for (Enumeration e = nodes.elements(); e.hasMoreElements(); )
            	( (Node)e.nextElement() ).resolve();
        }

        private Hashtable nodes;
    }

    private class Node
    {
        Node(NonTerminal nonTerminal)
        {
	    this.nonTerminal = nonTerminal;
	    this.links = new Vector();
        }

        void resolve()
        {
	    // return right away if we've already done it
	    if (isResolved) 
		return;

	    // a cached out handle for clarity
	    Node other = null;
	    // step over all nonTerminal links
	    for (int i=0; i<links.size(); i++) {
		// get each nonterminal
		other = (Node)links.elementAt(i);
		// make sure *it*'s resolved
		other.resolve();
		// and union their followsets
		getFollowSet(nonTerminal).union(getFollowSet(other.nonTerminal));
	    }
	    // mark that we've done our job.
	    isResolved = true;
        }

        NonTerminal nonTerminal;
    	Vector links;
    	boolean isResolved;
    }
}





