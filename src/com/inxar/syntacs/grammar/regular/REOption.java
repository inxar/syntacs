/**
 * $Id: REOption.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.syntacs.grammar.regular.Union;
import org.inxar.syntacs.grammar.regular.Option;
import org.inxar.syntacs.util.IntSet;

/**
 * Standard <code>Option</code> implementation.
 */
public class REOption implements Option {
  /**
   * Constructs the <code>REOption</code> on the given
   * <code>REGrammar</code> and input
   * <code>RegularExpression</code>.
   */
  public REOption(REGrammar grammar, RegularExpression internal) {
    this.grammar = grammar;
    this.internal = internal;
    init();
  }

  public String toString() {
    //return internal.toString() + "?";
    return regex.toString();
  }

  private void init() {
    // r? >> r|e
    regex = grammar.newUnion();
    regex.addAllele(internal);
    regex.addAllele(grammar.getEpsilon());
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
    REOption clone = (REOption) super.clone();
    // clone the internal
    clone.internal = (RegularExpression) this.internal.clone();
    clone.regex = (Union) this.regex.clone();
    // done
    return clone;
  }

  private Union regex;
  private RegularExpression internal;
  private REGrammar grammar;
}
