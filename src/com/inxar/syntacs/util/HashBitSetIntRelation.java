/**
 * $Id: HashBitSetIntRelation.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.Hashtable;
import java.util.Enumeration;
import org.inxar.syntacs.util.*;

/**
 * Concrete implementation of <code>IntRelation</code> which uses a
 * hash table such that each entry in the table contains a
 * <code>BitSetIntSet</code>.  <code>IntSet</code>, if needed.
 */
public class HashBitSetIntRelation implements IntRelation {
  /**
   * Constructs the <code>HashBitSetIntRelation</code> with the given
   * initial capacity.
   */
  public HashBitSetIntRelation(int capacity) {
    this.hash = new Hashtable(capacity);
    this.keys = new BitSetIntSet();
  }

  /**
   * Constructs the <code>HashBitSetIntRelation</code> with a default
   * initial capacity.
   */
  public HashBitSetIntRelation() {
    this(11);
  }

  public IntSet get(int key) {
    // Changed semantics here!  I Used to return the empty set,
    // but I don't know how much sense that makes.
    return keys.contains(key) ? (IntSet) hash.get(new Integer(key)) : null;
  }

  public void set(int key, IntSet values) {
    keys.put(key);
    hash.put(new Integer(key), values);
  }

  public void put(int key, int value) {
    Integer keyObj = new Integer(key);
    IntSet set = (IntSet) hash.get(keyObj);

    if (set == null) {

      // Case 'virgin':  Haven't seen this key yet.

      // this implementation tries to minimize space by assuming
      // that the majority of sets will have a single member and
      // thus uses singletons to save space
      set = new SingletonIntSet(value);
      hash.put(keyObj, set);
      keys.put(key);

    } else if (set.size() == 1 && set instanceof SingletonIntSet) {

      // Case 'singleton': Is this a singleton? if so, we'll
      // need to promote it to a full-scale bitvector.

      int single = ((SingletonIntSet) set).getValue();

      // promote only if it will make a difference...
      if (single != value) {

        set = new BitSetIntSet(4);
        set.put(single);
        set.put(value);
        hash.put(keyObj, set);
      }

    } else {

      // 'Default' case: already have this set, and it's not a
      // singleton.
      set.put(value);
    }
  }

  public int size() {
    return hash.size();
  }

  public IntSet keys() {
    return new ImmutableIntSet(keys);
  }

  public Reiterator reiterator() {
    return new Hasherator(hash);
  }

  public boolean isEmpty() {
    return hash.isEmpty();
  }

  public String toString() {
    return toBuffer(new StringBuffer()).toString();
  }

  private StringBuffer toBuffer(StringBuffer b) {
    int i = 0;
    Object key = null;
    b.append('{');

    Enumeration e = hash.keys();
    while (e.hasMoreElements()) {
      key = e.nextElement();
      if (i++ > 0) b.append(", ");
      b.append('(').append(key).append(',').append(hash.get(key)).append(')');
    }

    b.append('}');
    return b;
  }

  private IntSet keys;
  private Hashtable hash;

  private static class Hasherator implements Reiterator {
    Hasherator(Hashtable hash) {
      this.hash = hash;
      this.keys = hash.keys();
    }

    public boolean hasNext() {
      boolean hasNext = keys.hasMoreElements();
      if (hasNext) {
        Integer _key = (Integer) keys.nextElement();
        key = _key.intValue();
        values = (IntSet) hash.get(_key);
      }
      return hasNext;
    }

    public void next() {}

    public int key() {
      return key;
    }

    public IntSet values() {
      return values;
    }

    int key;
    IntSet values;
    Hashtable hash;
    Enumeration keys;
  }

  private static class EnumIter implements IntIterator {
    EnumIter(java.util.Enumeration e) {
      this.e = e;
    }

    public boolean hasNext() {
      return e.hasMoreElements();
    }

    public int next() {
      return ((Integer) e.nextElement()).intValue();
    }

    private java.util.Enumeration e;
  }
}
