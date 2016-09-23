/**
 * $Id: LRTranslatorGrammar.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

import org.inxar.syntacs.util.IntArray;
import org.inxar.syntacs.translator.TranslatorGrammar;

/**
 * The <code>LRTranslatorGrammar</code> interface describes the
 * symbolic components of a grammar, relevant to LR parsing
 * algorithms.  The LRTranslatorGrammar contains all the structural
 * information of the original grammar.
 */
public interface LRTranslatorGrammar extends TranslatorGrammar {
  /**
   * Constant returned by <code>getContextAction(int, int)</code> to
   * signal a context stack peek, meaning "change to the context
   * given at the top of stack".  This effectively means "no change"
   * and is the most common context stack action.
   */
  int ACTION_PEEK = 1;

  /**
   * Constant returned by <code>getContextAction(int, int)</code> to
   * signal a context stack pop, meaning "change back to the
   * previous stack context".
   */
  int ACTION_POP = 2;

  /**
   * Constant returned by <code>getContextAction(int, int)</code> to
   * signal a context stack push, meaning "shift to the context
   * having the ID given by the return value of
   * <code>getContextRegister(int, int)</code>".
   */
  int ACTION_PUSH = 3;

  /**
   * Constant returned by <code>getContextAction(int, int)</code> to
   * signal a context stack peel, meaning "execute ACTION_POP until
   * the top of the stack is equal to the return value of
   * <code>getContextRegister(int, int)</code>.  If the stack
   * becomes empty, PUSH the register value."  This has the effect
   * of removing plates from the buffet table until the top plate is
   * blue, or if all plates are gone, put a single blue plate on the
   * table.  This action is useful during error recovery
   * synchronization routines where the stack should be put in a
   * known and minimal state.
   */
  int ACTION_PEEL = 4;

  /**
   * Constant returned by <code>getContextAction(int, int)</code> to
   * signal a context stack pounce, meaning "execute an ACTION_POP,
   * then an ACTION_PUSH".  The name "pounce" is derived from two
   * points: (1) stack instructions starting with "p" have a certain
   * ring to them, (2) the action of pouncing resembles something
   * that goes down (a crouching tiger, perhaps) and then goes back
   * up.  The effect is ACTION_POUNCE is to change the context
   * without having to POP it later.  Exclusive use of ACTION_POUNCE
   * makes the context stack appear like a single scalar value as
   * demonstrated by other lexers having start states (such as
   * flex).
   */
  int ACTION_POUNCE = 5;

  /**
   * Constant used to indicate general undefined conditions.
   */
  int UNDEFINED = Integer.MIN_VALUE;

  /**
   * Constant used to indicate the non-definition of a particular
   * context.
   */
  int UNDEFINED_CONTEXT = Integer.MIN_VALUE + 1;

  /**
   * Constant used to indicate the non-definition of a particular
   * terminal.
   */
  int UNDEFINED_TERMINAL = Integer.MIN_VALUE + 2;

  /**
   * Constant used to indicate the non-definition of a particular
   * nonterminal.
   */
  int UNDEFINED_NONTERMINAL = Integer.MIN_VALUE + 3;

  /**
   * Constant used to indicate the non-definition of a particular
   * production.
   */
  int UNDEFINED_PRODUCTION = Integer.MIN_VALUE + 4;

  /**
   * Constant used to indicate the non-definition of a particular
   * grammar symbol (where a grammar symbol defined as a terminal or
   * a nonterminal).
   */
  int UNDEFINED_SYMBOL = Integer.MIN_VALUE + 5;

  // ================================================================
  // SET RESOLUTION METHODS
  // ================================================================

  /**
   * Returns the list of context ID numbers as an
   * <code>IntArray</code>.
   */
  IntArray getContexts();

  /**
   * Returns the list of terminal ID numbers as an
   * <code>IntArray</code>.
   */
  IntArray getTerminals();

  /**
   * Returns the list of nonterminal ID numbers as an
   * <code>IntArray</code>.
   */
  IntArray getNonTerminals();

  /**
   * Returns the list of production ID numbers as an
   * <code>IntArray</code>.
   */
  IntArray getProductions();

  // ================================================================
  // NAME RESOLUTION METHODS
  // ================================================================

  /**
   * Returns the name of the context having the given ID or
   * <code>null</code> if no such ID is known.
   */
  String getContext(int ID);

  /**
   * Returns the name of the terminal having the given ID or
   * <code>null</code> if no such ID is known.
   */
  String getTerminal(int ID);

  /**
   * Returns the name of the nonterminal having the given ID or
   * <code>null</code> if no such ID is known.
   */
  String getNonTerminal(int ID);

  /**
   * Returns the name of the production having the given ID or
   * <code>null</code> if no such ID is known.  The
   * <code>String</code> has the format "<code>nonterminal: sym1
   * sym2 sym3</code>" such that the nonterminal name is the first
   * part of the string, followed by a colon, followed by a space
   * delimited list of symbols.
   */
  String getProduction(int ID);

  // ================================================================
  // CONTEXT DEFINITION METHODS
  // ================================================================

  /**
   * Returns a list of terminals defined within the context having
   * the given ID or <code>null</code> if no such ID is known.
   */
  IntArray getContextTerminals(int contextID);

  /**
   * Returns the action defined for the given grammar symbol in the
   * given context as one of the <code>ACTION_XXX</code> constants
   * in this interface.  If the contextID or symbolID is not known,
   * <code>UNDEFINED_CONTEXT</code> or <code>UNDEFINED_SYMBOL</code>
   * will be returned.  Note: If UNDEFINED_SYMBOL is returned the ID
   * may still be a valid terminal or nonterminal, but it not a
   * member of the given context.
   */
  int getContextAction(int contextID, int symbolID);

  /**
   * Returns an integer stored at the value of the given
   * <code>contextID</code> and <code>symbolID</code>.  This is used
   * in conjunction with <code>getContextAction(int, int)</code>
   * such that the second member in the tuple (action, register) is
   * returned. If the contextID or symbolID is not known,
   * <code>UNDEFINED_CONTEXT</code> or <code>UNDEFINED_SYMBOL</code>
   * will be returned.
   */
  int getContextRegister(int contextID, int symbolID);

  /**
   * Returns the ID of the initial context.
   */
  int getStartContext();

  // ================================================================
  // TERMINAL DEFINITION METHODS
  // ================================================================

  /**
   * Returns a list of contexts in which this terminal is defined.
   */
  IntArray getTerminalContexts(int terminalID);

  /**
   * Indicates whether the given symbol ID is a terminal or
   * nonterminal.
   *
   * @return <code>0</code> if the ID is a terminal.
   *
   * @return <code>1</code> if the ID is a nonterminal.
   *
   * @return UNDEFINED_SYMBOL if the ID is not known.
   */
  //int isTerminal(int symbolID);

  /**
   * Returns the regular expression <code>String</code> for the
   * terminal having the given ID or <code>null</code> if no such ID
   * is known.  If the terminal ID is valid but no definition has
   * been provided for that terminal, the empty string
   * <code>""</code> will be returned.
   */
  Object getTerminalRegexp(int terminalID);

  // ================================================================
  // PRODUCTION DEFINITION METHODS
  // ================================================================

  /**
   * Returns the ID of the nonterminal obtained by reduction of the
   * given <code>productionID</code> or
   * <code>UNDEFINED_PRODUCTION</code> if no such production is known.
   */
  int getProductionNonTerminal(int productionID);

  /**
   * Returns the list of grammar symbols of the right-hand-side of
   * the production having the given ID as an <code>IntArray</code>
   * or <code>null</code> if no such ID is known.
   */
  IntArray getProductionSymbols(int productionID);

  /**
   * Returns the length of the list of grammar symbols of the
   * right-hand-side of the production having the given ID or
   * <code>UNDEFINED_PRODUCTION</code> if no such ID is known.
   */
  int getProductionLength(int productionID);

  /**
   * Returns the ID of the goal symbol (a nonterminal) or
   * <code>UNDEFINED_NONTERMINAL</code> is no such goal symbol has
   * been set.
   */
  int getGoalNonTerminal();
}
