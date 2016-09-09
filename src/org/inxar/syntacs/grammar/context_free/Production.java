/**
 * $Id: Production.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar.context_free;

import org.inxar.syntacs.util.IntArray;

/**
 * The <code>Production</code> interface is an abstraction of a
 * discrete 'rule' in a grammar.  A <code>Production</code> specifies
 * a valid sequence of <code>GrammarSymbol</code>s that may be reduce
 * to a certain <code>NonTerminal</code>.
 */
public interface Production
{
    /**
     * Gets the globally allocated identifier for this
     * <code>Production</code>.  
     */
    int getID();
    
    /**
     * Returns the <code>NonTerminal</code> defined as the left hand
     * side of this <code>Production</code>.
     */
    NonTerminal getNonTerminal();

    /**
     * Returns the first <code>Item</code> in the
     * <code>Production</code>, A := *ab.
     */
    Item getInitialItem();
    
    /**
     * Concatenates the given <code>GrammarSymbol</code> to the end of
     * this <code>Production</code> sequence.  The
     * <code>Production</code> object is returned simply to support a
     * convenient <code>java.lang.StringBuffer<code> style of coding.  
     */
    Production add(GrammarSymbol grammarSymbol);

    /**
     * Returns an <code>IntArray</code> for the right hand side of
     * this <code>Production</code>.  Each member in the array is
     * the ID of a <code>GrammarSymbol</code>.
     */
    IntArray getGrammarSymbols();

    /**
     * Returns the number of <code>GrammarSymbols</code> on the right
     * hand side.  This has the same effect as
     * <code>getGrammarSymbols().length()</code>.
     */
    int length();
}


