/**
 * $Id: SyntacsInterpreter.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.translator.syntacs;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.inxar.syntacs.analyzer.Symbol;
import org.inxar.syntacs.analyzer.Input;
import org.inxar.syntacs.analyzer.syntactic.Correction;
import org.inxar.syntacs.analyzer.syntactic.Sentence;
import org.inxar.syntacs.analyzer.syntactic.Recovery;
import org.inxar.syntacs.translator.TranslationException;
import org.inxar.syntacs.translator.lr.LRTranslatorGrammar;
import org.inxar.syntacs.util.Log;

import com.inxar.syntacs.analyzer.StringSymbol;
import com.inxar.syntacs.analyzer.ObjectSymbol;
import com.inxar.syntacs.analyzer.ListSymbol;
import com.inxar.syntacs.analyzer.ConstantSymbol;
import com.inxar.syntacs.analyzer.syntactic.StandardRecovery;
import com.inxar.syntacs.translator.StandardAuditor;
import com.inxar.syntacs.translator.lr.StandardLRTranslatorGrammar;
import com.inxar.syntacs.translator.lr.StandardLRTranslatorInterpreter;
import com.inxar.syntacs.util.Mission;
import com.inxar.syntacs.util.StringTools;

/**
 * <code>Interpreter</code> used in the translation of newtacs grammar
 * files (.stt).
 */
public class SyntacsInterpreter extends StandardLRTranslatorInterpreter {
  private static final boolean DEBUG = false;
  private static final boolean VERBOSE = true;

  public SyntacsInterpreter() {
    this.g = new StandardLRTranslatorGrammar();
    g.setAuditor(new StandardAuditor());
    initKeywords();
  }

  private void initKeywords() {
    kwds = new HashMap();
    kwd("this", SyntacsGrammar.T_THIS);
    kwd("is", SyntacsGrammar.T_IS);
    kwd("version", SyntacsGrammar.T_VERSION);
    kwd("start", SyntacsGrammar.T_START);
    kwd("in", SyntacsGrammar.T_IN);
    kwd("context", SyntacsGrammar.T_CONTEXT);
    kwd("terminal", SyntacsGrammar.T_TERMINAL);
    kwd("nonterminal", SyntacsGrammar.T_NONTERMINAL);
    kwd("reduce", SyntacsGrammar.T_REDUCE);
    kwd("includes", SyntacsGrammar.T_INCLUDES);
    kwd("when", SyntacsGrammar.T_WHEN);
    kwd("property", SyntacsGrammar.T_PROPERTY);
    kwd("shifts", SyntacsGrammar.T_SHIFTS);
    kwd("unshifts", SyntacsGrammar.T_UNSHIFTS);
    kwd("accept", SyntacsGrammar.T_ACCEPT);
    kwd("matches", SyntacsGrammar.T_MATCHES);
  }

  private void kwd(String key, int ID) {
    kwds.put(key, new Integer(ID));
  }

  public void match(int type, int off, int len) throws TranslationException {
    // init our symbol
    Symbol symbol = null;

    switch (type) {
      case SyntacsGrammar.T_COMMENT:
      case SyntacsGrammar.T_WHITESPACE:
        if (DEBUG) log().debug().write("Skipping whitespace").out();
        return;

      case SyntacsGrammar.T_EQUALS:
      case SyntacsGrammar.T_COMMA:
      case SyntacsGrammar.T_SEMI:
        symbol = new ConstantSymbol(type);
        break;

      case SyntacsGrammar.T_IDENT:
        String str = in.stretch(off, len);
        Integer ID = (Integer) kwds.get(str);

        if (ID != null) symbol = new ConstantSymbol(ID.intValue());
        else symbol = new StringSymbol(type, str);
        break;

      case SyntacsGrammar.T_VERSION_STRING:
        symbol = new StringSymbol(type, in.stretch(off, len));
        break;

      case SyntacsGrammar.T_STRING:
        String s = in.stretch(off + 1, len - 2);

        if (s.indexOf("\\\"") != -1) s = StringTools.replace(s, "\\\"", "\"");

        if (s.indexOf("\\;") != -1) s = StringTools.replace(s, "\\;", ";");

        symbol = new StringSymbol(type, s);
        break;

      default:
        throw new InternalError("Unknown terminal type: " + grammar.getTerminal(type));
    }

    if (symbol == null) throw new InternalError("Null terminal for " + grammar.getTerminal(type));

    parser.notify(symbol);
  }

  public Symbol reduce(int type, Sentence s) throws TranslationException {
    if (DEBUG) log().debug().write("REDUCE ").write(grammar.getProduction(type)).out();

    Symbol symbol = null;

    Iterator iter;
    Instruction ins;
    ListSymbol sym;

    switch (type) {

        // unary promotion cases.
        // case SyntacsGrammar.P__start___Goal_$:
      case SyntacsGrammar.P_Goal__ThisIsStatement_StatementList:
      case SyntacsGrammar.P_StatementList__StatementList_Statement:
      case SyntacsGrammar.P_StatementList__Statement:
      case SyntacsGrammar.P_Statement__TerminalStatement:
      case SyntacsGrammar.P_Statement__MatchesStatement:
      case SyntacsGrammar.P_Statement__ContextStatement:
      case SyntacsGrammar.P_Statement__StartInStatement:
      case SyntacsGrammar.P_Statement__NonterminalStatement:
      case SyntacsGrammar.P_Statement__IncludesStatement:
      case SyntacsGrammar.P_Statement__ReduceStatement:
      case SyntacsGrammar.P_Statement__AcceptWhenStatement:
      case SyntacsGrammar.P_Statement__PropertyStatement:
        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_Instruction__IDENT:
      case SyntacsGrammar.P_Instruction__IDENT_SHIFTS_IDENT:
      case SyntacsGrammar.P_Instruction__IDENT_UNSHIFTS:
        ins = new Instruction();
        ins.terminal = ((StringSymbol) s.at(0)).value;

        switch (type) {
          case SyntacsGrammar.P_Instruction__IDENT:
            ins.action = LRTranslatorGrammar.ACTION_PEEK;
            break;
          case SyntacsGrammar.P_Instruction__IDENT_UNSHIFTS:
            ins.action = LRTranslatorGrammar.ACTION_POP;
            break;
          case SyntacsGrammar.P_Instruction__IDENT_SHIFTS_IDENT:
            ins.action = LRTranslatorGrammar.ACTION_PUSH;
            ins.reg = ((StringSymbol) s.at(2)).value;
            break;
        }

        symbol = new ObjectSymbol(ins);
        break;

        // symbol list creation cases.
      case SyntacsGrammar.P_InstructionList__Instruction:
        symbol = new ListSymbol(s.at(0));
        break;

        // symbol list extension cases at 2.
      case SyntacsGrammar.P_InstructionList__InstructionList_COMMA_Instruction:
        sym = (ListSymbol) s.at(0);
        sym.list.add(s.at(2));
        symbol = sym;
        break;

        // string list creation.
      case SyntacsGrammar.P_IdentList__IDENT:
      case SyntacsGrammar.P_NonterminalList__IDENT:
        symbol = new ListSymbol(((StringSymbol) s.at(0)).value);
        break;

        // string list extension at 1
      case SyntacsGrammar.P_NonterminalList__NonterminalList_IDENT:
        sym = (ListSymbol) s.at(0);
        sym.list.add(((StringSymbol) s.at(1)).value);
        symbol = sym;
        break;

        // string list extension at 2
      case SyntacsGrammar.P_IdentList__IdentList_COMMA_IDENT:
        sym = (ListSymbol) s.at(0);
        sym.list.add(((StringSymbol) s.at(2)).value);
        symbol = sym;
        break;

      case SyntacsGrammar.P_ThisIsStatement__THIS_IS_IDENT_VERSION_VERSION_STRING_SEMI:
        g.setName(((StringSymbol) s.at(2)).value);
        g.setVersion(((StringSymbol) s.at(4)).value);

        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_TerminalStatement__TERMINAL_IdentList_SEMI:
        iter = ((ListSymbol) s.at(1)).list.iterator();
        while (iter.hasNext()) g.addTerminal((String) iter.next());

        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_MatchesStatement__IDENT_MATCHES_STRING_SEMI:
        g.setTerminalRegexp(((StringSymbol) s.at(0)).value, ((StringSymbol) s.at(2)).value);

        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_ContextStatement__CONTEXT_IdentList_SEMI:
        iter = ((ListSymbol) s.at(1)).list.iterator();
        while (iter.hasNext()) g.addContext((String) iter.next());

        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_IncludesStatement__IDENT_INCLUDES_InstructionList_SEMI:
        String context = ((StringSymbol) s.at(0)).value;
        iter = ((ListSymbol) s.at(2)).list.iterator();
        while (iter.hasNext()) {

          ins = (Instruction) ((ObjectSymbol) iter.next()).value;

          switch (ins.action) {
            case LRTranslatorGrammar.ACTION_PEEK:
              g.setContextPeek(context, ins.terminal);
              break;
            case LRTranslatorGrammar.ACTION_POP:
              g.setContextPop(context, ins.terminal);
              break;
            case LRTranslatorGrammar.ACTION_PUSH:
              g.setContextPush(context, ins.terminal, ins.reg);
              break;
          }
        }

        symbol = s.at(1);
        break;

      case SyntacsGrammar.P_StartInStatement__START_IN_CONTEXT_IDENT_SEMI:
        g.setStartContext(((StringSymbol) s.at(3)).value);
        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_NonterminalStatement__NONTERMINAL_IdentList_SEMI:
        iter = ((ListSymbol) s.at(1)).list.iterator();
        while (iter.hasNext()) g.addNonTerminal((String) iter.next());

        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_ReduceStatement__REDUCE_IDENT_WHEN_NonterminalList_SEMI:
        String nonterminal = ((StringSymbol) s.at(1)).value;
        List rhs = ((ListSymbol) s.at(3)).list;

        g.addProduction(nonterminal, rhs);

        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_AcceptWhenStatement__ACCEPT_WHEN_IDENT_SEMI:
        g.setGoalNonTerminal(((StringSymbol) s.at(2)).value);
        symbol = s.at(0);
        break;

      case SyntacsGrammar.P_PropertyStatement__PROPERTY_IDENT_EQUALS_STRING_SEMI:
        g.setProperty(((StringSymbol) s.at(1)).value, ((StringSymbol) s.at(3)).value);

        symbol = s.at(0);
        break;

      default:
        throw new InternalError("Unknown Production: " + grammar.getProduction(type));
    }

    // return the phrase
    return symbol;
  }

  public Recovery recover(int type, Sentence left_context) throws TranslationException {
    StandardRecovery r = new StandardRecovery();
    r.add(Correction.WAIT, new Integer(SyntacsGrammar.T_SEMI));
    r.add(Correction.TUMBLE);
    return r;
  }

  public void reset() {
    if (VERBOSE) log().info().write("Parsing stt...").time();
  }

  public Object getResult() {
    if (VERBOSE) log().info().touch();

    return g;
  }

  public void setInput(Input in) {
    super.setInput(in);
    g.setInput(in);
  }

  private Log log() {
    if (log == null) log = Mission.control().log("syn", this);
    return log;
  }

  private Map kwds;
  private Log log;

  // This is the output object, not the translator grammar used by
  // THIS parser per se (ie this is the object where data is
  // written, not where data is read).
  private StandardLRTranslatorGrammar g;

  private static class Instruction {
    String terminal;
    int action;
    String reg;
  }
}
