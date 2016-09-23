/**
 * $Id: Interpret.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.inxar.syntacs.translator.Translator;
import org.inxar.syntacs.translator.TranslatorGrammar;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.util.Log;

import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.Arboreal;
import com.inxar.syntacs.util.Tree;

/**
 * <code>Interpret</code> is the command-line interface to execute test
 * translations.  Two arguments are required: <OL> <LI> The classname
 * of the grammar which implements <code>TranslatorGrammar</code> <LI>
 * A string to translate.  The string is first interpreted to be the
 * name of a file; if that fails, it passes the string directly to be
 * translated.</OL> <code>Properties</code> to be passed to the
 * translator may be expressed using the -D option.  <P> Two Examples:
 *
 * <pre>
 * java -classpath $CLASSPATH \
 *   com.inxar.syntacs.Interpret \
 *   com.inxar.syntacs.translator.test.AbbGrammar "t+f*(id+f)"
 * </pre>
 *
 * <pre>
 * java -classpath $CLASSPATH \
 *   com.inxar.syntacs.Interpret \
 *   -Drun-parser-debug=true \
 *   com.inxar.syntacs.translator.test.AbbGrammar ./input-file.txt
 * </pre>
 */
public class Interpret {
  private static final boolean DEBUG = true;

  private Interpret() {}

  /**
   * Runs the tool; invoke this method with the classname of the
   * grammar to use and either a string to parse or the name of a
   * file to parse.
   */
  public static void main(String[] argv) {
    Mission.control().put("verbose", Boolean.TRUE);
    Properties p = new Properties();
    //p.setProperty("run-parser-debug", "true");
    //p.setProperty("run-print-parse-tree", "true");

    int idx = parseopts(argv, p);
    if (idx != argv.length - 2)
      usage(
          "The last two arguments must be the name of the " + "grammar class and an input string.");

    String className = argv[idx];
    String input = argv[idx + 1];

    verbose = Mission.control().isTrue("verbose");

    Translator t = null;
    try {

      // Load the translator grammar.
      TranslatorGrammar tg = (TranslatorGrammar) Class.forName(className).newInstance();

      //p.setProperty("run-lexer-debug", "false");
      //p.setProperty("run-parser-debug", "true");

      // Fetch a translator.
      t = tg.newTranslator(p);

    } catch (Exception ex) {
      System.err.println("Could not instantiate translator for " + className + ".");
      ex.printStackTrace();
    }

    Reader in = null;
    try {

      in = new BufferedReader(new FileReader(input));

      if (verbose) log().debug().write("Translating file ").write(input).write("...").time();

      parse(t, in);

      if (verbose) log().debug().touch();

    } catch (FileNotFoundException fnfex) {

      if (verbose) log().debug().write("Translating string \"").write(input).write("\"").time();

      in = new StringReader(input);
      parse(t, in);

      if (verbose) log().debug().touch();

    } finally {
      if (in != null)
        try {
          in.close();
          in = null;
        } catch (Exception ex) {
        }
    }

    Mission.deactivate();
  }

  private static void parse(Translator t, Reader in) {
    try {

      Object o = t.translate(in);

      if ("true".equals(t.getProperties().getProperty("run-print-parse-tree"))) {

        if (o instanceof Arboreal) {

          Tree tree = true ? new Tree("TOP") : new Tree.Box("TOP");

          ((Arboreal) o).toTree(tree);

          System.out.println();
          System.out.println("Parse Tree:");
          System.out.println(tree);

        } else {

          System.out.println(
              "Cannot print parse tree: result must " + "implement interface Arboreal.");
        }

      } else {
      }

      System.out.println("Parse Result:");
      System.out.println(o);
      System.out.println();

    } catch (TranslationException tex) {
      System.out.println();
      System.out.println(tex.getAuditor());
    }
  }

  private static Log log() {
    if (log == null) log = Mission.control().log("run", new Interpret());
    return log;
  }

  private static Log log;
  private static boolean verbose;

  public static void pause(int len) {
    try {
      Thread thr = Thread.currentThread();
      thr.sleep(len);
    } catch (Exception ex) {
    }
  }

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
        splitopt(opts[++i], p);
      }

      // Handle case where value is concatentated to the
      // option.
      else if (opts[i].startsWith("-D")) {
        splitopt(opts[i].substring(2), p);
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

  static void splitopt(String keyval, Properties p) {
    int eq = keyval.indexOf('=');
    //System.out.println("eq: "+eq);
    // Equals sign must exist and not be at the very end
    // of the string.
    if (eq < 0 || eq > keyval.length() - 1)
      usage("Argument to the -D option must be a key=val " + "pair with no intervening space");

    String key = keyval.substring(0, eq).trim();
    String val = keyval.substring(eq + 1).trim();
    //System.out.println("key = "+key+", val = "+val);
    p.put(key, val);
  }

  private static void usage(String msg) {
    System.err.println("|");
    System.err.println("| Error: " + msg);
    System.err.println("|");
    System.err.println(
        "| Usage: java com.inxar.syntacs.Interpret [options] "
            + "<grammar-classname> <input-string>");
    System.err.println("|");
    System.err.println("| Options:");
    System.err.println("|  -D<key=val>...........Set translator property");
    System.err.println("|");
    System.err.println("| Requirements:");
    System.err.println(
        "|  <grammar-classname>...The name of the translator grammar to instantiate");
    System.err.println("|  <input-string>........The name of a file or a raw string to parse");
    System.err.println("|");

    System.exit(-1);
  }
}
