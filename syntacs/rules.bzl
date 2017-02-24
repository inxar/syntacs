_stt_filetype = FileType([".stt"])

def _stt_compile_impl(ctx):
    grammar = ctx.file.grammar
    compiler = ctx.executable._compiler
    srcjar = ctx.outputs.srcjar

    ctx.action(
        mnemonic = "SttCompile",
        executable = compiler,
        arguments = [
            "-Dcompile-sourcepath=%s" % (ctx.var["GENDIR"] + "/" + ctx.label.package),
            "-Dcompile-srcjar=%s" % srcjar.path,
            grammar.path,
        ],
        inputs = [compiler, grammar],
        outputs = [srcjar],
    )
    return struct(
        files = set([srcjar]),
    )

stt_compile = rule(
    implementation = _stt_compile_impl,
    attrs = {
        "grammar": attr.label(
            mandatory = True,
            single_file = True,
            allow_files = _stt_filetype,
        ),
        "_compiler": attr.label(
            default = Label("//src/com/inxar/syntacs:compiler"),
            executable = True,
            cfg = "host",
        )
    },
    outputs = {
        "srcjar": "%{name}.srcjar",
    },
    output_to_genfiles = True,
)

def stt_library(name = None, grammar = None, deps = []):

    stt_compile(
        name = name + ".sttc",
        grammar = grammar,
    )

    native.java_library(
        name = name,
        srcs = [name + '.sttc.srcjar'],
        deps = deps + [
            "//src/com/inxar/syntacs/util:pickler",
            "//src/org/inxar/syntacs/util",
            "//src/com/inxar/syntacs/util",
            "//src/org/inxar/syntacs/automaton/finite",
            "//src/com/inxar/syntacs/automaton/finite",
            "//src/com/inxar/syntacs/automaton/pushdown",
            "//src/org/inxar/syntacs/analyzer",
            "//src/com/inxar/syntacs/analyzer:input",
            "//src/com/inxar/syntacs/analyzer/lexical",
            "//src/com/inxar/syntacs/analyzer/syntactic",
            "//src/org/inxar/syntacs/analyzer/lexical",
            "//src/org/inxar/syntacs/analyzer/syntactic",
            "//src/org/inxar/syntacs/translator/lr:grammar",
            "//src/org/inxar/syntacs/translator/lr:translator",
            "//src/com/inxar/syntacs/translator/lr:interpreter",
            "//src/com/inxar/syntacs/translator/lr:translator",
            "//src/org/inxar/syntacs/translator",
        ],
    )


def stt_interpret(name = None, grammar = None, deps = [], **kwargs):

    stt_library(
        name = name + ".stt",
        grammar = grammar,
        deps = deps,
    )

    native.java_binary(
        name = name,
        main_class = "com.inxar.syntacs.Interpret",
        runtime_deps = [name + '.stt'] + deps + [
            "//src/com/inxar/syntacs:interpret",
        ],
        **kwargs
    )
