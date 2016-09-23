/**
 * $Id: Dot.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import java.util.List;
import java.util.LinkedList;
import org.inxar.syntacs.util.GraphViz;

/**
 * Basic implementation of the <code>GraphViz</code> interface.
 */
public class Dot implements GraphViz {
  public Dot(String name) {
    this.name = name;
    this.entries = new LinkedList();
    this.isDirected = true;
  }

  public GraphViz attr(String key, String val) {
    BAttr attr = new BAttr(key, val);
    entries.add(attr);
    return this;
  }

  public GraphViz comment(String text) {
    this.comment = "/* " + text + " */";
    return this;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void isDirected(boolean isDirected) {
    this.isDirected = isDirected;
  }

  public boolean isDirected() {
    return this.isDirected;
  }

  public GraphViz.Node node(String name) {
    Node node = new BNode(name);
    entries.add(node);
    return node;
  }

  public GraphViz.Edge edge(String src, String dst) {
    Edge edge = new BEdge(src, dst);
    entries.add(edge);
    return edge;
  }

  public GraphViz subgraph(String name) {
    BGraph sub = new BGraph(name);
    entries.add(sub);
    return this;
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    if (comment != null) b.append(comment).append(StringTools.NEWLINE);

    if (isDirected) b.append("digraph ");
    else b.append("graph ");

    b.append(name).append(' ').append('{').append(StringTools.NEWLINE);

    for (int i = 0; i < entries.size(); i++) {
      Bufferable r = (Bufferable) entries.get(i);
      b.append("    ");
      r.toBuffer(b);

      if (r instanceof BAttr) b.append(';').append(StringTools.NEWLINE);
    }

    b.append('}').append(StringTools.NEWLINE);

    return b.toString();
  }

  protected String name;
  protected String comment;
  protected List entries;
  protected boolean isDirected;

  private interface Bufferable {
    void toBuffer(StringBuffer b);
  }

  private static class AttrList implements Bufferable {
    public void toBuffer(StringBuffer b) {
      if (attrs != null) {
        b.append(' ').append('[');
        for (int i = 0; i < attrs.size(); i++) {
          BAttr a = (BAttr) attrs.get(i);
          if (i > 0) b.append(',');

          a.toBuffer(b);
        }
        b.append(']');
      }
    }

    List attrs;
  }

  private static class BAttr implements Bufferable {
    BAttr(String key, String val) {
      this.key = key;
      this.val = val;
    }

    public void toBuffer(StringBuffer b) {
      b.append(key).append('=').append('"').append(val).append('"');
    }

    String key;
    String val;
  }

  private static class BNode extends AttrList implements GraphViz.Node {
    BNode(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public GraphViz.Node attr(String key, String val) {
      if (attrs == null) attrs = new LinkedList();

      BAttr attr = new BAttr(key, val);
      attrs.add(attr);
      return this;
    }

    public void toBuffer(StringBuffer b) {
      b.append(name);
      super.toBuffer(b);
      b.append(';').append(StringTools.NEWLINE);
    }

    String name;
  }

  private class BEdge extends AttrList implements GraphViz.Edge {
    BEdge(String src, String dst) {
      this.src = src;
      this.dst = dst;
    }

    public String getSrc() {
      return src;
    }

    public String getDst() {
      return dst;
    }

    public GraphViz.Edge attr(String key, String val) {
      if (attrs == null) attrs = new LinkedList();

      BAttr attr = new BAttr(key, val);
      attrs.add(attr);
      return this;
    }

    public void toBuffer(StringBuffer b) {
      b.append(src).append(isDirected ? " -> " : " -- ").append(dst);
      super.toBuffer(b);
      b.append(';').append(StringTools.NEWLINE);
    }

    String src;
    String dst;
  }

  private static class BGraph extends Dot implements Bufferable {
    BGraph(String name) {
      super(name);
    }

    public void toBuffer(StringBuffer b) {
      b.append("subgraph ").append(name).append(' ').append('{').append(StringTools.NEWLINE);

      for (int i = 0; i < entries.size(); i++) {
        Bufferable r = (Bufferable) entries.get(i);
        b.append("        ");
        r.toBuffer(b);

        if (r instanceof BAttr) b.append(';').append(StringTools.NEWLINE);
      }

      b.append("    }").append(StringTools.NEWLINE);
    }
  }
}
