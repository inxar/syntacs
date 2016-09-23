/**
 * $Id: ListSymbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer;

import java.util.*;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.analyzer.Symbol;
import com.inxar.syntacs.util.Tree;

/**
 * Concrete implementation of <code>Symbol</code> which uses a list.
 */
public class ListSymbol extends AbstractSymbol
{
    /**
     * Constructs the <code>ListSymbol</code> with the given
     * <code>Object</code> value as the first List item.
     */
    public ListSymbol(Object value)
    {
	super(Token.ERROR);
	this.list = new LinkedList();
	this.list.add(value);
    }

    /**
     * Constructs the <code>ListSymbol</code> with the given
     * type and given <code>Object</code> value as the first list item.
     */
    public ListSymbol(int type, Object value)
    {
	super(type);
	this.list = new LinkedList();
	this.list.add(value);
    }

    public String toString()
    {
	return "("+type+", `"+list+"')";
    }

    public void toTree(Tree t)
    {
	if (list.size() == 1) {
	    ((AbstractSymbol)list.get(0)).toTree(t);
	} else {
	    t = t.add("@");

	    for (int i = 0; i < list.size(); i++) {
		Object obj = list.get(i);

		if (obj instanceof AbstractSymbol)
		    ((AbstractSymbol)obj).toTree(t);
		else
		    t.add(obj.toString());
	    }
	}
    }

    public boolean equals(Object other)
    {
	// obviously true
	if (this == other)
	    return true;

	// obviously false
	if (other == null || !(other instanceof ListSymbol))
	    return false;

	// ok to narrow
	ListSymbol that = (ListSymbol)other;

	// compare fields
	return this.list.equals(that.list);
    }

    /**
     * The value of the symbol <code>Symbol</code> represented as an
     * <code>Object</code>. This might be a <code>String</code> for
     * identifier tokens or an <code>Integer</code> object for integer
     * literals.
     */
    public List list;
}
