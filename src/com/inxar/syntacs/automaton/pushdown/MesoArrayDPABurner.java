/**
 * $Id: MesoArrayDPABurner.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.pushdown;

import org.inxar.jenesis.*;
import org.inxar.syntacs.util.Burner;
import org.inxar.syntacs.util.Log;
import com.inxar.syntacs.util.BurnTools;
import com.inxar.syntacs.util.Pickler;
import com.inxar.syntacs.util.Mission;

/**
 * Utility class which transforms an instance of
 * <code>MesoArrayDPA</code> to a corresponding source code
 * representation.
 */
public class MesoArrayDPABurner implements Burner
{
    private static final boolean DEBUG = false;

    // Note: An initial implementation used a static initialization
    // block to setup all the data structures.  This was problematic
    // because it was generating "java.lang.ClassFormatError:
    // com/inxar/rsl/parser/RSL_DPA (Code of a method longer than
    // 65535 bytes)" errors on "large" grammars.  So I decided to
    // break up the initialization into several named static mehthods.

    public MesoArrayDPABurner()
    {
	this.vm = VirtualMachine.getVirtualMachine();
    }

    public void burn(Object src, ClassDeclaration cls)
    {
	if (DEBUG)
	    log().debug()
		.write("src is ").write(src)
		.out();

	if ( ! (src instanceof MesoArrayDPA) )
	    throw new ClassCastException
		("I can only work instances of " + MesoArrayDPA.class.getName() +
		 ", not `" + (src == null ? "null" : src.getClass().getName()) + "'.");

	this.dpa = (MesoArrayDPA)src;

	cls.getUnit().addImport("com.inxar.syntacs.automaton.pushdown.MesoArrayDPA");
	cls.setExtends("MesoArrayDPA");

        // now lets make a public no-arg constructor
        Constructor con = cls.newConstructor();

	// and lets add an (unqualified==null) super class constructor
	// invocation
	con.newStmt(vm.newInvoke(null, "super")
		    .addArg(vm.newVar("_action"))
		    .addArg(vm.newVar("_go"))
		    .addArg(vm.newVar("_actions"))
		    );

	// ok, add all the private static fields
	addIntArrayField(cls, 2, "_action");
	addIntArrayField(cls, 2, "_go");
	addIntArrayField(cls, 1, "_actions");

	// ok, looks good.  Now do the static initialization block
	StaticInitializer si = cls.newStaticInitializer();

	// Check if should /not/ pickle.
	if (Mission.control().isFalse("compile-pickle") ||
	    Mission.control().isFalse("compile-dpa-pickle"))
	    initStaticInitializerNotPickle(cls, si);
	else
	    initStaticInitializerPickle(cls, si);
    }

    protected void initStaticInitializerNotPickle(ClassDeclaration cls,
						  StaticInitializer si)
    {
	si.newStmt(vm.newInvoke(null, "initAction"));
	si.newStmt(vm.newInvoke(null, "initGo"));
	si.newStmt(vm.newInvoke(null, "initActions"));

	// make the initialization class methods
	ClassMethod m1 = addVoidMethod(cls, "initAction");
	ClassMethod m2 = addVoidMethod(cls, "initGo");
	ClassMethod m3 = addVoidMethod(cls, "initActions");

	// init the action[][] array
	BurnTools.init2DArray(m1, "_action", dpa.action);
	// make the go[][] array
	BurnTools.init2DArray(m2, "_go", dpa.go);
	// make the actions[] array
	BurnTools.init1DArray(m3, "_actions", dpa.actions);
    }

    protected void initStaticInitializerPickle(ClassDeclaration cls,
					       StaticInitializer si)
    {
	cls.getUnit().addImport("com.inxar.syntacs.util.Pickler");

	Invoke invoke = null;

	invoke = vm.newInvoke("Pickler", "unpickle2D")
	    .addArg(BurnTools.split(Pickler.pickle(dpa.action)));
	si.newStmt( vm.newAssign( vm.newVar("_action"), invoke ) );

	invoke = vm.newInvoke("Pickler", "unpickle2D")
	    .addArg(BurnTools.split(Pickler.pickle(dpa.go)));
	si.newStmt( vm.newAssign( vm.newVar("_go"), invoke ) );

	invoke = vm.newInvoke("Pickler", "unpickle1D")
	    .addArg(BurnTools.split(Pickler.pickle(dpa.actions)));
	si.newStmt( vm.newAssign( vm.newVar("_actions"), invoke ) );
    }

    protected ClassField addIntArrayField(ClassDeclaration cls, int dims, String name)
    {
	ClassField f = cls.newField(vm.newArray(Type.INT, dims), name);
	f.setAccess(Access.PRIVATE);
	f.isStatic(true);
	return f;
    }

    protected ClassMethod addVoidMethod(ClassDeclaration cls, String name)
    {
	ClassMethod m = cls.newMethod(vm.newType(Type.VOID), name);
	m.setAccess(Access.PRIVATE);
	m.isStatic(true);
	return m;
    }

    private Log log()
    {
	if (this.log == null)
	    this.log = Mission.control().log("mpb", this); // Meso Pushdown Burner
	return log;
    }

    protected MesoArrayDPA dpa;
    protected VirtualMachine vm;
    private Log log;
}
