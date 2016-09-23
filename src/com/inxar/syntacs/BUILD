java_library(
    name = "compile",
    srcs = [
        "Compile.java",
    ],
    deps = [
        "interpret",
        "//src/org/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/translator/syntacs",
        "//src/com/inxar/syntacs/translator/lr:xmlt",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/util:session",
    ]
)

java_binary(
    name = "compiler",
    main_class = "com.inxar.syntacs.Compile",
    runtime_deps = [
        "compile",
    ]
)

java_library(
    name = "interpret",
    srcs = [
        "Interpret.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util:session",

        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:arbor",
    ]
)

java_binary(
    name = "interpreter",
    main_class = "com.inxar.syntacs.Interpret",
    runtime_deps = [
        "interpret",
    ]
)

java_binary(
    name = "abb",
    main_class = "com.inxar.syntacs.Interpret",
    runtime_deps = [
        "interpret",
        "//src/com/inxar/syntacs/test",
    ],
    args = [
        "com.inxar.syntacs.translator.test.AbbGrammar",
        "t+f*(id+f)",
    ],
)
