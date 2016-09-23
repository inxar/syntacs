/**
 * $Id: Complaint.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.translator;

import java.io.*;

/**
 * <code>Complaint</code> is a general name for a semantic/syntactic
 * warnings and errors.
 */
public interface Complaint {
  /**
   * Type constant for a semantic error.  Semantic errors represent
   * logical problems which are generally unrecoverable.
   */
  int SEMANTIC_ERROR = 1;

  /**
   * Type constant for a semantic warning.  Semantic warnings represent
   * logical issues which are generally recoverable.
   */
  int SEMANTIC_WARNING = 2;

  /**
   * Type constant for a lexical error.
   */
  int LEXICAL_ERROR = 3;

  /**
   * Type constant for a syntactic error.
   */
  int SYNTACTIC_ERROR = 4;

  /**
   * Type constant for a nonspecific error.
   */
  int UNSPECIFIED_ERROR = 5;

  /**
   * Returns the type of this <code>Complaint</code> as one of the
   * constants in this interface.
   */
  int getType();

  /**
   * Returns the name of the source file/URI/whatever or
   * <code>null</code> if it is not known.
   */
  String getSource();

  /**
   * Returns the line number of the error or <code>-1</code> if it
   * is not known.  The first line is <code>1</code>, there is no
   * line zero.
   */
  int getLineNumber();

  /**
   * Returns a message describing this complaint.
   */
  String getMessage();

  /**
   * Prints a trace of the <code>Complaint</code> to
   * <code>System.out</code>.  The <code>printMessage()</code> and
   * <code>getMessage()</code> operations are different in that
   * <code>printMessage()</code> will synthesize a formatted output
   * that may include all the other data (source URI, line number,
   * etc...) whereas <code>getMessage()</code> will only return the
   * message string.  The contract is analogous to the
   * <code>printStackTrace()</code> and <code>getMessage()</code>
   * methods in the <code>Throwable</code> heirarchy.
   */
  void printMessage();

  /**
   * Identical to <code>printMessage()</code> with the exception
   * that output is written to the given <code>PrintStream</code>
   * rather than <code>System.out</code>.
   */
  void printMessage(PrintStream out);
}
