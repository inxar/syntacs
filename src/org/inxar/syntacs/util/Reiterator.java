/**
 * $Id: Reiterator.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.util;

/**
 * The <code>Reiterator</code> interface abstracts traversal over a
 * list of nodes where each node has an <code>int</code> key and a
 * <code>IntSet</code> value.  Additionally, the "cursor" remains over
 * the same node until the <code>next()</code> method is called.  This
 * implies that one can access the <code>IntSet</code> value or
 * <code>int</code> key multiple times at the same node (and hence the
 * "re" in "re" + "iterator") 
 */
public interface Reiterator
{
    /**
     * Returns true if there is at least one more element in the list.  
     */
    boolean hasNext();
    
    /**
     * Advances the pointer to the next element in the list.
     */
    void next();
    
    /**
     * Returns the key of the current element.
     */
    int key();
    
    /**
     * Returns a set view of the current element's values.
     */
    IntSet values();
}

