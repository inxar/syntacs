/**
 * $Id: TranslatorGrammar.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.translator;

import java.io.Serializable;
import java.util.*;

/**
 * The <code>TranslatorGrammar</code> interface describes the symbolic
 * components of a grammar as well as acting as a factory for
 * <code>Translator</code> instances.  It is a central interface as
 * the TranslatorGrammar is responsible for instantiation of new
 * Translator instances.
 */
public interface TranslatorGrammar
    extends Serializable
{
    /**
     * Returns the name of the <code>Translator</code>.
     */
    String getName();

    /**
     * Returns the version number given to this grammar.
     */
    String getVersion();

    /**
     * Returns a new <code>Translator</code> for this
     * <code>TranslatorGrammar</code> having some default set of
     * properties.  
     */
    Translator newTranslator();

    /**
     * Returns a new <code>Translator</code> for this
     * <code>TranslatorGrammar</code> using the given
     * <code>Properties</code>.  The names and values of the elements
     * in the <code>Properties</code> may be used to modify or
     * communicate with the internals of the <code>Translator</code>
     * instance (such as turning on debugging).  The specifics
     * reagarding the names and values of the properties are specific
     * to the translator itself and need be documented elsewhere.
     */
     Translator newTranslator(Properties properties); }


