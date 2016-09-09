/**
 * $Id: AlgorithmException.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
 * The <code>AlgorithmException</code> is a general
 * <code>Exception</code> possibly thrown by <code>Algorithm</code>
 * implementations.  
 */
public class AlgorithmException
    extends Exception
{
    /**
     * Constructs an empty <code>AlgorithmException</code>.
     */
    public AlgorithmException()
    {
	super();
    }
    
    /**
     * Constructs an <code>AlgorithmException</code> on the nested
     * <code>Throwable</code>.  
     */
    public AlgorithmException(Throwable t)
    {
	super();
	this.t = t;
    }
    
    /**
     * Constructs an AlgorithmException with the given message.  
     */
    public AlgorithmException(String msg)
    {
	super(msg);
    }

    /**
     * Constructs an <code>AlgorithmException</code> on the nested
     * <code>Throwable</code> having the given message.  
     */
    public AlgorithmException(Throwable t, String msg)
    {
	super(msg);
	this.t = t;
    }

    public void printStackTrace()
    {
	super.printStackTrace();
	System.out.println("Nested exception is: ");
	t.printStackTrace();
    }
    
    private Throwable t;
}

