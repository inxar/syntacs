/**
 * $Id: ContextFreeGrammar.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar.context_free;

import org.inxar.syntacs.grammar.Token;

/**
 * The <code>ContextFreeGrammar</code> interface is a factory for
 * assembling context free grammars, analogous to the
 * <code>RegularGrammar</code> interface.  Each <code>newXXX</code>
 * method allocates and returns a construct used in defining context
 * free grammars.  When complete, the <code>ContextFreeSet</code>
 * object is obtained via the <code>compile()</code> method, which
 * compiles the state of the grammar into a form more amenable to set
 * operations (which are generally implemented using bit vectors).  
 */
public interface ContextFreeGrammar
{
    /**
     * Allocates and returns a new <code>Terminal</code> based on the
     * given <code>Token</code>.  This method is pragmatically
     * particularly important for the following reason: One generally
     * defines a <code>Lexer</code> using a
     * <code>RegularGrammar</code> and a <code>Parser</code> with a
     * <code>ContextFreeGrammar</code>.  By defining our
     * <code>Terminal</code>s based on <code>token</code>s, the link
     * from the <code>Lexer</code> to the <code>Parser</code> is made.
     * In other words, the output of a <code>Lexer</code> (token based
     * in the regular grammar) forms the input for the
     * <code>Parser</code> (as <code>terminal</code>s).  Thus, this
     * method is the compile-time link between the <code>Lexer</code>
     * and <code>Parser</code>.  
     */
    Terminal newTerminal(Token type);

    /**
     * Allocates and returns a new <code>NonTerminal</code> with the
     * given name.  
     */
    NonTerminal newNonTerminal(String name);

    /**
     * Allocates and returns a new <code>Production</code> on the
     * given LHS <code>NonTerminal</code> and predefined length.  
     */
    Production newProduction(NonTerminal nonTerminal);

    /**
     * Returns the <code>Epsilon</code> object if needed.
     */
    Epsilon getEpsilon();

    /**
     * Sets the start production to the given production.
     */
    void setStartProduction(Production start);

    /**
     * When the construction and setup of the context free language
     * elements is complete, <code>compile()</code> returns a
     * <code>ContextFreeSet</code> object which can then be used to
     * construct a <code>DPA</code>.    
     */
    ContextFreeSet compile();
}





