/**
 * $Id: RegularSet.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar.regular;

import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.grammar.Token;

/**
 * The <code>RegularSet</code> interface is an optimized version of
 * the <code>RegularGrammar</code> object generally used by
 * <code>DFA</code> construction algorithms.  By supplying a 'first
 * set' and acting as a repository for <code>Interval</code> object
 * accessible by id, a <code>DFA</code> can be constructed.
 */
public interface RegularSet {
  /**
   * Returns the parent <code>RegularGrammar</code>.
   */
  RegularGrammar getRegularGrammar();

  /**
   * Returns the globally allocated ID for this
   * <code>RegularSet</code>.  This is useful for lexer construction
   * algorithms which use "start states" or multiple DFA's and
   * switch between them.
   */
  int getID();

  /**
   * Returns the name of this <code>RegularSet</code>.  This is
   * generally relevant only when
   * <code>PushdownRegularGrammars</code> are being created.
   */
  String getName();

  /**
   * Returns the <code>Interval</code> by the given id.
   */
  Interval getInterval(int id);

  /**
   * Returns the number of <code>Intervals</code> used by the
   * grammar.
   */
  int intervals();

  /**
   * Returns the <code>IntSet</code> of intervals which are visible
   * from some abstract initial state.  This is the first set over
   * the collection of tokens (each with its regular expression).
   * Therefore, this set represents the first state in a
   * <code>DFA</code>.
   */
  IntSet getStart();

  /**
   * Returns the <code>Token</code> with the given id.
   */
  Token getToken(int id);

  /**
   * Returns the number of tokens in the <code>RegularSet</code>.
   */
  int tokens();
}
