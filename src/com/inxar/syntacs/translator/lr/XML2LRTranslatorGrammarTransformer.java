/**
 * $Id: XML2LRTranslatorGrammarTransformer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator.lr;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.inxar.syntacs.translator.Translator;
import org.inxar.syntacs.translator.TranslatorGrammar;
import org.inxar.syntacs.translator.Auditor;
//import org.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.AlgorithmException;
import org.inxar.syntacs.util.Log;

import com.inxar.syntacs.translator.StandardAuditor;
import com.inxar.syntacs.translator.regexp.Regexp;
import com.inxar.syntacs.translator.regexp.XML2RegexpTransformer;
import com.inxar.syntacs.translator.syntacs.SyntacsGrammar;
import com.inxar.syntacs.util.DOM;
import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.StringTools;

/**
 * Implementation of Grammar which reads an XML instance of
 * the DTD "grammar.dtd".
 */
public class XML2LRTranslatorGrammarTransformer {
  private static final boolean DEBUG = true;

  private static boolean verbose = Mission.control().isTrue("verbose");

  public XML2LRTranslatorGrammarTransformer() {}

  public TranslatorGrammar transform(String uri) throws AlgorithmException {
    if (verbose) log().debug().write("Parsing XML...").time();

    try {
      // Create the document element and transform that.
      return _transform(uri, DOM.getRoot(uri, true));
    } catch (Exception ex) {
      throw new AlgorithmException(ex);
    } finally {
      if (verbose) log().debug().touch();
    }
  }

  public synchronized TranslatorGrammar transform(Element doc) throws AlgorithmException {
    return _transform(null, doc);
  }

  private synchronized TranslatorGrammar _transform(String uri, Element doc)
      throws AlgorithmException {
    this.doc = doc;
    this.g = new StandardLRTranslatorGrammar();
    Auditor auditor = new StandardAuditor();
    auditor.setSource(uri);
    g.setAuditor(auditor);

    parseNameAndVersion();
    parseProperties();
    parseContexts();
    parseTerminals();
    parseNonTerminals();
    parseContextMembers();
    parseMatchDefinitions();
    parseReductions();

    TranslatorGrammar grammar = g;

    return grammar;
  }

  private Element getDocument() {
    return doc;
  }

  private void parseNameAndVersion() {
    String name = DOM.getString("name", doc);
    String version = DOM.getString("version", doc);
    g.setName(name);
    g.setVersion(version);
  }

  private void parseProperties() {
    Iterator i = DOM.getElements("property", doc);
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      String name = DOM.getString("name", e);
      String value = DOM.getString("value", e);
      g.setProperty(name, value);
    }
  }

  private void parseContexts() {
    Iterator i = DOM.getElements("context", doc);
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      String name = DOM.getString("name", e);
      g.addContext(name);
      if (DOM.getBoolean("start", e)) g.setStartContext(name);
    }
  }

  private void parseTerminals() {
    Iterator i = DOM.getElements("terminal", doc);
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      g.addTerminal(DOM.getString("name", e));
    }
  }

  private void parseNonTerminals() {
    Iterator i = DOM.getElements("nonterminal", doc);
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      String name = DOM.getString("name", e);
      g.addNonTerminal(name);
      if (DOM.getBoolean("start", e)) g.setGoalNonTerminal(name);
    }
  }

  private void parseContextMembers() {
    Iterator i = DOM.getElements("context", doc);
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      String contextName = DOM.getString("name", e);
      Iterator j = DOM.getElements("member", e);
      while (j.hasNext()) {
        Node o = (Node) j.next();
        if (o.getNodeType() != Node.ELEMENT_NODE) continue;
        Element f = (Element) o;
        String terminalName = DOM.getString("terminal", f);
        String actionName = DOM.getString("action", f);
        if (!StringTools.isDefined(actionName)) g.setContextPeek(contextName, terminalName);
        else if (actionName.equals("unshift")) g.setContextPop(contextName, terminalName);
        else g.setContextPush(contextName, terminalName, actionName);
      }
    }
  }

  private void parseMatchDefinitions() {
    Iterator i = DOM.getElements("match", doc);
    outerloop:
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      String terminalName = DOM.getString("terminal", e);
      NodeList list = e.getChildNodes();
      for (int j = 0; j < list.getLength(); j++) {
        Node o = list.item(j);
        if (o.getNodeType() == Node.ELEMENT_NODE) {
          Regexp re = xml2re.transform((Element) o);
          g.setTerminalRegexp(terminalName, re);
          continue outerloop;
        }
      }
    }
  }

  private void parseReductions() {
    Iterator i = DOM.getElements("reduce", doc);
    while (i.hasNext()) {
      Node n = (Node) i.next();
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      Element e = (Element) n;
      String nonterminalName = DOM.getString("nonterminal", e);
      List symbols = new ArrayList(3);
      Iterator j = DOM.getElements("symbol", e);
      while (j.hasNext()) {
        Node o = (Node) j.next();
        if (o.getNodeType() != Node.ELEMENT_NODE) continue;
        Element f = (Element) o;
        symbols.add(DOM.getString("name", f));
      }
      g.addProduction(nonterminalName, symbols);
    }
  }

  private Log log() {
    if (log == null) log = Mission.control().log("x2g", this); // Xml To lrtranslatorGrammar
    return log;
  }

  private Element doc;
  private StandardLRTranslatorGrammar g;
  private static XML2RegexpTransformer xml2re = new XML2RegexpTransformer();
  private Log log;

  public static void main(String[] argv) throws Exception {
    String uri = argv[0];

    try {

      // -------------------------------------
      // PART 1: Make a translator grammar
      // -------------------------------------
      TranslatorGrammar g = null;

      switch (2) {
        case 1:
          g = transformXML(uri);
          break;
        case 2:
          g = new SyntacsGrammar();
          break;
        default:
          throw new InternalError();
      }

      // -------------------------------------
      // PART 2: Get a new Translator
      // -------------------------------------
      Translator t = g.newTranslator();

      // -------------------------------------
      // PART 2: Do something with it
      // -------------------------------------
      use(2, t);

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static void use(int value, Translator t) throws Exception {
    switch (value) {
      case 1:
        {
          TranslatorGrammar g = transformSimulacs(t);
          report(g);
          break;
        }
      case 2:
        {
          TranslatorGrammar g = transformSimulacs(t);
          //report(g);
          Translator t2 = g.newTranslator();
          use(2, t2);
          break;
        }
    }
  }

  private static void report(Object o) {
    System.out.println(o == null ? "no result" : o.toString());
  }

  private static TranslatorGrammar transformSimulacs(Translator translator) throws Exception {
    Reader in = new BufferedReader(new FileReader(new File("./etc/simulacs-grammar.txt")));

    report("Translating now...");
    TranslatorGrammar g = (TranslatorGrammar) translator.translate(in);

    in.close();

    return g;
  }

  private static TranslatorGrammar transformXML(String uri) throws Exception {
    XML2LRTranslatorGrammarTransformer tr = new XML2LRTranslatorGrammarTransformer();

    return tr.transform(uri);
  }

  private static String getAccept(String uri) throws Exception {
    StringReader in = null;
    Element doc = DOM.getRoot(uri, true);
    Iterator i = DOM.getElements("accept", doc);

    while (i.hasNext()) {

      Element e = (Element) i.next();
      return DOM.getText(e);
    }

    throw new InternalError();
  }
}
