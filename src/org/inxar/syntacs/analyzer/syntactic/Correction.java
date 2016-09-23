/**
 * $Id: Correction.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package org.inxar.syntacs.analyzer.syntactic;

/**
 * A <code>Correction</code> is a discrete instruction for a
 * <code>Parser</code> to execute (apply) during an error recovery.
 */
public interface Correction {
  /**
   * The "No Operation" correction instruction.  The semantics of a NOOP is to
   * do nothing (the error is ignored).
   */
  int NOOP = 0;

  //      int INSERT = 1;
  //      int DELETE = 2;
  //      int SUBSTITUTE = 3;
  //      int REDUCE = 4;
  //      int ACCEPT = 5;

  /**
   * The "Wait" correction instruction.  The semantics of
   * <code>WAIT</code> is closely tied to the notion of
   * "synchonizing tokens"; when the Parser sees a <code>WAIT</code>
   * instruction, it calls <code>Correction.getValue()</code> and
   * expects an <code>Integer</code>.  The value of this
   * <code>Integer</code> should correspond to a terminal
   * <code>Symbol</code>.  The <code>Parser</code> will then discard
   * incoming terminals from the <code>LexerInterpreter</code> until
   * it sees one that whose type matches the given
   * <code>Integer</code>.  When that happens, the
   * <code>Parser</code> will advance to the next
   * <code>Correction</code> in the scheduled
   * <code>Recovery</code>.
   *
   * For example, if the symbol type for a semicolon in some grammar
   * is 12, then <code>WAIT 12</code> means <i>discard all input
   * until you see a semicolon; when you do, go ahead to the next
   * correction in the recovery plan.</i> In this hypothetical
   * grammar then, the semicolon is a synchronizing token.
   */
  int WAIT = 6;

  //      int UNSHIFT = 7;
  //      int EXPAND = 8;

  /**
   * The "Tumble" correction instruction.  Tumbling is a recovery
   * state such that the Parser iteratively challenges
   * <code>DPA.action()</code> method with the current input token
   * and the current state on the top of the state stack until it
   * returns a non-error <code>Action</code> from the
   * <code>DPA</code>.  After each unsuccessful challenge, the state
   * stack is popped.  <P>This has the effect of backing up through
   * the state graph until one can move forward again.  It is called
   * "tumble" because it hybridizes the is loose notion of finding
   * the right configuration in a lock (finding the right input),
   * and the notion of falling (the depth of the stack).  <P> The
   * "Wait and Tumble" is a useful recovery plan.
   */
  int TUMBLE = 9;

  /**
   * The "Abort" correction instruction causes the
   * <code>Parser</code> to immediately bail by throwing a
   * <code>TranslationException</code>.
   */
  int ABORT = 10;

  // int BURKE_FISHER = 11;
  // int PENELLO_DEREMER = 12;

  /**
   * Returns the type of this <code>Correction</code> as one of the
   * constants in this interface.
   */
  int getType();

  /**
   * Returns some value associated with this Correction.  The
   * specifics of what this object should be (if anything) varies
   * according to the type of correction.
   */
  Object getValue();
}
