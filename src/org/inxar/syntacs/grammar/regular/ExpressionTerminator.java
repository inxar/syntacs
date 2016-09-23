/**
 * $Id: ExpressionTerminator.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * The <code>ExpressionTerminator</code> interface describes a special
 * pseudo-abstract object that symbolizes the end of a particular
 * regular expression.  By concatenation of a <code>ExpressionTerminator</code>
 * to the end of another extant regular expression, some algorithms
 * become simplified.  However, there is generally no need to actually
 * do this in practice -- it is something that is done internally.
 * When a <code>ExpressionTerminator</code> is encountered in certain specific
 * places in these algorithms, it is known that a token is
 * implied. Thus, a <code>ExpressionTerminator</code> has a link back to the
 * <code>Token</code>.
 */
public interface ExpressionTerminator extends Interval {
  /**
   * Returns the <code>Token</code> which this
   * <code>ExpressionTerminator</code> implies.
   */
  RegularToken getToken();
}
