/**
 * $Id: RegexpInterpreter.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator.regexp;

import java.util.*;
import org.inxar.syntacs.grammar.*;
import org.inxar.syntacs.analyzer.*;
import com.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.analyzer.lexical.*;
import com.inxar.syntacs.analyzer.lexical.*;
import org.inxar.syntacs.analyzer.syntactic.*;
import com.inxar.syntacs.analyzer.syntactic.*;
import org.inxar.syntacs.automaton.finite.*;
import org.inxar.syntacs.automaton.pushdown.*;
import org.inxar.syntacs.translator.*;
import com.inxar.syntacs.translator.*;
import org.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * <code>Interpreter</code> used in the translation of regular
 * expression strings.
 */
public class RegexpInterpreter extends StandardLRTranslatorInterpreter 
{
    private static final boolean DEBUG = false;

    public RegexpInterpreter()
    {
    }

    public void match(int type, int off, int len) throws TranslationException
    {
	if (DEBUG) 
	    log().debug()
		.write("seeing input ").write(type)
		.out();

	// init our symbol
	Symbol symbol = null;
	
        switch (type) {

	case RegexpGrammar.T_WHITESPACE:
	    if (DEBUG) 
		log().debug()
		    .write("Skipping whitespace")
		    .out();

	    return;

	case RegexpGrammar.T_PIPE:
	case RegexpGrammar.T_OPEN_PAREN:
	case RegexpGrammar.T_CLOSE_PAREN:
	case RegexpGrammar.T_CHAR_CLASS_DASH:
	case RegexpGrammar.T_CLOSE_BRACKET:
	    symbol = new ConstantSymbol(type); 
	    break;

	case RegexpGrammar.T_CHAR:
	case RegexpGrammar.T_CHAR_CLASS_CHAR:
	    if (len != 1)
		throw new InternalError("Expected token to be only one character.");
	    symbol = newAtom( type, in.retch(off) );
	    break;

	case RegexpGrammar.T_ESC_OCTAL:
	    {
		char c = (char)0;
		try {
		    c = (char)Integer.parseInt( in.stretch(off + 1, len - 1), 8 );
		} catch (NumberFormatException nfex) {

		    log().critical()
			.write("NumberFormatException caught while trying to parse ")
			.write(in.stretch(off + 1, len - 1))
			.out();

		    nfex.printStackTrace();
		}
		symbol = newAtom(type, c);
		break;
	    }

	case RegexpGrammar.T_ESC_UNICODE:
	    {
		char c = (char)0;
		try {
		    c = (char)Integer.parseInt( in.stretch(off + 2, len - 2), 16 );
		} catch (NumberFormatException nfex) {
		    nfex.printStackTrace();
		}
		symbol = newAtom(type, c);
		break;
	    }

	case RegexpGrammar.T_ESC:         
	    char c = in.retch(off + 1);
	    switch (c) {

	    case 's': c = ' ';  break;
	    case 'n': c = '\n'; break;
	    case 'r': c = '\r'; break;
	    case 'f': c = '\f'; break;
	    case 't': c = '\t'; break;
	    case 'b': c = '\b'; break;
	    case 'v': c = '\013'; break;

	    case '+': case '*': case '?': 
	    case '|': case '(': case ')': 
	    case '[': case ']': case '\\': 
	    case '"':
		break;

	    default:
		auditor.notify(Complaint.LEXICAL_ERROR, 
			       "Undefined literal escape", 
			       in, off, len);

	    }
	    symbol = newAtom(type, c);
	    break;

	case RegexpGrammar.T_OPEN_BRACKET:            
	    symbol = newClass(type, false, false); break;
	case RegexpGrammar.T_OPEN_BRACKET_CARET:      
	    symbol = newClass(type, true, false); break;
	case RegexpGrammar.T_OPEN_BRACKET_DASH:       
	    symbol = newClass(type, false, true); break;
	case RegexpGrammar.T_OPEN_BRACKET_CARET_DASH: 
	    symbol = newClass(type, true, true); break;

	case RegexpGrammar.T_STAR:     
	    symbol = new QuantifierSymbol(type, Regexp.CLOSURE); break;
	case RegexpGrammar.T_QUESTION: 
	    symbol = new QuantifierSymbol(type, Regexp.OPTIONAL); break;
	case RegexpGrammar.T_PLUS:     
	    symbol = new QuantifierSymbol(type, Regexp.PCLOSURE); break;

	default: 
	    throw new InternalError
		("Expected terminal type: " + grammar.getTerminal(type));
        }

	parser.notify(symbol);
    }

    public Symbol reduce(int type, Sentence s) throws TranslationException
    {
	if (DEBUG) 
	    log().debug()
		.write("REDUCE ")
		.write(grammar.getProduction(type))
		.out();

	Symbol symbol = null;

	switch (type) {

	    // unary promotion cases.
	case RegexpGrammar.P_Quantifier__STAR:
	case RegexpGrammar.P_Quantifier__PLUS:
	case RegexpGrammar.P_Quantifier__QUESTION:

	case RegexpGrammar.P_Term__Atom:
	case RegexpGrammar.P_Atom__CHAR:
	case RegexpGrammar.P_Atom__ESC:
	case RegexpGrammar.P_Atom__ESC_OCTAL:
	case RegexpGrammar.P_Atom__ESC_UNICODE:
	case RegexpGrammar.P_Atom__CharClass:

	case RegexpGrammar.P_CharClassTerm__CharClassAtom:
	case RegexpGrammar.P_CharClassAtom__CHAR_CLASS_CHAR:
	case RegexpGrammar.P_CharClassAtom__ESC:
	case RegexpGrammar.P_CharClassAtom__ESC_OCTAL:
	case RegexpGrammar.P_CharClassAtom__ESC_UNICODE:

	case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET:
	case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET_CARET:
	case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET_DASH:
	case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET_CARET_DASH:
	    {
		symbol =  s.at(0);
		break;
	    }

	case RegexpGrammar.P_Union__Concat:
	    {
		RegexpList union = new RegexpList(Regexp.UNION);
		union.addRegexp( (Regexp)s.at(0) );
		symbol = union;
		break;
	    }

	case RegexpGrammar.P_Concat__Term:
	    {
		RegexpList concat = new RegexpList(Regexp.CONCAT);
		concat.addRegexp( (Regexp)s.at(0) );
		symbol = concat;
		break;
	    }

	case RegexpGrammar.P_Concat__Concat_Term:
	    {
		RegexpList list = (RegexpList)s.at(0);
		list.addRegexp( (Regexp)s.at(1) );
		symbol = list;
		break;
	    }

	case RegexpGrammar.P_Union__Union_PIPE_Concat:
	    {
		RegexpList list = (RegexpList)s.at(0);
		list.addRegexp( (Regexp)s.at(2) );
		symbol = list;
		break;
	    }

	case RegexpGrammar.P_CharClassTermList__CharClassTerm:
	    {
		symbol = new ListSymbol(type, s.at(0));
		break;
	    }

	case RegexpGrammar.P_CharClassTermList__CharClassTermList_CharClassTerm:
	    {
		ListSymbol sym = (ListSymbol)s.at(0);
		sym.list.add(s.at(1));
		symbol = sym;
		break;
	    }

	case RegexpGrammar.P_Term__Atom_Quantifier:
	    {
		QuantifierSymbol q = (QuantifierSymbol)s.at(1); 
		RegexpTerm term = new RegexpTerm(q.regexpType);
		term.setInternal( (Regexp)s.at(0) );
		symbol = term;
		break;
	    }

	case RegexpGrammar.P_Atom__OPEN_PAREN_Union_CLOSE_PAREN:
	    {
		RegexpTerm term = new RegexpTerm(Regexp.GROUP);
		term.setInternal( (Regexp)s.at(1) );
		symbol = term;
		break;
	    }

	case RegexpGrammar.P_CharClassTerm__CharClassAtom_CHAR_CLASS_DASH_CharClassAtom:
	    {
		RegexpAtom lo = (RegexpAtom)s.at(0);
		RegexpAtom hi = (RegexpAtom)s.at(2);
		symbol = new RegexpRange(lo, hi);
		break;
	    }

	case RegexpGrammar.P_CharClass__CharClassBegin_CharClassTermList_CLOSE_BRACKET:
	    {
		RegexpCharClass cc = new RegexpCharClass();
		CharClassBeginSymbol ccbs = (CharClassBeginSymbol)s.at(0);
		cc.isNegated(ccbs.isNegated);
		cc.hasDash(ccbs.hasDash);
		List list = ((ListSymbol)s.at(1)).list;
		cc.setList(list);
		symbol = cc;
		break;
	    }

	case RegexpGrammar.P_CharClass__OPEN_BRACKET_CARET_DASH_CLOSE_BRACKET:
	    {
		RegexpCharClass cc = new RegexpCharClass();
		CharClassBeginSymbol ccbs = (CharClassBeginSymbol)s.at(0);
		cc.isNegated(ccbs.isNegated);
		cc.hasDash(false);

		RegexpAtom dash_atom = new RegexpAtom();
		dash_atom.setValue('-');
		List list = new ArrayList();
		list.add(dash_atom);

		cc.setList(list);
		symbol = cc;
		break;
	    }

	case RegexpGrammar.P_Goal__Union:
	    {
		Regexp regexp = (Regexp)s.at(0);
		this.regexp = regexp;
		symbol = regexp;
		break;
	    }

	default:
	    {
		throw new InternalError("Unknown Production: "+grammar.getProduction(type));
	    }
	}

	// return the phrase
	return symbol;
    }

    public void reset()
    {
	super.reset();
	regexp = null;
    }

    public Object getResult()
    {
	return regexp;
    }

    private Symbol newAtom(int type, char value)
    {
	RegexpAtom atom = new RegexpAtom();
	atom.setValue( value );
	atom.setSymbolType(type);
	return atom;
    }

    private Symbol newClass(int type, boolean isNegated, boolean hasDash)
    {
	CharClassBeginSymbol ccbs = new CharClassBeginSymbol(type);
	ccbs.isNegated = isNegated;
	ccbs.hasDash = hasDash;
	return ccbs;
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("regi", this);
	return log;
    }

    private Log log;
    private Regexp regexp;

    private static class QuantifierSymbol
	implements Symbol
    {
	QuantifierSymbol(int symbolType, int regexpType)
	{
	    this.symbolType = symbolType;
	    this.regexpType = regexpType;
	}

	public int getSymbolType()
	{
	    return symbolType;
	}

	public void setSymbolType(int symbolType)
	{
	    this.symbolType = symbolType;
	}

	public String toString()
	{
	    switch (regexpType) {
	    case Regexp.OPTIONAL: return "?";
	    case Regexp.CLOSURE:  return "*";
	    case Regexp.PCLOSURE: return "+";
	    default:
		throw new InternalError();
	    }
	}
	
	int symbolType;
	int regexpType;
    }

    private static class CharClassBeginSymbol
	implements Symbol
    {
	CharClassBeginSymbol(int symbolType)
	{
	    this.symbolType = symbolType;
	}

	public int getSymbolType()
	{
	    return symbolType;
	}

	public void setSymbolType(int symbolType)
	{
	    this.symbolType = symbolType;
	}

	public String toString()
	{
	    StringBuffer b = new StringBuffer(5);
	    b.append('[');
	    if (isNegated) b.append('^');
	    if (hasDash) b.append('-');
	    b.append(" ~~~ ]");
	    return b.toString();
	}

	int symbolType;
	boolean isNegated;
	boolean hasDash;
    }
}
