/**
 * $Id: IntStack.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * <code>IntStack</code> abstracts a last-in-first-out
 * dynamically-growable list of integers. All the methods are standard
 * except for the <code>peel()</code> method, which pops
 * <code>n</code> items off the stack and returns the new "uncovered"
 * top item on the stack (like peeling a banana).
 */
public interface IntStack {
  /**
   * Pushes the given element to the top of the stack.
   */
  void push(int value);

  /**
   * Pops the top element off the stack.
   */
  int pop();

  /**
   * Pops <code>len</code> elements off the stack and returns the
   * top item on the stack (a <code>peek()</code>).
   */
  int peel(int len);

  /**
   * Returns the top element of the stack.
   */
  int peek();

  /**
   * Returns the depth of the stack.
   */
  int size();

  /**
   * Returns <code>true</code> if the stack contains the given
   * value, <code>false</code> otherwise.
   */
  boolean contains(int value);

  /**
   * Returns <code>true</code> if the stack has no elements,
   * <code>false</code> otherwise.
   */
  boolean isEmpty();
}
