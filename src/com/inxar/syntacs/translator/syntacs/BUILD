package(default_visibility = ["//visibility:public"])

java_library(
    name = "syntacs",
    srcs = [
        "SyntacsGrammar.java",
        "SyntacsInterpreter.java",
    ],
    deps = [
        "dfa",
        "dpa",
        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/analyzer:input",
        "//src/com/inxar/syntacs/analyzer/lexical",
        "//src/com/inxar/syntacs/analyzer/syntactic",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/translator/lr:grammar",
        "//src/com/inxar/syntacs/translator/lr:interpreter",
        "//src/com/inxar/syntacs/translator/lr:translator",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:string",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "dfa",
    srcs = [
        "SyntacsDefaultDFA.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/util:pickler",
        "//src/org/inxar/syntacs/automaton/finite",
    ],
)

java_library(
    name = "dpa",
    srcs = [
        "SyntacsDPA.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:pickler",
    ],
)
