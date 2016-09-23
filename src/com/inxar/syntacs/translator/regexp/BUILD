package(default_visibility = ["//visibility:public"])


java_library(
    name = "regexp",
    srcs = [
        "Regexp.java",
        "RegexpAtom.java",
        "RegexpTerm.java",
        "RegexpList.java",
        "RegexpEpsilon.java",
        "RegexpRange.java",
        "RegexpCharClass.java",
        "RegexpGrammar.java",
        "RegexpInterpreter.java",
    ],
    deps = [
        "dfa",
        "dpa",
        "//src/org/inxar/syntacs/analyzer",
        "//src/org/inxar/syntacs/automaton/finite",
        "//src/org/inxar/syntacs/grammar/regular",
        "//src/org/inxar/syntacs/analyzer/lexical",
        "//src/org/inxar/syntacs/analyzer/syntactic",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/translator/lr:translator",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/analyzer:input",
        "//src/com/inxar/syntacs/analyzer/lexical",
        "//src/com/inxar/syntacs/analyzer/syntactic",
        "//src/com/inxar/syntacs/translator/lr:translator",
        "//src/com/inxar/syntacs/translator/lr:interpreter",
        "//src/com/inxar/syntacs/util",
        "//src/com/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "dfa",
    srcs = [
        "RegexpDefaultDFA.java",
        "RegexpCharclassDFA.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util:pickler",
        "//src/com/inxar/syntacs/automaton/finite",
    ],
)

java_library(
    name = "dpa",
    srcs = [
        "RegexpDPA.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util:pickler",
        "//src/com/inxar/syntacs/automaton/pushdown",
    ],
)


java_library(
    name = "xmlt",
    srcs = [
        "XML2RegexpTransformer.java",
    ],
    deps = [
        # "//src/org/inxar/syntacs/analyzer",
        # "//src/org/inxar/syntacs/analyzer/lexical",
        # "//src/org/inxar/syntacs/analyzer/syntactic",
        # "//src/org/inxar/syntacs/grammar",
        # "//src/org/inxar/syntacs/grammar/regular",
        # "//src/org/inxar/syntacs/grammar/context_free",
        "//src/org/inxar/syntacs/translator",
        # "//src/org/inxar/syntacs/translator/lr:translator",
        # "//src/org/inxar/syntacs/translator/lr:component",
        # "//src/org/inxar/syntacs/translator/lr:grammar",
        "//src/org/inxar/syntacs/util:session",
        # "//src/com/inxar/syntacs/automaton/finite",
        # "//src/com/inxar/syntacs/automaton/pushdown",
        # "//src/com/inxar/syntacs/translator",
        # "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/translator/regexp",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:dom",
    ],
)
