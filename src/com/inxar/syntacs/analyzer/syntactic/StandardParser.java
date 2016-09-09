/**
 * $Id: StandardParser.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer.syntactic;

import org.inxar.syntacs.grammar.*;
import org.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.analyzer.syntactic.*;
import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.translator.*;
import org.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>Parser</code>.
 */
public class StandardParser extends AbstractLRTranslationComponent
    implements Parser
{
    private static final boolean DEBUG = true;
    
    /*
     * Constructs a new parser.
     */
    public StandardParser()
    {
    }

    /**
     * The argument must be a <code>DPA</code>.
     */
    public void initialize(Object arg)
    {
	if (arg instanceof DPA) 
	    this.dpa = (DPA)arg;
	else
	    throw new IllegalArgumentException
		("Require DPA initialization argument.");
    }

    public void reset()
    {
	this.debug = "true".equals(p.getProperty("run-parser-debug"));
	this.errlim = Mission.control().getInt("run-error-limit", -1);
	
	// make the state integer stack
	states = new ArrayIntStack(11);
	// and the symbol stack
	stack = new ParseStack(7);
	// set the start state
	state = DPA.START_STATE;
	// and push it
	states.push(state);
    }

    public void nextRx()
    {
	// PART 0: possible recovery routines.
	if (rx != null) {
	    if (rx.hasNext()) {
		cx = rx.next();
	    } else {
		cx = null;
		rx = null;
	    }
	}
    }

    public void notify(Symbol symbol) throws TranslationException
    {
	if (cx != null) {

	    switch (cx.getType()) {

	    case Correction.NOOP:
		break;

	    case Correction.ABORT:
		throw new TranslationException(auditor);

	    case Correction.WAIT:
		int waitSymbol = ((Number)cx.getValue()).intValue();

		if (DEBUG && debug)
		    log().debug()
			.write("In WAIT for ")
			.write(grammar.getTerminal(waitSymbol))
			.write(", seeing ")
			.write(getSymbolName(symbol.getSymbolType()))
			.out();

		if (symbol.getSymbolType() == waitSymbol) {
		    if (DEBUG && debug)
			log().debug()
			    .write("WAIT satisfied, moving to next correction instruction.")
			    .out();

		    nextRx();
		} 

		return;
		
	    case Correction.TUMBLE:
		if (DEBUG && debug)
		    log().debug()
			.write("TUMBLING with input ")
			.write(getSymbolName(symbol.getSymbolType()))
			.out();

		while (!states.isEmpty()) {
		    state = states.peek();

		    if (dpa.action
			(state, symbol.getSymbolType()).getType() != Action.ERROR) {

			if (DEBUG && debug)
			    log().debug()
				.write("TUMBLED into state ").write(state)
				.write(" ")
				.write(grammar.getNonTerminal(stack.peek().getSymbolType()))
				.out();

			break; /* out-of-switch */
		    }

		    states.pop();

		    if (stack.isEmpty())
			throw new TranslationException(auditor, "Tumbled Out of Parse");

		    Symbol sym = stack.pop();
		    if (DEBUG && debug) 
			log().debug()
			    .write("TUMBLING... discarding ")
			    .write(getSymbolName(symbol.getSymbolType()))
			    .out();
		}
		
		nextRx();
		notify(symbol);
		return;
	    }
	}
    	    
	if (DEBUG && debug)
	    log().debug().over();

	// PART 1: get the action from the pushdown automata
	Action action = dpa.action(state, symbol.getSymbolType());

	if (DEBUG && debug) 
	    if (action.getType() == Action.REDUCE)
		log().debug()
		    .write("in state ").write(state)
		    .write(", seeing input ")
		    .write(getSymbolName(symbol.getSymbolType()))
		    .write(" ").write(symbol)
		    .write(", DPA says REDUCE ")
		    .write(grammar.getProduction(action.getValue()))
		    .out();
	    else
		log().debug()
		    .write("in state ").write(state)
		    .write(", seeing input ")
		    .write(getSymbolName(symbol.getSymbolType()))
		    .write(" ").write(symbol)
		    .write(", DPA says ")
		    .write(action)
		    .out();
	
	// PART 2: see what to do
	switch (action.getType()) {
	case Action.SHIFT:
	    
	    // grab the next state from the action register and save
	    // to current
	    state = action.getValue();
	    // shift this next state
	    states.push(state);
	    // shift the token
	    stack.push(symbol);

	    if (DEBUG && debug) 
		log().debug()
		    .write("Shifted state ").write(state)
		    .out();

	    // done
	    break;
	    
	case Action.REDUCE:
	    
	    // get the value of the production from the action
	    // register
	    int productionID = action.getValue();

	    // Get the length of the production we are about to reduce
	    int length = grammar.getProductionLength(productionID);

	    // Get the id of the nonterminal symbol we are about to
	    // reduce to
	    int nonTerminalID = grammar.getProductionNonTerminal(productionID);
	    
	    if (DEBUG && debug) 
		log().debug()
		    .write("Reducing ")
		    .write(grammar.getProduction(productionID))
		    .write("...")
		    .out();

	    // notify the interpreter the number of the production
	    // contained in the action at the register and an iterator
	    // over the top |P| elements in the stack where P is the
	    // production given by type given at the action.getValue().
	    // This returns a syntax node that is the result of the
	    // reduction (a Symbol) and thus we shift it.
	    Symbol nonTerminal = interpreter.reduce(productionID, stack.reduce(length));

	    // Set the type of the symbol that was returned.  This
	    // design is such that the user does not have to manage
	    // the nonTerminals type id's.
	    nonTerminal.setSymbolType(nonTerminalID);

	    // push the new symbol onto the parse stack
	    stack.push(nonTerminal);

	    // now consult the goto table for the next state. Peeling
	    // thus returns the top state on the stack.  the type of
	    // nonTerminal that was just pushed on the stacktack
	    state = dpa.go(states.peel(length), nonTerminalID);
	    
	    // push the next state to the top of the state stack
	    states.push(state);
	
//  	    if (DEBUG && debug) 
//  		log().debug()
//  		    .write("Reduced ")
//  		    .write(grammar.getProduction(productionID))
//  		    .out();
     
	    // we recurse on reduce as there is more work to do.
	    notify(symbol);
	    
	    // done
	    break;
	    
	case Action.ACCEPT:
	    
	    interpreter.accept();
	    break;
	    
	case Action.ERROR:
	    
	    if (DEBUG && debug) 
		log().debug()
		    .write("SYNTAX ERROR!")
		    .out();

	    rx = interpreter.recover(symbol.getSymbolType(), stack);
	    
	    if (rx != null)
		if (rx.hasNext()) 
		    cx = rx.next();
		else
		    rx = null;
	     

	    // Make error message
	    if (errlim > 0 && auditor.errors() > errlim)
		throw new TranslationException(auditor);
	    else
		auditor.notify(Complaint.SYNTACTIC_ERROR, 
			       "Unexpected parse sequence at or before current input position.",
			       in, in.atch(), 1);
	    break;
	    
	default:
	    // this should *never* occur
	    throw new InternalError("Inappropriate LR parse instruction.");
	}

	if (DEBUG && debug)
	    log().debug().back();
    }

    public void setParserInterpreter(ParserInterpreter interpreter)
    {
	this.interpreter = interpreter;
    }

    public ParserInterpreter getParserInterpreter()
    {
	return interpreter;
    }
    
    private String getSymbolName(int ID)
    {
	String name = grammar.getTerminal(ID);
	if (name == null)
	    name = grammar.getNonTerminal(ID);
	return name;
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("prs", this);
	return log;
    }
    
    private int errlim;
    private int state;
    private Recovery rx;
    private Correction cx;
    private ArrayIntStack states;
    private ParseStack stack;
    private ParserInterpreter interpreter;
    private DPA dpa;
    private Log log;
    private boolean debug;

    private static final class ParseStack
	implements Sentence
    {
	ParseStack(int capacity)
	{
	    stack = new Symbol[capacity];
	    index = 0;
	}
	
	void push(Symbol symbol)
	{
	    check();
	    stack[index++] = symbol;
	}
	
	Symbol pop()
	{
	    return stack[--index];
	}
	
	Symbol peek()
	{
	    return stack[index - 1];
	}
	
	boolean isEmpty()
	{
	    return index == 0;
	}
	
	int size()
	{
	    return index;
	}
	
	public String toString()
	{
	    StringBuffer buf = new StringBuffer("[");
	    
	    for (int i=0; i < index; i++) {
		if (i>0) buf.append("; ");
		buf.append(stack[i]);
	    }
	    
	    return buf.append(']').toString();
	}
	
	private void check()
	{
	    if (index == stack.length) {
		Symbol[] dst = new Symbol[stack.length * 2];
		System.arraycopy(stack, 0, dst, 0, stack.length);
		stack = dst;
	    }
	}
	
	Sentence reduce(int length)
	{
	    this.length = length;
	    this.index -= length;
	    return this;
	}
	
	public int length()
	{
	    return length;
	}
	
	public Symbol at(int pos)
	{
	    return stack[index + pos];
	}
	
	public Symbol get(int index)
	{
	    return stack[index];
	}
	
	private int length;
	private int index;
	private Symbol[] stack;
    }
}




