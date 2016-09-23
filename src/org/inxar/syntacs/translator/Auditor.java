/**
 * $Id: Auditor.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

import org.inxar.syntacs.analyzer.*;
import java.util.*;

/**
 * <code>Auditor</code> is a "global" repository for errors and
 * warnings; a <i>listener</i> for complaints.  If a translation
 * component discovers an error or wishes to report a warning, it may
 * do so through one of the <code>notify()</code> methods in this
 * interface.  The use of the word "audit" here specifically refers to
 * <i>one that hears or listens</i>; it's not related to taxation
 * <code>:)</code>
 */
public interface Auditor {
  /**
   * Gets the name of the input source.
   */
  String getSource();

  /**
   * Sets the name of the input source.  This is useful in order to
   * make more meaningful error messages.
   */
  void setSource(String src);

  /**
   * Gets the <code>Properties</code> instance.
   */
  Properties getProperties();

  /**
   * Sets the <code>Properties</code> instance.
   */
  void setProperties(Properties p);

  /**
   * Returns <code>true</code> if there are no errors or warnings,
   * <code>false</code> if there is at least one error or at least
   * one warning.
   */
  boolean isEmpty();

  /**
   * Returns the current number of complaints (<code>errors() +
   * warnings()</code>).
   */
  int complaints();

  /**
   * Returns the current number of errors.
   */
  int errors();

  /**
   * Returns the current number of warnings.
   */
  int warnings();

  /**
   * Returns <code>true</code> if there is at least one error,
   * <code>false</code> otherwise.
   */
  boolean hasErrors();

  /**
   * Returns <code>true</code> if there is at least one warning,
   * <code>false</code> otherwise.
   */
  boolean hasWarnings();

  /**
   * Returns an unmodifiable list of errors, where each member in
   * the <code>List</code> is an <code>Complaint</code>.
   */
  List getErrors();

  /**
   * Returns an unmodifiable list of warnings, where each member in
   * the <code>List</code> is an <code>Complaint</code>.
   */
  List getWarnings();

  /**
   * Adds the given <code>Complaint</code> to the end of the the
   * internal <code>List</code> of complaints and returns the same
   * object to the caller.
   */
  Complaint notify(Complaint complaint);

  /**
   * Creates a new <code>Complaint</code> of the given type having
   * the given message and given line number.  The
   * <code>Complaint</code> is appended to the internal
   * <code>List</code> of complaints and also returned to the
   * caller.
   */
  Complaint notify(int type, int line, String msg);

  /**
   * Creates a new <code>Complaint</code> of the given type having
   * the given message.  The <code>Complaint</code> is appended to
   * the internal <code>List</code> of complaints and also returned
   * to the caller.
   */
  Complaint notify(int type, String msg);

  /**
   * Creates a new <code>Complaint</code> of the given type having
   * the given message at the given <code>Input</code> offset with
   * the given length.  The <code>Complaint</code> is appended to
   * the internal <code>List</code> of complaints and also returned
   * to the caller.  This method is generally used for syntactic
   * errors.
   */
  Complaint notify(int type, String msg, Input in, int offset, int length);
}
