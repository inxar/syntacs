/**
 * $Id: REUnion.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.regular;

import org.inxar.syntacs.grammar.regular.*;
import org.inxar.syntacs.util.IntSet;
import com.inxar.syntacs.util.BitSetIntSet;

/**
 * Standard <code>Union</code> implementation.
 */
public class REUnion implements Union {
  /**
   * Constructs the <code>REUnion</code>.
   */
  public REUnion() {
    this.regexes = new java.util.Vector();
  }

  public Union addAllele(RegularExpression e) {
    regexes.addElement(e);
    return this;
  }

  public String toString() {
    // make new tmp
    StringBuffer buf = new StringBuffer();

    // front matter
    buf.append('(');

    // middle matter

    for (int i = 0; i < regexes.size(); i++) {
      if (i > 0) buf.append("|");
      buf.append(regexes.elementAt(i).toString());
    }

    // back matter and done
    return buf.append(')').toString();
  }

  public boolean isNullable() {
    if (isNullable == null) {

      // initialize as false
      isNullable = Boolean.FALSE;
      // search for nullable, assign true if find one
      for (int i = 0; i < regexes.size(); i++) {
        if (((RegularExpression) regexes.elementAt(i)).isNullable()) {
          isNullable = Boolean.TRUE;
          break;
        }
      }
    }

    //System.out.println("union "+this+" nullable? "+isNullable.booleanValue());
    return isNullable.booleanValue();
  }

  public IntSet getFirstSet() {
    if (first == null) {
      first = new BitSetIntSet(11);

      for (int i = 0; i < regexes.size(); i++)
        first.union(((RegularExpression) regexes.elementAt(i)).getFirstSet());
    }
    return first;
  }

  public IntSet getLastSet() {
    if (last == null) {
      last = new BitSetIntSet(11);

      for (int i = 0; i < regexes.size(); i++) {
        last.union(((RegularExpression) regexes.elementAt(i)).getLastSet());
      }
    }
    return last;
  }

  public void follow() {
    for (int i = 0; i < regexes.size(); i++) {
      ((RegularExpression) regexes.elementAt(i)).follow();
    }
  }

  public int alleles() {
    return regexes.size();
  }

  public RegularExpression getAllele(int index) {
    return (RegularExpression) regexes.elementAt(index);
  }

  public RegularExpression[] getAlleles() {
    // count em
    int len = regexes.size();
    // make new array
    RegularExpression[] dst = new RegularExpression[len];
    // copy from vector
    for (int i = 0; i < len; i++) dst[i] = (RegularExpression) regexes.elementAt(i);
    // return finished.
    return dst;
  }

  public Object clone() throws CloneNotSupportedException {
    // clone self
    REUnion clone = (REUnion) super.clone();

    // drop the old vector and make new one
    clone.regexes = new java.util.Vector();

    // clone each member in the vector
    for (int i = 0; i < this.regexes.size(); i++) {
      // get each regex from this, clone it, add to clones' vector
      clone.regexes.addElement(((RegularExpression) this.regexes.elementAt(i)).clone());
    }

    // done
    return clone;
  }

  private IntSet first;
  private IntSet last;
  private Boolean isNullable;
  private java.util.Vector regexes;
}
