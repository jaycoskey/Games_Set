import java.util.stream.IntStream;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.plaf.ColorUIResource;


public class SetGui extends JFrame {
    public static int SIZE_FONT = 18;
    public static int SIZE_X = 1600;
    public static int SIZE_Y = 800;

    private JTextArea consoleArea;
    private JPanel consolePanel;
    // private JLabel consolePrompt;  // TODO
    // private JTextField consoleInput;  // TODO

    SetGui() {
        this.consoleArea = getConsoleArea();
        this.consolePanel = getConsolePanel(this.consoleArea);
        activate();
    }

    private void activate() {
        JFrame mainWindow = new JFrame("Set Game");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setContentPane(this.consolePanel);
        mainWindow.setSize(SetGui.SIZE_X, SetGui.SIZE_Y);
        mainWindow.setVisible(true);
    }

    private JTextArea getConsoleArea() {
        JTextArea consoleArea = new JTextArea();
        consoleArea.setEditable(true);
        consoleArea.setLineWrap(true);
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.WHITE);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, SetGui.SIZE_FONT);
        consoleArea.setFont(font);
        // IntStream.range(0, 100).forEach((k) -> consoleArea.append("Test\na\nb\nc\nd\ne\n"));
        return consoleArea;
    }

    private JPanel getConsolePanel(JTextArea consoleArea) {
        JScrollPane consolePane = new JScrollPane(consoleArea);
        consolePane.setPreferredSize(new Dimension(SetGui.SIZE_X, SetGui.SIZE_Y));
        consolePane.setViewportView(consoleArea);
        consolePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consolePane.getVerticalScrollBar().setBackground(Color.YELLOW);
        // UIManager.put("ScrollBar.highlight", new ColorUIResource(Color.RED));
        consolePane.setVisible(true);

        JPanel console = new JPanel();
        console.add(consolePane);
        console.setOpaque(true);
        return console;
    }

    public void print(String s) {
        this.consoleArea.append(s);
    }

    public void println(String s) {
        this.print(s + "\n");
    }
}
