/**
 * $Id: ContextFreeSet.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

/**
 * The <code>ContextFreeSet</code> interface is an optimized version
 * of the <code>ContextFreeGrammar</code> object used by
 * <code>DPA</code> construction algorithms.  The absolute number of
 * each type of element is available via the named method like
 * '<code>terminals()</code>'.  The start <code>Production</code> is
 * available via the method of the same name.  Although the name
 * '<code>ContextFreeSet</code>' is not a term that is used often (or
 * at all, for that matter) in theoretical books about these kinds of
 * things, it makes a nice parallel to <code>RegularSet</code>, which
 * is a relatively common term.  
 */
public interface ContextFreeSet
{
    /**
     * Returns the parent <code>ContextFreeGrammar</code>.
     */
    ContextFreeGrammar getContextFreeGrammar();

    /**
     * Returns the number of <code>Terminal</code>s in the grammar.
     */
    int terminals();

    /**
     * Returns the number of <code>NonTerminal</code>s in the grammar.
     */
    int nonTerminals();

    /**
     * Returns the number of <code>Production</code>s in the grammar.
     */
    int productions();

    /**
     * Returns the number of <code>Item</code>s in the grammar.
     */
    int items();

    /**
     * Returns the <code>Terminal</code> with the given id.  If the id
     * is not known, implementations may either return
     * <code>null</code> or throw a <code>RuntimeException</code>.  
     */
    Terminal getTerminal(int id);

    /**
     * Returns the <code>NonTerminal</code> with the given id.  If the
     * id is not known, implementations may either return
     * <code>null</code> or throw a <code>RuntimeException</code>.  
     */
    NonTerminal getNonTerminal(int id);

    /**
     * Returns the <code>Production</code> with the given id.  If the
     * id is not known, implementations may either return
     * <code>null</code> or throw a <code>RuntimeException</code>.  
     */
    Production getProduction(int id);

    /**
     * Returns the <code>GrammarSymbol</code> with the given id.  If
     * the id is not known, implementations may either return
     * <code>null</code> or throw a <code>RuntimeException</code>.  
     */
    GrammarSymbol getGrammarSymbol(int id);

    /**
     * Returns the <code>Item</code> with the given id.  If the id is
     * not known, implementations may either return <code>null</code>
     * or throw a <code>RuntimeException</code>.  
     */
    Item getItem(int id);

    /**
     * Returns the <code>Production</code> designated as the start.
     * Note that this production is generally not the same as the one
     * given in
     * <code>ContextFreeLanguage.toGrammar(Production)</code>, as the
     * start <code>Production</code> is typically 'augmented'.  
     */
    Production getStart();
}


