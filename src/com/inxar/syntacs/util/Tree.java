/**
 * $Id: Tree.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
import org.inxar.syntacs.util.*;

/**
 * Utility for pretty-printing parse trees.  Output modeled after the
 * tree(1) utility written by Steve Baker.
 */
public class Tree {
  private final static boolean DEBUG = true;

  public Tree(String name) {
    this.name = name;
  }

  public Tree(Tree parent, String name) {
    this.name = name;
    this.parent = parent;
  }

  public Tree add(String name) {
    if (list == null) list = new LinkedList();

    Tree t = new Tree(this, name);

    list.add(t);

    return t;
  }

  public int arity() {
    return list == null ? 0 : list.size();
  }

  public Tree child(int i) {
    if (list == null) return null;

    return (Tree) list.get(i);
  }

  public Tree parent() {
    return parent;
  }

  public String toString() {
    Buffer out = new Buffer();
    Buffer head = new Buffer();

    toBuffer(head, out);

    return out.toString();
  }

  private void toBuffer(Buffer head, Buffer out) {
    out.add(name).ln();

    if (list != null) {

      if (list.size() > 1) {

        Tree t;
        for (int i = 0; i < list.size() - 1; i++) {
          out.add(head);
          head.add("|   ");

          //out.add(head).add("|-- ");
          out.add("|-- ");
          ((Tree) list.get(i)).toBuffer(head, out);
          head.length(head.length() - 4);
        }
      }

      out.add(head);

      head.add("    ");
      out.add("`-- ");
      ((Tree) list.get(list.size() - 1)).toBuffer(head, out);
      head.length(head.length() - 4);
    }
  }

  protected static Log log() {
    if (log == null) log = Mission.control().log("tree", new Tree(""));
    return log;
  }

  protected String name;
  protected Tree parent;
  protected List list;
  protected static Log log;

  private static final int LEFT = 1;
  private static final int RIGHT = 2;
  private static final int MIDDLE = 3;

  /**
   * Experimental subclass of <code>Tree</code> which tries to do
   * more advanced tree layout.
   */
  public static class Box extends Tree {
    public Box(String name) {
      super(name);
      init();
    }

    public Box(Box parent, String name) {
      super(parent, name);
      init();
    }

    private void init() {
      width = median = pivot = -1;
    }

    public Tree add(String name) {
      if (list == null) list = new LinkedList();

      Box t = new Box(this, name);

      list.add(t);

      return t;
    }

    private int getWidth() {
      // The width of this box is 1 plus the sum of the widths
      // of the children, if any.
      if (width == -1) {

        width = 1;

        if (list != null)
          for (int i = 0; i < list.size(); i++) width += ((Box) list.get(i)).getWidth();
      }

      return width;
    }

    private int getPivot() {
      if (pivot == -1) getMedian();
      return pivot;
    }

    private int getMedian() {
      if (median == -1) {

        if (list == null || list.size() < 2) {
          median = (getWidth() / 2);
          pivot = 0;
        } else if (list.size() == 2) {
          median = (getWidth() / 2) + 1;
          pivot = 0;
        } else {

          log()
              .debug()
              .write("box ")
              .write(name)
              .write(", ")
              .write(System.identityHashCode(this))
              .write(" has list size ")
              .write(list.size())
              .out();

          int len = list.size();

          // Part 1: get a list if widths
          IntList widths = new ArrayIntList(len);
          for (int i = 0; i < len; i++) widths.add(((Box) list.get(i)).getWidth());

          log()
              .debug()
              .write("box ")
              .write(name)
              .write(", ")
              .write(System.identityHashCode(this))
              .write(" has child widths ")
              .write(widths)
              .out();

          // Part 2: make a list of pivots (always one less
          // than the number of children)..
          IntList pivots = new ArrayIntList(--len);

          // Initialize sides
          int left, right, tmp;

          left = widths.at(0);
          right = sum(widths, 1, len);

          for (int i = 1; i < len; i++) {
            pivots.add(Math.abs(left - right));
            tmp = widths.at(i);
            left += tmp;
            left -= tmp;
          }

          // Part 3: Get smallest pivot.  If two pivots are
          // equal, try to find the one closest to the
          // middle.
          int pidx = 0, pval = pivots.at(0);

          for (int i = 1; i < len; i++) {
            tmp = pivots.at(i);
            // If this value is smaller OR the values are
            // equal AND this one is closer to the center,
            // then update.
            if ((tmp < pval) || (tmp == pval && isMedial(i, pval, len))) {
              pval = tmp;
              pidx = i;
            }
          }

          pivot = pidx;
          median = sum(widths, 0, pivot);
        }
      }
      return median;
    }

    public String toString() {
      // Make a new grid
      Grid grid = new Grid();

      // Need to calculate the total width and starting point.
      // We will use our own values for this.
      int w = getWidth();
      int m = getMedian();

      log()
          .debug()
          .write("Initial box has")
          .write(" width ")
          .write(width)
          .write(", median ")
          .write(median)
          .write(", pivot ")
          .write(pivot)
          .out();

      toGrid(0, m, grid);

      return grid.toString();
    }

    private void toGrid(int xoff, int yoff, Grid grid) {
      log()
          .debug()
          .write("Writing grid for ")
          .write(name)
          .write(", ")
          .write(System.identityHashCode(this))
          .write(", width ")
          .write(width)
          .write(", pivot ")
          .write(pivot)
          .write(", median ")
          .write(median)
          .write(", xoff ")
          .write(xoff)
          .write(", yoff ")
          .write(yoff)
          .out();

      // We take arguments under the assumption that (xoff,yoff)
      // is an origin, the spot where we should write the first
      // character of our name.
      grid.write(xoff, yoff, name);
      //grid.write(xoff, yoff, ""+System.identityHashCode(this));

      // Now get out width and pivot, which has already been
      // calculated.
      int w = getWidth();
      int m = getMedian();
      int p = getPivot();

      // Now we write the children, if any.
      if (list == null) return;

      Box b;
      int bw, bm;
      int i, x, y, dy;

      int r = 0;

      // First we will write all kids that are below us, or
      // LEFT.
      i = p; // iterates the child list
      x = xoff; // absolute x position
      y = yoff; // absolute y position
      dy = 0; // relative distance from yoff

      while (i >= 0) {

        // For each child, we need to figure out what it's
        // width is and where the pivot is.
        b = (Box) list.get(i);
        bw = b.getWidth();
        bm = b.getMedian();

        log()
            .debug()
            .write("In ")
            .write(name)
            .write(", ")
            .write(System.identityHashCode(this))
            .write(", box width ")
            .write(bw)
            .write(", box median ")
            .write(bm)
            .out();

        dy += bw;

        // Now we need to step vertically up from yoff to
        // (yoff + bw - bp) to find the next point.
        while (++y > (yoff + dy - bm)) {

          log().debug().write("| at (" + x + "," + y + ") ").out();

          grid.write(x, y, "k" + r++);
        }

        // Write the header...
        if (i == 0) grid.write(x, y, "`-- ");
        else grid.write(x, y, "|-- ");

        // ...then have the box write itself at this position.
        b.toGrid(x + 4, y, grid);

        // And move to the next child down...
        --i;
      }

      // Now write all below us.
      i = p + 1;
      y = yoff;
      dy = 0;

      while (i < list.size()) {

        if (false) break;

        // For each child, we need to figure out what it's
        // width is and where the pivot is.
        b = (Box) list.get(i);
        bw = b.getWidth();
        bm = b.getMedian();

        dy -= bw - 1;

        // Now we need to step vertically down from yoff to
        // (yoff - bw - bp) to find the next point.
        while (--y > (yoff + dy - bm)) grid.write(x, y, "|");

        // Now write the header, then have the child write itself.
        if (i == list.size() - 1) grid.write(x, y, ".-- ");
        else grid.write(x, y, "|-- ");
        b.toGrid(x + 4, y, grid);

        // And move to the next child...
        ++i;
      }
    }

    int width;
    int pivot;
    int median;
  }

  private static class Grid {
    Grid() {
      src = new Buffer[12];
    }

    public void write(int x, int y, String s) {
      while (y >= src.length) realloc();

      if (src[y] == null) src[y] = new Buffer();

      if (false)
        log()
            .trace()
            .write("writing ")
            .write(s)
            .write(" at (")
            .write(x)
            .write(",")
            .write(y)
            .write(")")
            .out();

      src[y].set(x, s);
    }

    int count;

    public String toString() {
      Buffer out = new Buffer(), line = null;

      int len;
      char c;
      for (int i = 0; i < src.length; i++) {
        line = src[i];
        if (line == null) continue;

        //System.out.println("line "+i+" is "+line);

        len = line.length();

        for (int j = 0; j < len; j++) {
          c = line.charAt(j);
          //System.out.println("["+i+","+j+"] = "+(int)c);
          if (c < 0x20) out.add(' ');
          else out.add(c);
        }

        out.ln();
      }

      return out.toString();
    }

    private void realloc() {
      //System.out.println("realloc'ing grid x to "+(src.length * 2));
      Buffer[] dst = new Buffer[src.length * 2];
      System.arraycopy(src, 0, dst, 0, src.length);
      src = dst;
    }

    private Buffer[] src;
  }

  private static boolean isMedial(int x, int y, int len) {
    if (x == 0) return false;
    if (y == 0) return true;
    if (x == y) return false;
    if (len < 3) return false;

    float ctr = len / 2;

    if (x == ctr) return true;
    if (y == ctr) return false;

    float dx = x < ctr ? ctr - x : x - ctr;
    float dy = y < ctr ? ctr - y : y - ctr;

    return dx < dy;
  }

  private static int sum(IntArray a, int off, int len) {
    int sum = 0;
    for (int i = off; i < off + len; i++) sum += a.at(i);
    return sum;
  }

  public static void main(String[] argv) {
    if (argv.length != 0) {
      System.err.println("Usage: java this.class.name <>");
      System.exit(1);
    }

    Tree n00, n10, n11, n12, n13, n20, n21, n30, n31, n32, n33;

    n00 = new Tree("n00");

    n10 = n00.add("n10");
    n11 = n10.add("n11");
    n12 = n11.add("n12");
    n13 = n12.add("n13");

    n20 = n12.add("n20");
    n21 = n12.add("n21");

    n30 = n21.add("n30");
    n31 = n21.add("n31");
    n32 = n21.add("n32");
    n33 = n21.add("n33");

    System.out.println(n00.toString());
  }
}

/*

	private int getPivot()
	{
	    if (median == -1) {

		if (list == null || list.size() < 2)
		    pivot = (getWidth() / 2);
		else if (list.size() == 2)
		    pivot = (getWidth() / 2) + 1;
		else {

		    log().debug()
			.write("box ").write(name)
			.write(" has list size ").write(list.size())
			.out();

		    int len = list.size();

		    // Part 1: get a list if widths
		    IntList widths = new ArrayIntList(len);
		    for (int i = 0; i < len; i++)
			widths.add( ((Box)list.get(i)).getWidth() );

		    log().debug()
			.write("box ").write(name)
			.write(" has child widths ").write(widths)
			.out();

		    // Part 2: make a list of pivots (always one less
		    // than the number of children)..
		    IntList pivots = new ArrayIntList(--len);

		    // Initialize sides
		    int left, right, tmp;

		    left = widths.at(0);
		    right = sum(widths, 1, len);

		    for (int i = 1; i < len; i++) {
			pivots.add(Math.abs(left - right));
			tmp = widths.at(i);
			left += tmp;
			left -= tmp;
		    }

		    // Part 3: Get smallest pivot.  If two pivots are
		    // equal, try to find the one closest to the
		    // middle.
		    int pidx = 0, pval = pivots.at(0);

		    for (int i = 1; i < len; i++) {
			tmp = pivots.at(i);
			// If this value is smaller OR the values are
			// equal AND this one is closer to the center,
			// then update.
			if ( (tmp < pval) || (tmp == pval && isMedial(i, pval, len)) ) {
			    pval = tmp;
			    pidx = i;
			}
		    }

		    pivot = pidx;
		}
	    }
	    return pivot;
	}


*/
