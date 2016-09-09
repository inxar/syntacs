/**
 * $Id: MesoArrayDPA.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete <code>DPA</code> implementation which uses meso-array
 * parse tables.
 */
public class MesoArrayDPA
    implements DPA, java.io.Serializable
{
    private static final boolean DEBUG = false;

    static final int TYPE_MASK_SHIFT = 20;

    /**
     * The bitmask used to fetch the type information from a single
     * <code>int</code> in the <code>actions[]</code> array.
     */
    static final int TYPE_MASK     = 0x0FF00000;

    /**
     * The bitmask used to fetch the register information from a single
     * <code>int</code> in the <code>actions[]</code> array.
     */
    static final int REGISTER_MASK = 0x000FFFFF;

    /**
     * The LOWER constant is the index at which the length of the low stretch is held.
     */
    static final int LOWER = 0;
    
    /**
     * The UPPER constant is the index at which the length of the high stretch is held.
     */
    static final int UPPER = 1;
    
    /**
     * The START constant holds the index at which the 'meso' part of the array begins.
     */
    static final int START = 2;

    /**
     * Constructs the <code>MesoArrayDPA</code> on the given action
     * transition table, go transition table, and action table.
     */
    public MesoArrayDPA(int[][] action, 
			int[][] go, 
			int[] actions)
    {
	this.action = action;
	this.go = go;
	this.actions = actions;
    }

    public Action action(int state_num, int symbol)
    {
	if (DEBUG) 
	    log().debug()
		.write("MesoArrayDPA: action table length is ")
		.write(action.length)
		.out();

	// cache out the right part of the 2D array
	int[] state = action[state_num];

	if (DEBUG) 
	    log().debug()
		.write("MesoArrayDPA: state ")
		.write(state_num)
		.write(" is ")
		.write(state == null 
		       ? "null" 
		       : "non-null")
		.out();

	// check if the given symbol falls below the minimum value or
	// if the given symbol falls above the maximum value
	if (symbol < state[LOWER] || symbol > state[UPPER]) {
	    return ERROR;
	} else {
	    // pull out the right action
	    int action = actions[state[symbol - state[LOWER] + START]];

	    if (DEBUG) 
		log().debug()
		    .write("raw encoded action is ")
		    .write(action)
		    .out();

	    // now do the bitmasks
	    a.value = action & REGISTER_MASK;
	    a.type = (action & TYPE_MASK) >> TYPE_MASK_SHIFT;

	    // and you're done
	    return a;
	}
    }

    public int go(int state_num, int symbol)
    {
	// cache out the right part of the 2D array
	int[] state = go[state_num];

	// check if the given symbol falls below the minimum value or
	// if the given symbol falls above the maximum value
	if (symbol < state[LOWER] || symbol > state[UPPER]) {
	    return 0;
	} else {
	    // pull out the right action
	    return state[symbol - state[LOWER] + START];
	}
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("mpa", this); // Meso Pushdown Automata
	return log;
    }
    
    public int[][] action;
    public int[][] go;
    public int[] actions;

    private Log log;

    /**
     * A standard error <code>Action</code> cached here for
     * convenience.  
     */
    private static final Action ERROR = new StandardAction();
    
    /**
     * This is the object which we will re-use.
     */
    private static final StandardAction a = new StandardAction();
}








