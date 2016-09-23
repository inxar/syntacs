package(default_visibility = ["//visibility:public"])

java_library(
    name = "translator",
    srcs = [
        "LRTranslator.java",
        "LRTranslatorInterpreter.java",
    ],
    deps = [
        "grammar",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
    ],
)

java_library(
    name = "component",
    srcs = [
        "LRTranslationComponent.java",
    ],
    deps = [
        "grammar",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/translator",
    ],
)

java_library(
    name = "grammar",
    srcs = [
        "LRTranslatorGrammar.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/translator",
    ],
)
