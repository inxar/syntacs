/**
 * $Id: IntSet.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.util;

/**
 * <code>IntSet</code> abstracts a mathematical set of integers.  Sets
 * are generally expected to be <code>Cloneable</code>.
 */
public interface IntSet extends Cloneable {
  /**
   * Puts the given <code>int</code> value in the <code>IntSet</code>.
   */
  void put(int value);

  /**
   * Returns an <code>IntIterator</code> view of the members in the
   * <code>IntSet</code>.
   */
  IntIterator iterator();

  /**
   * All the members of the given other <code>IntSet</code> are added
   * to this <code>IntSet</code>.
   */
  void union(IntSet other);

  /**
   * Returns <code>true</code> if there are no members in this
   * <code>IntSet</code> (the empty set).
   */
  boolean isEmpty();

  /**
   * Returns the number of members in this <code>IntSet</code>.
   */
  int size();

  /**
   * Returns <code>true</code> if the given value is already in this
   * <code>IntSet</code>.
   */
  boolean contains(int value);

  /**
   * Returns the list of set members as an <code>IntArray</code>.
   */
  IntArray toIntArray();

  /**
   * Clones the <code>IntSet</code>.
   */
  Object clone() throws CloneNotSupportedException;
}
