/**
 * $Id: Session.java,v 1.1.1.1 2001/07/06 09:08:05 pcj Exp $
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

/**
 * The <code>Session</code> interface acts a central repository for
 * properties as well as a logging access.  
 */
public interface Session 
{
    /**
     * Returns the <code>Object</code> under the given key or
     * <code>null</code> if no such key exists.  
     */
    Object get(String key);

    /**
     * Stores the given value under the given key. 
     */
    void put(String key, Object value);

    /**
     * Removes the key/value pair and returns the value or null if no
     * such key exists.
     */
    Object remove(String key);

    /**
     * Returns the <code>String</code> under the given key or
     * <code>null</code> if no such key exists.  
     */
    String getString(String key);

    /**
     * Returns the <code>String</code> under the given key or
     * the given default if no such key exists.  
     */
    String getString(String key, String defaultValue);

    /**
     * Returns the <code>Object</code> under the given key. If no such
     * key exists, an <code>IllegalArgumentException</code> will be
     * thrown.  
     */
    int getInt(String key);

    /**
     * Returns the <code>Object</code> under the given key. If no such
     * key exists, the given <code>default</code> will be returned.
     */
    int getInt(String key, int def);

    /**
     * Returns the <code>boolean</code> under the given key. If no such
     * key exists, an <code>IllegalArgumentException</code> will be
     * thrown.  
     */
    boolean getBoolean(String key);

    /**
     * Returns the <code>boolean</code> under the given key. If no such
     * key exists, the given default will be returned.
     */
    boolean getBoolean(String key, boolean def);

    /**
     * Returns true if an entry exists for the given key, false
     * otherwise.
     */
    boolean contains(String key);

    /**
     * Returns <code>true</code> if an extry exists for the given key
     * and the value is either a <code>boolean</code> having value
     * <code>true</code>, the string "true", or the string "yes".  If
     * no entry exists for the given key, false is returned.
     */
    boolean isTrue(String key);

    /**
     * Returns <code>true</code> if an extry exists for the given key
     * and the value is either a <code>boolean</code> having value
     * <code>false</code>, the string "false", or the string "no".  If
     * no entry exists for the given key, false is returned.
     */
    boolean isFalse(String key);

    /**
     * Returns <code>true</code> if no entry exists for the given key,
     * the value is either a <code>boolean</code> having value
     * <code>false</code>, or any string other than "true" or "yes".
     */
    boolean isNotTrue(String key);

    /**
     * Returns <code>true</code> if no entry exists for the given key,
     * the value is either a <code>boolean</code> having value
     * <code>true</code>, or any string other than "false" or "no".
     */
    boolean isNotFalse(String key);

    /**
     * Registers the object with the control such that it may emit
     * messages through the log channels.
     */
    Log log(String name, Object src);

    /**
     * To be called at the end of the session.
     */
    void close();
}


