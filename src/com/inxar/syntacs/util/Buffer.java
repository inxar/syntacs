/**
 * $Id: Buffer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.*;
import java.util.*;

/**
 * Simpler, unsynchronized implementation of <code>StringBuffer</code> 
 * that can render itself to a <code>Writer</code>. 
 */
public class Buffer implements Cloneable
{
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final boolean DEBUG = false;

    public Buffer()
    {
	this(17);
    }
    
    public Buffer(int capacity)
    {
	this.src = new char[capacity];
	this.len = 0;
    }
    
    public Buffer set(int offset, String s)
    {
	return set(offset, s.toCharArray(), 0, s.length());
    }

    public Buffer set(int offset, char[] that_src, int that_off, int length)
    {

	while (this.src.length < offset + length)
	    realloc();
	
	System.arraycopy(that_src, that_off, this.src, offset, length);

	this.len = Math.max(this.len, offset + length);

	return this;
    }

    public Buffer add(String val, int offset, int length)
    {
	if (val == null) 
	    val = String.valueOf(val);

	while (this.src.length < this.len + length)
	    realloc();
	
	if (DEBUG)
	    System.out.println("Copying from string with length "+val.length()+
			       " starting at "+offset+
			       " and ending at "+(offset+length)+
			       " into array with length "+this.src.length+
			       " starting at "+this.len);

	val.getChars(offset, offset + length, this.src, this.len);

	this.len += length;

	return this;
    }

    public Buffer add(String val)
    {
	if (val == null) 
	    val = "null";

	return add(val, 0, val.length());
    }

    public Buffer add(boolean val)
    {
	return add(String.valueOf(val));
    }

    public Buffer add(char val)
    {
	return add(String.valueOf(val));
    }

    public Buffer add(int val)
    {
	return add(String.valueOf(val));
    }

    public Buffer add(long val)
    {
	return add(String.valueOf(val));
    }

    public Buffer ln()
    {
	return add(NEWLINE);
    }

    public Buffer add(Buffer that)
    {
	while (this.src.length < this.len + that.len)
	    realloc();
	
	System.arraycopy(that.src, 0, this.src, this.len, that.len);

	this.len += that.len;

	return this;
    }

    public Buffer add(char[] that_src)
    {
	return add(that_src, 0, that_src.length);
    }

    public Buffer add(char[] that_src, int that_off, int that_len)
    {
	while (this.len + that_len > this.src.length)
	    realloc();

	System.arraycopy(that_src, that_off, this.src, this.len, that_len);

	return this;
    }

    public Buffer setCharAt(int i, char c)
    {
	while (this.src.length < i)
	    realloc();
	
	this.src[i] = c;

	if (len <= i)
	    len = i;

	return this;
    }

    public char charAt(int i)
    {
	return src[i];
    }

    public int length()
    {
	return this.len;
    }

    public void length(int that_len)
    {
	while (this.src.length < that_len) 
	    realloc();

	this.len = that_len;
    }

    public Buffer copy(int capacity)
    {
	try {

	    Buffer clone = (Buffer)super.clone();
	    char[] dst = new char[len + capacity];
	    System.arraycopy(src, 0, dst, 0, len);
	    clone.src = dst;
	    return clone;

	} catch (CloneNotSupportedException cnsex) {
	    cnsex.printStackTrace();
	    throw new InternalError();
	}
    }

    public void realloc()
    {
	if (DEBUG)
	    System.out.println("realloc to "+(src.length * 2));

	char[] dst = new char[src.length * 2];
	System.arraycopy(src, 0, dst, 0, len);
	this.src = dst;
    }

    public String toString()
    {
	return new String(this.src, 0, this.len);
    }

    public void toWriter(Writer out) throws IOException
    {
	out.write(this.src, 0, this.len);
    }

    public Object clone() throws CloneNotSupportedException
    {
	Buffer clone = (Buffer)super.clone();

	char[] dst = new char[len];
	System.arraycopy(src, 0, dst, 0, len);
	clone.src = dst;

	return clone;
    }

    public char[] getCharArray()
    {
	return src;
    }

    private int len;
    private char[] src;
}




/*
	System.out.println("writing "+ new String(that_src)+" at offset "+offset);
	System.out.println("this.src was "+ new String(this.src));
	System.out.println("that_src was "+ new String(that_src));
	System.out.println("this.src now "+ new String(this.src));
	System.out.println("that_src now "+ new String(that_src));
*/
