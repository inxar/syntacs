/**
 * $Id: SCCTransitiveClosure.java,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp $
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
package com.inxar.syntacs.automaton.pushdown;

import org.inxar.syntacs.util.IntStack;
import org.inxar.syntacs.util.IntRelation;
import org.inxar.syntacs.util.IntIterator;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntStack;
import org.inxar.syntacs.util.IntFunction;
import org.inxar.syntacs.util.Algorithm;
import org.inxar.syntacs.util.AlgorithmException;

import com.inxar.syntacs.util.ArrayIntStack;
import com.inxar.syntacs.util.EmptyIntSet;
import com.inxar.syntacs.util.HashIntFunction;

/**
 * This is originally from Eve J, Kurki-Suonio R <code>On Computing
 * the Transitive Closure of a Relation" Acta Inf. 8 (1977),
 * 303-314;</code> modified for use in LALR set determination by
 * DeRemer/Penello <code>DeRemer F, Penello T "Efficient Computation
 * of LALR(1) Look-Ahead Sets" ACM-TOPLAS vol.4, no.4 (Oct 1982)
 * 615-649</code>.  Alternative explanation can be found in <code>"The
 * Theory and Practice of Compiler Writing", Tremblay/Sorenson
 * p.382</code>.
 */
public class SCCTransitiveClosure implements Algorithm {
  private static final boolean DEBUG = true;

  /**
   * Constructs the <code>SCCTransitiveClosure</code> on the given
   * raw <code>IntSet</code> of vertices, the
   * <code>IntRelation</code> which implies the edges which connect
   * those vertices, the <code>IntRelation</code> which holds the
   * <code>IntSet</code> of values at each vertex, and the
   * <code>IntRelation</code> where the output <code>IntSet</code>
   * of values should be put.
   */
  public SCCTransitiveClosure(
      IntSet vertices, IntRelation relation, IntRelation input, IntRelation output) {
    this.vertices = vertices;
    this.relation = relation;
    this.input = input;
    this.output = output;

    // make a new stack with said capacity
    this.stack = new ArrayIntStack(vertices.size());

    // initialize a structure to be able to mark vertices.  The
    // indices of the vector should correspond to the vertices and
    // the value will be an integer, initially zero.
    this.map = new HashIntFunction(vertices.size());

    if (DEBUG) trace("relation is " + relation);
    if (DEBUG) this.tab = -1;
  }

  public IntFunction getMap() {
    return map;
  }

  public void evaluate() throws NonTrivialSCCException {
    // need iterator over vertices
    IntIterator vertex = vertices.iterator();

    // an arbitrary vertex x
    int x;
    // push all vertices
    while (vertex.hasNext()) {
      // get each one
      x = vertex.next();
      // have we seen it?
      if (map.get(x) == 0) {
        traverse(x);
      }
    }

  }

  private void traverse(int x) throws NonTrivialSCCException {
    if (DEBUG) tab++;
    if (DEBUG) trace("traversing x " + x + ", stack is " + stack);

    // first push the vertex on the stack
    stack.push(x);
    // save the depth of the stack
    int depth = stack.size();
    // mark this vertex's relative location
    map.put(x, depth);
    if (DEBUG) trace("marked x " + x + " at depth " + depth);
    // now execute the function
    if (DEBUG) trace("copied input set " + input.get(x) + " for x=" + x + " to output");
    output.set(x, copy(input.get(x)));
    // now get an iterator over all the vertices
    // y such that xRy
    IntSet set = relation.get(x);
    if (DEBUG) trace("relation set for x " + x + " is " + set);

    if (set != null) {

      // Note: It has been awhile since I coded this algorithm.  The
      // design of the HashSetFunction used to be such that the empty
      // set (non-null) would be returned if x was not mapped to a
      // value in the function.  This was changed such that null is
      // now the return value, so it was necessary to add the null
      // check above.  The question is, does the existence of a null
      // set from the projection over x mean something significant?
      // Should we always expect that the values we choose for x
      // always have a corresponding IntSet value?  Hmm....

      IntIterator iterator = set.iterator();

      // an arbitrary vertex y
      int y;
      // and run the iterator...
      while (iterator.hasNext()) {
        // get each one
        y = iterator.next();
        if (DEBUG) trace("checking y of xRy " + y);
        // Seen it? 0 means no.
        if (map.get(y) == 0) {
          traverse(y); // recursion
        }
        // mark the minimal SCC cycle
        //map.put(y, Math.min(map.get(x), map.get(y)));
        map.put(x, Math.min(map.get(x), map.get(y)));

        if (DEBUG)
          trace(
              "marked x "
                  + x
                  + " at min (x,y) ("
                  + map.get(x)
                  + ","
                  + map.get(y)
                  + ") to "
                  + Math.min(map.get(x), map.get(y)));

        // merge the functions
        if (x != y) {
          if (DEBUG)
            trace("unioning " + x + ":" + output.get(x) + " with " + y + ":" + output.get(y));
          output.get(x).union(output.get(y));
        }
      }
    }

    // finished with stage 1.

    if (DEBUG) trace("stack is " + stack + ", x is " + x + ", depth is " + depth);
    // now check if we are at depth (in a SCC)
    if (map.get(x) == depth) {
      if (DEBUG)
        trace(
            "stack is "
                + stack
                + ", x is "
                + x
                + ", map x is "
                + map.get(x)
                + ", depth is "
                + depth);
      // if the depth of the stack is greater than one it means
      // we are in a non-trivial SCC.
      if (depth > 1) {
        if (DEBUG) trace("* throwing SCC Exception!");

        // Whoa!  Why has the throw statement been commented
        // out til now?  This means that this class would
        // NEVER throw an NonTrivialException!
        // Jeezus-H-Christ!

        //throw new NonTrivialSCCException();

        // If it is commented out, things seem to work
        // correctly.  But if it not commented out, it throws
        // Exceptions on what should be good input.  Something
        // is funny, and needs to be further reviewed.  One
        // problem is that I don't have a data set which is
        // known to be context-free, but not LALR1.
      }

      // mark the vertex away
      map.put(stack.peek(), INFINITY);
      if (DEBUG) trace("assigning map(" + x + ") to INF");
      // initialize last vertex to this x
      output.set(stack.peek(), copy(output.get(x)));
      if (DEBUG) trace("setting set " + output.get(x) + " to " + stack.peek());
      // get the last vertex from the stack
      int vertex = stack.pop();
      // compress the scc
      while (vertex != x) {
        // mark the vertex away
        map.put(stack.peek(), INFINITY);
        // initialize last vertex to this x
        output.set(stack.peek(), copy(output.get(x)));
        if (DEBUG) trace("compressing SCC, setting set " + output.get(x) + " to " + stack.peek());
        // get the last vertex from the stack
        vertex = stack.pop();
      }
    }
    if (DEBUG) trace("returning...");
    if (DEBUG) tab--;
  }

  private void trace() {
    System.out.println();
  }

  private void trace(String s) {
    int i = tab;
    while (i-- > 0) System.out.print("\t");
    System.out.println(s);
  }

  private IntSet copy(IntSet set) {
    if (set == null) {
      return EmptyIntSet.EMPTY_SET;
      //throw new NullPointerException("Cannot copy null input set");
    }
    try {
      return (IntSet) set.clone();
    } catch (CloneNotSupportedException cnsex) {
      throw new RuntimeException("Please use Cloneable set implementations with this algorithm.");
    }
  }

  private int tab;
  private IntSet vertices;
  private IntRelation relation;
  private IntRelation input;
  private IntRelation output;
  private IntFunction map;
  private IntStack stack;
  private static final int INFINITY = Integer.MAX_VALUE;

  public static class NonTrivialSCCException extends AlgorithmException {
    NonTrivialSCCException() {
      super();
    }
  }
}
