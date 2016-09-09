/**
 * $Id: DPA.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.automaton.pushdown;

/**
 * The "Deterministic Pushdown Automaton" (DPA) abstracts a state
 * machine which maintains a stack of Symbol objects used for
 * recognizing an LR-grammar.
 * <P>
 *
 * For more information regarding Pushdown Automata, see Chapter 3 in
 * the Dragon Book or Chapter 5 in "Introduction to Automata Theory,
 * Languages, and Computation" (Hopcroft, Ullman; 1979).
 *
 * <P>
 *
 * The term DPA is used somewhat loosely here.  Specifically, this
 * interface really only tries to abstract the automata part of a DPA
 * and does not do any stack manipulation (the <code>Parser</code>
 * does this).  
 */
public interface DPA
{
    /**
     * Constant for the start state of a <code>DPA</code>.
     * <code>Parser</code>s should use this upon state initialization.
     */
    int START_STATE = 0;

    /**
     * Returns the <code>Action</code> associated by traversing the
     * path given from the given state over the given symbol.  The
     * <code>Action</code> object returned should never be
     * <code>null</code> but may be the <code>error</code> action.
     */
    Action action(int state, int symbol);
    
    /**
     * Returns the state reached upon transition after reduction to
     * the given <code>nonterminal</code>.  
     */
    int go(int state, int nonterminal);
}

