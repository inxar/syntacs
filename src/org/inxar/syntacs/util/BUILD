package(default_visibility = ["//visibility:public"])

java_library(
    name = "util",
    srcs = [
        "Algorithm.java",
        "AlgorithmException.java",
        "BinaryIntFunction.java",
        "IntArray.java",
        "IntFunction.java",
        "IntList.java",
        "IntSet.java",
        "IntRelation.java",
        "IntStack.java",
        "IntIterator.java",
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
        "Log.java",
        "Session.java",
        "Channel.java",
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
