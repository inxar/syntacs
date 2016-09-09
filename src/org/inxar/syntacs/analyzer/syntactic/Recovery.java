/**
 * $Id: Recovery.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.syntactic;

/**
 * A <code>Recovery</code> is a sequential list of
 * <code>Correction</code> instances to be executed by the
 * <code>Parser</code>..  
 */
public interface Recovery
{
    /**
     * Returns the next <code>Correction</code> to apply.
     */
    Correction next();

    /**
     * Returns <code>true</code> if there is at least one more
     * correction to apply, <code>false</code> if no more corrections
     * need to be done.  
     */
    boolean hasNext();
}


