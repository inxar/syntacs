/**
 * $Id: Burner.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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
package org.inxar.syntacs.util;

import org.inxar.jenesis.*;

/**
 * A <code>Burner</code> is some entity which is capable of creating a
 * class using the <a href="http://www.inxar.org/jenesis">Jenesis
 * API</a>; it "burns" the state of some object into a
 * <code>ClassDeclaration</code> (not unlike "burning" a
 * <code>CD-Recordable</code> disc).
 */
public interface Burner
{
  /**
   * Modifies the given empty <code>ClassDeclaration</code> such
   * that the class is "burned".
   */
  void burn(Object src, ClassDeclaration cls);
}
