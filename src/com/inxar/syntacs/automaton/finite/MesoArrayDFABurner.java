/**
 * $Id: MesoArrayDFABurner.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.finite;

import java.io.*;
import java.util.*;
import org.inxar.jenesis.*;
import org.inxar.syntacs.automaton.finite.*;
import com.inxar.syntacs.automaton.finite.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Utility class which transforms an instance of MesoArrayDFA to a
 * corresponding source code representation.  
 */
public class MesoArrayDFABurner implements Burner
{
    public MesoArrayDFABurner()
    {
	this.vm = VirtualMachine.getVirtualMachine();
    }
    
    public void burn(Object src, ClassDeclaration cls)
    {
	if ( ! (src instanceof MesoArrayDFA) )
	    throw new ClassCastException
		("I can only work instances of " + MesoArrayDFA.class.getName() + 
		 ", not `" + src.getClass().getName() + "'.");

	this.dfa = (MesoArrayDFA)src;
	
	cls.getUnit().addImport("com.inxar.syntacs.automaton.finite.MesoArrayDFA");
	cls.setExtends("MesoArrayDFA");

        // now lets make a no-arg constructor
        Constructor con = cls.newConstructor();

	// and lets add an (unqualified==null) super class constructor invocation
	con.newStmt(vm.newInvoke(null, "super")
		    .addArg(vm.newVar("_table"))
		    .addArg(vm.newVar("_accepts")));
	
	// ok, add all the private static fields
	ClassField fld1 = cls.newField(vm.newArray(Type.INT, 2), "_table");
	ClassField fld2 = cls.newField(vm.newArray(Type.INT, 1), "_accepts");
	
	// set the access constrol and modifiers
	fld1.setAccess(Access.PRIVATE); fld1.isStatic(true); fld1.isFinal(true);
	fld2.setAccess(Access.PRIVATE); fld2.isStatic(true); fld2.isFinal(true);
	
	// ok, looks good.  Now do the static initialization block
	StaticInitializer si = cls.newStaticInitializer();

	// Check if should /not/ pickle.
	if (Mission.control().isFalse("compile-pickle") || 
	    Mission.control().isFalse("compile-dfa-pickle"))
	    
	    initStaticInitializerNotPickle(cls, si);
	else
	    initStaticInitializerPickle(cls, si);
    }

    protected void initStaticInitializerNotPickle(ClassDeclaration cls, 
						  StaticInitializer si)
    {
	// init the table[][] array
	init2DArray(si, "_table", dfa.table);
	// make the accepts[] array
	BurnTools.init1DArray(si, "_accepts", dfa.accepts);
    }

    protected void initStaticInitializerPickle(ClassDeclaration cls, 
					       StaticInitializer si)
    {
	cls.getUnit().addImport("com.inxar.syntacs.util.Pickler");

	Invoke invoke = null;
	
	invoke = vm.newInvoke("Pickler", "unpickle2D")
	    .addArg(BurnTools.split(Pickler.pickle(dfa.table)));
	si.newStmt( vm.newAssign( vm.newVar("_table"), invoke ) );

	invoke = vm.newInvoke("Pickler", "unpickle1D")
	    .addArg(BurnTools.split(Pickler.pickle(dfa.accepts)));
	si.newStmt( vm.newAssign( vm.newVar("_accepts"), invoke ) );
    }


    protected void init2DArray(Block block, String var, int[][] a)
    {
	// Make a new Map to hold (int[],int) keys.
	Map map = new HashMap();
	
	// set up the new array expressions
	NewArray na  = vm.newArray(vm.newType(Type.INT))
	    .addDim(vm.newInt(a.length))
	    .addDim();

	block.newStmt(vm.newAssign(vm.newVar(var), na));

        // iterate outer loop
        for (int i=0; i<a.length; i++) {

	    int[] sub = a[i];
	    Expression e = null;

	    // check for null arrays
	    if (sub == null) {
		e = vm.newNull();
	    } else if (map.containsKey(sub)) {
		int backindex = ((Integer)map.get(sub)).intValue();
		e = vm.newArrayAccess(null, var).addDim(vm.newInt(backindex));
	    } else {
		map.put(sub, new Integer(i));
		// make array initializer
		NewArray n = vm.newArray(vm.newType(Type.INT)).addDim();
		n.setInitializer(vm.newArrayInit(make1DIntArray(a[i])));
		e = n;
	    }
	    // make new assignment
	    block.newStmt( vm.newAssign(vm.newArrayAccess(null, var).addDim(vm.newInt(i)), e) );
	}
    }
    
    protected Object make1DIntArray(int[] a)
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

    private Log log()
    {
	if (this.log == null)
	    this.log = Mission.control().log("mfb", this); // Meso Finite Burner
	return log;
    }

    protected MesoArrayDFA dfa;
    protected VirtualMachine vm;
    private Log log;
}





