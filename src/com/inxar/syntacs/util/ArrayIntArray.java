/**
 * $Id: ArrayIntArray.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete implementation of <code>IntArray</code> which uses an
 * array internally.  
 */
public class ArrayIntArray
    implements IntArray
{
    /**
     * Constructs a new <code>ArrayIntArray</code> wrapping the given
     * <code>int[]</code>.  
     */
    public ArrayIntArray(int[] src)
    {
	this.src = src;
    }

    public int at(int index)
    {
	return src[index];
    }

    public int[] toArray()
    {
	int[] dst = new int[src.length];
	System.arraycopy(src, 0, dst, 0, src.length);
	return dst;
    }

    public int length()
    {
	return src.length;
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();
	b.append('[');
	for (int i = 0; i < src.length; i++) {
	    if (i > 0)
		b.append(',');
	    b.append(src[i]);
	}

	b.append(']');
	return b.toString();
    }

    public Object clone() throws CloneNotSupportedException
    {
	// It is OK for them to share the same int array is it is
	// immutable.
	return super.clone();

    }

    private int[] src;
}







