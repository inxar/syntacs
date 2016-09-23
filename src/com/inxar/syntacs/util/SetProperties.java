/**
 * $Id: SetProperties.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
 * The <code>SetProperties</code> class is a subclass of
 * <code>java.util.Properties</code> which interprets multiple entries
 * having the same key as an array.
 */
public class SetProperties extends java.util.Properties {
  public SetProperties() {
    super();
  }

  public SetProperties(String file) throws IOException {
    super();

    BufferedInputStream in = null;

    try {
      in = new BufferedInputStream(new FileInputStream(file));

      load(in);

      in.close();
      in = null;

    } catch (IOException ioex) {
      if (in != null)
        try {
          in.close();
        } catch (Exception ex) {
        }
      throw ioex;
    }
  }

  public SetProperties(SetProperties defaults) {
    super();
    this.defaultSetProperties = defaults;
  }

  public SetProperties(Properties defaults) {
    super(defaults);
  }

  public synchronized String getProperty(String key) {
    // first grab the object, which we expect to be a vector
    Object oval = super.get(key);

    // safe cast to a vector or null
    Vector v = (oval instanceof Vector) ? (Vector) oval : null;

    // if vector exists, has at least one element, and that
    // element is not null or empty, return it.  OTW, keep going
    if (v != null && v.size() > 0) {
      String sval = (String) v.elementAt(0);
      if (sval != null && sval.length() > 0) return sval;
    }

    // check the other stores and return from there if it exists
    if (defaultSetProperties != null) {
      return defaultSetProperties.getProperty(key);
    } else if (defaults != null) {
      return defaults.getProperty(key);
    }

    // no luck
    return null;
  }

  public synchronized String[] getProperties(String key) {
    // first grab the object, which we expect to be a vector
    Object oval = super.get(key);

    // safe cast to a vector or null
    Vector v = (oval instanceof Vector) ? (Vector) oval : null;

    // if vector exists, has at least one element, and that
    // element is not null or empty, return it.  OTW, keep going
    if (v != null) {
      int len = v.size();
      String[] as = new String[len];
      for (int i = 0; i < len; i++) as[i] = (String) v.elementAt(i);

      return as;
    }

    // check the other stores and return from there if it exists
    if (defaultSetProperties != null) {
      return defaultSetProperties.getProperties(key);
    } else if (defaults != null) {
      String s = defaults.getProperty(key);
      return new String[] {s};
    }

    // no luck
    return new String[0];
  }

  public synchronized String[] getProperties(String key, String[] defaultValues) {
    String[] vals = getProperties(key);
    return (vals == null || vals.length == 0) ? defaultValues : vals;
  }

  public synchronized void list(PrintStream out) {
    // pring a notice
    out.println("-- listing properties --");

    // make a new container...
    Hashtable h = new Hashtable();

    // ...and fill it
    enumerate(h);

    // enumerate the keys of this new hash
    for (Enumeration e = h.keys(); e.hasMoreElements(); ) {

      // get each key
      String key = (String) e.nextElement();

      // and get the vector under this key
      Vector v = (Vector) h.get(key);

      // print if vector exists
      if (v != null) {

        // enumerate the values of the vector
        for (Enumeration f = v.elements(); f.hasMoreElements(); ) {

          // get each value
          String val = (String) f.nextElement();

          // trunc the line if necessary
          if (val.length() > 40) {
            val = val.substring(0, 37) + "...";
          }

          // and print it
          out.println(key + "=" + val);
        }
      }
    }
  }

  public synchronized void list(PrintWriter out) {
    // pring a notice
    out.println("-- listing properties --");

    // make a new container...
    Hashtable h = new Hashtable();

    // ...and fill it
    enumerate(h);

    // enumerate the keys of this new hash
    for (Enumeration e = h.keys(); e.hasMoreElements(); ) {

      // get each key
      String key = (String) e.nextElement();

      // and get the vector under this key
      Vector v = (Vector) h.get(key);

      // print if vector exists
      if (v != null) {

        // enumerate the values of the vector
        for (Enumeration f = v.elements(); f.hasMoreElements(); ) {

          // get each value
          String val = (String) f.nextElement();

          // trunc the line if necessary
          if (val.length() > 40) {
            val = val.substring(0, 37) + "...";
          }

          // and print it
          out.println(key + "=" + val);
        }
      }
    }
  }

  public synchronized Enumeration propertyNames() {
    Hashtable h = new Hashtable();
    enumerate(h);
    return h.keys();
  }

  /**
   * Enumerates all key/value pairs in the specified hashtable.
   * @param h the hashtable
   */
  private synchronized void enumerate(Hashtable h) {
    // enumerate the default multikeys if is exists
    if (defaultSetProperties != null) {
      defaultSetProperties.enumerate(h);

      // enumerate the defaults if is exists
    } else if (defaults != null) {

      // get all the property names
      for (Enumeration e = defaults.propertyNames(); e.hasMoreElements(); ) {

        // get the key from the enum
        String key = (String) e.nextElement();

        // pull the vector out from this hash
        Vector v = (Vector) h.get(key);

        // make a new hash and store it if needed
        if (v == null) {
          v = new Vector(1);
          h.put(key, v);
        }

        // add this value to the hash from the defaults
        v.addElement(defaults.getProperty(key));
      }
    }

    for (Enumeration e = keys(); e.hasMoreElements(); ) {

      // get the key from the enum
      String key = (String) e.nextElement();

      // pull the vector out from this hash
      Vector v = (Vector) h.get(key);

      // make a new hash and store it if needed
      if (v == null) {
        v = new Vector(1);
        h.put(key, v);
      }

      for (Enumeration f = v.elements(); f.hasMoreElements(); ) {

        // get the key from the enum
        String val = (String) f.nextElement();

        // add this value to the hash from the defaults
        v.addElement(val);
      }
    }
  }

  public synchronized Object setProperties(String key, String[] values) {
    if (values != null) {
      Vector v = new Vector(values.length);
      for (int i = 0; i < values.length; i++) v.addElement(values[i]);
      return put(key, v);
    }
    return null;
  }

  public synchronized void load(InputStream inStream) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(inStream, "8859_1"));
    while (true) {
      // Get next line
      String line = in.readLine();

      // abort when no more lines
      if (line == null) return;

      // make sure the line has something
      if (line.length() > 0) {

        // get the first char and test it
        char firstChar = line.charAt(0);

        // Continue lines that end in slashes if they are not comments
        if ((firstChar != '#') && (firstChar != '!')) {

          // keep parsing the current line until no more ie
          // expand lines that are continued into a single
          // line
          while (continueLine(line)) {

            // get the next line
            String nextLine = in.readLine();

            // assign the empty string if the line is null
            // (at the end of the file)
            if (nextLine == null) nextLine = new String("");

            // snip the part of the line without the
            // trailing slash
            String loppedLine = line.substring(0, line.length() - 1);

            // Advance beyond whitespace on new line
            int startIndex = 0;
            for (startIndex = 0; startIndex < nextLine.length(); startIndex++)
              if (whiteSpaceChars.indexOf(nextLine.charAt(startIndex)) == -1) break;

            // cut interesting part out
            nextLine = nextLine.substring(startIndex, nextLine.length());

            // and join them
            line = new String(loppedLine + nextLine);
          }

          // Find start of key
          int len = line.length();
          int keyStart;
          for (keyStart = 0; keyStart < len; keyStart++) {
            if (whiteSpaceChars.indexOf(line.charAt(keyStart)) == -1) break;
          }

          // Find separation between key and value
          int separatorIndex;
          for (separatorIndex = keyStart; separatorIndex < len; separatorIndex++) {
            char currentChar = line.charAt(separatorIndex);
            if (currentChar == '\\') separatorIndex++;
            else if (keyValueSeparators.indexOf(currentChar) != -1) break;
          }

          // Skip over whitespace after key if any
          int valueIndex;
          for (valueIndex = separatorIndex; valueIndex < len; valueIndex++)
            if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1) break;

          // Skip over one non whitespace key value
          // separators if any
          if (valueIndex < len)
            if (strictKeyValueSeparators.indexOf(line.charAt(valueIndex)) != -1) valueIndex++;

          // Skip over white space after other separators if
          // any
          while (valueIndex < len) {
            if (whiteSpaceChars.indexOf(line.charAt(valueIndex)) == -1) break;
            valueIndex++;
          }
          String key = line.substring(keyStart, separatorIndex);
          String value = (separatorIndex < len) ? line.substring(valueIndex, len) : "";

          // Convert then store key and value
          key = loadConvert(key);
          value = loadConvert(value);

          // now put the key and value in the hashtable.  We
          // first have to retrieve the vector at that key
          Vector v = (Vector) get(key);

          // if it is null, we have to make a new one and
          // store it under the key
          if (v == null) {
            v = new Vector(1);
            put(key, v);
          }

          // and append this value to the key
          v.addElement(value);
        }
      }
    }
  }

  /*
   * Returns true if the given line is a line that must be appended
   * to the next line
   */
  private boolean continueLine(String line) {
    int slashCount = 0;
    int index = line.length() - 1;
    while ((index >= 0) && (line.charAt(index--) == '\\')) slashCount++;
    return (slashCount % 2 == 1);
  }

  /*
   * Converts encoded \\uxxxx to unicode chars and changes special
   * saved chars to their original forms
   */
  private String loadConvert(String theString) {
    char aChar;
    int len = theString.length();
    StringBuffer outBuffer = new StringBuffer(len);

    for (int x = 0; x < len; ) {
      aChar = theString.charAt(x++);
      if (aChar == '\\') {
        aChar = theString.charAt(x++);
        if (aChar == 'u') {
          // Read the xxxx
          int value = 0;
          for (int i = 0; i < 4; i++) {
            aChar = theString.charAt(x++);
            switch (aChar) {
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
                value = (value << 4) + aChar - '0';
                break;
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
                value = (value << 4) + 10 + aChar - 'a';
                break;
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
                value = (value << 4) + 10 + aChar - 'A';
                break;
              default:
                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            }
          }
          outBuffer.append((char) value);
        } else {
          if (aChar == 't') aChar = '\t';
          else if (aChar == 'r') aChar = '\r';
          else if (aChar == 'n') aChar = '\n';
          else if (aChar == 'f') aChar = '\f';
          outBuffer.append(aChar);
        }
      } else outBuffer.append(aChar);
    }
    return outBuffer.toString();
  }

  /*
   * Converts unicodes to encoded \\uxxxx and writes out any of the
   * characters in specialSaveChars with a preceding slash
   */
  private String saveConvert(String theString) {
    char aChar;
    int len = theString.length();
    StringBuffer outBuffer = new StringBuffer(len * 2);

    for (int x = 0; x < len; ) {
      aChar = theString.charAt(x++);
      switch (aChar) {
        case '\\':
          outBuffer.append('\\');
          outBuffer.append('\\');
          continue;
        case '\t':
          outBuffer.append('\\');
          outBuffer.append('t');
          continue;
        case '\n':
          outBuffer.append('\\');
          outBuffer.append('n');
          continue;
        case '\r':
          outBuffer.append('\\');
          outBuffer.append('r');
          continue;
        case '\f':
          outBuffer.append('\\');
          outBuffer.append('f');
          continue;
        default:
          if ((aChar < 20) || (aChar > 127)) {
            outBuffer.append('\\');
            outBuffer.append('u');
            outBuffer.append(toHex((aChar >> 12) & 0xF));
            outBuffer.append(toHex((aChar >> 8) & 0xF));
            outBuffer.append(toHex((aChar >> 4) & 0xF));
            outBuffer.append(toHex((aChar >> 0) & 0xF));
          } else {
            if (specialSaveChars.indexOf(aChar) != -1) outBuffer.append('\\');
            outBuffer.append(aChar);
          }
      }
    }
    return outBuffer.toString();
  }

  public synchronized void store(OutputStream out, String header) throws IOException {
    BufferedWriter awriter;

    // make the writer
    awriter = new BufferedWriter(new OutputStreamWriter(out, "8859_1"));

    // print the header if it exists
    if (header != null) writeln(awriter, "#" + header);

    // print the date
    writeln(awriter, "#" + new Date().toString());

    // now print each entry
    for (Enumeration e = keys(); e.hasMoreElements(); ) {
      String key = (String) e.nextElement();
      Vector vals = (Vector) get(key);
      key = saveConvert(key);

      // print each member of that entry
      for (Enumeration f = vals.elements(); f.hasMoreElements(); ) {
        String val = (String) f.nextElement();
        val = saveConvert(val);
        writeln(awriter, key + "=" + val);
      }
    }
    awriter.flush();
  }

  private static void writeln(BufferedWriter bw, String s) throws IOException {
    bw.write(s);
    bw.newLine();
  }

  /**
   * Convert a nibble to a hex character
   * @param	nibble	the nibble to convert.
   */
  private static char toHex(int nibble) {
    return hexDigit[(nibble & 0xF)];
  }

  /** A table of hex digits */
  private static final char[] hexDigit = {
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };

  private static final String keyValueSeparators = "=: \t\r\n\f";
  private static final String strictKeyValueSeparators = "=:";
  private static final String specialSaveChars = "=: \t\r\n\f#!";
  private static final String whiteSpaceChars = " \t\r\n\f";

  protected SetProperties defaultSetProperties;
}
