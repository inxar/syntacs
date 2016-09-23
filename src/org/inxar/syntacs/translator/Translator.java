/**
 * $Id: Translator.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.translator;

import java.io.Serializable;
import java.util.Properties;

/**
 * A <code>Translator</code> is an object capable if transforming a
 * stream of characters into an <code>Object</code>.  A
 * <code>Translator</code> encapsulates both lexing and parsing tasks.
 */
public interface Translator extends Serializable {
  /**
   * Returns the <code>TranslatorGrammar</code> for this
   * <code>Translator</code>.
   */
  TranslatorGrammar getGrammar();

  /**
   * Translates the given <code>Object</code> to some other
   * <code>Object</code> representation.  If errors are encountered
   * during the translation, a <code>TranslationException</code>
   * will be thrown.  For <code>LRTranslator</code> instances, the
   * <code>Object</code> argument must be related to character
   * streams, such as a <code>Reader</code>, <code>URL</code>,
   * <code>char[]</code>, <code>File</code>, or something like that.
   * If the Translator cannot handle the <code>src</code> argument
   * type, it will let you know.
   */
  Object translate(Object src) throws TranslationException;

  /**
   * Returns the <code>Properties</code> defined for this
   * <code>Translator</code>.  Property names, types, and semantics
   * need be documented by actual translator instances.
   */
  Properties getProperties();
}
