/**
 * $Id: LRConstructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Abstract implementation of <code>LRConstructor</code> which defines
 * code common to different LR construction algorithms.
 */
public abstract class LRConstructor
    implements DPAConstructor /* , LR0Machine */
{
    /**
     * Constructs an <code>LRConstructor</code>.
     */
    protected LRConstructor()
    {
	this.stateHash = new java.util.Hashtable();
	this.stateVector = new java.util.Vector();
    }

    public IntSet getState(int id)
    {
	return ((State)stateVector.elementAt(id)).set;
    }

    // ================================================================
    // state management methods
    // ================================================================

    /**
     * This method returns the state of the given set.  If the set is
     * not already in the canonical collection, a new set is
     * allocated.  
     */
    protected State lookup(IntSet items)
    {
	Object o = stateHash.get(items);
	return o == null ? allocState(items) : (State)o;
    }

    protected State lookup(int id)
    {
	return (State)stateVector.elementAt(id);
    }

    protected State allocState(IntSet items)
    {
        State state = new State(stateVector.size(), items);
	stateVector.addElement(state);
        stateHash.put(items, state);
	return state;
    }

    // ================================================================
    // general reporting methods
    // ================================================================

    protected String show(IntSet set)
    {
	StringBuffer buf = new StringBuffer();
	IntIterator iter = set.iterator();
	while ( iter.hasNext() ) 
	    buf.append(grammar.getItem(iter.next())).append(StringTools.NEWLINE);
	return buf.toString();
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
    	showCanon(buf);
    	return buf.toString();
    }

    protected void showCanon(StringBuffer buf)
    {
    	buf.append("canonical collection is: ").append(StringTools.NEWLINE);
	int len = stateVector.size();
	State state = null;

    	for (int i=0; i<len; i++) {
	    state = (State)stateVector.elementAt(i);
	    buf.append(state.getID())
		.append(':').append(StringTools.NEWLINE)
		.append(show(state.set));
    	}
    }

    // ================================================================
    // instance fields 
    // ================================================================

    protected ContextFreeSet grammar;
    protected java.util.Hashtable stateHash;
    protected java.util.Vector stateVector;

    /**
     * Concrete implementation of a State used by several LR
     * construction algorithms.
     */
    protected static class State 
    {
	State(int id, IntSet set)
	{
	    this.id = id;
	    this.set = set;
	    this.paths = new java.util.Hashtable();
	}

	public int hashCode()
	{
            return set.hashCode();
	}

	public boolean equals(Object o)
	{
	    return set.equals(o);
	}

	public void connect(GrammarSymbol grammarSymbol, State nextState)
	{
	    paths.put(grammarSymbol, nextState);
	}

	public State go(GrammarSymbol grammarSymbol)
	{
	    return (State)paths.get(grammarSymbol);
	}

	public int getID()
	{
	    return id;
	}

	public String toString()
	{
	    return "<"+id+">";
	}

	int id;
	IntSet set;
	java.util.Hashtable paths;
    }
}
















