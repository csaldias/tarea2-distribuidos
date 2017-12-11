JCFLAGS = -g -d out/ -cp src/ -encoding utf8
JC = javac

JVMFLAGS = -cp out/
JVM= java

.SUFFIXES: .java .class

.java.class:
				$(JC) $(JCFLAGS) $*.java

CLASSES = \
        src/Token.java \
        src/Interfaz.java \
        src/Proceso.java \
        src/Main_Proceso.java

MAIN = Main_Proceso

default: classes

classes: $(CLASSES:.java=.class)

clean:
				$(RM) out/*.class

run:
				$(JVM) $(JVMFLAGS) $(MAIN) 0 $N 500 true &
				for ((i=1;i<$N;i++)) do $(JVM) $(JVMFLAGS) $(MAIN) $$i $N `expr "$$i" '*' "1000"` false & done
