/**
 * $Id: LR1Item.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar.context_free;

/**
 * An <code>LR1Item</code> is a more specialized type of
 * <code>Item</code> that contains lookahead information pertinent to
 * the generation of LR-grammars by certain algorithms.
 */
public interface LR1Item extends Item {
  /**
   * Returns the <code>Terminal</code> symbol which may follow this
   * <code>Item</code>.
   */
  Terminal getLookahead();

  /**
   * Returns the <code>Item</code> which represents the core of this
   * <code>Item</code>.
   */
  Item getCore();
}
