/**
 * $Id: Option.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * The <code>Option</code> interface is another 'utility' regex which
 * abstracts the '?' operator. It is not a 'fundamental' regular
 * language construct.  Therefore, the <code>Option</code> 'a?' is a
 * shortcut for saying '(a|e)' where 'e' represents
 * <code>Epsilon</code>.  
 */
public interface Option
    extends RegularExpression
{
    /**
     * Returns the <code>RegularExpression</code> which this '?'
     * modifies.  
     */
    RegularExpression getInternal();
}

