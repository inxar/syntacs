/**
 * $Id: Clock.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.util;

/**
 * A <code>Clock</code> is a simple utility class used for timing
 * things, like a stopwatch.  Use it wherever you would use
 * <code>System.currentTimeMillis()</code> for timing runtime
 * performance and such.  
 */
public class Clock
{
    /**
     * Makes a new <code>Clock</code> and marks the current time. 
     */
    public Clock()
    {
	mark = System.currentTimeMillis();
	start = mark;
    }
    
    /**
     * Sets the mark to the current time.
     */
    public void reset()
    {
	mark = System.currentTimeMillis();
	start = mark;
    }
    
    /**
     * Computes the interval between the current time and the last
     * mark and sets the mark to the current time (resets the
     * <code>Clock</code>).  
     */
    public long lap()
    {
	long now = System.currentTimeMillis();
	long len = now - mark;
	mark = now;
	return len;
    }
    
    /**
     * Computes the interval between the current time and the last
     * mark but does not reset the mark.
     */
    public long elapsed()
    {
	return System.currentTimeMillis() - mark;
    }
    
    public long time()
    {
	return System.currentTimeMillis() - start;
    }
    
    private long mark;
    private long start;
}
