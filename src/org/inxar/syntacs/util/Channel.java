/**
 * $Id: Channel.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * A <code>Channel</code> is a buffered writing device for a
 * <code>Log</code>.  
 */
public interface Channel
{
    /**
     * Writes an <code>Object</code>.
     */
    Channel write(Object msg);

    /**
     * Writes a <code>boolean</code>.
     */
    Channel write(boolean msg);

    /**
     * Writes a <code>char</code>.
     */
    Channel write(char msg);

    /**
     * Writes an <code>int</code>.
     */
    Channel write(int msg);

    /**
     * Writes a <code>String</code>.
     */
    Channel write(String msg);

    /**
     * Writes a single space.
     */ 
    Channel spc();

    /**
     * Writes a message enclosed in double quotations.
     */ 
    Channel quote(Object msg);

    /**
     * Writes the given <code>char</code> <code>n</code> number of
     * times.  
     */
    Channel stripe(char c, int n);

    /**
     * Writes a newline character.
     */ 
    Channel writeln();

    /**
     * Increments an indent which is manifested after successive newlines. 
     */
    Channel over();

    /**
     * Decrements an indent which is manifested after successive newlines. 
     */
    Channel back();

    /**
     * The <code>time()</code> method is functionally similar to the
     * <code>out()</code> method; the contents of the buffer are
     * flushed to the parent <code>Log</code>.  However, a timer is
     * started which runs until the next use of the
     * <code>Channel</code>, at which point the elapsed time is
     * printed to the <code>Log</code>.
     */
    void time();

    /**
     * The <code>touch()</code> method is useful for stopping a timed
     * Channel but not writing anything to it.  Often,
     * <code>time()</code> and <code>touch()</code> invocations come
     * in pairs.
     */
    void touch();

    /**
     * Writes a newline, flushes the buffer contents, and resets the
     * stream.  
     */
    void out();
}


