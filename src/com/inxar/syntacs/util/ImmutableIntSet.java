/**
 * $Id: ImmutableIntSet.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.util;

import org.inxar.syntacs.util.*;

/**
 * <code>IntSet</code> wrapper implementation which throws
 * <code>UnsupportedMethodOperation</code> exceptions when the set is
 * attempted to be changed.  
 */
public class ImmutableIntSet
    implements IntSet
{
    private static final boolean DEBUG = false;
    
    /**
     * Constructs the <code>ImmutableIntSet</code> wrapping the given
     * set.
     */
    public ImmutableIntSet(IntSet set)
    {
	if (set == null)
	    throw new NullPointerException("Immutable Set cannot be non-null");

	try {

	    this.set = (IntSet)set.clone();

	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new RuntimeException(ex.getMessage());
	}
    }
    
    public boolean contains(int id)
    {
    	return set.contains(id);
    }

    public boolean isEmpty()
    {
    	return set.isEmpty();
    }

    public void put(int id)
    {
	throw new UnsupportedOperationException("This set is immutable");
    }
    
    public void put(IntIterator iter)
    {
	throw new UnsupportedOperationException("This set is immutable");
    }
    
    public IntIterator iterator()
    {
	return set.iterator();
    }

    public void union(IntSet other)
    {
	throw new UnsupportedOperationException("This set is immutable");
    }

    public int size()
    {
	return set.size();
    }

    public boolean equals(Object other)
    {
	return set.equals(other);
    }

    public int hashCode()
    {
	return set.hashCode();
    }

    public String toString()
    {
	return set.toString();
    }

    public IntArray toIntArray()
    {
	return set.toIntArray();
    }

    private IntSet set;

    public Object clone() throws CloneNotSupportedException
    {
	throw new UnsupportedOperationException("This set is immutable");
    }

}










