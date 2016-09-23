/**
 * $Id: StandardRecovery.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer.syntactic;

import java.util.List;
import java.util.LinkedList;

import org.inxar.syntacs.analyzer.syntactic.Correction;
import org.inxar.syntacs.analyzer.syntactic.Recovery;

/**
 * Concrete implementation of <code>Recovery</code>.
 */
public class StandardRecovery implements Recovery {
  private static final boolean DEBUG = true;

  /*
   * Constructs a new parser.
   */
  public StandardRecovery() {
    this.list = new LinkedList();
    this.index = 0;
  }

  public boolean hasNext() {
    return index < list.size();
  }

  public Correction next() {
    return (Correction) list.get(index++);
  }

  public void add(int type, Object val) {
    list.add(new StandardCorrection(type, val));
  }

  public void add(int type) {
    list.add(new StandardCorrection(type));
  }

  private List list;
  private int index;

  private static class StandardCorrection implements Correction {
    StandardCorrection(int type) {
      this.type = type;
    }

    StandardCorrection(int type, Object val) {
      this.type = type;
      this.val = val;
    }

    public int getType() {
      return type;
    }

    public Object getValue() {
      return val;
    }

    public String toString() {
      switch (type) {
        case Correction.WAIT:
          return "WAIT";
        case Correction.TUMBLE:
          return "TUMBLE";
      }
      return "UNKNOWN";
    }

    private int type;
    private Object val;
  }
}
