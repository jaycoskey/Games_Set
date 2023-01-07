// In the card game Set, each card has 4 properties:
// 1. Color:   Each card is red, green, or purple.
// 2. Symbol:  Each card contains ovals, squiggles, or diamonds.
// 3. Number:  Each card has one, two, or three symbols.
// 4. Shading: Each card is solid, open, or striped.
class SetCard {
    public SetCard(int x) { id = x; }
    protected int id;

    public void printNumeric() {
        for (int d = 3; d >= 0; --d) {
            System.out.print(getTernaryDigit(d));
        }
        System.out.print("(");
        System.out.print(id);
        System.out.print(") ");
        if (id < 10) { System.out.print(" "); }
    }
    protected int getTernaryDigit(int d) {
        int n = id;
        n /= Math.pow(3, d);	 // Shift right to drop unneeded digits
        return n - 3 * (n / 3);  // Get last ternary digit
    }
}
