/**
 * $Id: IntArrayIterator.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * Concrete implementation of <code>IntIterator</code> which steps an
 * <code>int[]</code> array.
 */
public class IntArrayIterator implements IntIterator {
  /**
   * Creates an iterator over the values in the given
   * <code>IntArray</code>. The <code>IntArray.toArray()</code> is
   * called to "guarantee" a clean arraycopy.
   */
  public IntArrayIterator(IntArray src) {
    this.src = src.toArray();
  }

  public IntArrayIterator(int[] src) {
    this(src, 0, src.length);
  }

  public IntArrayIterator(int[] src, int len) {
    this(src, 0, len);
  }

  public IntArrayIterator(int[] src, int off, int len) {
    int[] dst = new int[len];
    System.arraycopy(src, off, dst, 0, len);
    this.src = dst;
  }

  public boolean hasNext() {
    return off < src.length;
  }

  public int next() {
    return src[off++];
  }

  private int off;
  private int[] src;
}
