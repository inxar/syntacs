/**
 * Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org
 * @author Paul Cody Johnston - pcj@inxar.org
 */
package com.inxar.syntacs.translator.syntacs;

import com.inxar.syntacs.automaton.pushdown.MesoArrayDPA;
import com.inxar.syntacs.util.Pickler;

/**
 * Automatically generated by <a href='http://www.inxar.org/syntacs'>Syntacs
 *  Translation Toolkit</a> on Fri Jul 06 12:05:11 PDT 2001
 */
class SyntacsDPA extends MesoArrayDPA {

  SyntacsDPA() {
    super(_action, _go, _actions);
  }

  private static int[][] _action;
  private static int[] _actions;
  private static int[][] _go;

  static {
    _action =
        Pickler.unpickle2D(
            " e # # # ! # $ $ \" # 3 3 # # % % $ # 5 5 % # 7 7 & 0 & 3 P   P P P P         P P   P #     \'\u8001\u001f\u8001\u001f 0 & 3 (   - 2 4 6         < @   E # \' \' ) # ( ( * # 3 3 + # 7 7 , 6   3 ]           ]   ] ] ] ]         ] ]   ] # 3 3 . $ 6 7 g g $ 6 7 / 1 # 3 3 0 $ 6 7 f f 6   3 _           _   _ _ _ _         _ _   _ # 3 3 . $ 6 7 / 3 6   3 c           c   c c c c         c c   c # 3 3 . $ 6 7 / 5 6   3 Y           Y   Y Y Y Y         Y Y   Y # 3 3 7 # - - 8 # 3 3 9 \' 3 7 i       i \' 3 7 :       ; \' 3 7 h       h 6   3 W           W   W W W W         W W   W # - - = # 3 3 > # 7 7 ? 6   3 U           U   U U U U         U U   U # 3 3 A # 8 8 B # 4 4 C # 7 7 D 6   3 S           S   S S S S         S S   S ) , 2 F           M # 3 3 G , . 7 H J             m m # 3 3 I $ 6 7 l l $ 6 7 k k $ 6 7 K L # 3 3 G $ 6 7 j j 6   3 [           [   [ [ [ [         [ [   [ $ 6 7 n n # 4 4 N # 7 7 O 6   3 a           a   a a a a         a a   a 6   3 e           e   e e e e         e e   e 6   3 d           d   d d d d         d d   d 6   3 b           b   b b b b         b b   b 6   3 `           `   ` ` ` `         ` `   ` 6   3 ^           ^   ^ ^ ^ ^         ^ ^   ^ 6   3 \\           \\   \\ \\ \\ \\         \\ \\   \\ 6   3 Z           Z   Z Z Z Z         Z Z   Z 6   3 X           X   X X X X         X X   X 6   3 V           V   V V V V         V V   V 6   3 T           T   T T T T         T T   T 6   3 Q           (   - 2 4 6         < @   E 6   3 R           R   R R R R         R R   R");
    _go =
        Pickler.unpickle2D(
            " d % 9 ; \'   )\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f . : E Y   Z [ \\ ] ^ _ ` a b c\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f # H H 1\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f # H H 6\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f # H H 9\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f # G G ?\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f & F I Q     U\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f # I I S\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f\u8001\u001f - : D d   Z [ \\ ] ^ _ ` a b");
    _actions =
        Pickler.unpickle1D(
            " o  0!0\"0#0$0%0&P 0*0+0,0-0.0/000203040507080:0;0<0=0>0@0A0B0C0D0E0F0G0H0I0J0K0L0M0N0O0P0R0T0V0W0X@,@ @\"@5@+@4@*@3@)@/@(@2@\'@1@&@0@%@.@$@-@#@!@7@6@9@8@;@>@=@<@:");
  }
}
