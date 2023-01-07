SRCS= SetGame.java SetDeck.java SetCard.java EmptyException.java

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

