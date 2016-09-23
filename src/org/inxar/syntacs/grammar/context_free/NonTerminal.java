/**
 * $Id: NonTerminal.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
 *
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.inxar.syntacs.grammar.context_free;

import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntArray;

/**
 * The <code>NonTerminal</code> interface abstracts the symbol on the
 * left-hand side of a <code>Production</code>.  More information
 * about what a <code>NonTerminal</code> represents see Chapter 4 in
 * the Dragon Book.
 */
public interface NonTerminal extends GrammarSymbol {
  /**
   * Returns the set of Item ID's which mention this
   * <code>NonTerminal</code> as an <code>IntArray</code>.  This is
   * useful in the calculation of follow sets.  Each member in the
   * array corresponds to the ID of an <code>Item</code> [ <i>a</i>
   * dot <i>b</i> ] where <i>a</i> is equal to this
   * <code>NonTerminal</code>.
   */
  IntArray getProductionItems();

  /**
   * Returns an <code>IntArray</code> of <code>Productions</code>
   * which have this <code>NonTerminal</code> as the left hand side.
   */
  IntArray getReductions();
}
