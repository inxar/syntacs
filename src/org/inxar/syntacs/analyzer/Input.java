/**
 * $Id: Input.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.io.*;

/**
 * The <code>Input</code> interface is responsible for presenting a
 * raw character stream to the <code>Lexer</code> in a way that makes
 * the lexing process easier.  Since all the methods in this interface
 * deal with character handling, they all end in "ch".  This was not
 * the case originally --- only <code>getch()</code> was named with
 * the ch-suffix.  But I thought <code>fetch()</code>,
 * <code>retch()</code> and <code>strech()</code> were so exceedingly
 * clever that I converted the rest of the names to the
 * ch-suffix-convention.
 */
public interface Input {
  /**
   * Initialize the Input source to the given <code>Object</code>.
   */
  void initch(Object src) throws IOException;

  /**
   * Returns the forthcoming <code>char</code> without advancing the
   * buffer (like a `peek').  If the end of the buffer has been
   * reached, <code>-1</code> is returned.
   */
  int broach();

  /**
   * "Get char": Gets the next <code>char</code> and advances the
   * offset by one.  If the end of input symbol has already been
   * returned and getch() is called again, <code>EOFException</code>
   * is thrown.  Underlying <code>IOExceptions</code> that may occur
   * are delegated to the caller.
   */
  int getch() throws EOFException, IOException;

  /**
   * "backup chars": Moves the input "back" the given number of
   * characters.  For example, if the state of the input over the
   * sequence 'abc' is after position 2 (0,1,2; therefore directly
   * after 'c'), <code>ungetch(3)</code> would move the input
   * pointer back three characters and therefore to the beginning
   * (just before 'a').
   */
  void bach(int len);

  /**
   * "Return chars": Gets the region from offset to length as a new
   * character array.  Retch stands for `return characters', but
   * also invokes the vulgar sense of the word by `throwing up' new
   * arrays.
   *
   * @throws <code>ArrayIndexOutOfBoundsException</code> if the
   * region ifs not valid.
   */
  char[] retch(int offset, int length);

  /**
   * "Return char": get the the <code>char</code> at the given
   * offset.
   */
  char retch(int offset);

  /**
   * "Return chars as String": fetches the stretch of characters
   * from offset to length as a new string.
   *
   * @throws <code>ArrayIndexOutOfBoundsException</code> if the
   * region ifs not valid.
   */
  String stretch(int offset, int length);

  /**
   * "Fetch chars": Copies the region from offset with given length
   * into the dst array provided starting at the given destination
   * offset.
   *
   * @throws <code>ArrayIndexOutOfBoundsException</code> if the
   * region ifs not valid.
   */
  void fetch(int offset, int length, char[] dst, int dstoff);

  /**
   * "Patch": change the given region starting at the given offset.
   * This is used only in lexical error correction.
   */
  void patch(int offset, char src[], int srcoff, int length);

  /**
   * "At char" (no argument): Returns the current absolute position
   * of the input (the <i>offset</i>), as though an index into an
   * array.
   */
  int atch();

  /**
   * "At char (with argument)": Sets the input position to the given
   * argument.
   */
  void atch(int offset);

  /**
   * "At line": Returns the current line number.
   */
  int atln();
}
