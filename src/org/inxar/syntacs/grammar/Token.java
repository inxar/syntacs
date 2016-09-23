/**
 * $Id: Token.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar;

/**
 * A <code>Token</code> is the compile-time abstraction between
 * different syntax analysis stages in a front end of a compiler,
 * typically the <code>Lexer</code> <code>Parser</code> interface.  As
 * a lexical analyzer runs, it generates <code>Symbol</code> objects.
 * These are run-time tokens, as they are actual sequence instances
 * matched against some pattern.  The <code>Token</code> interface is
 * the abstraction over the pattern (thus the compile-time
 * abstraction).  Thus, if a specific token instance belongs to some
 * language implied by a regular expression, the <code>Token</code>
 * holds that expression.
 *
 * <P>
 *
 * On a more pragmatic level, the <code>Token</code> interface
 * coordinates that lexers and parsers use the same token type numbers
 * (id's).
 */
public interface Token extends Cloneable {
  /**
   * This is the universal token type for the <code>STOP</code>
   * symbol.  The <code>STOP</code> token type is emitted by the
   * <code>Lexer</code> when the end of input has been reached.
   */
  int STOP = 0;

  /**
   * This is the token type used to indicate that the value of a
   * token is unknown, that it has not been set.  This is used as a
   * return value from the <code>DFA.output()</code> method.
   */
  int UNDEF = -1;

  /**
   * This is the token type used for the <code>ERROR</code> symbol.
   * This constant is only used internally by the
   * <code>Lexer</code>.
   */
  int ERROR = -2;

  /**
   * This is the universal token type id that denotes the empty
   * string.
   */
  int EPSILON = -3;

  /**
   * Returns the id given to this <code>Token</code>.
   */
  int getID();

  /**
   * Returns the name given to this <code>Token</code>.
   */
  String getName();

  /**
   * Clones the <code>Token</code>.
   */
  Object clone() throws CloneNotSupportedException;
}
