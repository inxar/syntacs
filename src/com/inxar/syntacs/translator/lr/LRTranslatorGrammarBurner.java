/**
 * $Id: LRTranslatorGrammarBurner.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator.lr;

import java.util.Properties;
import org.inxar.jenesis.*;
import org.inxar.syntacs.translator.*;
import org.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * A <code>Burner</code> which creates
 * <code>LRTranslatorGrammar</code> implementations.
 */
public class LRTranslatorGrammarBurner implements Burner
{
    private static final boolean DEBUG = false;

    // ================================================================
    // CONSTRUCTORS
    // ================================================================

    public LRTranslatorGrammarBurner()
    {
	this.vm = VirtualMachine.getVirtualMachine();

	this.v_ID = "ID";

	this.l_null = vm.newNull();
	this.l_UNP = vm.newInt(LRTranslatorGrammar.UNDEFINED_PRODUCTION);
	this.l_UNC = vm.newInt(LRTranslatorGrammar.UNDEFINED_CONTEXT);
	this.l_UNT = vm.newInt(LRTranslatorGrammar.UNDEFINED_TERMINAL);

	this.t_void = vm.newType(Type.VOID);
	this.t_boolean = vm.newType(Type.BOOLEAN);
	this.t_int = vm.newType(Type.INT);
	this.t_int_2D = vm.newArray(Type.INT, 2);
	this.t_Object = vm.newType("Object");
	this.t_String = vm.newType("String");
	this.t_IntArray = vm.newType("IntArray");
	this.t_ArrayIntArray = vm.newType("ArrayIntArray");
	this.t_Translator = vm.newType("Translator");
    }

    // ================================================================
    // PUBLIC METHODS
    // ================================================================

    public void burn(Object src, ClassDeclaration cls)
    {
	if (! (src instanceof LRTranslatorGrammar) )
	    throw new IllegalArgumentException
		("I can only generate instances of " + LRTranslatorGrammar.class +
		 ", not " + (src == null ? "null" : src.getClass().toString()));

	this.g = (LRTranslatorGrammar)src;
	this.cls = cls;
	cls.setAccess(Access.PUBLIC);

	cls.getUnit().addImport("org.inxar.syntacs.translator.Translator");
	cls.getUnit().addImport("org.inxar.syntacs.translator.lr.LRTranslator");
	cls.getUnit().addImport("org.inxar.syntacs.translator.lr.LRTranslatorGrammar");
	cls.getUnit().addImport("org.inxar.syntacs.util.IntArray");
	cls.getUnit().addImport("com.inxar.syntacs.util.ArrayIntArray");
	cls.getUnit().addImport("org.inxar.syntacs.automaton.finite.DFA");

	cls.addImplements("LRTranslatorGrammar");

	newMethod_getName();
	newMethod_getVersion();
	newMethod_getStartContext();
	newMethod_getGoalNonTerminal();

	newMethod_getContexts();
	newMethod_getTerminals();
	newMethod_getNonTerminals();
	newMethod_getProductions();

	newMethod_getContext();
	newMethod_getTerminal();
	newMethod_getNonTerminal();
	newMethod_getProduction();
	newMethod_getTerminalRegexp();
	newMethod_getProductionLength();
	newMethod_getProductionNonTerminal();

	newMethod_getProductionSymbols();
	newMethod_getContextTerminals();
	newMethod_getTerminalContexts();

	newMethod_getContextActionAndRegister();

	newMethod_newTranslator();
	newMethod_newTranslator3();

	String s = "<PRE>" + g.toString() + "</PRE>";
	Comment c = cls.getComment();

	if (c != null) {
	    s = c.getText() + "<P>" + s;
	    c.setText(s);
	} else
	    cls.setComment(Comment.D, s);

    }

    // ================================================================
    // INTERFACE METHODS
    // ================================================================

    private void newMethod_getName()
    {
	newMethod(t_String, "getName")
	    .newReturn().setExpression( vm.newString(g.getName()) );
    }

    private void newMethod_getVersion()
    {
	newMethod(t_String, "getVersion")
	    .newReturn().setExpression( vm.newString(g.getVersion()) );
    }

    private void newMethod_getStartContext()
    {
	newMethod(t_int, "getStartContext")
	    .newReturn().setExpression( vm.newInt( g.getStartContext()) );
    }

    private void newMethod_getGoalNonTerminal()
    {
	newMethod(t_int, "getGoalNonTerminal")
	    .newReturn().setExpression( vm.newInt( g.getGoalNonTerminal()) );
    }

    private void newMethod_getContexts()
    {
	newMethod_IntArrayField("getContexts", "contexts", g.getContexts());
    }

    private void newMethod_getTerminals()
    {
	newMethod_IntArrayField("getTerminals", "terminals", g.getTerminals());
    }

    private void newMethod_getNonTerminals()
    {
	newMethod_IntArrayField("getNonTerminals", "nonTerminals", g.getNonTerminals());
    }

    private void newMethod_getProductions()
    {
	newMethod_IntArrayField("getProductions", "productions", g.getProductions());
    }

    private void newMethod_getContext()
    {
	IntArray keys = g.getContexts();
	Expression[] vals = new Expression[ keys.length() ];

	for (int i = 0; i < keys.length(); i++) {
	    int cID = keys.at(i);
	    String cName = g.getContext(cID);
	    vals[i] = vm.newString(cName);
	    newConstant("Constant ID for " + cName, "C_" + cName, cID);
	}

	newMethod_switch(t_String, "getContext", v_ID, keys, vals, l_null);
    }

    private void newMethod_getTerminal()
    {
	IntArray keys = g.getTerminals();
	Expression[] vals = new Expression[ keys.length() ];
	for (int i = 0; i < keys.length(); i++) {
	    int tID = keys.at(i);
	    String tName = g.getTerminal(tID);
	    vals[i] = vm.newString(tName);
	    newConstant("Terminal ID for " + tName, "T_" + tName, tID);
	}

	newMethod_switch(t_String, "getTerminal", v_ID, keys, vals, l_null);
    }

    private void newMethod_getNonTerminal()
    {
	IntArray keys = g.getNonTerminals();
	Expression[] vals = new Expression[ keys.length() ];
	for (int i = 0; i < keys.length(); i++) {
	    int ntID = keys.at(i);
	    String ntName = g.getNonTerminal(ntID);
	    vals[i] = vm.newString(ntName);
	    newConstant("NonTerminal ID for " + ntName, "N_" + ntName, ntID);
	}

	newMethod_switch(t_String, "getNonTerminal", v_ID, keys, vals, l_null);
    }

    private void newMethod_getProduction()
    {
	IntArray keys = g.getProductions();
	Expression[] vals = new Expression[ keys.length() ];
	for (int i = 0; i < keys.length(); i++) {
	    int pID = keys.at(i);
	    String pName = g.getProduction(pID);
	    String pConstantName = pName.replace(':', '_').replace(' ', '_');
	    vals[i] = vm.newString(pName);
	    newConstant("Production ID for " + pConstantName, "P_" + pConstantName, pID);
	}
	newMethod_switch(t_String, "getProduction", v_ID, keys, vals, l_null);
    }

    private void newMethod_getTerminalRegexp()
    {
	IntArray keys = g.getTerminals();
	Expression[] vals = new Expression[ keys.length() ];
	for (int i = 0; i < keys.length(); i++) {
	    Object o = g.getTerminalRegexp( keys.at(i) );
	    if (o != null)
		vals[i] = vm.newString(o.toString());
	    else
		vals[i] = l_null;
	}

	newMethod_switch(t_Object, "getTerminalRegexp", v_ID, keys, vals, l_null);
    }

    private void newMethod_getProductionLength()
    {
	IntArray keys = g.getProductions();
	Expression[] vals = new Expression[ keys.length() ];
	for (int i = 0; i < keys.length(); i++)
	    vals[i] = vm.newInt(g.getProductionLength( keys.at(i) ));

	newMethod_switch(t_int, "getProductionLength", v_ID, keys, vals, l_UNP);
    }

    private void newMethod_getProductionNonTerminal()
    {
	IntArray keys = g.getProductions();
	Expression[] vals = new Expression[ keys.length() ];
	for (int i = 0; i < keys.length(); i++)
	    vals[i] = vm.newInt(g.getProductionNonTerminal( keys.at(i) ));

	newMethod_switch(t_int, "getProductionNonTerminal", v_ID, keys, vals, l_UNP);
    }

    private void newMethod_getProductionSymbols()
    {
	IntArray keys = g.getProductions();
	int[][] vals = new int[ keys.length() ][];
	Expression[] e = new Expression[ keys.length() ];

	for (int i = 0; i < keys.length(); i++) {
	    IntArray syms = g.getProductionSymbols( keys.at(i) );

	    if (syms != null) {
		vals[i] = new int[ syms.length() ];
		for (int j = 0; j < syms.length(); j++)
		    vals[i][j] = syms.at(j);

		e[i] =
		    newIntArray( vm.newArrayAccess(null, "productionSymbols")
				 .addDim(vm.newInt(i)) );
	    } else {
		vals[i] = null;
		e[i] = l_null;
	    }

	}

	newField_int2D("productionSymbols", vals);

   	newMethod_switch(t_IntArray, "getProductionSymbols", v_ID, keys, e, l_null);
    }

    private void newMethod_getContextTerminals()
    {
	IntArray keys = g.getContexts();
	int[][] vals = new int[ keys.length() ][];
	Expression[] e = new Expression[ keys.length() ];

	for (int i = 0; i < keys.length(); i++) {
	    IntArray syms = g.getContextTerminals( keys.at(i) );

	    if (syms != null) {
		vals[i] = new int[ syms.length() ];
		for (int j = 0; j < syms.length(); j++) {
		    vals[i][j] = syms.at(j);
		}
		e[i] =
		    newIntArray( vm.newArrayAccess(null, "contextTerminals")
				 .addDim(vm.newInt(i)) );
	    } else {
		vals[i] = null;
		e[i] = l_null;
	    }

	}

	newField_int2D("contextTerminals", vals);

   	newMethod_switch(t_IntArray, "getContextTerminals", v_ID, keys, e, l_null);
    }

    private void newMethod_getTerminalContexts()
    {
	IntArray keys = g.getTerminals();
	int[][] vals = new int[ keys.length() ][];
	Expression[] e = new Expression[ keys.length() ];

	for (int i = 0; i < keys.length(); i++) {
	    IntArray syms = g.getTerminalContexts( keys.at(i) );

	    if (syms != null) {

		vals[i] = new int[ syms.length() ];

		for (int j = 0; j < syms.length(); j++)
		    vals[i][j] = syms.at(j);

		e[i] =
		    newIntArray( vm.newArrayAccess(null, "terminalContexts")
				 .addDim(vm.newInt(i)) );
	    } else {
		vals[i] = null;
		e[i] = l_null;
	    }

	}

	newField_int2D("terminalContexts", vals);

   	newMethod_switch(t_IntArray, "getTerminalContexts", v_ID, keys, e, l_null);
    }

    private void newMethod_getContextActionAndRegister()
    {
	IntArray c = g.getContexts();

	// Initialize an array such that each expression is a method
	// call to the sublayer. A is for action method, R is for
	// register method.
	Expression[] eA = new Expression[ c.length() ];
	Expression[] eR = new Expression[ c.length() ];

	// Iterate all the contexts in the grammar.
	for (int i = 0; i < c.length(); i++) {

	    int cID = c.at(i);
	    String cName = StringTools.capitalize(g.getContext(cID));
	    IntArray t = g.getContextTerminals(cID);

	    // Format some method names
	    String mNameA = "get" + cName + "ContextAction";
	    String mNameR = "get" + cName + "ContextRegister";

	    // Now we want to group terminals by action and register
	    // such that we can build a case statement.  Basically
	    // function inversion.
	    IntSet template = new ListIntSet();

	    // "actions" is a mapping from the set A (a is an
	    // ACTION_XXX) to a set T (t is a terminalID) such that
	    // getContextAction(c, t) == a.
	    IntRelation actions = new TreeListIntRelation();

	    // "registers" is a mapping from the set R (r is the value
	    // of some register) to a set T (t is a terminalID) such
	    // that getContextAction(c, t) == r.
	    IntRelation registers = new TreeListIntRelation();

	    // Iterate through each terminal and put action and
	    // register in the functions.
	    for (int j = 0; j < t.length(); j++) {
		int tID = t.at(j);
		actions.put(g.getContextAction(cID, tID), tID);
		registers.put(g.getContextRegister(cID, tID), tID);
	    }

	    // Now generate a switch method for each context in the
	    // grammar such that the action (or register) is the switch
	    // constant.
	    newMethod_switch_inverse(mNameA, "tID", actions, l_UNT);
	    newMethod_switch_inverse(mNameR, "tID", registers, l_UNT);

	    // Last thing is to create the expression for the method
	    // call to the sublayer
	    eA[i] = vm.newInvoke(null, mNameA).addArg( vm.newVar("tID") );
	    eR[i] = vm.newInvoke(null, mNameR).addArg( vm.newVar("tID") );
	}

	// Finally add the top-level switch methods in.
	newMethod_switch(t_int, "getContextAction", "cID", c, eA, l_UNC)
	    .addParameter(t_int, "tID");

	newMethod_switch(t_int, "getContextRegister", "cID", c, eR, l_UNC)
	    .addParameter(t_int, "tID");

    }

    private void newMethod_newTranslator()
    {
	ClassMethod m = newMethod(t_Translator, "newTranslator");
	Invoke inv = vm.newInvoke(null, "newTranslator").addArg(l_null);
	m.newReturn().setExpression(inv);
    }

    private Type getType(String property)
    {
	String typeName = Mission.control().getString(property);
	if (typeName == null)
	    throw new IllegalArgumentException
		("Expected property `"+property+"' to hold a the name of a class.");

	return vm.newType(typeName);
    }

    private void newMethod_newTranslator2()
    {
	ClassMethod m = newMethod(t_Translator, "newTranslator");
	m.addParameter(vm.newType("Properties"), "properties");

	Type t_dpa = getType("dpa-class");
	Type t_StandardTranslator = vm.newType("StandardTranslator");

	String[] dfaNames = (String[])Mission.control().get("_dfa-names");

	Expression e_interpreter = null;
	if (p.containsKey("interpreter-class"))
	    e_interpreter = vm.newClass(getType("interpreter-class"));
	else
	    e_interpreter = l_null;

	NewClass nc = vm.newClass(t_StandardTranslator)
	    .addArg( vm.newVar("properties") )
	    .addArg( vm.newVar("this") )
	    .addArg( makeDFAArray(dfaNames) )
	    .addArg( vm.newClass(t_dpa) )
	    .addArg( e_interpreter );

	m.newReturn().setExpression(nc);
    }
    private void newMethod_newTranslator3()
    {
	cls.getUnit().addImport("org.inxar.syntacs.analyzer.Input");
	cls.getUnit().addImport("org.inxar.syntacs.analyzer.lexical.Lexer");
	cls.getUnit().addImport("org.inxar.syntacs.analyzer.syntactic.Parser");
	cls.getUnit().addImport("org.inxar.syntacs.translator.lr.LRTranslatorInterpreter");
	cls.getUnit().addImport("java.util.Properties");

	ClassMethod m = newMethod(t_Translator, "newTranslator");
	m.addParameter(vm.newType("Properties"), "p");

	Type t_LRTranslator = vm.newType("LRTranslator");
	String trans = Mission.control().get("_translator").getClass().getName();
	Type t_trans = vm.newType(trans);
	m.newLet(t_LRTranslator).addAssign("t", vm.newClass(t_trans));

	m.newStmt(vm.newInvoke("t", "setLRTranslatorGrammar").addArg(vm.newVar("this")));
	m.newStmt(vm.newInvoke("t", "setProperties").addArg(vm.newVar("p")));

	set(m, "Input", "_input", "input", "setInput");
	set(m, "Lexer", "_lexer", "lexer", "setLexer");
	set(m, "Parser", "_parser", "parser", "setParser");
	set(m, "LRTranslatorInterpreter", "_interpreter", "interp",
	    "setLRTranslatorInterpreter");

	// Initialize the lexer
	String[] dfaNames = (String[])Mission.control().get("_dfa-names");
	Expression[] ae = new Expression[dfaNames.length];
	for (int i = 0; i < ae.length; i++)
	    ae[i] = vm.newClass(vm.newType(dfaNames[i]));

	NewArray na = vm.newArray(vm.newType("DFA")).addDim();
	na.setInitializer(vm.newArrayInit(ae));
	m.newStmt(vm.newInvoke("lexer", "initialize").addArg(na));

	// Initialize the parser
	String dpaName = Mission.control().getString("_dpa-name");
	m.newStmt(vm.newInvoke("parser", "initialize")
		  .addArg(vm.newClass(vm.newType(dpaName))));

	// Add the return statement
	m.newReturn().setExpression(vm.newVar("t"));
    }

    private void set(ClassMethod m,
		     String className,
		     String implKey,
		     String varName,
		     String methodName)
    {
	String implName = Mission.control().get(implKey).getClass().getName();
	Type implType = vm.newType(implName);
	Type classType = vm.newType(className);

	m.newLet(classType).addAssign(varName, vm.newClass(implType));
	m.newStmt(vm.newInvoke("t", methodName).addArg(vm.newVar(varName)));
    }

    private Expression makeDFAArray(String[] classnames)
    {
	// ---------------------------------------------------------
	// This is the type of expression we want to create:
	//
	// new DFA[]{ new DefaultContext(), new CharclassContext() }
	// ---------------------------------------------------------

	Expression[] ae = new Expression[classnames.length];
	for (int i = 0; i < ae.length; i++)
	    ae[i] = vm.newClass(vm.newType(classnames[i]));

	Type tt_dfa = vm.newArray("DFA", 1);
	NewArray a = vm.newArray(tt_dfa);
	a.setInitializer(vm.newArrayInit(ae));

	return a;
    }

    // ================================================================
    // METHOD FABS
    // ================================================================

    private ClassMethod newMethod_IntArrayField(String mName, String fName, IntArray a)
    {
	// First add an IntArray field.
	newField(t_IntArray, fName).setExpression( newIntArray(a.toArray()) );

	// Now add a simple return method.
	ClassMethod m = newMethod(t_IntArray, mName);
	m.newReturn().setExpression( vm.newVar(fName) );
	return m;
    }

    private ClassMethod newMethod_switch(Type type,
					 String mName,
					 String pName,
					 IntArray keys,
					 Expression[] vals,
					 Expression def)
    {
	ClassMethod m = newMethod(type, mName);
	m.addParameter(t_int, pName);

	Switch s = m.newSwitch( vm.newVar(pName));
	for (int i = 0; i < keys.length(); i++)
	    s.newCase( vm.newInt( keys.at(i)) )
		.newReturn()
		.setExpression( vals[i] );

	s.getDefault().newReturn().setExpression( def );

	return m;
    }

    private void newMethod_switch_inverse(String mName,
					  String pName,
					  IntRelation f,
					  Expression def)
    {
	ClassMethod m = cls.newMethod(t_int, mName);
	m.addParameter(t_int, pName);
	m.setAccess(Access.PRIVATE);

	Switch s = m.newSwitch( vm.newVar(pName) );

	for (Reiterator i = f.reiterator(); i.hasNext(); i.next()) {

	    int key = i.key();
	    IntArray values = i.values().toIntArray();

	    if (values.length() > 0) {
		Case last = null;
		for (int j = 0; j < values.length(); j++)
		    last = s.newCase( vm.newInt(values.at(j)) );
		last.newReturn().setExpression( vm.newInt(key) );
	    } else {
		throw new InternalError();
	    }
	}

	s.getDefault().newReturn().setExpression( def );
    }

    private ClassMethod newMethod(Type type, String name)
    {
	ClassMethod m = cls.newMethod(type, name);
	m.setAccess(Access.PUBLIC);
	return m;
    }

    // ================================================================
    // FIELD FABS
    // ================================================================

    private ClassField newField_int2D(String fName, int[][] a)
    {
	ClassField f = newField(t_int_2D, fName);
    	f.setExpression( BurnTools.newIntIntArray(a) );
	return f;
    }

    private ClassField newField(Type type, String name)
    {
	ClassField f = cls.newField(type, name);
	f.setAccess(Access.PRIVATE);
	return f;
    }

    private ClassField newConstant(String comment, String name, int val)
    {
	ClassField f = cls.newField(t_int, name);
	f.setAccess(Access.PUBLIC);
	f.isStatic(true);
	f.isFinal(true);
	f.setExpression(vm.newInt(val));
	f.setComment(Comment.D, comment);
	return f;
    }

    // ================================================================
    // EXPRESSIONs FABS
    // ================================================================

    private NewClass newIntArray(int[] a)
    {
	return newIntArray( BurnTools.newIntArray(a) );
    }

    private NewClass newIntArray(Expression e)
    {
	return vm.newClass(t_ArrayIntArray).addArg( e );
    }

    private Invoke newInvoke_int(String mName, int i)
    {
	return vm.newInvoke(null, mName).addArg( vm.newInt(i) );
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("lrtg-burner", this);
	return log;
    }

    // ================================================================
    // TYPES AND FIELDS
    // ================================================================

    private Literal l_UNP;
    private Literal l_UNC;
    private Literal l_UNT;
    private Literal l_null;

    private Type t_void;
    private Type t_boolean;
    private Type t_int;
    private Type t_int_2D;
    private Type t_String;
    private Type t_Object;
    private Type t_IntArray;
    private Type t_ArrayIntArray;
    private Type t_Translator;

    private String v_ID;

    private Properties p;
    private ClassDeclaration cls;
    private VirtualMachine vm;
    private LRTranslatorGrammar g;
    private Log log;
}
