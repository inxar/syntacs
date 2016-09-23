/**
 * $Id: StringTools.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.util;

import java.io.*;
import java.util.*;

/**
 * A collection of utilities for working with text and strings.
 */
public class StringTools {
  private StringTools() {}

  /**
   * A Central location to stash the newline String.
   */
  public static final String NEWLINE = System.getProperty("line.separator");

  public static boolean isDefined(String s) {
    return s != null && s.length() > 0;
  }

  public static String capitalize(String s) {
    char[] chars = s.toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new String(chars);
  }

  public static String escape(String s) {
    int len = s.length();
    StringBuffer b = new StringBuffer(len + 3);

    char c;
    for (int i = 0; i < len; i++) {
      c = s.charAt(i);
      switch (c) {
        case '\t':
          b.append("\\t");
          break;
        case '\n':
          b.append("\\n");
          break;
        case '\r':
          b.append("\\r");
          break;
        case '\f':
          b.append("\\f");
          break;
        default:
          b.append(c);
      }
    }
    return b.toString();
  }

  public static String substr(String s, int length) {
    return escape(s.substring(0, Math.min(length, s.length())));
  }

  public static String replace(String src, String out, String in) {
    //System.out.println("StringTools.replace(): replacing "+src);

    int srclen = src.length(), outlen = out.length(), inlen = in.length();

    int offset = 0;
    while (true) {

      int index = src.indexOf(out, offset);

      if (index < 0) break;

      //  	    String pre = src.substring(0, index);
      //  	    String post = src.substring(index + outlen);
      //  	    src = pre + in + post;
      //  	    System.out.println("StringTools.replace(): ["+pre+"]["+in+"]["+post+"]");
      //  	    System.out.println("StringTools.replace(): src is now: " + src);

      src = src.substring(0, index) + in + src.substring(index + outlen);
      offset += index + inlen;
    }

    //System.out.println("StringTools.replace(): returning "+src);
    return src;
  }

  public static int lengthOfUTF(String s, int off, int maxsz) {
    int strlen = s.length(); // the length of the input string
    int utflen = 0; // the number of bytes in utf buffer
    int sublen = 0; // the result (number of chars in substring)
    char c;

    while (off < strlen) {

      // Okay, there is at least room for one more byte, but not
      // necessarily one more char since this char may require
      // more than one byte.  Grab the next character.
      c = s.charAt(off);

      if ((c >= 0x0001) && (c <= 0x007F)) utflen++;
      else if (c > 0x07FF) utflen += 3;
      else utflen += 2;

      // If the current utflen is equal or greater than the
      // maximum allowed buffer size, we need to stop now since
      // the addition of this character would certainly place it
      // over the limit.
      if (utflen >= maxsz) return sublen;

      // Increment both the length meter and the offset into the
      // string.
      sublen++;
      off++;
    }

    // End of string hit.
    return sublen;
  }

  public static int getUTFLength(String s) {
    int strlen = s.length();
    int utflen = 0;
    char c;

    for (int i = 0; i < strlen; i++) {
      c = s.charAt(i);
      if ((c >= 0x0001) && (c <= 0x007F)) utflen++;
      else if (c > 0x07FF) utflen += 3;
      else utflen += 2;
    }

    return utflen;
  }

  private static boolean getTrue(String key, Properties p) {
    String val = p.getProperty(key);
    if (val == null) return false;

    val = val.toLowerCase().intern();

    return "true" == val || "yes" == val || "yep" == val;
  }

  private static boolean getFalse(String key, Properties p) {
    String val = p.getProperty(key);
    if (val == null) return false;

    val = val.toLowerCase().intern();

    return "false" == val || "no" == val || "nope" == val;
  }

  public static boolean isTrue(String key, Properties p) {
    return getTrue(key, p);
  }

  public static boolean isFalse(String key, Properties p) {
    return getFalse(key, p);
  }

  public static boolean isNotTrue(String key, Properties p) {
    return !getTrue(key, p);
  }

  public static boolean isNotFalse(String key, Properties p) {
    return !getFalse(key, p);
  }

  public static String getPath(String sourcepath, String namespace) {
    if (sourcepath.charAt(sourcepath.length() - 1) != File.separatorChar)
      sourcepath += File.separatorChar;

    if (namespace != null) {
      return sourcepath + namespace.replace('.', File.separatorChar) + File.separatorChar;
    } else return sourcepath;
  }
}
