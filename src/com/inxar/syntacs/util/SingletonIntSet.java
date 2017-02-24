/**
 * $Id: SingletonIntSet.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>IntSet</code> which may only hold a
 * single element.
 */
public class SingletonIntSet implements IntSet, IntIterator, IntArray {

  public static IntSet of(int val) {
    return new SingletonIntSet(val);
  }

  /**
   * Constructs the <code>SingletonIntSet</code> on the given singleton
   * value.  This value cannot change over the life of the
   * <code>SingletonIntSet</code>.
   */
  public SingletonIntSet(int val) {
    this.val = val;
  }

  public boolean contains(int val) {
    return this.val == val;
  }

  public boolean isEmpty() {
    return false;
  }

  public IntSet put(int val) {
    throw new RuntimeException("Uncle!");
  }

  public IntSet put(IntIterator iter) {
    throw new RuntimeException("Uncle!");
  }

  public IntIterator iterator() {
    called = false;
    return this;
  }

  public IntSet union(IntSet other) {
    throw new RuntimeException("Uncle!");
  }

  public int size() {
    return 1;
  }

  public int length() {
    return 1;
  }

  public int at(int index) {
    if (index != 0) throw new ArrayIndexOutOfBoundsException();

    return val;
  }

  public int[] toArray() {
    return new int[] {val};
  }

  public IntArray toIntArray() {
    return this;
  }

  public boolean equals(Object other) {
    if (other == null || !(other instanceof IntSet)) return false;

    if (this == other) return true;

    if (other instanceof SingletonIntSet) {
      SingletonIntSet that = (SingletonIntSet) other;
      return this.val == that.val;
    } else {
      IntSet that = (IntSet) other;

      if (that.size() != 1) return false;

      return this.val == that.iterator().next();
    }
  }

  public int hashCode() {
    return 127 + val;
  }

  public String toString() {
    return "{" + val + "}";
  }

  public Object clone() throws CloneNotSupportedException {
    // clone self
    return super.clone();
  }

  public boolean hasNext() {
    return called ? false : true;
  }

  public int next() {
    called = true;
    return val;
  }

  public int getValue() {
    return val;
  }

  private int val;
  private boolean called;
}
