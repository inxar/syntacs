/**
 * $Id: StandardAction.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.pushdown;

import org.inxar.syntacs.automaton.pushdown.*;

/**
 * Standard implementation of <code>Action</code>.
 */
public class StandardAction
    implements Action
{
    /**
     * Creates a new <code>Action</code> with the given data.
     */
    public StandardAction(int type, int value)
    {
	this.type = type;
	this.value = value;
    }
    
    /**
     * Constructs an error action with <code>id=0, value=0, type=ERROR</code>.
     */
    public StandardAction()
    {
	this(ERROR, 0);
    }
    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        switch (type) {
	case ERROR: b.append("ERROR "); break;
	case SHIFT: b.append("SHIFT " + value); break;
	case REDUCE: b.append("REDUCE " + value); break;
	case ACCEPT: b.append("ACCEPT "); break;
	default: 
	    b.append("UNKNOWN: " + value); break;
        }
        return b.toString();
    }
    
    public int getType()
    {
	return type;
    }

    public int getValue()
    {
	return value;
    }

    public int hashCode()
    {
	return 115 * type * value;
    }

    public boolean equals(Object other)
    {
	if (other == this)
	    return true;

	if (other == null || !(other instanceof Action))
	    return false;

	if (other instanceof StandardAction) {

	    StandardAction that = (StandardAction)other;
	    return this.type == that.type && this.value == that.value;

	} else {

	    Action that = (Action)other;
	    return this.type == that.getType() && this.value == that.getValue();

	}

    }

    /**
     * The type of this action, one of (ERROR|SHIFT|REDUCE|ACCEPT).
     */
    int type;
    
    /**
     * A generic container for additional action information.
     */
    int value;
}


