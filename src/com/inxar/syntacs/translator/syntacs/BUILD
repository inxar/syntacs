package(default_visibility = ["//visibility:public"])

java_library(
    name = "syntacs",
    srcs = [
        "SyntacsGrammar.java",
        "SyntacsInterpreter.java",
    ],
    deps = [
        "dpa",
        "dfa",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/analyzer:input",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/analyzer/lexical",
        "//src/com/inxar/syntacs/analyzer/syntactic",
        "//src/com/inxar/syntacs/translator/lr:translator",
        "//src/com/inxar/syntacs/translator/lr:interpreter",
        "//src/com/inxar/syntacs/translator/lr:grammar",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:string",
    ],
)


java_library(
    name = "dfa",
    srcs = [
        "SyntacsDefaultDFA.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/util:pickler",
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
