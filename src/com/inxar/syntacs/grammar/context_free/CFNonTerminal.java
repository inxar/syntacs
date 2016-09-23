/**
 * $Id: CFNonTerminal.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.grammar.context_free;

import org.inxar.syntacs.grammar.context_free.NonTerminal;
import org.inxar.syntacs.grammar.context_free.Item;
import org.inxar.syntacs.grammar.context_free.Production;
import org.inxar.syntacs.grammar.context_free.GrammarSymbol;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntList;
import org.inxar.syntacs.util.IntArray;
import org.inxar.syntacs.util.IntStack;

import com.inxar.syntacs.util.ArrayIntStack;
import com.inxar.syntacs.util.ArrayIntList;
import com.inxar.syntacs.util.BitSetIntSet;

/**
 * Standard <code>NonTerminal</code> implementation.
 */
public class CFNonTerminal implements NonTerminal {
  /**
   * Constructs the <code>CFNonTerminal</code> on the given
   * <code>CFGrammar</code> and <code>String</code> name.
   */
  CFNonTerminal(CFGrammar grammar, String name) {
    this.grammar = grammar;
    this.name = name;
    this.alts = new ArrayIntList();
    this.items = new ArrayIntList();
  }

  void setID(int ID) {
    this.ID = ID;
  }

  void addItem(Item item) {
    items.add(item.getID());
  }

  void addAlternative(Production production) {
    //System.out.println("CFNonTerminal.addAlternative(): adding "+production);
    alts.add(production.getID());
  }

  public int getID() {
    return ID;
  }

  public String getName() {
    return name;
  }

  public boolean isTerminal() {
    return false;
  }

  public IntArray getProductionItems() {
    return items;
  }

  public IntArray getReductions() {
    return alts;
  }

  public IntSet getFirstSet() {
    if (first == null) first = first();

    return first;
  }

  /**
   * Calculates and returns the first set for the given
   * non-terminal.  Each member in the set corresponds the the id of
   * a terminal.
   */
  protected IntSet first() {
    // Maintain a vector to track what nonterminals we have
    // already processed and do no need to again.
    IntList seen = new ArrayIntList(1);

    // Maintain a stack of the non-terminals we need to process.
    IntStack stack = new ArrayIntStack(5);

    // push this on the stack -- we certainly need to process this
    // one
    stack.push(this.ID);

    // make the result set
    IntSet set = new BitSetIntSet();

    // cached out vars
    GrammarSymbol symbol;
    NonTerminal nonTerminal;
    IntArray symbols, productions;

    // process until stack is done
    while (!stack.isEmpty()) {
      // get the top nonTerminal
      nonTerminal = grammar.getNonTerminal(stack.pop());
      // and mark it
      seen.add(nonTerminal.getID());
      // now gets all the productions
      productions = nonTerminal.getReductions();
      // iterate the productions
      for (int i = 0; i < productions.length(); i++) {
        // get each productions grammar symbols
        symbols = grammar.getProduction(productions.at(i)).getGrammarSymbols();
        // and iterate these
        for (int j = 0; i < symbols.length(); j++) {
          // get the symbol...
          symbol = grammar.getGrammarSymbol(symbols.at(j));
          // test if this grammar symbol is a terminal
          if (symbol.isTerminal()) {
            // add it to the set
            set.put(symbol.getID());
          } else {
            // this is a nonterminal.  Push it on the
            // stack.
            if (!seen.contains(symbol.getID())) stack.push(symbol.getID());
          }
          // break out of the loop if this symbol is NOT
          // nullable
          if (!symbol.isNullable()) break;
        }
      }
    }

    // done populating the set.
    return set;
  }

  public boolean isNullable() {
    // check if we've calculated it yet...
    if (isNullable == null) {

      // cache out the ID of epsilon
      int epsilonID = grammar.getEpsilon().getID();

      // cached out vars
      Production production = null;
      IntArray symbols = null;

      // test each production
      for (int i = 0; i < alts.length(); i++) {
        // get each production's grammar symbol iterator
        symbols = (grammar.getProduction(i)).getGrammarSymbols();
        // see if the length is one and the first position is epsilon
        if (symbols.length() == 1 && symbols.at(0) == epsilonID) {
          // assign true
          isNullable = Boolean.TRUE;
          // and get out of this test
          break;
        }
      }
      // not nullable
      isNullable = Boolean.FALSE;
    }

    // return boolean result
    return isNullable.booleanValue();
  }

  public String toString() {
    return "(" + ID + "," + name + ")";
    //return name;
  }

  /**
   * Since we assume that nonTerminals have unique ID's, we base the
   * hashCode solely upon this (also equals()).
   */
  public int hashCode() {
    return this.ID;
  }

  public boolean equals(Object other) {
    if (other == this) return true;
    if (other == null || !(other instanceof NonTerminal)) return false;
    return this.ID == ((NonTerminal) other).getID();
  }

  // ******************************************************
  // INSTANCE FIELDS
  // ******************************************************
  private int ID;
  private String name;
  private Boolean isNullable;
  private IntSet first;
  private IntList alts;
  private IntList items;
  private CFGrammar grammar;
}
