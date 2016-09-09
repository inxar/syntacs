\input texinfo

@c %**start of header @setfilename syntacs.info @settitle Syntacs
Translation Toolkit User Manual @setchapternewpage odd @c %**end of
header

@c @c \$Id: userman.texinfo,v 1.1.1.1 2001/07/06 09:08:04 pcj Exp \$ @c
@c Copyright (C) 2001 Paul Cody Johnston - pcj@inxar.org @c @c This
program is free software; you can redistribute it and/or modify @c it
under the terms of the GNU General Public License as published by @c the
Free Software Foundation; either version 2 of the License, or (at @c
your option) any later version. @c @c This program is distributed in the
hope that it will be useful, but @c WITHOUT ANY WARRANTY; without even
the implied warranty of @c MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE. See the GNU @c General Public License for more details. @c @c
You should have received a copy of the GNU General Public License @c
along with this program; if not, write to the Free Software @c
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, @c
USA.\
@c

@ifinfo This is the users manual for the Syntacs Translation Toolkit.

Copyright @copyright{} 2001 Paul Cody Johnston, inxar.org. @end ifinfo

@titlepage @title Syntacs Translation Toolkit User Manual @subtitle A
lexer and parser generator for Java @subtitle April 2001, STT Version
0.1.0

@author Paul Cody Johnston

@page @vskip 0pt plus 1fill @end titlepage

@node Top, Overview, (dir), (dir)

The Syntacs Translation Toolkit (@code{STT}) is a lexer/parser generator
in the lex/yacc family.

This document does not explain the theory of finite automata,
shift-reduce parsing, or any other of the topics one typically
encounters in the first section of any compiler book. Although it is not
strictly essential to be familiar with these concepts, it's much easier
going if you do.

@menu \* Overview::\
\* Definitions::\
\* Compilation Model::\
\* Lexical Context::\
\* Error Handling::\
\* Grammar Syntax::\
\* Regular Expression Syntax::\
\* Properties::\
\* Example Grammar::\
\* Graphviz::\
\* Resources::\
@end menu

@c =====================================================================

@node Overview, Definitions, Top, Top @chapter Overview

@section What

The Syntacs Translation Toolkit (@code{STT}) is a lexer/parser generator
in the lex/yacc family. Given an input grammar, the @code{STT} will
generate a @dfn{translator}, a machine that partially implements the
lexing and parsing tasks in Java. The grammar author is responsible for
implementing an @dfn{Interpreter} that is used to guide the
instantiation of terminals and nonterminals throughout the translation
process. The Interpreter takes the place of @dfn{semantic actions} which
are traditionally interleaved into a grammar.

@section How

Lexical analysis is done using traditional finite automata. The notion
of stack-managed lexer states (\`\`lexical context'') is introduced.
Syntactic analysis is done using shift-reduce parsing using either SLR1,
LR1, or LALR1 parse tables.

@section Why

No reason; just because. Well, that's not exactly true -- I was writing
a new language of my own and of course the first thing one does when
writing a new language is to write one's own lexer and parser
generator -- yes, to pretty much reinvent the wheel. The thing is is
that one needs to understand the wheel, and the best way to do that is
to reinvent it. So it was/is an educational experience.

@node Definitions, Compilation Model, Overview, Top @chapter Definitions

It is useful to define some terms that are used throughout the STT
related to the theory of lexical and syntactic analysis. Note that the
concepts defined here are reworked to my own interpretation and specific
to the STT, they are not intended to be generally applicable.

@section Lexical Definitions

@itemize @item A @dfn{regular expression} is a language that defines
what set of input character sequences \`\`reduce'' to a token. @item A
@dfn{token} is an input character sequence that is a particular instance
of a regular expression. @item A @dfn{regular definition} is a regular
expression that has a @dfn{symbol} (a name). @item A @dfn{regular
grammar} is a set of regular definitions. @item A @dfn{lexical context}
is a regular grammar that has a @dfn{symbol} (a name). @item A
@dfn{context transition function} is a mapping from the tuple
@dfn{(context symbol, regular definition symbol)} to @dfn{context
action}. @item A @dfn{context action} is a tuple @dfn{(instruction,
value)} that drives the management of the lexer context stack. @item A
@dfn{lexical grammar} is a set of lexical contexts, a context transition
function, and a @dfn{start context} (a distinguished context in the
lexical grammar). @item A @dfn{deterministic finite automaton} (DFA) is
an regular expression recognition machine that has a @dfn{transition
function} and an @dfn{output function}. The output function maps state
numbers to regular definition names. A DFA is equivalent to a single
regular grammar. @item A lexer is machine that transforms an input
sequence of characters to an output sequence of tokens. It uses a set of
DFAs (one for each lexical context), the context transition function,
and a context stack.

@end itemize

@section Syntactic Definitions

@itemize @item A @dfn{context-free grammar} is a set of terminals, a set
of nonterminals, and an @dfn{accepting nonterminal} (a distinguished
nonterminal). @item A @dfn{grammar symbol} is the union of the set of
terminal symbols and the set of nonterminal symbols. @item A
@dfn{terminal} is a symbol. The set of symbols that constitute the
terminals is typically drawn from the union of set of regular definition
symbols over all the lexical contexts of the lexical grammar. Note that
not all regular definition symbols need be terminal symbols, and not all
terminal symbols need be defined as a regular definition (see fig 1).
@item a @dfn{context-free expression} is a language that defines which
grammar symbol sequences \`\`reduce'' to a nonterminal. @item A
@dfn{production} is a single partition of a context-free expression, it
is one alternative of a nonterminal. @item A @dfn{nonterminal} is
context-free expression that has a @dfn{symbol}. @item A
@dfn{deterministic pushdown automaton} (DPA) is an context-free
expression recognition engine that has a terminal transition function
and a nonterminal transition function. A DPA is equivalent to a single
context-free grammar. @item A @dfn{parser} is a machine that transforms
an input sequence of terminals to a tree of grammar symbols (the syntax
tree). It uses a DPA, a state stack, and a symbol stack.

@end itemize

@example +---------------+ | Terminals | | |\
 +----|----------+ |\
 | | | |\
 | +---------------+ | | | Regular Dfn's | +---------------+ @end
example Fig. 1: The set of terminal symbols and the set of regular
definition symbols are not necessarily equal.

@section Translative Definitions

@itemize @item @dfn{Translation} refers to the combined process of
lexical and syntactic analysis. @item A @dfn{translator grammar} is a
lexical grammar and a context-free grammar. @item A @dfn{translator} is
a machine that transforms an input character sequence to an output
object. The output object may be the syntax tree itself or some result
that is generated through @dfn{interpretation} of the (abstract) syntax
tree. @end itemize

@c =====================================================================

@node Processing Model, Compilation Model, Definitions, Top

@chapter Processing Model

This section describes the sequence of events that occur during a
translation run and the components that are involved. Below is a
graphical summary of how these components interact, refer to it as
necessary.

@example

              SUMMARY OF PROCESSING MODEL
                           
                        (1) user invokes `trnsltr.translate(in)'.
       +-----------+    (2) trnsltr initializes Input with `in' argument.    
       |           |    (3) trnsltr initializes Auditor (the error repository).
       | The User  |    (4) trnsltr invokes `lexer.start()', begins parse loop.
       |           |    (5) lexer returns, parse is complete.    
       +-----------+    (6) trnsltr requests result Object from ParserInterpreter.
         ||     /\      (7) ParserInterpreter returns result.
         || (1) || (8)  (8) trnsltr returns result to user, translation is complete.
         \/     ||
       +------------------------------------------+
       | Translator                               |      
       +------------------------------------------+
         ||     /\     /\             ||       ||
         || (4) || (5) || (6,7)       || (2)   || 

> >     \/     ||     ||             \/       ||
> >
> > \_\_ +---------------------+ +---+ || '
> > `| Lexer               | <---> |I  |     || (3)           |M |  +---------------------+       |N  |     \/  |A |    ||            ||  |         |P  |    +---+   |I |    || (a)        ||`---------|U
> > |--\> |A | |N | / || |T | |U | | | +=====================+ | | |D t|
> > |P | | LexerInterpreter | \<---\> | | |I h| |A
> > | +=====================+ | | |T e| |R | || || | | | |O | |S | ||
> > (b) ||
> > `---------|   |--> |R e|  |E |    \/            ||            |   |    |  r|  |  |  +---------------------+       |   |    |  r|  |L |  | Parser              | <---> |   |    |  o|  |O |  +---------------------+       |   |    |  r|  |O |    ||     /\     ||  |         |   |    |   |  |P |    || (c) || (d) ||`---------|
> > |--\> | m| | | / || / | | | a| | | +=====================+ | | | n|
> > | | | ParserInterpreter | \<---\> | | | a|
> > `__'  +=====================+       +---+    |  g|                            |                  |  e|   <<`----------------\>
> > | r| +---+ PARSE LOOP SUMMARY --------------------

(a) Lexer uses Input & DFA[] recognizes terminal character sequence
    (match event) calls \`interpreter.match(int terminal\_type, int
    offset, int length)'

(b) LexerInterpreter interprets match event packages terminal as a
    Symbol calls \`parser.notify(Symbol terminal)'

(c) Parser uses Symbol & DPA recognizes nonterminal symbol sequence
    (reduction event) calls \`interpreter.reduce(int production\_type,
    Sentence stack)'

(d) ParserInterpreter interprets reduction packages nonterminal as a
    Symbol returns nonterminal to Parser for inclusion on parse stack

-   All components may interact with Input and Auditor as necessary

-   LexerInterpreter and ParserInterpreter (double-lined boxes) are
    generally a single object that implements LRTranslatorInterpreter
    @end example

@section Translation Components

There are several components that make up a translator. They are listed
here as interfaces, which is how they are exist in the API, but a
translator that has been generated by the @code{STT} may or may not
retain these abstractions (for performance reasons).

@subsection Input

Responsible for directly managing the input character sequence; it
tracks position and line status. When the input has been exhausted, it
will trigger an end-of-file signal.

@subsection Lexer

Responsible for @dfn{transforming} the fine-grained sequence of
characters into a larger-grained sequence of tokens. It @dfn{uses} an
@code{Input} object and a set of finite automatons (@code{DFA}s). When a
token is recognized, it notifies the @code{LexerInterpreter} that a
token recognition event has occurred; this event describes what token
was matched, where in the input the token starts, and how long the token
is. Context switching also occurs within the @code{Lexer}.

@subsection LexerInterpreter

Responsible for @dfn{listening} for lexer events, @dfn{packaging} tokens
as terminal symbols, and @dfn{notifying} the @code{Parser} of these
terminals. It is therefore the intermediary between the @dfn{Lexer} and
the @dfn{Parser} that @dfn{interprets} lexer events.

Code within the @code{LexerInterpreter} is typically a large switch
statement with a case for each token type. @emph{The user is responsible
for writing this code}.

One of the advantages of using this type of event model is that
@code{String} creation is minimized. No since most tokens are either
ignored (comments and whitespace) or syntactic placeholders (which carry
all their meaning in their name, such as a parenthesis), no
@code{String} or array copying needs to occur in these instances. It
gives the user complete control of what is passed to the parser. For
example, if the @code{LexerInterpreter} recieves an @code{Identifier}
event, it could do a keyword symbol table lookup and then pass the
correct keyword terminal to the parser.

This also gives a chance to instantiate terminals that may form part of
the final syntax tree, minimizing transformations later in the
processing cycle.

@subsection Parser

Responsible for transforming a serial stream of tokens into a syntax
tree; it employs an augmented finite automaton (@code{DPA}) as the
recognition engine and maintains a parse stack of the symbols.

When the parser is notified of a terminal @code{Symbol}, it consults the
DPA to see what to do. If the action is @dfn{reduce}, the parser will
delegate nonterminal construction to the @code{ParserInterpreter}. This
delegation of @code{Symbol} construction responsibility to the
@code{ParserInterpreter} is analogous to the relationship between the
@code{Lexer} and the @code{LexerInterpreter}. When the
@code{ParserInterpreter} returns, it passes back a nonterminal symbol to
be placed on the top of the parse stack as per normal shift-reduce
parsing.

When the DPA says @dfn{accept}, the @code{accept()} method is called on
the @code{ParserInterpreter}.

@subsection ParserInterpreter

Responsible for @dfn{listening} for parse events and @dfn{packaging}
nonterminal symbols to the parser. When the parser sees that a reduction
is necessary, it sends the @code{ParserInterpreter} a number identifying
what production needs to be reduced as well as another object (called a
@code{Sentence}), which is the exposed top of the parse stack. The
@code{ParserInterpreter} then decides what symbols in the
@code{Sentence} (i.e. stack) to keep, packages them as a [new]
nonterminal, and returns that to the @code{Parser}. The @code{Parser}
then places this symbol on the top of the (now reduced) parse stack.

Code within the @code{ParserInterpreter} is typically a large switch
statement with a case for each production type. @emph{The user is
responsible for writing this code}.

When the parser discovers a syntactic error, it consults the
@code{ParserListener} for instruction on how to @dfn{recover} from the
error. The ParserListener formulates a list of @dfn{corrections} and
returns this to the parser for execution. When these corrections are
@dfn{satisfied}, normal parsing resumes.

@subsection LRTranslatorInterpreter

Typically, the @code{LexerInterpreter} and @code{ParserInterpreter} are
a single object that implements @code{LRTranslatorInterpreter}, which is
formed by the union of these interfaces. For examples, consult the
@code{RegexpInterpreter} and the @code{SyntacsInterpreter} classes.

The @code{LRTranslatorInterpreter} has the additional responsibility of
packaging the final result for the @code{Translator}. The translator
instance will retrieve this object through the @code{getResult()}
method. The interpreter only need return a meaningful result if the
input is accepted.

@subsection Translator

A @code{Translator} is an object that abstracts the lower-level
structural analysis; it is the thing the user interacts with.
Internally, a @code{Translator} may or may not use all of the other
components listed above. It manages initialization of the parse, runs
it, and then returns the result @code{Object} to the user.

If the translation is not error-free, a @code{TranslationException} is
thrown. This exception carries out the @code{Auditor} (the repository
for errors and warnings) to the user.

In summary, a simplified schematic for the translation is given below.

@image{components-scheme,,}

@example (1) User invokes @code{translate()} (2) Translator runs the
internal parse loop (3) Translator fetches the result from the
Interpreter (4) Result is returned to the User @end example

Fig 2: Simplified Processing Model

@node Compilation Model, Lexical Context, Definitions, Top @chapter
Compilation Model

When a grammar is compiled, each component in the translator is
constructed.

@section Construction of LRTranslatorGrammar

The @code{LRTranslatorGrammar} is constructed via parsing of either a
file in the @dfn{native syntacs format} or @code{XML}. If the parse is
error-free, additional semantic checks are done to make sure that the
grammar is internally consistent.

@section Construction of Lexical Analyzer

Each context in the @code{LRTranslatorGrammar} is transformed into a
@code{RegularGrammar} instance, which is further transformed to a
@code{DFA}. The @code{DFA} is then transformed (compressed) and
integrated into the @code{Lexer}.

@section Construction of the Syntactic Analyzer

The @code{LRTranslatorGrammar} is used to build a
@code{ContextFreeGrammar} which is then transformed into a
@dfn{deterministic pushdown automaton} (@code{DPA}). The @code{DPA} is
compressed and integrated into the @code{Parser}.

@section Construction of the LRTranslatorInterpreter

The @code{LRTranslatorInterpreter} is implemented by the grammar author
and named in the @code{compile-interpreter-classname} property such that
it may be reflected by the grammar compiler and integrated into the
@code{Translator}. If no such property is given, a default
@code{LRTranslatorInterpreter} is used.

@image{construction,,}

Fig 3: Schematic showing how a @code{Translator} is constructed.

@c =====================================================================

@node Lexical Context, Error Handling, Compilation Model, Top @chapter
Lexical Context

@heading Background

The concept of @dfn{lexer states} was introduced by the flex scanner
generator as a means to subdivide the lexer into multiple layers such
that only a subset of the token definitions can be matched at a
particular time. This is useful when a syntax has widely variable
\`\`topography''; certain sections of a file are syntatically very
different from one another. Attempts to cram all the required regular
expressions into a a single @dfn{lexer state} (a single DFA) result in
collisions within the grammar. Under these circumstances it becomes
advantageous to partition the token definitions into subsets and switch
between them at the appropriate time.

@strong{Note}: I would imagine that the concept of lexer states was
almost certainly known well before flex came along, but my knowledge of
the subject is pretty limited so don't quote me on historical accuracy.

@heading Transition Function

In the @code{STT}, this notion of @dfn{lexer states} is recast into the
term @dfn{lexical context}, or more commonly just @dfn{context}, for
short. Note however its meaning here is unrelated to the term
`context-free parsing''; that is,`lexical context'' does not imply any
sort of \`\`context-ful parsing''. This is because the notion of lexical
context does not affect the theory or algorithms used in the parsing
phase (syntactic analysis).

It is useful to remember the formula that @dfn{one lexical context
equals one DFA}. With this in mind then we can restate what it means to
be a lexical analyzer in the @code{STT}: A lexical analyzer is a machine
that transforms a stream of characters into a stream of tokens; to
accomplish its job it requires a @dfn{set of contexts} and a
@dfn{context transition function}. The transition function is a mapping
from @code{(context, token) --\> context}.

Therefore, when a DFA says \`\`hey, we found the beginning of a
comment!'', the context transition function is consulted to check if the
context needs to be changed.

@heading Context Stack

@code{STT} lexers have an additional feature in that they maintain a
@dfn{context stack}. I fibbed when I said that the transition function
is a mapping from @code{(context, token) --\> context}; it is actually a
mapping @code{(context, token) --\> context action} where the
@code{context action} is a @dfn{context stack instruction}. A context
action is a tuple @code{(instruction, register)} and hence the full
definition for the transition function is a tuple to tuple mapping
@code{(context, token) --\> (instruction, register)}.

The instruction is a constant that is one of:

@itemize @item @strong{@code{PEEK}}: This is the no-op instruction; it
says to change contexts to the one you are currently in, meaning \`\`do
nothing''. The @code{register} for a @code{PEEK} instruction does not
hold a meaningful value. @item @strong{@code{PUSH}}: This instruction
switches to the context named in the @code{register} and places it on
the top of the stack. @item @strong{@code{POP}}: This instruction
removes the top element of the context stack, throws it away, and then
switches into the context at the new top of the stack. This has the
effect of going back to the context you were in before a @code{PUSH}.
The @code{register} for a @code{POP} instruction does not hold a
meaningful value. @end itemize

@heading How to Use Multiple Contexts

To take advantage of multiple lexical contexts you need to declare the
names of the contexts to be used in your grammar. Then, define which
terminals will be included in what context such that the finite automata
can be correctly assembled for each context. The context definition is
also where PUSH or POP instructions are made. @xref{Grammar Syntax} for
more information.

@c =====================================================================

@node Error Handling, Grammar Syntax, Lexical Context, Top @chapter
Error Handling

An important part of any parser generator is error recovery and/or error
repair. The design that the STT uses is that when an error is discovered
by the parser, it asks to the @code{ParserInterpreter} how to recover.
This, in theory, gives the user complete control over error recovery. In
practice, however, only a few maneuvers (called @dfn{Corrections}) are
defined by the parser, but these are sufficient for general use.

@section Auditor

The @dfn{auditor} is the central listener for errors and warnings,
collectively called @dfn{complaints}. The auditor instance is visible to
all translation components. When a @code{TranslationException} is
thrown, the auditor instance is carried out on the back of the exception
to the caller such that it can be printed out or otherwise inspected.

@section Lexical Errors

If the lexer cannot find an appropriate match given the current input,
it will notify the @code{LexerInterpreter} through the @code{error(int
offset, int length)} method with the offset and length of the
unrecognizable sequence. No context switching occurs during a lexical
error.

It is standard behavior for the lexer interpreter to report this to the
@code{Auditor} such that the line is printed, highlighting the syntax
error.

Currently, no lexical error handling is done in the lexer itself. The
overall design, however, allows custom lexical error handling to be
implemented by the author within the @code{LexerInterpreter}.

@section Syntactic Errors

If for a given parser state and input terminal no valid action is known,
the parser will react by calling @code{interpreter.recover(int type,
Sentence left\_context)}. The return value from the
@code{ParserInterpreter} is a @code{Recovery} object (a list of
@code{Correction} actions that the parser will execute in sequence).

This design is relatively flexible because it allows for future
implementation of a number of methods for error recovery. However, only
a few @code{Correction} types have been implemented thus far! This is
partically due to the fact that (1) error recovery is for some reason
one of the last things implemented in these types of projects, (2) the
@dfn{SYNC and TUMBLE} recovery paradigm works nicely and is extremely
simple.

@section Correction Types

@subsection ABORT

When the parser executes an ABORT correction, it bails out by
immediately throwing a @code{TranslationException}. This is useful if a
maximum number of errors have been discovered (such as 100). In this
case the user would be responsible for checking with the auditor how
many errors the translation has encountered.

@subsection SYNC

When the parser executes a SYNC correction, it will discard future input
terminals from the @code{LexerInterpreter} until it sees one that
matches some given type (a semicolon, for example). The terminal named
by the SYNC instruction is called a @dfn{synchronizing symbol}.

Note that the synchronizing symbol is also discarded. When the SYNC
instruction is satisfied, the parser advances to the next correction in
the recovery list.

@subsection TUMBLE

When the parser executes a TUMBLE currection, it iteratively challenges
the current input terminal symbol and the current parse state against
the @code{DPA.action(state, symbol)} method.

If the @code{Action} returned by the DPA is not an ERROR, the TUMBLE
condition is satisfied, the parser executes the given @code{Action}, and
the parser advances to the next correction in the recovery plan. If the
end of the recovery plan has been reached, normal parsing resumes.

Conversely, if the @code{Action} is an ERROR, the parser pops both the
state and symbol stacks, discards their values, and tries another
challenge. In this manner the parser is \`\`tumbling'' down the stack
trying to find a combination that works.

For grammars that have obvious synchronizing tokens, the SYNC and TUMBLE
recovery combo works well to limit the number of errors to a reasonable
number. I hope to implement more error correction types in a future
release as it is an interesting problem. I would also encourage others
to get involved. Your help is needed!

@c =====================================================================

@node Grammar Syntax, Regular Expression Syntax, Error Handling, Top
@chapter Grammar Syntax

A grammar for a translator generated with the @code{STT} is an @dfn{STT
Grammar}. Either the @dfn{native .stt format} or @code{XML} can be used
to express the required structure.

@heading XML Format

Though @code{XML} can be used as to write a grammar, it is far more
verbose than the native stt format. Since the abstract structure of an
@code{XML} grammar instance and an stt grammar instance are
interchangeable, no formal description of the @code{XML} format is
given; consult the @code{DTD} and the examples in the distribution. The
use of XML was basically a bootstrap mechanism. It is still occasionally
required when some part of the translation machinery is broken due to
development, disabling the native pathway.

@code{XML} instances must conform to the @code{grammar.dtd} document
type.

@heading STT Format

A grammar file consists of a set of sections, some of which are
optional. Each section consists of one or more @dfn{statements}
terminated by a semicolon.

Comments and whitespace are discarded. Comments are typical unix-style;
they start with a pound sign (@code{\#}) and end with a newline.

The sections are:

@itemize @item @strong{Grammar Declaration}: defines the grammar name
and version [required]. @item @strong{Property Definitions}: declares
the names and values of various of properties [optional]. @item
@strong{Terminal Declarations}: declares the names of terminals (tokens)
[required]. @item @strong{Terminal Definitions}: assocations between the
terminal names and regular expressions [optional]. @item
@strong{Nonterminal Declarations}: declares the names of nonterminals
[required]. @item @strong{Nonterminal Definitions}: defines the
productions used to control parse stack reductions [required]. @item
@strong{Accept Definition}: defines the nonterminal to be used as the
goal symbol [required]. @item @strong{Context Declarations}: declares
the names of the lexical contexts [optional]. @item @strong{Context
Definitions}: defines what terminals are to be included in what context
[optional]. @item @strong{Start Context Definition}: defines what
context is the initial context [optional]. @end itemize

@section Grammar Declaration

The grammar declaration defines the name of the grammar and the version.
It looks like this:

@example \# format: this is <NAME> version <VERSION>; this is syntacs
version 0.1.0; @end example

@section Property Declarations

Properties are key:value pairs that are put into a hashtable and used
throughout grammar processing. @xref{Properties} for a listing of these
properties. They are enclosed in double-quotes.

@example \# format: property <NAME> = "<VALUE>"; property namespace =
"com.inxar.syntacs.translator.regexp"; @end example

@section Terminal Declarations

Terminals need to be declared before they can be defined. A declaration
establishes that name as a terminal. There may be multiple terminal
statements, each of which may declare multiple names.

Terminals and Nonterminals share the same @dfn{namespace}, meaning there
cannot be a terminal and a nonterminal having the same name. By
convention, terminals identifiers are all caps and nonterminal
identifiers are capitalized, but it is up to the preference of the
grammar author...

@example \# format: terminal <NAME>; \# format: terminal <NAME>, <NAME>,
<NAME>; terminal IDENT; terminal T1, T2; @end example

@section Terminal Definitions

Terminal definitions are regular definitions; they associate a name with
an expression. A regular expression is enclosed in double-quotes;
whitespace within the string is insignificant. @xref{Regular Expression
Syntax} about how regular expressions are written in @code{STT}.

@example \# format: <TERMINAL> matches "regexp"; IDENT matches "
[\_a-zA-Z0-9] [-\_a-zA-Z0-9]\* "; @end example

@section Nonterminal Declarations

Nonterminal declarations are identical to terminal declarations with the
exception of the keyword. Nonterminal identifiers are by convention
capitalized.

@example \# format: nonterminal <NAME>; \# format: nonterminal <NAME>,
<NAME>, <NAME>; nonterminal Goal; nonterminal IdentList, Name,
Statement; @end example

@section Nonterminal Definitions

Nonterminal definitions are productions: each production relates a
nonterminal to a sequence of grammar symbols; when that sequence of
grammar symbols (terminals or nonterminal) appears the top of the parse
stack, the parser will reduce it to the nonterminal named in the
production (i.e. the nonterminal definition).

@example \# format: reduce <NONTERMINAL> when <SYMBOL> <SYMBOL>
<SYMBOL>; reduce Term when Term PLUS Factor; @end example

@section Accept Definition

This section consists of a single statement that states what
`goal symbol'' must be reduced in order for the grammar to signal acceptance of the input.  The goal symbol must be a declared nonterminal.  The convention is`Goal''.

@example \# format: accept when <NONTERMINAL>; accept when Goal; @end
example

@section Context Declarations

The context declarations and definitions are optional. @xref{Lexical
Context} for an explanation of what a \`\`context'' is.

The context declarations section is similar to the terminal declarations
section and nonterminal declarations section.

@example \# format: context <NAME>; \# format: context <NAME>, <NAME>,
<NAME>; context comment; context special1, special2; @end example

Identifiers used for contexts have their own namespace, each one must be
unique only within the set of context declarations. The context names
`default'' and`all'' have special meaning.

@section Context Definitions

A context definition determines what subset of terminals in the full set
of terminals is @dfn{included} in the context. If a terminal is included
within a particular context, its corresponding DFA will recognize the
appropriate character sequence (given the opportunity).

Each context definition statement consists of a context name and a list
of one or more @dfn{context stack instructions}. A stack instruction can
say one of three things:
`when terminal @code{X} is matched, do nothing'',`when terminal @code{X}
is matched, switch into context @code{Y}'', and \`\`when terminal
@code{X} is matched, return to the previous context''.

@itemize @item The default instruction for all terminals in a context
that do not explicitly name an instruction is PEEK, meaning \`\`do not
change context; do nothing''.

@example \# Implicit PEEK instructions for R, S, and T in context
"default". default includes R, S, T; @end example

@item An @code{shifts} instruction changes the lexer context to the
named context.

@example \# Implicit PEEK instruction for X; PUSH for Y in context
"default". default includes X, Y shifts special; @end example

@item An @code{unshifts} instruction changes the lexer context to the
previous context.

@example \# POP instruction for Z in context "special".\
special includes Z unshifts; @end example

@end itemize

The following example demonstrates the use of context switching through
context stack instructions:

@example \# format: <NAME> includes <INSTRUCTION>; terminal WHITESPACE,
START\_COMMENT, COMMENT\_DATA, END\_COMMENT;

context default, comment;

default includes START\_COMMENT shifts comment, WHITESPACE; comment
includes COMMENT\_DATA, END\_COMMENT unshifts; @end example

@section Start Context Definition

This section defines what the starting context will be. When omitted,
the default context is \`\`default''.

@example \# format: start with context <NAME>; start with context
special; @end example

@section Context Post-Processing

After the grammar is parsed, some processing is done to initialize each
context with the terminals that will be included in it.

@heading Case 1: No explicit contexts

The simplest case is when no context information has been explicitly
provided --- the grammar consists of terminal and nonterminal
declarations/definitions only.

In this circumstance, the processor implicitly adds in a single context
\`\`default'', and all terminals are added to that context. The lexer
acts on the corresponding DFA and no context switching is done.

@example terminal WHITESPACE, DATA;

nonterminal data; reduce data when DATA; accept when data @end example

@heading Case 2: One or more explicit contexts, no \`\`all'' context

In this circumstance the user declares more or more contexts. The
\`\`default'' context is always implicitly declared, but can be declared
explicitly with no error.

@example terminal WHITESPACE, DATA, START\_QUOTE, QUOTE\_DATA,
END\_QUOTE;

nonterminal data, quote;

context quoted\_context;

default includes WHITESPACE, DATA, START\_QUOTE shifts quoted\_context;
quoted\_context includes WHITESPACE, QUOTE\_DATA, END\_QUOTE unshifts;
@end example

@heading Case 3: Use of the \`\`all'' context

The
`all'' context is special in that it does not actually refer to a real context (a DFA), but rather is a syntactic convenience.  Terminals included in the`all''
context are placed into every other context after the grammar is parsed.
The all context does not have to be declared.

@example terminal WHITESPACE, DATA, START\_QUOTE, QUOTE\_DATA,
END\_QUOTE;

nonterminal data, quote;

context quoted\_context;

all includes WHITESPACE; default includes DATA, START\_QUOTE shifts
quoted\_context; quoted\_context includes QUOTE\_DATA, END\_QUOTE
unshifts; @end example

@node Regular Expression Syntax, Properties, Grammar Syntax, Top
@chapter Regular Expression Syntax

The regular expression syntax in STT is pretty standard. Whitespace is
never significant, however -- any literal space characters must be
introduced with the space character escape @samp{\s}. Any literal
double-quotation mark @samp{"} must be escaped since regexps are always
enclosed in double-quotations.

@section List Operators

@table @code @item @dfn{Op} @dfn{Definition} @item | Union: a list of
alternate choices that can be matched, like @samp{a|b|c}. @item
@dfn{none} Concatenation: a list of atoms that must be matched in
sequence, like @samp{a b c} or @samp{abc}. @item Character Classes: A
syntactic convenience for alternation of character intervals:
@samp{[\r\n]}, @samp{[a-z]}. Negation of character classes inverts the
sense of the inclusion: @samp{[\^a-z]}. If the dash character @samp{-}
is one of the characters in the class, it must be the first member in
the class: @samp{[-=+]}. Whitespace within the brackets is not
significant and characters that would normally have to be escaped do
not. The ones that do include: backslash @samp{\\}, close-bracket
@samp{}, semicolon @samp{;}, and all the whitespace escapes @samp{\s},
@samp{\r}, @samp{\n}, @samp{\t}, @samp{\v}. Octal and Unicode escapes
can be used as well. @end table

@section Quantification Operators

@table @code @item @dfn{Op} @dfn{Definition} @item \* Closure:
zero-or-more occurrences must exist @item + Positive-closure: one or
more occurrences must exist @item ? Optional: zero-or-one occurrences
must exist @end table

@section Literal Escapes

@table @code @item @dfn{Op} @dfn{Definition} @item \\ literal backslash
@item \s
literal space @item \n
literal newline @item \r
literal carriage return @item \t
literal horizontal tab @item \v
literal vertical tab @item + literal plus sign @item \* literal asterisk
@item ? literal question-mark @item ( literal open-parenthesis @item )
literal close-parenthesis @item [ literal open-bracket @item ] literal
close-bracket @item | literal pipe @item " literal double-quote
(necessary since regexps are enclosed in double quotes)

@end table

@section Octal and Unicode Escapes

Octal and unicode escapes match the following regular expressions,
respectively:

@example OCTAL\_ESCAPE matches " \\ [0-3] [0-7] [0-7] "; UNICODE\_ESCAPE
matches " \\ u [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] [0-9a-fA-F] ";

@end example

@section Precedence

From lowest to highest: union, concatentation, quantification, atom
(char | escape | char-class), grouping.

@section Examples

@example IDENTIFIER matches " [\_a-z] [-\_a-zA-Z0-9] "; WHITESPACE
matches " [\n \r \t \v \s]+ "; BEVERAGE matches " coffee | tea | cola ";
CAFFEINE matches " caff(ei|ie)ne "; @end example

@c =====================================================================

@node Properties, Example Grammar, Regular Expression Syntax, Top
@chapter Properties

Properties are used to pass variables to the grammar processor; they do
not affect the language itself, but assist in the generation process.

@subsection General properties @table @code @item verbose A boolean
value that, if \`\`true'', will write verbose messages to the log. @end
table

@subsection Run-time properties

@table @code @item run-error-limit A number that, if defined, places a
ceiling on the number of errors that can occur during a translation
before aborting. @item run-lexer-debug A boolean value that, if
`true'', will signal the @code{Lexer} to output debugging messages to the log. @item run-parser-debug A boolean value that, if`true'',
will signal the @code{Parser} to output debugging messages to the log.
@item run-interpreter-debug A boolean value that, if
`true'', will signal the @code{LRTranslatorInterpreter} to output debugging messages to the log. @item run-print-parse-tree A boolean value that, if`true'',
will signal the @code{com.inxar.syntacs.Run} class to printout the parse
tree. @end table

@subsection Compile-time properties that should be in every grammar

@table @code @item author A string that names the author or authors of
the grammar. @item author-email A string that names the email
address/addresses of the author/authors. @item copyright A string that
will be included in generated files. Defaults to "Copyright \$YEAR
\$AUTHOR, \$AUTHOR\_EMAIL". @item compile-namespace A string that names
the package membership for the generated classes. Example:
`com.inxar.syntacs.translator.test''. @item compile-sourcepath A string that names the filesystem directory where the generated classes should be written.  Example:`./src''.
@item compile-dpa-constructor-method A string that names the strength of
the algorithm used to generate the DPA (the parse tables). Can be one of
@code{lalr1}, @code{lr1}, @code{slr1}. The default is lalr1, so this
property is NOT NECESSARY for those grammars that choose LALR1. @item
compile-interpreter-classname A string that names the class to be used
by the generated translator. Example:
`com.inxar.syntacs.translator.regexp.RegexpInterpreter''.  This is the thing you have to implement for your grammar.  The default value is`com.inxar.syntacs.translator.lr.StandardLRInterpreter'',
which builds a concrete syntax tree and is useful for testing. @end
table

@subsection Compile-time properties that affect class generation
(development use)

@table @code @item compile A boolean value that, if
`false'', will prevent any implementation classes from being generated and written to the filesystem. @item compile-lexical A boolean value that, if`false'',
will prevent any lexical analysis classes from being generated and
written to the filesystem. @item compile-syntactic A boolean value that,
if
`false'', will prevent the syntax analysis classes from being generated and written to the filesystem. @item compile-grammar A boolean value that, if`false'',
will prevent the @code{LRTranslatorGrammar} class from being generated
and written to the filesystem. @item compile-pickle A boolean value
that, if
`false'', will prevent classes from using an int array packing technique called`pickling''
(decreases bytecode size). The code seems stable, so this should not be
needed. @item compile-pickle-dfa A boolean value that, if
`false'', will prevent pickling of generated DFA classes. @item compile-pickle-dpa A boolean value that, if`false'',
will prevent pickling of generated DPA classes. @end table

@subsection Compile-time properties that customize the translation
components

@table @code @item compile-input-classname A string that names the class
which implements @code{Input} that will be used by the generated
translator at run-time. @item compile-lexer-classname A string that
names the class which implements @code{Lexer} that will be used by the
generated translator at run-time. @item compile-parser-classname A
string that names the class which implements @code{Parser} that will be
used by the generated translator at run-time. @item
compile-interpreter-classname A string that names the class which
implements @code{LRTranslatorInterpreter} that will be used by the
generated translator at run-time. @item
compile-dpa-constructor-classname A string that names the class which
implements @code{DPAConstructor} that will be used by the grammar
compiler at compile-time to construct a DPA. @end table

@subsection Compile-time debugging properties

@table @code @item compile-grammar-regular-debug A boolean value that,
if
`true'', will printout the regular grammars. @item compile-grammar-context-free-debug A boolean value that, if`true'',
will printout the context-free grammar. @item compile-dfa-debug A
boolean value that, if
`true'', will printout the grammar DFAs. @item compile-dpa-debug A boolean value that, if`true'',
will printout the grammar DPA. @end table

@subsection Compile-time properties that control GraphViz output

@table @code @item viz A boolean value that, if
`false'', will prevent any generation of GraphViz dot files. @item viz-lexical A boolean value that, if`true'',
will generate graphviz .dot files for DFA instances. @item viz-syntactic
A boolean value that, if \`\`true'', will generate graphviz .dot files
for DPA instances. @item viz-namespace A string that names the package
membership for the generated dot files. Defaults to value of
@file{compile-namespace}. @item viz-sourcepath A string that names the
filesystem directory where the generated dot files should be written.
Defaults to value of @file{compile-sourcepath}.

@item viz-dfa-size A string that names the size of the postscript
bounding box for the DPA graph (see graphviz manual). @item
viz-dfa-rankdir A string that names the rank direction of the DFA graph
(see graphviz manual). @item viz-dfa-concentrate-edges A boolean that,
if \`\`true'', will heuristically merge edges when appropriate (see
graphviz manual). @item viz-dfa-node-color A string that controls the
color of the DFA nodes (see graphviz manual). @item viz-dfa-node-shape A
string that controls the shape of the DFA nodes (see graphviz manual).
@item viz-dfa-edge-color A string that controls the color of the DFA
edges (see graphviz manual). @item viz-dfa-edge-style A string that
controls the style of the DFA edges (see graphviz manual). @item
viz-dfa-label-edge-color A string that controls the color of the DFA
label edges (see graphviz manual). @item viz-dfa-label-edge-style A
string that controls the style of the DFA label edges (see graphviz
manual).

@item viz-dpa-size A string that names the size of the postscript
bounding box for the DPA graph (see graphviz manual). @item
viz-dpa-rankdir A string that names the rank direction of the DPA graph
(see graphviz manual). @item viz-dpa-concentrate-edges A boolean that,
if \`\`true'', will heuristically merge edges when appropriate (see
graphviz manual). @item viz-dpa-node-color A string that controls the
color of the DPA nodes (see graphviz manual). @item viz-dpa-node-shape A
string that controls the shape of the DPA nodes (see graphviz manual).

@item viz-dpa-hide-terminal-edges A boolean that, if \`\`true'', will
prevent visualization of terminal transitions. @item
viz-dpa-terminal-edge-color A string that controls the color of the DPA
terminal edges (see graphviz manual). @item viz-dpa-terminal-edge-style
A string that controls the style of the DPA terminal edges (see graphviz
manual).

@item viz-dpa-hide-nonterminal-edges A boolean that, if \`\`true'', will
prevent visualization of nonterminal transitions. @item
viz-dpa-terminal-edge-color A string that controls the color of the DPA
nonterminal edges (see graphviz manual). @item
viz-dpa-terminal-edge-style A string that controls the style of the DPA
nonterminal edges (see graphviz manual).

@item viz-dpa-hide-loopback-edges A boolean that, if \`\`true'', will
prevent visualization of loopback edges (closely related to reductions).
@item viz-dpa-loopback-edge-color A string that controls the color of
the DPA loopback edges (see graphviz manual). @item
viz-dpa-loopback-edge-style A string that controls the style of the DPA
loopback edges (see graphviz manual).

@end table

A perusal of the source code might uncover undocumented properties.

@c =====================================================================

@node Example Grammar, Graphviz, Properties, Top @chapter Example
Grammar

The regular expression grammar will be used as an example.

@section The Grammar

@example

GRAMMAR DECLARATION
===================

this is regexp version 0.1.0;

PROPERTY DEFINITIONS
====================

property compile-sourcepath = "./src"; property compile-namespace =
"com.inxar.syntacs.translator.regexp"; property
compile-interpreter-classname =
"com.inxar.syntacs.translator.regexp.RegexpInterpreter";

TERMINAL DECLARATIONS
=====================

terminal WHITESPACE; terminal CHAR; terminal CHAR\_CLASS\_CHAR; terminal
PIPE; terminal STAR; terminal QUESTION; terminal PLUS; terminal
OPEN\_PAREN; terminal CLOSE\_PAREN; terminal OPEN\_BRACKET; terminal
OPEN\_BRACKET\_CARET; terminal OPEN\_BRACKET\_DASH; terminal
OPEN\_BRACKET\_CARET\_DASH; terminal CLOSE\_BRACKET; terminal
CHAR\_CLASS\_DASH; terminal ESC\_PIPE; terminal ESC\_STAR; terminal
ESC\_QUESTION; terminal ESC\_PLUS; terminal ESC\_OPEN\_PAREN; terminal
ESC\_CLOSE\_PAREN; terminal ESC\_OPEN\_BRACKET; terminal
ESC\_CLOSE\_BRACKET; terminal ESC\_BACKSLASH; terminal ESC\_SPACE;
terminal ESC\_TAB; terminal ESC\_VERTICAL\_TAB; terminal ESC\_CR;
terminal ESC\_LF; terminal ESC\_OCTAL; terminal ESC\_UNICODE;

TERMINAL DEFINITIONS
====================

WHITESPACE matches "(\t|\n|\v|\r|\s)+"; CHAR matches
"[\^\\\\|()[\\]*+?]"; CHAR\_CLASS\_CHAR matches "[\^-\\]\\]"; PIPE
matches "|"; STAR matches "\*"; QUESTION matches "?"; PLUS matches "+";
OPEN\_PAREN matches "("; CLOSE\_PAREN matches ")"; OPEN\_BRACKET matches
"([(\t|\n|\v|\r|\s)*)"; OPEN\_BRACKET\_CARET matches
"([(\t|\n|\v|\r|\s)*\^)"; OPEN\_BRACKET\_DASH matches
"([(\t|\n|\v|\r|\s)*-)"; OPEN\_BRACKET\_CARET\_DASH matches
"([(\t|\n|\v|\r|\s)*\^(\t|\n|\v|\r|\s)*-)"; CLOSE\_BRACKET matches "]";
CHAR\_CLASS\_DASH matches "(-)"; ESC\_PIPE matches "(\\|)"; ESC\_STAR
matches "(\\\*)"; ESC\_QUESTION matches "(\\?)"; ESC\_PLUS matches
"(\\+)"; ESC\_OPEN\_PAREN matches "(\\()"; ESC\_CLOSE\_PAREN matches
"(\\))"; ESC\_OPEN\_BRACKET matches "(\\[)"; ESC\_CLOSE\_BRACKET matches
"(\\])"; ESC\_BACKSLASH matches "(\\\\)"; ESC\_SPACE matches "(\\s)";
ESC\_TAB matches "(\\t)"; ESC\_VERTICAL\_TAB matches "(\\v)"; ESC\_CR
matches "(\\r)"; ESC\_LF matches "(\\n)"; ESC\_OCTAL matches
"(\\0[0-3][0-7][0-7])"; ESC\_UNICODE matches
"(\\u[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F])";

NONTERMINAL DECLARATIONS
========================

nonterminal Goal; nonterminal Union; nonterminal Concat; nonterminal
Term; nonterminal Quantifier; nonterminal Atom; nonterminal CharClass;
nonterminal CharClassBegin; nonterminal CharClassTermList; nonterminal
CharClassTerm; nonterminal CharClassAtom;

NONTERMINAL DEFINITIONS
=======================

reduce Goal when Union; reduce Union when Concat; reduce Union when
Union PIPE Concat; reduce Concat when Term; reduce Concat when Concat
Term; reduce Term when Atom; reduce Term when Atom Quantifier; reduce
Quantifier when STAR; reduce Quantifier when PLUS; reduce Quantifier
when QUESTION; reduce Atom when CHAR; reduce Atom when ESC\_BACKSLASH;
reduce Atom when ESC\_PIPE; reduce Atom when ESC\_PLUS; reduce Atom when
ESC\_STAR; reduce Atom when ESC\_QUESTION; reduce Atom when
ESC\_OPEN\_BRACKET; reduce Atom when ESC\_CLOSE\_BRACKET; reduce Atom
when ESC\_OPEN\_PAREN; reduce Atom when ESC\_CLOSE\_PAREN; reduce Atom
when ESC\_SPACE; reduce Atom when ESC\_TAB; reduce Atom when
ESC\_VERTICAL\_TAB; reduce Atom when ESC\_CR; reduce Atom when ESC\_LF;
reduce Atom when ESC\_OCTAL; reduce Atom when ESC\_UNICODE; reduce Atom
when CharClass; reduce Atom when OPEN\_PAREN Union CLOSE\_PAREN; reduce
CharClass when CharClassBegin CharClassTermList CLOSE\_BRACKET; reduce
CharClass when OPEN\_BRACKET\_CARET\_DASH CLOSE\_BRACKET; reduce
CharClassBegin when OPEN\_BRACKET; reduce CharClassBegin when
OPEN\_BRACKET\_CARET; reduce CharClassBegin when OPEN\_BRACKET\_DASH;
reduce CharClassBegin when OPEN\_BRACKET\_CARET\_DASH; reduce
CharClassTermList when CharClassTerm; reduce CharClassTermList when
CharClassTermList CharClassTerm; reduce CharClassTerm when
CharClassAtom; reduce CharClassTerm when CharClassAtom CHAR\_CLASS\_DASH
CharClassAtom; reduce CharClassAtom when CHAR\_CLASS\_CHAR; reduce
CharClassAtom when ESC\_BACKSLASH; reduce CharClassAtom when
ESC\_CLOSE\_BRACKET; reduce CharClassAtom when ESC\_SPACE; reduce
CharClassAtom when ESC\_TAB; reduce CharClassAtom when
ESC\_VERTICAL\_TAB; reduce CharClassAtom when ESC\_CR; reduce
CharClassAtom when ESC\_LF; reduce CharClassAtom when ESC\_OCTAL; reduce
CharClassAtom when ESC\_UNICODE;

accept when Goal;

CONTEXT DECLARATIONS
====================

context default; context charclass;

CONTEXT DEFINITIONS
===================

default includes WHITESPACE, CHAR, PIPE, STAR, QUESTION, PLUS,
OPEN\_PAREN, CLOSE\_PAREN, ESC\_PIPE, ESC\_QUESTION, ESC\_STAR,
ESC\_PLUS, ESC\_OPEN\_PAREN, ESC\_CLOSE\_PAREN, ESC\_OPEN\_BRACKET,
ESC\_BACKSLASH, ESC\_SPACE, ESC\_TAB, ESC\_VERTICAL\_TAB, ESC\_CR,
ESC\_LF, ESC\_OCTAL, ESC\_UNICODE, ESC\_CLOSE\_BRACKET, OPEN\_BRACKET
shifts charclass, OPEN\_BRACKET\_CARET shifts charclass,
OPEN\_BRACKET\_DASH shifts charclass, OPEN\_BRACKET\_CARET\_DASH shifts
charclass;

charclass includes WHITESPACE, CHAR\_CLASS\_CHAR, CHAR\_CLASS\_DASH,
ESC\_BACKSLASH, ESC\_SPACE, ESC\_TAB, ESC\_VERTICAL\_TAB, ESC\_CR,
ESC\_LF, ESC\_OCTAL, ESC\_UNICODE, ESC\_CLOSE\_BRACKET, CLOSE\_BRACKET
unshifts;

start in context default; @end example

Several things to note in the grammar:

@itemize @item There are multiple contexts; character classes have their
own set of regular definitions that are not shared across all contexts.
@item Whitespace and a number of ESC\_xxx tokens are members of both
contexts. @item @code{CLOSE\_BRACKET} returns the lexer the to the
previous lexical context. @end itemize

@section The TranslatorGrammar

Notice that the grammar is @dfn{code-free}; it does not contain any Java
code. This means that user-defined code has to go somewhere else. The
paradigm used by the STT is to sandwich an object between the lexer and
the parser that is reponsible for interpreting lexer match events. A
similar interface exists between the parser and the parser-interpreter
with reduction events.

Terminal match events are signaled to the @code{LexerInterpreter}
through the @code{match(int terminal\_type, int offset, int length}
method. The first argument identifies what kind of terminal (i.e. token)
was matched, represented as a number. Therefore, each terminal is given
a unique number that identifies it at the time of declaration.

@quotation Note: since terminal declarations are separate from terminal
definitions (the regular expression), this means that some terminals may
never be signaled by the lexer. The rationale behind this is to allow
the user more flexibility in how they want to structure their grammar:
some authors might prefer to match all keywords using a single regular
expression, do a symbol lookup, then pass along the appropriate terminal
constant to the parser. This has the potential to have much smaller
lexer transition tables.

Therefore, the set of terminals known to the lexer and the set of
terminals known to the parser may be non-equal. @end quotation

Unique numbers are assigned to each terminal, nonterminal, and
production in the grammar. When the grammar is read by the syntacs
processor, the first thing it generates is the @code{TranslatorGrammar}.
This class contains all the context, terminal, nonterminal, and
production constants as well as functions like @code{getTerminal(int
ID)} that returns the name of a terminal by number.

These constants are used in the switch statements of the
@code{LexerInterpreter} and @code{ParserInterpreter} implementations.

The @code{TranslatorGrammar} object is also a factory for
@code{Translator} instances of that grammar. Therefore, to get an
instance of a @code{Translator} that parses regular expressions:

@example TranslatorGrammar tg = new
com.inxar.syntacs.translator.regexp.RegexpGrammar(); Translator t =
tg.newTranslator(); @end example

To generate the translator grammar, run the syntacs compiler on the
grammar:

@example [user@host]\$ sttc regexp.stt @end example

@section The LexerInterpreter

Once you have the @code{TranslatorGrammar} and the grammar constants,
you can write the code for the @code{LexerInterpreter} and
@code{ParserInterpreter}.

For the @code{LexerInterpreter}, the key method to implement is
@code{match(int terminal\_type, int offset, int length}. Within this
method the @code{LexerInterpreter} presumably does something significant
with the output of the lexer.

For our purposes, the @code{LexerInterpreter} will generate new
@code{Symbol} instances and pass them to the parser. The @code{Symbol}
interface defines a method @code{getSymbolType()} that identifies it as
a member of the context-free grammar which the parser was built to
recognize.

Here is ther salient code:

@example public void match(int type, int off, int len) { Symbol symbol =
null;

        switch (type) {

    case RegexpGrammar.T_WHITESPACE:
        return;

    case RegexpGrammar.T_PIPE:
    case RegexpGrammar.T_OPEN_PAREN:
    case RegexpGrammar.T_CLOSE_PAREN:
    case RegexpGrammar.T_CHAR_CLASS_DASH:
    case RegexpGrammar.T_CLOSE_BRACKET:
    case Token.STOP: 
        symbol = new ConstantSymbol(type); 
        break;

    case RegexpGrammar.T_CHAR:
    case RegexpGrammar.T_CHAR_CLASS_CHAR:
        if (len != 1)
        throw new InternalError("Expected token to be only one character.");
        symbol = newAtom( type, in.retch(off) );
        break;

    case RegexpGrammar.T_ESC_OCTAL:
        {
        char c = (char)0;
        try {
            c = (char)Integer.parseInt( in.stretch(off + 1, len - 1), 8 );
        } catch (NumberFormatException nfex) {
            trace("NumberFormatException caught while trying to parse "+
              in.stretch(off + 1, len - 1));
            nfex.printStackTrace();
        }
        symbol = newAtom(type, c);
        break;
        }

    case RegexpGrammar.T_ESC_UNICODE:
        {
        char c = (char)0;
        try {
            c = (char)Integer.parseInt( in.stretch(off + 2, len - 2), 16 );
        } catch (NumberFormatException nfex) {
            nfex.printStackTrace();
        }
        symbol = newAtom(type, c);
        break;
        }

    case RegexpGrammar.T_ESC_SPACE:         symbol = newAtom(type, ' '); break;
    case RegexpGrammar.T_ESC_TAB:           symbol = newAtom(type, '\t'); break;
    case RegexpGrammar.T_ESC_VERTICAL_TAB:  symbol = newAtom(type, '\013'); break;
    case RegexpGrammar.T_ESC_CR:            symbol = newAtom(type, '\r'); break;
    case RegexpGrammar.T_ESC_LF:            symbol = newAtom(type, '\n'); break;
    case RegexpGrammar.T_ESC_BACKSLASH:     symbol = newAtom(type, '\\'); break;
    case RegexpGrammar.T_ESC_CLOSE_BRACKET: symbol = newAtom(type, ']'); break;
    case RegexpGrammar.T_ESC_PIPE:          symbol = newAtom(type, '|'); break;
    case RegexpGrammar.T_ESC_QUESTION:      symbol = newAtom(type, '?'); break;
    case RegexpGrammar.T_ESC_STAR:          symbol = newAtom(type, '*'); break;
    case RegexpGrammar.T_ESC_PLUS:          symbol = newAtom(type, '+'); break;
    case RegexpGrammar.T_ESC_OPEN_BRACKET:  symbol = newAtom(type, '['); break;
    case RegexpGrammar.T_ESC_OPEN_PAREN:    symbol = newAtom(type, '('); break;
    case RegexpGrammar.T_ESC_CLOSE_PAREN:   symbol = newAtom(type, ')'); break;

    case RegexpGrammar.T_OPEN_BRACKET:            
        symbol = newClass(type, false, false); break;
    case RegexpGrammar.T_OPEN_BRACKET_CARET:      
        symbol = newClass(type, true, false); break;
    case RegexpGrammar.T_OPEN_BRACKET_DASH:       
        symbol = newClass(type, false, true); break;
    case RegexpGrammar.T_OPEN_BRACKET_CARET_DASH: 
        symbol = newClass(type, true, true); break;

    case RegexpGrammar.T_STAR:     
        symbol = new QuantifierSymbol(type, Regexp.CLOSURE); break;
    case RegexpGrammar.T_QUESTION: 
        symbol = new QuantifierSymbol(type, Regexp.OPTIONAL); break;
    case RegexpGrammar.T_PLUS:     
        symbol = new QuantifierSymbol(type, Regexp.PCLOSURE); break;

    default: 
        throw new InternalError
        ("Expected terminal type: " + grammar.getTerminal(type));
        }

    parser.notify(symbol);
    }

    private Symbol newAtom(int type, char value)
    {
    RegexpAtom atom = new RegexpAtom();
    atom.setValue( value );
    atom.setSymbolType(type);
    return atom;
    }

    private Symbol newClass(int type, boolean isNegated, boolean hasDash)
    {
    CharClassBeginSymbol ccbs = new CharClassBeginSymbol(type);
    ccbs.isNegated = isNegated;
    ccbs.hasDash = hasDash;
    return ccbs;
    }

@end example

Some points from the code:

@itemize @item WHITESPACE is ignored by returning immediately; no symbol
is passed to the parser. @item Several different @code{Symbol}
implementations are used: @code{ConstantSymbol} requires no interaction
with the @code{Input}, other types need little to no interaction with
the @code{Input}. This minimizes unnecessary arraycopying in the
tightest part of the parse loop. @item Multiple different cases can be
aggregated to the same switch block. @item The last statement passes the
symbol along to the parser. @end itemize

@section The ParserInterpreter

The @code{ParserInterpreter} is implemented in a similar manner: the
reduce method is generally a big switch block that handles each
production. The @code{ParserInterpreter} can do whatever it wants with
the symbols that are currently on the top of the parse stack --- if they
are to be retained in the parse tree, the @code{ParserInterpreter} must
fetch it from the stack and include it into the nonterminal symbol that
will be returned to the parser.

The @code{Sentence} object abstracts the part of the parse stack that is
being reduced such that indices into the stack refer to the expected
component of the production. For example, when the production
@samp{P\_CharClass\_\_CharClassBegin\_CharClassTermList\_CLOSE\_BRACKET}
is being reduced, @code{sentence.at(0)} accesses the
@samp{CharClassBegin} symbol, @code{sentence.at(1)} accesses
@samp{CharClassTermList}, and @code{sentence.at(2)} accesses
@samp{CLOSE\_BRACKET}.

@example public Symbol reduce(int type, Sentence s) { Symbol symbol =
null;

    switch (type) {

    case RegexpGrammar.P_Quantifier__STAR:
    case RegexpGrammar.P_Quantifier__PLUS:
    case RegexpGrammar.P_Quantifier__QUESTION:
    case RegexpGrammar.P_Term__Atom:
    case RegexpGrammar.P_Atom__CHAR:
    case RegexpGrammar.P_Atom__ESC_BACKSLASH:
    case RegexpGrammar.P_Atom__ESC_PIPE:
    case RegexpGrammar.P_Atom__ESC_PLUS:
    case RegexpGrammar.P_Atom__ESC_STAR:
    case RegexpGrammar.P_Atom__ESC_QUESTION:
    case RegexpGrammar.P_Atom__ESC_OPEN_BRACKET:
    case RegexpGrammar.P_Atom__ESC_CLOSE_BRACKET:
    case RegexpGrammar.P_Atom__ESC_OPEN_PAREN:
    case RegexpGrammar.P_Atom__ESC_CLOSE_PAREN:
    case RegexpGrammar.P_Atom__ESC_SPACE:
    case RegexpGrammar.P_Atom__ESC_TAB:
    case RegexpGrammar.P_Atom__ESC_VERTICAL_TAB:
    case RegexpGrammar.P_Atom__ESC_CR:
    case RegexpGrammar.P_Atom__ESC_LF:
    case RegexpGrammar.P_Atom__ESC_OCTAL:
    case RegexpGrammar.P_Atom__ESC_UNICODE:
    case RegexpGrammar.P_Atom__CharClass:
    case RegexpGrammar.P_CharClassTerm__CharClassAtom:
    case RegexpGrammar.P_CharClassAtom__CHAR_CLASS_CHAR:
    case RegexpGrammar.P_CharClassAtom__ESC_BACKSLASH:
    case RegexpGrammar.P_CharClassAtom__ESC_CLOSE_BRACKET:
    case RegexpGrammar.P_CharClassAtom__ESC_SPACE:
    case RegexpGrammar.P_CharClassAtom__ESC_TAB:
    case RegexpGrammar.P_CharClassAtom__ESC_VERTICAL_TAB:
    case RegexpGrammar.P_CharClassAtom__ESC_CR:
    case RegexpGrammar.P_CharClassAtom__ESC_LF:
    case RegexpGrammar.P_CharClassAtom__ESC_OCTAL:
    case RegexpGrammar.P_CharClassAtom__ESC_UNICODE:
    case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET:
    case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET_CARET:
    case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET_DASH:
    case RegexpGrammar.P_CharClassBegin__OPEN_BRACKET_CARET_DASH:
        {
        symbol =  s.at(0);
        break;
        }

    case RegexpGrammar.P_Union__Concat:
        {
        RegexpList union = new RegexpList(Regexp.UNION);
        union.addRegexp( (Regexp)s.at(0) );
        symbol = union;
        break;
        }

    case RegexpGrammar.P_Concat__Term:
        {
        RegexpList concat = new RegexpList(Regexp.CONCAT);
        concat.addRegexp( (Regexp)s.at(0) );
        symbol = concat;
        break;
        }

    case RegexpGrammar.P_Concat__Concat_Term:
        {
        RegexpList list = (RegexpList)s.at(0);
        list.addRegexp( (Regexp)s.at(1) );
        symbol = list;
        break;
        }

    case RegexpGrammar.P_Union__Union_PIPE_Concat:
        {
        RegexpList list = (RegexpList)s.at(0);
        list.addRegexp( (Regexp)s.at(2) );
        symbol = list;
        break;
        }

    case RegexpGrammar.P_CharClassTermList__CharClassTerm:
        {
        symbol = new ListSymbol(type, s.at(0));
        break;
        }

    case RegexpGrammar.P_CharClassTermList__CharClassTermList_CharClassTerm:
        {
        ListSymbol sym = (ListSymbol)s.at(0);
        sym.list.add(s.at(1));
        symbol = sym;
        break;
        }

    case RegexpGrammar.P_Term__Atom_Quantifier:
        {
        QuantifierSymbol q = (QuantifierSymbol)s.at(1); 
        RegexpTerm term = new RegexpTerm(q.regexpType);
        term.setInternal( (Regexp)s.at(0) );
        symbol = term;
        break;
        }

    case RegexpGrammar.P_Atom__OPEN_PAREN_Union_CLOSE_PAREN:
        {
        RegexpTerm term = new RegexpTerm(Regexp.GROUP);
        term.setInternal( (Regexp)s.at(1) );
        symbol = term;
        break;
        }

    case RegexpGrammar.P_CharClassTerm__CharClassAtom_CHAR_CLASS_DASH_CharClassAtom:
        {
        RegexpAtom lo = (RegexpAtom)s.at(0);
        RegexpAtom hi = (RegexpAtom)s.at(2);
        symbol = new RegexpRange(lo, hi);
        break;
        }

    case RegexpGrammar.P_CharClass__CharClassBegin_CharClassTermList_CLOSE_BRACKET:
        {
        RegexpCharClass cc = new RegexpCharClass();
        CharClassBeginSymbol ccbs = (CharClassBeginSymbol)s.at(0);
        cc.isNegated(ccbs.isNegated);
        cc.hasDash(ccbs.hasDash);
        List list = ((ListSymbol)s.at(1)).list;
        cc.setList(list);
        symbol = cc;
        break;
        }

    case RegexpGrammar.P_CharClass__OPEN_BRACKET_CARET_DASH_CLOSE_BRACKET:
        {
        RegexpCharClass cc = new RegexpCharClass();
        CharClassBeginSymbol ccbs = (CharClassBeginSymbol)s.at(0);
        cc.isNegated(ccbs.isNegated);
        cc.hasDash(false);

        RegexpAtom dash_atom = new RegexpAtom();
        dash_atom.setValue('-');
        List list = new ArrayList();
        list.add(dash_atom);

        cc.setList(list);
        symbol = cc;
        break;
        }

    case RegexpGrammar.P_Goal__Union:
        {
        Regexp regexp = (Regexp)s.at(0);
        this.regexp = regexp;
        symbol = regexp;
        break;
        }

    default:
            throw new InternalError("Unknown Production: "+grammar.getProduction(type));
    }

    return symbol;
    }

@end example

@itemize @item Each case picks out the relevant @code{Symbol} objects
out of the parse stack through the @code{Sentence} interface (the top of
the parse stack). @item Multiple cases aggregate to the same code; they
are handled the same. @item

@end itemize

Once again, the @code{LexerInterpreter} and @code{ParserInterpreter} is
typically the same object that implements
@code{LRTranslatorInterpreter}. Once this object has been defined, you
can set the property:

@example property compile-interpreter-classname =
"com.inxar.syntacs.translator.regexp.RegexpInterpreter"; @end example

And regenerate the translator. At this point the translator can be used:

@example TranslatorGrammar tg = new
com.inxar.syntacs.translator.regexp.RegexpGrammar(); Translator t =
tg.newTranslator();

StringReader in = new StringReader("a|b|c");

try { Regexp regexp = (Regexp)translator.translate(in); } catch
(TranslationException ex) { ex.printStackTrace(); } @end example

@c =====================================================================

@node Graphviz, Resources, Example Grammar, Top @chapter Graphviz

Certain pieces of the STT such as the finite automata and pushdown
automata are capable of writing themselves as
@uref{http://www.research.att.com/sw/tools/graphviz/,Graphviz} @dfn{dot}
files. These text files can then be converted to postscript using the
tools in the graphviz distribution and further converted to various
image formats using freely available postscript/image conversion tools.
My machine has @uref{http://www.cs.wisc.edu/\~ghost/, Ghostscript} and
@uref{http://www.imagemagick.org/,ImageMagick} installed and I have been
very pleased with them.

To generate graphviz files, you need to add a few properties to the
grammar and recompile it. The @file{.dot} files will be written where
the generated classes are (can override this with the
@code{viz-namespace} and @code{vis-sourcepath} properties). To tell the
processor to write the files, say:

@example property viz-lexical = "true"; property viz-syntactic = "true";
@end example

There are several properties which customize the dimensions, colors, and
styles of the generated files. Note that visualization of all but the
most trivial of grammars is difficult at best since the complexity of
the graphs and the size of the images become prohibitive. You'll find
yourself wanting to visualize those graphs anyway since it's awfully
cool, but you'll only get a gestalt view. Try fiddling with the graph
size with the @code{vis-dfa-size} and @code{viz-dpa-size} properties
(see graphviz manual).

@strong{Note}: The license for GraphViz is \`\`open-source'', but it's
not @uref{http://www.gnu.org/philosophy/free-sw.html, free} -- caveat
emptor, and for that matter, caveat vendor.

@c =====================================================================

@node Resources, , Graphviz, Top @chapter Resources

@section Books

@table @asis @item
@uref{http://images.amazon.com/images/P/0201100886.01.LZZZZZZZ.gif,
Compilers - Principles Techniques and Tools} The classic compiler text
by
@uref{http://www.columbia.edu/cu/record/archives/vol21/vol21\_iss5/record2105.14c.gif,
Aho}, @uref{http://cm.bell-labs.com/who/ravi/, Sethi}, and
@uref{http://www-db.stanford.edu/\~ullman/, Ullman} commonly known as
the @dfn{Red Dragon Book}, or just @dfn{Dragon Book}. Highly recommended
for a substantive introduction to automata and parsing theory.

@item
@uref{http://images.amazon.com/images/P/0201441241.01.LZZZZZZZ.jpg,
Introduction to Automata Theory, Languages, and Computation} Despite its
self-declaration of triviality in the title, this one by
@uref{http://www.cs.cornell.edu/annual\_report/1997/hopcroft.htm,
Hopcroft} and Ullman is less introductory than others, if you ask me.
Still, it's a great book. The link above is to the second edition --- I
have the first edition that has a completely different graphic on the
cover. I can see why they changed it, but I kind-of wished they hadn't.

@item @uref{http://www.oreilly.com/catalog/regex/, Mastering Regular
Expressions} While not a theoretical book, it is useful for practical
learning about how to write a regular expression.

@item
@uref{http://www.cs.ualberta.ca/research/library/0-07-065161-2.html, The
Theory and Practice of Compiler Writing} This book by
@uref{http://www.cs.usask.ca/faculty/tremblay/, Jean-Paul Tremblay} and
@uref{http://www.cs.ualberta.ca/people/faculty/sorenson.html, Paul
Sorenson} has a good alternate description of the DeRemer-Pennello LALR1
construction algorithm. Out of print, but you can probably find it at
your local univerity computer science library. Please tell me he didn't
actually shoot that beautiful animal.

@item @uref{http://www.oreilly.com/catalog/lex/, Lex & Yacc} A good
overview of the classic lexer and parser compiler duo by Levine, Mason,
and Brown.

@end table

@section Articles

@table @asis @item
@uref{http://www.acm.org/pubs/citations/journals/toplas/1982-4-4/p615-deremer/,
Efficient Computation of LALR(1) look-ahead sets} F. L. DeRemer and T.
Pennello, ACM Transactions on Programming Languages and Systems, October
1982. The canonical reference to the LALR1 construction algorithm used
by this parser generator. Note their analysis of the relationship of
SLR(1) and NQLALR(1) described in this paper is flawed (see \`\`On the
(non-)Relationship between SLR(1) and NQLALR(1) Grammars.'' M. E.
Bermudez and K. M. Schimpf, ACM Transactions on Programming Languages
and Systems, April 1988).

@end table

@c @printindex cp

@contents @bye
