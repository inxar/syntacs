/**
 * $Id: LR1Constructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>LRConstructor</code> that builds
 * canonical LR1 parse tables.  The algorithm used by this class was
 * coded based directly on the Dragon Book.  
 */
public class LR1Constructor extends LRConstructor
    implements Vizualizable
{
    private static final boolean DEBUG = false;

    /**
     * Constructs an <code>LR1Constructor</code>.
     */
    public LR1Constructor() 
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

        // Put the start productions initial item in the set.  In this
        // case (canonical construction), we need to make this first
        // item with lookahead to the '$' terminal.  To grab the
        // instance of that terminal, we take advantage of the fact
        // that we KNOW that the start productions' second grammar
        // symbol on the RHS is '$'.  * get start production
        Production start = grammar.getStart();

        if (DEBUG) 
	    log().debug()
		.write("init(): Start production is ")
		.write(start)
		.out();

        // * get it's first item
        Item initial = start.getInitialItem();

        if (DEBUG) 
	    log().debug()
		.write("init(): Initial start item is ")
		.write(initial)
		.out();

        // * we assume that the next items' next symbol is $.
        Terminal stop = (Terminal)initial.nextItem().nextSymbol();

	// add the ID of the item which has form [ S' -> <>S, $]
        first.put(initial.lookahead(stop).getID());

        // and take the closure of this
        IntSet initialSet = closure(first);

        if (DEBUG) 
	    log().debug()
		.write("init(): initialSet")
		.write(initialSet)
		.out();

        // we want to set this as the start state
        State startState = lookup(initialSet);

        if (DEBUG) 
	    log().debug()
		.write("init(): assigning start state ")
		.write(startState)
		.out();

        // put this on the stack...
        stack.push(initialSet);

        // some cached out vars
        int nextSymbolID = -1;
        IntSet currentSet, nextSet = null;
        GrammarSymbol nextSymbol = null;
        LR1Item currentItem = null;
        Production currentProduction = null;
        State currentState, nextState = null;
        NonTerminal currentNonTerminal = null;
        IntIterator nexts, follows, currentItems = null;

        // now iterate the sets until the stack is exhausted
        while (!stack.isEmpty()) {

	    // pop the top o' the stack
	    currentSet = (IntSet)stack.pop();
            // get the state number
            currentState = lookup(currentSet);
            if (DEBUG) 
		log().debug()
		    .write("init(): currently processing set ")
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
			.write("init(): next set ")
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
                currentState.connect
		    (grammar.getGrammarSymbol(nextSymbolID), nextState);

		// we want to test whether this is a new set of items.
		// If it is, then we'll need to process it eventually.
		if (!seen.contains(nextSet))     // NOTE! This is O(n) when it could be O(1).
		    stack.push(nextSet);	 // Use hash instead of vector possibly.
	    }

	    // now for the construction of the parse table we iterate
	    // over all the items in the set...
	    currentItems = currentSet.iterator();
	    // and make shift and reduce instructions
	    while (currentItems.hasNext()) {
		// need the item
		currentItem = (LR1Item)grammar.getItem(currentItems.next());

		if (DEBUG) 
		    log().debug()
			.write("currently processing item ")
			.write(currentItem)
			.out();

		// and the grammar symbol this item sees in front if
		// it
		nextSymbol = currentItem.nextSymbol();
		// the item is not at the end of the production.
		if (nextSymbol != null) {

		    // now we need to check for two conditions: one is
		    // that the nextSymbol is the EndOfInout, in which
		    // case we want to accept, two is that the
		    // nextSymbol is a terminal, in which case we want
		    // to encode a shift,
		    //if (nextSymbol instanceof EndOfInput)
		    if (nextSymbol.isTerminal()) {

			//Terminal tt = (Terminal)nextSymbol;
			//log().debug().write("token Type is ").write(tt.getToken());

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

		    // we need to encode reduce actions for the
		    // lookahead named
                    dpa.reduce(currentState.getID(),
			       currentItem.getLookahead().getID(),
			       currentItem.getProduction().getID());
                }
	    }
        }
    }

    /**
     * Returns the closure set of the given set of items.
     */
    public IntSet closure(IntSet set)
    {
	if (DEBUG) 
	    log().debug()
		.write("closure(): initial set is:").writeln()
		.write(toItems(set))
		.write("end listing of initial set.")
		.out();
	
	// ok, need to make it.  Here's the container.
	IntSet closure = new BitSetIntSet(11);

	// Make a temporary stack to track which items we have
	// processed.
	IntStack stack = new ArrayIntStack(set.size());

	IntIterator iter = set.iterator();
	// add all the items in this set to the stack
	while ( iter.hasNext() ) 
	    stack.push(iter.next());

	// A bunch of vars we init here...
	int ID;
        IntSet first;
	Item core;
	LR1Item item;
        Terminal terminal;
	NonTerminal nonTerminal;
	IntArray alternatives;
	IntIterator firsts;

	// now loop until there are no unprocessed items in the stack
	while ( ! stack.isEmpty() ) {

	    ID = stack.pop();
	    // grab the top item from the stack
	    item = (LR1Item)grammar.getItem( ID );
	    if (DEBUG) 
		log().debug()
		    .write("closure(): processing item ")
		    .write(ID)
		    .write(": ")
		    .write(item)
		    .out();

	    // Looking for items which face a nonterminal...
	    if ( item.hasNext() && ( ! item.nextSymbol().isTerminal() ) ) {
		nonTerminal = (NonTerminal)item.nextSymbol();

		// get the set of items for FIRST(beta,a)
		first = item.nextItem().getFirstSet();

		if (DEBUG) 
		    log().debug()
			.write("closure(): first set for item ")
			.write(item.nextItem())
			.write(": ")
			.write(first)
			.out();
		
		// is it an empty set?
		if (first.isEmpty()) {

		    // case 1: the set is nullable since it's empty.
		    // Add an LR1 item from this core with the
		    // lookahead from the current item.  
		    alternatives = nonTerminal.getReductions();

		    // Process each alternative.
		    for (int i = 0; i < alternatives.length(); i++) {
			// Get the initial core item for this
			// production.
			core = grammar.getProduction( alternatives.at(i) ).getInitialItem();

			// add the LR1item
			ID = core.lookahead(item.getLookahead()).getID();

			if (DEBUG) 
			    log().debug()
				.write("closure(): stacking LR1-item ")
				.write( ID )
				.write(" with core ")
				.write(core)
				.write(" and `a' lookahead ")
				.write(item.getLookahead())
				.out();

			if ( (!closure.contains(ID)) && (!stack.contains(ID)) )
			    stack.push( ID );
		    }

		} else {

		    // case 2: iterate the set

		    // get all the alternatives for this nonterminal
		    alternatives = nonTerminal.getReductions();

		    // process each alternative
		    for (int i = 0; i < alternatives.length(); i++) {

			// get the initial core item for this
			// production
			core = grammar.getProduction( alternatives.at(i) ).getInitialItem();

			// make sure the iterator is reset since we
			// use it multiple times in the other loop
			firsts = first.iterator();

			// now add add all items
			while (firsts.hasNext()) {

			    // grab each terminal as the lookahead
			    terminal = grammar.getTerminal(firsts.next());

			    // OK, here's a tricky statement.  We know
			    // that we will be adding an LR1 item to
			    // the stack, and we know it will be
			    // coming from the core item's lookahead()
			    // method.  The only variable is what
			    // terminal to use for the lookahead.
			    // Since *we are screening for epsilon*,
			    // if the terminal isNullable, it means
			    // that it IS epsilon.  In that case we
			    // want to use the lookahead from the
			    // current item , which really corresponds
			    // to the case FIRST(beta,a) where beta is
			    // nullable, so we use 'a' instead.  Thus,
			    // 'a' == item.getLookahead().
			    ID = core.lookahead(terminal.isNullable() 
						? item.getLookahead() : terminal
						).getID();

			    if (DEBUG) 
				log().debug()
				    .write("closure(): stacking LR1-item ")
				    .write(ID)
				    .write(" with core ")
				    .write(core)
				    .write(" and `beta' lookahead ")
				    .write((terminal.isNullable() 
					    ? item.getLookahead() 
					    : terminal) 
					   )
				    .out();
			    
			    if ( (!closure.contains(ID)) && (!stack.contains(ID)) )
				stack.push(ID);

			} /* end while firsts.hasNext() */

		    } /* end while alternatives.hasNext() */

		} /* end if first.isEmpty() [else clause] */

	    } /* end if item.hasNext() */

	    // Finally add this fully processed item to the closure
	    // set
	    closure.put( item.getID() );

	} /* end when stack is empty */

	if (DEBUG) 
	    log().debug()
		.write("closure(): final closed set is:").writeln()
		.write(toItems(closure))
		.out();

	// last thing, return the closure set
	return closure;
    }

    /**
     * Calculates and returns the next set of items for the given set
     * of items.  Each member in the set corresponds the the ID of a
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
	LR1Item item = null;

	IntIterator items = set.iterator();
	// iterate each item in the set and test if it's nextSymbol is
	// the same as this one.  If it is, then add it to the gotoSet
	while (items.hasNext()) {
	    // get each item. WE HAVE TO CAST NOW.  BETTER DESIGN?
	    item = (LR1Item)grammar.getItem(items.next());
	    // test symbol
	    if (item.hasNext()) {
		if (item.nextSymbol().getID() == grammarSymbolID) {
		    // put the LR1Item with this item's lookahead.
		    // This is the difference between SLR go() and
		    // this one.
		    gotoSet.put(
				item.nextItem()
				.lookahead(item.getLookahead())
				.getID()
        			);
		}
	    }
	}
	if (DEBUG) 
	    log().debug()
		.write("go(): got set ")
		.write(gotoSet)
		.write(" upon traveral through ")
		.write(grammar.getGrammarSymbol(grammarSymbolID))
		.out();

	// now return the closure of this set.  Of course if it's
	// empty, there's really no need.
	return gotoSet.isEmpty() ? gotoSet : closure(gotoSet);
    }

    private String toItems(IntSet items)
    {
	StringBuffer b = new StringBuffer();
	IntIterator i = items.iterator();
	while (i.hasNext())
	    b.append(grammar.getItem(i.next())).append(StringTools.NEWLINE);
	return b.toString();
    }

    public DPA construct(ContextFreeSet grammar) throws AlgorithmException
    {
	this.grammar = grammar;

	// initialilze
	init();

	// return delegate
	return dpa.construct(grammar);
    }

    public void vizualize(GraphViz dot)
    {
	dpa.vizualize(dot);
	
	if (Mission.control().isNotTrue("viz-dpa-hide-loopback-edges"))
	    log().warn().write("Warning! Only GraphViz visualization of shift "+
			       "transitions is currently available for LR1").out();
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("cpc", this); // Canonical Pushdown Constructor
	return log;
    }

    private ArrayDPAConstructor dpa;
    private Log log;
}



