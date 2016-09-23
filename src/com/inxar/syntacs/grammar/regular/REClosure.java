/**
 * $Id: REClosure.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntIterator;

/**
 * Standard <code>Closure</code> implementation.
 */
public class REClosure implements Closure {

  /**
   * Constructs the <code>REClosure</code> on the given
   * <code>REGrammar</code> and input <code>RegularExpression</code>.
   */
  public REClosure(REGrammar grammar, RegularExpression regex) {
    this.grammar = grammar;
    this.regex = regex;
  }

  public String toString() {
    return new StringBuffer().append(regex).append('*').toString();
  }

  public boolean isNullable() {
    return true;
  }

  public IntSet getFirstSet() {
    if (first == null) first = regex.getFirstSet();
    return first;
  }

  public IntSet getLastSet() {
    if (last == null) last = regex.getLastSet();
    return last;
  }

  public void follow() {
    IntSet firsts = regex.getFirstSet();
    IntIterator lasts = regex.getLastSet().iterator();
    while (lasts.hasNext()) {
      grammar.getInterval(lasts.next()).getFollowSet().union(firsts);
    }
    regex.follow();
  }

  public RegularExpression getInternal() {
    return regex;
  }

  public Object clone() throws CloneNotSupportedException {
    // clone self
    REClosure clone = (REClosure) super.clone();
    // clone the regex
    clone.regex = (RegularExpression) regex.clone();
    // done
    return clone;
  }

  private RegularExpression regex;
  private REGrammar grammar;
  private IntSet first;
  private IntSet last;
}
