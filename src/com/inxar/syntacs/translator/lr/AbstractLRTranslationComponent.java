/**
 * $Id: AbstractLRTranslationComponent.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.Properties;
import org.inxar.syntacs.analyzer.Input;
import org.inxar.syntacs.translator.Auditor;
import org.inxar.syntacs.translator.lr.LRTranslationComponent;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;

/**
 * Base implementation of <code>LRTranslationComponent</code>.
 */
public abstract class AbstractLRTranslationComponent implements LRTranslationComponent {
  public void initialize(Object obj) {}

  public void reset() {}

  public void setLRTranslatorGrammar(LRTranslatorGrammar grammar) {
    this.grammar = grammar;
  }

  public LRTranslatorGrammar getLRTranslatorGrammar() {
    return grammar;
  }

  public void setInput(Input in) {
    this.in = in;
  }

  public Input getInput() {
    return in;
  }

  public void setAuditor(Auditor auditor) {
    this.auditor = auditor;
  }

  public Auditor getAuditor() {
    return auditor;
  }

  public void setProperties(Properties p) {
    this.p = p;
  }

  public Properties getProperties() {
    return this.p;
  }

  protected Input in;
  protected LRTranslatorGrammar grammar;
  protected Auditor auditor;
  protected Properties p;
}
