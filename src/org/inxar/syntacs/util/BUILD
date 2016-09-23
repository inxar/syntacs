package(default_visibility = ["//visibility:public"])

java_library(
    name = "util",
    srcs = [
        "Algorithm.java",
        "AlgorithmException.java",
        "BinaryIntFunction.java",
        "IntArray.java",
        "IntFunction.java",
        "IntIterator.java",
        "IntList.java",
        "IntRelation.java",
        "IntSet.java",
        "IntStack.java",
        "Reiterator.java",
    ],
)

java_library(
    name = "viz",
    srcs = [
        "GraphViz.java",
        "Vizualizable.java",
    ],
)

java_library(
    name = "session",
    srcs = [
        "Channel.java",
        "Log.java",
        "Session.java",
    ],
)

java_library(
    name = "burner",
    srcs = [
        "Burner.java",
    ],
    deps = [
        "//lib:jenesis",
    ],
)
