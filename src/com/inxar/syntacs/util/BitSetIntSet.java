/**
 * $Id: BitSetIntSet.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.util;

import java.util.BitSet;
import org.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>IntSet</code> which uses a
 * <code>java.util.BitSet</code> internally.
 */
public class BitSetIntSet implements IntSet {
  private static final boolean DEBUG = false;

  /**
   * Constructs the <code>BitSetIntSet</code> with a default initial
   * length of 17.
   */
  public BitSetIntSet() {
    this(17);
  }

  /**
   * Constructs the <code>BitSetIntSet</code> with the given initial
   * length.
   */
  public BitSetIntSet(int length) {
    this.bits = new BitSet(length);
    hasChanged = true;
  }

  public boolean contains(int value) {
    return bits.get(value);
  }

  public boolean isEmpty() {
    if (hasChanged) recompile();

    return vals.length() == 0;
  }

  public void put(int value) {
    if (!bits.get(value)) {
      bits.set(value);
      hasChanged = true;
    }
  }

  public void put(IntIterator iter) {
    while (iter.hasNext()) put(iter.next());
  }

  public void union(IntSet other) {
    if (other == this || other == null) return;

    if (DEBUG) trace(this.toString() + " | " + other.toString());

    if (other instanceof BitSetIntSet) {

      BitSetIntSet that = (BitSetIntSet) other;
      this.bits.or(that.bits);
      hasChanged = true;

    } else {
      put(other.iterator());
    }
  }

  public IntIterator iterator() {
    if (hasChanged) recompile();

    return vals.iterator();
  }

  private void recompile() {
    vals = new ArrayIntList();

    int len = bits.length();
    for (int i = 0; i < len; i++) if (bits.get(i)) vals.add(i);

    hasChanged = false;
  }

  private void trace(String msg) {
    //log.write(Log.TRACE, msg);
  }

  public int size() {
    if (hasChanged) recompile();

    return vals.length();
  }

  public boolean equals(Object other) {
    if (this == other) return true;

    if (other == null || !(other instanceof IntSet)) return false;

    if (other instanceof BitSetIntSet) {

      return this.bits.equals(((BitSetIntSet) other).bits);

    } else {

      IntSet that = (IntSet) other;

      if (this.size() != that.size()) return false;

      IntIterator members = that.iterator();

      while (members.hasNext()) if (!this.bits.get(members.next())) return false;

      return true;
    }
  }

  public IntArray toIntArray() {
    if (hasChanged) recompile();

    return vals;
  }

  public int hashCode() {
    return isEmpty() ? 127 : bits.hashCode();
  }

  public String toString() {
    return bits.toString();
  }

  public Object clone() throws CloneNotSupportedException {
    BitSetIntSet clone = (BitSetIntSet) super.clone();
    clone.bits = (BitSet) this.bits.clone();
    if (this.vals != null) clone.vals = (IntList) this.vals.clone();
    return clone;
  }

  private BitSet bits;
  private IntList vals;
  private boolean hasChanged;
}
