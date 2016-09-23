/**
 * $Id: AmbiguityException.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.automaton.pushdown;

import org.inxar.syntacs.util.AlgorithmException;

/**
 * AmbiguityException objects are thrown to indicate mismatches
 * between the definition of a language grammar and the strength of
 * the algorithm used to compute the state machine to recognize that
 * grammars.
 */
public class AmbiguityException
    extends AlgorithmException
{
    /**
     * Type of AmbiguityException to indicate a shift-shift
     * conflict.
     */
    public static final int SHIFT_SHIFT = 1;

    /**
     * Type of AmbiguityException to indicate a shift-reduce
     * conflict.
     */
    public static final int SHIFT_REDUCE = 2;

    /**
     * Type of AmbiguityException to indicate a reduce-reduce
     * conflict.
     */
    public static final int REDUCE_REDUCE = 3;

    /**
     * Constructs a new AmbiguityException with the given message.
     */
    public AmbiguityException(int type, String msg)
    {
	super(msg);
	this.type = type;
    }

    public int getType()
    {
	return type;
    }

    private int type;
}
