/**
 * $Id: Union.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

/**
 * The <code>Union</code> interface is an abstraction over the
 * <code>RegularExpression</code> alternation object classically known
 * syntactically by the pipe '|'.  An <code>Union</code> is a discrete regular
 * expression that contains an arbitrary number of 'alternatives', any
 * of which if matched will satisfy the expression.  
 */
public interface Union
    extends RegularExpression
{
    /**
     * Adds the given <code>RegularExpression</code> to the list of
     * alternatives.  If the alternative is already in the set of
     * alternatives, no action is taken.  This method returns the
     * <code>Union</code> simply to support a coding style reminiscent
     * of <code>java.lang.StringBuffer</code>.  
     */
    Union addAllele(RegularExpression e);

    /**
     * Returns the list of alternatives which are currently in the set
     * as an array.  
     */
    RegularExpression[] getAlleles();

    /**
     * Returns the number of alternatives.
     */
    int alleles();
}


