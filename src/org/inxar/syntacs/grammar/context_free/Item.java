/**
 * $Id: Item.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

import org.inxar.syntacs.util.IntSet;

/**
 * The <code>Item</code> interface represents a discrete position or
 * progress along the right hand side of a <code>Production</code>.  
 */
public interface Item
{
    /**
     * Returns the unique allocated ID for this <code>Item</code>.
     */
    int getID();

    /**
     * Returns the <code>Production</code> to which this
     * <code>Item</code> is associated.  
     */
    Production getProduction();

    /**
     * Returns <code>true</code> if the dot is not after the last
     * symbol in the <code>Production</code>.  
     */
    boolean hasNext();

    /**
     * Returns <code>true</code> if the dot is not before the first
     * symbol in the <code>Production</code>.  
     */
    boolean hasPrevious();

    /**
     * Returns the <code>Item</code> which represents moving the dot
     * forward one symbol.  If there is no such <code>Item</code> (ie
     * the dot is already at the end), <code>null</code> is returned.  
     */
    Item nextItem();

    /**
     * Returns the <code>Item</code> which represents moving the dot
     * backwards one symbol.  If there is no such <code>Item</code>
     * (ie the dot is already at the beginning), <code>null</code> is
     * returned.  
     */
    Item previousItem();

    /**
     * Returns the <code>GrammarSymbol</code> b where [a dot b].
     */
    GrammarSymbol nextSymbol();

    /**
     * Returns the <code>GrammarSymbol</code> a where [a dot b].
     */
    GrammarSymbol previousSymbol();
    
    /**
     * Return a <code>IntSet</code> of <code>Item</code>s over the
     * <code>FIRST(beta, a);</code> 
     */
    IntSet getFirstSet();
    
    /**
     * Returns an <code>LR1Item</code> corresponding to this core
     * <code>Item</code> with the given lookahead.  
     */
    LR1Item lookahead(Terminal lookahead);
}



