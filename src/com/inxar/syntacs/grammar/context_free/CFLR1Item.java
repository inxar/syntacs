/**
 * $Id: CFLR1Item.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.syntacs.grammar.context_free.Production;
import org.inxar.syntacs.grammar.context_free.LR1Item;
import org.inxar.syntacs.grammar.context_free.Item;
import org.inxar.syntacs.grammar.context_free.GrammarSymbol;
import org.inxar.syntacs.util.*;

/**
 * Standard <code>LR1Item</code> implementation.
 */
public class CFLR1Item
    implements LR1Item
{
    /**
     * Constructs the <code>CFLR1Item</code> on the given
     * <code>CFItem</code> core <code>and</code> Terminal lookahead.
     * */
    CFLR1Item(CFItem core, Terminal lookahead)
    {
	this.core = core;
	this.lookahead = lookahead;
    }

    void setID(int ID)
    {
	this.ID = ID;
    }

    public int getID()
    {
	return ID;
    }

    public Production getProduction()
    {
	return core.production;
    }

    public Terminal getLookahead()
    {
	return lookahead;
    }

    public Item getCore()
    {
	return core;
    }

    public boolean hasNext()
    {
	return core.hasNext();
    }

    public boolean hasPrevious()
    {
	return core.hasPrevious();
    }

    public Item nextItem()
    {
	return core.nextItem;
    }

    public Item previousItem()
    {
	return core.previousItem;
    }

    public GrammarSymbol nextSymbol()
    {
	return core.nextSymbol;
    }

    public GrammarSymbol previousSymbol()
    {
	return core.previousSymbol;
    }

    public LR1Item lookahead(Terminal lookahead)
    {
	if (this.lookahead == lookahead)
	    return this;
	else
	    return core.lookahead(lookahead);
    }

    public IntSet getFirstSet()
    {
	return core.getFirstSet();
    }

    public String toString()
    {
	if (string == null) {

	    StringBuffer b = new StringBuffer("[")
		.append(core.production.getNonTerminal())
		.append(" -> ");

	    IntArray array = core.production.getGrammarSymbols();
	    int len = array.length();

	    for (int i=0; i <= len; i++) {
		if (i == core.position)
		    b.append("<>");
		if (i < len)
		    b.append(core.grammar.getGrammarSymbol( array.at(i) ));
	    }

	    string = b.append(", ").append(lookahead).append(']').toString();
	}

	return string;
    }

    private int ID;
    private CFItem core;
    private Terminal lookahead;
    private String string;
    private IntSet first = null;
}
