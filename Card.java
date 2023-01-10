// In the card game Set, each card has 4 properties:
// 1. Color:   Each card is red, green, or purple.
// 2. Symbol:  Each card contains ovals, squiggles, or diamonds.
// 3. Number:  Each card has one, two, or three symbols.
// 4. Shading: Each card is solid, open, or striped.
class Card {
    public Card(int x) { id = x; }

    protected int id;

    protected int getTernaryDigit(int d) {
        int n = id;
        n /= Math.pow(3, d);	 // Shift right to drop unneeded digits
        return n - 3 * (n / 3);  // Get last ternary digit
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int d = 3; d >= 0; --d) {
            str.append(getTernaryDigit(d));
        }
        str.append("(");
        str.append(id);
        str.append(") ");
        if (id < 10) { str.append(" "); }
        return str.toString();
    }
}
