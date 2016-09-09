/**
 * $Id: Tree2MesoArrayDFATransformer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.automaton.finite.*;

/**
 * Concrete <code>DFATransformer</code> implementation which converts
 * a <code>TreeDFA</code> into a <code>MesoArrayDFA</code>.  
 */
public class Tree2MesoArrayDFATransformer
    implements DFATransformer
{
    private static final boolean DEBUG = false;

    // constants which say how big the input alphabet could be.  we
    // say that it is the unicode reange
    private static final int MIN = Character.MIN_VALUE;
    private static final int MAX = Character.MAX_VALUE;

    // the dead state.  Note that each length is one less than max
    // value
    private static final int[] DEAD;

    // static make the dead state.  The length is 4.  The lower and
    // upper bounds are set such that the go() algorithm will always
    // shunt to the LOWER_NEXT (or UPPER_NEXT, if the go() algorigthm
    // checked hi first).  Thefore, the predicate "the current input
    // is less that the lower bound of the mesotic state array" is
    // always semantically true.
    static {
	DEAD = new int[MesoArrayDFA.START];
	DEAD[MesoArrayDFA.LOWER] = MAX + 1;
	DEAD[MesoArrayDFA.UPPER] = MIN - 1;
	DEAD[MesoArrayDFA.LOWER_NEXT] = DFA.DEAD_STATE;
	DEAD[MesoArrayDFA.UPPER_NEXT] = DFA.DEAD_STATE;
    }

    /**
     * Constructs the <code>Tree2MesoArrayDFATransformer</code>.
     */
    public Tree2MesoArrayDFATransformer() 
    {
    }

    /**
     * Transform the argument into a <code>DFA</code>.  If the RTT of
     * the argument is NOT an instance of <code>TreeDFA</code>, a
     * <code>ClassCastException</code> will be thrown.  
     */
    public DFA transform(Object tree_dfa)
    {
	TreeDFA dfa = (TreeDFA)tree_dfa;

        // we want to build a mesotic dfa which implies that we need
        // to build the 2D int table array and the 1D accepts array.
        // We know the number of states already.
        int len = dfa.table.length;

        // make the target containers
        int[][] table = new int[len][];
        int[] accepts = new int[len];

        // now loop over all the states in the dfa and move to the new
        // arrays
        for (int i=0; i<len; i++) {
	    table[i] = transform(dfa.table[i].tree);
	    accepts[i] = dfa.table[i].output;
        }

        // finally, make the new dfa and return it
        return new MesoArrayDFA(table, accepts);
    }

    private int[] transform(TreeDFA.Edge tree)
    {
	if (DEBUG) System.out.println("transforming tree "+tree);

	// check to see if this is a the dead state, in which case we
	// emprically know what the state will look like
	if (tree == null 
	    || (tree.lo == MIN && tree.hi == MAX) 
	    || (tree.lo < MIN && tree.hi < MIN))
	    return DEAD;

        // now init the first and last edges.  These will be the
        // boundaries
        TreeDFA.Edge front = tree;
        TreeDFA.Edge back  = tree;

        if (DEBUG) {
	    StringBuffer b = new StringBuffer();
	    TreeDFA.toStringInOrder(tree, b, 0);
	    System.out.println("tree is:");
	    System.out.println(b.toString());
	}

        // run down left and right edge of the pyramid to get the
        // first and last nodes on the tree
        while (true) { if (front.left == null || front.left.lo < MIN) break; else front = front.left; }
        while (true) { if (back.right == null || back.right.hi > MAX) break; else back =  back.right; }

	/*
	  FYI: there are 7 cases of how the intervals could lie in the
	  state:

	  |----------| case 0:  no edges (DEAD)
	  |----**----| case 1:  both the same, in middle
	  |**--------| case 2:  both the same, at front
	  |--------**| case 3:  both the same, at back
	  |--**--**--| case 4:  different, in middle
	  |**--**----| case 5:  different, one at front
	  |----**--**| case 6:  different, one at back

	*/

        // set the front index and next state.  This depends on
        // whether the first interval starts at zero or not
        if (front.lo == MIN) {
            // it is, so we set the lower bound next state to this
            // intervals next
            lower_next = front.next;
            // and set the lower bound *after* this states' hi bound
            lower_bound = front.hi + 1;
        } else {
	    // the interval does not abutt the zero postion, so we
	    // know that the lower next is the dead state
	    lower_next = DFA.DEAD_STATE;
	    // and the lower bound is at the front's low bound
	    lower_bound = front.lo;
        }

	/*
	  REPEAT SYMMETRIC IDEA FOR MesoArrayDFA.UPPER BOUND
	*/

        if (back.hi == MAX) {
            // it is, so we set the upper bound next state to this
            // intervals next
            upper_next = back.next;
            // and set the upper bound *before* this states' lo bound
            upper_bound = back.lo - 1;
        } else {
	    // the interval does not abutt the MAX postion, so we know
	    // that the upper next is the dead state
	    upper_next = DFA.DEAD_STATE;
	    // and the upper bound is at the back's hi bound
	    upper_bound = back.hi;
        }

        // get the length of the meso (middle) section.  Add one since
        // our bounds are exact (inclusive).
        int meso_len = upper_bound - lower_bound + 1;

        if (DEBUG) {
	    System.out.println("front: ("+front.lo+","+front.hi+","+front.next+")");
	    System.out.println("front: ("+back.lo+","+back.hi+","+back.next+")");
	    System.out.println("upper_bound: "+upper_bound);
	    System.out.println("lower_bound: "+lower_bound);
	    System.out.println("upper_next: "+upper_next);
	    System.out.println("lower_next: "+lower_next);
	    System.out.println("meso_len: "+meso_len);
	}

        // now we are ready to make the array.
        state = new int[ MesoArrayDFA.START + meso_len ];

        // set the telomeres
        state[MesoArrayDFA.LOWER] = lower_bound;
        state[MesoArrayDFA.UPPER] = upper_bound;
        state[MesoArrayDFA.LOWER_NEXT] = lower_next;
        state[MesoArrayDFA.UPPER_NEXT] = upper_next;

	// ok, now set the middle.  This will involve recursing down
	// each node of the tree. We make the analogy to putting down
	// a bunch of tiles across a floor. 
	if (tree != null)
	    tile(tree);

	// finally, return the array
	return state;
    }

    private void tile(TreeDFA.Edge tree)
    {
	// do an in-order traversal, but it does not really matter
	if (tree.left != null) tile(tree.left);

        if (DEBUG) System.out.println("tile node has lo "+tree.lo+", hi "+tree.hi);

	// do not encode on out-of-range input
	if (!(tree.lo < lower_bound || tree.hi > upper_bound)) {
	    if (tree.lo == tree.hi) {
		state[tree.lo - lower_bound + MesoArrayDFA.START] = tree.next;
	    } else {
		// now walk across the section this node is
		// responsible for and tile it's next state across
		// that region
		for (int i=tree.lo; i<=tree.hi; i++) 
		    state[i - lower_bound + MesoArrayDFA.START] = tree.next;
	    }
	}

	// back down
	if (tree.right != null) tile(tree.right);
    }

    private int max;
    private int min;
    private int[] state;
    private int upper_bound;
    private int lower_bound;
    private int upper_next = Token.UNDEF;
    private int lower_next = Token.UNDEF;
}









