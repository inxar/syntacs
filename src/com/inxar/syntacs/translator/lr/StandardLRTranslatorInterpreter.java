/**
 * $Id: StandardLRTranslatorInterpreter.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.analyzer.Symbol;
import org.inxar.syntacs.analyzer.lexical.Lexer;
import org.inxar.syntacs.analyzer.syntactic.Parser;
import org.inxar.syntacs.analyzer.syntactic.Recovery;
import org.inxar.syntacs.analyzer.syntactic.Sentence;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.Complaint;
import org.inxar.syntacs.translator.lr.LRTranslatorInterpreter;
import org.inxar.syntacs.util.Log;

import com.inxar.syntacs.analyzer.ArraySymbol;
import com.inxar.syntacs.analyzer.ConstantSymbol;
import com.inxar.syntacs.analyzer.ObjectSymbol;
import com.inxar.syntacs.util.Mission;

/**
 * Concrete implementation of <code>LRInterpreter</code>.
 */
public class StandardLRTranslatorInterpreter extends AbstractLRTranslationComponent
    implements LRTranslatorInterpreter
{
    private static final boolean DEBUG = true;

    private static boolean debug =
	Mission.control().getBoolean("run-interpreter-debug", false);

    /**
     * Constructs the <code>StandardLRInterpreter</code>.
     */
    public StandardLRTranslatorInterpreter()
    {
    }

    // ================================================================
    // LexerInterpreter methods
    // ================================================================

    public void match(int type, int off, int len) throws TranslationException
    {
	if (DEBUG && debug)
	    log().debug()
		.write("Matched ")
		.write(grammar.getTerminal(type))
		.out();

	parser.notify(new ObjectSymbol(type, in.stretch(off, len)));
    }

    public int error(int off, int len) throws TranslationException
    {
	String msg = null;
	if (len > 1)
	    msg = "Unexpected string was ignored by the lexer.";
	else if (len == 1)
	    msg = "Unexpected character was ignored by the lexer.";
	else
	    throw new InternalError("An error of length "+len+"?");

	if (DEBUG && debug)
	    log().debug()
		.write("Lexical Error from ").write(off)
		.write(" to ").write((off+len))
		.out();

	auditor.notify(Complaint.LEXICAL_ERROR,
		       msg, in, off, len);
	return -1;
    }

    public void stop() throws TranslationException
    {
	parser.notify(new ConstantSymbol(Token.STOP));
    }

    public void setParser(Parser parser)
    {
	this.parser = parser;
    }

    public Parser getParser()
    {
	return parser;
    }

    // ================================================================
    // ParserInterpreter methods
    // ================================================================

    public Symbol reduce(int type, Sentence sentence) throws TranslationException
    {
	if (DEBUG && debug)
	    log().debug()
		.write("REDUCE ").write(grammar.getProduction(type))
		.out();

        // make a new phrase of that type with this many members
        ArraySymbol symbol = new ArraySymbol(sentence.length());

        // add em all
        for (int i=0; i<sentence.length(); i++)
	    symbol.add(sentence.at(i));

	last = symbol;

        // return the phrase
        return symbol;
    }

    public Recovery recover(int type, Sentence left_context) throws TranslationException
    {
	if (DEBUG && debug)
	    log().debug()
		.write("ERROR!")
		.out();

	if (false)
	    auditor.notify
		(Complaint.SYNTACTIC_ERROR,
		 "The parser reported a structural error at or near line " +
		 in.atln());

	return null;
    }

    public void accept() throws TranslationException
    {
	result = last;
	if (DEBUG && debug)
	    log().debug()
		.write("ACCEPT!")
		.out();
    }

    public Object getResult()
    {
	return result;
    }

    public void setLexer(Lexer lexer)
    {
	this.lexer = lexer;
    }

    public Lexer getLexer()
    {
	return this.lexer;
    }

    // ================================================================
    // General methods
    // ================================================================

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("int", this); // standard INTerpreter
	return log;
    }

    protected Object last;
    protected Object result;
    protected Lexer lexer;
    protected Parser parser;
    private Log log;
}
