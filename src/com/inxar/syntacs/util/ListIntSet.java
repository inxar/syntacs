/**
 * $Id: ListIntSet.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete implementation of <code>IntSet</code> which uses a linked
 * list internally.  
 */
public class ListIntSet
    implements IntSet
{
    private static final boolean DEBUG = false;

    /**
     * Constructs the <code>ListIntSet</code>.
     */
    public ListIntSet()
    {
    	this.size = 0;
    	this.hash = 127;
    }

    public boolean contains(int val)
    {
    	Link link = root;
    	while (link != null) {
	    if (link.val == val)
		return true;
	    link = link.next;
    	}
    	return false;
    }

    public boolean isEmpty()
    {
    	return root == null;
    }

    public void put(int val)
    {
	if (!contains(val)) {
	    root = new Link(val, root);
	    hash += val * size;
	    size++;
	}
    }

    public IntIterator iterator()
    {
	return new ListIterator(root);
    }

    public void union(IntSet other)
    {
    	IntIterator iterator = other.iterator();
    	while (iterator.hasNext())
	    put(iterator.next());
    }

    public int size()
    {
    	return size;
    }

    public IntArray toIntArray()
    {
	IntList list = new ArrayIntList(size);
	IntIterator iterator = iterator();
	while (iterator.hasNext())
	    list.add(iterator.next());
	return list;
    }

    public boolean equals(Object other)
    {
	if (this == other)
	    return true;

	if (!(other instanceof IntSet)) {
	    return false;

	} else {

	    IntSet that = (IntSet)other;
	    
	    if (this.size != that.size())
		return false;

	    IntIterator members = that.iterator();
	    while(members.hasNext())
		if (!contains(members.next()))
		    return false;

	    return true;
	}
    }

    public int hashCode()
    {
	return hash;
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
	buf.append('{');

	boolean notFirst = false;
	Link link = root;
	while (link != null) {
	    if  (notFirst) buf.append(',');
	    else notFirst = true;
	    buf.append(link.val);
	    link = link.next;
        }
	buf.append('}');

	return buf.toString();
    }

    public Object clone() throws CloneNotSupportedException
    {
	// clone self
	ListIntSet clone = (ListIntSet)super.clone();
	// clone the regex
	if (this.root != null)
	    clone.root = (Link)this.root.clone();
	return clone;
    }

    private Link root;
    private int size;
    private int hash;

    private static final class Link
    {
	Link(int val, Link next)
	{
	    this.val = val;
	    this.next = next;
	}

	public Object clone() throws CloneNotSupportedException
	{
	    // clone self
	    Link clone = (Link)super.clone();
	    // clone the regex
	    clone.next = (Link)this.next.clone();
	    return clone;
	}

	int val;
	Link next;
    }

    private static final class ListIterator implements IntIterator
    {
	ListIterator(Link link)
	{
	    this.link = link;
	}

	public boolean hasNext()
	{
	    return link != null;
	}

	public int next()
	{
            int val = link.val;
            link = link.next;
            return val;
	}

	private Link link;
    }
}

