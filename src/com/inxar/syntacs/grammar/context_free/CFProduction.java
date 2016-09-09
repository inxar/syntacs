/**
 * $Id: CFProduction.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.ArrayList;
import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Standard <code>Production</code> implementation.
 */
public class CFProduction
    implements Production
{
    private static final boolean DEBUG = false;

    /**
     * Constructs the <code>CFProduction</code> on the given
     * <code>CFGrammar</code>, <code>NonTerminal</code>
     * right-hand-side.
     */
    CFProduction(CFGrammar grammar, int ID, NonTerminal nonTerminal)
    {
	this.grammar = grammar;
	this.ID = ID;
	this.nonTerminal = nonTerminal;
	this.symbols = new GrammarSymbolList();
	this.initialItem = null;
    }

    private void setID(int ID)
    {
	this.ID = ID;
    }

    /**
     * Assume that the CFGrammar object will call this method on all
     * productions when 'compiling'.  
     */
    void itemize()
    {
	//System.out.println("Itemizing now... "+ID);
	// some storage vars
	CFItem nextItem = null, currentItem = null;
	GrammarSymbol nextSymbol = null, currentSymbol = null;

	// iterate the symbols in the production in reverse
        for (int position = symbols.length(); position >= 0; --position) {
	    // allocate a new item at this position.  No lookahead is
	    // defined right now.
	    currentItem = grammar.newItem(this, position);
	    // set the nextItem at this item position. This makes
	    // sense due to the fast that we are zero based.
	    currentItem.nextItem = nextItem;
	    // get the grammar symbol as this position in the
	    // production.
	    if (position != symbols.length()) {
//    		System.out.println(this.toString());
//    		System.out.println("itemize(): pos = " + position + 
//    				   ", iter.at(pos) = "+symbols.at(position));

		currentSymbol = grammar.getGrammarSymbol( symbols.at(position) );
		currentItem.nextSymbol = currentSymbol;
	    } else {
		currentSymbol = null;
	    }

	    // and tie the next back to this one
	    if (nextItem != null) {
		nextItem.previousItem = currentItem;
            	nextItem.previousSymbol = currentSymbol;
	    }

	    // for the later calculation of follow sets, we
            // want to compile a list of what items mention
            // what non-terminals.  Thus, we check if this
            // grammar symbol is a non-terminal.
	    if (currentSymbol != null && !currentSymbol.isTerminal()) {
		// it is a non-terminal.  We'll let it know.
		((CFNonTerminal)currentSymbol).addItem(nextItem);
	    }

	    // and get setup for the next iteration
	    nextItem = currentItem;
	}

        // which leaves us with the first item...
        this.initialItem = currentItem;
    }

    public int getID()
    {
	return ID;
    }

    public Production add(GrammarSymbol grammarSymbol)
    {
	//System.out.println("Production "+ID+" adding grammar symbol "+grammarSymbol);
	symbols.add(grammarSymbol);
	return this;
    }

    public IntArray getGrammarSymbols()
    {
	return symbols;
    }

    public Item getInitialItem()
    {
	return initialItem;
    }

    public NonTerminal getNonTerminal()
    {
	return nonTerminal;
    }

    public int length()
    {
	return symbols.length();
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer();
        buf.append('<')
	    .append(ID)
	    .append(": ")
	    .append(nonTerminal)
	    .append(" ->");

	for (int i = 0; i < symbols.length(); i++) {
	    GrammarSymbol s = symbols.src[i];
	    buf.append(" ").append( s );
	    //buf.append(" ").append(s.getID()).append(':').append( s );
	}
	return buf.append(" >").toString();
    }

    private int ID;
    private GrammarSymbolList symbols;
    private NonTerminal nonTerminal;
    private Item initialItem;
    private CFGrammar grammar;

    private static class GrammarSymbolList implements IntArray
    {
	public GrammarSymbolList()
	{
	    this.src = new GrammarSymbol[1];
	    this.count = 0;
	}

	public int at(int index)
	{
	    if (index < 0 || index >= count)
		throw new ArrayIndexOutOfBoundsException
		    ("Request for index " + index + 
		     ", but effective size of array is " + count);

	    if (src[index] == null)
		throw new NullPointerException
		    ("Request for index " + index + 
		     " has a null entry.");

	    return src[index].getID();
	}

	public int length()
	{
	    return count;
	}

	public int[] toArray()
	{
	    int[] dst = new int[count];
	    for (int i = 0; i < count; i++)
		dst[i] = src[i].getID();
	    return dst;
	}
	
	void add(GrammarSymbol s)
	{
	    if (count == src.length)
		enlarge();
	    src[count++] = s;
	}

	void enlarge()
	{
	    GrammarSymbol[] dst = new GrammarSymbol[ src.length * 2 ];
	    System.arraycopy(src, 0, dst, 0, src.length);
	    src = dst;
	}

	public String toString()
	{
	    StringBuffer b = new StringBuffer()
		.append('[');

	    for (int i = 0; i < count; i++) {
		if (i > 0)
		    b.append(',');
		b.append(src[i]);
	    }
	    
	    return b.append(']').toString();
	}

	public Object clone() throws CloneNotSupportedException
	{
	    GrammarSymbolList clone = (GrammarSymbolList)super.clone();
	    clone.src = (GrammarSymbol[])src.clone();
	    return clone;
	}

	GrammarSymbol[] src;
	int count;
    }

}





