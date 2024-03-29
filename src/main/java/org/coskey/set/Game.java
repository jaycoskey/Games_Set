package org.coskey.set;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Game {
    // Show user a Set if one is present that they cannot find.
    public static boolean isHelpEnabled_onBlankShowSet = true;

    // TODO: End game if deck is empty and no more Sets are present.
    public static boolean isMercyEnabled = false;

    public Deck deck;
    public CardCollection tableCards;
    public Gui gui;
    private int numSetsFound;

    Game() {
        gui = new Gui(this);
    }

    // TODO: Refactor into smaller pieces.
    public static void main(String [] args) throws EmptyException, IOException {
        Game game = new Game();

        for (;;) {
            game.setup();
            game.playRounds();

            if (! game.queryUserReplay()) {
                game.gui.consolePrintln("Bye!");
                break;  // End game loop
            }
        }
    }

    public static boolean allDifferent(int x, int y, int z) {
        return x != y && x != z && y != z;
    }

    public static boolean allSame(int x, int y, int z) {
        return x == y && y == z;
    }

    /**
    * @param deck Deck to be searched.
    * @param indxs Indices of found Set, if one is found.
    * @return Returns true if Set was found; otherwise, false.
    * TODO: Change to findSets, with a maxCount parameter.
    */
    public static boolean findSet(CardCollection cards, int[] indxs) {
        int size = cards.size();

        for (int i = 0; i < size - 2; ++i) {
            for (int j = i + 1; j < size - 1; ++j) {
                for (int k = j + 1; k < size; ++k) {
                    Card[] trio = { cards.getCard(i), cards.getCard(j), cards.getCard(k) };
                    if (isSet(trio)) {
                        indxs[0] = i;
                        indxs[1] = j;
                        indxs[2] = k;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSet(Card[] cards) {
        for (int d = 0; d < 4; ++d) {
            int x = cards[0].getTernaryDigit(d);
            int y = cards[1].getTernaryDigit(d);
            int z = cards[2].getTernaryDigit(d);

            if (!allSame(x, y, z) && !allDifferent(x, y, z)) {
                return false;
            }
        }
        return true;
    }

    // ========================================

    public boolean getHelpEnabled_onBlankShowSet() {
        return isHelpEnabled_onBlankShowSet;
    }

    public boolean getMercyEnabled() {
        return isMercyEnabled;
    }

    // TODO: Change return type to reflect how the game ended.
    public void playRounds() throws EmptyException, IOException {
        for (;;) {
            printGameStats(numSetsFound, tableCards.size(), deck.size());
            gui.consolePrint(tableCards.toString());

            Optional<int[]> optUserResponse = queryUserSet(tableCards.size());
            if (optUserResponse.isPresent()) {
                // User claims to have found a set
                int[] userSetIndxs = optUserResponse.get();
                Card[] cards = { tableCards.getCard(userSetIndxs[0]),
                                    tableCards.getCard(userSetIndxs[1]),
                                    tableCards.getCard(userSetIndxs[2])
                                    };
                if (isSet(cards)) {
                    // User found a set
                    gui.consolePrintln("\n*** You found a set!");
                    numSetsFound++;
                    tableCards.removeThree(userSetIndxs);
                    if (tableCards.isEmpty()) {
                        gui.consolePrintln("\nYou cleared the board. Congratulations!!");
                        break;  // Game over --- Board cleared
                    }
                    if (tableCards.size() < Deck.BASE_OUTLAY_COUNT) {
                        if (!deck.isEmpty()) {
                            deck.dealThreeIntoOther(tableCards);
                        }
                        continue;
                    }
                } else {
                    gui.consolePrintln("\n*** No, that's not a set.");
                    continue;
                }
            } else {
                assert(optUserResponse.isEmpty());
                if (isHelpEnabled_onBlankShowSet) {
                    int[] setIndxs = new int[3];
                    boolean isSetFound = findSet(tableCards, setIndxs);
                    if (isSetFound) {
                        showSet(tableCards, setIndxs);
                        continue;
                    }
                }
                if (deck.isEmpty()) {
                    gui.consolePrintln("\nGame is over. Thanks for playing!");
                    break;  // Game over. User could not find Set, and deck is empty
                } else {
                    // Lay down 3 more cards
                    deck.dealThreeIntoOther(tableCards);
                    continue;
                }
            }
        }
    }

    public void printGameStats(int numSetsFound, int tableCardCount, int deckCardCount) {
        String setCountStr = numSetsFound == 0 ? "No Sets"
                                 : ((numSetsFound == 1) ? "One Set"
                                 : String.format("%d Sets", numSetsFound));
        String tableCountStr = String.format("%d cards", tableCardCount);
        String deckCountStr = tableCardCount == 0 ? "No cards"
                                 : String.format("%d cards", deckCardCount);

        String outStr = String.format("%s found. %s on the table. %s in the deck.",
                                          setCountStr, tableCountStr, deckCountStr);
        gui.consolePrintln(outStr);
    }

    public void printSet(CardCollection cards, int[] indxs) {
        gui.consolePrintln(
            "Cards #" + String.valueOf(indxs[0])
            +   ", #" + String.valueOf(indxs[1])
            +   ", #" + String.valueOf(indxs[2])
            +   " form a set.");
    }

    public boolean queryUserReplay() throws IOException {
        String reply;
        BufferedReader in;

        in = new BufferedReader(new InputStreamReader(System.in));
        gui.consolePrint("\nPlay again (y/n)? ");
        reply = in.readLine();
        gui.consolePrintln("");
        reply = reply.toLowerCase().trim();
        if (reply.startsWith("n") || reply.startsWith("q")) {
            return false;
        }
        return true;
    }

    public Optional<int[]> queryUserSet(int tableCardCount) throws IOException {
        // TODO: Modify user prompts and input to the GUI (using JLabel & JTextArea).
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        Pattern regex = Pattern.compile("^\\s*\\d+\\s+\\d+\\s+\\d+\\s*$");

        for (;;) {
            gui.consolePrint("\nEnter three card #numbers (or <RETURN> for no Set found): ");
            String userInput = in.readLine().trim();
            if (userInput.isEmpty()) {
                return Optional.empty();  // User did not find set
            }
            Matcher matcher = regex.matcher(userInput);
            if (!matcher.find()) {
                gui.consolePrintln("Error: Did not recognize input format.");
                continue;
            }
            int[] cardIndxs = Arrays.stream(userInput.split(" "))
                                  .filter(s -> s.length() > 0)
                                  .mapToInt(Integer::parseInt)
                                  .toArray();
            boolean areIndxsValid = true;
            for (int cardIndx : cardIndxs) {
                if (cardIndx < 0 || cardIndx >= tableCardCount) {
                    gui.consolePrint(String.format("Error: Cards #%d is out of range (0 to %d)",
                        cardIndx, tableCardCount - 1));
                    areIndxsValid = false;
                }
            }
            if (!areIndxsValid) {
                continue;
            }
            return Optional.ofNullable(cardIndxs);
        }
        // NOT_REACHED
    }

    public void setHelpEnabled_onBlankShowSet(boolean isEnabled) {
        isHelpEnabled_onBlankShowSet = isEnabled;
    }

    public void setMercyEnabled(boolean isEnabled) {
        isMercyEnabled = isEnabled;
    }

    public void setup() throws EmptyException, IOException {
        numSetsFound = 0;
        // Refresh deck and table cards.
        deck = new Deck();
        tableCards = new Deck();

        assert(deck.isEmpty());
        deck.populateAndShuffle();
        assert(deck.size() == Deck.CARD_COUNT);
        assert(tableCards.isEmpty());

        // Deal initial cards to the table
        for (int k = 0; k < Deck.BASE_OUTLAY_COUNT; ++k) {
            tableCards.add(deck.dealCard());
        }
        assert(deck.size() == Deck.CARD_COUNT - Deck.BASE_OUTLAY_COUNT);
        assert(tableCards.size() == Deck.BASE_OUTLAY_COUNT);

        for (;;) {  // User looks for sets
            playRounds();
        }  // Done with this deck
    }

    public void showSet(CardCollection tableCards, int[] indxs) throws EmptyException, IOException {
        gui.consolePrint("\nSet: ");
        // TODO:Next: Display card table: gui.updateCardTable(tableCards);
        printSet(tableCards, indxs);
    }
}
