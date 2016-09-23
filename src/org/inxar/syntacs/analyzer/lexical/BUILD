package(default_visibility = ["//visibility:public"])

java_library(
    name = "lexical",
    srcs = glob([
        "Lexer.java",
        "LexerInterpreter.java",
    ]),
    deps = [
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/translator/lr:component",
        "//src/org/inxar/syntacs/util",
    ],
)
