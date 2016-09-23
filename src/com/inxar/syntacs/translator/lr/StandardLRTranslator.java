/**
 * $Id: StandardLRTranslator.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.Properties;

import org.inxar.syntacs.analyzer.Input;
import org.inxar.syntacs.analyzer.lexical.Lexer;
import org.inxar.syntacs.analyzer.syntactic.Parser;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.TranslatorGrammar;
import org.inxar.syntacs.translator.lr.LRTranslator;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;
import org.inxar.syntacs.translator.lr.LRTranslatorInterpreter;
import org.inxar.syntacs.translator.lr.LRTranslationComponent;
import org.inxar.syntacs.translator.Auditor;
import org.inxar.syntacs.util.Log;

import com.inxar.syntacs.translator.StandardAuditor;
import com.inxar.syntacs.util.Mission;

/**
 * Standard implementation of <code>LRTranslator</code>.
 */
public class StandardLRTranslator implements LRTranslator
{
    private static final boolean DEBUG = false;
    private static boolean verbose =
	Mission.control().isTrue("verbose");

    public StandardLRTranslator()
    {
    }

    public Object translate(Object src) throws TranslationException
    {
	if (DEBUG)
	    log().debug()
		.write("Translating ")
		.write(src)
		.time();

	// Errors
	Auditor auditor = new StandardAuditor();

	try {

	    // Check
	    if (grammar == null)
		throw new NullPointerException("Undefined LRTranslatorGrammar");

	    if (in      == null)
		throw new NullPointerException("Undefined Input");

	    if (lexer   == null)
		throw new NullPointerException("Undefined Lexer");

	    if (inter   == null)
		throw new NullPointerException("Undefined Interpreter");

	    if (parser  == null)
		throw new NullPointerException("Undefined Parser");

	    if (p == null)
		p = new Properties();

	    // Reset
	    in.initch(src);
	    reset(lexer,  auditor);
	    reset(inter,  auditor);
	    reset(parser, auditor);

	    // Chain
	    lexer.setLexerInterpreter(inter);
	    inter.setLexer(lexer);
	    inter.setParser(parser);
	    parser.setParserInterpreter(inter);

	    // Go
	    lexer.start();

	    if (!auditor.isEmpty())
		throw new TranslationException(auditor);
	    else
		return getResult();

	} catch (TranslationException tex) {
	    throw tex;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    throw new TranslationException(auditor, ex);
	} finally {
	    if (verbose)
		log().debug().touch();
	}
    }

    protected Object getResult()
    {
	Object o = inter.getResult();

	return o;
    }

    private void reset(LRTranslationComponent c, Auditor auditor)
    {
	c.setInput(in);
	c.setLRTranslatorGrammar(grammar);
	c.setAuditor(auditor);
	c.setProperties(p);
	c.reset();
    }

    public TranslatorGrammar getGrammar()
    {
	return grammar;
    }

    public Properties getProperties()
    {
	return p;
    }

    public void setProperties(Properties p)
    {
	if (p != null)
	    this.p = p;
    }

    public void setInput(Input in)
    {
	this.in = in;
    }

    public void setLexer(Lexer lexer)
    {
	this.lexer = lexer;
    }

    public void setLRTranslatorInterpreter(LRTranslatorInterpreter inter)
    {
	this.inter = inter;
    }

    public void setParser(Parser parser)
    {
	this.parser = parser;
    }

    public void setLRTranslatorGrammar(LRTranslatorGrammar grammar)
    {
	this.grammar = grammar;
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("lrt", this); // LRTranslator
	return log;
    }

    private LRTranslatorGrammar grammar;
    private Input in;

    private Lexer lexer;
    private Parser parser;
    private LRTranslatorInterpreter inter;

    private Properties p;

    private Log log;
}
