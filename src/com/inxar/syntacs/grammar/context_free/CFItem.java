/**
 * $Id: CFItem.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Standard <code>Item</code> implementation.
 */
public class CFItem
    implements Item
{
    /**
     * Constructs the <code>CFItem</code> on the given
     * <code>CFGrammar</code>, Production, and position in that
     * production.  
     */
    CFItem(CFGrammar grammar, Production production, int position)
    {
	this.grammar = grammar;
	this.production = production;
	this.position = position;
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
	return production;
    }

    public LR1Item lookahead(Terminal lookahead)
    {
	// see if we need to make the hash
	if (lr1s == null)
	    lr1s = new java.util.Hashtable();

	// do the lookup
	LR1Item lr1 = (LR1Item)lr1s.get(lookahead);

	// do we need to make it?
        if (lr1 == null) {
	    // create new
	    lr1 = grammar.newLR1Item(this, lookahead);
	    // store
	    lr1s.put(lookahead, lr1);
        }
        // done
	return lr1;
    }

    public boolean hasNext()
    {
	return nextItem != null;
    }

    public boolean hasPrevious()
    {
	return previousItem != null;
    }

    public Item nextItem()
    {
	return nextItem;
    }

    public Item previousItem()
    {
	return previousItem;
    }

    public GrammarSymbol nextSymbol()
    {
	return nextSymbol;
    }

    public GrammarSymbol previousSymbol()
    {
	return previousSymbol;
    }

    public IntSet getFirstSet()
    {
       	if (first == null) {

            // check if this item is at the end of a production.
            if (!hasNext()) {
            	// make a new set with the single item
            	first = EmptyIntSet.EMPTY_SET;
            	// done
            	return first;
            }

            // make a new bitvector set
            first = new BitSetIntSet(3);

	    // initialize the loop variable
	    Item item = this;
	    GrammarSymbol symbol = null;
	    // union the grammar symbol first sets from here to the
	    // end of the production while nullable.
	    while (item.hasNext()) {
		// grab each one
		symbol = item.nextSymbol();
		// union the first sets
		first.union(symbol.getFirstSet());
		// exit the loop when nullability terminates
		if (!symbol.isNullable())
		    break;
		else
		    item = item.nextItem(); /* advance */
	    }
	}
	return first;
    }

    public String toString()
    {
	if (string == null) {

	    StringBuffer b = new StringBuffer("[ ")
		.append(production.getNonTerminal())
		.append(" -> ");

	    IntArray array = production.getGrammarSymbols();
	    int len = array.length();
	    for (int i = 0; i <= len; i++) {
		if (i > 0) 
		    b.append(' ');
		if (i == position) 	
		    b.append("<> ");
		if (i < len)
		    b.append(grammar.getGrammarSymbol( array.at(i) ));
	    }

	    string = b.append(']').toString();
	}

	return string;
    }

    // instance variables
    private int ID;
    private String string;
    private IntSet first;
    private java.util.Hashtable lr1s = null;

    int position;
    CFGrammar grammar;
    Production production;
    Item nextItem = null;
    Item previousItem = null;
    GrammarSymbol nextSymbol = null;
    GrammarSymbol previousSymbol = null;
}


