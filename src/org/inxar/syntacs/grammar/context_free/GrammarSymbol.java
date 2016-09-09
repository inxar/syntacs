/**
 * $Id: GrammarSymbol.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

import org.inxar.syntacs.util.IntSet;

/**
 * <code>GrammarSymbol</code> is the base interface for
 * <code>Terminal</code>s and <code>NonTerminal</code>s.  Each
 * <code>GrammarSymbol</code> has a name and is capable of determining
 * its first set and its <code>nullable</code> status.  The
 * <code>isTerminal()</code> method is provided as an alternative for
 * the <code>instanceof</code> operator.  This is syntactically more
 * readable (my opinion) and empirically faster.  
 */
public interface GrammarSymbol
{
    /**
     * The globally assigned integer identifier for this <code>GrammarSymbol</code>.
     */
    int getID();
    
    /**
     * The common name of the <code>GrammarSymbol</code>.
     */
    String getName();

    /**
     * Returns <code>true</code> if this item either is
     * <code>Epsilon</code> (case of <code>Terminal</code>) or derives
     * it (case of <code>NonTerminal</code>).  
     */
    boolean isNullable();

    /**
     * Returns <code>true</code> if this <code>GrammarSymbol</code> is
     * an <code>instanceof Terminal</code> and may safely be cast to a
     * <code>Terminal</code> (or vice versa).  
     */
    boolean isTerminal();

    /**
     * Computes the <code>IntSet</Code> Of <code>terminal</code>s that is
     * the first set for this <code>GrammarSymbol</code>.  
     */
    IntSet getFirstSet();
}


