/**
 * $Id: REConcatenation.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import com.inxar.syntacs.util.BitSetIntSet;

/**
 * Standard <code>Concatenation</code> implementation.
 */
public class REConcatenation implements Concatenation {
  /**
   * Constructs the <code>REConcatenation</code> on the given
   * <code>REGrammar</code> and left & right
   * <code>RegularExpression</code> inputs.
   */
  public REConcatenation(REGrammar grammar, RegularExpression left, RegularExpression right) {
    super();
    this.grammar = grammar;
    this.left = left;
    this.right = right;
  }

  public String toString() {
    return new StringBuffer().append('(').append(left).append(right).append(')').toString();
  }

  public boolean isNullable() {
    return left.isNullable() && right.isNullable();
  }

  public IntSet getFirstSet() {
    if (first == null) {
      if (left.isNullable()) {
        first = new BitSetIntSet(11);
        first.union(left.getFirstSet());
        first.union(right.getFirstSet());
      } else {
        first = left.getFirstSet();
      }
    }
    return first;
  }

  public IntSet getLastSet() {
    if (last == null) {
      if (right.isNullable()) {
        last = new BitSetIntSet(11);
        last.union(right.getLastSet());
        last.union(left.getLastSet());
      } else {
        last = right.getLastSet();
      }
    }
    return last;
  }

  public void follow() {
    left.follow();

    // fetch firstpos(c2)
    IntSet firsts = right.getFirstSet();

    //System.out.println("REConcatenation.follow(): firstpos(c2) for "+this+": "+firsts);

    // get a list of all the intervals for lastpos(c1)
    IntIterator lasts = left.getLastSet().iterator();

    //System.out.println("REConcatenation.follow(): lastpos(c1) for "+this+": "+lasts);

    // if i is a member in lastpos(c1) then all positions in
    // firstpos(c2) are in followpos(i)
    while (lasts.hasNext()) {
      grammar.getInterval(lasts.next()).getFollowSet().union(firsts);
    }

    right.follow();
  }

  public Object clone() throws CloneNotSupportedException {
    // clone self
    REConcatenation clone = (REConcatenation) super.clone();
    // clone the regex
    clone.left = (RegularExpression) this.left.clone();
    clone.right = (RegularExpression) this.right.clone();
    // done
    return clone;
  }

  public RegularExpression getLeft() {
    return left;
  }

  public RegularExpression getRight() {
    return right;
  }

  private RegularExpression left;
  private RegularExpression right;
  private REGrammar grammar;
  private IntSet first;
  private IntSet last;
}
