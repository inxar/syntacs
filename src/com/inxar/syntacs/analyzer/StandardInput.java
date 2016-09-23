/**
 * $Id: StandardInput.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer;

import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.EOFException;
import java.net.URL;
import org.inxar.syntacs.analyzer.Input;
import org.inxar.syntacs.util.Log;
import com.inxar.syntacs.util.Mission;

/**
 * Concrete implementation of Input which wraps a
 * <code>java.io.Reader</code>.  This particular implementation slups
 * the entire input into a char array at the upon initialization, so
 * therefore it is fast, but it is a memory hog for large files.
 */
public class StandardInput
    implements Input
{
    private static final boolean DEBUG = false;

    private final static char CR = '\r';
    private final static char LF = '\n';

    /**
     * Constructs a new <code>StandardInput</code> instance.
     */
    public StandardInput()
    {
    }

    public void initch(Object src) throws IOException
    {
	if (src instanceof char[])
	    init((char[])src);
	else if (src instanceof URL)
	    init((URL)src);
	else if (src instanceof Reader)
	    init((Reader)src);
	else if (src instanceof File)
	    init((File)src);
	else if (src instanceof String)
	    init((String)src);
	else if (src == null)
	    throw new IllegalArgumentException
		("A non-null initialization argument is required");
	else
	    throw new IllegalArgumentException
		("I can only be initialized using a char[], URL, File, "+
		 "or Reader instance, not "+src);
    }

    private void init(URL url) throws IOException
    {
	InputStream in = null;

	try {

	    in = url.openStream();
	    CharArrayWriter out = new CharArrayWriter();

	    int c;
	    while ( (c = in.read()) != -1 )
		out.write(c);

	    init( out.toCharArray() );

	    in.close(); in = null;

	} finally {
	    if (in != null)
		try { in.close(); }
		catch (Exception ex) {}
	}
    }

    private void init(File file) throws IOException
    {
      // make the src array
	long length = file.length();

	// is this bigger than we can index?
	if (length > Integer.MAX_VALUE)
	    throw new IOException
		("The length of this file (" + length +
		 " bytes) is larger than the maximum size allowed " +
		 "by this Input implementation (" + Integer.MAX_VALUE+
		 " bytes).");

	// narrow
	int len = (int)length;

	BufferedReader in = null;
	try {

	    // ok make the array
	    char[] src = new char[len];

	    // and fill it from a buffered reader
	    in = new BufferedReader(new FileReader(file), len);

	    if (DEBUG)
		log().debug()
		    .write("length of file ")
		    .write(file.getAbsolutePath())
		    .write(" is ")
		    .write(len)
		    .write(" bytes").out();

	    // read into the array, close it, and kill the reference
	    in.read(src, 0, len); in.close(); in = null;

	    init(src);

	} finally {
	    if (in != null)
		try { in.close(); in = null; }
		catch (Exception ex) {}
	}
    }

    private void init(Reader in) throws IOException
    {
	if (!(in instanceof BufferedReader))
	    in = new BufferedReader(in);

	try {

	    CharArrayWriter out = new CharArrayWriter();

	    int c;
	    while ( (c = in.read()) != -1 )
		out.write(c);

	    init( out.toCharArray() );

	    in.close(); in = null;

	} finally {
	    if (in != null)
		try { in.close(); } catch (Exception ex) {}
	}
    }

    private void init(String src)
    {
	init(src.toCharArray());
    }

    private void init(char[] src)
    {
	this.src = src;
	//if (DEBUG) debug("src array is "+src);

	this.currentPosition = 0;
	this.currentLine = 1;
    }

    public void reset()
    {
	this.currentPosition = 0;
	this.currentLine = 1;
    }

    public int atch()
    {
    	return currentPosition;
    }

    public void atch(int pos)
    {
    	this.currentPosition = pos;
    }

    public int atln()
    {
	return currentLine;
    }

    public void bach(int length)
    {
	while (length-->0)
	    if (LF == src[--currentPosition])
		currentLine--;
    }

    public int getch() throws IOException, EOFException
    {
	if (DEBUG) log().debug()
		       .write("getch(): now at ")
		       .write(currentPosition)
		       .out();

	try {

	    // (1) get the next character from the stored input
	    char c = src[currentPosition];

	    // (2) check for newlines.  Assume that we are dealing with
	    // UNIX files in which the LF is the sole newline char OR CRLF
	    // dos files, in which case we count the transition after LF
	    // as the moment when the newline occurs.  This does not deal
	    // with solitary LF characters in dos files.  Hopefully this
	    // is not too much of a problem.
	    if (c == LF)
		currentLine++;

	    // increment our position
	    currentPosition++;

	    // return as int and we're done.
	    return (int)c;

	} catch (ArrayIndexOutOfBoundsException aiiobex) {
	    throw new EOFException();
	}
    }

    public int broach()
    {
	return currentPosition == src.length ? -1 : src[currentPosition];
    }

    public void fetch(int offset, int length, char[] dst, int dstoff)
    {
	//System.out.println("fetch: request for char[] with length "+length+" at offset "+offset+" into given array at "+dstoff);
        // make the copy
        System.arraycopy(src, offset, dst, dstoff, length);
    }

    public char[] retch(int offset, int length)
    {
        // make a new array for the caller
        char[] dst = new char[length];
	// do the copy
        System.arraycopy(src, offset, dst, 0, length);
	// return it
	return dst;
    }

    public char retch(int offset)
    {
	if (offset < 0)
	    throw new ArrayIndexOutOfBoundsException("Negative array offset: "+offset);
	if (offset >= src.length)
	    throw new ArrayIndexOutOfBoundsException
		("Offset is greater than max array element: "+offset+ " > "+(src.length - 1));
	return src[offset];
    }

    public String stretch(int offset, int length)
    {
	if (length == 0)
	    return "";

	//System.out.println("stretch: request for string with length "+length+" at offset "+offset);
        // make a new array for the caller
        char[] dst = new char[length];
	// do the copy
        System.arraycopy(src, offset, dst, 0, length);
	// return it
	return new String(dst);
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();
	b.append(src);
	return b.toString();
    }

    public void patch(int dstoff, char[] src, int srcoff, int len)
    {
        System.arraycopy(src, srcoff, this.src, dstoff, len);
    }

    private Log log()
    {
	if (this.log == null)
	    this.log = Mission.control().log("inp", this);
	return log;
    }

    // this is the current absolute position
    private int currentPosition;

    // this is the current line number
    private int currentLine;

    // this is the source array
    private char[] src;

    // The log.
    private Log log;
}
