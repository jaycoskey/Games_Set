import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class SetGame {
    // Show user a Set if one is present that they cannot find.
    public static boolean isHelpEnabled_onBlankShowSet = true;

    // TODO: End game if deck is empty and no more Sets are present.
    public static boolean isMercyEnabled = false;

    public SetGui gui;

    SetGame() {
        this.gui = new SetGui(this);
    }

    // TODO: Refactor into smaller pieces.
    public static void main(String [] args) throws EmptyException, IOException {
        SetGame game = new SetGame();
        int numSetsFound = 0;

        // Play the game
        for (;;) {
            SetDeck deck = new SetDeck();
            SetCardCollection tableCards = new SetDeck();

            assert(deck.isEmpty());
            deck.populateAndShuffle();
            assert(deck.size() == SetDeck.CARD_COUNT);
            assert(tableCards.isEmpty());

            // Deal initial cards to the table
            for (int k = 0; k < SetDeck.BASE_OUTLAY_COUNT; ++k) {
                tableCards.add(deck.dealCard());
            }
            assert(deck.size() == SetDeck.CARD_COUNT - SetDeck.BASE_OUTLAY_COUNT);
            assert(tableCards.size() == SetDeck.BASE_OUTLAY_COUNT);

            for (;;) {  // User looking for set
                game.printGameStats(numSetsFound, tableCards.size(), deck.size());
                game.gui.consolePrint(tableCards.toString());

                Optional<int[]> optUserResponse = game.queryUserSet(tableCards.size());
                if (optUserResponse.isPresent()) {
                    // User claims to have found a set
                    int[] userSetIndxs = optUserResponse.get();
                    SetCard[] cards = { tableCards.getCard(userSetIndxs[0]),
                                        tableCards.getCard(userSetIndxs[1]),
                                        tableCards.getCard(userSetIndxs[2])
                                        };
                    if (isSet(cards)) {
                        // User found a set
                        game.gui.consolePrintln("\n*** You found a set!");
                        numSetsFound++;
                        tableCards.removeThree(userSetIndxs);
                        if (tableCards.isEmpty()) {
                            game.gui.consolePrintln("\nYou cleared the board. Congratulations!!");
                            break;  // Game over --- Board cleared
                        }
                        if (tableCards.size() < SetDeck.BASE_OUTLAY_COUNT) {
                            if (!deck.isEmpty()) {
                                deck.dealThreeIntoOther(tableCards);
                            }
                            continue;
                        }
                    } else {
                        game.gui.consolePrintln("\n*** No, that's not a set.");
                        continue;
                    }
                } else {
                    assert(optUserResponse.isEmpty());
                    if (SetGame.isHelpEnabled_onBlankShowSet) {
                        int[] setIndxs = new int[3];
                        boolean isSetFound = findSet(tableCards, setIndxs);
                        if (isSetFound) {
                            game.showSet(tableCards, setIndxs);
                            continue;
                        }
                    }
                    if (deck.isEmpty()) {
                        game.gui.consolePrintln("\nGame is over. Thanks for playing!");
                        break;  // Game over. User could not find Set, and deck is empty
                    } else {
                        // Lay down 3 more cards
                        deck.dealThreeIntoOther(tableCards);
                        continue;
                    }
                }
            }  // Done with this deck

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
    public static boolean findSet(SetCardCollection cards, int[] indxs) {
        int size = cards.size();

        for (int i = 0; i < size - 2; ++i) {
            for (int j = i + 1; j < size - 1; ++j) {
                for (int k = j + 1; k < size; ++k) {
                    SetCard[] trio = { cards.getCard(i), cards.getCard(j), cards.getCard(k) };
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

    public static boolean isSet(SetCard[] cards) {
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

    public void printGameStats(int numSetsFound, int tableCardCount, int deckCardCount) {
        String setCountStr = numSetsFound == 0 ? "No Sets"
                                 : ((numSetsFound == 1) ? "One Set"
                                 : String.format("%d Sets", numSetsFound));
        String tableCountStr = String.format("%d cards", tableCardCount);
        String deckCountStr = tableCardCount == 0 ? "No cards"
                                 : String.format("%d cards", deckCardCount);

        String outStr = String.format("%s found. %s on the table. %s in the deck.",
                                          setCountStr, tableCountStr, deckCountStr);
        this.gui.consolePrintln(outStr);
    }

    public void printSet(SetCardCollection cards, int[] indxs) {
        this.gui.consolePrintln(
            "Cards #" + String.valueOf(indxs[0])
            +   ", #" + String.valueOf(indxs[1])
            +   ", #" + String.valueOf(indxs[2])
            +   " form a set.");
    }

    public boolean queryUserReplay() throws IOException {
        String reply;
        BufferedReader in;

        in = new BufferedReader(new InputStreamReader(System.in));
        this.gui.consolePrint("\nPlay again (y/n)? ");
        reply = in.readLine();
        this.gui.consolePrintln("");
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
            this.gui.consolePrint("\nEnter three card #numbers (or <RETURN> for no Set found): ");
            String userInput = in.readLine().trim();
            if (userInput.isEmpty()) {
                return Optional.empty();  // User did not find set
            }
            Matcher matcher = regex.matcher(userInput);
            if (!matcher.find()) {
                this.gui.consolePrintln("Error: Did not recognize input format.");
                continue;
            }
            int[] cardIndxs = Arrays.stream(userInput.split(" "))
                                  .filter(s -> s.length() > 0)
                                  .mapToInt(Integer::parseInt)
                                  .toArray();
            boolean areIndxsValid = true;
            for (int cardIndx : cardIndxs) {
                if (cardIndx < 0 || cardIndx >= tableCardCount) {
                    this.gui.consolePrint(String.format("Error: Cards #%d is out of range (0 to %d)",
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

    public boolean getHelpEnabled_onBlankShowSet() {
        return this.isHelpEnabled_onBlankShowSet;
    }

    public boolean getMercyEnabled() {
        return this.isMercyEnabled;
    }

    public void setHelpEnabled_onBlankShowSet(boolean isEnabled) {
        this.isHelpEnabled_onBlankShowSet = isEnabled;
    }

    public void setMercyEnabled(boolean isEnabled) {
        this.isMercyEnabled = isEnabled;
    }

    public void showSet(SetCardCollection tableCards, int[] indxs) {
        this.gui.consolePrint("\nSet: ");
        printSet(tableCards, indxs);
    }
}
