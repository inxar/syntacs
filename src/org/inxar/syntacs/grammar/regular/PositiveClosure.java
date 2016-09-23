/**
 * $Id: PositiveClosure.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.grammar.regular;

/**
 * The <code>PositiveClosure</code> interface is a 'utility'
 * <code>RegularExpression</code> which makes it easier to build more
 * complex concatenation::Kleene closure constructs like
 * <code>aa*</code>.  Therefore, <code>a+</code> becomes
 * <code>aa*</code>.
 */
public interface PositiveClosure extends RegularExpression {
  /**
   * Returns the <code>RegularExpression</code> this closure
   * 'wraps'.  Thus for a positive closure <code>a_plus</code>
   * (<code>a+</code>), <code>a_plus.getInternal()</code> would
   * return '<code>a</code>'.
   */
  RegularExpression getInternal();
}
