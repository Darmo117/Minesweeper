package net.darmo_creations.minesweeper.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;

public class MainFrame extends JFrame implements ActionListener {
    private static final long serialVersionUID = 5041068026834586876L;
    
    private static final String FRAME_TITLE = "Minesweeper";
    private static final String REM_TEXT = "Mines: ";
    
    private boolean tabletMode;
    
    private AboutFrame aboutFrame;
    /**
     * The game's difficulty.
     */
    private Difficulty difficulty;
    /**
     * Tells if a game has been started.
     */
    private boolean started;
    /**
     * Tells if the current game is finished.
     */
    private boolean finished;
    /**
     * The remaining number of flags.
     */
    private int flags;
    /**
     * The timer.
     */
    private Timer timer;
    
    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource(Constants.FRAME_ICON)).getImage());
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
        
        tabletMode = false;
        
        aboutFrame = new AboutFrame(this, true);
        
        remainingLbl = new JLabel(REM_TEXT);
        timeLbl = new JLabel();
        
        remainingLbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
        timeLbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
        
        JPanel infoPnl = new JPanel(new BorderLayout());
        JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        leftPnl.add(remainingLbl);
        rightPnl.add(timeLbl);
        infoPnl.add(leftPnl, BorderLayout.WEST);
        infoPnl.add(rightPnl, BorderLayout.EAST);
        
        setJMenuBar(initJMenuBar());
        
        gridPnl = new JPanel();
        ((FlowLayout) gridPnl.getLayout()).setHgap(15);
        ((FlowLayout) gridPnl.getLayout()).setVgap(15);
        resetGame();
        
        getContentPane().add(infoPnl, BorderLayout.NORTH);
        getContentPane().add(gridPnl, BorderLayout.CENTER);
        
        setGameDifficulty(Difficulty.EASY);
        updateTimer(0, 0, 0);
        
        pack();
        centerFrame();
        setLocationRelativeTo(null);
    }
    
    private JMenuBar initJMenuBar() {
        JMenuBar menubar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        diffMenu = new JMenu("Difficulty");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        JMenuItem i;
        
        gameMenu.setMnemonic('G');
        diffMenu.setMnemonic('D');
        helpMenu.setMnemonic('H');
        
        gameMenuItems = new LinkedHashMap<String, JMenuItem>();
        gameMenuItems.put("new", i = new JMenuItem("New Game"));
        i.setMnemonic('N');
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        gameMenuItems.put("tablet", i = new JCheckBoxMenuItem("Tablet Mode", tabletMode));
        i.setMnemonic('T');
        gameMenuItems.put("exit", i = new JMenuItem("Exit"));
        i.setMnemonic('E');
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        
        for (String key : gameMenuItems.keySet()) {
            i = gameMenuItems.get(key);
            i.addActionListener(this);
            i.setActionCommand(key);
            gameMenu.add(i);
        }
        
        diffMenuItems = new LinkedHashMap<String, JMenuItem>();
        ButtonGroup bg = new ButtonGroup();
        for (Difficulty diff : Difficulty.values()) {
            String cmd = diff.name().toLowerCase();
            
            diffMenuItems.put(cmd, i = new JRadioButtonMenuItem(diff.getName(), diff == Difficulty.EASY));
            i.addActionListener(this);
            i.setActionCommand(cmd);
            bg.add(i);
            diffMenu.add(i);
        }
        
        about.addActionListener(this);
        about.setActionCommand("about");
        helpMenu.add(about);
        
        menubar.add(gameMenu);
        menubar.add(diffMenu);
        menubar.add(helpMenu);
        
        return menubar;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        switch (cmd) {
            case "new":
                resetGame();
                break;
            case "tablet":
                updateDisplayMode();
                break;
            case "exit":
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                break;
            case "easy":
                setGameDifficulty(Difficulty.EASY);
                break;
            case "normal":
                setGameDifficulty(Difficulty.NORMAL);
                break;
            case "hard":
                setGameDifficulty(Difficulty.HARD);
                break;
            case "expert":
                setGameDifficulty(Difficulty.EXPERT);
                break;
            case "about":
                aboutFrame.setVisible(true);
                break;
        }
    }
    
    /**
     * Resets the game.
     */
    public void resetGame() {
        if (difficulty == null) return;
        
        if (grid != null) gridPnl.remove(grid);
        if (timer != null && timer.isAlive()) timer.terminate();
        
        timer = new Timer(this);
        
        started = finished = false;
        flags = difficulty.getMines();
        grid = new JPanel(new GridLayout(difficulty.getRows(), difficulty.getColumns()));
        cells = new Cell[difficulty.getRows()][difficulty.getColumns()];
        
        updateRemainingMines();
        updateTimer(0, 0, 0);
        
        // Grid generation.
        for (int row = 0; row < difficulty.getRows(); row++) {
            for (int col = 0; col < difficulty.getColumns(); col++) {
                Cell cell = new Cell(tabletMode);
                
                cell.addMouseListener(new CellClickListener(this, cell, row, col));
                cells[row][col] = cell;
                grid.add(cells[row][col]);
            }
        }
        
        grid.setBorder(new BevelBorder(BevelBorder.LOWERED));
        gridPnl.add(grid);
        
        gameMenuItems.get("tablet").setEnabled(true);
        diffMenu.setEnabled(true);
        
        pack();
        repaint();
    }
    
    /**
     * Starts a new game.
     */
    public void startGame(int clickedR, int clickedC) {
        if (!started && !finished) {
            started = true;
            gameMenuItems.get("tablet").setEnabled(false);
            diffMenu.setEnabled(false);
            generateMines(clickedR, clickedC);
            timer.start();
        }
    }
    
    /**
     * Generates the mines in the grid.
     */
    private void generateMines(int avoidR, int avoidC) {
        for (int i = 0; i < difficulty.getMines(); i++) {
            int col, row;
            Random rand = new Random();
            
            do {
                col = rand.nextInt(difficulty.getColumns());
                row = rand.nextInt(difficulty.getRows());
            } while (cells[row][col].isMine() || row == avoidR && col == avoidC);
            
            cells[row][col].setMine(true);
        }
    }
    
    /**
     * Ends the game. Disables all the cells.
     * 
     * @param victory tells if the player has won or not
     */
    public void endGame(boolean victory) {
        String title = victory ? "Victory!" : "Game Over";
        String msg = (victory ? "You win!" : "You lose!") + "\nDo you want to play again?";
        
        finished = true;
        timer.terminate();
        for (int row = 0; row < difficulty.getRows(); row++) {
            for (int col = 0; col < difficulty.getColumns(); col++) {
                cells[row][col].showMine();
            }
        }
        gameMenuItems.get("tablet").setEnabled(true);
        diffMenu.setEnabled(true);
        
        int choice = JOptionPane.showConfirmDialog(this, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) resetGame();
    }
    
    /**
     * Explores all the non-clicked cells from the given starting cell that don't have any mines
     * nearby.
     * 
     * @param row the starting row
     * @param col the starting column
     */
    public void exploreGrid(int row, int col) {
        if (cells[row][col].isClicked() || cells[row][col].isFlagged()) return;
        
        int mines = getNearbyMinesNumber(row, col);
        
        cells[row][col].click(mines);
        if (mines == 0) {
            if (row > 0) exploreGrid(row - 1, col);
            if (row > 0 && col < difficulty.getColumns() - 1) exploreGrid(row - 1, col + 1);
            if (col < difficulty.getColumns() - 1) exploreGrid(row, col + 1);
            if (row < difficulty.getRows() - 1 && col < difficulty.getColumns() - 1) exploreGrid(row + 1, col + 1);
            if (row < difficulty.getRows() - 1) exploreGrid(row + 1, col);
            if (row < difficulty.getRows() - 1 && col > 0) exploreGrid(row + 1, col - 1);
            if (col > 0) exploreGrid(row, col - 1);
            if (row > 0 && col > 0) exploreGrid(row - 1, col - 1);
        }
    }
    
    /**
     * @return {@code true} if the only remaining cells contain mines; false otherwise
     */
    public boolean checkVictory() {
        if (!started) return false;
        
        boolean win = true;
        
        loop: for (int row = 0; row < difficulty.getRows(); row++) {
            for (int col = 0; col < difficulty.getColumns(); col++) {
                if (!cells[row][col].isClicked() && !cells[row][col].isMine()) {
                    win = false;
                    break loop;
                }
            }
        }
        
        return win;
    }
    
    public void putFlag() {
        if (started && !finished) flags--;
        updateRemainingMines();
    }
    
    public void removeFlag() {
        if (started && !finished) flags++;
        updateRemainingMines();
    }
    
    public void updateRemainingMines() {
        remainingLbl.setText(REM_TEXT + flags);
    }
    
    public void updateTimer(int h, int m, int s) {
        timeLbl.setText((h != 0 ? addLeadingZero(h) + ":" : "") + addLeadingZero(m) + ":" + addLeadingZero(s));
    }
    
    private String addLeadingZero(int v) {
        return v >= 0 && v < 10 ? "0" + v : "" + v;
    }
    
    private void updateDisplayMode() {
        tabletMode = !tabletMode;
        resetGame();
    }
    
    /**
     * Returns the number of mines in the 8 adjacent cells to the one at the specified coordinates.
     * 
     * @param row the row
     * @param col the column
     * 
     * @return the number of nearby mines
     */
    public int getNearbyMinesNumber(int row, int col) {
        return (row > 0 ? booleanToInt(cells[row - 1][col].isMine()) : 0) + //
                (col < cells[0].length - 1 && row > 0 ? booleanToInt(cells[row - 1][col + 1].isMine()) : 0) + //
                (col < cells[0].length - 1 ? booleanToInt(cells[row][col + 1].isMine()) : 0) + //
                (col < cells[0].length - 1 && row < cells.length - 1 ? booleanToInt(cells[row + 1][col + 1].isMine()) : 0) + //
                (row < cells.length - 1 ? booleanToInt(cells[row + 1][col].isMine()) : 0) + //
                (col > 0 && row < cells.length - 1 ? booleanToInt(cells[row + 1][col - 1].isMine()) : 0) + //
                (col > 0 ? booleanToInt(cells[row][col - 1].isMine()) : 0) + //
                (col > 0 && row > 0 ? booleanToInt(cells[row - 1][col - 1].isMine()) : 0);
    }
    
    /**
     * Converts a boolean to an integer.
     * 
     * @param value the boolean
     * 
     * @return 1 if true; 0 otherwise
     */
    private int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }
    
    public boolean isInTabletMode() {
        return tabletMode;
    }
    
    public Difficulty getGameDifficulty() {
        return difficulty;
    }
    
    public void setGameDifficulty(Difficulty difficulty) {
        if (started && !finished) return;
        
        this.difficulty = difficulty;
        setTitle(FRAME_TITLE + " - " + difficulty.getName());
        resetGame();
    }
    
    public boolean gameIsStarted() {
        return started;
    }
    
    public boolean gameIsFinished() {
        return finished;
    }
    
    private void centerFrame() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        setLocation(dim.width / 2 - getPreferredSize().width / 2, dim.height / 2 - getPreferredSize().height / 2);
    }
    
    private JMenu diffMenu;
    private Map<String, JMenuItem> gameMenuItems, diffMenuItems;
    private JLabel remainingLbl, timeLbl;
    private JPanel gridPnl, grid;
    private Cell[][] cells;
}