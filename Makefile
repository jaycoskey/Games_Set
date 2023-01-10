SRCS= EmptyException.java \
    Card.java \
    Deck.java \
    Game.java \
    Gui.java

CLASSES = $(SRCS:%.java=%.class)

default: $(CLASSES)

clean:
	rm *.class

run: default
	java Game

wc:
	wc $(SRCS)

%.class: %.java
	javac $<
