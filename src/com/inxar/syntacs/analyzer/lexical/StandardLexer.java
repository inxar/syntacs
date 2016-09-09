/**
 * $Id: StandardLexer.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.analyzer.lexical;

import java.io.*;
import org.inxar.syntacs.analyzer.*;
import org.inxar.syntacs.analyzer.lexical.*;
import org.inxar.syntacs.grammar.Token;
import org.inxar.syntacs.automaton.finite.*;
import org.inxar.syntacs.translator.*;
import org.inxar.syntacs.translator.lr.*;
import com.inxar.syntacs.translator.lr.*;
import org.inxar.syntacs.util.*;
import com.inxar.syntacs.util.*;

/**
 * Concrete <code>Lexer</code> implementation which uses a
 * <code>DFA</code> for the recognition engine.  
 */
public class StandardLexer extends AbstractLRTranslationComponent 
    implements Lexer
{
    private final static boolean DEBUG = true;

    /**
     * Constructs the <code>StandardLexer</code>.
     */
    public StandardLexer()
    {
    }

    /**
     * The argument must be a <code>DFA</code> or a <code>DFA[]</code>
     * array.  
     */
    public void initialize(Object arg)
    {
	if (arg instanceof DFA) 
	    this.dfas = new DFA[] { (DFA)arg };
	else if (arg instanceof DFA[]) {
	    this.dfas = (DFA[])arg;

	    // If we get an array of dfas that has several contexts,
	    // we assume that there are instructions which switch
	    // context, so we initialize the stack.
	    if (dfas.length > 1)
		this.stack = new ArrayIntStack();
	} else
	    throw new IllegalArgumentException
		("Initialization argument must be a single "+
		 "DFA or an array of DFA, not "+arg);

    }

    public void start() throws TranslationException
    {
	this.m_context = grammar.getStartContext();
	if (dfas.length > 1)
	    stack.push(m_context);

	resume();
    }

    public void resume() throws TranslationException
    {
	// Since this method only checks whether it has been paused
	// AFTER it matches a token, there are no variables that need
	// to be synchronized with instance variables.
	
	if (this.dfas == null)
	    throw new IllegalStateException
		("The Lexer must be initialized before it can be run.");

	// Turn off the paused flag if it was on.
	m_isPaused = false;

	// ================================================================
	// Declare local variables
	// ================================================================
	Input in;		// The input source
	DFA[] dfas;		// The contexts
	DFA dfa;		// The current context
	ArrayIntStack stack;	// A stack to manage contexts
	int p;			// current dfa state
	int t, u;		// current/next token id numbers
	int mt, nt;		// begin/end token markers
	int me;			// begin error markers (end marker unnecessary)
	int cp;			// current context
	int off;		// input offset;
	boolean isContextual;	// flag to say whether we do context switching. 
				// This is used as an optimization since I 
				// don't want to maintain two lexer classes 
				// (contextual v. non-contextual).

	// ================================================================
	// Define local variables
	// ================================================================
	in = this.in;		// get local handle to input
	dfas = this.dfas;	// get local handle to dfa array
	stack = this.stack;	// get local handle to context stack
	cp = this.m_context;	// set context to member context
	dfa = dfas[cp];		// get local handle to current context dfa
	p = DFA.START_STATE;	// set current dfa state to start state
	t = Token.UNDEF;	// set current token to undefined
	off = mt = me = nt = in.atch();	// set all offset markers to currentpos
	isContextual = dfas.length > 1;	// set contextual hack

	// ================================================================
	// Loop until the lexer is paused or until all chars in the
	// input have been classified as errors or tokens.
	// ================================================================
    main:
	while (true) {

	    // catch EOF
	    try {
		
		if (DEBUG && debug) {
		    int c = in.broach();
		    Channel debug = log().debug()
			.write("at pos ").write(off)
			.write(" in state ").write(p)
			.write(" facing ");
		    
		    if (c < 0)
			debug.write("EOF");
		    else
			debug.write('\'').write((char)c).write('\'');

		    debug.write(" tracking ");
		    
		local:
		    switch (t) {
		    case Token.UNDEF:
			debug.write("UNDEF starting at ").write(mt);
			break local;
		    case Token.ERROR:
			debug.write("ERROR starting at ").write(me);
			break local;
		    default:
			debug.write(grammar.getTerminal(t))
			    .write(" starting at ").write(me);
			if (nt > 0)
			    debug.write(" and ending at ").write(nt - 1);
		    }

		    debug.out();

		    if (c > 0) {
			int q = dfa.go(p, (char)c);
			debug.write("DFA says: \"move from state ")
			    .write(p)
			    .write(" to ")
			    .write(q)
			    .write(" over ")
			    .write(c)
			    .write("\"")
			    .out();
		    }
		} /* end-debug-if */

		// =====================================================
		// Get the next input char and go to the next state in
		// the dfa transition graph.
		// =====================================================
		++off; p = dfa.go(p, in.getch());

		// =====================================================
		// Check if we are now in the dead state
		// =====================================================
		if (p == DFA.DEAD_STATE) {

		    // =================================================
		    // alternative A: yes, we're in the dead state (a
		    // dead input char).
		    // =================================================
		    // If the current token is UNDEF, we're in the
		    // pluripotent state.  If it's error, then we are
		    // in open-error, else we're in open token.
		token_switch:
		    switch (t) {
			
		    case Token.UNDEF: 
			if (DEBUG && debug) 
			    log().debug()
				.write("Moving from pluripotent to open-error")
				.out();

			// If the token value is undefined, it means
			// that the lexer is still in the pluripotent
			// state.  Now having hit the dead state, we
			// can move to open-error, reset to the start
			// state, and continue the input loop.
			t = Token.ERROR;
			mt = -1;
			p = DFA.START_STATE;
			break token_switch;
			
		    case Token.ERROR:
			if (DEBUG && debug) 
			    if (mt < 0)
				log().debug()
				    .write("Moving from open-error to open-error")
				    .out();
			    else
				log().debug()
				    .write("Moving from phoenix-error to open-error")
				    .out();

			// If the token value is error, it means that
			// we are already in the open-error state;
			// reset to the start state.  However, we also
			// need to negate the beginning-of-token
			// marker if it was set (by the phoenix
			// state).
			p = DFA.START_STATE;
			mt = -1;
			break token_switch;

		    default:
			if (DEBUG && debug) 
			    log().debug()
				.write("Moving from open-token to closed-token")
				.out();
			
			// The default case is selected when the token
			// value is defined and not ERROR.  From this
			// we can conclude that we are in the
			// open-token state, which can now be closed.

			if (DEBUG && debug) 
			    log().debug()
				.write("Match for ")
				.write(grammar.getTerminal(t))
				.write(" at ")
				.write(mt)
				.write(" with length ")
				.write(nt - mt)
				.out();

			// Inform the interpreter of the token ID,
			// offset, and length.
			interpreter.match(t, mt, nt - mt);

			if (DEBUG && debug) 
			    log().debug()
				.write("Backing up ")
				.write(off - nt)
				.write(" chars")
				.out();

			// Back up the input buffer to the end of the
			// lexeme so we can start the search again
			// from there.
			in.bach(off - nt);
			
			// Check if we are paused, in which case we
			// break out;
			if (m_isPaused) 
			    break main;

			if (DEBUG && debug) 
			    log().debug()
				.write("Moving from closed-token to pluripotent"
				       +" (through tabla-rasa)")
				.out();

			if (isContextual) {
			    // Now see if we need to switch contexts.
			context_switch:
			    switch (grammar.getContextAction(cp, t)) {
			    case LRTranslatorGrammar.ACTION_PEEK:
				/* do nothing */
				break context_switch;
			    case LRTranslatorGrammar.ACTION_POP:
				stack.pop();
				cp = stack.peek();
				dfa = dfas[cp];
				break context_switch;
			    case LRTranslatorGrammar.ACTION_PUSH:
				cp = grammar.getContextRegister(cp, t);
				stack.push(cp);
				dfa = dfas[cp];
				break context_switch;
			    default:
				throw new UnsupportedOperationException
				    ("Unsupported Context Action: "+
				     grammar.getContextAction(cp, t));
			    }
			}
			
			// Reset the dfa state, token ID, and
			// begin-token-marker.
			p = DFA.START_STATE;
			t = Token.UNDEF;
			off = mt = me = nt;

		    } /* end-of-token-switch */

		} else {
		    
		    // =====================================================
		    // alternative B: No, not in the dead state (a
		    // live input char).
		    // =====================================================
		    
		    // The next thing is to check the output on this
		    // state...
		    u = dfa.output(p);
		    
		    if (DEBUG && debug) 
			log().debug()
			    .write("Output for state ")
			    .write(p)
			    .write(" is ")
			    .write(u)
			    .out();
		    
		    // Test whether the input is an accepting char.
		    if (u != Token.UNDEF) {
			// If the output of this dfa state is defined,
			// then the input is an accepting char.
			// Switch on the token value to see whether we
			// are in the pluripotent, open-error, or
			// open-token states.

			if (t == Token.ERROR) {

			    if (DEBUG && debug) 
				if (mt < 0)
				    log().debug()
					.write("Moving from open-error to open-token")
					.out();
				else
				    log().debug()
					.write("Moving from phoenix-error to open-token")
					.out();

			    // If this case is selected, the lexer is
			    // in the open-error state.  Since we have
			    // now discovered that this is an
			    // accepting char, it means that the end
			    // of the error has been found and the
			    // interpreter will be notified of the
			    // error.  Also need to check if the
			    // beginning-of-token marker has been set
			    // yet.  If it has not, we are moving from
			    // open-error; if it has, from
			    // phoenix-error.
			    if (mt < 0)
				mt = off - 1;

			    if (DEBUG && debug) 
				log().debug()
				    .write("Notifying ERROR (")
				    .write(me)
				    .write(",")
				    .write(off - me)
				    .write(")")
				    .out();

			    interpreter.error(me, mt - me);
			}
			
			if (DEBUG && debug) {
			    if (t == Token.UNDEF)
				log().debug()
				    .write("Moving from pluripotent to open-token")
				    .out();
			    else
				log().debug()
				    .write("Moving from open-token to open-token")
				    .out();
			}

			// The default case cover both instances in
			// which the token value is undefined
			// (pluripotentcy), and when the token is
			// defined (open-token).  We need to update
			// the token to the current dfa output and set
			// the end-token marker.
			t = u;
			nt = off;
			
		    } else if (t == Token.ERROR && mt < 0) {
			if (DEBUG && debug) 
			    log().debug()
				.write("Moving from open-error to pheonix-error")
				.out();

			// If we are here then the input is not an
			// accepting char, but it is a live char.
			// Thus, we need to test if the current token
			// value is ERROR, which implies that the
			// lexer is in the open-error state.  If it is
			// in the open-error state and the
			// begin-token-marker has not been set, then
			// we need to set it (the pheonix state).
			mt = off - 1;
		    }
		} /* end-if-dead-state */

	    } catch (EOFException eofex) {
		
		
		// The EOF exception has very similar effects to that of
		// traversing over a dead char.
	    token_switch:
		switch (t) {
		case Token.UNDEF:
		    // If the token is undefined, two things may have
		    // happened: (1) we traversed over the rest of the
		    // input in the pluripotent state, and nothing was
		    // ever even remotely matched. (2) The last char
		    // triggered a match, and thus we are left in a
		    // pluripotent state right before the EOF char.
		    
		    /* fall-through */
		    
		case Token.ERROR:
		    if (DEBUG && debug) 
			log().debug()
			    .write("Moving from pluripotent to closed-error "+
				   "(through open-error by EOF)")
			    .out();

		    // If we were in the open-error state, this means that
		    // we close it now.  However, in the special case
		    // where there was no input at all, don't call an
		    // error (actually, this can only occur if the Token
		    // is UNDEFINED and we're here due to the case
		    // fallthough).
		    if (me != off - 1) {
			if (DEBUG && debug) 
			    log().debug()
				.write("Notifying ERROR (")
				.write(me)
				.write(",")
				.write(off - me)
				.write(")")
				.out();

			interpreter.error(me, off - me);
		    }

		    break main;
		    
		default:
		    if (DEBUG && debug) 
			log().debug()
			    .write("Moving from open-token to closed-token")
			    .out();
		    
		    // The default case is a little different here.  If
		    // the lexer is in the open-token state, the
		    // EOFexception has the effect of closing it.  But if
		    // there are any characters between the end of the
		    // token and the end of the input, that's an error, so
		    // we need to notify that too.
		    if (DEBUG && debug) 
			log().debug()
			    .write("Notifying ")
			    .write(grammar.getTerminal(t))
			    .write(" (")
			    .write(mt)
			    .write(",")
			    .write(nt - mt)
			    .write(")")
			    .out();

		    interpreter.match(t, mt, nt - mt);
		    
		    if (nt != off - 1) {
			
		    possibility_for_main_loop_reentry:
			if (isContextual) {

			context_switch:
			    switch (grammar.getContextAction(cp, t)) {
			    case LRTranslatorGrammar.ACTION_PEEK:
				break possibility_for_main_loop_reentry;

			    case LRTranslatorGrammar.ACTION_POP:
				stack.pop();
				cp = stack.peek();
				dfa = dfas[cp];
				break context_switch;
				
			    case LRTranslatorGrammar.ACTION_PUSH:
				cp = grammar.getContextRegister(cp, t);
				stack.push(cp);
				dfa = dfas[cp];
				break context_switch;
				
			    default:
				throw new UnsupportedOperationException
				    ("Unsupported Context Action: "+
				     grammar.getContextAction(cp, t));
			    }
			    
			    // Back up the input buffer to the end of the
			    // lexeme so we can start the search again
			    // from there.
			    if (DEBUG && debug) 
				log().debug()
				    .write("Backing up ")
				    .write(off - nt)
				    .write(" chars")
				    .out();

			    in.bach(off - nt);
			    
			    // Check if we are paused, in which case we
			    // break out;
			    if (m_isPaused) 
				break main;
			    
			    // Reset the dfa state, token ID, and
			    // begin-token-marker.
			    p = DFA.START_STATE;
			    t = Token.UNDEF;
			    mt = me = nt;

			    continue main;
			} 
			
			if (DEBUG && debug) 
			    log().debug()
				.write("Notifying ERROR (")
				.write(nt)
				.write(",")
				.write(off - nt)
				.write(")")
				.out();

			interpreter.error(nt, off - nt);
		    }
		}
		
		// Finally, send the STOP token.
		if (DEBUG && debug) 
		    log().debug()
			.write("Notifying STOP")
			.out();

		interpreter.stop();

		// DONE!
		break;
		
	    } catch (IOException ioex) {
		
		System.out.println(ioex.getMessage());
		break;
	    }

	} /* end-of-while-loop */
    }


    public void reset()
    {
	super.reset();

	this.debug = "true".equals(p.getProperty("run-lexer-debug"));
    }

    public void stop()
    {
    	m_isPaused = true;
    }

    public int getCurrentContext()
    {
	return m_context;
    }

    public IntStack getContextStack()
    {
	return stack;
    }

    public LexerInterpreter getLexerInterpreter()
    {
    	return interpreter;
    }

    public void setLexerInterpreter(LexerInterpreter interpreter)
    {
    	this.interpreter = interpreter;
    }

    private Log log()
    {
	if (log == null)
	    log = Mission.control().log("lex", this);
	return log;
    }
    
    private DFA[] dfas;
    private LexerInterpreter interpreter;
    private ArrayIntStack stack;
    private int m_context;
    private boolean m_isPaused;
    private Log log;

    private boolean debug;
}
