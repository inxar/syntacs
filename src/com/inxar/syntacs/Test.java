/**
 * $Id: Test.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
 *
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package com.inxar.syntacs;

import java.io.*;
import java.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.translator.*;
import com.inxar.syntacs.translator.*;
import org.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.translator.regexp.*;
import com.inxar.syntacs.translator.syntacs.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * <code>Test</code> is the command-line interface to execute a set of
 * test translations.  The filename of the XML instance of the DTD
 * <code>test.dtd</code> is required. <code>Properties</code> to be
 * passed to the translator may be expressed using the -D option.  <P>
 *
 * <pre>
 * java -classpath $CLASSPATH com.inxar.syntacs.Test ./grammar/abb/tests.xml
 * </pre>
 */
public class Test {
  private static final boolean DEBUG = true;

  private Test() {}

  /**
   * Runs the tool; invoke this method with the classname of the
   * grammar to use and either a string to parse or the name of a
   * file to parse.
   */
  public static void main(String[] argv) throws Exception {
    Properties p = new Properties();
    p.setProperty("run-parser-debug", "true");

    int idx = parseopts(argv, p);
    if (idx != argv.length - 1)
      usage("The last argument must be the filename or URI of the " + "tests.dtd XML instance.");

    String uri = argv[idx];
    if (!uri.startsWith("file:")) uri = "file:" + uri;

    log().info().write("Parsing ").write(uri).time();

    Element doc = DOM.getRoot(uri, true);
    ;
    String className = DOM.getString("grammar", doc);
    Translator t = ((TranslatorGrammar) Class.forName(className).newInstance()).newTranslator(p);

    log().info().touch();

    Auditor a = new StandardAuditor();

    String s = null;
    Iterator i = null;

    log().info().write("Running tests... ").time();

    i = DOM.getElements("accept", doc);
    while (i.hasNext()) {
      s = DOM.getText((Element) i.next());
      try {
        t.translate(s);
      } catch (TranslationException tex) {
        a.notify(
            Complaint.UNSPECIFIED_ERROR,
            "Input `" + s + "' was rejected despite expected acceptance.");
        List errs = tex.getAuditor().getErrors();
        if (errs != null) {
          Iterator iter = errs.iterator();
          while (iter.hasNext()) a.notify((Complaint) iter.next());
        }
      }
    }

    i = DOM.getElements("reject", doc);
    while (i.hasNext()) {
      s = DOM.getText((Element) i.next());
      try {
        t.translate(s);
        a.notify(
            Complaint.UNSPECIFIED_ERROR,
            "Input `" + s + "' was accepted despite expected rejection.");
      } catch (TranslationException tex) {
      }
    }

    log().info().touch();

    if (a.hasErrors()) System.out.println(a);
    else log().info().write("OK").out();

    Mission.deactivate();
  }

  private static Log log() {
    if (log == null) log = Mission.control().log("tst", new Test());
    return log;
  }

  private static Log log;

  private static int parseopts(String[] opts, Properties p) {
    int i = 0;
    while (i < opts.length) {
      //System.out.println("opts["+i+"]: "+opts[i]);
      // "--" Signals the end of options
      if (opts[i].equals("--")) return i;

      // All options must start with a dash
      if (!opts[i].startsWith("-")) return i;

      // Handle lone -D case.
      if ("-D".equals(opts[i])) {
        if (i + 1 == opts.length)
          throw new IllegalArgumentException("The -D option must have a valid key=val argument");
        Interpret.splitopt(opts[++i], p);
      }

      // Handle case where value is concatentated to the
      // option.
      else if (opts[i].startsWith("-D")) {
        Interpret.splitopt(opts[i].substring(2), p);
      }

      // Unknown option.
      else if (opts[i].startsWith("-")) usage("Unknown option `" + opts[i] + "'");

      // If not an option, we should stop.
      else return i;

      // Go to next option
      ++i;
    }

    return i;
  }

  private static void usage(String msg) {
    System.err.println("|");
    System.err.println("| Error: " + msg);
    System.err.println("|");
    System.err.println("| Usage: java com.inxar.syntacs.Test [options] <test-filename|uri>");
    System.err.println("|");
    System.err.println("| Options:");
    System.err.println("|  -D<key=val>...........Set translator property");
    System.err.println("|");
    System.err.println("| Requirements:");
    System.err.println(
        "|  <test-filename|uri>...The filename (or URI) of an XML instance of tests.dtd");
    System.err.println("|");

    System.exit(-1);
  }
}
