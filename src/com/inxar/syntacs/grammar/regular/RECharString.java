/**
 * $Id: RECharString.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Standard <code>CharString</code> implementation.
 */
public class RECharString
    implements CharString
{
    /**
     * Constructs the <code>RECharString</code> on the given
     * <code>REGrammar</code> and input <code>String</code>.
     */
    public RECharString(REGrammar grammar, String s)
    {
    	this.grammar = grammar;
    	this.s = s;
    	init();
    }

    private void init()
    {
	head = grammar.newInterval(s.charAt(0));
	tail = head;

	// iterate over the chars of the string
	for (int i = 1; i < s.length(); i++)
	    // make the concatentation
	    tail = grammar.newConcatenation(tail, grammar.newInterval(s.charAt(i)));
    }

    public boolean isNullable()
    {
	return head.isNullable();
    }

    public IntSet getFirstSet()
    {
	return head.getFirstSet();
    }

    public IntSet getLastSet()
    {
	return tail.getLastSet();
    }

    public void follow()
    {
    	head.follow();
    	tail.follow();
    }

    public RegularExpression getHead()
    {
    	return head;
    }

    public RegularExpression getTail()
    {
    	return tail;
    }

    public String toString()
    {
	return s;
    }

    public Object clone() throws CloneNotSupportedException
    {
	return super.clone();
    }

    private String s;
    private RegularExpression head;
    private RegularExpression tail;
    private REGrammar grammar;
}

