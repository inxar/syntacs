package(default_visibility = ["//visibility:public"])

java_library(
    name = "regular",
    srcs = [
        "RECharClass.java",
        "REGrammar.java",
        "REUnion.java",
        "REClosure.java",
        "REConcatenation.java",
        "REExpressionTerminator.java",
        "REInterval.java",
        "REOption.java",
        "REEpsilon.java",
        "RECharString.java",
        "REPositiveClosure.java",
   ],
    deps = [
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/grammar/regular",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/translator/regexp",
        "//src/com/inxar/syntacs/util:string",
        "//src/com/inxar/syntacs/util:session",
    ],
)
