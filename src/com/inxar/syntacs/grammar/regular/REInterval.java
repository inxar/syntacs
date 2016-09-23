/**
 * $Id: REInterval.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.grammar.regular.Interval;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntIterator;
import com.inxar.syntacs.util.BitSetIntSet;
import com.inxar.syntacs.util.SingletonIntSet;

/**
 * Standard <code>Interval</code> implementation.
 */
public class REInterval implements Interval {

  /**
   * Constructs the <code>REInterval</code> on the given
   * <code>REGrammar</code>, lo and hi interval points, and the
   * assigned ID number.
   */
  public REInterval(REGrammar grammar, int ID, int lo, int hi) {
    this.grammar = grammar;
    this.ID = ID;
    this.lo = lo;
    this.hi = hi;

    // make a new single-element set which will be used for both
    // the first and last sets
    this.set = new SingletonIntSet(ID);
  }

  // Concrete terminals (symbols and the like) are never nullable
  public boolean isNullable() {
    return false;
  }

  public IntSet getFirstSet() {
    return set;
  }

  public IntSet getLastSet() {
    return set;
  }

  public IntSet getFollowSet() {
    // the follow set is really just a container that is attached
    // to the interval object.  Other entities do the actual work
    // of filling it with stuff.  All we are responsible for here
    // is simply to return a viable set object.  Initially, the
    // set is empty.
    if (follow == null) follow = new BitSetIntSet(1);

    return follow;
  }

  public void follow() {
    // do nothing?
  }

  public IntIterator iterator() {
    return new IntervalIterator(lo, hi);
  }

  public boolean isTerminator() {
    return false;
  }

  public boolean equals(Object other) {
    if (other == null || !(other instanceof Interval)) return false;

    Interval that = (Interval) other;
    return this.lo == that.lo() && this.hi == that.hi();
  }

  public Object clone() throws CloneNotSupportedException {
    return grammar.newInterval(lo, hi);
  }

  public String toString() {
    if (lo == hi) return "<" + ID + ":" + lo + ">";
    else return "<" + ID + ":" + lo + "," + hi + ">";
  }

  public boolean includes(int c) {
    //System.out.println("checking inclusion of this "+this+" with c "+c);
    return c <= hi && c >= lo;
  }

  public int lo() {
    return lo;
  }

  public int hi() {
    return hi;
  }

  public int getID() {
    return ID;
  }

  // ******************************************************
  // INSTANCE FIELDS
  // ******************************************************
  int ID, lo, hi; // obvious
  IntSet set, follow; // the first *and* last set; the follow set
  REGrammar grammar; // our grammar, of course

  /**
   * An iterator which runs over the int interval.
   */
  private static final class IntervalIterator implements IntIterator {
    IntervalIterator(int lo, int hi) {
      this.index = lo;
      this.max = hi;
    }

    public boolean hasNext() {
      return index <= max;
    }

    public int next() {
      return index++;
    }

    private int max;
    private int index;
  }
}
