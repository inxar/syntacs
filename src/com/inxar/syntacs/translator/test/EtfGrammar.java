/**
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * @author Paul Cody Johnston - pcj@inxar.org
 */
package com.inxar.syntacs.translator.test;

import org.inxar.syntacs.translator.Translator;
import org.inxar.syntacs.translator.lr.LRTranslator;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;
import org.inxar.syntacs.util.IntArray;
import com.inxar.syntacs.util.ArrayIntArray;
import org.inxar.syntacs.automaton.finite.DFA;
import org.inxar.syntacs.analyzer.Input;
import org.inxar.syntacs.analyzer.lexical.Lexer;
import org.inxar.syntacs.analyzer.syntactic.Parser;
import org.inxar.syntacs.translator.lr.LRTranslatorInterpreter;
import java.util.Properties;

/**
 * Automatically generated by <a href='http://www.inxar.org/syntacs'>Syntacs
 *  Translation Toolkit</a> on Fri Jul 06 12:05:01 PDT 2001<P><PRE># GRAMMAR
 *  DECLARATION
 * this is etf version 0.0.1;
 *
 * # PROPERTY DEFINITIONS
 * property viz-namespace = "etf";
 * property compile-dpa-debug = "true";
 * property viz-dpa-size = "12,12";
 * property viz-syntactic = "true";
 * property author-email = "pcj@inxar.org";
 * property author = "Paul Cody Johnston";
 * property compile-pickle = "false";
 * property viz-sourcepath = "./grammar";
 * property viz-lexical = "true";
 * property compile-namespace = "com.inxar.syntacs.translator.test";
 * property compile-dpa-constructor-method = "LALR1";
 * property compile-sourcepath = "./src";
 *
 * # TERMINAL DECLARATIONS
 * terminal PL;
 * terminal ST;
 * terminal OP;
 * terminal CP;
 * terminal ID;
 *
 * # TERMINAL DEFINITIONS
 * PL matches "\+";
 * ST matches "\*";
 * OP matches "\(";
 * CP matches "\)";
 * ID matches " [_a-zA-Z] [_a-zA-Z0-9]* ";
 *
 * # NONTERMINAL DECLARATIONS
 * nonterminal Goal;
 * nonterminal E;
 * nonterminal T;
 * nonterminal F;
 *
 * # NONTERMINAL DEFINITIONS
 * reduce Goal when E;
 * reduce E when E PL T;
 * reduce E when T;
 * reduce T when T ST F;
 * reduce T when F;
 * reduce F when OP E CP;
 * reduce F when ID;
 *
 * accept when Goal;
 *
 * # CONTEXT DECLARATIONS
 * context default;
 *
 * # CONTEXT DEFINITIONS
 * default includes PL, ST, OP, CP, ID;
 *
 * start in context default;
 *
 * </PRE>
 */
public class EtfGrammar implements LRTranslatorGrammar {

  /**
   * Constant ID for default
   */
  public static final int C_default = 0;

  /**
   * Terminal ID for PL
   */
  public static final int T_PL = 1;

  /**
   * Terminal ID for ST
   */
  public static final int T_ST = 2;

  /**
   * Terminal ID for OP
   */
  public static final int T_OP = 3;

  /**
   * Terminal ID for CP
   */
  public static final int T_CP = 4;

  /**
   * Terminal ID for ID
   */
  public static final int T_ID = 5;

  /**
   * NonTerminal ID for Goal
   */
  public static final int N_Goal = 6;

  /**
   * NonTerminal ID for E
   */
  public static final int N_E = 7;

  /**
   * NonTerminal ID for T
   */
  public static final int N_T = 8;

  /**
   * NonTerminal ID for F
   */
  public static final int N_F = 9;

  /**
   * Production ID for Goal__E
   */
  public static final int P_Goal__E = 0;

  /**
   * Production ID for E__E_PL_T
   */
  public static final int P_E__E_PL_T = 1;

  /**
   * Production ID for E__T
   */
  public static final int P_E__T = 2;

  /**
   * Production ID for T__T_ST_F
   */
  public static final int P_T__T_ST_F = 3;

  /**
   * Production ID for T__F
   */
  public static final int P_T__F = 4;

  /**
   * Production ID for F__OP_E_CP
   */
  public static final int P_F__OP_E_CP = 5;

  /**
   * Production ID for F__ID
   */
  public static final int P_F__ID = 6;

  public String getContext(int ID) {
    switch (ID) {
      case 0:
        {
          return "default";
        }
      default:
        {
          return null;
        }
    }
  }

  public int getContextAction(int cID, int tID) {
    switch (cID) {
      case 0:
        {
          return getDefaultContextAction(tID);
        }
      default:
        {
          return -2147483647;
        }
    }
  }

  public int getContextRegister(int cID, int tID) {
    switch (cID) {
      case 0:
        {
          return getDefaultContextRegister(tID);
        }
      default:
        {
          return -2147483647;
        }
    }
  }

  public IntArray getContextTerminals(int ID) {
    switch (ID) {
      case 0:
        {
          return new ArrayIntArray(contextTerminals[0]);
        }
      default:
        {
          return null;
        }
    }
  }

  public IntArray getContexts() {
    return contexts;
  }

  public int getGoalNonTerminal() {
    return 6;
  }

  public String getName() {
    return "etf";
  }

  public String getNonTerminal(int ID) {
    switch (ID) {
      case 6:
        {
          return "Goal";
        }
      case 7:
        {
          return "E";
        }
      case 8:
        {
          return "T";
        }
      case 9:
        {
          return "F";
        }
      default:
        {
          return null;
        }
    }
  }

  public IntArray getNonTerminals() {
    return nonTerminals;
  }

  public String getProduction(int ID) {
    switch (ID) {
      case 0:
        {
          return "Goal: E";
        }
      case 1:
        {
          return "E: E PL T";
        }
      case 2:
        {
          return "E: T";
        }
      case 3:
        {
          return "T: T ST F";
        }
      case 4:
        {
          return "T: F";
        }
      case 5:
        {
          return "F: OP E CP";
        }
      case 6:
        {
          return "F: ID";
        }
      default:
        {
          return null;
        }
    }
  }

  public int getProductionLength(int ID) {
    switch (ID) {
      case 0:
        {
          return 1;
        }
      case 1:
        {
          return 3;
        }
      case 2:
        {
          return 1;
        }
      case 3:
        {
          return 3;
        }
      case 4:
        {
          return 1;
        }
      case 5:
        {
          return 3;
        }
      case 6:
        {
          return 1;
        }
      default:
        {
          return -2147483644;
        }
    }
  }

  public int getProductionNonTerminal(int ID) {
    switch (ID) {
      case 0:
        {
          return 6;
        }
      case 1:
        {
          return 7;
        }
      case 2:
        {
          return 7;
        }
      case 3:
        {
          return 8;
        }
      case 4:
        {
          return 8;
        }
      case 5:
        {
          return 9;
        }
      case 6:
        {
          return 9;
        }
      default:
        {
          return -2147483644;
        }
    }
  }

  public IntArray getProductionSymbols(int ID) {
    switch (ID) {
      case 0:
        {
          return new ArrayIntArray(productionSymbols[0]);
        }
      case 1:
        {
          return new ArrayIntArray(productionSymbols[1]);
        }
      case 2:
        {
          return new ArrayIntArray(productionSymbols[2]);
        }
      case 3:
        {
          return new ArrayIntArray(productionSymbols[3]);
        }
      case 4:
        {
          return new ArrayIntArray(productionSymbols[4]);
        }
      case 5:
        {
          return new ArrayIntArray(productionSymbols[5]);
        }
      case 6:
        {
          return new ArrayIntArray(productionSymbols[6]);
        }
      default:
        {
          return null;
        }
    }
  }

  public IntArray getProductions() {
    return productions;
  }

  public int getStartContext() {
    return 0;
  }

  public String getTerminal(int ID) {
    switch (ID) {
      case 1:
        {
          return "PL";
        }
      case 2:
        {
          return "ST";
        }
      case 3:
        {
          return "OP";
        }
      case 4:
        {
          return "CP";
        }
      case 5:
        {
          return "ID";
        }
      default:
        {
          return null;
        }
    }
  }

  public IntArray getTerminalContexts(int ID) {
    switch (ID) {
      case 1:
        {
          return new ArrayIntArray(terminalContexts[0]);
        }
      case 2:
        {
          return new ArrayIntArray(terminalContexts[1]);
        }
      case 3:
        {
          return new ArrayIntArray(terminalContexts[2]);
        }
      case 4:
        {
          return new ArrayIntArray(terminalContexts[3]);
        }
      case 5:
        {
          return new ArrayIntArray(terminalContexts[4]);
        }
      default:
        {
          return null;
        }
    }
  }

  public Object getTerminalRegexp(int ID) {
    switch (ID) {
      case 1:
        {
          return "\\+";
        }
      case 2:
        {
          return "\\*";
        }
      case 3:
        {
          return "\\(";
        }
      case 4:
        {
          return "\\)";
        }
      case 5:
        {
          return " [_a-zA-Z] [_a-zA-Z0-9]* ";
        }
      default:
        {
          return null;
        }
    }
  }

  public IntArray getTerminals() {
    return terminals;
  }

  public String getVersion() {
    return "0.0.1";
  }

  public Translator newTranslator() {
    return newTranslator(null);
  }

  public Translator newTranslator(Properties p) {
    LRTranslator t = new com.inxar.syntacs.translator.lr.StandardLRTranslator();
    t.setLRTranslatorGrammar(this);
    t.setProperties(p);
    Input input = new com.inxar.syntacs.analyzer.StandardInput();
    t.setInput(input);
    Lexer lexer = new com.inxar.syntacs.analyzer.lexical.StandardLexer();
    t.setLexer(lexer);
    Parser parser = new com.inxar.syntacs.analyzer.syntactic.StandardParser();
    t.setParser(parser);
    LRTranslatorInterpreter interp =
        new com.inxar.syntacs.translator.lr.StandardLRTranslatorInterpreter();
    t.setLRTranslatorInterpreter(interp);
    lexer.initialize(new DFA[] {new EtfDefaultDFA()});
    parser.initialize(new EtfDPA());
    return t;
  }

  private int getDefaultContextAction(int tID) {
    switch (tID) {
      case 5:
        {}
      case 4:
        {}
      case 3:
        {}
      case 2:
        {}
      case 1:
        {
          return 1;
        }
      default:
        {
          return -2147483646;
        }
    }
  }

  private int getDefaultContextRegister(int tID) {
    switch (tID) {
      case 5:
        {}
      case 4:
        {}
      case 3:
        {}
      case 2:
        {}
      case 1:
        {
          return 0;
        }
      default:
        {
          return -2147483646;
        }
    }
  }

  private int[][] contextTerminals = new int[][] {{1, 2, 3, 4, 5}};
  private IntArray contexts = new ArrayIntArray(new int[] {0});
  private IntArray nonTerminals = new ArrayIntArray(new int[] {6, 7, 8, 9});
  private int[][] productionSymbols =
      new int[][] {{7}, {7, 1, 8}, {8}, {8, 2, 9}, {9}, {3, 7, 4}, {5}};
  private IntArray productions = new ArrayIntArray(new int[] {0, 1, 2, 3, 4, 5, 6});
  private int[][] terminalContexts = new int[][] {{0}, {0}, {0}, {0}, {0}};
  private IntArray terminals = new ArrayIntArray(new int[] {1, 2, 3, 4, 5});
}
