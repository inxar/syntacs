/**
 * $Id: Sentence.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.syntactic;

import org.inxar.syntacs.analyzer.Symbol;

/**
 * The <code>Sentence</code> interface is essentially a view into the
 * top of the LR parse stack.  For example, assume that the following
 * production has been defined in the grammar: <code>Term :=
 * OPEN_PAREN Expr CLOSE_PAREN</code>.  When the <code>Parser</code>
 * discovers this production and attempts to reduce the parse stack,
 * it passes those three <code>Symbols</code> in a
 * <code>Sentence</code> to the <code>ParserInterpreter</code>.
 * <code>OPEN_PAREN</code> would be located at position
 * <code>0</code>, <code>Expr</code> at position <code>1</code>, and
 * <code>CLOSE_PAREN</code> at position <code>2</code> in the
 * <code>Sentence</code>.  The <code>ParserInterpreter</code> would most
 * likely not care about the <code>PAREN</code> symbols, create a
 * <code>Symbol</code> for <code>Term</code>, and pass it back to the
 * <code>Parser</code>.
 */
public interface Sentence {
  /**
   * Returns the <code>Symbol</code> at the nth visible position of
   * the stack such that position <code>0</code> is the top of the
   * stack.  Therefore, the largest index contains the least
   * recently pushed item (the bottom).  This is useful when
   * indexing the parse stack during a reduction as it mirrors the
   * definition of a production rather then the reverse.
   */
  Symbol at(int index);

  /**
   * Returns the <code>Symbol</code> at the nth visible position of
   * the stack such that position <code>n</code> is the shallowest
   * part of the stack.  Therefore, the largest index contains the
   * most recently pushed item.  This is useful when evaluating left
   * context.
   */
  Symbol get(int index);

  /**
   * Returns the length of the <code>Sentence</code>, aka the
   * exposed portion of the stack for this reduction.  The length of
   * a <code>Sentence</code> is determined by the number of symbols
   * on the right-hand-side of a production.
   */
  int length();
}
