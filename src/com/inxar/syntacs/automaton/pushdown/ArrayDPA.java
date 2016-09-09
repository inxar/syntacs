/**
 * $Id: ArrayDPA.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>DPA</code> which uses full-length
 * array parse tables.  In this case (versus the <code>DFA</code>
 * case) this is acceptable since the input range is much smaller than
 * the Unicode character set.  
 */
public class ArrayDPA implements DPA
{
    private static final boolean DEBUG = false;

    /**
     * A standard error <code>Action</code> cached here for
     * convenience.  
     */
    public static final Action ERROR = new StandardAction();

    /**
     * Constructs the <code>ArrayDPA</code> on the given action table,
     * transition table, production length table, and Action registry.  
     */
    public ArrayDPA(int[][] action, 
		    int[][] go, 
		    Action[] actions)
    {
	this.action = action;
	this.go = go;
	this.actions = actions;
    }

    public Action action(int state, int symbol)
    {
	try {

	    if (DEBUG) 
		log().debug()
		    .write("ArrayDPA.action(): action at state ").write(state)
		    .write(", symbol ").write(symbol)
		    .write(", action is ").write(action)
		    .write(", action.length ").write(action.length)
		    .write(", action[state].length ").write(action[state].length)
		    .out();
	    
	    return
		action[state].length <= symbol || action[state][symbol] == 0
		? ERROR : actions[action[state][symbol]];
	    
	} catch (ArrayIndexOutOfBoundsException aioobex) {

	    if (state < 0)
		throw new ArrayIndexOutOfBoundsException
		    ("Bad state index (negative?): " + state);
	    else if (state >= action.length)
		throw new ArrayIndexOutOfBoundsException
		    ("State " + state + 
		     " is not valid.  The highest numbered state is " + action.length);
	    else if (symbol < 0)
		throw new ArrayIndexOutOfBoundsException
		    ("Bad symbol index (negative?): " + symbol);

	    else if (symbol >= action[state].length)
		throw new ArrayIndexOutOfBoundsException
		    ("Symbol " + (char)symbol + 
		     " is not valid for state " + state + 
		     ".  The highest numbered traversal is " + action[state].length);
	    else 
		throw aioobex;
	}
    }

    public int go(int state, int symbol)
    {
    	return go[state][symbol];
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("apa", this); // Array Pushdown Automaton
	return log;
    }

    public int[][] go;
    public int[][] action;
    public Action[] actions;
    private Log log;
}








