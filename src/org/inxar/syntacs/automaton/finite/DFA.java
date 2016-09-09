/**
 * $Id: DFA.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.automaton.finite;

/**
 * The "Deterministic Finite Automaton" (<code>DFA</code>) interface
 * abstracts a Moore state machine used to recognize discrete
 * sequences of symbols (usually character symbols).  More information
 * about <code>DFA</code>'s can be found pretty much anywhere, but
 * chapter 3 of the Dragon Book is a good place to start.  */
public interface DFA
{
    /*
     * WHOA! -- what is the convention?  Are final states indicated by
     * a non -1 value or *a* negative value.  For instance DFA's are
     * using the 'if it's less then zero, use the abs value as the
     * token type number.  What the deal?  
     */

    /**
     * Lexical analyzers or other tools should synchronize to the
     * START_STATE constant upon initialization.  
     */
    int START_STATE = 1;

    /**
     * The <code>DEAD_STATE</code> is the state where all exit paths
     * lead back to the <code>DEAD_STATE</code>.  
     */
    int DEAD_STATE = 0;

    /**
     * Returns the next state for the given state and given input symbol.  
     */
    int go(int state, int input);
    
    /**
     * Returns the output at the given state.  Output values generally
     * correspond to to Token ID numbers.  If no output has been
     * defined for the given state, <code>Token.UNDEF</code> is
     * returned.
     */
    int output(int state);
}



