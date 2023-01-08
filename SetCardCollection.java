import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
// import java.util.sort;


class SetCardCollection {
    public SetCardCollection() {
        cards = new ArrayList<SetCard>();
    }

    public void add(SetCard sc) {
        cards.add(sc);
    }

    public void clear() throws EmptyException {
        cards.clear();
    }

    public SetCard getCard(int i) {
        return cards.get(i);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void printNumeric() {
        int maxColumns = 6;
        int rowCount = 3 + 3 * (int) ((size() - 1) / (3 * maxColumns));
        for (int row = 0; row < rowCount; ++row) {
            for (int cardNum = row; cardNum < size(); cardNum += rowCount) {
                System.out.print("#");
                System.out.print(cardNum);
                if (cardNum < 10) { System.out.print(" "); }
                System.out.print(": ");
                cards.get(cardNum).printNumeric();
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }

    public void removeThree(int[] indxs) {
        Arrays.sort(indxs);
        // Remove the elements in order of decreasing index
        cards.remove(indxs[2]);
        cards.remove(indxs[1]);
        cards.remove(indxs[0]);
        cards.trimToSize();
    }

    public int size() {
        return cards.size();
    }

    protected ArrayList<SetCard> cards;
}
