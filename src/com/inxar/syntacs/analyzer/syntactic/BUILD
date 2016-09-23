package(default_visibility = ["//visibility:public"])

java_library(
    name = "syntactic",
    srcs = [
        "StandardParser.java",
        "StandardRecovery.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/translator/lr:component",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
    ],
)
