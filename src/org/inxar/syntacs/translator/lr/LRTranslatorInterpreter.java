/**
 * $Id: LRTranslatorInterpreter.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.translator.lr;

import org.inxar.syntacs.analyzer.lexical.LexerInterpreter;
import org.inxar.syntacs.analyzer.syntactic.ParserInterpreter;

/**
 * The <code>LRTranslatorInterpreter</code> interface is a union of
 * the <code>LexerInterpreter</code> and
 * <code>ParserInterpreter</code> interfaces.  
 */
public interface LRTranslatorInterpreter extends LexerInterpreter, ParserInterpreter
{
    /**
     * When translation is complete and there were no unrecoverable
     * errors, the <code>LRTranslatorInterpreter</code> should be able
     * to produce some <code>Object</code> which was built from the
     * parse tree. This <code>Object</code> will be returned by the
     * <code>Translator</code> if appropriate.  
     */
    Object getResult();
}



