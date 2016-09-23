/**
 * $Id: ArraySymbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer;

import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.analyzer.Symbol;
import com.inxar.syntacs.util.Tree;

/**
 * Concrete list implementation of <code>Symbol</code> which has a
 * fixed length.
 */
public class ArraySymbol extends AbstractSymbol {
  /**
   * Constructs the <code>ArraySymbol</code> with the given array
   * length given by <code>len</code>.
   */
  public ArraySymbol(int len) {
    this(Token.ERROR, len);
  }

  /**
   * Constructs the <code>ArraySymbol</code> with the given type and
   * array length given by <code>len</code>.
   */
  public ArraySymbol(int type, int len) {
    super(type);
    this.as = new Symbol[len];
    this.idx = 0;
  }

  /**
   * Adds the given symbol to the next empty slot in the
   * <code>Symbol</code> array.
   */
  public void add(Symbol s) {
    as[idx++] = s;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    b.append('[').append(type).append(':');

    if (as.length == 1) b.append(as[0]);
    else
      for (int i = 0; i < as.length; i++) {
        if (i > 0) b.append(',');
        b.append(as[i]);
      }
    b.append(']');

    return b.toString();
  }

  public void toTree(Tree t) {
    if (as.length == 1) {
      ((AbstractSymbol) as[0]).toTree(t);
    } else {
      t = t.add("@");

      for (int i = 0; i < as.length; i++) {
        if (as[i] instanceof AbstractSymbol) ((AbstractSymbol) as[i]).toTree(t);
        else t.add(as[i].toString());
      }
    }
  }

  /**
   * The array which holds the child <code>Symbol</code>s within
   * this <code>Symbol</code>.
   */
  public Symbol[] as;

  // used by add()
  private int idx;
}
