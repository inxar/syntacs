/**
 * $Id: BurnerController.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import java.io.IOException;
import java.util.Date;
import java.util.jar.JarFile;

import org.inxar.jenesis.*;

import org.inxar.syntacs.automaton.pushdown.DPA;
import org.inxar.syntacs.util.Burner;
import org.inxar.syntacs.util.Log;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;

import com.inxar.syntacs.automaton.finite.MesoArrayDFA;
import com.inxar.syntacs.automaton.finite.MesoArrayDFABurner;
import com.inxar.syntacs.automaton.pushdown.MesoArrayDPABurner;
import com.inxar.syntacs.util.Jar;
import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.StringTools;

/**
 * The <code>BurnerController</code> is centrally responsible for
 * overseeing and coordinating various <code>Burner</code> instances.
 * It is the thing that does the work of creating each
 * <code>CompilationUnit</code> and top-level
 * <code>PackageClass</code> and handing it off it to a
 * <code>Burner</code> to get burned.
 */
public class BurnerController {
  private static final boolean DEBUG = true;

  private static boolean verbose = Mission.control().isTrue("verbose");

  public BurnerController() {
    this.vm = VirtualMachine.getVirtualMachine();
  }

  public void burn() {
    if (Mission.control().isFalse("compile")) return;

    init();
    burnLexical();
    burnSyntactic();
    burnGrammar();
    burnSrcjar();
  }

  private void init() {
    this.srcjar = Mission.control().getString("compile-srcjar");
    this.srcpath = Mission.control().getString("compile-sourcepath", ".");
    this.namespace = Mission.control().getString("compile-namespace");
    this.author = Mission.control().getString("author");
    this.email = Mission.control().getString("author-email");
    this.copyright = Mission.control().getString("copyright");

    System.out.println("srcpath: " + srcpath);

    if (author != null) {
      if (email != null) author += " - " + email;
    } else {
      if (email != null) author = email;
    }

    if (copyright == null) {
      copyright = "Copyright (C) " + (new Date().getYear() + 1900);
      if (author != null) copyright += " " + author;
      else copyright += " unattributed";
    }
  }

  private void burnLexical() {
    if (Mission.control().isFalse("compile-lexical")) return;

    String[] names = (String[]) Mission.control().get("_dfa-names");
    MesoArrayDFA[] dfas = (MesoArrayDFA[]) Mission.control().get("_meso-array-dfas");
    Burner burner = new MesoArrayDFABurner();

    ClassDeclaration cls;
    for (int i = 0; i < dfas.length; i++) {
      cls = newClass(names[i]);

      if (verbose)
        log()
            .debug()
            .write("Burning ")
            .write(StringTools.getPath(srcpath, namespace))
            .write(cls.getName())
            .write(".java")
            .time();

      burner.burn(dfas[i], cls);
      emit(cls);

      if (verbose) log().debug().touch();
    }
  }

  private void burnSyntactic() {
    if (Mission.control().isFalse("compile-syntactic")) return;

    String name = Mission.control().getString("_dpa-name");
    DPA dpa = (DPA) Mission.control().get("_meso-array-dpa");
    Burner burner = new MesoArrayDPABurner();
    ClassDeclaration cls = newClass(name);

    if (verbose)
      log()
          .debug()
          .write("Burning ")
          .write(StringTools.getPath(srcpath, namespace))
          .write(cls.getName())
          .write(".java")
          .time();

    burner.burn(dpa, cls);
    emit(cls);

    if (verbose) log().debug().touch();
  }

  private void burnGrammar() {
    if (Mission.control().isFalse("compile-grammar")) return;

    String name = Mission.control().getString("_grammar-name");
    LRTranslatorGrammar grammar =
        (LRTranslatorGrammar) Mission.control().get("_lr-translator-grammar");
    Burner burner = new LRTranslatorGrammarBurner();
    ClassDeclaration cls = newClass(name);

    if (verbose)
      log()
          .debug()
          .write("Burning ")
          .write(StringTools.getPath(srcpath, namespace))
          .write(cls.getName())
          .write(".java")
          .time();

    burner.burn(grammar, cls);
    emit(cls);

    if (verbose) log().debug().touch();
  }

  private void burnSrcjar() {
    if (srcjar == null) {
      return;
    }
    try {
      File srcdir = new File(srcpath);
      File jarfile = new File(srcjar);
      srcdir.mkdirs();
      if (!srcdir.isDirectory()) {
        throw new IllegalArgumentException("srcdir is not a directory: " + srcdir.getAbsolutePath());
      }
      Jar jar = new Jar(jarfile, true /* recursive */);
      for (File d : srcdir.listFiles()) {
        jar.add(srcpath, d);
      }
      jar.close();

      if (verbose)
        log()
          .debug()
          .write("Wrote jar ")
          .write(jarfile)
          .time();
    } catch (IOException ioex) {
      throw new RuntimeException(ioex);
    }
  }

  private void emit(ClassDeclaration cls) {
    try {

      cls.getUnit().encode();

    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
    }
  }

  private ClassDeclaration newClass(String className) {
    CompilationUnit unit = vm.newCompilationUnit(srcpath);
    unit.setNamespace(namespace);
    unit.setComment(Comment.D, "");
    DocumentationComment c = (DocumentationComment) unit.getComment();
    if (copyright != null) c.setText(copyright);
    else
      c.setText(
          "Automatically generated by <a href='http://www.inxar"
              + ".org/syntacs'>Syntacs Translation Toolkit</a>");
    if (author != null) c.setAuthor(author);

    PackageClass cls = unit.newClass(className);
    cls.setComment(
        Comment.D,
        "Automatically generated by <a href='http://www.inxar"
            + ".org/syntacs'>Syntacs Translation Toolkit</a> on "
            + new Date());
    return cls;
  }

  private Log log() {
    if (log == null) log = Mission.control().log("bct", this); // Burner ConTroller
    return log;
  }

  private VirtualMachine vm;
  private String srcjar;
  private String srcpath;
  private String namespace;
  private String author;
  private String email;
  private String copyright;
  private Log log;
}
