package(default_visibility = ["//visibility:public"])

java_library(
    name = "syntactic",
    srcs = [
        "Parser.java",
        "ParserInterpreter.java",
        "Sentence.java",
        "Recovery.java",
        "Correction.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/translator/lr:component",
    ],
)
