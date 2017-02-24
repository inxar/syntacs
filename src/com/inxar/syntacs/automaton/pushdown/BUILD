package(default_visibility = ["//visibility:public"])

java_library(
    name = "pushdown",
    srcs = [
        "ArrayDPA.java",
        "ArrayDPAConstructor.java",
        "MesoArrayDPA.java",
    ],
    deps = [
        "action",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/grammar/context_free",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/util:viz",
    ],
)

java_library(
    name = "action",
    srcs = [
        "StandardAction.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/automaton/pushdown",
    ],
)

java_library(
    name = "array2meso",
    srcs = [
        "Array2MesoArrayDPATransformer.java",
    ],
    deps = [
        "pushdown",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "burner",
    srcs = [
        "MesoArrayDPABurner.java",
    ],
    deps = [
        "//lib:jenesis",
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/util:burntools",
        "//src/com/inxar/syntacs/util:pickler",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:string",
        "//src/org/inxar/syntacs/util:burner",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "scc",
    srcs = [
        "SCCTransitiveClosure.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util",
    ],
)

java_library(
    name = "lalr1",
    srcs = [
        "LALR1Constructor.java",
    ],
    deps = [
        "scc",
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:string",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/grammar/context_free",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/util:viz",
    ],
)

java_library(
    name = "lr1",
    srcs = [
        "LR1Constructor.java",
        "LRConstructor.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/grammar/context_free",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/util:viz",
    ],
)


java_test(
    name = "scc-test",
    size = "small",
    srcs = [
        "SCCTransitiveClosureTest.java",
    ],
    test_class = "com.inxar.syntacs.automaton.pushdown.SCCTransitiveClosureTest",
    deps = [
        ":scc",
        "//lib:junit4",
        "//src/org/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util",
    ],
)
