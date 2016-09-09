/**
 * $Id: CFGrammar.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Standard <code>ContextFreeGrammar</code> implementation.
 */
public class CFGrammar
    implements ContextFreeGrammar, ContextFreeSet
{
    private static final boolean DEBUG = false;

    // ============================================================
    // constructors
    // ============================================================

    /**
     * Constructs the <code>CFGrammar</code>.
     */
    public CFGrammar()
    {
    	// create some dynamic-sizable grammar obkect storage
    	// containers
    	v_terminals = new Vector();
    	v_nonterminals = new Vector();
    	v_productions = new Vector();
	v_items = new ItemVector(11);
    }

    public ContextFreeGrammar getContextFreeGrammar()
    {
	return this;
    }

    // ============================================================
    // object factory methods
    // ============================================================

    public Terminal newTerminal(Token type)
    {
    	// make sure token is not null
    	if (type == null)
	    throw new NullPointerException
		("newTerminal(Token) requires a non-null argument.");
	
    	CFTerminal terminal = new CFTerminal(type);
    	v_terminals.addElement(terminal);
    	return terminal;
    }

    public NonTerminal newNonTerminal(String name)
    {
    	// make sure nt is not null
    	if (name == null)
	    throw new NullPointerException
		("newNonTerminal(String) requires a non-null String argument.");

    	CFNonTerminal nonTerminal = new CFNonTerminal(this, name);
    	v_nonterminals.addElement(nonTerminal);
    	return nonTerminal;
    }

    public Production newProduction(NonTerminal nonTerminal)
    {
    	// make sure nt is not null
    	if (nonTerminal == null)
	    throw new NullPointerException
		("newProduction(NonTerminal, int) requires a non-null NonTerminal argument.");

    	CFProduction production = new CFProduction(this, v_productions.size(), nonTerminal);

    	// Cast here.  We hope that the user does not mix toolkit
    	// implementations!  May want to promote this method to the
    	// interface or change the algorithms that use it.
    	((CFNonTerminal)nonTerminal).addAlternative(production);
    	v_productions.addElement(production);
    	return production;
    }

    CFItem newItem(Production production, int position)
    {
    	CFItem item = new CFItem(this, production, position);
	item.setID( v_items.add(item) );
	return item;
    }

    CFLR1Item newLR1Item(CFItem core, Terminal lookahead)
    {
    	CFLR1Item item = new CFLR1Item(core, lookahead);
	item.setID( v_items.add(item) );
	return item;
    }

    // ============================================================
    // object quantification methods
    // ============================================================

    public int terminals()
    {
    	return v_terminals.size();
    }

    public int nonTerminals()
    {
    	return v_nonterminals.size();
    }

    public int productions()
    {
    	return v_productions.size();
    }

    public int items()
    {
    	return v_items.size();
    }

    // ============================================================
    // object retrieval methods
    // ============================================================

    public Epsilon getEpsilon()
    {
    	return epsilon;
    }

    public Terminal getTerminal(int ID)
    {
	return (Terminal)grammarSymbols[ID];
    }

    public NonTerminal getNonTerminal(int ID)
    {
	return (NonTerminal)grammarSymbols[ID];
    }

    public GrammarSymbol getGrammarSymbol(int ID)
    {
	return grammarSymbols[ID];
    }

    public Production getProduction(int ID)
    {
	return productions[ID];
    }

    public Item getItem(int ID)
    {
	return v_items.get(ID);
    }

    public NonTerminal getNonTerminal(String name)
    {
    	NonTerminal nonTerminal = null;
    	for (int i=0; i<v_nonterminals.size(); i++) {
	    nonTerminal = (NonTerminal)v_nonterminals.elementAt(i);
	    if (nonTerminal.getName().equals(name))
		return nonTerminal;
    	}
    	return null;
    }

    public Terminal getTerminal(String name)
    {
    	Terminal terminal = null;
    	for (int i=0; i<v_terminals.size(); i++) {
	    terminal = (Terminal)v_terminals.elementAt(i);
	    if (terminal.getName().equals(name))
		return terminal;
    	}
    	return null;
    }

    public java.util.Enumeration getNonTerminals()
    {
    	return v_nonterminals.elements();
    }

    public java.util.Enumeration getTerminals()
    {
    	return v_terminals.elements();
    }

    public java.util.Enumeration getProductions()
    {
    	return v_productions.elements();
    }

    // ============================================================
    // start production methods
    // ============================================================

    public Production getStart()
    {
    	return startProduction;
    }

    public void setStartProduction(Production startProduction)
    {
	// save this.  Will change it later during augment.
	this.startProduction = startProduction;
    }

    // ============================================================
    // compilation routines
    // ============================================================

    public ContextFreeSet compile()
    {
	if (!isCompiled) {

	    // in this method we want to setup all the containers for
	    // Terminals, NonTerminals, Productions, and Items.
	    
	    // augment the grammar with a new start production
	    augment();
	    
	    // calculate the ID's for each of the following
	    // classes of objects
	    compileGrammarSymbols();

	    if (DEBUG) print("compile(): grammar symbols: ", grammarSymbols);
	    
	    compileProductions();

	    if (DEBUG) print("compile(): productions: ", productions);

	    isCompiled = true;
	}
	// done
	return this;
    }

    private void augment()
    {
	// Although not completely particular to augmentation, we take
	// this opportunity to initialize the end of input terminal
	// that is expected to be used in all parsers / DPAs.
	CFTerminal halt = new CFTerminal(Token.STOP, "$");
	halt.setID( 0 );
	v_terminals.addElement(halt);

    	// now on to augmentation.  Reserve the initial start
    	// production handle...
    	Production initial = startProduction;
	// make the new start production on a new start nonterminal
	// (length of 2)
    	startProduction = newProduction( newNonTerminal("_start_") ); 
    	// add the nonterminal from the start production and the end
    	// of input terminal to this new start production
    	startProduction.add(initial.getNonTerminal());
    	startProduction.add(halt);
    }

    /**
     * ----------------------------------------------------------
     * initialization of terminals and nonterminals
     * ----------------------------------------------------------
     *
     * There are several issues here that we need to be mindful of.
     * First is a compile time issue.  We are going to be throwing
     * around sets of terminals and nonterminals together.  Since we
     * typically use bitvectors to do this, we (1) need to represent
     * each nonterminl or terminal as an integer ID and be able to
     * retrieve it later with that ID; (2) ideally have the ID's in a
     * contiguous block such that representations of them in the
     * bitvector take up as least amount of memory as possible.
     *
     * The second issue is concerns run-time, but it relates to the
     * first issue.  This is as follows: When the lexer runs, it
     * generates tokens.  These tokens are then sent to the parser
     * which uses the token ID as the second argument in
     * action[state][tokenID] to get the next action.  One of the
     * consequences of this is that the token ID's need to be used
     * when building the action table since otherwise it simply
     * wouldn't work.  Since in the first step we are concerned with
     * assigning terminal and nonterminal IDs, we can see that the
     * terminal ID's need to correspond to the token ID's of the
     * grammar we are using.  ***Since we make no assumptions about
     * the range of numbers used by the tokens***, we have to reserve
     * the tokens ID's first and then fill in the rest unused ones
     * with nonterminals.
     *
     * For example, say we have a set of terminals T with token IDs
     * {1,2,4}.  We have a set of Nonterminal N {A, B} without
     * assigned ID's.  Our goal is to make an array A of grammar
     * symbols such that each token ID in T gets index ID in A ( T[i]
     * = A[i] ) and that each nonterminal in N gets an ID equal to the
     * lowest unused array element in A.  From this example we would
     * want to have the array sequence {A, 1, 2, B, 4}.
     *
     * In this way no array elements are wasted and the tokens get
     * their stubborn way.  
     */
    private void compileGrammarSymbols()
    {
	// create array with enough elements for the terminals,
	// nonterminals, and epsilon
	grammarSymbols = new GrammarSymbol[ terminals() + nonTerminals() + 1];

	// mow do specific subtasks
	compileTerminals();
	compileNonTerminals();

        // Finally, add EPSILON.  First we make the object
	epsilon = new CFEpsilon(); // -1 is bogus value
        // Then look for where to put it.  Returns -1 if it could not find one
        int index = getMinIndex(grammarSymbols, 0);
        // check if array is full
        if (index == -1) {
	    // make the array a little bigger
	    grammarSymbols = grow(grammarSymbols, 1);
	    // set to this new element
	    index = grammarSymbols.length - 1;
        }
	// set it there
	grammarSymbols[index] = epsilon;
	// give it this ID
	epsilon.setID( index );
    }

    private void compileTerminals()
    {
	// these are all vars initialized outside the loops
	final int len = terminals();
	int index = 0;
	CFTerminal terminal = null;

	// now iterate each token in the grammar and assign it to it's
	// rightful array index
        for (int i = 0; i < len; i++) {
	    // get the terminal from the grammar
	    terminal = (CFTerminal)v_terminals.elementAt(i);
	    // get the tokenID
	    index = terminal.getToken().getID();

            // make sure the array is big enough for it
	    if (index >= grammarSymbols.length)
		grammarSymbols = grow(grammarSymbols, index);
	    // now make sure that the element has not already been assigned
	    else if (grammarSymbols[index] != null)
		throw new RuntimeException
		    ("There is a tokenID conflict between "
		     +grammarSymbols[index]+" and "+terminal);

	    // assign ID and to array
	    terminal.setID(index);
	    grammarSymbols[index] = terminal;
        }
    }

    private void compileNonTerminals()
    {
	final int len = nonTerminals();
	int index = 0;
	CFNonTerminal nonTerminal = null;

	// ok now we can go ahead and add in the nonterminals
        for (int i=0; i<len; i++) {
	    // get each one
	    nonTerminal = (CFNonTerminal)v_nonterminals.elementAt(i);
	    // get the last element
	    index = getMinIndex(grammarSymbols, index);
	    // check the index
	    if (index == -1) {
		// grow it.
		grammarSymbols = grow(grammarSymbols, 1);
            	// finally set it
		index = grammarSymbols.length - 1;
            }
	    nonTerminal.setID( index );
	    grammarSymbols[index] = nonTerminal;
        }
    }

    private void compileProductions()
    {
    	final int len = productions();
    	productions = new Production[len];
    	CFProduction production = null;

	// now iterate each production in the grammar
        for (int i=0; i<len; i++) {
	    // get the terminal from the grammar
	    production = (CFProduction)v_productions.elementAt(i);
	    //production.setID( i );
	    production.itemize();
	    productions[i] = production;
	    // careful here.  The itemize() method will trigger new
	    // items to be made which may eventually put IDs in sets.
	    // Make sure this will not be a problem.
        }
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();

	b.append("Context Free Grammar").append(StringTools.NEWLINE);
	b.append("--------------------").append(StringTools.NEWLINE);

	int len;
	Object o;
	
	b.append("Terminals:").append(StringTools.NEWLINE);
	len = v_terminals.size();
	for (int i=0; i < len; i++) {
	    o = v_terminals.elementAt(i);
	    //if (o == null) continue;
	    b.append('[').append(i).append(']').append(": ")
		.append(o).append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);
	
	b.append("Nonterminals:").append(StringTools.NEWLINE);
	len = v_nonterminals.size();
	for (int i=0; i < len; i++) {
	    o = v_nonterminals.elementAt(i);
	    //if (o == null) continue;
	    b.append('[').append(i).append(']').append(": ")
		.append(o).append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);

	b.append("Productions:").append(StringTools.NEWLINE);
	len = v_productions.size();
	for (int i=0; i < len; i++) {
	    o = v_productions.elementAt(i);
	    //if (o == null) continue;
	    b.append('[').append(i).append(']').append(": ")
		.append(o).append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);	

	GrammarSymbol gs;
	b.append("Grammar Symbols:").append(StringTools.NEWLINE);
	len = grammarSymbols.length;
	for (int i=0; i < len; i++) {
	    gs = grammarSymbols[i];
	    //if (o == null) continue;
	    b.append('[').append(i).append(']').append(": ")
		.append(gs.getName()).append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);	

	b.append("Items:").append(StringTools.NEWLINE);
	len = v_items.size();
	for (int i=0; i < len; i++) {
	    o = v_items.get(i);
	    //if (o == null) continue;
	    try {
		b.append('[').append(i).append(']').append(": ").append(o).append(StringTools.NEWLINE);
	    } catch (ArrayIndexOutOfBoundsException aiiobex) {
		b.append('[').append(i).append(']').append(": ")
		    .append(aiiobex.toString()).append(StringTools.NEWLINE);
		aiiobex.printStackTrace();
	    }
	}
	b.append(StringTools.NEWLINE);	

	return b.toString();
    }

    protected final void print(String name, Object[] a)
    {
	StringBuffer b = new StringBuffer().append(name).append(" { ");
	for (int i=0; i < a.length; i++) {
	    if (i>0) b.append(',');
	    b.append(a[i]);
	}
	b.append(" } ").append(StringTools.NEWLINE);

	if (DEBUG) 
	    log().debug()
		.write(b)
		.out();
    }

    protected final void print(String name, GrammarSymbol[] a)
    {
	StringBuffer b = new StringBuffer().append(name).append('{');
	for (int i=0; i < a.length; i++) {
	    if (i>0) b.append(", ");
	    b.append(a[i].getID()).append(':').append(a[i]);
	}
	b.append('}').append(StringTools.NEWLINE);

	if (DEBUG) 
	    log().debug()
		.write(b)
		.out();
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("cfg", this);
	return log;
    }

    // ============================================================
    // instance vars
    // ============================================================
    protected GrammarSymbol[] grammarSymbols;
    protected Production[] productions;
    protected Production startProduction;
    protected CFEpsilon epsilon;

    protected Vector v_terminals;
    protected Vector v_nonterminals;
    protected Vector v_productions;
    protected ItemVector v_items;

    protected boolean isCompiled;

    private Log log;

    // ============================================================
    // some class utility methods
    // ============================================================
    protected static final GrammarSymbol[] grow(GrammarSymbol[] src, int len)
    {
	GrammarSymbol[] dst = new GrammarSymbol[src.length + len];
	System.arraycopy(src, 0, dst, 0, src.length);
	return dst;
    }

    protected static final int getMinIndex(GrammarSymbol[] a, int offset)
    {
	int i = offset;
	while (i < a.length) {
	    if (a[i] == null)
		return i;
	    else
		i++;
	}

	return -1;
    }

    // ============================================================
    // utility classes
    // ============================================================

    private static final class ItemVector
    {
	ItemVector(int capacity)
	{
	    this.src = new Item[capacity];
	    this.maxsize = capacity - 1;
	    this.size = 0;
	}

	int add(Item item)
	{
	    // check if we need to realloc our storage
	    checkCapacity();

	    // put this in the next slot
	    src[size++] = item;

	    //System.out.println("alloc'd item " + (size - 1));
	    return size - 1;
	}

	Item get(int ID)
	{
	    return src[ID];
	}

	int size()
	{
	    return size;
	}

	private void checkCapacity()
	{
	    if (size == maxsize) {
		Item[] dst = new Item[src.length * 2];
		System.arraycopy(src, 0, dst, 0, src.length);
		src = dst;
		maxsize = dst.length - 1;
	    }
	}

	private Item[] src;
	private int maxsize;
	private int size;
    }
}






