/**
 * $Id: LexerInterpreter.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.lexical;

import org.inxar.syntacs.analyzer.syntactic.Parser;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.lr.LRTranslationComponent;

/**
 * The <code>LexerInterpreter</code> is responsible for handling "lexer
 * events".  A lexer "event" is defined when then <code>Lexer</code>
 * has found a new character sequence that matches some
 * <code>Token</code> type.  The <code>Lexer</code> passes the
 * <code>Token</code> type and character sequence offset and length to
 * the <code>LexerInterpreter</code>.  
 */
public interface LexerInterpreter extends LRTranslationComponent
{
    /**
     * The <code>match</code> method is used by the <code>Lexer</code>
     * to inform the interpreter that a new token has been found at the
     * given offset with the given length.  
     */
    void match(int type, int offset, int length) throws TranslationException;

    /**
     * Notify an error starting at the given offset having the given
     * length.  The return value is a code which instructs the lexer
     * how to recover.  Since there is currently no lexical error
     * recovery implemented, the return value is meaningless.
     */
    int error(int offset, int length) throws TranslationException;

    /**
     * Notify that the end of the <code>Input</code> has been reached.  
     */
    void stop() throws TranslationException;

    /**
     * Sets the <code>Parser</code> object to which this
     * <code>LexerInterpreter</code> may relay Symbol events to.  
     */
    void setParser(Parser parser);

    /**
     * Gets the Parser object which this <code>LexerInterpreter</code>
     * may relay <code>Symbol</code> events to.  
     */
    Parser getParser();

    /**
     * Sets the <code>Lexer</code> object to which this
     * <code>LexerInterpreter</code> is listening.  
     */
    void setLexer(Lexer lexer);

    /**
     * Gets the Lexer object which this <code>LexerInterpreter</code>
     *  is listening.
     */
    Lexer getLexer();
}



