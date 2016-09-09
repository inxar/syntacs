/**
 * $Id: ArrayIntStack.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete implementation of <code>IntStack</code> which uses an
 * array internally.  
 */
public class ArrayIntStack
    implements IntStack, java.io.Serializable
{
    /**
     * Constructs the <code>ArrayIntStack</code> with the given
     * initial capacity.  
     */
    public ArrayIntStack(int capacity)
    {
        stack = new int[capacity];
        max = capacity - 1;
        index = 0;
    }
    
    /**
     * Constructs the <code>ArrayIntStack</code> using a default
     * capacity.  
     */
    public ArrayIntStack()
    {
        this(7);
    }

    public void push(int i)
    {
        check();
        stack[index++] = i;
    }

    public int pop()
    {
        return stack[--index];
    }

    public int peel(int len)
    {
        //System.out.println("looking to peel len "+len+" off stack "+this+" with index "+index+" and return index at "+(index - (len+1)));
        //return stack[index -= (len - 1)]; /* note side-effect */
        return stack[(index -= len) - 1]; /* note side-effect */
    }

    public int peek()
    {
        return stack[index - 1];
    }

    public boolean isEmpty()
    {
        return index == 0;
    }

    public boolean contains(int value)
    {
	int len = index;

	for (int i = 0; i < len; i++)
	    if (stack[i] == value)
		return true;
        return false;
    }

    public int size()
    {
    	return index;
    }

    public synchronized IntArray toIntArray()
    {
	int[] dst = new int[index];
	System.arraycopy(stack, 0, dst, 0, index);
	return new ArrayIntArray(dst);
    }

    public String toString()
    {
    	StringBuffer buf = new StringBuffer("[");

    	for (int i=0; i < index; i++) {
	    if (i > 0) buf.append(',');
	    buf.append(stack[i]);
	}

    	return buf.append(']').toString();
    }

    private void check()
    {
        if (index == max) {
            int[] dst = new int[stack.length * 2];
            System.arraycopy(stack, 0, dst, 0, stack.length);
            stack = dst;
            max = stack.length - 1;
        }
    }

    private int index;
    private int max;
    private int[] stack;
}


