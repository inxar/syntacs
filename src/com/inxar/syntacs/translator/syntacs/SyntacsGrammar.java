/**
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * @author Paul Cody Johnston - pcj@inxar.org
 */
package com.inxar.syntacs.translator.syntacs;

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
 *  Translation Toolkit</a> on Fri Jul 06 12:05:11 PDT 2001<P><PRE># GRAMMAR
 *  DECLARATION
 * this is syntacs version 0.1.0;
 * 
 * # PROPERTY DEFINITIONS
 * property viz-namespace = "syntacs";
 * property viz-dpa-size = "20,20";
 * property viz-syntactic = "true";
 * property author-email = "pcj@inxar.org";
 * property viz-dpa-concentrate-edges = "true";
 * property author = "Paul Cody Johnston";
 * property viz-dpa-hide-loopback-edges = "false";
 * property compile-interpreter-classname =
 *  "com.inxar.syntacs.translator.syntacs.SyntacsInterpreter";
 * property viz-sourcepath = "./grammar";
 * property viz-dfa-size = "12,12";
 * property viz-dfa-rankdir = "TB";
 * property viz-lexical = "true";
 * property compile-namespace = "com.inxar.syntacs.translator.syntacs";
 * property compile-dpa-constructor-method = "LALR1";
 * property compile-sourcepath = "./src";
 * 
 * # TERMINAL DECLARATIONS
 * terminal COMMENT;
 * terminal WHITESPACE;
 * terminal THIS;
 * terminal IS;
 * terminal VERSION;
 * terminal START;
 * terminal IN;
 * terminal CONTEXT;
 * terminal TERMINAL;
 * terminal NONTERMINAL;
 * terminal REDUCE;
 * terminal INCLUDES;
 * terminal WHEN;
 * terminal SHIFTS;
 * terminal UNSHIFTS;
 * terminal ACCEPT;
 * terminal PROPERTY;
 * terminal MATCHES;
 * terminal IDENT;
 * terminal STRING;
 * terminal VERSION_STRING;
 * terminal COMMA;
 * terminal SEMI;
 * terminal EQUALS;
 * 
 * # TERMINAL DEFINITIONS
 * COMMENT matches "(#[^\n\r]*(\n|(\r\n)))";
 * WHITESPACE matches "(\t|\n|\v|\r|\s)+";
 * IDENT matches "([_a-zA-Z][-_a-zA-Z0-9]*)";
 * STRING matches "((\")([^\"\\]|(\\[^\n]))*(\"))";
 * VERSION_STRING matches "([0-9]+.[0-9]+.[0-9]+)";
 * COMMA matches ",";
 * SEMI matches "\;";
 * EQUALS matches "=";
 * 
 * # NONTERMINAL DECLARATIONS
 * nonterminal Goal;
 * nonterminal Statement;
 * nonterminal ThisIsStatement;
 * nonterminal TerminalStatement;
 * nonterminal MatchesStatement;
 * nonterminal ContextStatement;
 * nonterminal StartInStatement;
 * nonterminal IncludesStatement;
 * nonterminal NonterminalStatement;
 * nonterminal ReduceStatement;
 * nonterminal AcceptWhenStatement;
 * nonterminal PropertyStatement;
 * nonterminal StatementList;
 * nonterminal InstructionList;
 * nonterminal NonterminalList;
 * nonterminal IdentList;
 * nonterminal Instruction;
 * 
 * # NONTERMINAL DEFINITIONS
 * reduce Goal when ThisIsStatement StatementList;
 * reduce StatementList when Statement;
 * reduce StatementList when StatementList Statement;
 * reduce Statement when TerminalStatement;
 * reduce Statement when MatchesStatement;
 * reduce Statement when ContextStatement;
 * reduce Statement when StartInStatement;
 * reduce Statement when IncludesStatement;
 * reduce Statement when NonterminalStatement;
 * reduce Statement when ReduceStatement;
 * reduce Statement when AcceptWhenStatement;
 * reduce Statement when PropertyStatement;
 * reduce ThisIsStatement when THIS IS IDENT VERSION VERSION_STRING SEMI;
 * reduce TerminalStatement when TERMINAL IdentList SEMI;
 * reduce MatchesStatement when IDENT MATCHES STRING SEMI;
 * reduce NonterminalStatement when NONTERMINAL IdentList SEMI;
 * reduce ContextStatement when CONTEXT IdentList SEMI;
 * reduce StartInStatement when START IN CONTEXT IDENT SEMI;
 * reduce IncludesStatement when IDENT INCLUDES InstructionList SEMI;
 * reduce ReduceStatement when REDUCE IDENT WHEN NonterminalList SEMI;
 * reduce AcceptWhenStatement when ACCEPT WHEN IDENT SEMI;
 * reduce PropertyStatement when PROPERTY IDENT EQUALS STRING SEMI;
 * reduce IdentList when IDENT;
 * reduce IdentList when IdentList COMMA IDENT;
 * reduce NonterminalList when IDENT;
 * reduce NonterminalList when NonterminalList IDENT;
 * reduce InstructionList when Instruction;
 * reduce InstructionList when InstructionList COMMA Instruction;
 * reduce Instruction when IDENT;
 * reduce Instruction when IDENT SHIFTS IDENT;
 * reduce Instruction when IDENT UNSHIFTS;
 * 
 * accept when Goal;
 * 
 * # CONTEXT DECLARATIONS
 * context default;
 * 
 * # CONTEXT DEFINITIONS
 * default includes COMMENT, WHITESPACE, IDENT, STRING, VERSION_STRING, COMMA,
 *  SEMI, EQUALS;
 * 
 * start in context default;
 * 
 * </PRE>
 */
public class SyntacsGrammar
implements LRTranslatorGrammar
{
    
    /**
     * Constant ID for default
     */
    public static final int C_default = 0;
    
    /**
     * Terminal ID for COMMENT
     */
    public static final int T_COMMENT = 1;
    
    /**
     * Terminal ID for WHITESPACE
     */
    public static final int T_WHITESPACE = 2;
    
    /**
     * Terminal ID for THIS
     */
    public static final int T_THIS = 3;
    
    /**
     * Terminal ID for IS
     */
    public static final int T_IS = 4;
    
    /**
     * Terminal ID for VERSION
     */
    public static final int T_VERSION = 5;
    
    /**
     * Terminal ID for START
     */
    public static final int T_START = 6;
    
    /**
     * Terminal ID for IN
     */
    public static final int T_IN = 7;
    
    /**
     * Terminal ID for CONTEXT
     */
    public static final int T_CONTEXT = 8;
    
    /**
     * Terminal ID for TERMINAL
     */
    public static final int T_TERMINAL = 9;
    
    /**
     * Terminal ID for NONTERMINAL
     */
    public static final int T_NONTERMINAL = 10;
    
    /**
     * Terminal ID for REDUCE
     */
    public static final int T_REDUCE = 11;
    
    /**
     * Terminal ID for INCLUDES
     */
    public static final int T_INCLUDES = 12;
    
    /**
     * Terminal ID for WHEN
     */
    public static final int T_WHEN = 13;
    
    /**
     * Terminal ID for SHIFTS
     */
    public static final int T_SHIFTS = 14;
    
    /**
     * Terminal ID for UNSHIFTS
     */
    public static final int T_UNSHIFTS = 15;
    
    /**
     * Terminal ID for ACCEPT
     */
    public static final int T_ACCEPT = 16;
    
    /**
     * Terminal ID for PROPERTY
     */
    public static final int T_PROPERTY = 17;
    
    /**
     * Terminal ID for MATCHES
     */
    public static final int T_MATCHES = 18;
    
    /**
     * Terminal ID for IDENT
     */
    public static final int T_IDENT = 19;
    
    /**
     * Terminal ID for STRING
     */
    public static final int T_STRING = 20;
    
    /**
     * Terminal ID for VERSION_STRING
     */
    public static final int T_VERSION_STRING = 21;
    
    /**
     * Terminal ID for COMMA
     */
    public static final int T_COMMA = 22;
    
    /**
     * Terminal ID for SEMI
     */
    public static final int T_SEMI = 23;
    
    /**
     * Terminal ID for EQUALS
     */
    public static final int T_EQUALS = 24;
    
    /**
     * NonTerminal ID for Goal
     */
    public static final int N_Goal = 25;
    
    /**
     * NonTerminal ID for Statement
     */
    public static final int N_Statement = 26;
    
    /**
     * NonTerminal ID for ThisIsStatement
     */
    public static final int N_ThisIsStatement = 27;
    
    /**
     * NonTerminal ID for TerminalStatement
     */
    public static final int N_TerminalStatement = 28;
    
    /**
     * NonTerminal ID for MatchesStatement
     */
    public static final int N_MatchesStatement = 29;
    
    /**
     * NonTerminal ID for ContextStatement
     */
    public static final int N_ContextStatement = 30;
    
    /**
     * NonTerminal ID for StartInStatement
     */
    public static final int N_StartInStatement = 31;
    
    /**
     * NonTerminal ID for IncludesStatement
     */
    public static final int N_IncludesStatement = 32;
    
    /**
     * NonTerminal ID for NonterminalStatement
     */
    public static final int N_NonterminalStatement = 33;
    
    /**
     * NonTerminal ID for ReduceStatement
     */
    public static final int N_ReduceStatement = 34;
    
    /**
     * NonTerminal ID for AcceptWhenStatement
     */
    public static final int N_AcceptWhenStatement = 35;
    
    /**
     * NonTerminal ID for PropertyStatement
     */
    public static final int N_PropertyStatement = 36;
    
    /**
     * NonTerminal ID for StatementList
     */
    public static final int N_StatementList = 37;
    
    /**
     * NonTerminal ID for InstructionList
     */
    public static final int N_InstructionList = 38;
    
    /**
     * NonTerminal ID for NonterminalList
     */
    public static final int N_NonterminalList = 39;
    
    /**
     * NonTerminal ID for IdentList
     */
    public static final int N_IdentList = 40;
    
    /**
     * NonTerminal ID for Instruction
     */
    public static final int N_Instruction = 41;
    
    /**
     * Production ID for Goal__ThisIsStatement_StatementList
     */
    public static final int P_Goal__ThisIsStatement_StatementList = 0;
    
    /**
     * Production ID for StatementList__Statement
     */
    public static final int P_StatementList__Statement = 1;
    
    /**
     * Production ID for StatementList__StatementList_Statement
     */
    public static final int P_StatementList__StatementList_Statement = 2;
    
    /**
     * Production ID for Statement__TerminalStatement
     */
    public static final int P_Statement__TerminalStatement = 3;
    
    /**
     * Production ID for Statement__MatchesStatement
     */
    public static final int P_Statement__MatchesStatement = 4;
    
    /**
     * Production ID for Statement__ContextStatement
     */
    public static final int P_Statement__ContextStatement = 5;
    
    /**
     * Production ID for Statement__StartInStatement
     */
    public static final int P_Statement__StartInStatement = 6;
    
    /**
     * Production ID for Statement__IncludesStatement
     */
    public static final int P_Statement__IncludesStatement = 7;
    
    /**
     * Production ID for Statement__NonterminalStatement
     */
    public static final int P_Statement__NonterminalStatement = 8;
    
    /**
     * Production ID for Statement__ReduceStatement
     */
    public static final int P_Statement__ReduceStatement = 9;
    
    /**
     * Production ID for Statement__AcceptWhenStatement
     */
    public static final int P_Statement__AcceptWhenStatement = 10;
    
    /**
     * Production ID for Statement__PropertyStatement
     */
    public static final int P_Statement__PropertyStatement = 11;
    
    /**
     * Production ID for
     *  ThisIsStatement__THIS_IS_IDENT_VERSION_VERSION_STRING_SEMI
     */
    public static final int P_ThisIsStatement__THIS_IS_IDENT_VERSION_VERSION_STRING_SEMI = 12;
    
    /**
     * Production ID for TerminalStatement__TERMINAL_IdentList_SEMI
     */
    public static final int P_TerminalStatement__TERMINAL_IdentList_SEMI = 13;
    
    /**
     * Production ID for MatchesStatement__IDENT_MATCHES_STRING_SEMI
     */
    public static final int P_MatchesStatement__IDENT_MATCHES_STRING_SEMI = 14;
    
    /**
     * Production ID for NonterminalStatement__NONTERMINAL_IdentList_SEMI
     */
    public static final int P_NonterminalStatement__NONTERMINAL_IdentList_SEMI = 15;
    
    /**
     * Production ID for ContextStatement__CONTEXT_IdentList_SEMI
     */
    public static final int P_ContextStatement__CONTEXT_IdentList_SEMI = 16;
    
    /**
     * Production ID for StartInStatement__START_IN_CONTEXT_IDENT_SEMI
     */
    public static final int P_StartInStatement__START_IN_CONTEXT_IDENT_SEMI = 17;
    
    /**
     * Production ID for IncludesStatement__IDENT_INCLUDES_InstructionList_SEMI
     */
    public static final int P_IncludesStatement__IDENT_INCLUDES_InstructionList_SEMI = 18;
    
    /**
     * Production ID for ReduceStatement__REDUCE_IDENT_WHEN_NonterminalList_SEMI
     */
    public static final int P_ReduceStatement__REDUCE_IDENT_WHEN_NonterminalList_SEMI = 19;
    
    /**
     * Production ID for AcceptWhenStatement__ACCEPT_WHEN_IDENT_SEMI
     */
    public static final int P_AcceptWhenStatement__ACCEPT_WHEN_IDENT_SEMI = 20;
    
    /**
     * Production ID for PropertyStatement__PROPERTY_IDENT_EQUALS_STRING_SEMI
     */
    public static final int P_PropertyStatement__PROPERTY_IDENT_EQUALS_STRING_SEMI = 21;
    
    /**
     * Production ID for IdentList__IDENT
     */
    public static final int P_IdentList__IDENT = 22;
    
    /**
     * Production ID for IdentList__IdentList_COMMA_IDENT
     */
    public static final int P_IdentList__IdentList_COMMA_IDENT = 23;
    
    /**
     * Production ID for NonterminalList__IDENT
     */
    public static final int P_NonterminalList__IDENT = 24;
    
    /**
     * Production ID for NonterminalList__NonterminalList_IDENT
     */
    public static final int P_NonterminalList__NonterminalList_IDENT = 25;
    
    /**
     * Production ID for InstructionList__Instruction
     */
    public static final int P_InstructionList__Instruction = 26;
    
    /**
     * Production ID for InstructionList__InstructionList_COMMA_Instruction
     */
    public static final int P_InstructionList__InstructionList_COMMA_Instruction = 27;
    
    /**
     * Production ID for Instruction__IDENT
     */
    public static final int P_Instruction__IDENT = 28;
    
    /**
     * Production ID for Instruction__IDENT_SHIFTS_IDENT
     */
    public static final int P_Instruction__IDENT_SHIFTS_IDENT = 29;
    
    /**
     * Production ID for Instruction__IDENT_UNSHIFTS
     */
    public static final int P_Instruction__IDENT_UNSHIFTS = 30;
    
    public String getContext(int ID)
    {
        switch (ID) {
            case 0: {
                return "default";
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public int getContextAction(int cID, int tID)
    {
        switch (cID) {
            case 0: {
                return getDefaultContextAction(tID);
            } 
            default: {
                return -2147483647;
            } 
        } 
    }
    
    public int getContextRegister(int cID, int tID)
    {
        switch (cID) {
            case 0: {
                return getDefaultContextRegister(tID);
            } 
            default: {
                return -2147483647;
            } 
        } 
    }
    
    public IntArray getContextTerminals(int ID)
    {
        switch (ID) {
            case 0: {
                return new ArrayIntArray(contextTerminals[0]);
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public IntArray getContexts()
    {
        return contexts;
    }
    
    public int getGoalNonTerminal()
    {
        return 25;
    }
    
    public String getName()
    {
        return "syntacs";
    }
    
    public String getNonTerminal(int ID)
    {
        switch (ID) {
            case 25: {
                return "Goal";
            } 
            case 26: {
                return "Statement";
            } 
            case 27: {
                return "ThisIsStatement";
            } 
            case 28: {
                return "TerminalStatement";
            } 
            case 29: {
                return "MatchesStatement";
            } 
            case 30: {
                return "ContextStatement";
            } 
            case 31: {
                return "StartInStatement";
            } 
            case 32: {
                return "IncludesStatement";
            } 
            case 33: {
                return "NonterminalStatement";
            } 
            case 34: {
                return "ReduceStatement";
            } 
            case 35: {
                return "AcceptWhenStatement";
            } 
            case 36: {
                return "PropertyStatement";
            } 
            case 37: {
                return "StatementList";
            } 
            case 38: {
                return "InstructionList";
            } 
            case 39: {
                return "NonterminalList";
            } 
            case 40: {
                return "IdentList";
            } 
            case 41: {
                return "Instruction";
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public IntArray getNonTerminals()
    {
        return nonTerminals;
    }
    
    public String getProduction(int ID)
    {
        switch (ID) {
            case 0: {
                return "Goal: ThisIsStatement StatementList";
            } 
            case 1: {
                return "StatementList: Statement";
            } 
            case 2: {
                return "StatementList: StatementList Statement";
            } 
            case 3: {
                return "Statement: TerminalStatement";
            } 
            case 4: {
                return "Statement: MatchesStatement";
            } 
            case 5: {
                return "Statement: ContextStatement";
            } 
            case 6: {
                return "Statement: StartInStatement";
            } 
            case 7: {
                return "Statement: IncludesStatement";
            } 
            case 8: {
                return "Statement: NonterminalStatement";
            } 
            case 9: {
                return "Statement: ReduceStatement";
            } 
            case 10: {
                return "Statement: AcceptWhenStatement";
            } 
            case 11: {
                return "Statement: PropertyStatement";
            } 
            case 12: {
                return "ThisIsStatement: THIS IS IDENT VERSION VERSION_STRING SEMI";
            } 
            case 13: {
                return "TerminalStatement: TERMINAL IdentList SEMI";
            } 
            case 14: {
                return "MatchesStatement: IDENT MATCHES STRING SEMI";
            } 
            case 15: {
                return "NonterminalStatement: NONTERMINAL IdentList SEMI";
            } 
            case 16: {
                return "ContextStatement: CONTEXT IdentList SEMI";
            } 
            case 17: {
                return "StartInStatement: START IN CONTEXT IDENT SEMI";
            } 
            case 18: {
                return "IncludesStatement: IDENT INCLUDES InstructionList SEMI";
            } 
            case 19: {
                return "ReduceStatement: REDUCE IDENT WHEN NonterminalList SEMI";
            } 
            case 20: {
                return "AcceptWhenStatement: ACCEPT WHEN IDENT SEMI";
            } 
            case 21: {
                return "PropertyStatement: PROPERTY IDENT EQUALS STRING SEMI";
            } 
            case 22: {
                return "IdentList: IDENT";
            } 
            case 23: {
                return "IdentList: IdentList COMMA IDENT";
            } 
            case 24: {
                return "NonterminalList: IDENT";
            } 
            case 25: {
                return "NonterminalList: NonterminalList IDENT";
            } 
            case 26: {
                return "InstructionList: Instruction";
            } 
            case 27: {
                return "InstructionList: InstructionList COMMA Instruction";
            } 
            case 28: {
                return "Instruction: IDENT";
            } 
            case 29: {
                return "Instruction: IDENT SHIFTS IDENT";
            } 
            case 30: {
                return "Instruction: IDENT UNSHIFTS";
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public int getProductionLength(int ID)
    {
        switch (ID) {
            case 0: {
                return 2;
            } 
            case 1: {
                return 1;
            } 
            case 2: {
                return 2;
            } 
            case 3: {
                return 1;
            } 
            case 4: {
                return 1;
            } 
            case 5: {
                return 1;
            } 
            case 6: {
                return 1;
            } 
            case 7: {
                return 1;
            } 
            case 8: {
                return 1;
            } 
            case 9: {
                return 1;
            } 
            case 10: {
                return 1;
            } 
            case 11: {
                return 1;
            } 
            case 12: {
                return 6;
            } 
            case 13: {
                return 3;
            } 
            case 14: {
                return 4;
            } 
            case 15: {
                return 3;
            } 
            case 16: {
                return 3;
            } 
            case 17: {
                return 5;
            } 
            case 18: {
                return 4;
            } 
            case 19: {
                return 5;
            } 
            case 20: {
                return 4;
            } 
            case 21: {
                return 5;
            } 
            case 22: {
                return 1;
            } 
            case 23: {
                return 3;
            } 
            case 24: {
                return 1;
            } 
            case 25: {
                return 2;
            } 
            case 26: {
                return 1;
            } 
            case 27: {
                return 3;
            } 
            case 28: {
                return 1;
            } 
            case 29: {
                return 3;
            } 
            case 30: {
                return 2;
            } 
            default: {
                return -2147483644;
            } 
        } 
    }
    
    public int getProductionNonTerminal(int ID)
    {
        switch (ID) {
            case 0: {
                return 25;
            } 
            case 1: {
                return 37;
            } 
            case 2: {
                return 37;
            } 
            case 3: {
                return 26;
            } 
            case 4: {
                return 26;
            } 
            case 5: {
                return 26;
            } 
            case 6: {
                return 26;
            } 
            case 7: {
                return 26;
            } 
            case 8: {
                return 26;
            } 
            case 9: {
                return 26;
            } 
            case 10: {
                return 26;
            } 
            case 11: {
                return 26;
            } 
            case 12: {
                return 27;
            } 
            case 13: {
                return 28;
            } 
            case 14: {
                return 29;
            } 
            case 15: {
                return 33;
            } 
            case 16: {
                return 30;
            } 
            case 17: {
                return 31;
            } 
            case 18: {
                return 32;
            } 
            case 19: {
                return 34;
            } 
            case 20: {
                return 35;
            } 
            case 21: {
                return 36;
            } 
            case 22: {
                return 40;
            } 
            case 23: {
                return 40;
            } 
            case 24: {
                return 39;
            } 
            case 25: {
                return 39;
            } 
            case 26: {
                return 38;
            } 
            case 27: {
                return 38;
            } 
            case 28: {
                return 41;
            } 
            case 29: {
                return 41;
            } 
            case 30: {
                return 41;
            } 
            default: {
                return -2147483644;
            } 
        } 
    }
    
    public IntArray getProductionSymbols(int ID)
    {
        switch (ID) {
            case 0: {
                return new ArrayIntArray(productionSymbols[0]);
            } 
            case 1: {
                return new ArrayIntArray(productionSymbols[1]);
            } 
            case 2: {
                return new ArrayIntArray(productionSymbols[2]);
            } 
            case 3: {
                return new ArrayIntArray(productionSymbols[3]);
            } 
            case 4: {
                return new ArrayIntArray(productionSymbols[4]);
            } 
            case 5: {
                return new ArrayIntArray(productionSymbols[5]);
            } 
            case 6: {
                return new ArrayIntArray(productionSymbols[6]);
            } 
            case 7: {
                return new ArrayIntArray(productionSymbols[7]);
            } 
            case 8: {
                return new ArrayIntArray(productionSymbols[8]);
            } 
            case 9: {
                return new ArrayIntArray(productionSymbols[9]);
            } 
            case 10: {
                return new ArrayIntArray(productionSymbols[10]);
            } 
            case 11: {
                return new ArrayIntArray(productionSymbols[11]);
            } 
            case 12: {
                return new ArrayIntArray(productionSymbols[12]);
            } 
            case 13: {
                return new ArrayIntArray(productionSymbols[13]);
            } 
            case 14: {
                return new ArrayIntArray(productionSymbols[14]);
            } 
            case 15: {
                return new ArrayIntArray(productionSymbols[15]);
            } 
            case 16: {
                return new ArrayIntArray(productionSymbols[16]);
            } 
            case 17: {
                return new ArrayIntArray(productionSymbols[17]);
            } 
            case 18: {
                return new ArrayIntArray(productionSymbols[18]);
            } 
            case 19: {
                return new ArrayIntArray(productionSymbols[19]);
            } 
            case 20: {
                return new ArrayIntArray(productionSymbols[20]);
            } 
            case 21: {
                return new ArrayIntArray(productionSymbols[21]);
            } 
            case 22: {
                return new ArrayIntArray(productionSymbols[22]);
            } 
            case 23: {
                return new ArrayIntArray(productionSymbols[23]);
            } 
            case 24: {
                return new ArrayIntArray(productionSymbols[24]);
            } 
            case 25: {
                return new ArrayIntArray(productionSymbols[25]);
            } 
            case 26: {
                return new ArrayIntArray(productionSymbols[26]);
            } 
            case 27: {
                return new ArrayIntArray(productionSymbols[27]);
            } 
            case 28: {
                return new ArrayIntArray(productionSymbols[28]);
            } 
            case 29: {
                return new ArrayIntArray(productionSymbols[29]);
            } 
            case 30: {
                return new ArrayIntArray(productionSymbols[30]);
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public IntArray getProductions()
    {
        return productions;
    }
    
    public int getStartContext()
    {
        return 0;
    }
    
    public String getTerminal(int ID)
    {
        switch (ID) {
            case 1: {
                return "COMMENT";
            } 
            case 2: {
                return "WHITESPACE";
            } 
            case 3: {
                return "THIS";
            } 
            case 4: {
                return "IS";
            } 
            case 5: {
                return "VERSION";
            } 
            case 6: {
                return "START";
            } 
            case 7: {
                return "IN";
            } 
            case 8: {
                return "CONTEXT";
            } 
            case 9: {
                return "TERMINAL";
            } 
            case 10: {
                return "NONTERMINAL";
            } 
            case 11: {
                return "REDUCE";
            } 
            case 12: {
                return "INCLUDES";
            } 
            case 13: {
                return "WHEN";
            } 
            case 14: {
                return "SHIFTS";
            } 
            case 15: {
                return "UNSHIFTS";
            } 
            case 16: {
                return "ACCEPT";
            } 
            case 17: {
                return "PROPERTY";
            } 
            case 18: {
                return "MATCHES";
            } 
            case 19: {
                return "IDENT";
            } 
            case 20: {
                return "STRING";
            } 
            case 21: {
                return "VERSION_STRING";
            } 
            case 22: {
                return "COMMA";
            } 
            case 23: {
                return "SEMI";
            } 
            case 24: {
                return "EQUALS";
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public IntArray getTerminalContexts(int ID)
    {
        switch (ID) {
            case 1: {
                return new ArrayIntArray(terminalContexts[0]);
            } 
            case 2: {
                return new ArrayIntArray(terminalContexts[1]);
            } 
            case 3: {
                return new ArrayIntArray(terminalContexts[2]);
            } 
            case 4: {
                return new ArrayIntArray(terminalContexts[3]);
            } 
            case 5: {
                return new ArrayIntArray(terminalContexts[4]);
            } 
            case 6: {
                return new ArrayIntArray(terminalContexts[5]);
            } 
            case 7: {
                return new ArrayIntArray(terminalContexts[6]);
            } 
            case 8: {
                return new ArrayIntArray(terminalContexts[7]);
            } 
            case 9: {
                return new ArrayIntArray(terminalContexts[8]);
            } 
            case 10: {
                return new ArrayIntArray(terminalContexts[9]);
            } 
            case 11: {
                return new ArrayIntArray(terminalContexts[10]);
            } 
            case 12: {
                return new ArrayIntArray(terminalContexts[11]);
            } 
            case 13: {
                return new ArrayIntArray(terminalContexts[12]);
            } 
            case 14: {
                return new ArrayIntArray(terminalContexts[13]);
            } 
            case 15: {
                return new ArrayIntArray(terminalContexts[14]);
            } 
            case 16: {
                return new ArrayIntArray(terminalContexts[15]);
            } 
            case 17: {
                return new ArrayIntArray(terminalContexts[16]);
            } 
            case 18: {
                return new ArrayIntArray(terminalContexts[17]);
            } 
            case 19: {
                return new ArrayIntArray(terminalContexts[18]);
            } 
            case 20: {
                return new ArrayIntArray(terminalContexts[19]);
            } 
            case 21: {
                return new ArrayIntArray(terminalContexts[20]);
            } 
            case 22: {
                return new ArrayIntArray(terminalContexts[21]);
            } 
            case 23: {
                return new ArrayIntArray(terminalContexts[22]);
            } 
            case 24: {
                return new ArrayIntArray(terminalContexts[23]);
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public Object getTerminalRegexp(int ID)
    {
        switch (ID) {
            case 1: {
                return "(#[^\\n\\r]*(\\n|(\\r\\n)))";
            } 
            case 2: {
                return "(\\t|\\n|\\v|\\r|\\s)+";
            } 
            case 3: {
                return null;
            } 
            case 4: {
                return null;
            } 
            case 5: {
                return null;
            } 
            case 6: {
                return null;
            } 
            case 7: {
                return null;
            } 
            case 8: {
                return null;
            } 
            case 9: {
                return null;
            } 
            case 10: {
                return null;
            } 
            case 11: {
                return null;
            } 
            case 12: {
                return null;
            } 
            case 13: {
                return null;
            } 
            case 14: {
                return null;
            } 
            case 15: {
                return null;
            } 
            case 16: {
                return null;
            } 
            case 17: {
                return null;
            } 
            case 18: {
                return null;
            } 
            case 19: {
                return "([_a-zA-Z][-_a-zA-Z0-9]*)";
            } 
            case 20: {
                return "((\")([^\"\\\\]|(\\\\[^\\n]))*(\"))";
            } 
            case 21: {
                return "([0-9]+.[0-9]+.[0-9]+)";
            } 
            case 22: {
                return ",";
            } 
            case 23: {
                return ";";
            } 
            case 24: {
                return "=";
            } 
            default: {
                return null;
            } 
        } 
    }
    
    public IntArray getTerminals()
    {
        return terminals;
    }
    
    public String getVersion()
    {
        return "0.1.0";
    }
    
    public Translator newTranslator()
    {
        return newTranslator(null);
    }
    
    public Translator newTranslator(Properties p)
    {
        LRTranslator t = new com.inxar.syntacs.translator.lr.StandardLRTranslator();
        t.setLRTranslatorGrammar(this);
        t.setProperties(p);
        Input input = new com.inxar.syntacs.analyzer.StandardInput();
        t.setInput(input);
        Lexer lexer = new com.inxar.syntacs.analyzer.lexical.StandardLexer();
        t.setLexer(lexer);
        Parser parser = new com.inxar.syntacs.analyzer.syntactic.StandardParser();
        t.setParser(parser);
        LRTranslatorInterpreter interp = new com.inxar.syntacs.translator.syntacs.SyntacsInterpreter();
        t.setLRTranslatorInterpreter(interp);
        lexer.initialize(new DFA[]{new SyntacsDefaultDFA()});
        parser.initialize(new SyntacsDPA());
        return t;
    }
    
    private int getDefaultContextAction(int tID)
    {
        switch (tID) {
            case 24: {
            }
            case 23: {
            }
            case 22: {
            }
            case 21: {
            }
            case 20: {
            }
            case 19: {
            }
            case 2: {
            }
            case 1: {
                return 1;
            } 
            default: {
                return -2147483646;
            } 
        } 
    }
    
    private int getDefaultContextRegister(int tID)
    {
        switch (tID) {
            case 24: {
            }
            case 23: {
            }
            case 22: {
            }
            case 21: {
            }
            case 20: {
            }
            case 19: {
            }
            case 2: {
            }
            case 1: {
                return 0;
            } 
            default: {
                return -2147483646;
            } 
        } 
    }
    private int[][] contextTerminals = new int[][]{{1, 2, 19, 20, 21, 22, 23, 24}};
    private IntArray contexts = new ArrayIntArray(new int[]{0});
    private IntArray nonTerminals = new ArrayIntArray(new int[]{25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41});
    private int[][] productionSymbols = new int[][]{{27, 37}, {26}, {37, 26}, {28}, {29}, {30}, {31}, {32}, {33}, {34}, {35}, {36}, {3, 4, 19, 5, 21, 23}, {9, 40, 23}, {19, 18, 20, 23}, {10, 40, 23}, {8, 40, 23}, {6, 7, 8, 19, 23}, {19, 12, 38, 23}, {11, 19, 13, 39, 23}, {16, 13, 19, 23}, {17, 19, 24, 20, 23}, {19}, {40, 22, 19}, {19}, {39, 19}, {41}, {38, 22, 41}, {19}, {19, 14, 19}, {19, 15}};
    private IntArray productions = new ArrayIntArray(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30});
    private int[][] terminalContexts = new int[][]{{0}, {0}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {0}, {0}, {0}, {0}, {0}, {0}};
    private IntArray terminals = new ArrayIntArray(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24});
}