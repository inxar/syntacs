package(default_visibility = ["//visibility:public"])

java_library(
    name = "compile",
    srcs = [
        "Compile.java",
    ],
    deps = [
        "interpret",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/translator/lr:xmlt",
        "//src/com/inxar/syntacs/translator/syntacs",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_binary(
    name = "compiler",
    main_class = "com.inxar.syntacs.Compile",
    runtime_deps = [
        "compile",
    ],
)

java_library(
    name = "interpret",
    srcs = [
        "Interpret.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/analyzer",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/util:arbor",
        "//src/com/inxar/syntacs/util:session",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_binary(
    name = "interpreter",
    main_class = "com.inxar.syntacs.Interpret",
    runtime_deps = [
        "interpret",
    ],
)

java_library(
    name = "test",
    srcs = [
        "Test.java",
    ],
    deps = [
        "interpret",
        "//src/com/inxar/syntacs/translator",
        "//src/com/inxar/syntacs/translator/lr:xmlt",
        "//src/com/inxar/syntacs/translator/syntacs",
        "//src/com/inxar/syntacs/util:session",
        "//src/com/inxar/syntacs/util:dom",
        "//src/org/inxar/syntacs/translator",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_binary(
    name = "tester",
    main_class = "com.inxar.syntacs.Test",
    runtime_deps = [
        "test",
    ],
)

java_binary(
    name = "compile-abb",
    main_class = "com.inxar.syntacs.Compile",
    runtime_deps = [
        "compile",
    ],
    data = [
        "//grammar/abb:stt",
    ],
    args = [
        "grammar/abb/abb.stt",
    ],
)

java_binary(
    name = "interpret-abb",
    main_class = "com.inxar.syntacs.Interpret",
    runtime_deps = [
        "interpret",
        "//test/com/inxar/syntacs/translator/test:abb",
    ],
    args = [
        "com.inxar.syntacs.translator.test.AbbGrammar",
    ],
)
