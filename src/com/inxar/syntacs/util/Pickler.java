/**
 * $Id: Pickler.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.IOException;
import org.inxar.jenesis.*;

/**
 * The <code>Pickler</code> class contains static methods to transform
 * <code>int</code> arrays into <code>String</code> objects and vice
 * versa.  Pickling is useful to decrease classfile size when one
 * requires large <code>static final int</code> arrays.  One the
 * shortcomings of Java's design is that there is little bytecode
 * support for array initialization.  The classfile constant pool was
 * not designed to store arbitrarily typed arrays. Therefore, each
 * element in an array must be initialized expliclitly in bytecode
 * upon compilation.  The constant pool /is/ designed to store char
 * arrays (Strings) however, so by converting an <code>int</code>
 * array into a <code>String</code>, the compiler will put this in the
 * constant pool, saving the need for all those extra bytecode
 * instructions.  Thus, when the class is initialized you can unpickle
 * the String back into the array.  All said and done, the classfile
 * is not only /much/ smaller, the time needed to initialize the class
 * decreases.  
 */
public class Pickler
{
    private static final boolean DEBUG = false;
    private static final boolean TRACE = false;

    // This is a constant used to encode the case when an array is
    // null.
    private static final int  NULL = -1;
    private static final char OFFSET = 0x20;
    private static final char MARK = 0x8000;
    
    /**
     * "Pickles" a 1-dimensional array of <code>int</code> to a
     * <code>String</code>.
     *
     * @param src - The source array to be pickled.
     * @return The source array pickled as a <code>String</code>.  The
     * original in array can be restored using the <code>unpickle1D()
     * method.</code> 
     */
    public static String pickle(int[] src) 
    {
	// Get the length of the array we want to pickle.
	int len = src.length;

	// Okay, now make the pickle jar.
	Cucumber dst = new Cucumber(len);

	// Pickle it.
	pickle(src, dst, len);

	//System.out.println("int[] pickled to string with len: "+dst.length());

	// Finished.  Wrap the array with a new string.
	return dst.toString();
    }

    /**
     * "Pickles" a 2-dimensional array of <code>int</code> to a
     * <code>String</code>.
     *
     * @param src - The source array to be pickled.
     * @return The source array pickled as a <code>String</code>.  The
     * original in array can be restored using the <code>unpickle2D()
     * method.</code> 
     */
    public static String pickle(int[][] src) 
    {
	// Get the length of the array we want to pickle.
	int len = src.length;

	// Okay, now make the pickle jar.
	Cucumber dst = new Cucumber(len);

	// Pickle it.
	pickle(src, dst, len);

	//System.out.println("int[][] pickled to string with len: "+dst.length());

	// Finished.  Wrap the array with a new string.
	return dst.toString();
    }

    /**
     * "Pickles" a 3-dimensional array of <code>int</code> to a
     * <code>String</code>.
     *
     * @param src - The source array to be pickled.
     * @return The source array pickled as a <code>String</code>.  The
     * original in array can be restored using the <code>unpickle3D()
     * method.</code> 
     */
    public static String pickle(int[][][] src) 
    {
	// Get the length of the array we want to pickle.
	int len = src.length;

	// Okay, now make the pickle jar.
	Cucumber dst = new Cucumber(len);

	// Pickle it.
	pickle(src, dst, len);

	//System.out.println("int[][][] pickled to string with len: "+dst.length());

	// Finished.  Wrap the array with a new string.
	return dst.toString();
    }

    /**
     * "Unpickles" a <code>String</code> into a 1-dimensional array of
     * <code>int</code> that was previously pickled.  
     *
     * @param src - The Pickle.
     * @return The unpickled array.
     */
    public static int[] unpickle1D(String src) 
    {
	return unpickle1D(new Pickle(src), -1); 
    }

    /**
     * "Unpickles" a <code>String[]</code> array into a 1-dimensional
     * array of <code>int</code> that was previously pickled.  The
     * elements in the array are concatenated to a single string.
     * This method signature is provided to bypass the constant pool
     * requirement that a string have a maximum length of 65535 chars.
     *
     * @param src - The Pickle, split into several contiguous parts in
     * the array.
     * @return The unpickled array.  
     */
    public static int[] unpickle1D(String[] src) 
    {
	return unpickle1D(new Pickle(src), -1); 
    }

    /**
     * "Unpickles" a <code>String</code> into a 2-dimensional array of
     * <code>int</code> that was previously pickled.  
     *
     * @param src - The Pickle.
     * @return The unpickled array.
     */
    public static int[][] unpickle2D(String src) 
    {
	return unpickle2D(new Pickle(src)); 
    }

    /**
     * "Unpickles" a <code>String[]</code> array into a 2-dimensional
     * array of <code>int</code> that was previously pickled.  The
     * elements in the array are concatenated to a single string.
     * This method signature is provided to bypass the constant pool
     * requirement that a string have a maximum length of 65535 chars.
     *
     * @param src - The Pickle, split into several contiguous parts in
     * the array.
     * @return The unpickled array.  
     */
    public static int[][] unpickle2D(String[] src) 
    {
	return unpickle2D(new Pickle(src)); 
    }

    /**
     * "Unpickles" a <code>String</code> into a 3-dimensional array of
     * <code>int</code> that was previously pickled.  
     *
     * @param src - The Pickle.
     * @return The unpickled array.
     */
    public static int[][][] unpickle3D(String src) 
    {
	return unpickle3D(new Pickle(src)); 
    }

    /**
     * "Unpickles" a <code>String[]</code> array into a 3-dimensional
     * array of <code>int</code> that was previously pickled.  The
     * elements in the array are concatenated to a single string.
     * This method signature is provided to bypass the constant pool
     * requirement that a string have a maximum length of 65535 chars.
     *
     * @param src - The Pickle, split into several contiguous parts in
     * the array.
     * @return The unpickled array.  
     */
    public static int[][][] unpickle3D(String[] src) 
    {
	return unpickle3D(new Pickle(src)); 
    }

    /**
     * Pickles a 1-dimensional int array.
     *
     * @param src - The source array to read integers from.
     * @param dst - The sink to write to.
     * @param len - the number of <code>int</code> elements to read
     * from the source array.
     */
    private static void pickle(int[] src, Cucumber dst, int len) 
    {
	if (DEBUG) trace("src[].len = ", len);

	// Encode the first two elements of the pickle to contain the
	// length of first dim.
	dst.write(len);
	// Don't pickle if the len argument is equal to NULL since it
	// means that the src array is null.
	if (len != NULL) 
	    // Iterate over the source int[] array.
	    for (int i = 0; i < len; i++) {
		// Writethe target value.
		if (DEBUG) trace("src["+i+"] = ", src[i]);
		dst.write(src[i]);
	    }
    }

    /**
     * Pickles a 2-dimensional int array.
     *
     * @param src - The source array to read integers from.
     * @param dst - The sink to write to.
     * @param len - the number of <code>int</code> elements to read
     * from the source array.
     */
    private static void pickle(int[][] src, Cucumber dst, int len) 
    {
	if (DEBUG) trace("src[][].len = ", len);

	// Encode the first two elements of the pickle to contain the
	// length of first dim.
	dst.write(len);

	// Don't pickle if the len argument is equal to NULL since it
	// means that the src array is null.
	if (len != NULL) {
	    
	    // "subsrc" is the array at i.
	    int[] subsrc;
	    
	    // "i" is a counter which tracks progress along the elements
	    // of m in src.
	    for (int i = 0; i < len; i++) {
		
		// Fetch the subarray.
		subsrc = src[i];
		
		// Pickle the elements in the subarray.  Note we need to
		// correct dstoff for the number of chars we've written.
		// If the subarray is null, use a special token NULL for
		// the len argument.
		pickle(subsrc, dst, subsrc == null ? NULL : subsrc.length);

	    }

	} 
    }

    /**
     * Pickles a 3-dimensional int array.
     *
     * @param src - The source array to read integers from.
     * @param dst - The sink to write to.
     * @param len - the number of <code>int</code> elements to read
     * from the source array.
     */
    private static void pickle(int[][][] src, Cucumber dst, int len) 
    {
	if (DEBUG) trace("src[][][].len = ", len);

	// Encode the first two elements of the pickle to contain the
	// length of first dim.
	dst.write(len);

	// Don't pickle if the len argument is equal to NULL since it
	// means that the src array is null.
	if (len != NULL) {
	    
	    // "subsrc" is the array at i.
	    int[][] subsrc;
	    
	    // "i" is a counter which tracks progress along the elements
	    // of m in src.
	    for (int i = 0; i < len; i++) {
		
		// Fetch the subarray.
		subsrc = src[i];
		
		// Pickle the elements in the subarray.  Note we need to
		// correct dstoff for the number of chars we've written.
		// If the subarray is null, use a special token NULL for
		// the len argument.
		pickle(subsrc, dst, subsrc == null ? NULL : subsrc.length);

	    }

	} 
    }

    /**
     * "Unpickles" a <code>String</code> into an array of
     * <code>int</code>.  
     *
     * @param src - The pickle to read characters from.
     * @return The unpickled array.
     */
    private static int[] unpickle1D(Pickle src, int d2) 
    {
	// Read the length of the original array that was encoded.
	int len = src.read();

	if (DEBUG) trace("dst["+d2+"].len = ", len);
	
	// If the len is the special NULL token, we'll return a null
	// array.
	if (len == NULL)
	    return null;

	// Create a new array having this length
	int[] dst = new int[len];

	// Iterate the expected length
	for (int i = 0; i < len ; i++) {
	    dst[i] = src.read();
	    if (DEBUG) trace("dst["+i+"] =  ", dst[i]);
	}

	// Return the unpickled array.
	return dst;
    }

    /**
     * "Unpickles" a <code>String</code> into an array of
     * <code>int[]</code>.  
     *
     * @param src - The pickle to read characters from.
     * @return The unpickled array.
     */
    private static int[][] unpickle2D(Pickle src)
    {
	// Read the length of the original array that was encoded.
	int len = src.read();

	if (DEBUG) trace("dst[][].len = ", len);

	// If the len is the special NULL token, we'll return a null
	// array.
	if (len == NULL)
	    return null;

	// Create a new array having this length
	int[][] dst = new int[len][];

	// Iterate the expected length
	for (int i = 0; i < len ; i++) 
	    dst[i] = unpickle1D(src, i);

	// Return the unpickled array.
	return dst;
    }

    /**
     * "Unpickles" a <code>String</code> into an array of
     * <code>int[][]</code>.  
     *
     * @param src - The pickle to read characters from.
     * @return The unpickled array.
     */
    private static int[][][] unpickle3D(Pickle src)
    {
	// Read the length of the original array that was encoded.
	int len = src.read();

	if (DEBUG) trace("dst[][][].len = ", len);

	// If the len is the special NULL token, we'll return a null
	// array.
	if (len == NULL)
	    return null;

	// Create a new array having this length
	int[][][] dst = new int[len][][];

	// Iterate the expected length
	for (int i = 0; i < len ; i++) 
	    dst[i] = unpickle2D(src);

	// Return the unpickled array.
	return dst;
    }

    /**
     * The <code>Cucumber</code> class is the doppelganger to
     * <code>Pickle</code>.  It is capable of writing
     * <code>int</code>s to a <code>char</code> array.  
     */
    private static final class Cucumber 
    {
	Cucumber(int len)
	{
	    // Make a new StringBuffer 212% bigger than the length of
	    // expexted integers.
	    this.dst = new StringBuffer((len << 1) + (len >> 2));
	}
	
	void write(int x)
	{
	    if (DEBUG) {
		char h = (char)(x >>> 16);
		char l = (char)(x);
		trace("write: x = ", x);
		trace("write: h = ", h, 16);
		putch(h);
		trace("write: l = ", l, 16);
		putch(l);
	    } else {
		// Write the highest 16 bits, then the lowest 16 bits.
		putch( (char)(x >>> 16) );
		putch( (char)(x) );
	    }

	}

	void putch(char c)
	{

	    StringBuffer dst = this.dst;

	    // Offset the raw char value such that control code
	    // conflicts in the final string will not be a problem.
	    int o = c + OFFSET;

	    if (DEBUG) trace("putch["+dst.length()+"]: o = ", o);

	    // See if the value is or is greater than the binary value
	    // "00000000 00000000 10000000 00000000".  Since we are
	    // using the highest bit in the 16-bit char for
	    // bookkeeping, we can only use the first 15 bits for
	    // data.
	    if (o >= MARK) {

		//System.out.println("complex case.");

		// We will need to use two chars to store the
		// information.  The first char will be used to mark
		// the 0x8000 bit since we'll use that when reading to
		// detect multichar sequences.  Interestingly, we can
		// use all 16 bits of the second char since that one
		// will never need to store the marking bit.
		if (DEBUG) {

		    // high and low 16 bits.  
		    char u = (char)(o >>> 16);
		    trace("putch["+dst.length()+"]: u = ", u, 16);

		    char m = (char)(u | MARK );
		    trace("putch["+dst.length()+"]: m = ", m, 16);
		    dst.append(m);

		    char v = (char)(o);
		    trace("putch["+dst.length()+"]: v = ", v, 16);
		    dst.append(v);

		} else {
		    // Store the highest 15 bits OR'd with the mark in the
		    // first char.  For values between 32K and 64K, all
		    // we're actually storing is the mark.
		    dst.append( (char)( (o >>> 16) | MARK ) );
		    
		    // Store the lowest 16 bits in the second char.
		    dst.append( (char)(o) );
		}  
	    } else {

		//System.out.println("simple case.");

		// Ok, just one char is needed. 
		if (DEBUG) trace("putch["+dst.length()+"]: v = ", o, 16);
		dst.append((char)o);

	    }
	}
	
	public String toString()
	{
	    return new String(dst);
	}

	public int length()
	{
	    return dst.length();
	}
	
	StringBuffer dst;
    }

    /**
     * The <code>Pickle</code> class is the doppelganger to
     * <code>Cucumber</code>.  It is capable of reading
     * <code>int</code>s from a <code>String</code>.  
     */
    private static final class Pickle 
    {
	Pickle(String src)
	{
	    this.src = src;

	    //System.out.println("Pickle of length "+src.length());
	    //DEBUG = false;
	}
	
	Pickle(String[] src)
	{
	    StringBuffer b = new StringBuffer();
	    for (int i = 0; i < src.length; i++)
		b.append(src[i]);

	    this.src = b.toString();

	    //System.out.println("Pickle of length "+this.src.length());
	    //DEBUG = false;
	}
	
	int read()
	{
	    // Combine the next two "chars" in the pickle.
	    if (TRACE) {

		int h = (getch() << 16);
		trace("read: h = ", h);

		int l = getch();
		trace("read: l = ", l);

		int x = h | l;
		trace("read: x = ", x);

		return x;

	    } else {
		return (getch() << 16) | getch();
	    }
	} 
	
	char getch()
	{
	    String src = this.src;

	    if (src.length() - index <= 5) {
		//System.out.println("reading pickle at index "+index+" (total: "+src.length()+")");
		//DEBUG = true;
	    }

	    // Get the first next char in the String.
	    int c = src.charAt(index++);

	    if (TRACE) trace("read: c = ", c, 16);

	    // Is this a continuing char?  If so, the highest bit will
	    // be marked.
	    //if ( (c & MARK) == 1 ) {
	    if (c >= MARK) {

		//System.out.println("reading complex case.");
		// Remove the mark on c by masking with the MARK
		// bit-inverse.
		c &= ~MARK;

		// Shift it back up.
		c <<= 16;

		char k = src.charAt(index++);

		c |= k;

		if (TRACE) trace("read: k = ", k, 16);

		// And recombine with the next char to the original
		// offset value.  Store this back in c.
		//c |= src.charAt(index++);


	    } 

	    //System.out.println("got1 '"+c+"' ("+(int)c+"): ");

	    //c -= OFFSET;

	    //System.out.println("got2 '"+c+"' ("+(int)c+"): ");

	    // Return the char, but remove the offset first.
	    //return (char)c;
	    return (char)(c - OFFSET);
	}
	
	String src;
	int index;
    }

    // static methods only
    private Pickler()
    {
    }

    private static void main(String[] argv) throws IOException
    {
	test();
    }

    private static void main2(String[] argv)
    {
	if (true) {
	    System.out.println("3D:");
	    int[][][] a3 = new int[3][][];
	    a3[0] = new int[39][];
	    a3[0][0] = new int[]{65536, -1, 0, 0};

	    String s3 = pickle(a3);
	    System.out.println(s3);
	    
	    int[][][] b3 = unpickle3D(s3);
	    
	    String t3 = pickle(b3);
	    System.out.println(t3);
	}

	if (false) {
	    System.out.println("1D:");

	    int[] a1 = new int[]{ -1, -2, 65535};
	    System.out.println(toString(a1));
	
	    String s1 = pickle(a1);
	    System.out.println(s1);

	    int[] b1 = unpickle1D(s1);
	    System.out.println(toString(b1));

	    String t1 = pickle(b1);
	    System.out.println(t1);
	    
	    //	    System.exit(1);
	    System.out.println("2D:");
	    int[][] a2 = new int[][]{ {1, 2, 3}, {4, 5, 6} };
	    
	    String s2 = pickle(a2);
	    System.out.println(s2);
	    
	    int[][] b2 = unpickle2D(s2);
	    
	    String t2 = pickle(b2);
	    System.out.println(t2);
	    
	    System.out.println("3D:");
	    int[][][] a3 = new int[][][]{ { {1, 2, 3}, {4, 5, 6} }, { {7, 8, 9}, {10, 11, 12} } };
	    
	    String s3 = pickle(a3);
	    System.out.println(s3);
	    
	    int[][][] b3 = unpickle3D(s3);
	    
	    String t3 = pickle(b3);
	    System.out.println(t3);
	} 
    }

    private static void print(int x)
    {
	for (int i = 32; i >= 0; i--) {
	    System.out.print( (x & (1 << i)) > 0 ? 1 : 0 );
	    if ((i % 8) == 0)
		System.out.print(' ');
	}
    }

    public static char[] bin(int x, int bits)
    {
	int index = 36;

        for (int i = 0; i < bits; ++i) {

	    // Add a byte separator if multiple of 8
	    if ((i % 8) == 0)
		buf[--index] = ' ';

	    // In the next position, add the corresponding bit.
	    buf[--index] = (x & 0x01) == 0 ? '0' : '1';

            // Shift the int to the right one bit
            x >>>= 1;
	    
        }

	while (index > 0)
	    buf[--index] = ' ';

	return buf;
    }  

    private static String toString(int[] a)
    {
	StringBuffer b = new StringBuffer();
	print(a, b);
	return b.toString();
    }

    private static void print(int[] a, StringBuffer b)
    {
	b.append('[');
	for (int i = 0; i < a.length; i++) {
	    if (i > 0) b.append(',');
	    b.append(a[i]);
	}
	b.append(']');
    }

    private static void trace(String label, int value, int bits)
    {
	System.out.print(label+"\t");
	System.out.print(bin(value, bits));
	System.out.println("("+value+")");
    }

    private static void trace(String label, int value)
    {
	trace(label, value, 32);
    }

    private static void test() throws IOException
    {
	long start = System.currentTimeMillis(), time;

	// Get the VirtualMachine implementation.  
	VirtualMachine vm = VirtualMachine.getVirtualMachine();
	
	// Instantiate a new CompilationUnit.  The argument to the
	// compilation unit is the "codebase" or directory where the
	// compilation unit should be written.
	//
	// Make a new compilation unit rooted to the given sourcepath.
	CompilationUnit unit = vm.newCompilationUnit("./src");
	
	// Set the package namespace.
	unit.setNamespace("com.inxar.syntacs.translator.test");
	
	// Add an import statement for fun.
	unit.addImport("java.io.Serializable");
	
	// Comment the package with a javadoc (DocumentationComment).
	unit.setComment(Comment.D, "Auto-Generated using the Jenesis Syntax API");
	
	// Make a new class.
	PackageClass cls = unit.newClass("HelloWorld");
	// Make it a public class.
	cls.setAccess(Access.PUBLIC);
	
	// Make a new Method in the Class having type VOID and name "main".
	ClassMethod method = cls.newMethod(vm.newType(Type.VOID), "main");
	// Make it a public method.
	method.setAccess(Access.PUBLIC);
	// Make it a static method
	method.isStatic(true);
	// Add the "String[] argv" formal parameter.
	method.addParameter(vm.newArray("String", 1), "argv");

	// Create a new Method Invocation expression.
	Invoke println = vm.newInvoke("System.out", "println");

	// Add the Hello World string literal as the sole argument.
	println.addArg(vm.newString(string()));
	// Add this expression to the method in a statement.
	method.newStmt(println);
	
	time = System.currentTimeMillis() - start;
	System.out.println("constructed in "+time+" ms");

	unit.encode();
    }

    private static String string()
    {
	int len = 287;

	int[] a = new int[len];

	for (int i = 0; i < len; i++)
	    a[i] = i;

	String p = pickle(a);
	return p;
    }

    private static String string2()
    {
	String msg = "Hello World! ";
	StringBuffer b = new StringBuffer();
	for (int i = 0; i < 10; i++)
	    b.append(msg);

	return b.toString();
    }

    static char[] buf = new char[36];
}




