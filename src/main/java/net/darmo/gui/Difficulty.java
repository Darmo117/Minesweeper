package net.darmo.gui;

public enum Difficulty {
    EASY(9, 9, 10, "Easy"), NORMAL(16, 16, 40, "Normal"), HARD(32, 16, 100, "Hard"), EXPERT(32, 32, 200, "Expert");
    
    private int cols, rows, mines;
    private String name;
    
    private Difficulty(int cols, int rows, int mines, String name) {
        this.cols = cols;
        this.rows = rows;
        this.mines = mines;
        this.name = name;
    }
    
    public int getColumns() {
        return cols;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getMines() {
        return mines;
    }
    
    public String getName() {
        return name;
    }
}
