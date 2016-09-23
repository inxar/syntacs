package(default_visibility = ["//visibility:public"])

java_library(
    name = "finite",
    srcs = [
        "MesoArrayDFA.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util:string",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/grammar",
    ],
)

java_library(
    name = "tree",
    srcs = [
        "TreeDFA.java",
        "TreeDFAConstructor.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:bubble",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:string",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/grammar/regular",
        "//src/org/inxar/syntacs/translator/lr:component",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/util:viz",
    ],
)

java_library(
    name = "tree2meso",
    srcs = [
        "Tree2MesoArrayDFATransformer.java",
    ],
    deps = [
        "finite",
        "tree",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/grammar",
    ],
)

java_library(
    name = "burner",
    srcs = [
        "MesoArrayDFABurner.java",
    ],
    deps = [
        "//lib:jenesis",
        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/util:burntools",
        "//src/com/inxar/syntacs/util:pickler",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:string",
        "//src/org/inxar/syntacs/util:burner",
        "//src/org/inxar/syntacs/util:session",
    ],
)
