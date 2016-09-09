/**
 * $Id: StandardLRTranslatorGrammar.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator.lr;

import java.io.*;
import java.util.*;

import org.inxar.syntacs.analyzer.*;
import com.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.analyzer.lexical.*;
import com.inxar.syntacs.analyzer.lexical.*;
import org.inxar.syntacs.analyzer.syntactic.*;
import com.inxar.syntacs.analyzer.syntactic.*;
import org.inxar.syntacs.grammar.*;
import org.inxar.syntacs.grammar.regular.*;
import com.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.grammar.context_free.*;
import com.inxar.syntacs.grammar.context_free.*;
import org.inxar.syntacs.automaton.finite.*;
import com.inxar.syntacs.automaton.finite.*;
import org.inxar.syntacs.automaton.pushdown.*;
import com.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.translator.*;
import com.inxar.syntacs.translator.*;
import org.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.translator.regexp.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * This is a "mutable" implementation of
 * <code>LRTranslatorGrammar</code> used by both the
 * <code>SyntacsTranslator</code> and the
 * <code>XML2LRTranslatorGrammarTransformer</code> when a grammar is
 * parsed into memory.  It does all the dirty work of semantic checks
 * after the parse has been completed, and is pretty much a central
 * part of the whole enchilada.  
 */
public class StandardLRTranslatorGrammar
    implements LRTranslatorGrammar
{
    private static final String DEFAULT = "default";
    private static final String ALL = "all";

    private static final boolean DEBUG = false;
    private static boolean verbose = 
	Mission.control().isTrue("verbose");


    /*
                    ____________________________________________
                   /                                           /
                  /                                           / \
                 /                                           /   \___
                /                                           /     \ /
               /                                           /      //
              /                                           /      //
             /                                           /      //
            /                                           /      //
           /                                           /      //
          /                                           /      // 
         /                                           /      //
        /                                           /      //
       /                                           /      // 
      /                                           /      //
     /___________________________________________/      //
     |                                           |\    //
      \   Book I: Constructors               pcj  \\  //
       \___________________________________________\\_/
    */

    /**
     * Constructs a new empty grammar.
     */
   public StandardLRTranslatorGrammar()
    {
	this.contexts = new SymbolList(UNDEFINED_CONTEXT);
	this.symbols = new SymbolList(UNDEFINED_SYMBOL);
	this.terminals = new ArrayIntList();
	this.nonterminals = new ArrayIntList();
	this.productions = new SymbolList(UNDEFINED_PRODUCTION);
	this.properties = new Properties();
	this.startContext = UNDEFINED;
	this.goal = UNDEFINED;

	// Need to reserve context position zero as default context.
	contexts.add( this.new ContextSymbol(contexts.length(), DEFAULT) );
    }

    /*
                    /-------------------------------------------
                   /                                           /
                  /                                           / \
                 /                                           /   \___
                /                                           /     \ /
               /                                           /      //
              /                                           /      //
             /                                           /      //
            /                                           /      //
           /                                           /      //
          /                                           /      // 
         /                                           /      //
        /                                           /      //
       /                                           /      // 
      /                                           /      //
     /___________________________________________/      //
     |                                           |\    //
      \   Book II: Interface Methods         pcj  \\  //
       \___________________________________________\\_/
    */

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book II, Chapter 1     ||      Grammar Methods        |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
    */

    public String getName()
    {
	return this.grammarName;
    }

    public String getVersion()
    {
	return this.version;
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book II, Chapter 1b    ||    Translator Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
    */

    public Translator newTranslator()
    {
	return newTranslator(this.properties);
    }

    public Translator newTranslator(Properties p)
    {
	if (translator == null) {

	    // If there are errors, we bail at this point.
	    errcheck();

	    if (DEBUG) 
		log().debug()
		    .write("Compiling translator...")
		    .out();

	    Iterator keys = p.keySet().iterator();
	    while (keys.hasNext()) {
		String key = (String)keys.next();
		Mission.control().put(key, p.get(key));
	    }
	    
	    // First thing is to make sure we are compiled.
	    compile(p);

	    if (DEBUG) 
		log().debug()
		    .write("Done compiling lexer.")
		    .out();

	    // If there are errors, we bail again.
	    errcheck();
	}

	return translator;
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book II, Chapter 2     ||       Context Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    public IntArray getContexts()
    {
	return contexts;
    }

    public String getContext(int ID)
    {
	return contexts.getName(ID);
    }

    public int getStartContext()
    {
	return startContext;
    }

    public int getContextAction(int contextID, int symbolID)
    {
	ContextSymbol s = (ContextSymbol)contexts.getSymbol(contextID);
	if (s == null) 
	    return UNDEFINED_CONTEXT;

	if (!s.hasAction(symbolID)) 
	    return UNDEFINED_SYMBOL;

	int action = s.getAction(symbolID);

	return s.getAction(symbolID);
    }

    public int getContextRegister(int contextID, int symbolID)
    {
	ContextSymbol s = (ContextSymbol)contexts.getSymbol(contextID);
	if (s == null) 
	    return UNDEFINED_CONTEXT;

	if (!s.hasAction(symbolID))
	    return UNDEFINED_SYMBOL;

	return s.getRegister(symbolID);
    }

    public IntArray getContextTerminals(int contextID)
    {
	ContextSymbol s = (ContextSymbol)contexts.getSymbol(contextID);
	return s != null ? s.symbols : null;
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book II, Chapter 3     ||      Terminal Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    public IntArray getTerminals()
    {
	return terminals;
    }

    public String getTerminal(int ID)
    {
	return symbols.getName(ID);
    }

    public Object getTerminalRegexp(int ID)
    {
	Object o = (Symbol)symbols.getSymbol(ID);
	if (o instanceof TerminalSymbol) {
	    return ((TerminalSymbol)o).regexp;
	} else 
	    return null;
    }

    public IntArray getTerminalContexts(int ID)
    {
	Object o = (Symbol)symbols.getSymbol(ID);
	if (o instanceof TerminalSymbol) {
	    TerminalSymbol s = (TerminalSymbol)o;
	    return s != null ? s.getContexts() : null;
	} else 
	    return null;
    }

    public boolean isTerminal(int ID)
    {
	Object o = (Symbol)symbols.getSymbol(ID);
	if (o != null) {
	    return o instanceof TerminalSymbol;
	} else 
	    throw new IllegalArgumentException("Undefined Terminal");
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book II, Chapter 4     ||     NonTerminal Methods     |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    public IntArray getNonTerminals()
    {
	return nonterminals;
    }

    public String getNonTerminal(int ID)
    {
	return symbols.getName(ID);
    }

    public int getGoalNonTerminal()
    {
	return goal;
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book II, Chapter 5     ||     Production Methods      |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    public IntArray getProductions()
    {
	return productions;
    }

    public String getProduction(int ID)
    {
	return productions.getName(ID);
    }

    public int getProductionNonTerminal(int ID)
    {
	ProductionSymbol s = (ProductionSymbol)productions.getSymbol(ID);
	return s != null ? s.lhs : UNDEFINED_PRODUCTION;
    }

    public IntArray getProductionSymbols(int ID)
    {
	ProductionSymbol s = (ProductionSymbol)productions.getSymbol(ID);
	return s != null ? s.rhs : null;
    }

    public int getProductionLength(int ID)
    {
	ProductionSymbol s = (ProductionSymbol)productions.getSymbol(ID);
	return s != null ? s.rhs.length() : UNDEFINED_PRODUCTION;
    }

    /*
                    /-------------------------------------------
                   /                                           /
                  /                                           / \
                 /                                           /   \___
                /                                           /     \ /
               /                                           /      //
              /                                           /      //
             /                                           /      //
            /                                           /      //
           /                                           /      //
          /                                           /      // 
         /                                           /      //
        /                                           /      //
       /                                           /      // 
      /                                           /      //
     /___________________________________________/      //
     |                                           |\    //
      \   Book III: Non-Interface Methods    pcj  \\  //
       \___________________________________________\\_/
    */

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |\
    +--+-----------------------------++-----------------------------+--+||
    |  | 41                          ||                          42 |  |\|
    |  |      Book III, Chapter 1    ||       Grammar Methods       |  |||
    |  |                             ||                             |  |||
    |  |                             ||                             |  |||
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~~~ 
     */

    /**
     * Sets the name of the grammar to the given <code>String</code>.
     */
    public void setName(String grammarName)
    {
	this.grammarName = grammarName;
	setChanged();
    }
    
    /**
     * Sets the version of the grammar to the given <code>String</code>.
     */
    public void setVersion(String version)
    {
	this.version = version;
	setChanged();
    }
    
    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 2    ||       Context Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    /**
     * Returns <code>true</code> if there is an existing context with
     * the given name, <code>false</code> otherwise.  
     */
    public boolean hasContext(String name)
    {
	return contexts.contains(name);
    }

    /**
     * Returns the ID of the context with the given name or
     * <code>UNDEFINED_CONTEXT</code> if no such context exists.  
     */
    public int getContext(String name)
    {
	return contexts.getID(name);
    }

    /**
     * Adds a context having the given name to the list of contexts.
     * If a context already exists with this name, a semantic error
     * will be added to the list of errors, but no exception will be
     * thrown.
     */
    public void addContext(String name)
    {
	if (ALL.equals(name)) {

	    if (all != null)
		addSemanticError
		    ("Context `" + name + "' was declared twice (duplicate declaration).");
	    else {
		all = this.new ContextSymbol(UNDEFINED, ALL);
		setChanged();
	    }
	} else if (DEFAULT.equals(name)) {

	    if (isDefaultExplicit)
		addSemanticError
		    ("Context `" + name + "' was declared twice (duplicate declaration).");
	    else {
		isDefaultExplicit = true;
		setChanged();
	    }

	} else if (hasContext(name)) {

	    addSemanticError
		("Context `" + name + "' was declared twice (duplicate declaration).");
	    
	} else {

	    contexts.add( this.new ContextSymbol(contexts.length(), name) );
	    setChanged();
	}
    }

    /**
     * Returns <code>true</code> if the start context has already been
     * set, <code>false</code> otherwise.  
     */
    public boolean hasStartContext()
    {
	return startContext != UNDEFINED;
    }

    /**
     * Sets the start context to the context with the given name.  If
     * no such context exists an error will be added to the list of
     * errors, but no exception will be thrown.  If the start context
     * has already been set an error will be noted also.
     */
    public void setStartContext(String name)
    {
	if (ALL.equals(name)) {
	    addSemanticError
		 ("`all' is a virtual context -- it cannot be the start context.");
	} else if (hasStartContext()) {
	    addSemanticError
		("Can't set the start context to `" + name +
		 "' since it was previously defined as `" + getContext(getStartContext()) + 
		 "' (duplicate definition)");
	} else {

	    int ID = getContext(name);
	    if (ID == UNDEFINED_CONTEXT)
		addSemanticError
		    ("Can't set start context to undefined context `" + name + "'");
	    
	    else {
		this.startContext = ID;
		setChanged();
	    }
	}
    }

    /**
     * Sets the lexical context action to PUSH for the given context
     * and symbol.  If either the context or symbol do not exist, an
     * error will be noted.  If the action for this tuple has already
     * been set, an error will be noted.  An exception will not be
     * thrown.
     */
    public void setContextPush(String contextName, String symbolName, String contextPushed)
    {
	ContextSymbol s = getContextSymbol(contextName, symbolName);
	if (s == null)
	    return;
		
	if (ALL.equals(contextPushed)) {
	    addSemanticError
		("Illegal context stack instruction for context `" + contextName + 
		  "', symbol `" + symbolName + 
		  "':  Destination context cannot be `all'");
	    return;
	} else if (contextName.equals(contextPushed)) {
	    addSemanticError
		("Illegal context stack instruction for context `" + contextName + 
		 "', symbol `" + symbolName + 
		 "':  Source and destination contexts must be disjoint.");
	    return;
	}

	int pushedID = getContext(contextPushed);
	if (pushedID == UNDEFINED_CONTEXT) 
	    
	    addSemanticError
		("Illegal context stack instruction for context `" + contextName + 
		 "', symbol `" + symbolName + 
		 "':  Destination context `"+contextPushed+"' is undefined.");

	else {
	    s.setPush(getSymbol(symbolName), pushedID);
	    setChanged();
	}
    }

    /**
     * Sets the lexical context action to PEEK for the given context
     * and symbol.  If either the context or symbol do not exist, an
     * error will be noted.  If the action for this tuple has already
     * been set, an error will be noted.  An exception will not be
     * thrown.
     */
    public void setContextPeek(String contextName, String symbolName)
    {
	ContextSymbol s = getContextSymbol(contextName, symbolName);
	if (s == null)
	    return;
	
	s.setPeek(getSymbol(symbolName));
	setChanged();
    }

    /**
     * Sets the lexical context action to POP for the given context
     * and symbol.  If either the context or symbol do not exist, an
     * error will be noted.  If the action for this tuple has already
     * been set, an error will be noted.  An exception will not be
     * thrown.  
     */
    public void setContextPop(String contextName, String symbolName)
    {
	ContextSymbol s = getContextSymbol(contextName, symbolName);
	if (s == null)
	    return;
		
	s.setPop(getSymbol(symbolName));
	setChanged();
    }

    private ContextSymbol getContextSymbol(String contextName, String symbolName)
    {
	ContextSymbol s = null;

	// PART 1: Check the symbol name.
	int symbolID = getSymbol(symbolName);
	if (symbolID == UNDEFINED_SYMBOL) {
	    
	    addSemanticError
		("Illegal context stack instruction for context `" + contextName + 
		 "', symbol `" + symbolName + 
		 "':  Symbol `"+symbolName+"' is undefined.");

	    return null;
	}

	// PART 2: Fetch the context.
	if (ALL.equals(contextName)) {
	    s = all;
	} else {
	    int contextID = getContext(contextName);
	    if (contextID == UNDEFINED_CONTEXT) {
		
		addSemanticError
		    ("Illegal context stack instruction for context `" + contextName + 
		     "', symbol `" + symbolName + 
		     "':  Context `"+contextName+"' is undefined.");

		return null;
	    } 

	    addTerminalContext(symbolID, contextID);

	    s = (ContextSymbol)contexts.getSymbol(contextID);

	} 

	// PART 3: Check if an action has already been defined for
	// this symbol.
	if (s.hasAction(symbolID)) {

	    addSemanticError
		("Illegal context stack instruction for context `" + contextName + 
		 "', symbol `" + symbolName + 
		 "':  Duplicate definition (already "+getActionName(s, symbolID)+")");
	    
	    return null;
	}

	// All OK.
	return s;
    }

    private String getActionName(ContextSymbol s, int symbolID)
    {
	int action = s.getAction(symbolID);

	switch (action) {
	case ACTION_PEEK: return "PEEK";
	case ACTION_POP:  return "POP";
	case ACTION_PUSH: return "PUSH " + getContext(s.getRegister(symbolID));
	default:
	    throw new InternalError();
	}
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 3    ||      Terminal Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    /**
     * Returns <code>true</code> if there is a terminal with the given
     * name, <code>falsew</code> otherwise.  
     */
    public boolean hasTerminal(String name)
    {
	Symbol s = symbols.getSymbol(name);
	return s instanceof TerminalSymbol;
    }

    /**
     * Returns <code>true</code> if there is a terminal with the given
     * ID, <code>falsew</code> otherwise.  
     */
    public boolean hasTerminal(int ID)
    {
	Symbol s = symbols.getSymbol(ID);
	return s instanceof TerminalSymbol;
    }

    /**
     * Returns the ID of the terminal having the given name or
     * <code>UNDEFINED_TERMINAL</code> if no such terminal exists.  
     */
    public int getTerminal(String name)
    {
	return symbols.getID(name);
    }

    /**
     * Adds a new terminal to the list of terminal having the given
     * name.  If a terminal with that name already exists, an error
     * will be noted.
     */
    public void addTerminal(String name)
    {
	if (hasTerminal(name))
	    addSemanticError
		("Duplicate definition of terminal `" + name + "'.");
	else {

	    // Here is the code that sets the terminal ID.  It must
	    // never be zero, since that value is reserved for
	    // Token.STOP.  The way we handle this is to always add
	    // one.
	    int ID = symbols.length() + 1;
	    Symbol symbol = new TerminalSymbol(ID, name);
	    terminals.add(ID);
	    symbols.add(symbol);
	    setChanged();
	}
    }
    
    /**
     * Sets the regular expression for the terminal having the given
     * name.  If a terminal with that name does not exist OR the
     * reglar expression for that terminal has already been set, an
     * error will be noted.
     */
    public void setTerminalRegexp(String name, Object regexp)
    {
	if (!hasTerminal(name)) {

	    addSemanticError
		("Illegal match definition for `"+name+"' ("+regexp+"): terminal `"+name+
		 "' is undefined.");

	} else {

	    TerminalSymbol s = (TerminalSymbol)symbols.getSymbol(name);
	    if (s.regexp != null)
		addSemanticError
		    ("Duplicate match definition for terminal `"+name+"'.");
	    else {
		s.regexp = regexp;
		setChanged();
	    }
	}
    }

    private void addTerminalContext(int terminalID, int contextID)
    {
	TerminalSymbol s = (TerminalSymbol)symbols.getSymbol(terminalID);
	if (s == null) {
	    throw new InternalError("Null terminal symbol for "+getTerminal(terminalID));
	} else {
	    if (s.hasContext(contextID)) {
		throw new InternalError
		    ("Terminal "+getTerminal(terminalID) + 
		     " is already a member of context " + getContext(contextID));
	    } else {
		s.addContext(contextID);
		setChanged();
	    }
	}
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 4    ||     NonTerminal Methods     |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    /**
     * Returns <code>true</code> if a nonterminal having the given
     * name exists in this grammar, <code>false</code> otherwise.  
     */
    public boolean hasNonTerminal(String name)
    {
	Symbol s = symbols.getSymbol(name);
	return s instanceof NonTerminalSymbol;
    }

    /**
     * Returns <code>true</code> if a nonterminal having the given
     * ID exists in this grammar, <code>false</code> otherwise.  
     */
    public boolean hasNonTerminal(int ID)
    {
	Symbol s = symbols.getSymbol(ID);
	return s instanceof NonTerminalSymbol;
    }

    /**
     * Returns the ID of the nonterminal having the given name or
     * <code>UNDEFINED_NONTERMINAL</code> if no such nonterminal
     * exists.
     */
    public int getNonTerminal(String name)
    {
	return symbols.getID(name);
    }

    /**
     * Adds a new nonterminal to the list of nonterminal having the
     * given name.  If a nonterminal with that name already exists, an
     * error will be noted. 
     */
    public void addNonTerminal(String name)
    {
	if (hasNonTerminal(name)) 
	    addSemanticError
		("Duplicate definition of nonterminal `" + name + "'.");
	else {
	    int ID = symbols.length() + 1;
	    Symbol symbol = new NonTerminalSymbol(ID, name);
	    nonterminals.add(ID);
	    symbols.add(symbol);
	    setChanged();
	}
    }

    /**
     * Returns <code>true</code> if a goal symbol has been set,
     * <code>false</code> otherwise (the goal symbol has not been
     * set).  
     */
    public boolean hasGoalNonTerminal()
    {
	return goal != UNDEFINED;
    }

    /**
     * Sets the goal symbol to the nonterminal with the given name.
     * If no such nonterminal exists, an error will be noted.
     */
    public void setGoalNonTerminal(String name)
    {
	if (hasGoalNonTerminal()) {
	    addSemanticError
		("Duplicate definition of goal symbol: `"+
		 getNonTerminal(getGoalNonTerminal())+
		 "' or `"+name+"'?");
	} else {

	    int ID = getNonTerminal(name);
	    if (ID == UNDEFINED_SYMBOL || ID == UNDEFINED_NONTERMINAL) 
		addSemanticError
		    ("Illegal goal symbol definition: undefined nonterminal `"+name+ "'");
	    else {
		this.goal = ID;
		setChanged();
	    }
	}
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 5    ||     Production Methods      |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */


    /**
     * Returns true if a production having the given name exists,
     * false otherwise.  The format of the string should be
     * "<code>nonterminal-name: symbol symbol symbol</code>".  This is
     * a simple string match, so caveat emptor.
     */
    public boolean hasProduction(String name)
    {
	return productions.contains(name);
    }

    /**
     * Returns the production having the given name or
     * <code>UNDEFINED_PRODUCTION</code> is no such production exists.
     */
    public int getProduction(String name)
    {
	return productions.getID(name);
    }

    /**
     * Adds a new production to the grammar having the given
     * nonterminal as the right-hand-side and the given
     * <code>List</code> of symbol names as the left-hand-side.  Each
     * member in the <code>List</code> should be a
     * <code>String</code>.  If the given nonterminal or any of the
     * symbols are unknown, an error will be noted.
     */
    public void addProduction(String nonterminalName, List symbolNames)
    {
	if (DEBUG) 
	    log().debug()
		.write("adding production having LHS `")
		.write(nonterminalName)
		.write("' and RHS: ")
		.write(symbolNames)
		.out();

	IntList syms = new ArrayIntList();
	IntList undefs = new ArrayIntList();

	StringBuffer b = new StringBuffer(nonterminalName).append(':');
	for (int i = 0; i < symbolNames.size(); i++) {
	    String name = (String)symbolNames.get(i);
	    int ID = getSymbol(name);
	    if (ID == UNDEFINED_SYMBOL)
		undefs.add(i);
	    else {
		if (DEBUG) 
		    log().debug()
			.write("-- `")
			.write(name)
			.write("' [")
			.write(ID)
			.write("] added to RHS")
			.out();

		syms.add(ID);
	    }
	    b.append(' ').append(name);
	}

	String name = b.toString();

	if (undefs.length() != 0) {
	    b = new StringBuffer("There was a problem while trying to add a ")
		.append("production for the rule `")
		.append(name)
		.append("' .  The problem is that ");
	    if (undefs.length() == 1) {
		b.append("the symbol `")
		    .append( symbolNames.get(undefs.at(0)) )
		    .append("' is undefined.)");
	    } else {
		b.append("there were several symbols in the right-hand-side of ")
		    .append("this production that are undefined: ");
		for (int i = 0; i < undefs.length(); i++) {
		    if (i > 0)
			b.append(',');
		    b.append( symbolNames.get(undefs.at(i)) );
		}
		b.append('.');   
	    }
	    addSemanticError(b.toString());

	} else {

	    if (hasProduction(name)) {
		addSemanticError
		    ("Duplicate declaration of production `"+name+"'.");
	    } else {
	    
		int nonterminalID = getNonTerminal(nonterminalName);
		if (nonterminalID == UNDEFINED_NONTERMINAL || 
		    nonterminalID == UNDEFINED_SYMBOL)
		    addSemanticError
			("Illegal production definition: nonterminal `"+nonterminalName
			 +"' is undefined.");
		else {
		    productions.add
			( new ProductionSymbol(productions.length(), 
					       name, 
					       nonterminalID, 
					       syms) );
		    setChanged();
		}
	    }
	}
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 6    ||        Misc Methods         |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    /**
     * Returns the ID of the given terminal or nonterminal (symbols)
     * or <code>UNDEFINED_SYMBOL</code> if no such name exists in the
     * list of grammar symbols.
     */
    public int getSymbol(String name)
    {
	return symbols.getID(name);
    }


    /**
     * Returns the name of the symbol having the given ID or
     * <code>null</code> if no such ID exists.  
     */
    public String getSymbol(int ID)
    {
	return symbols.getName(ID);
    }

    /**
     * Returns the value of the given property key or
     * <code>null</code> of no such property exists.  
     */
    public String getProperty(String name)
    {
	return properties.getProperty(name);
    }

    /**
     * Sets the property with the given name to the given value.
     */
    public void setProperty(String name, String value)
    {
	properties.setProperty(name, value);
	Mission.control().put(name, value);
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 7    ||       Utility Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    private boolean hasChanged()
    {
	return hasChanged;
    }

    private void setChanged()
    {
	this.hasChanged = true;
    }

    private void clearChanged()
    {
	this.hasChanged = false;
    }

    /**
     * Once all the grammar declarations and definitions have been
     * done, <code>compile()</code> will go through everything and do
     * all the semantic checks.
     */
    public void compile(Properties p)
    {
	if (hasChanged()) {

	    if (verbose) log().info().write("Checking grammar...").time();
	    compileContexts();
	    
	    compileRegexps(); 
	    compileTerminals(); 
	    compileNonTerminals(); 
	    compileProductions(); errcheck();

	    compileInput(p); errcheck();
	    compileNames(p); errcheck();

	    compileRegularSets(p); errcheck();
	    compileDFAs(p); errcheck();
	    compileLexer(p); errcheck();
	    compileContextFreeSet(p); errcheck();
	    compileLRTranslatorGrammar(p); errcheck();
	    compileDPA(p); errcheck();
	    compileParser(p); errcheck();
	    compileInterpreter(p); errcheck();
	    compileLRTranslator(p); errcheck();

	    burn(p);
	    viz(p);

	    clearChanged();
	}
    }

    private void compileContexts()
    {
	if (terminals.length() == 0)
	    return;

	if (contexts.length() > 1) {
	    
	    // If the "all" context was defined, we need to distribute
	    // each of the terminals in it to all the other contexts.
	    if (all != null) {
		// Get a list of the terminals in the all context.
		IntArray t = all.symbols;
		if (t.length() == 0) {
		    addSemanticWarning
			("Vacuous definition of context `all' "+
			 "(should contain at least one terminal).");
		} else {

		    // Now get a list of all the other contexts.
		    IntArray c = getContexts();

		    // Iterate all the terminals in the outer loop,
		    // then all the contexts in the inner loop.  This
		    // will copy each terminal definition in the all
		    // context to each other context.
		    for (int i = 0; i < t.length(); i++) {
			int tID = t.at(i);
			String tName = getTerminal(tID);

			for (int j = 0; j < c.length(); j++) {
			    int cID = c.at(j);
			    String cName = getContext(cID);

			    int action = all.getAction(tID);

			    switch (action) {
			    case ACTION_PEEK:
				setContextPeek(cName, tName);
				break;
			    case ACTION_POP:
				setContextPop(cName, tName);
				break;
			    case ACTION_PUSH:
				String pName = getContext(all.getRegister(tID));
				setContextPush(cName, tName, pName);
				break;
			    case UNDEFINED_CONTEXT:
				throw new InternalError("Undefined context: "+cName);
			    case UNDEFINED_SYMBOL:
				throw new InternalError("Undefined symbol: "+tName);
			    default:
				throw new InternalError("Unknown Action Type: "+action);
			    }
			}
		    }
		}
	    }

	    // If the default context was explicit, then we are done,
	    // since there will be no implicit distribution of unnamed
	    // terminals into the default context.  If the default
	    // context was Implicit though, then we need to identify
	    // all terminals which do not currently belong to any
	    // context and add them to the default.
	    if (!isDefaultExplicit) {
		// Get the list of terminals
		IntArray t = getTerminals();
		IntList empties = new ArrayIntList(t.length());
		
		// Iterate it, looking for empty context lists.
		for (int i = 0; i < t.length(); i++) {
		    int tID = t.at(i);
		    IntArray c = getTerminalContexts(tID);
		    if (c.length() == 0) 
			empties.add(tID);
		}

		// Now add all these to default.
		for (int i = 0; i < empties.length(); i++) 
		    setContextPeek(DEFAULT, getTerminal(empties.at(i)));
	    }
	    
	} else {

	    if (all != null)
		addSemanticWarning
		    ("The only context that was named was the special `all' context. " + 
		     "In this circumstance it is unnecessary to define `all' since all " + 
		     "terminals will be placed in the default context automatically. " + 
		     "I will ignore all definitions within the `all' context.");

	    // There is only one defined context. There is a chance
	    // that the default context was explicit in which case we
	    // should do nothing.  If not, add all the terminals to
	    // the default context.
	    if (!isDefaultExplicit) {
		IntArray a = getTerminals();
		for (int i = 0; i < a.length(); i++)
		    setContextPeek(DEFAULT, getTerminal(a.at(i)));
	    }
	}

	// Now Check all the contexts to screen for ones that have no
	// terminals.
	IntArray c = getContexts();
	for (int i = 0; i < c.length(); i++) {
	    int cID = c.at(i);
	    IntArray t = getContextTerminals(cID);
	    if (t.length() == 0) {
		String cName = getContext(cID);
		addSemanticError
		    ("Context `" + cName + 
		     "' has no terminals.  A context must not be empty.");
	    }
	}

	// Check that all terminals have defined context actions.
	for (int i = 0; i < c.length(); i++) {
	    int cID = c.at(i);
	    ContextSymbol s = (ContextSymbol)contexts.getSymbol(cID);

	    IntArray t = getContextTerminals(cID);
	    for (int j = 0; j < t.length(); j++) {
		int tID = t.at(j);
		int action = getContextAction(cID, tID);
		switch (action) {

		case ACTION_PEEK: 
		case ACTION_POP:  
		    continue;

		case ACTION_PUSH: 
		    int toID = getContextRegister(cID, tID);
		    String name = getContext(toID);
		    if (name == null)
			addSemanticError
			    ("In context `" + getContext(cID) + 
			     "' the action for terminal `" + getTerminal(tID) + 
			     "' is PUSH " + toID + 
			     ", but no context having that ID is known.");
		    continue;

		case UNDEFINED_CONTEXT:
		    addSemanticError
			("In context `" + getContext(cID) + 
			 "' the action for terminal `" + getTerminal(tID) + 
			 "' is UNDEFINED_CONTEXT! Now how can that be?");
		    continue;

		case UNDEFINED_SYMBOL:
		    addSemanticError
			("In context `" + getContext(cID) + 
			 "' the action for terminal `" + getTerminal(tID) + 
			 "' is UNDEFINED_SYMBOL! Now how can that be?");
		    continue;

		default:
		    addSemanticError
			("In context `" + getContext(cID) + 
			 "' the action for terminal `" + getTerminal(tID) + 
			 "' is unknown ("+action+")! Now how can that be?");

		}
	    }
	}

	if (!hasStartContext())
	    setStartContext(DEFAULT);
    }

    private void compileTerminals()
    {
	if (terminals.length() == 0)
	    addSemanticError("There were no defined terminals. At least one is required.");

	// Get the list of terminals and iterate it, looking for empty
	// context lists.

//  	IntArray t = getTerminals();
//  	for (int i = 0; i < t.length(); i++) {
//  	    if (getTerminalContexts(t.at(i)).length() == 0) 
//  		addSemanticWarning
//  		    ("Terminal `" + getTerminal(t.at(i)) + 
//  		     "' is not a member of any context.");
//  	}
    }

    private void compileRegexps()
    {
	IntArray c = getContexts();
	for (int i = 0; i < c.length(); i++) {
	    int cID = c.at(i);
	    IntArray t = getContextTerminals(cID);
	    for (int j = 0; j < t.length(); j++) {
		int tID = t.at(j);
		// ignore stop token placeholder.
		if (tID == 0)
		    continue;
		Object regexp = getTerminalRegexp(tID);
		if (regexp == null)
		    addSemanticError
			("The terminal `"+getTerminal(tID)+
			 "' was included in context `"+getContext(cID)+
			 "', but it has no regular definition.  Either remove the teminal "+
			 "name from the context inclusion list or "+
			 "define a regular expression for it.");
	    }
	}
    }

    private void compileNonTerminals()
    {
	if (nonterminals.length() == 0)
	    addSemanticError
		("There were no defined nonterminals.  At least one is required.");
    }

    private void compileProductions()
    {
	if (productions.isEmpty())
	    addSemanticError
		("There were no defined productions.  At least one is required.");
	if (!hasGoalNonTerminal())
	    addSemanticError
		("A goal symbol (nonterminal) was not defined, but one needs to be.");
    }

    private void compileLRTranslator(Properties p)
    {
	translator = (LRTranslator)
	    reflect("compile-translator-classname", 
		    "com.inxar.syntacs.translator.lr.StandardLRTranslator");

	translator.setProperties(p);
	translator.setLRTranslatorGrammar
	    ( (LRTranslatorGrammar)Mission.control().get("_lr-translator-grammar") );
	translator.setLRTranslatorInterpreter
	    ( (LRTranslatorInterpreter)Mission.control().get("_lr-translator-interpreter") );
	translator.setInput( (Input)Mission.control().get("_input") );
	translator.setLexer( (Lexer)Mission.control().get("_lexer") );
	translator.setParser( (Parser)Mission.control().get("_parser") );

	Mission.control().put("_translator", translator);
    }

    private void compileNames(Properties p)
    {
	int len = getContexts().length();
	String name = StringTools.capitalize(getName());
	String[] names = new String[len];

	for (int i = 0; i < len; i++) 
	    names[i] = name + StringTools.capitalize(getContext(i)) + "DFA";

	Mission.control().put("_dfa-names", names);
	Mission.control().put("_name", name);
	Mission.control().put("_grammar-name", name + "Grammar");
	Mission.control().put("_dpa-name", name + "DPA");
	
    }
    
    private void compileRegularSets(Properties p)
    {
	String[] names = null;
	if (verbose)
	    names = (String[])Mission.control().get("_dfa-names");

	IntArray c = getContexts();
	int len = c.length(); 
	RegularSet[] regularSets = new RegularSet[len];

	for (int i = 0; i < len; i++) {

	    if (verbose)
		log().info()
		    .write("Building regular grammar for ").write(names[i])
		    .time();

	    ContextSymbol context = (ContextSymbol)contexts.getSymbol(c.at(i));   
	    RegularGrammar g = new REGrammar();
	    IntArray t = context.symbols;

	    for (int j = 0; j < t.length(); j++) {

		int tID = t.at(j);
		String name = getTerminal(tID);
		Object re = getTerminalRegexp(tID);
		
		if (tID == 0)
		    continue;
		
		try {
		    if (re instanceof Regexp) {
			g.newToken(tID, name, ((Regexp)re).toRegularExpression(g));
		    } else if (re instanceof String) {
			g.newToken(tID, name, (String)re);
		    } else if (re == null) {
			// Do nothing, because we assume the semantic
			// error has already detected this and notified
			// the auditor.
		    } else {
			throw new IllegalArgumentException
			    ("Whilst trying to compile a regexp, got unfamiliar instance "+re);
		    }
		} catch (Exception ex) {
		    auditor.notify(Complaint.UNSPECIFIED_ERROR, 
				   "Subsystem translation of regexp for `"+name+"' failed.");
		    if (ex instanceof RuntimeTranslationException) {
			RuntimeTranslationException tex = (RuntimeTranslationException)ex;
			Auditor a = tex.getAuditor();
			if (a.hasErrors()) {
			    List errors = a.getErrors();
			    Iterator iter = errors.iterator();
			    while (iter.hasNext()) {
				auditor.notify((Complaint)iter.next());
			    }
			}
		    }
		}

		errcheck();
	    }
	    
	    regularSets[i] = g.compile();

	    if (verbose)
		log().info().touch();
	}
	
	Mission.control().put("_regular-sets", regularSets);
    }
    
    private void compileDFAs(Properties p)
    {
	String[] names = null;
	if (verbose)
	    names = (String[])Mission.control().get("_dfa-names");

	RegularSet[] regularSets = (RegularSet[])Mission.control().get("_regular-sets");
	int len = regularSets.length;

	TreeDFA[] t_dfas = new TreeDFA[len];
	MesoArrayDFA[] m_dfas = new MesoArrayDFA[len];

	for (int i = 0; i < len; i++) {
	    if (verbose)
		log().info()
		    .write("Building ").write(names[i])
		    .time();

	    t_dfas[i] = (TreeDFA)
		new TreeDFAConstructor().construct(regularSets[i]);

	    if (Mission.control().isTrue("compile-grammar-regular-debug"))
		log().debug().write(regularSets[i]).out();

	    if (verbose)
		log().info()
		    .write("Compressing ").write(names[i])
		    .time();

	    if (Mission.control().isTrue("compile-dfa-debug"))
		log().debug().write(t_dfas[i]).out();

	    m_dfas[i] = (MesoArrayDFA)
		new Tree2MesoArrayDFATransformer().transform(t_dfas[i]);

	    if (verbose)
		log().info().touch();
	}

	Mission.control().put("_tree-dfas", t_dfas);
	Mission.control().put("_meso-array-dfas", m_dfas);
    }

    private void compileLexer(Properties p)
    {
	DFA[] dfas = (DFA[])Mission.control().get("_meso-array-dfas");

	Lexer lexer = (Lexer)
	    reflect("compile-lexer-classname", 
		    "com.inxar.syntacs.analyzer.lexical.StandardLexer");

	lexer.initialize(dfas);

	Mission.control().put("_lexer", lexer);
    }

    private void compileContextFreeSet(Properties p)
    {
	if (verbose) {
	    String name = (String)Mission.control().get("_dpa-name");
	    log().info()
		.write("Building context-free grammar for ")
		.write(name)
		.time();
	}

	CFGrammar g = new CFGrammar();
	Map map = new HashMap();

	// Add all the terminals to the grammar.
	IntArray t = getTerminals();
	for (int i = 0; i < t.length(); i++) {
	    Token token = (Token)symbols.getSymbol( t.at(i) );
	    Terminal terminal = g.newTerminal(token);
	    map.put(terminal.getName(), terminal);
	}
	
	// Add all the nonterminals.
	IntArray n = getNonTerminals();
	for (int i = 0; i < n.length(); i++) {
	    NonTerminal nonterminal = g.newNonTerminal(getNonTerminal( n.at(i) ));
	    map.put(nonterminal.getName(), nonterminal);
	}
	
	// Now add all the productions.
	IntArray r = getProductions();
	for (int i = 0; i < r.length(); i++) {
	    int pID = r.at(i);
	    String pName = getProduction(pID);

	    if (DEBUG) 
		log().debug()
		    .write("Adding production `")
		    .write(pName)
		    .write("' to CFG")
		    .out();

	    int ntID = getProductionNonTerminal(pID);
	    String ntName = getNonTerminal(ntID);
	    NonTerminal nt = (NonTerminal)map.get(ntName);
	    Production rule = g.newProduction(nt);
	    
	    IntArray s = getProductionSymbols(pID);
	    
	    if (s == null || s.length() == 0)
		throw new IllegalArgumentException
		    ("Production `" + getProduction(pID) + 
		     "' has empty list for RHS.");

	    for (int j = 0; j < s.length(); j++) {
		int sID = s.at(j);
		String symName = getSymbol(sID);
		GrammarSymbol symbol = (GrammarSymbol)map.get(symName);
		
		if (symbol == null) 
		    throw new NullPointerException
			("Local map lookup of symbol `"+symName+"' was null");
		
		rule.add(symbol);
	    }
	    
	    // Set the start production if it reduces to goal.
	    if (getGoalNonTerminal() == ntID)
		g.setStartProduction(rule);
	}
	
	ContextFreeSet cfs = g.compile();

	if (Mission.control().isTrue("compile-grammar-context-free-debug"))
	    log().debug().write(cfs).out();
	
	Mission.control().put("_context-free-set", cfs);

	if (verbose) 
	    log().info()
		.touch();
    }
    
    private void compileDPA(Properties p)
    {
	ContextFreeSet cfs = (ContextFreeSet)Mission.control().get("_context-free-set");

	DPAConstructor ctor = null;
	ArrayDPA a_dpa = null;
	MesoArrayDPA ma_dpa = null;

	try {

	    String name = null;
	    if (verbose) {
		name = (String)Mission.control().get("_dpa-name");
		log().info()
		    .write("Building ")
		    .write(name)
		    .time();
	    }
	    
	    String ctorType = Mission.control()
		.getString("compile-dpa-constructor-method", "lalr1");

	    if (ctorType != null) {
		ctorType = ctorType.toLowerCase();
		if ("lalr1".equals(ctorType))
		    ctor = new LALR1Constructor();
		else if ("slr1".equals(ctorType))
		    throw new IllegalArgumentException("SLR is not currently supported, sorry.");
		/* ctor = new SLR1Constructor(); */
		else if ("lr1".equals(ctorType))
		    ctor = new LR1Constructor();
		else {
		    auditor.notify(Complaint.UNSPECIFIED_ERROR, 
				   "Illegal dpa constructor method: `"+ctorType+
				   "'.  Must be \"SLR1\", \"LALR1\", or \"LR1\" (not case-sensitive).");
		    ctor = new LR1Constructor();
		}
	    }

	    errcheck();

	    if (ctor == null) 
		ctor = (DPAConstructor)
		    reflect("compile-dpa-constructor-classname", 
			    "com.inxar.syntacs.automaton.pushdown.LALR1Constructor");

	    // If the CFGrammar fails by detection of a circular
	    // followset dependency, we will catch it and repackage to
	    // the auditor.
	    try {
		a_dpa = (ArrayDPA)ctor.construct(cfs);
	    } catch (IllegalStateException isex) {
		auditor.notify(Complaint.UNSPECIFIED_ERROR, isex.getMessage());
	    }

	    errcheck();


	    if (verbose)
		log().info()
		    .write("Compressing ")
		    .write(name)
		    .time();

	    ma_dpa = (MesoArrayDPA)new Array2MesoArrayDPATransformer()
		.transform(a_dpa);

	    if (Mission.control().isTrue("compile-dpa-debug"))
		log().debug().write(ctor).out();

	    
	} catch (AlgorithmException aex) {
	    aex.printStackTrace();
	    throw new IllegalStateException(aex.getMessage());
	} finally {
	    if (verbose) 
		log().info()
		    .touch();
	}

	Mission.control().put("_dpa-constructor", ctor);
	Mission.control().put("_array-dpa", a_dpa);
	Mission.control().put("_meso-array-dpa", ma_dpa);
	Mission.control().put("_initial-dpa", a_dpa);
	Mission.control().put("_final-dpa", ma_dpa);

    }

    private void compileParser(Properties p)
    {
	DPA dpa = (DPA)Mission.control().get("_final-dpa");

	Parser parser = (Parser)
	    reflect("compile-parser-classname", 
		    "com.inxar.syntacs.analyzer.syntactic.StandardParser");

	parser.initialize(dpa);
	Mission.control().put("_parser", parser);
    }

    private void compileLRTranslatorGrammar(Properties p)
    {
	ContextFreeSet cfs = (ContextFreeSet)Mission.control().get("_context-free-set");

	LRTranslatorGrammar tg = new ProxyLRTranslatorGrammar(this, cfs);
	Mission.control().put("_lr-translator-grammar", tg);
    }

    private void compileInterpreter(Properties p)
    {
	LRTranslatorInterpreter interp = (LRTranslatorInterpreter)
	    reflect("compile-interpreter-classname", 
		    "com.inxar.syntacs.translator.lr.StandardLRTranslatorInterpreter");

	Mission.control().put("_interpreter", interp);
    }

    private void compileInput(Properties p)
    {
	Input input = (Input)
	    reflect("compile-input-classname", 
		    "com.inxar.syntacs.analyzer.StandardInput");

	Mission.control().put("_input", input);
    }

    private void burn(Properties p)
    {
	new BurnerController().burn();
    }

    private void viz(Properties p)
    {
	new VizController().viz();
    }
    
    private Object reflect(String key, String def)
    {
	String className = Mission.control().getString(key, def);
	Object o = null;

	try {
	    o = Class.forName(className).newInstance();
	} catch (Exception ex) {

	    if (className != def) {
		addSemanticError
		    ("Could not reflect `"+className+
		     "' ("+ex.getClass().getName()+").  Check value for `"+key+
		     "' or CLASSPATH.");

		try { o = Class.forName(def).newInstance(); }
		catch (Exception ex2) {}
	    } else 
		throw new InternalError("Bad property name management.");

	}
	
	return o;
    }

    private void errcheck()
    {
	if (auditor.hasErrors())
	    throw new RuntimeTranslationException
		(auditor, "Translator construction failed.");
    }

    public void setInput(Input in)
    {
	this.in = in;
    }

    public Input getInput()
    {
	return in;
    }

    public void setAuditor(Auditor auditor)
    {
	this.auditor = auditor;
    }

    public Auditor getAuditor()
    {
	return auditor;
    }

    public void addSemanticError(String msg) 
    {
	auditor.notify(Complaint.SEMANTIC_ERROR, msg);
    }

    public void addSemanticWarning(String msg) 
    {
	auditor.notify(Complaint.SEMANTIC_WARNING, msg);
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 8    ||      Standard Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    public String toString()
    {
	return toSyntacs();
    }

    public String toString2()
    {
	if (version == null)
	    setVersion("0.0.1");

	StringBuffer b = new StringBuffer();
	b.append("Grammar `").append(grammarName)
	    .append("' version ").append(version).append(':')
	    .append(StringTools.NEWLINE);

	if (!properties.isEmpty()) {
	    Enumeration e = properties.keys();
	    b.append(StringTools.NEWLINE).append(" Properties:").append(StringTools.NEWLINE);
	    while (e.hasMoreElements()) {
		String key = (String)e.nextElement();
		b.append("  ").append('[').append(key).append("] ")
		    .append(properties.getProperty(key)).append(StringTools.NEWLINE);
	    }
	}

	IntArray a = getTerminals();
	b.append(StringTools.NEWLINE).append(" Terminals:").append(StringTools.NEWLINE);
	for (int i = 0; i < a.length(); i++) {
	    int tID = a.at(i);
	    b.append("  ").append('[').append(tID).append("] ").append(getTerminal(tID));
	    Object re = getTerminalRegexp(tID);
	    if (re != null)
		b.append(": ").append(re);
	    else 
		b.append(": <UNDEFINED>");
	    b.append(StringTools.NEWLINE);
	}

	a = getNonTerminals();
	b.append(StringTools.NEWLINE).append(" NonTerminals:").append(StringTools.NEWLINE);
	for (int i = 0; i < a.length(); i++) {
	    int ntID = a.at(i);
	    b.append("  ").append('[').append(ntID).append("] ").append(getNonTerminal(ntID));
	    if (goal == ntID)
		b.append(" <start>");
	    b.append(StringTools.NEWLINE);
	}

	a = getProductions();
	b.append(StringTools.NEWLINE).append(" Productions:").append(StringTools.NEWLINE);
	for (int i = 0; i < a.length(); i++) {
	    int pID = a.at(i);
	    b.append("  ").append('[').append(pID).append("] ")
		.append(getProduction(pID)).append(StringTools.NEWLINE);
	}

	a = getContexts();
	b.append(StringTools.NEWLINE).append(" Contexts:").append(StringTools.NEWLINE);
	for (int i = 0; i < a.length(); i++) {
	    int cID = a.at(i);
	    b.append("  ").append('[').append(cID).append("] ").append(getContext(cID));
	    if (startContext == cID)
		b.append(" <start>");
	    IntArray t = getContextTerminals(i);
	    if (t == null || t.length() == 0) {
		b.append(" is empty");
	    } else {
		b.append(StringTools.NEWLINE);
		for (int j = 0; j < t.length(); j++) {
		    int tID = t.at(j);
		    b.append("   -- ")
			.append(getTerminal(tID))
			.append(" -> ");
		    int action = getContextAction(cID, tID);
		    switch (action) {

		    case ACTION_PEEK: 
			b.append("PEEK"); 
			break;
		    case ACTION_POP:  
			b.append("POP"); 
			break;
		    case ACTION_PUSH: 
			b.append("PUSH ").append(getContext(getContextRegister(cID, tID)));
			break;
		    case UNDEFINED_CONTEXT:
			b.append("(error: undefined context)"); 
			break;
		    case UNDEFINED_SYMBOL:
			b.append("(error: undefined symbol)"); 
			break;
		    default:
			throw new InternalError();
		    }
		    
		    b.append(StringTools.NEWLINE);
		}
	    }
	    b.append(StringTools.NEWLINE);
	}

	if (!auditor.isEmpty()) {
	    b.append(StringTools.NEWLINE);
	    b.append(auditor.toString());
	}

	return b.toString();
    }

    public String toSyntacs()
    {
	if (version == null)
	    setVersion("0.0.1");

	StringBuffer b = new StringBuffer();

	// Print the grammar declaration
	comment(b, "GRAMMAR DECLARATION");
	b.append("this is ").append(grammarName).append(" version ").append(version).append(';')
	    .append(StringTools.NEWLINE);
	b.append(StringTools.NEWLINE);

	comment(b, "PROPERTY DEFINITIONS");
	// Print all the property definitions
	if (!properties.isEmpty()) {
	    Enumeration e = properties.keys();
	    while (e.hasMoreElements()) {
		String key = (String)e.nextElement();
		if (key.startsWith("_"))
		    continue;
		b.append("property ").append(key).append(" = \"")
		    .append(properties.getProperty(key)).append("\";").append(StringTools.NEWLINE);
	    }
	}
	b.append(StringTools.NEWLINE);

	comment(b, "TERMINAL DECLARATIONS");
	// Print all the terminal declarations
	IntArray a = getTerminals();
	for (int i = 0; i < a.length(); i++) {
	    int tID = a.at(i);
	    b.append("terminal ").append(getTerminal(tID)).append(';').append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);

	comment(b, "TERMINAL DEFINITIONS");
	// Print all the terminal definitions
	a = getTerminals();
	for (int i = 0; i < a.length(); i++) {
	    int tID = a.at(i);
	    Object o = getTerminalRegexp(tID);

	    if (o != null) {
		String re = o.toString();
		re = StringTools.replace(re.toString(), "\"", "\\\"");
		re = StringTools.replace(re.toString(), ";", "\\;");

		b.append(getTerminal(tID))
		    .append(" matches \"")
		    .append(re)
		    .append("\";")
		    .append(StringTools.NEWLINE);
	    }
	}
	b.append(StringTools.NEWLINE);

	comment(b, "NONTERMINAL DECLARATIONS");
	// Print all the nonterminal declarations
	a = getNonTerminals();
	for (int i = 0; i < a.length(); i++) {
	    int ntID = a.at(i);
	    b.append("nonterminal ").append(getNonTerminal(ntID)).append(';').append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);

	comment(b, "NONTERMINAL DEFINITIONS");
	// Print all the nonterminal definitions
	a = getProductions();
	for (int i = 0; i < a.length(); i++) {
	    int pID = a.at(i);
	    b.append("reduce ")
		.append( getNonTerminal(getProductionNonTerminal(pID)) )
		.append(" when");

	    IntArray sy = getProductionSymbols(pID);
	    for (int j = 0; j < sy.length(); j++) {
		b.append(' ').append(getSymbol(sy.at(j)));
	    }
	    b.append(';').append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);

	// Print the start nonterminal definitions
	b.append("accept when ")
	    .append(getNonTerminal(getGoalNonTerminal()))
	    .append(';')
	    .append(StringTools.NEWLINE);
	b.append(StringTools.NEWLINE);

	comment(b, "CONTEXT DECLARATIONS");
	// Print all the context declarations
	a = getContexts();
	for (int i = 0; i < a.length(); i++) {
	    int cID = a.at(i);
	    b.append("context ").append(getContext(cID)).append(';').append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);

	comment(b, "CONTEXT DEFINITIONS");
	// Print all the context definitions
	a = getContexts();
	for (int i = 0; i < a.length(); i++) {
	    int cID = a.at(i);
	    IntArray t = getContextTerminals(i);

	    if (t == null || t.length() == 0)
		continue;

	    b.append(getContext(cID)).append(" includes ");

	    for (int j = 0; j < t.length(); j++) {
		int tID = t.at(j);
		
		if (j > 0)
		    b.append(", ");

		b.append(getTerminal(tID));

		int action = getContextAction(cID, tID);

		switch (action) {
		    
		case ACTION_PEEK: 
		    break;
		case ACTION_POP:  
		    b.append(" unshifts"); 
		    break;
		case ACTION_PUSH: 
		    b.append(" shifts ").append(getContext(getContextRegister(cID, tID)));
		    break;
		case UNDEFINED_CONTEXT:
		    b.append("(error: undefined context)"); 
		    break;
		case UNDEFINED_SYMBOL:
		    b.append("(error: undefined symbol)"); 
		    break;
		default:
		    throw new InternalError();
		}
	    }
	    b.append(';').append(StringTools.NEWLINE);
	}
	b.append(StringTools.NEWLINE);

	// Print the start context
	b.append("start in context ")
	    .append(getContext(getStartContext()))
	    .append(';')
	    .append(StringTools.NEWLINE);
	b.append(StringTools.NEWLINE);

	if (!auditor.isEmpty()) {
	    b.append(StringTools.NEWLINE);
	    b.append(auditor.toString());
	}

	return b.toString();
    }

    private void writeFile(String dir, String file, String ext, String text)
    {
	BufferedWriter out = null;

	try {
	    
	    StringBuffer filename = new StringBuffer();
	    if (dir != null && dir.length() > 0) {
		filename.append(dir);
		if (dir.charAt(dir.length() - 1) != File.separatorChar)
		    filename.append(File.separatorChar);
	    }

	    filename.append(file);

	    if (ext != null)
		filename.append(ext);
		
	    out = new BufferedWriter(new FileWriter(filename.toString()));

	    //System.out.println(text);
	    out.write(text);

	    out.flush();
	    out.close();
	    out = null;
	    
	} catch (IOException ioex) {
	    ioex.printStackTrace();
	} finally {
	    if (out != null)
		try { out.close(); out = null; }
		catch (Exception ex) {}
	}
    }

    private static void comment(StringBuffer b, String s)
    {
	b.append("# ").append(s).append(StringTools.NEWLINE);
    }

    /* 
    +--+-----------------------------++-----------------------------+--+
    |  |                             ||                             |  |
    +--+-----------------------------++-----------------------------+--+
    |  | 41                          ||                          42 |  |
    |  |      Book III, Chapter 9    ||     Debugging Methods       |  |
    |  |                             ||                             |  |
    |  |                             ||                             |  |
   ~~~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~~~~~~ 
     */

    private Log log()
    {
	if (this.log == null)
	    this.log = Mission.control().log("lrg", this); // LRtranslatorGrammar
	return log;
    }

    /*
                    /-------------------------------------------
                   /                                           /
                  /                                           / \
                 /                                           /   \___
                /                                           /     \ /
               /                                           /      //
              /                                           /      //
             /                                           /      //
            /                                           /      //
           /                                           /      //
          /                                           /      // 
         /                                           /      //
        /                                           /      //
       /                                           /      // 
      /                                           /      //
     /___________________________________________/      //
     |                                           |\    //
      \   Book IV: Member Fields             pcj  \\  //
       \___________________________________________\\_/
    */

    private String grammarName;
    private String version;
    private int goal;
    private int startContext;

    private SymbolList contexts;
    private SymbolList symbols;
    private SymbolList productions;
    private IntList terminals;
    private IntList nonterminals;
    private ContextSymbol all;
    private boolean isDefaultExplicit;
    private boolean hasChanged;
    private Properties properties;
    
    private LRTranslator translator;
    private Input in;
    private Auditor auditor;

    private Log log;

    /*
                    /-------------------------------------------
                   /                                           /
                  /                                           / \
                 /                                           /   \___
                /                                           /     \ /
               /                                           /      //
              /                                           /      //
             /                                           /      //
            /                                           /      //
           /                                           /      //
          /                                           /      // 
         /                                           /      //
        /                                           /      //
       /                                           /      // 
      /                                           /      //
     /___________________________________________/      //
     |                                           |\    //
      \   Book V: Inner Classes              pcj  \\  //
       \___________________________________________\\_/
    */

    private static abstract class Symbol
    {
	protected Symbol(int ID, String name)
	{
	    this.ID = ID;
	    this.name = name;
	}

	public int getID()
	{
	    return ID;
	}

	public String getName()
	{
	    return name;
	}

	public String toString()
	{
	    return name;
	}

	public Object clone() throws CloneNotSupportedException 
	{ 
	    return super.clone(); 
	}

	int ID;
	String name;
    }

    private static class TerminalSymbol extends Symbol implements Token
    {
	TerminalSymbol(int ID, String name)
	{
	    super(ID, name);
	    this.contexts = new ArrayIntList();
	}

	void addContext(int ID)
	{
	    contexts.add(ID);
	}

	IntArray getContexts()
	{
	    return contexts;
	}

	boolean hasContext(int contextID)
	{
	    return contexts.contains(contextID);
	}

	Object regexp;
	private IntList contexts;
    }

    private static class NonTerminalSymbol extends Symbol
    {
	NonTerminalSymbol(int ID, String name)
	{
	    super(ID, name);
	}
    }

    private static class ProductionSymbol extends Symbol
    {
	ProductionSymbol(int ID, String name, int lhs, IntArray rhs)
	{
	    super(ID, name);
	    this.lhs = lhs;
	    this.rhs = rhs;
	}

	int lhs;
	IntArray rhs;
    }

    private class ContextSymbol extends Symbol
    {
	ContextSymbol(int ID, String name)
	{
	    super(ID, name);
	    this.symbols = new ArrayIntList();
	    this.actions = new HashIntFunction();
	    this.registers = new HashIntFunction();
	}

	int getAction(int ID)
	{
	    return actions.get(ID);
	}

	int getRegister(int ID)
	{
	    return registers.get(ID);
	}

	boolean hasAction(int ID)
	{
	    if (!actions.keys().contains(ID)) {
		return false;
	    } else {
		return true;
	    }
	}

	boolean hasRegister(int ID)
	{
	    if (ID >= registers.size())
		return false;
	    else
		return registers.get(ID) != 0;
	}

	void setPeek(int symbolID)
	{
	    addSymbol(symbolID);
	    actions.put(symbolID, ACTION_PEEK);
	}

	void setPop(int symbolID)
	{
	    addSymbol(symbolID);
	    actions.put(symbolID, ACTION_POP);
	}

	void setPush(int symbolID, int contextID)
	{
	    addSymbol(symbolID);
	    actions.put(symbolID, ACTION_PUSH);
	    registers.put(symbolID, contextID);
	}

	private void addSymbol(int symbolID)
	{
	    if (!symbols.contains(symbolID))
		symbols.add(symbolID);
	}

	HashIntFunction actions;
	HashIntFunction registers;
	IntList symbols;
    }

    private static class SymbolList implements IntArray
    {
	public SymbolList(int error)
	{
	    this.error = error;
	    this.src = new Symbol[3];
	    this.count = 0;
	}

	public int at(int index)
	{
	    return getID(index);
	}

	public int length()
	{
	    return count;
	}

	public int[] toArray()
	{
	    int[] dst = new int[count];
	    for (int i = 0; i < count; i++)
		dst[i] = src[i].ID;
	    return dst;
	}
	
	void add(Symbol s)
	{
	    while (count >= src.length)
		enlarge();
	    src[count++] = s;
	}

	int getID(int ID)
	{
	    Symbol s = getSymbol(ID);
	    return s == null ? error : s.ID;
	}

	int getID(String name)
	{
	    for (int i = 0; i < count; i++)
		if (src[i].name.equals(name))
		    return src[i].ID;
	    return error;
	}
	
	String getName(int ID)
	{
	    Symbol s = getSymbol(ID);
	    return s == null ? null : s.name;
	}
	
	Symbol getSymbol(int ID)
	{
	    // In some instances, the ID will correspond to the index
	    // in the array (for productions and contexts), and in
	    // other instances (terminals + nonterminals), the ID will
	    // be one greater than the array index.
	    for (int i = Math.min(count-1, ID); i >= 0; i--)
		if (src[i].ID == ID)
		    return src[i];

	    return null;
	}

	Symbol getSymbol(String name)
	{
	    for (int i = 0; i < count; i++)
		if (src[i].name.equals(name))
		    return src[i];
	    return null;
	}
	
	boolean contains(int ID)
	{
	    for (int i = 0; i < count; i++)
		if (src[i].ID == ID)
		    return true;
	    return false;
	}

	boolean contains(String name)
	{
	    for (int i = 0; i < count; i++)
		if (src[i].name.equals(name))
		    return true;
	    return false;
	}

	boolean isEmpty()
	{
	    return count == 0;
	}

	void enlarge()
	{
	    Symbol[] dst = new Symbol[ src.length * 2 ];
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
	    SymbolList clone = (SymbolList)super.clone();
	    clone.src = (Symbol[])src.clone();
	    return clone;
	}

	private Symbol[] src;
	private int count;
	private int error;
    }
}

