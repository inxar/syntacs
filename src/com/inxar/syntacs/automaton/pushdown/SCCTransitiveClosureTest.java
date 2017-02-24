package com.inxar.syntacs.automaton.pushdown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.inxar.syntacs.util.IntStack;
import org.inxar.syntacs.util.IntRelation;
import org.inxar.syntacs.util.IntIterator;
import org.inxar.syntacs.util.IntSet;
import org.inxar.syntacs.util.IntStack;
import org.inxar.syntacs.util.IntFunction;
import org.inxar.syntacs.util.Algorithm;
import org.inxar.syntacs.util.AlgorithmException;

import com.inxar.syntacs.util.ArrayIntStack;
import com.inxar.syntacs.util.HashIntFunction;
import com.inxar.syntacs.util.HashBitSetIntRelation;
import com.inxar.syntacs.util.BitSetIntSet;
import com.inxar.syntacs.util.SingletonIntSet;

public class SCCTransitiveClosureTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testSimple() throws SCCTransitiveClosure.NonTrivialSCCException {
    IntSet vertices = new BitSetIntSet();
    vertices.put(1);
    //vertices.put(2);
    //vertices.put(3);
    IntRelation edges = new HashBitSetIntRelation();
    edges.put(1, 2);
    edges.put(2, 1);
    edges.put(1, 1);
    edges.put(2, 2);
    IntRelation input = new HashBitSetIntRelation();
    input.put(1, 4);
    input.put(1, 5);
    input.put(1, 6);
    input.put(1, 7);
    input.put(2, 9);
    input.put(2, 10);
    input.put(2, 11);
    input.put(2, 12);
    IntRelation output = new HashBitSetIntRelation();

    SCCTransitiveClosure c = new SCCTransitiveClosure(vertices, edges, input, output);

    c.evaluate();

    System.out.println("scc map: " + c.getMap());
    System.out.println("vertices: " + vertices);
    System.out.println("edges: " + edges);
    System.out.println("input: " + input);
    System.out.println("output: " + output);

    // NOTE(pcj): confirm this should not be '3'
    assertEquals(2, output.size());
  }
}
