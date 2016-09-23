package(default_visibility = ["//visibility:public"])

java_library(
    name = "analyzer",
    srcs = [
        "AbstractSymbol.java",
        "ArraySymbol.java",
        "CharSymbol.java",
        "ConstantSymbol.java",
        "ListSymbol.java",
        "ObjectSymbol.java",
        "StringSymbol.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util:arbor",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/translator/lr:grammar",
    ],
)

java_library(
    name = "input",
    srcs = [
        "StandardInput.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/util:session",
    ],
)
