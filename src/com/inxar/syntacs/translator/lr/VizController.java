/**
 * $Id: VizController.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;

import com.inxar.syntacs.automaton.finite.TreeDFA;
import org.inxar.syntacs.automaton.pushdown.DPAConstructor;
import org.inxar.syntacs.util.Log;
import org.inxar.syntacs.util.Vizualizable;
import org.inxar.syntacs.util.GraphViz;

import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.StringTools;
import com.inxar.syntacs.util.Dot;


/**
 * The <code>VizController</code> is centrally responsible for
 * overseeing and coordinating the generation of GraphViz dot files
 * for various objects.
 */
public class VizController
{
    private static final boolean DEBUG = true;

    private static boolean verbose =
	Mission.control().isTrue("verbose");

    public VizController()
    {
    }

    public void viz()
    {
	if (Mission.control().isFalse("viz"))
	    return;

	init();
	vizLexical();
	vizSyntactic();
    }

    private void init()
    {
	srcpath = Mission.control().getString("viz-sourcepath");

	if (srcpath == null)
	    srcpath = Mission.control().getString("compile-sourcepath", ".");

	String namespace = Mission.control().getString("viz-namespace");

	if (namespace == null)
	    namespace = Mission.control().getString("compile-namespace");

	srcpath = StringTools.getPath(srcpath, namespace);

	String author    = Mission.control().getString("author");
	String email     = Mission.control().getString("author-email");
	this.copyright = Mission.control().getString("copyright");

	if (author != null) {
	    if (email != null)
		author += " - " + email;
	} else {
	    if (email != null)
		author = email;
	}

	if (copyright == null) {
	    copyright = "Copyright (C) " + (new Date().getYear() + 1900);
	    if (author != null)
		copyright += " " + author;
	    else
		copyright += " unattributed";
	}
    }

    private void vizLexical()
    {
	if (Mission.control().isNotTrue("viz-lexical"))
	    return;

	String[] names = (String[])
	    Mission.control().get("_dfa-names");

	TreeDFA[] dfas = (TreeDFA[])
	    Mission.control().get("_tree-dfas");

	GraphViz dot;
	for (int i = 0; i < dfas.length; i++) {
	    dot = new Dot(names[i]);
	    dfas[i].vizualize(dot);
	    emit(names[i], dot);
	}
    }

    private void vizSyntactic()
    {
	if (Mission.control().isNotTrue("viz-syntactic"))
	    return;

	String name = Mission.control().getString("_dpa-name");
	DPAConstructor ctor = (DPAConstructor)Mission.control().get("_dpa-constructor");

	if (ctor instanceof Vizualizable) {
	    GraphViz dot = new Dot(name);
	    ((Vizualizable)ctor).vizualize(dot);

	    String mthd = Mission.control().getString("compile-dpa-constructor-method");
	    if (mthd != null)
		name += "." + mthd.toLowerCase();

	    emit(name, dot);
	} else {
	    log().info()
		.write("Sorry, "+ctor.getClass().getName()+
		       " does not support GraphViz.  Aborting DPA vizualization.")
		.out();
	}

    }

    private void emit(String name, GraphViz dot)
    {
	dot.comment(copyright);

	BufferedWriter out = null;

	try {

	    String filename = srcpath + name + ".dot";
	    out = new BufferedWriter
		(new FileWriter(filename));

	    if (verbose)
		log().debug()
		    .write("Writing ").write(filename).write("...")
		    .time();

	    out.write(dot.toString());
	    out.close(); out = null;

	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    if (verbose)
		log().debug()
		    .touch();

	    if (out != null)
		try { out.close(); }
		catch (Exception ex) {}
	}
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("vct", this); // Vizualization ConTroller
	return log;
    }

    private String srcpath;
    private String copyright;
    private Log log;
}
