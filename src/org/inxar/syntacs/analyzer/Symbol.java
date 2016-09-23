/**
 * $Id: Symbol.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer;

/**
 * <code>Symbol</code> is an abstraction of either a terminal or a
 * nonterminal.  Terminal <code>Symbol</code> instances are typically constructed as
 * within a <code>LexerInterpreter</code> and nonterminal <code>Symbol</code> instances within
 * the <code>ParserInterpreter</code>.  These form the nodes of an [abstract] syntax
 * tree.
 *
 * <P>
 *
 * The value returned from the <code>getSymbolType()</code>
 * corresponds to a grammar constant, typically one allocated by a
 * <code>TranslatorGrammar</code> or a constant in a
 * <code>TranslatorGrammar</code> class that has been generated
 * (<code>T_XXX</code> or <code>N_XXX</code>).
 */
public interface Symbol {
  /**
   * Returns the symbol type.
   */
  int getSymbolType();

  /**
   * Sets the symbol type.  This method is necessary because the
   * design of the shift-reduce parser interacts with the symbol
   * such that it automatically sets the type of the symbol returned
   * to the parser after a reduction to the correct nonterminal
   * (such that the user doesn't have to worry about it).
   */
  void setSymbolType(int type);
}
