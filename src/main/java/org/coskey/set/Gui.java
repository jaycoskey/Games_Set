package org.coskey.set;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.plaf.ColorUIResource;


public class Gui extends JFrame {
    public static int SIZE_FONT = 18;
    public static int SIZE_X = 1600;
    public static int SIZE_Y = 800;

    public static String MENU_FILE = "File";
    public static String MENU_FILE_EXIT = "Exit";

    public static String MENU_HELP = "Help";
    public static String MENU_HELP_ON_BLANK_SHOW_SET = "Show one Sets when drawing a blank";

    private JTextArea consoleArea;
    private JPanel consolePanel;
    private JMenuBar menuBar;

    private Game game;
    // TODO:Next: Add display of card table: private CardTable cardTable;

    // private JLabel consolePrompt;  // TODO
    // private JTextField consoleInput;  // TODO

    Gui(Game game) {
        this.game = game;
        // TODO:Next: this.cardTable = new CardTable();
        this.consoleArea = getInitConsoleArea();
        this.consolePanel = getInitConsolePanel(this.consoleArea);
        this.menuBar = this.getInitMenuBar();
        activate();
    }

    private void activate() {
        JFrame mainWindow = new JFrame("Set Game");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(Gui.SIZE_X, Gui.SIZE_Y);
        mainWindow.setJMenuBar(this.menuBar);
        mainWindow.setLayout(new BoxLayout(mainWindow.getContentPane(), BoxLayout.Y_AXIS));

        // mainWindow.add(this.cardTable);
        mainWindow.add(this.consolePanel);
        consolePrint("");
        mainWindow.setVisible(true);
    }

    public void consolePrint(String s) {
        this.consoleArea.append(s);
        this.consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    public void consolePrintln(String s) {
        this.consolePrint(s + "\n");
    }

    private JTextArea getInitConsoleArea() {
        JTextArea consoleArea = new JTextArea();
        consoleArea.setEditable(true);
        consoleArea.setLineWrap(true);
        consoleArea.setBackground(Color.BLACK);
        consoleArea.setForeground(Color.WHITE);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, Gui.SIZE_FONT);
        consoleArea.setFont(font);
        // IntStream.range(0, 100).forEach((k) -> consoleArea.append("Test\na\nb\nc\nd\ne\n"));
        return consoleArea;
    }

    private JPanel getInitConsolePanel(JTextArea consoleArea) {
        JScrollPane consolePane = new JScrollPane(consoleArea);
        consolePane.setPreferredSize(new Dimension(Gui.SIZE_X, Gui.SIZE_Y));
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

    private JMenuBar getInitMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu(Gui.MENU_FILE);
        JMenuItem exit = new JMenuItem(Gui.MENU_FILE_EXIT);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                    System.exit(0);
                }
            });
        menuFile.add(exit);
        menuBar.add(menuFile);

        JMenu menuHelp = new JMenu(Gui.MENU_HELP);
        JMenuItem showSetOnBlank = new JMenuItem(Gui.MENU_HELP_ON_BLANK_SHOW_SET);
        showSetOnBlank.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                    boolean current = game.getHelpEnabled_onBlankShowSet();
                    game.setHelpEnabled_onBlankShowSet(!current);
                }
            });
        menuHelp.add(showSetOnBlank);
        menuBar.add(menuHelp);

        return menuBar;
    }

    // TODO:Next
    // public void updateCardTable(CardCollection tableCards) throws EmptyException, IOException {
    //     this.cardTable.update(tableCards);
    // }
}
