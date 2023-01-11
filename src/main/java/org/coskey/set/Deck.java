package org.coskey.set;

import java.util.Arrays;
import java.util.Random;


class Deck extends CardCollection {
    public final static int CARD_COUNT = 81;
    public final static int BASE_OUTLAY_COUNT = 12;
    public final static int INCREMENTAL_OUTLAY_COUNT = 3;

    public Card dealCard() throws EmptyException {
        if (cards.isEmpty()) {
            throw new EmptyException();
        }
        int topCardIndex = cards.size() - 1;
        Card card = cards.get(topCardIndex);
        cards.remove(topCardIndex);
        cards.trimToSize();
        return card;
    }

    public boolean dealThreeIntoOther(CardCollection other) throws EmptyException {
        if (cards.isEmpty()) {
            return false;
        }
        other.add(dealCard());
        other.add(dealCard());
        other.add(dealCard());
        return true;
    }

    public Card getCard(int i) {
        return cards.get(i);
    }

    public void populateAndShuffle() {
        for (int cardNum = 0; cardNum < CARD_COUNT; ++cardNum) {
            Card sc = new Card(cardNum);
            cards.add(sc);
        }
        shuffle();
    }

    public void printSize() {
        if (cards.isEmpty()) {
            System.out.println("There are no cards left in the deck");
        } else {
            String verb = cards.size() == 1 ? "is" : "are";
            String s = String.format("There %s %d cards left in the deck.\n", verb, cards.size());
            System.out.print(s);
        }
    }

    // Fisher-Yates shuffle
    public void shuffle() {
        int n;
        Random prng = new Random();

        for (n = 80; n >= 1; --n) {
            // Determine what element should be at position #n.
            // Come up with a random number, r, from 0 to n.
            int r = Math.abs(prng.nextInt()) % n;
            if (r == n) {
                continue;
            }
            swap(r, n);
        }
    }

    public int size() {
        return cards.size();
    }

    public void swap(int a, int b) {
        assert(a != b);
        Card tmp = cards.get(a);
        cards.set(a, cards.get(b));
        cards.set(b, tmp);
    }
}
