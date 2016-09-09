/**
 * $Id: RegularExpression.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
 *
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.inxar.syntacs.grammar.regular;

import org.inxar.syntacs.util.IntSet;

/**
 * The <code>RegularExpression</code> interface is a base interface
 * for all specific <code>RegularExpression</code> constructs.  Each
 * regex needs to know how to build its first and last sets, and what
 * to do inside its follow() method.  For more information about what
 * these methods really mean, refer to Chapter 3 in the Dragon Book.
 * 
 * <P> 
 * 
 * This interface extends <code>Cloneable</code>, but the semantics of
 * cloning go beyond what one might expect.  For example, if we have
 * the regex '(a|b|c)+', this needs to be deconstructed into it's more
 * fundamental form '(a|b|c)-(a|b|c)*' where '-' means
 * <code>Concatenation</code>.  However, we cannot simply copy the
 * <code>Union</code> object as its members (a, b, c) would have the
 * same id and thus indistinguishable.  Therefore, the clone()
 * operation will end up making *new* members (a', b', c') and thus
 * id(a) != id(a').  
 */
public interface RegularExpression
    extends Cloneable
{
    /**
     * Returns <code>true</code> if this
     * <code>RegularExpression</code> either *is* <code>Epsilon</code>
     * or derives it.  
     */
    boolean isNullable();

    /**
     * Returns the <code>IntSet</code> of <code>Intervals</code> which
     * are visible at the logical beginning of the the expression.  
     */
    IntSet getFirstSet();

    /**
     * Returns the <code>IntSet</code> of <code>Intervals</code> which
     * are visible at the logical end of the the expression.  
     */
    IntSet getLastSet();

    /**
     * Triggers the process of computing the follow sets.
     */
    void follow();

    /**
     * See the general explanation of <code>clone()</code> given above.
     */
    Object clone() throws CloneNotSupportedException;
}



