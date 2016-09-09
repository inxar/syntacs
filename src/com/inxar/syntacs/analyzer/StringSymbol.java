/**
 * $Id: StringSymbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.analyzer.Symbol;

/**
 * Concrete implementation of <code>Symbol</code> which internally
 * contains a String.  
 */
public class StringSymbol
    implements Symbol
{
    /**
     * Constructs the <code>StringSymbol</code> with the given
     * <code>String</code> value.
     */
    public StringSymbol(String value)
    {
	this.value = value;
    }

    /**
     * Constructs the <code>StringSymbol</code> with the given
     * type and <code>String</code> value.
     */
    public StringSymbol(int type, String value)
    {
	this.type = type;
	this.value = value;
    }

    public int getSymbolType()
    {
	return type;
    }

    public void setSymbolType(int type)
    {
	this.type = type;
    }

    public String toString()
    {
	return "("+type+", `"+value+"')";
    }

    public boolean equals(Object other)
    {
	// obviously true
	if (this == other)
	    return true;

	// obviously false
	if (other == null || !(other instanceof StringSymbol))
	    return false;

	// ok to narrow
	StringSymbol that = (StringSymbol)other;

	// compare fields
	return this.type == that.type && this.value == that.value;
    }

    /**
     * The (token) type of this <code>Symbol</code>.  
     */
    private int type;

    /**
     * The value of the symbol <code>Symbol</code> represented as an
     * <code>String</code>.
     */
    public String value;
}




