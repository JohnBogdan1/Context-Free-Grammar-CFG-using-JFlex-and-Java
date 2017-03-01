JFLAGS = -g
JC = javac
JVM = java
JFLEX = jflex

all: build

JFlexer.java:
	$(JFLEX) --skel skeleton.nested-1.6.0 jflexer.flex

sources = $(wildcard *.java)
classes = $(sources:.java=.class)

build: JFlexer.java $(classes)

run: build
	$(JVM) Main ${arg}

.PHOONY: clean
clean :
	rm -f *.class JFlexer.java *~
%.class : %.java
	$(JC) $(JFLAGS) $<
