/**
 * $Id: Log.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.util;

/**
 * A <code>Log</code> is a stream which is partitioned into different
 * <code>Channel</code>s, each Channel dedicated to a particular
 * level.  
 */
public interface Log
{
    /**
     * Gets the tracing <code>Channel</code>.
     */
    Channel trace();

    /**
     * Gets the debugging <code>Channel</code>.
     */
    Channel debug();

    /**
     * Gets the general info <code>Channel</code>.
     */
    Channel info();

    /**
     * Gets the warning <code>Channel</code>.
     */
    Channel warn();

    /**
     * Gets the critical <code>Channel</code>.
     */
    Channel critical();

    /**
     * Gets the catastrophe <code>Channel</code>.
     */
    Channel catastrophe();
}


