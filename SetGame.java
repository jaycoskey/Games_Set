import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;


class SetGame {
    public static boolean isHelpEnabled = true;   // Show user a Set if one is present that they cannot find.
    public static boolean isMercyEnabled = false;  // TODO: End game if deck is empty and no more Sets are present.

    // TODO: Refactor into smaller pieces.
    public static void main(String [] args) throws EmptyException, IOException {
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
                deck.printSize();
                tableCards.printNumeric();

                Optional<int[]> optUserResponse = queryUserSet();
                if (optUserResponse.isPresent()) {
                    // User claims to have found a set
                    int[] userSetIndxs = optUserResponse.get();
                    SetCard[] cards = { tableCards.getCard(userSetIndxs[0]),
                                        tableCards.getCard(userSetIndxs[1]),
                                        tableCards.getCard(userSetIndxs[2])
                                        };
                    if (isSet(cards)) {
                        // User found a set
                        System.out.println("*** You found a set!");
                        tableCards.removeThree(userSetIndxs);
                        if (tableCards.isEmpty()) {
                            System.out.println("You cleared the board. Congratulations!!");
                            break;  // Game over --- Board cleared
                        }
                        if (tableCards.size() < SetDeck.BASE_OUTLAY_COUNT) {
                            if (!deck.isEmpty()) {
                                deck.dealThreeIntoOther(tableCards);
                            }
                            continue;
                        }
                    } else {
                        System.out.println("*** No, that's not a set.");
                        continue;
                    }
                } else {
                    assert(optUserResponse.isEmpty());
                    if (SetGame.isHelpEnabled) {
                        int[] setIndxs = new int[3];
                        boolean isSetFound = findSet(tableCards, setIndxs);
                        if (isSetFound) {
                            showSet(tableCards, setIndxs);
                            continue;
                        }
                    }
                    if (deck.isEmpty()) {
                        System.out.println("Game is over. Thanks for playing!");
                        break;  // Game over. User could not find Set, and deck is empty
                    } else {
                        // Lay down 3 more cards
                        deck.dealThreeIntoOther(tableCards);
                        continue;
                    }
                }
            }  // Done with this deck

            if (! queryUserReplay()) {
                System.out.println("Bye!");
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

    public static void printSet(SetCardCollection cards, int[] indxs) {
        System.out.println(
            "Cards #" + String.valueOf(indxs[0])
            +   ", #" + String.valueOf(indxs[1])
            +   ", #" + String.valueOf(indxs[2])
            +   " form a set.");
    }

    public static boolean queryUserReplay() throws IOException {
        String reply;
        BufferedReader in;

        in = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("\nPlay again (y/n)? ");
        reply = in.readLine();
        System.out.println("");
        reply = reply.toLowerCase().trim();
        if (reply.startsWith("n") || reply.startsWith("q")) {
            return false;
        }
        return true;
    }

    public static Optional<int[]> queryUserSet() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        for (;;) {
            System.out.print("\nEnter three card numbers (or <RETURN> for no set found): ");
            String userInput = in.readLine().trim();
            if (userInput.isEmpty()) {
                return Optional.empty();  // User did not find set
            }
            // TODO: Make robust against input errors, both in format and range.
            int[] cardIndxs = Arrays.stream(userInput.split(" ")).mapToInt(Integer::parseInt).toArray();
            return Optional.ofNullable(cardIndxs);
        }
        // NOT_REACHED
    }

    public static void showSet(SetCardCollection tableCards, int[] indxs) {
        System.out.print("Set: ");
        printSet(tableCards, indxs);
    }
}
