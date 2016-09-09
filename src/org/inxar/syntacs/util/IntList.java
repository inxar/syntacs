/**
 * $Id: IntList.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * <code>IntList</code> abstracts a dynamically-growable list of
 * integers.  
 */
public interface IntList
    extends IntArray
{
    /**
     * Adds the given value to the end of the list and returns the
     * index number of the element that was written.
     */
    int add(int value);
    
    /**
     * Returns an iterator view over the list.
     */
    IntIterator iterator();

    /**
     * Returns <code>true</code> if the given <code>value</code> is in
     * the list, <code>false</code> otherwise.  
     */
    boolean contains(int value);
}

