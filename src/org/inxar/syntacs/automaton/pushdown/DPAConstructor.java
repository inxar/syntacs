/**
 * $Id: DPAConstructor.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

import org.inxar.syntacs.util.AlgorithmException;
import org.inxar.syntacs.grammar.context_free.ContextFreeSet;

/**
 * The <code>DPAConstructor</code> interface abstracts algorithms
 * which are capable of transforming a <code>ContextFreeSet</code>
 * into a <code>DPA</code>.
 */
public interface DPAConstructor {
  /**
   * Constructs a <code>DPA</code>.
   */
  DPA construct(ContextFreeSet grammar) throws AlgorithmException;
}
