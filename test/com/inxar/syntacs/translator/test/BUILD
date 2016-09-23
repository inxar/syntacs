package(default_visibility = ["//visibility:public"])

java_library(
    name = "abb",
    srcs = [
        "AbbGrammar.java",
        "AbbDPA.java",
        "AbbDefaultDFA.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/analyzer:input",
        "//src/com/inxar/syntacs/analyzer/lexical",
        "//src/com/inxar/syntacs/analyzer/syntactic",
        "//src/com/inxar/syntacs/translator/lr:interpreter",
        "//src/com/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:pickler",
        "//src/com/inxar/syntacs/automaton/pushdown",
    ],
)
