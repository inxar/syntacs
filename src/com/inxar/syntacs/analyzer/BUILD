package(default_visibility = ["//visibility:public"])

java_library(
    name = "analyzer",
    srcs = [
        "AbstractSymbol.java",
        "ListSymbol.java",
        "ArraySymbol.java",
        "ConstantSymbol.java",
        "CharSymbol.java",
        "ObjectSymbol.java",
        "StringSymbol.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/translator/lr:grammar",

        "//src/com/inxar/syntacs/util:arbor",
        "//src/com/inxar/syntacs/util:session",
    ]
)


java_library(
    name = "input",
    srcs = [
        "StandardInput.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/util:session",
    ]
)
