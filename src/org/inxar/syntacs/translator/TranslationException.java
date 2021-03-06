/**
 * $Id: TranslationException.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
import java.util.*;

/**
 * <code>TranslationException</code> is a carrier for errors
 * discovered during a translation run.
 */
public class TranslationException extends IOException {
  /**
   * Constructs a <code>TranslationException</code> with the given
   * <code>Auditor</code>.
   */
  public TranslationException(Auditor auditor) {
    super();
    this.auditor = auditor;
    this.nested = null;
  }

  /**
   * Constructs a <code>TranslationException</code> with the given
   * message and <code>Auditor</code>.
   */
  public TranslationException(Auditor auditor, String msg) {
    super(msg);
    this.auditor = auditor;
    this.nested = null;
  }

  /**
   * Constructs a <code>TranslationException</code> with the given
   * nested Throwable and <code>Auditor</code>.
   */
  public TranslationException(Auditor auditor, Throwable nested) {
    super();
    this.auditor = auditor;
    this.nested = nested;
  }

  /**
   * Constructs a <code>TranslationException</code> with the given
   * nested Throwable and <code>Auditor</code>.
   */
  public TranslationException(Auditor auditor, String msg, Throwable nested) {
    super(msg);
    this.auditor = auditor;
    this.nested = nested;
  }

  public Auditor getAuditor() {
    return auditor;
  }

  public void printStackTrace() {
    printStackTrace(System.out);
  }

  public void printStackTrace(PrintStream out) {
    super.printStackTrace(out);

    if (nested != null) {
      out.println("Nested Exception:");
      nested.printStackTrace(out);
    }

    if (!auditor.isEmpty()) out.println(auditor.toString());
  }

  public void printStackTrace(PrintWriter out) {
    super.printStackTrace(out);

    if (nested != null) {
      out.println("Nested Exception:");
      nested.printStackTrace(out);
    }

    if (!auditor.isEmpty()) out.println(auditor.toString());
  }

  private final Auditor auditor;
  private final Throwable nested;
}
