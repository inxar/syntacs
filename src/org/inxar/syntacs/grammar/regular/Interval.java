/**
 * $Id: Interval.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
import org.inxar.syntacs.util.IntIterator;

/**
 * The <code>Interval</code> interface abstracts a contiguous bounded
 * block of characters.  <code>Intervals</code> take the place of
 * normal input characters since it makes it easier to throw around
 * and otherwise manipulate large sets of characters.  For example, if
 * we want to represent the ascii character charset, we chould use 256
 * individual char objects, or one <code>Interval</code> object.  
 */
public interface Interval
    extends RegularExpression
{
    /**
     * Returns an <code>IntIterator</code> over the chars named by this
     * <code>Interval</code> of characters.  
     */
    IntIterator iterator();

    /**
     * Returns the RTTI specifically to check if this
     * <code>Interval</code> is an <code>instanceof</code>
     * <code>ExpressionTerminator</code>.  This method is used by some
     * DFA algorithms to find final NFA states in a DFA state.  It is
     * faster than the <code>instanceof</code> operator.
     */
    boolean isTerminator();

    /**
     * Returns <code>true</code> if the given <code>char</code> (as an
     * <code>int</code>) is within the bounds of this character set.  
     */
    boolean includes(int c);

    /**
     * Returns the low <code>char</code> in the <code>Interval</code>.  
     */
    int lo();

    /**
     * Returns the high <code>char</code> in the
     * <code>Interval</code>.  
     */
    int hi();

    /**
     * Returns the globally allocated ID for this
     * <code>Interval</code>.  
     */
    int getID();

    /**
     * Returns the "follow set", or the <code>Intervals</code> (input)
     * that may occur directly after encountering this one.  
     */
    IntSet getFollowSet();
}



