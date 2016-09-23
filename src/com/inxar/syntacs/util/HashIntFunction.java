/**
 * $Id: HashIntFunction.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.*;
import org.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>IntFunction</code> which uses a
 * hash table to maintain the association between key and value.
 */
public class HashIntFunction implements IntFunction {
  /**
   * Constructs the <code>HashIntFunction</code> with a default
   * initial capacity.
   */
  public HashIntFunction() {
    this.hash = new Hashtable();
    this.hasChanged = true;
  }

  /**
   * Constructs the <code>HashIntFunction</code> with the given
   * initial capacity.
   */
  public HashIntFunction(int capacity) {
    this.hash = new Hashtable(capacity);
    this.hasChanged = true;
  }

  public int get(int x) {
    Object o = hash.get(new Integer(x));
    return o != null ? ((Integer) o).intValue() : 0;
  }

  public void put(int x, int value) {
    hash.put(new Integer(x), new Integer(value));
    hasChanged = true;
  }

  public int size() {
    return hash.size();
  }

  public IntSet keys() {
    if (hasChanged) {

      if (keys == null) keys = new BitSetIntSet();

      Enumeration e = hash.keys();
      while (e.hasMoreElements()) {
        Integer i = (Integer) e.nextElement();
        keys.put(i.intValue());
      }

      hasChanged = false;
    }

    return keys;
  }

  public String toString() {
    return hash.toString();
  }

  private Hashtable hash;
  private boolean hasChanged;
  private IntSet keys;
}
