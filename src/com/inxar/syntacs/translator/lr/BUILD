package(default_visibility = ["//visibility:public"])

java_library(
    name = "component",
    srcs = [
        "AbstractLRTranslationComponent.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:component",
        "//src/org/inxar/syntacs/translator/lr:grammar",
    ],
)


java_library(
    name = "grammar",
    srcs = [
        "StandardLRTranslatorGrammar.java",
        "ProxyLRTranslatorGrammar.java",
    ],
    deps = [
        "burner",
        "viz",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/grammar/regular",
        "//src/org/inxar/syntacs/grammar/context_free",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/automaton/finite:tree",
        "//src/com/inxar/syntacs/automaton/finite:tree2meso",
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/automaton/pushdown:array2meso",
        "//src/com/inxar/syntacs/automaton/pushdown:lr1",
        "//src/com/inxar/syntacs/automaton/pushdown:lalr1",
        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/translator/regexp",
        "//src/com/inxar/syntacs/grammar/regular",
        "//src/com/inxar/syntacs/grammar/context_free",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
    ],
)


java_library(
    name = "translator",
    srcs = [
        "StandardLRTranslator.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/grammar/regular",
        "//src/org/inxar/syntacs/grammar/context_free",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/translator/lr:component",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "xmlt",
    srcs = [
        "XML2LRTranslatorGrammarTransformer.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/translator/lr:grammar",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/translator/syntacs",
        "//src/com/inxar/syntacs/translator/regexp",
        "//src/com/inxar/syntacs/translator/regexp:xmlt",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:dom",
    ],
)


java_library(
    name = "interpreter",
    srcs = [
        "StandardLRTranslatorInterpreter.java",
    ],
    deps = [
        "component",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/grammar",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "burner",
    srcs = [
        "BurnerController.java",
        "LRTranslatorGrammarBurner.java",
    ],
    deps = [
        "//lib:jenesis",
        "//src/org/inxar/syntacs/util:burner",
        "//src/org/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/automaton/pushdown",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/util",

        "//src/com/inxar/syntacs/automaton/finite",
        "//src/com/inxar/syntacs/automaton/finite:burner",
        "//src/com/inxar/syntacs/automaton/pushdown:burner",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:burntools",
    ],
)

java_library(
    name = "viz",
    srcs = [
        "VizController.java",
    ],
    deps = [
        # "//lib:jenesis",
        # "//src/org/inxar/syntacs/util:burner",
        # "//src/org/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/automaton/pushdown",
        # "//src/org/inxar/syntacs/translator/lr:grammar",
        # "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:viz",
        "//src/org/inxar/syntacs/util:session",

        # "//src/com/inxar/syntacs/automaton/finite",
        # "//src/com/inxar/syntacs/automaton/finite:burner",
        "//src/com/inxar/syntacs/automaton/finite:tree",
        "//src/com/inxar/syntacs/automaton/pushdown",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:dot",
        # "//src/com/inxar/syntacs/util",
        # "//src/com/inxar/syntacs/util:burntools",
    ],
)
