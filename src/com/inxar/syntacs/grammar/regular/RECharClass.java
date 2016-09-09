/**
 * $Id: RECharClass.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.regular;

import org.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.util.IntSet;

/**
 * Standard <code>CharClass</code> implementation.
 */
public class RECharClass
    implements CharClass
{
    private static final boolean DEBUG = false;

    /**
     * Constructs the <code>RECharClass</code> on the given
     * <code>REGrammar</code>.
     */
    public RECharClass(REGrammar grammar)
    {
    	this.grammar = grammar;
    }

    public String toString()
    {
	// tmp holder
	StringBuffer buf = new StringBuffer();

	// front matter
	buf.append('[');

	if (isNot)
	    buf.append('^');

	// middle matter
	int len = v.size();
	for (int i=0; i < len; i++)
	    buf.append(v.elementAt(i));

	// back matter
	return buf.append(']')
	    .toString();
    }

    public boolean isNullable()
    {
    	if (regex == null) init();
	return regex.isNullable();
    }

    public IntSet getFirstSet()
    {
    	if (regex == null) init();
	return regex.getFirstSet();
    }

    public IntSet getLastSet()
    {
    	if (regex == null) init();
	return regex.getLastSet();
    }

    public void follow()
    {
    	if (regex == null) init();
    	regex.follow();
    }

    private void init()
    {
	// we want to convert to an internal formal regular
	// expression.  There are two possibilities for what this
	// regex might be -- if it's a simple character class like
	// [a-z], this can represented by a single Range object.  If
	// it is more complex, such as [a-z0-9], then we will have to
	// use a union.

	// cache out the length of the vector
	int len = v.size();

	// a temporary bitset to hold the flags for which chars are
	// included.  It is the length of the unicode charset
        boolean[] chars = new boolean[Character.MAX_VALUE+1];

	// now we want to set the positions of the bitset for those
	// positions that are named by the specified ranges
	for (int i = 0; i < len; i++)
	    {
		// get the object
		Node node = (Node)v.elementAt(i);

		// now loop this section, setting the chars flag for
		// each one
		for (int j = node.lo; j <= node.hi; j++) {
		    chars[j] = true;
		}
	    }

        // now we have a contiguous block of booleans.  We need to
        // convert this information into Range objects, but we dont
        // yet know how many.
        java.util.Vector intervals = new java.util.Vector();

	// temporaries used in loop below
	int lo = -1;
	int hi = -1;

        // We are now ready to make the range objects.  We will loop
        // over the length of the boolean array we created above.  We
        // will add a range for each contiguous block that is either:
        // --chars[i..i] is true and isNot is false --chars[i..i] is
        // false and isNot is true
	for (int i = 0; i <= Character.MAX_VALUE; i++) {

	    // reset the lo value
	    lo = i;

	    try {
		// loop while adjacent elements are the same
		while (chars[i] == chars[++i]);
	    } catch (ArrayIndexOutOfBoundsException aiiobex) {
	    }

	    // IntSet the hi value to one less than the counter, since it
	    // just ran one too far.  It will be incremented by for
	    // loop
	    hi = --i;

	    if (DEBUG) System.out.println("got lo/hi: " + lo + "/" + hi);

	    // now add the range from lo to hi iff the 'sense' and
	    // isNot are different
	    if (chars[i] ^ isNot)
		intervals.addElement(grammar.newInterval(lo, hi));
	    else
		continue;
	}

	// count the number of intervals we just made
	len = intervals.size();

	// now we have one or more intervals in the temoprary
	// container.  We either convert this to a union or simply use
	// the Interval directory if there is only one of them.
	if (len == 0) {
	    throw new RuntimeException
		("Character classes must specify at least one character!");
	} else if (len == 1) {
	    regex = (RegularExpression)intervals.elementAt(0);
	} else {
	    Union union = grammar.newUnion();
	    for (int i = 0; i < len; i++)
		union.addAllele((RegularExpression)intervals.elementAt(i));
	    regex = union;
	}

	// whew! done.
    }

    /**
     * True if this is a negated character class
     */
    public boolean isNegated()
    {
	return isNot;
    }

    /**
     * Sets the negate flag
     */
    public void isNegated(boolean value)
    {
	this.isNot = value;
    }

    public void add(char c)
    {
	add(c, c);
    }

    public void add(char lo, char hi)
    {
       	// make a new node over the given interval
       	v.addElement( new Node(lo, hi) );
    }

    public Object clone() throws CloneNotSupportedException
    {
	return super.clone();
    }

    private REGrammar grammar;
    private RegularExpression regex;
    private boolean isNot;
    private java.util.Vector v = new java.util.Vector();

    /**
     * This is essentially a temporary stand-in for an Interval.  The
     * reason we use this is so that we do not end up allocating
     * actual intervals needlessly.  By using up intervals and thus
     * interval ids, the bitvectors involved in their union must get
     * bigger.  
     */
    private static final class Node
    {
	Node(int lo, int hi)
	{
	    this.lo = lo;
	    this.hi = hi;
	}

	public String toString()
	{
	    if (lo == hi) {
                return "" + (char)lo;
	    }
            return "<" + lo + "," + hi + ">";
	}

	int lo;
	int hi;
    }
}

