load("//syntacs:rules.bzl", "stt_library", "stt_compile")

package(default_visibility = ["//visibility:public"])

filegroup(
    name = "stt",
    srcs = [
        "abb.stt",
    ]
)

stt_library(
    name = "lib",
    grammar = "abb.stt",
)

stt_compile(
    name = "tr",
    grammar = "abb.stt",
)
