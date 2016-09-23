/**
 * $Id: ProxyLRTranslatorGrammar.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * This class is a hack; it is required due to the dis-synchrony
 * between the ID number of the grammar symbols when they are parsed
 * into the <code>StandardLRTranslatorGrammar</code> instance and the
 * ID numbers of the grammar symbols after they exists in the
 * <code>DPA</code>.
 *
 * <P> Let me explain: When a grammar is parsed, the translator reads
 * terminal and nonterminal names, and it adds those incrementally to
 * the <code>StandardLRTranslatorGrammar</code> instance.  The
 * <code>StandardLRTranslatorGrammar</code>, in turn, is allocating ID
 * numbers for those symbols as they come in.  So far so good.
 *
 * <P>
 *
 * When the parse is finished, the
 * <code>StandardLRTranslatorGrammar</code> is compiled and all the
 * semantic checks are done.  If everything checks out, the process of
 * creating a <code>Translator</code> from this information begins.
 * The first thing it does is create a <code>RegularGrammar</code>
 * instances for each context in the grammar which is then transformed
 * to a <code>DFA</code>.  The second thing that happens is that a
 * <code>ContextFreeGrammar</code> is created which is then
 * transformed to a <code>DPA</code>.
 *
 * <P>
 *
 * The hangup is that the <code>ContextFreeGrammar</code> <i>also</i>
 * allocated its own set of ID numbers for grammar symbols which may
 * be slightly different than those used by the original
 * <code>StandardLRTranslatorGrammar</code>.  This creates a problem
 * if the <code>StandardLRTranslatorGrammar</code> is used during
 * actual translation because the <code>DPAParser</code> relies on the
 * <code>getProductionNonTerminal()</code> and
 * <code>getProductionLength()</code> methods for successful
 * reduction.
 *
 * <P>
 *
 * This class is then a hybrid --- some of the methods are dispatched
 * to the <code>LRTranslatorGrammar</code>, while the ones used in
 * parsing are dispatched to the <code>ContextFreeSet</code>.
 *
 * <P>
 *
 * This "design" is a reflection of the fact that the
 * <code>StandardLRTranslatorGrammar</code> and the
 * <code>ContextFreeSet</code> were developed at different times; they
 * have not yet been completely unified.
 */
public class ProxyLRTranslatorGrammar
    implements LRTranslatorGrammar
{
    public ProxyLRTranslatorGrammar(LRTranslatorGrammar g, ContextFreeSet c)
    {
	this.g = g;
	this.c = c;
    }

    public String getName()
    {
	return g.getName();
    }

    public String getVersion()
    {
	return g.getVersion();
    }

    public Translator newTranslator()
    {
	return g.newTranslator();
    }

    public Translator newTranslator(Properties p)
    {
	return g.newTranslator(p);
    }

    public IntArray getContexts()
    {
	return g.getContexts();
    }

    public String getContext(int ID)
    {
	return g.getContext(ID);
    }

    public int getStartContext()
    {
	return g.getStartContext();
    }

    public int getContextAction(int contextID, int symbolID)
    {
	return g.getContextAction(contextID, symbolID);
    }

    public int getContextRegister(int contextID, int symbolID)
    {
	return g.getContextRegister(contextID, symbolID);
    }

    public IntArray getContextTerminals(int ID)
    {
	return g.getContextTerminals(ID);
    }

    public IntArray getTerminals()
    {
	return g.getTerminals();
    }

    public String getTerminal(int ID)
    {
	return g.getTerminal(ID);
    }

    public Object getTerminalRegexp(int ID)
    {
	return g.getTerminalRegexp(ID);
    }

    public IntArray getTerminalContexts(int ID)
    {
	return g.getTerminalContexts(ID);
    }

    public IntArray getNonTerminals()
    {
	return g.getNonTerminals();
    }

    public String getNonTerminal(int ID)
    {
	return g.getNonTerminal(ID);
    }

    public int getGoalNonTerminal()
    {
	return g.getGoalNonTerminal();
    }

    public IntArray getProductions()
    {
	return g.getProductions();
    }

    public String getProduction(int ID)
    {
	return g.getProduction(ID);
    }

    public int getProductionNonTerminal(int ID)
    {
	return c.getProduction(ID).getNonTerminal().getID();
    }

    public IntArray getProductionSymbols(int ID)
    {
	return g.getProductionSymbols(ID);
    }

    public int getProductionLength(int ID)
    {
	return c.getProduction(ID).length();
    }

    public String toString()
    {
	return g.toString();
    }

    private ContextFreeSet c;
    private LRTranslatorGrammar g;
}
