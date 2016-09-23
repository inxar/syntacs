/**
 * $Id: CFTerminal.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.context_free;

import org.inxar.syntacs.grammar.context_free.Terminal;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.util.IntSet;
import com.inxar.syntacs.util.SingletonIntSet;

/**
 * Standard <code>Terminal</code> implementation.
 */
public class CFTerminal
    implements Terminal
{
    /**
     * Constructs the <code>CFTerminal</code> on the given
     * <code>Token</code> which this Terminal abstracts.
     */
    CFTerminal(Token type)
    {
	this.type = type;
    }

    /**
     * Constructs the <code>CFTerminal</code> on the given ID number
     * of a general <code>Token</code> type and <code>Token</code>
     * name.
     */
    CFTerminal(int tokenID, String tokenName)
    {
	this.type = new GeneralToken(tokenID, tokenName);
    }

    void setID(int ID)
    {
	//System.out.println("CFTerminal.setID(int ID): ID is "+ID+" for terminal "+type);
	this.ID = ID;
    }

    public int getID()
    {
	return ID;
    }

    public String getName()
    {
	return type.getName();
    }

    public Token getToken()
    {
	return type;
    }

    public boolean isTerminal()
    {
	return true;
    }

    public boolean isNullable()
    {
	return false;
    }

    public IntSet getFirstSet()
    {
	if (first == null)
	    first = new SingletonIntSet(ID);
	return first;
    }

    public String toString()
    {
	return "("+ID+","+type.toString()+")";
    }

    public boolean equals(Object other)
    {
	if (other == null || !(other instanceof Terminal))
	    return false;
	if (other == this)
	    return true;
	return this.ID == ((Terminal)other).getID();
    }

    // ******************************************************
    // INSTANCE FIELDS
    // ******************************************************
    private int ID;
    private IntSet first;
    private Token type;

    /**
     * Standard implementation of a RegularToken.
     */
    static final class GeneralToken
	implements Token
    {
	GeneralToken(int ID, String name)
	{
	    this.ID = ID;
	    this.name = name;
	}

	public String toString()
	{
	    return name;
	}

	public int getID() { return ID; }
	public String getName() { return name; }
	public Object clone() throws CloneNotSupportedException { return super.clone(); }

	int ID;
	String name;
    }

}
