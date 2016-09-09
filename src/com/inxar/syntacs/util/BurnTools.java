/**
 * $Id: BurnTools.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.jenesis.*;

/**
 * A collection of utilities used by various <code>Burner</code>
 * instances; makes working with the <a
 * href="http://www.inxar.org/jenesis">Jenesis API</a> easier.
 */
public final class BurnTools
{
    private static final boolean DEBUG = false;

    private BurnTools()
    {
    }

    /**
     * Create a new 1-dimensional array initializer, like the
     * following: "int[] array = new int[]{ 1, 2, 3}" 
     */
    public static void init1DArray(Block block, String var, int[] a)
    {
	NewArray n = vm.newArray(vm.newType(Type.INT)).addDim();
	n.setInitializer(vm.newArrayInit(make1DIntArray(a)));
        block.newStmt(vm.newAssign( vm.newVar(var), n ));
    }
    
    /**
     * Create a new 1-dimensional array initializer, like the
     * following: "new int[]{ 1, 2, 3}" 
     */
    public static NewArray newIntArray(int[] a)
    {
	NewArray n = vm.newArray(vm.newType(Type.INT)).addDim();
	n.setInitializer(vm.newArrayInit(make1DIntArray(a)));
	return n;
    }
    
    /**
     * Create a new 2-dimensional array initializer, like the
     * following: "new int[]{ {1,2}, {1,3}, {1,4} }" 
     */
    public static NewArray newIntIntArray(int[][] a)
    {
	NewArray n = vm.newArray(vm.newType(Type.INT)).addDim().addDim();
	n.setInitializer(vm.newArrayInit(make2DIntArray(a)));
	return n;
    }
    
    public static void init2DArray(Block block, String var, int[][] a)
    {
	// set up the new array expressions
	NewArray na  = vm.newArray(vm.newType(Type.INT)).addDim(vm.newInt(a.length)).addDim();
	block.newStmt(vm.newAssign(vm.newVar(var), na));
        // iterate outer loop
        for (int i=0; i<a.length; i++) {
	    Expression e = null;
	    // check for null arrays
	    if (a[i] == null) {
		e = vm.newNull();
	    } else {
		// make array initializer
		NewArray n = vm.newArray(vm.newType(Type.INT)).addDim();
		n.setInitializer(vm.newArrayInit(make1DIntArray(a[i])));
		e = n;
	    }
	    // make new assignment
	    block.newStmt( vm.newAssign(vm.newArrayAccess(null, var).addDim(vm.newInt(i)), e) );
	}
    }
    
    public static Expression[] toExpression(String[] vals)
    {
	Expression[] e = new Expression[vals.length];
	for (int i = 0; i < vals.length; i++)
	    e[i] = vm.newString(vals[i]);
	return e;
    }

    public static Expression[] toExpression(int[] vals)
    {
	Expression[] e = new Expression[vals.length];
	for (int i = 0; i < vals.length; i++)
	    e[i] = vm.newInt(vals[i]);
	return e;
    }

    public static Object make1DIntArray(int[] a)
    {
    	if (a == null)
	    return vm.newBlank();
	
    	// make a new 1D expression array
    	Expression[] e = new Expression[a.length];
    	// d1 loop
    	for (int i=0; i<a.length; i++) {
	    e[i] = vm.newInt(a[i]);
    	}
    	return e;
    }

    public static Object make2DIntArray(int[][] a)
    {
    	if (a == null)
	    return vm.newBlank();
	
    	// make a new 2D expression array
    	Expression[][] e = new Expression[a.length][];
    	// d1 loop
    	for (int i=0; i<a.length; i++) {
	    if (a[i] == null) {
		e[i] = new Expression[]{vm.newBlank()};
		continue;
	    }
	    // make array
	    e[i] = new Expression[a[i].length];
	    // d2 loop
	    for (int j=0; j<a[i].length; j++) {
		//System.out.println("a["+i+"]["+j+"] = "+a[i][j]);
		e[i][j] = vm.newInt(a[i][j]);
	    }
    	}
    	return e;
    }
    
    public static Object make1DStringArray(String[] a)
    {
    	if (a == null)
	    return vm.newBlank();
	
    	// make a new 1D expression array
    	Expression[] e = new Expression[a.length];
    	// d1 loop
    	for (int i=0; i<a.length; i++) 
	    e[i] = vm.newString(a[i]);

    	return e;
    }
    
    public static Expression split(String pickle)
    {
	// Check the length of the string we intend to write.  If the
	// compiled length of the string is more than 65535 bytes, the
	// UTF constant pool entry for this string will be not be
	// valid (the classfile format limits string length to 65535
	// characters).  
	int utflen = StringTools.getUTFLength(pickle);

	if (utflen > 65535) {

	    // The string is too big for the constant pool entry.  We
	    // have to break the pickle in at least one place and
	    // reassemble during unpickling.

	    // PART 1: We will iterate over the string, breaking it up
	    // into 65535 byte chunks.  The chunks will be put into an
	    // string array which will become the argument to the
	    // invocation.

	    // See how many pieces we will need to break it into.
	    int chunklen = (utflen / 65535) + 1;

	    System.out.println("Pickle is too large ("+utflen+").  Splitting into "+chunklen+" chunks.");

	    // Make an array of this length
	    String[] chunks = new String[chunklen];

	    // The next two values will be used to substring the
	    // pickle.  The offset is the first char in the chunk, nad
	    // the length is the number of chars in the chunk.  The
	    // length of the chunk will be such that the number of
	    // bytes needed for the chunk will be 65535.
	    int off = 0, len;

	    // Now break up the string.  
	    for (int i = 0; i < chunklen; i++) {

		// Find the number of characters that take up 65535
		// bytes.
		len = StringTools.lengthOfUTF(pickle, off, 65535);
		
		System.out.println("Chunk "+i+" has length "+len);

		// Put this substring into the next array
		// position. Notice the tricky coding such that we
		// update the value of the offset to the new one and
		// use the return value of the assignment.
		chunks[i] = pickle.substring(off, off += len);

	    }

	    // Now we need to convert the string array into jenesis
	    // form.  Make a "new String[]{ "chunk1", "chunk2",
	    // "chunk3" }" expression.
	    NewArray newArray = vm.newArray(vm.newType("String")).addDim();
	    newArray.setInitializer(vm.newArrayInit(BurnTools.make1DStringArray(chunks)));

	    return newArray;

	} else {

	    // The string will fit fine into the classfile.  No
	    // special treatment in this case.
	    return vm.newString(pickle);

	}
    }

    private static VirtualMachine vm = VirtualMachine.getVirtualMachine();
}





