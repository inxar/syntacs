/**
 * $Id: ArrayIntFunction.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete implementation of <code>IntFunction</code> which uses an
 * array internally to store values and an <code>IntSet</code> to
 * track keys.
 */
public class ArrayIntFunction implements IntFunction {
  /**
   * Constructs a new <code>ArrayIntFunction</code>.
   */
  public ArrayIntFunction(int capacity) {
    this.src = new int[11];
    this.keys = new ListIntSet();
  }

  public void put(int key, int value) {
    if (keys.contains(key)) src[key] = value;
    else {
      keys.put(key);
      while (key >= src.length) enlarge();
      src[key] = value;
    }
  }

  public int get(int key) {
    if (keys.contains(key)) return src[key];
    else throw new IllegalArgumentException("Unknown key: " + key);
  }

  public IntSet keys() {
    return new ImmutableIntSet(keys);
  }

  private void enlarge() {
    int[] dst = new int[src.length * 2];
    System.arraycopy(src, 0, dst, 0, src.length);
    src = dst;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append('{');

    IntIterator i = keys.iterator();
    int key = -1;
    while (i.hasNext()) {
      if (key == -1) b.append(',');
      key = i.next();
      b.append(key).append('=').append(src[key]);
    }

    return b.append('}').toString();
  }

  public Object clone() throws CloneNotSupportedException {
    ArrayIntFunction clone = (ArrayIntFunction) super.clone();
    clone.src = (int[]) this.src.clone();
    clone.keys = (IntSet) this.keys.clone();
    return clone;
  }

  private int[] src;
  private IntSet keys;
}
