SRCS= EmptyException.java \
    SetCard.java \
    SetDeck.java \
    SetGame.java \
    SetGui.java

CLASSES = $(SRCS:%.java=%.class)

default: $(CLASSES)

clean:
	rm *.class

run: default
	java SetGame

wc:
	wc $(SRCS)

%.class: %.java
	javac $<

