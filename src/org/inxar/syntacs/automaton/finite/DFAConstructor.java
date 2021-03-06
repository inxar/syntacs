/**
 * $Id: DFAConstructor.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.automaton.finite;

import org.inxar.syntacs.grammar.regular.RegularSet;

/**
 * Algorithms which are capable of generating a <code>DFA</code> from
 * a <code>RegularSet</code> can be encapsulated by the
 * <code>DFAConstructor</code> interface.
 */
public interface DFAConstructor {
  /**
   * Constructs a <code>DFA</code> from the given
   * <code>RegularSet</code>.
   */
  DFA construct(RegularSet grammar);
}
