/**
 * $Id: Array2MesoArrayDPATransformer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete <code>DPATransformer</code> implementation which converts
 * an <code>ArrayDPA</code> into a <code>MesoArrayDPA</code>.  
 */
public class Array2MesoArrayDPATransformer
    implements DPATransformer
{
    private static final boolean DEBUG = true;
    private static final boolean WARN = true;

    /**
     * Constructs the <code>Array2MesoArrayDPATransformer</code>.
     */
    public Array2MesoArrayDPATransformer() 
    {
    }

    /**
     * Transform the argument into a <code>DPA</code>.  If the RTT of
     * the argument is NOT an instance of <code>ArrayDPA</code>, a
     * <code>ClassCastException</code> will be thrown.  
     */
    public DPA transform(Object array_dpa)
    {
	// cast back to ArrayDPA
	ArrayDPA dpa = (ArrayDPA)array_dpa;

	// PART 1: compress the action table.  The raw table has the
	// following characteristics: (1) the state table (the first
	// dimension in the 2D array) tends to have a long stretch at
	// the back which has null entries; This is due to the
	// expotential doubling of the array length during the
	// construction of the array dpa (like a vector), (2) each
	// entry (each state, or each int[] at the second dimension)
	// tends to have long leading AND trailing '0' valued
	// sequences; (3) adjacent non-zero elements are rarely equal
	// (they are different).
	//
	// By these characteristics, we want to compress the table
	// down such that we get rid of the null-state entries in the
	// first dimension and strip out the leading and trailing
	// zeros in each state (trimming).  This means that the new
	// int[] that represents a state will be at least as long as
	// the contiguous block of non-zero elements in the original
	// state.  We will use the first two elements in the
	// destination array to record where the leading and trailing
	// sequences begin and end.
	//
	// Note that this "Meso" implementation is different than it
	// is for a DFA.  in the DFA case, there were long stretches
	// of contiguous elements, but sometimes they were non-zero.
	// In this case they are always zero, so we don't have to
	// record that extra information.
	// 
	int[][] new_action = compress(dpa.action);

	// PART 2: compress the go table.  The raw go table has the
	// following characteristics: (1) the table is 'sparse' in
	// that about half of all state entries are null (there is no
	// entry in the go table for that state).  This suggests using
	// a tree or something that conserves memory better than a
	// sparse array, but since it's not *too* sparse and that the
	// table is not too big, we'll stay with a sparse array; (2)
	// each state tends to have long leading and trailing
	// zero-valued sequences, moreso than the action table in
	// fact; (3) like the action table, the trailing state table
	// tends to have a long null tail.
	//
	int[][] new_go = compress(dpa.go);

	// PART 3: compress the actions table.  The raw table is a
	// long array where each element in the array is an Action
	// object.  Each Action object has an id, a type, and a
	// register.  The thing about an Action is are two-fold: (1)
	// the id is not used in the parsing algorithms, so it can be
	// safely discarded.  This is relevant since many of the
	// actions in the action table are identical other than their
	// ID's.  I have to look closer into the construction
	// algorithms why this is the case, but for now I'll just
	// accept it as a fact of life and correct for it here; (2)
	// The value of the type is always an integer less then 4, and
	// the register is a value that is always pretty small (less
	// than a thousand, say...).  Therefore, we can easily fit all
	// this info into a single 32-bit integer rather than using an
	// object.
	// 
	// The tricky part about this is that since we are reducing
	// the number of entries in the table, the actions in the
	// new_action table need to be updated to reflect the new
	// mapping.
	//
	int[] new_actions = compress(dpa.actions, new_action);

        // done
        return new MesoArrayDPA(new_action, 
				 new_go, 
				 new_actions);
    }

    private int[][] compress(int[][] input)
    {
	// PART 1: Make a new state table. Start by finding out how
	// long the new state table should be.  Need to find the last
	// non-null element.
	int last = input.length;
	while (last >= 0)
	    if (input[--last] != null) 
		break;

	// Compute the length of the new array (add one since last is
	// an index value, not a length value)
	int len = last + 1;

	// now make a new table this length
	int[][] output = new int[len][];

	// PART 2:  Compress each state.
	for (int i=0; i < len; i++)
	    output[i] = compress(input[i]);

	// done
	return output;
    }

    private int[] compress(int[] input)
    {
	if (input == null) {
	    //System.out.println("Got a null state!");
	    return null;
	}

	// in this method, we have an int array which needs to be
	// meso-ized.  We need to find the index of the first and last
	// significant elements in the array, then set the first two
	// accounting elements and copy the rest of the array.
	int first = 0, last = input.length - 1;

	// rip up and down the array looking for the first and last
	// elements which have non-zero values.
	while (first <= last) if (input[first] > 0) break; else first++;
	while (last >= first) if (input[last]  > 0) break; else last--;

	// now we know how long the array is going to need to be.  Add
	// one since both last and first are array indexes (think
	// about case when last and first are the same).
	int length = last - first + 1;

	// Make the array.  Add two since the first two elements will
	// be used to say what the bounds of the telomeres are.
	int[] output = new int[length + MesoArrayDPA.START];

	// set the bounds
	output[MesoArrayDPA.LOWER] = first;
	output[MesoArrayDPA.UPPER] = last;

	// and copy over the input array starting at the first
	// position that we identified into the output array, starting
	// at element 3 such that we don't overwrite output[1] and
	// output[2].
	System.arraycopy(input, first, output, MesoArrayDPA.START, length);

	// done
	return output;
    }

    private int[] compress(Action[] input, int[][] action_table)
    {
	// the strategy here is a little different.  We need to create
	// integer representations of all the Action objects and hash
	// them such that we can locate them by value or by ID.  The
	// value hash is used to see if we already have that action
	// (as an int) and the id hash will be used to remap elements
	// in the action table.
 	HashIntFunction idx_map = new HashIntFunction(); 
	HashIntFunction action_map = new HashIntFunction();
	ArrayIntList output_list = new ArrayIntList();
	
	// PART 0: Add in the error action to the vector such that it
	// gets the zero spot
	output_list.add(0);

	// PART 1: Create all the new (integral) actions and populate
	// the index map such that we can go over the action table and
	// remap action values.  Start at 1 since we're ignoring the
	// ERROR action (at position zero) in this loop.
	for (int input_idx = 1; input_idx < input.length; input_idx++) {

	    // TASK 1: create an integral representation of the Action
	    // by masking in the type and the register into the high
	    // and low portions of a 32-bit int.
	    int new_action = input[input_idx].getValue();
     	    new_action |= input[input_idx].getType() << MesoArrayDPA.TYPE_MASK_SHIFT;

	    // TASK 2: fetch the index of this new action in the index
	    // map, and update collections if necessary.
	    int output_idx = action_map.get(new_action);
	    // if we don't have it yet we'll need to store it in the
	    // vector and the map.
	    if (output_idx == 0) {
		output_idx = output_list.length();
		output_list.add(new_action);
		action_map.put(new_action, output_idx);
	    }
	    
	    // TASK 3: Map the old index to the new one such that we
	    // can do remappings
	    idx_map.put(input_idx, output_idx);
	}

	// PART 2: remap the indices in the action table.  We know
	// that each element in the array is a state (another int[]
	// array).
	int[] state = null;

	if (DEBUG) 
	    log().debug()
		.write("compressing ")
		.write(action_table.length)
		.write(" states.")
		.out();

	for (int i=0; i < action_table.length; i++) {
	    // get each one.  At this point they should never be null.
	    state = action_table[i];

	    if (state == null) {
		if (WARN) 
		    log().warn()
			.write("compress(): state ").write(i)
			.write(" is null.")
			.out();

		continue;
	    }
	    // map all the elements except for the first two
	    for (int j=2; j < state.length; j++) {
		// if the value of the element is zero, this is the
		// error action which we KNOW will be zero in the new
		// scheme too, so we save ourselves the bother of
		// hash lookup.
		if (state[j] == 0) continue;
		// remap it
		state[j] = idx_map.get(state[j]);
	    } 
	}

	// done
	return output_list.toArray();
    }

    private Log log()
    {
	if (this.log == null)
	    this.log = Mission.control().log("a2m", this); // Array To Meso
	return this.log;
    }

    private Log log;
}









