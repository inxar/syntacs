fmt:
	buildifier WORKSPACE
	find . -name BUILD | xargs buildifier
	find src -name '*.java' | xargs java -jar ~/bin/google-java-format-0.1-alpha.jar --replace

#================================================================
# Everything below here is pretty old. I'm not using these
# Targets anymore but the jdk1.3 and netscape reference are
# pretty sweet.
#================================================================

#================================================================
# SYSTEM SETTINGS
#================================================================
JAVADIR    = /usr/java/jdk1.3
JAVABINDIR = ${JAVADIR}/bin

CP          = /bin/cp
CD          = /bin/cd
MV          = /bin/mv
RM          = /bin/rm
MKDIR       = /bin/mkdir
TAR         = /bin/tar
GZIP        = /bin/gzip

JAVA        = ${JAVABINDIR}/java
JAVAC       = ${JAVABINDIR}/javac
JAVADOC     = ${JAVABINDIR}/javadoc
JAR         = ${JAVABINDIR}/jar
RMIC        = ${JAVABINDIR}/rmic

JIKES       = /usr/bin/jikes
TEXI2HTML   = /usr/bin/texi2html
DIRCMP      = /usr/bin/dircmp
ZIP         = /usr/bin/zip
RSYNC       = /usr/bin/rsync
BZIP2       = /usr/bin/bzip2
NETSCAPE    = /usr/bin/netscape
CVS         = /usr/bin/cvs
FIND        = /usr/bin/find

# Flags
SYS_JCFLAGS      =
SYS_JIFLAGS      =
SYS_JAVADOCFLAGS =
SYS_JARFLAGS     =

# Java classpath
SYS_CLASSPATH = ${JAVADIR}/jre/lib/rt.jar

#================================================================
# PROJECT SETTINGS
#================================================================
P_VENDOR  = inxar
P_NAME    = syntacs
P_VERSION = 0_2_0

P_BINDIR     = ${P_ROOTDIR}/bin
P_CLASSESDIR = ${P_ROOTDIR}/classes
P_LIBDIR     = ${P_ROOTDIR}/lib
P_DOCSDIR    = ${P_ROOTDIR}/docs
P_APIDIR     = ${P_DOCSDIR}/api
P_SRCDIR     = ${P_ROOTDIR}/src
P_TMPDIR     = ${P_ROOTDIR}/tmp
P_BUILDDIR   = /tmp/.build
P_DISTDIR    = ${P_BUILDDIR}/${P_VENDOR}/${P_NAME}-${P_VERSION}

P_PACKAGES = \
	org.inxar.syntacs.analyzer\
	com.inxar.syntacs.analyzer\
	org.inxar.syntacs.analyzer.lexical\
	com.inxar.syntacs.analyzer.lexical\
	org.inxar.syntacs.analyzer.syntactic\
	com.inxar.syntacs.analyzer.syntactic\
	org.inxar.syntacs.automaton.finite\
	com.inxar.syntacs.automaton.finite\
	org.inxar.syntacs.automaton.pushdown\
	com.inxar.syntacs.automaton.pushdown\
	org.inxar.syntacs.grammar\
	org.inxar.syntacs.grammar.regular\
	com.inxar.syntacs.grammar.regular\
	org.inxar.syntacs.grammar.context_free\
	com.inxar.syntacs.grammar.context_free\
	org.inxar.syntacs.translator\
	com.inxar.syntacs.translator\
	org.inxar.syntacs.translator.lr\
	com.inxar.syntacs.translator.lr\
	org.inxar.syntacs.util\
	com.inxar.syntacs.util\
	com.inxar.syntacs.translator.regexp\
	com.inxar.syntacs.translator.syntacs\
	com.inxar.syntacs.translator.test\
	com.inxar.syntacs\

# Java Compiler
#JC = ${JAVAC}
JC = ${JIKES}

P_CLASSPATH = ${P_CLASSESDIR}:${P_LIBDIR}/jenesis.jar:${P_LIBDIR}/xerces.jar

P_JCFLAGS = \
	-classpath ${SYS_CLASSPATH}:${P_CLASSPATH} \
	-d ${P_CLASSESDIR}

P_JAVADOCFLAGS = \
	-classpath ${P_CLASSPATH} \
	-sourcepath ${P_SRCDIR} \
	-d ${P_APIDIR} \
	-group "Abstraction" "org.*" \
	-group "Implementation" "com.*" \
	-public \
	-use \
	-link http://java.sun.com/products/jdk/1.3/docs/api \
	-overview ${P_SRCDIR}/overview.html \
	-windowtitle "API Documentation for ${P_VENDOR}:${P_NAME} version ${P_VERSION}" \

P_JARFLAGS =

#================================================================
# MISC TARGETS
#================================================================

default: check classes

check:
	if [ "$$P_ROOTDIR" = "" ]; \
	then echo 'P_ROOTDIR is not set! (try "export P_ROOTDIR=`pwd`")'; \
	exit 1; fi

clean: check classesclean

realclean: check apiclean classesclean tmpclean libjarclean srcjarclean buildclean usermanclean docsclean

backupclean: check
	${FIND} ${P_ROOTDIR} -name '*~' -exec ${RM} {} \;

tmpdir: check
	${MKDIR} -p ${P_TMPDIR}

tmpclean: check
	${RM} -rf ${P_TMPDIR}

#================================================================
# CVS TARGETS
#================================================================

cvsk: check
	${CVS} commit

cvsr: check
	cd ${P_ROOTDIR}/.. && ${CVS} remove -d ${P_NAME}

# ================================================================
# DOCUMENTATION TARGETS
# ================================================================

apidir: check
	${MKDIR} -p ${P_APIDIR}

api: check apidir
	${JAVADOC} ${SYS_JAVADOCFLAGS} ${P_JAVADOCFLAGS} ${P_PACKAGES}

apiclean: check
	${RM} -rf ${P_APIDIR}

apibrowse: check
	${NETSCAPE} -remote "openURL(file:`cd ${P_APIDIR}; pwd`/overview-summary.html)"

# ================================================================
# COMPILATION TARGETS
# ================================================================

classesdir: check
	${MKDIR} -p ${P_CLASSESDIR}

classes: check classesdir
	P_FILES=`echo ${P_PACKAGES}| tr '.' '/'`; \
	P_FILES=`${DIRCMP} -s ${P_SRCDIR} -d ${P_CLASSESDIR} -p .java -q .class $$P_FILES`; \
	if [ "$$P_FILES" = "" ]; then echo "${P_NAME} is up to date"; \
	else \
	echo -n 'Compiling '; \
	for file in "$$P_FILES"; do echo -n ' '; echo -n `basename $$file`; \
	done; echo '...'; \
	${JC} ${SYS_JCFLAGS} ${P_JCFLAGS} $$P_FILES; fi

classesclean: check
	${RM} -rf ${P_CLASSESDIR}

# ================================================================
# LIBRARY TARGETS
# ================================================================

libdir: check
	${MKDIR}  -p ${P_LIBDIR}

# ================================================================
# LIBJAR TARGETS
# ================================================================

libjar: check libdir
	cd ${P_CLASSESDIR} \
		&& ${JAR} cfm ${P_NAME}.jar \
		${P_SRCDIR}/MANIFEST.MF ${SYS_JARFLAGS} ${P_JARFLAGS} .

	${MV} ${P_CLASSESDIR}/${P_NAME}.jar ${P_LIBDIR}

libjarclean: check
	${RM} -f ${P_LIBDIR}/${P_NAME}.jar

# ================================================================
# SRCJAR TARGETS
# ================================================================

srcjar: check
	cd ${P_ROOTDIR} && ${JAR} cfm src.jar ${P_SRCDIR}/MANIFEST.MF src

srcjarclean: check
	${RM} -f src.jar

# ================================================================
# DIST TARGETS
# ================================================================

distdir: check
	${MKDIR} -p ${P_DISTDIR}

buildclean: check
	${RM} -rf ${P_BUILDDIR}

distclean: check
	${RM} -rf ${P_DISTDIR}

dist: check classes all_s all_p classes libjar srcjar docs distdir _dist

_dist: check distdir
	${RSYNC} -azu --delete \
	--exclude '*CVS' \
	--exclude '*~' \
	--exclude 'classes' \
	--exclude 'src' \
	--exclude 'tmp' \
	${P_ROOTDIR}/* ${P_DISTDIR}

zipdist: check tmpdir dist _zipdist

_zipdist: check
	cd ${P_BUILDDIR} && ${ZIP} -r ${P_NAME}-${P_VERSION}.zip .
	${MV} ${P_BUILDDIR}/${P_NAME}-${P_VERSION}.zip ${P_TMPDIR}

tardist: check tmpdir dist _tardist

_tardist: check
	${TAR} cf ${P_TMPDIR}/${P_NAME}-${P_VERSION}.tar -C ${P_BUILDDIR} ${P_VENDOR}

gzipdist: check tardist _gzipdist

_gzipdist: check
	${GZIP} -f ${P_TMPDIR}/${P_NAME}-${P_VERSION}.tar

bzipdist: check tardist _bzipdist

_bzipdist: check
	${BZIP2} -f ${P_TMPDIR}/${P_NAME}-${P_VERSION}.tar

# ================================================================
# PROJECT SPECIFIC DOCUMENTATION TARGETS
# ================================================================
DOT2PNG = ${P_BINDIR}/dot2png

indexbrowse: check
	${NETSCAPE} -remote "openURL(file:`cd ${P_ROOTDIR}; pwd`/index.html)"

faq: check
	cd ${P_DOCSDIR}/faq && ${TEXI2HTML} -monolithic faq.texinfo

faqbrowse: check
	${NETSCAPE} -remote "openURL(file:`cd ${P_DOCSDIR}/faq; pwd`/faq.html)"

faqclean: check
	cd ${P_DOCSDIR}/faq && ${RM} -f *.html

lexerman: check
	cd ${P_DOCSDIR}/lexerman && ${DOT2PNG} lexer.dot
	cd ${P_DOCSDIR}/lexerman && ${TEXI2HTML} -monolithic lexerman.texinfo

lexermanbrowse: check
	${NETSCAPE} -remote "openURL(file:`cd ${P_DOCSDIR}/lexerman; pwd`/lexerman.html)"

lexermanclean: check
	cd ${P_DOCSDIR}/lexerman && ${RM} -f *.html *.png

userman: check
	cd ${P_DOCSDIR}/userman && ${DOT2PNG} construction.dot
	cd ${P_DOCSDIR}/userman && ${DOT2PNG} components-scheme.dot
	cd ${P_DOCSDIR}/userman && ${TEXI2HTML} -split_chapter userman.texinfo

usermanbrowse: check
	${NETSCAPE} -remote "openURL(file:`cd ${P_DOCSDIR}/userman; pwd`/userman_toc.html)"

usermanclean: check
	cd ${P_DOCSDIR}/userman && ${RM} -f *.html *.png

docs: check api faq lexerman userman

docsclean: check faqclean lexermanclean usermanclean apiclean


# ================================================================
# PROJECT SPECIFIC COMPILATION TARGETS
# ================================================================
SYNTACS_COMPILER    = com.inxar.syntacs.Compile
SYNTACS_INTERPRETER = com.inxar.syntacs.Run
SYNTACS_TESTER      = com.inxar.syntacs.Test
P_GRAMMARDIR = ${P_ROOTDIR}/grammar

#
# _x means "compile with xml file"
# _s means "compile with stt file"
# _t means "run tests"
# _c means "clean"
# _p means "generate PNG images"
#

abb_x: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/abb/abb.xml
abb_s: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/abb/abb.stt
abb_t: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_TESTER} ${P_GRAMMARDIR}/abb/tests.xml
abb_c: check
	cd ${P_GRAMMARDIR}/abb && ${RM} -f *.dot *.png
abb_p: check
	cd ${P_GRAMMARDIR}/abb && ${DOT2PNG} AbbDefaultDFA.dot
	cd ${P_GRAMMARDIR}/abb && ${DOT2PNG} AbbDPA.lalr1.dot

etf_x: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/etf/etf.xml
etf_s: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/etf/etf.stt
etf_t: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_TESTER} ${P_GRAMMARDIR}/etf/tests.xml
etf_c: check
	cd ${P_GRAMMARDIR}/etf && ${RM} -f *.dot *.png
etf_p: check
	cd ${P_GRAMMARDIR}/etf && ${DOT2PNG} EtfDefaultDFA.dot
	cd ${P_GRAMMARDIR}/etf && ${DOT2PNG} EtfDPA.lalr1.dot

scc_x: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/scc/scc.xml
scc_s: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/scc/scc.stt
scc_t: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_TESTER} ${P_GRAMMARDIR}/scc/tests.xml
scc_c: check
	cd ${P_GRAMMARDIR}/scc && ${RM} -f *.dot *.png
scc_p: check
	cd ${P_GRAMMARDIR}/scc && ${DOT2PNG} SccDefaultDFA
	cd ${P_GRAMMARDIR}/scc && ${DOT2PNG} SccDPA.lalr1.dot

regexp_x: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/regexp/regexp.xml
regexp_s: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/regexp/regexp.stt
regexp_t: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_TESTER} ${P_GRAMMARDIR}/regexp/tests.xml
regexp_c: check
	cd ${P_GRAMMARDIR}/regexp && ${RM} -f *.dot *.png
regexp_p: check
	cd ${P_GRAMMARDIR}/regexp && ${DOT2PNG} RegexpDefaultDFA.dot
	cd ${P_GRAMMARDIR}/regexp && ${DOT2PNG} RegexpCharclassDFA.dot
	cd ${P_GRAMMARDIR}/regexp && ${DOT2PNG} RegexpDPA.lalr1.dot

syntacs_x: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/syntacs/syntacs.xml
syntacs_s: check
	${JAVA} -classpath ${P_CLASSPATH} ${SYNTACS_COMPILER} ${P_GRAMMARDIR}/syntacs/syntacs.stt
syntacs_t: check
syntacs_c: check
	cd ${P_GRAMMARDIR}/syntacs && ${RM} -f *.dot *.png
syntacs_p: check
	cd ${P_GRAMMARDIR}/syntacs && ${DOT2PNG} SyntacsDefaultDFA.dot
	cd ${P_GRAMMARDIR}/syntacs && ${DOT2PNG} SyntacsDPA.lalr1.dot

all_x: check abb_x etf_x scc_x regexp_x syntacs_x
all_s: check abb_s etf_s scc_s regexp_s syntacs_s
all_t: check abb_t etf_t scc_t regexp_t syntacs_t
all_c: check abb_c etf_c scc_c regexp_c syntacs_c
all_p: check abb_p etf_p scc_p regexp_p syntacs_p

all_xst: check abb_x all_s all_t

regexp_i: check
	${P_BINDIR}/stti com.inxar.syntacs.translator.regexp.RegexpGrammar '(a|b)*abb'

etf_i: check
	${P_BINDIR}/stti com.inxar.syntacs.translator.test.EtfGrammar 'id+id*id+id*id'
