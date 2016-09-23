/**
 * $Id: LRTranslationComponent.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.translator.lr;

import java.util.Properties;
import org.inxar.syntacs.translator.Auditor;
import org.inxar.syntacs.analyzer.Input;

/**
 * The <code>LRTranslationComponent</code> interface describes common
 * behavior required of all components in the lr translation process
 * -- the ability to be reset to an initial state, the need to
 * reference the <code>LRTranslatorGrammar</code> during the
 * translation process, the need of referencing the <code>Input</code>
 * during the translation process, and access to general properties.
 */
public interface LRTranslationComponent {
  /**
   * Initializes the component with some <code>Object</code>.  If
   * the runtime type of the given Object is inappropriate, the
   * implementation will throw a <code>RuntimeException</code>.
   */
  void initialize(Object obj);

  /**
   * Resets the internal state of the
   * <code>TranslationComponent</code> in preparation for another
   * translation.
   */
  void reset();

  /**
   * Sets the current <code>LRTranslatorGrammar</code> to the given
   * instance.
   */
  void setLRTranslatorGrammar(LRTranslatorGrammar grammar);

  /**
   * Returns the current <code>LRTranslatorGrammar</code> to the given
   * instance.
   */
  LRTranslatorGrammar getLRTranslatorGrammar();

  /**
   * Sets the current <code>Input</code> to the given instance.
   */
  void setInput(Input in);

  /**
   * Returns the current <code>Input</code> instance.
   */
  Input getInput();

  /**
   * Sets the current <code>Auditor</code> to the given instance.
   */
  void setAuditor(Auditor auditor);

  /**
   * Returns the current <code>Auditor</code> instance.
   */
  Auditor getAuditor();

  /**
   * Sets the current <code>Properties</code> to the given instance.
   */
  void setProperties(Properties p);

  /**
   * Returns the current <code>Properties</code> instance.
   */
  Properties getProperties();
}
