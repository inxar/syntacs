/**
 * $Id: Action.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.automaton.pushdown;

/**
 * This is an abstraction of a <code>DPA</code> parse 'action'.  Each
 * action has an id, a type (one of the constants in this class), and
 * a value which may be used for two purposes.  If
 * <code>action.getType() == SHIFT</code>, the value is used to hold
 * the next state to shift to, else if <code>action.getType() ==
 * REDUCE</code>, the value is used to hold the number of the
 * <code>Production</code> to reduce.
 */
public interface Action {
  /**
   * The <code>ERROR</code> instruction type.
   */
  int ERROR = 0;

  /**
   * The <code>SHIFT</code> instruction type.
   */
  int SHIFT = 1;

  /**
   * The <code>REDUCE</code> instruction type.
   */
  int REDUCE = 2;

  /**
   * The <code>ACCEPT</code> instruction type.
   */
  int ACCEPT = 3;

  /**
   * The type of this action, one of (ERROR|SHIFT|REDUCE|ACCEPT).
   */
  int getType();

  /**
   * A generic container for additional action information.
   */
  int getValue();
}
