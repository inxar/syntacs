/**
 * $Id: EmptyIntSet.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete implementation of <code>IntSet</code> which may never contain
 * elements.  Used when a <code>IntSet</code> will always be empty and
 * you want to conserve memory.  Any attempt to modify the set will
 * result in a <code>RuntimeException</code>.
 */
public class EmptyIntSet implements IntSet, IntIterator, IntArray {
  /**
   * Returns a statically cached copy of the <code>EmptyIntSet</code>.
   */
  public static final IntSet EMPTY_SET = new EmptyIntSet();

  private EmptyIntSet() {}

  public boolean contains(int id) {
    return false;
  }

  public boolean isEmpty() {
    return true;
  }

  public void put(int id) {
    throw new ArrayIndexOutOfBoundsException("IntSet is empty");
  }

  public void put(IntIterator iter) {
    throw new ArrayIndexOutOfBoundsException("IntSet is empty");
  }

  public IntIterator iterator() {
    return this;
  }

  public void union(IntSet other) {
    throw new ArrayIndexOutOfBoundsException("IntSet is empty");
  }

  public int size() {
    return 0;
  }

  public int length() {
    return 0;
  }

  public int at(int index) {
    throw new ArrayIndexOutOfBoundsException("Empty Set");
  }

  public boolean equals(Object other) {
    if (this == other) return true;

    if (other == null || !(other instanceof IntSet)) return false;

    IntSet that = (IntSet) other;

    // if the other set is empty, then they are equal
    return that.isEmpty();
  }

  public int hashCode() {
    return 127;
  }

  public IntArray toIntArray() {
    return this;
  }

  public int[] toArray() {
    return emptyArray;
  }

  public String toString() {
    return "{}";
  }

  public Object clone() throws CloneNotSupportedException {
    return this;
  }

  public boolean hasNext() {
    return false;
  }

  public int next() {
    throw new ArrayIndexOutOfBoundsException("IntSet is empty");
  }

  private static final int[] emptyArray = new int[0];
}
