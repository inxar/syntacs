/**
 * $Id: CharClass.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

/**
 * The <code>CharClass</code> interface is another 'utility' interface
 * for assembling complex <code>Unions</code>.  A Character class is
 * known by the (example) syntax [^a-z], which reads 'any single
 * character other than lowercase a to z'.
 */
public interface CharClass extends RegularExpression {
  /**
   * Returns <code>true</code> if this is a 'negated' character
   * class, grammatically symbolized by the caret '^'.
   */
  boolean isNegated();

  /**
   * Sets the negate flag.  The default is <code>false</code>.
   */
  void isNegated(boolean value);

  /**
   * Adds the <code>given</code> char to the list of alternatives.
   *
   */
  void add(char c);

  /**
   * Adds the given range of characters to the character class.  For
   * example, 'a-z' would be encoded by a the call
   * <code>CharClass.add('a','z');</code>
   */
  void add(char lo, char hi);
}
