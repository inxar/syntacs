package(default_visibility = ["//visibility:public"])

java_library(
    name = "util",
    srcs = [
        "ArrayIntArray.java",
        "ArrayIntFunction.java",
        "ArrayIntList.java",
        "ArrayIntStack.java",
        "BitSetIntSet.java",
        "EmptyIntSet.java",
        "HashBitSetIntRelation.java",
        "HashIntFunction.java",
        "ImmutableIntSet.java",
        "IntArrayIterator.java",
        "ListIntSet.java",
        "SingletonIntSet.java",
        "TreeListIntRelation.java",
    ],
    deps = [
        "//src/org/inxar/syntacs/util",
    ],
)

java_library(
    name = "bubble",
    srcs = [
        "BubbleTree.java",
    ],
    deps = [
        "//src/com/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util",
    ],
)

java_library(
    name = "arbor",
    srcs = [
        "Arboreal.java",
        "Tree.java",
    ],
    deps = [
        "buffer",
        "session",
        "//src/com/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util",
        "//src/org/inxar/syntacs/util:session",
    ],
)

java_library(
    name = "buffer",
    srcs = [
        "Buffer.java",
    ],
)

java_library(
    name = "pickler",
    srcs = [
        "Pickler.java",
    ],
    deps = [
        "//lib:jenesis",
    ],
)

java_library(
    name = "string",
    srcs = [
        "StringTools.java",
    ],
)

java_library(
    name = "dom",
    srcs = [
        "DOM.java",
    ],
)

java_library(
    name = "dot",
    srcs = [
        "Dot.java",
    ],
    deps = [
        "string",
        "//src/org/inxar/syntacs/util:viz",
    ],
)

java_library(
    name = "burntools",
    srcs = [
        "BurnTools.java",
    ],
    deps = [
        "string",
        "//lib:jenesis",
    ],
)

java_library(
    name = "session",
    srcs = [
        "Mission.java",
        "StandardSession.java",
        "StringTools.java",
    ],
    deps = [
        "buffer",
        "string",
        "//src/org/inxar/syntacs/util:session",
    ],
)
