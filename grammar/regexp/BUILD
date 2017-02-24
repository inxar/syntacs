load("//syntacs:rules.bzl", "stt_library", "stt_compile", "stt_interpret")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "srcs",
    srcs = [
        "regexp.stt",
    ]
)

stt_library(
    name = "stt",
    grammar = "regexp.stt",
    deps = [
        "//src/com/inxar/syntacs/translator/regexp",
    ],
)

stt_interpret(
    name = "stt_bin",
    grammar = "regexp.stt",
    deps = [
        "//src/com/inxar/syntacs/translator/regexp",
    ],
    args = [
        "com.inxar.syntacs.translator.regexp.RegexpGrammar",
    ],
)
