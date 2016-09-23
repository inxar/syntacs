/**
 * $Id: Compile.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.File;
import java.util.Properties;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.inxar.syntacs.translator.Translator;
import org.inxar.syntacs.translator.TranslatorGrammar;
import org.inxar.syntacs.util.Log;

import com.inxar.syntacs.translator.syntacs.SyntacsGrammar;
import com.inxar.syntacs.translator.lr.XML2LRTranslatorGrammarTransformer;
import com.inxar.syntacs.util.Mission;

/**
 * <code>Compile</code> is the command-line interface to compile
 * grammar files into Translators.  The argument should be the name of
 * the grammar file to compile (can be in XML or "native syntacs"
 * format.  <code>Properties</code> to be passed to the translator may
 * be expressed using the -D option.  <P> Two Examples:
 *
 * <pre>
 * java -classpath $CLASSPATH com.inxar.syntacs.Compile ./abb.stt
 * </pre>
 *
 * <pre>
 * java -classpath $CLASSPATH com.inxar.syntacs.Compile ./abb.xml
 * </pre>
 */
public class Compile {
  private static final boolean DEBUG = true;

  private Compile() {}

  /**
   * Runs the tool; invoke this method with the name of the grammar
   * to process (filename must end with <code>.xml</code> or
   * <code>.stt</code>).
   */
  public static void main(String[] argv) {
    Mission.control().put("verbose", Boolean.TRUE);

    Properties p = new Properties();
    //p.setProperty("run-parser-debug", "true");

    int idx = parseopts(argv, p);
    if (idx != argv.length - 1)
      usage("The last argument must be the name of the " + "grammar filename.");

    String infile = argv[idx];

    verbose = Mission.control().isTrue("verbose");

    if (verbose) log().debug().write("Compiling ").write(infile).time();

    TranslatorGrammar g = null;
    if (infile.endsWith(".xml")) processXML(infile, p);
    else if (infile.endsWith(".stt")) processSTT(infile, p);
    else {
      usage("Grammar file must be an .stt or .xml file");
    }

    if (verbose) log().debug().touch();

    Mission.deactivate();
  }

  /**
   * Processes an <code>XML</code> grammar and returns the
   * corresponding <code>Translator</code> for the grammar.
   */
  public static Translator processXML(String uri, Properties p) {
    if (uri.indexOf(':') == -1) uri = "file:" + uri;

    try {
      TranslatorGrammar g = new XML2LRTranslatorGrammarTransformer().transform(uri);

      return g.newTranslator(p);

    } catch (Exception ex) {
      System.err.println("Unable to create translator:");
      ex.printStackTrace();
    }
    return null;
  }

  /**
   * Processes an <code>STT</code> grammar and returns the
   * corresponding <code>Translator</code> for the grammar.
   */
  public static Translator processSTT(String file, Properties p) {
    try {

      Translator t = new SyntacsGrammar().newTranslator(p);
      TranslatorGrammar tg = (TranslatorGrammar) t.translate(new File(file));
      return tg.newTranslator(p);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return null;
  }

  private static Log log() {
    if (log == null) log = Mission.control().log("cpl", new Compile());
    return log;
  }

  private static Log log;
  private static boolean verbose;

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
    System.err.println(
        "| Usage: java com.inxar.syntacs.Compile [options] " + "<grammar-input-file>");
    System.err.println("|");
    System.err.println("| Options:");
    System.err.println("|  -D<key=val>...........Set translator property");
    System.err.println("|");
    System.err.println("| Requirements:");
    System.err.println("|  <grammar-input-file>...The name of the grammar file to compile");
    System.err.println("|");

    System.exit(-1);
  }
}
