/**
 * $Id: IntRelation.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * A <code>IntRelation</code> is a mapping from <code>int</code> to
 * <code>IntSet</code>.
 */
public interface IntRelation {
  /**
   * Returns the set of keys.
   */
  IntSet keys();

  /**
   * Returns the set under the given key.
   */
  IntSet get(int key);

  /**
   * Puts the given value in the set under the given key.
   */
  void put(int key, int value);

  /**
   * Assigns the given set to the given key.
   */
  void set(int key, IntSet values);

  /**
   * Returns <code>true</code> if the set of keys is the empty set.
   */
  boolean isEmpty();

  /**
   * Returns a Reiterator view.
   */
  Reiterator reiterator();
}
