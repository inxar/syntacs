/**
 * $Id: Parser.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.syntactic;

import org.inxar.syntacs.analyzer.Symbol;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.lr.LRTranslationComponent;

/**
 * The <code>Parser</code> is responsible for managing the
 * construction of the parse tree; to do so it interacts with the
 * <code>LexerInterpreter</code> (presumably) and the
 * <code>ParserInterpreter</code>.  The <code>LexerInterpreter</code>
 * is the input terminal (token) source, and the
 * <code>ParserInterpreter</code> is the nonterminal source.  The
 * <code>Parser</code> delegates to the <code>ParserInterpreter</code>
 * when a reduction need be made.  
 */
public interface Parser extends LRTranslationComponent
{
    /**
     * Notifies the <code>Parser</code> that a new <code>Symbol</code>
     * (token) has been discovered.  
     */
    void notify(Symbol token) throws TranslationException;

    /**
     * Sets the <code>ParserInterpreter</code>.
     */
    void setParserInterpreter(ParserInterpreter interpreter);

    /**
     * Gets the <code>ParserInterpreter</code>.
     */
    ParserInterpreter getParserInterpreter();

}
