/**
 * $Id: TreeDFAConstructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.finite;

import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.grammar.regular.RegularSet;
import org.inxar.syntacs.grammar.regular.Interval;
import org.inxar.syntacs.automaton.finite.DFA;
import org.inxar.syntacs.automaton.finite.DFAConstructor;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntIterator;
import org.inxar.syntacs.util.Log;
import com.inxar.syntacs.util.BubbleTree;
import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.EmptyIntSet;

/**
 * Concrete implementation of <code>DFAConstructor</code> which builds
 * a <code>TreeDFA</code>. The method used to construct the
 * <code>DFA</code> is derived from the Dragon Book but features a
 * <code>BubbleTree</code> object in an interesting way that
 * makes things faster.
 */
public class TreeDFAConstructor
    implements DFAConstructor
{
    private static final boolean WARN = true;
    private static final boolean DEBUG = false;

    /**
     * Constructs the TreeDFA.
     */
    public TreeDFAConstructor()
    {
	// make our containers
	this.stack = new Stack();
    	this.states = new Vector();
	this.hash = new Hashtable();
    }

    private void init() throws CloneNotSupportedException
    {
	// push in the dead state
	lookup(EmptyIntSet.EMPTY_SET);

	// push in the first state
	lookup(regset.getStart());

	// now loop while the stack still has elements
	while (!stack.isEmpty())
	    resolve( (State)stack.pop() );
    }

    private State lookup(IntSet interval_set)
    {
	if (DEBUG)
	    log().debug()
		.write("lookup(): invoked with set ")
		.write(interval_set)
		.out();

	// each member in the incoming set is the id of an Interval
	// decorated somewhere on the regset tree.  Our job is to make
	// a q-state out of it.

    	// see if we already have it
	State state = (State)hash.get(interval_set);

	// If so, return
	if (state != null) {
	    if (DEBUG)
		log().debug()
		    .write("lookup(): found state ")
		    .write(state.id)
		    .write(" for set ")
		    .write(interval_set)
		    .out();

	    return state;
	}

	// no.  this is a brand new set.  We need to make a new state.
	state = new State(states.size(), interval_set);

	if (DEBUG)
	    log().debug()
		.write("lookup(): constructed new state ")
		.write(state.id)
		.write(" for set ")
		.write(interval_set)
		.out();

	// add to vector
	states.addElement(state);

	// add this to the hash under this set
	hash.put(interval_set, state);

	// !also place this on the stack.  Note this is a sneaky place
	// to put this line of code.  One might not necessarily expect
	// that we would modify the stack in this method.
	stack.push(state);

	// finally, we're done
	return state;
    }

    /**
     * In this method we want to transform the set of intervals into a
     * set of edges.
     */
    private void resolve(State state) throws CloneNotSupportedException
    {
	if (DEBUG)
	    log().debug()
		.write("resolve(): -------- "+
		       "building interval tree for state ")
		.write(state.id)
		.write(" with interval set ")
		.write(state.set)
		.out();

	// check for the dead state.
	if (state.set.isEmpty()) {
	    // place tree stump (a tree with no branches)
	    state.tree = new TreeDFA.Edge(state.id);
	    // done
	    return;
	}

	// make a new bubble tree for this state which we will fill
	BubbleTree tree = new BubbleTree();
	// reuseable Interval reference
	Interval interval = null;
	// loop over all intervals
	IntIterator i = state.set.iterator();
	while (i.hasNext()) {
	    // advance to next interval
	    interval = regset.getInterval(i.next());

	    if (DEBUG)
		log().debug()
		    .write("resolve(): evaluating interval ")
		    .write(interval)
		    .write(" for state ")
		    .write(state.id)
		    .out();

	    // percolate the interval into the tree
	    tree.put(interval.lo(), interval.hi(), interval.getFollowSet());
	}

	if (DEBUG)
	    log().debug()
		.write("resolve(): bubble tree looks like ")
		.write(tree)
		.out();

	// did we have *any* non-terminator intervals?  Normally we
	// do.  If not, it means that this state is the final node in
	// some accepting path.
	if (tree.isEmpty()) {
	    // tree stump!
	    state.tree = new TreeDFA.Edge(state.id);
	} else {
	    // ok, now we want to build an edge tree using the
	    // bifurcate tree.
	    state.tree = TreeDFA.balance( traverse(tree.root, state) );
	}
    }

    private TreeDFA.Edge traverse(BubbleTree.Bubble bubble, State state) throws CloneNotSupportedException
    {
	// return when argument is null
	if (bubble == null)
	    return null;

	// check for sub-zero bubbles.  Any such bubble represents
	// an accepting token id rather than an input path.
	if (bubble.lo < 0) {
            // check existing output to see if it has not been
            // assigned yet.
            if (state.output == Token.UNDEF) {
		// assign the output
		state.output = -bubble.lo;
		if (DEBUG)
		    log().debug()
			.write("assigned output " )
			.write( state.output)
			.write(" for state " )
			.write(state.id)
			.write(" using bubble ")
			.write(bubble)
			.out();

	    } else {

		// DFA conflict has occurred.
		Token current = regset.getToken(state.output);
		Token contender = regset.getToken(-bubble.lo);

		if (WARN)
		    log().warn()
			.write("Conflict between " )
			.write( current )
			.write(" and ")
			.write(contender)
			.write(" at state ")
			.write(state.id)
			.write(". ")
			.write(current)
			.write(" wins by earlier declaration.")
			.out();

	    }
        }

	// make a new edge
	return new TreeDFA.Edge
	    (// edge has same lo/hi as this bubble node
	     bubble.lo, bubble.hi, state.id,
	     // fetch the next state for this interval set
	     lookup(bubble.set).id,
	     // recurse left
	     traverse(bubble.ldn, state),
	     // recurse right
	     traverse(bubble.rdn, state));
    }

    public DFA construct(RegularSet regset)
    {
	if (DEBUG)
	    log().debug()
		.write("construct():  regset is :")
		.write(regset)
		.out();

	this.regset = regset;

	try {
	    // make the dfa
	    init();
	} catch (CloneNotSupportedException cnsex) {
	    throw new RuntimeException
		("There was an unexpected CloneNotSupportedException with this message: " +
		 cnsex.getMessage());
	}

	// cache the length
	int len = states.size();
	// make the array
	TreeDFA.State[] array = new TreeDFA.State[len];
        // cached out
        State state = null;
        // loop all the states
        for (int i=0; i<len; i++) {
	    // get each one
	    state = (State)states.elementAt(i);
	    // and finally make the state
	    array[i] = new TreeDFA.State(state.tree, state.output);
        }
        // done with the states, make an return the dfa
        return new TreeDFA(array);
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("tfc", this); // Tree Finite Constructor
	return log;
    }

    Stack stack;
    Vector states;
    Hashtable hash;
    RegularSet regset;
    private Log log;

    // ------------------------------------------------------------------------
    // State class
    // ------------------------------------------------------------------------
    private static class State
    {
	State(int id, IntSet set)
	{
	    this.id = id;
	    this.output = Token.UNDEF;
	    this.set = set;
	}

	int id;
	int output;
	IntSet set;
	TreeDFA.Edge tree;
    }
}
