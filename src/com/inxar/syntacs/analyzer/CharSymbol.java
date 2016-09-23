/**
 * $Id: CharSymbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.analyzer.Symbol;
import com.inxar.syntacs.util.Tree;

/**
 * Concrete implementation of <code>Symbol</code> which internally
 * contains a char.
 */
public class CharSymbol extends AbstractSymbol
{
    /**
     * Constructs the <code>CharSymbol</code> with the given
     * <code>char</code> value.
     */
    public CharSymbol(char value)
    {
	super(Token.ERROR);
	this.value = value;
    }

    /**
     * Constructs the <code>CharSymbol</code> with the given
     * type and <code>char</code> value.
     */
    public CharSymbol(int type, char value)
    {
	super(type);
	this.value = value;
    }

    public String toString()
    {
	return "("+type+", `"+value+"')";
    }

    public void toTree(Tree t)
    {
	t.add(String.valueOf(value));
    }

    public boolean equals(Object other)
    {
	// obviously true
	if (this == other)
	    return true;

	// obviously false
	if (other == null || !(other instanceof CharSymbol))
	    return false;

	// ok to narrow
	CharSymbol that = (CharSymbol)other;

	// compare fields
	return this.type == that.type && this.value == that.value;
    }

    /**
     * The <code>char</code> value of the symbol <code>Symbol</code>.
     */
    public char value;
}
