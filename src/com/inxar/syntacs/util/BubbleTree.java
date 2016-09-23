/**
 * $Id: BubbleTree.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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

import org.inxar.syntacs.util.*;

/**
 * <code>BubbleTree</code> is a binary tree (interval tree) data
 * structure which maintains a <code>IntSet</code> of integers at each
 * node in the tree.  The unique feature of is that adjacent nodes
 * which 'abut' each other (the low endpoint of <code>I1</code> is one
 * greater than the high endpoint of <code>I2</code>) will merge into
 * a single node when their bitsets are equal.  This can be envisioned
 * like two oil bubbles floating in water suddenly merging onto one
 * larger oil bubble.  This is the origin of 'bubble' in
 * '<code>BubbleTree</code>'.
 *
 * <P>
 *
 * Two bubbles in the tree may merge in four different ways.  Each
 * interval (node) in the tree may at some point find that its IntSet is
 * equal to one or more of its four "neighbors".  Thus, merging may
 * occur with the left upper node, the right upper node, the left down
 * node, or the right down node.
 *
 * <P>
 *
 * This data structure is exceedingly cool.
 */
public class BubbleTree {
  private static final boolean DEBUG = false;

  /**
   * Zero-argument constructor creates a new empty
   * <code>BubbleTree</code>.
   */
  public BubbleTree() {}

  /**
   * Puts the given value across the given interval &lt;lo,hi&gt;.
   */
  public void put(int lo, int hi, IntSet other) {
    //if (other.isEmpty()) return;

    if (root == null) {
      root = new Bubble(lo, hi, other);
    } else {
      root.put(lo, hi, other);
    }
  }

  /**
   * Returns the <code>IntSet</code> maintained at the interval which
   * includes the given key.
   */
  public IntSet get(int key) {
    return null;
  }

  /**
   * Returns <code>true</code> if this is an empty tree.
   */
  public boolean isEmpty() {
    return root == null;
  }

  public String toString() {
    if (root == null) return "<>";

    StringBuffer b = new StringBuffer();
    root.toBuffer(b);
    return b.toString();
  }

  public Bubble root;
  private static int count;

  /**
   * A Bubble is a single node in a BubbleTree interval tree.
   */
  public static class Bubble {
    /*

      lup    rup
        \    /
    	     +--+
         |  |
         +--+
        /    \
      ldn    rdn

    */

    Bubble(int lo, int hi, IntSet other) {
      this(lo, hi, other, null, null, null, null);
    }

    Bubble(int lo, int hi, IntSet other, Bubble ldn, Bubble rdn, Bubble lup, Bubble rup) {
      this.lo = lo;
      this.hi = hi;
      this.ldn = ldn;
      this.rdn = rdn;
      this.lup = lup;
      this.rup = rup;
      this.id = count++;
      this.set = new BitSetIntSet(other.size());
      this.set.union(other);
    }

    void put(int that_lo, int that_hi, IntSet other) {
      // TRINITY 1: is the other completely left?
      //
      // this:      [---)
      // that: (---]
      //
      if (this.lo > that_hi) {
        if (DEBUG)
          System.out.println(
              "(" + that_lo + "," + that_hi + "," + other + ") TRINITY LEFT " + this);
        // Check that ldn exists first
        if (this.ldn != null) {
          // delegate down
          this.ldn.put(that_lo, that_hi, other);
        } else {
          // insert new one
          this.ldn = new Bubble(that_lo, that_hi, other, null, null, this.lup, this);
        }

        // TRINITY 2: is the other completely right?
        //
        // this: (---]
        // that:      [---)
        //
      } else if (this.hi < that_lo) {
        if (DEBUG)
          System.out.println(
              "(" + that_lo + "," + that_hi + "," + other + ") TRINITY RIGHT " + this);
        // Check that rdn exists first
        if (this.rdn != null) {
          // delegate down
          this.rdn.put(that_lo, that_hi, other);
        } else {
          // insert new one
          this.rdn = new Bubble(that_lo, that_hi, other, null, null, this, this.rup);
        }

        // TRINITY 3: Overlapping. There are 9 overlap possibilities.
        //
        // this: (---)
        // that: (---)
        //
        //   1     2     3     4     5     6     7     8     9
        // [---] [--]  [---]  [--] [---] [--]   [--] [---]  [-]
        // [---] [---] [--]  [---]  [--]  [--] [--]   [-]  [---]
        //
      } else {

        // get a boolean value which -- if true -- indicates
        // that the value is not in the set.  This will affect
        // whether certain splitting and merging operations
        // will occur.  We take this value once reather than
        // potentially testing it several times.
        boolean setsAreNotEqual = !this.set.equals(other);

        // -----------------------------------------------------
        // PART 1: TEST LOW EDGES
        // -----------------------------------------------------
        //
        // CASE 1: LOW SUPERCLUSION
        //
        // this:    [---)  ==>     [---)
        // that: [------)  ==>  [-]
        //
        if (this.lo > that_lo) {
          if (DEBUG)
            System.out.println(
                "(" + that_lo + "," + that_hi + "," + other + ") LOW SUPERCLUSION " + this);
          // pass 'truncated' interval ldn (or make new)
          if (this.ldn != null) {
            this.ldn.put(that_lo, this.lo - 1, other);
          } else {
            this.ldn = new Bubble(that_lo, this.lo - 1, other, null, null, this.lup, this);
          }

          // CASE 2: LOW SUBCLUSION
          //
          // this: [------)
          // that:    [---)
          //
        } else if (this.lo < that_lo) {
          // First check to see if the sets are equal. If
          // they are then splitting will make no difference
          // and the two adjacent bubbles will subsequently
          // remerge.
          if (setsAreNotEqual) {
            if (DEBUG)
              System.out.println(
                  "(" + that_lo + "," + that_hi + "," + other + ") LOW SUBCLUSION " + this);
            // break off low overhang
            this.ldn = new Bubble(this.lo, that_lo - 1, this.set, this.ldn, null, this.lup, this);
            // check if ldn ldn child exists
            Bubble ldnldn = this.ldn.ldn;
            if (ldnldn != null) {
              ldnldn.lup = this.ldn;
            }
            // correct new lo endpoint
            this.lo = that_lo;
          }

          // CASE 3: LOW EQUAL
          //
          // this: [---)
          // that: [---)
          //
        } else {
          /* NO ACTION NECESSARY --- waiting for merge() below */
        }

        // -----------------------------------------------------
        // PART 2: TEST HIGH EDGES
        // -----------------------------------------------------
        //
        // CASE 1: HIGH SUPERCLUSION
        //
        // this: (---]     ==>  (---]
        // that: (------]  ==>       [-]
        //
        if (this.hi < that_hi) {
          if (DEBUG)
            System.out.println(
                "(" + that_lo + "," + that_hi + "," + other + ") HIGH SUPERCLUSION " + this);
          // pass 'bitten' interval rdn (or make new)
          if (this.rdn != null) {
            this.rdn.put(this.hi + 1, that_hi, other);
          } else {
            this.rdn = new Bubble(this.hi + 1, that_hi, other, null, null, this, this.rup);
          }

          // CASE 2: HIGH SUBCLUSION
          //
          // this: (------]
          // that: (---]
          //
        } else if (this.hi > that_hi) {
          // First check to see if the sets are equal. If
          // they are then splitting will make no difference
          // and the two adjacent bubbles will subsequently
          // remerge.
          if (setsAreNotEqual) {
            if (DEBUG)
              System.out.println(
                  "(" + that_lo + "," + that_hi + "," + other + ") HIGH SUBCLUSION " + this);
            // break off high overhang
            this.rdn = new Bubble(that_hi + 1, this.hi, this.set, null, this.rdn, this, this.rup);
            // check if rdn rdn child exists
            Bubble rdnrdn = this.rdn.rdn;
            if (rdnrdn != null) {
              rdnrdn.rup = this.rdn;
            }
            // correct new hi endpoint
            this.hi = that_hi;
          }

          // CASE 3: HIGH EQUAL
          //
          // this: (---]
          // that: (---]
          //
        } else {
          /* NO ACTION NECESSARY --- waiting for merge() below */
        }

        // check if sets are equal.  If so then there is no
        // need to try to merge adjacent bubbles (or do
        // anything)
        if (setsAreNotEqual) {
          // bubble merge
          merge(other);
        }
      }
    }

    void merge(IntSet other) {
      // add the value to the set
      this.set.union(other);

      // -----------------------------------------------------
      // PART 1: MERGE LEFT
      // -----------------------------------------------------
      //
      // try to merge low
      if (this.ldn != null) {
        // test boundary condition and set equality
        if (this.ldn.hi == this.lo - 1 && this.set.equals(this.ldn.set)) {
          if (DEBUG) System.out.println("MERGING LOW LEFT " + this);
          // take reference to node which we will excise
          Bubble ex = this.ldn;
          // expand this lower endpoint
          this.lo = ex.lo;
          // take new left down
          this.ldn = ex.ldn;
          // fixup new left if necessary
          if (this.ldn != null) {
            // update high right (high left does not need
            // to be updated as it already has the currect
            // lup)
            this.ldn.rup = this;
          }
        }
        // if lower left was null, we may be able to merge up
      } else if (this.lup != null) {
        // test boundary condition and set equality
        if (this.lup.hi == this.lo - 1 && this.set.equals(this.lup.set)) {
          if (DEBUG) System.out.println("MERGING HIGH LEFT " + this);
          // expand the highpoint of the lup
          this.lup.hi = this.hi;
          // if right down is non-null we will move it into
          // our current position.  We know that ldn is null
          // if lup boundary condition is true.
          if (this.rdn != null) {
            // and the converse
            this.rdn.rup = this.rup;
            // and update lower right's upper left to ours
            this.rdn.lup = this.lup;
          }
          // update upper right to point to new in bubble
          // instead of us
          // unless it was inherited from root in which case it will not exist
          if (this.rup != null) {
            this.rup.ldn = this.rdn;
          }
          // need to splice ourselves out lup if we are the
          // direct descendant
          if (this.lup.rdn == this) {
            this.lup.rdn = this.rdn;
          }
        }
      }

      // -----------------------------------------------------
      // PART 2: MERGE RIGHT
      // -----------------------------------------------------
      //
      // try to merge low
      if (this.rdn != null) {
        // test boundary condition and set equality
        if (this.rdn.lo == this.hi + 1 && this.set.equals(this.rdn.set)) {
          if (DEBUG) System.out.println("MERGING LOW RIGHT " + this);
          // take reference to node which we will excise
          Bubble ex = this.rdn;
          // expand this upper endpoint
          this.hi = ex.hi;
          // take new right down
          this.rdn = ex.rdn;
          // fixup new left if necessary
          if (this.rdn != null) {
            // update high left (high right does not need
            // to be updated as it already has the currect
            // rup)
            this.rdn.lup = this;
          }
        }
        // if lower right was null, we may be able to merge up
      } else if (this.rup != null) {
        // test boundary condition and set equality
        if (this.rup.lo == this.hi + 1 && this.set.equals(this.rup.set)) {
          if (DEBUG) System.out.println("MERGING HIGH RIGHT " + this);
          // expand the lowpoint of the rup
          this.rup.lo = this.lo;
          // if left down is non-null we will move it into
          // our current position.  We know that rdn is null
          // if rup boundary condition is true.
          if (this.ldn != null) {
            // and the converse
            this.ldn.lup = this.lup;
            // and update lower left's upper right to ours
            this.ldn.rup = this.rup;
          }
          // update upper left to point to new in bubble
          // instead of us
          // unless it was inherited from root in which case it will not exist
          if (this.lup != null) {
            this.lup.rdn = this.ldn;
          }
          // need to splice ourselves out lup if we are the
          // direct descendant
          if (this.rup.ldn == this) {
            this.rup.ldn = this.ldn;
          }
        }
      }
    }

    void toBuffer(StringBuffer b) {
      if (ldn != null) ldn.toBuffer(b);

      b.append('<')
          .append('[')
          .append(id)
          .append(']')
          .append(lo > 0 ? "+" : "")
          .append(lo)
          .append(':')
          .append(set)
          .append(':')
          .append(hi > 0 ? "+" : "")
          .append(hi)
          .append('>');
      //.append(Text.NEWLINE);

      if (rdn != null) rdn.toBuffer(b);
    }

    public String toString() {
      return new StringBuffer()
          .append('<')
          .append('[')
          .append(id)
          .append(']')
          .append(lo > 0 ? "+" : "")
          .append(lo)
          .append(':')
          .append(set)
          .append(':')
          .append(hi > 0 ? "+" : "")
          .append(hi)
          .append('>')
          .toString();
    }

    public int id; // debug identifier
    public int lo; // the lo input boundary
    public int hi; // the hi input boundary
    public IntSet set; // the int set at this node
    public Bubble ldn; // next lesser bubble down
    public Bubble rdn; // next greater bubble down
    public Bubble lup; // next lesser bubble up
    public Bubble rup; // next higher bubble up
  }

  /*
     public static void main(String[] argv) throws Exception
     {
  BubbleTree tree = new BubbleTree();

         // init first one
         add(-10, +10, 0, tree);
         // test trinity left
         //add(-30, -20, 1, tree);
         // test trinity right
         //add(+20, +30, 2, tree);
         // test low superclusion
         add(-15, +10, 3, tree);
  // test merge
         add(-15, -11, 0, tree);
         add(-15, -11, 4, tree);
         // test high superclusion
         add(-10, +15, 4, tree);
         // test merge
         add(-10, +15, 0, tree);
         add(-10, +15, 3, tree);
         add(-10, +15, 4, tree);
     }

     static void add(int lo, int hi, int other, BubbleTree tree)
     {
         add(lo, hi, new SingletonIntSet(other), tree);
     }

     static void add(int lo, int hi, IntSet other, BubbleTree tree)
     {
  System.out.println("ADDING ("+lo+","+hi+","+other+")");
         tree.put(lo, hi, other);
  System.out.println(tree);
  System.out.println();
     }
     */
}

/*
  try {
  this.set = (IntSet)other.clone();
  } catch (CloneNotSupportedException cnsex) {
  throw new RuntimeException("Unexpected clone failure.");
  }

                        *
                       / \
                      /
                     -
*/
