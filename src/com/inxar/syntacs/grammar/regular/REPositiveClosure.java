/**
 * $Id: REPositiveClosure.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.regular;

import org.inxar.syntacs.grammar.regular.RegularExpression;
import org.inxar.syntacs.grammar.regular.Concatenation;
import org.inxar.syntacs.grammar.regular.Closure;
import org.inxar.syntacs.grammar.regular.PositiveClosure;
import org.inxar.syntacs.util.IntSet;

/**
 * Standard <code>PositiveClosure</code> implementation.
 */
public class REPositiveClosure implements PositiveClosure {
  /**
   * Constructs the <code>REPositiveClosure</code> on the given
   * <code>REGrammar</code> and input <code>RegularExpression</code>.
   */
  public REPositiveClosure(REGrammar grammar, RegularExpression internal) {
    this.grammar = grammar;
    this.internal = internal;
    init();
  }

  private void init() {
    try {
      // r+ >> rr*
      Closure closure = grammar.newClosure((RegularExpression) internal.clone());
      regex = grammar.newConcatenation(internal, closure);
    } catch (CloneNotSupportedException cnsex) {
      cnsex.printStackTrace();
    }
  }

  public String toString() {
    return regex.toString();
    //return internal.toString() + "+";
  }

  public boolean isNullable() {
    return regex.isNullable();
  }

  public IntSet getFirstSet() {
    return regex.getFirstSet();
  }

  public IntSet getLastSet() {
    return regex.getLastSet();
  }

  public void follow() {
    regex.follow();
  }

  public RegularExpression getInternal() {
    return internal;
  }

  public Object clone() throws CloneNotSupportedException {
    // clone self
    REPositiveClosure clone = (REPositiveClosure) super.clone();
    // clone the internal
    clone.internal = (RegularExpression) this.internal.clone();
    clone.regex = (Concatenation) this.regex.clone();
    // done
    return clone;
  }

  private Concatenation regex;
  private RegularExpression internal;
  private REGrammar grammar;
}
