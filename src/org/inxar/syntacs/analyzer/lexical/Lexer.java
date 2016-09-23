/**
 * $Id: Lexer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.lexical;

import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.lr.LRTranslationComponent;
import org.inxar.syntacs.util.IntStack;

/**
 * The <code>Lexer</code> is responsible for reading an
 * <code>Input</code> stream and breaking it up into a stream of
 * tokens.  The standard interface for a <code>Lexer</code> would have
 * a single method like "<code>Token lex()</code>" that the parser
 * would call to fetch the next token in the input.  This interface
 * does not have any such method though it does have methods
 * <code>start()</code> and <code>stop()</code>.  Why?
 *
 * <P>
 *
 * The reason is that this interface assumes the <code>Lexer</code> is
 * responsible only for discovering token sequences in the input, NOT
 * discovering tokens AND wrapping those tokens in <code>Symbol</code>
 * objects. The difference is subtle, but it allows a separate object
 * to handle these "lexical events".  This is good because it gives
 * the user complete control of <code>Symbol</code> instantiation.
 *
 * <P>
 *
 * Thus, token sequences are communicated to the
 * <code>LexerInterpreter</code> via the <code>match(int token_type,
 * int offset, int length)</code> method.  The <code>start()</code>
 * method begins (or restarts) the lexing process and can be stopped
 * or stopped via the <code>stop()</code>.  The <code>Lexer</code>
 * will check to see if it is stopped before each token search.  Note
 * this does NOT imply that the <code>Lexer</code> represents a
 * <code>Thread</code>.
 *
 * <P>
 *
 * Another way to describe this setup would be
 * "lexer-directed-translation" versus "parser-driven-translation".
 *
 * <P>
 *
 * Said again, the <code>Lexer</code> component is responsible ONLY
 * for identification of the locations of tokens in the
 * <code>Input</code>, not the actual instantiation of
 * <code>Symbol</code> (token) objects.  Rather, this functionality is
 * delegated to the <code>LexerInterpreter</code>.
 */
public interface Lexer extends LRTranslationComponent {
  /**
   * Resets the internal state of the <code>Lexer</code> and
   * triggers the search for the next lexeme from the input.
   * Notification will take place through the
   * <code>LexerInterpreter</code>. This method will run until the
   * input buffer has been exhausted or <code>stop()</code> is
   * called.
   */
  void start() throws TranslationException;

  /**
   * Pauses the lexing process.  Lexing can be restarted via calling
   * <code>resume()</code>.  The lexer should check before the start
   * of each token search to see whether stop has been called.
   * Therefore, the granularity of stop is limited to the moment
   * between token searches.
   */
  void stop();

  /**
   * Continues the lexing process from the current
   * <code>Input</code> position.
   */
  void resume() throws TranslationException;

  /**
   * Returns the id of the current context.
   **/
  int getCurrentContext();

  /**
   * Accesses the context stack history.
   **/
  IntStack getContextStack();

  /**
   * Sets the <code>Listener</code> to be notified of token
   * <code>Symbol</code> events.
   */
  void setLexerInterpreter(LexerInterpreter interpreter);

  /**
   * Gets the <code>Listener</code> to be notified of token
   * <code>Symbol</code> events.
   */
  LexerInterpreter getLexerInterpreter();
}
