package net.darmo_creations.minesweeper.model;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

import net.darmo_creations.minesweeper.Images;
import net.darmo_creations.minesweeper.events.CellClickedEvent;
import net.darmo_creations.minesweeper.gui.MainFrame.CellLabel;

public class Grid {
  private Cell[][] grid;
  /** Remaining number of flags. */
  private int flags;
  private int minesNb;
  private CellLabelProvider cellLabelProvider;

  public static final int WIN = 1;
  public static final int LOST = -1;

  public Grid(Difficulty difficulty, CellLabelProvider cellLabelProvider) {
    this.grid = new Cell[difficulty.getRows()][difficulty.getColumns()];
    this.minesNb = difficulty.getMines();
    this.flags = this.minesNb;
    for (int row = 0; row < difficulty.getRows(); row++) {
      for (int col = 0; col < difficulty.getColumns(); col++) {
        this.grid[row][col] = new Cell(row, col);
      }
    }
    this.cellLabelProvider = cellLabelProvider;
  }

  public int getRemainingFlags() {
    return this.flags;
  }

  public int getRows() {
    return this.grid.length;
  }

  public int getColumns() {
    return this.grid[0].length;
  }

  public void generateMines(int clickedRow, int clickedCol) {
    Random rand = new Random();
    for (int i = 0; i < this.minesNb; i++) {
      int col, row;
      do {
        col = rand.nextInt(getColumns());
        row = rand.nextInt(getRows());
      } while (this.grid[row][col].isMine() || row == clickedRow && col == clickedCol);

      this.grid[row][col].setMine(true);
    }
  }

  public int performMainClick(CellClickedEvent event) {
    Point p = event.getCell().getCoordinates();
    int col = p.x, row = p.y;
    Cell cell = this.grid[row][col];
    CellLabel label = event.getCell();

    if (!cell.isFlagged()) {
      int nearbyMines = getNearbyMinesNumber(row, col);

      if (getNearbyFlagsNumber(row, col) == nearbyMines) {
        if (row > 0)
          exploreGrid(row - 1, col, false);
        if (row > 0 && col < getColumns() - 1)
          exploreGrid(row - 1, col + 1, false);
        if (col < getColumns() - 1)
          exploreGrid(row, col + 1, false);
        if (row < getRows() - 1 && col < getColumns() - 1)
          exploreGrid(row + 1, col + 1, false);
        if (row < getRows() - 1)
          exploreGrid(row + 1, col, false);
        if (row < getRows() - 1 && col > 0)
          exploreGrid(row + 1, col - 1, false);
        if (col > 0)
          exploreGrid(row, col - 1, false);
        if (row > 0 && col > 0)
          exploreGrid(row - 1, col - 1, false);
        if (checkVictory()) {
          return WIN;
        }
      }
      else {
        int res = cell.click(nearbyMines);

        switch (res) {
          case Cell.NOTHING:
            label.click();
            label.setBackground(new Color(150, 150, 150));
            label.setIcon(Images.NUMBERS[nearbyMines]);
            if (nearbyMines == 0)
              exploreGrid(row, col, true);
            if (checkVictory()) {
              return WIN;
            }
            break;
          case Cell.MINE:
            label.click();
            label.setBackground(Color.RED);
            label.setIcon(Images.MINE);
            return LOST;
        }
      }
    }
    return 0;
  }

  public void performSecondaryClick(CellClickedEvent event) {
    Point p = event.getCell().getCoordinates();
    int col = p.x, row = p.y;
    Cell cell = this.grid[row][col];
    CellLabel label = event.getCell();

    if (cell.isFlagged()) {
      cell.setMarked(true);
      label.setIcon(Images.MARK);
      this.flags++;
    }
    else if (cell.isMarked()) {
      cell.setMarked(false);
      label.setIcon(Images.EMPTY_CELL);
    }
    else {
      cell.setFlagged(true);
      label.setIcon(Images.FLAG);
      this.flags--;
    }
  }

  /**
   * Explores all the non-clicked cells from the given starting cell that don't have any mines
   * nearby.
   * 
   * @param row the starting row
   * @param col the starting column
   * @param ignoreCenter if true, the center cell (the one that was clicked) will be ignored
   */
  private void exploreGrid(int row, int col, boolean ignoreCenter) {
    Cell cell = this.grid[row][col];

    if ((!ignoreCenter && cell.isClicked()) || cell.isFlagged())
      return;

    if (!ignoreCenter) {
      int mines = getNearbyMinesNumber(row, col);
      cell.click(mines);
      CellLabel label = this.cellLabelProvider.getLabel(row, col);
      label.click();
      label.setBackground(new Color(150, 150, 150));
      label.setIcon(Images.NUMBERS[mines]);
    }

    if (getNearbyMinesNumber(row, col) == 0) {
      if (row > 0)
        exploreGrid(row - 1, col, false);
      if (row > 0 && col < getColumns() - 1)
        exploreGrid(row - 1, col + 1, false);
      if (col < getColumns() - 1)
        exploreGrid(row, col + 1, false);
      if (row < getRows() - 1 && col < getColumns() - 1)
        exploreGrid(row + 1, col + 1, false);
      if (row < getRows() - 1)
        exploreGrid(row + 1, col, false);
      if (row < getRows() - 1 && col > 0)
        exploreGrid(row + 1, col - 1, false);
      if (col > 0)
        exploreGrid(row, col - 1, false);
      if (row > 0 && col > 0)
        exploreGrid(row - 1, col - 1, false);
    }
  }

  public void endGame() {
    for (int row = 0; row < getRows(); row++) {
      for (int col = 0; col < getColumns(); col++) {
        Cell cell = this.grid[row][col];
        CellLabel label = this.cellLabelProvider.getLabel(row, col);

        label.lock();
        if (cell.isMine()) {
          label.setIcon(Images.MINE);
        }
        else if (!cell.isMine() && cell.isFlagged()) {
          label.setIcon(Images.WRONG_MINE);
        }
      }
    }
  }

  /**
   * Returns {@code true} if the only remaining cells contain mines; false otherwise.
   */
  private boolean checkVictory() {
    boolean win = true;

    loop: for (int row = 0; row < getRows(); row++) {
      for (int col = 0; col < getColumns(); col++) {
        if (!this.grid[row][col].isClicked() && !this.grid[row][col].isMine()) {
          win = false;
          break loop;
        }
      }
    }

    return win;
  }

  /**
   * Returns the number of mines in the 8 adjacent cells to the one at the specified coordinates.
   * 
   * @param row the row
   * @param col the column
   * 
   * @return the number of nearby mines
   */
  private int getNearbyMinesNumber(int row, int col) {
    return (row > 0 ? booleanToInt(this.grid[row - 1][col].isMine()) : 0) + //
        (col < this.grid[0].length - 1 && row > 0 ? booleanToInt(this.grid[row - 1][col + 1].isMine()) : 0) + //
        (col < this.grid[0].length - 1 ? booleanToInt(this.grid[row][col + 1].isMine()) : 0) + //
        (col < this.grid[0].length - 1 && row < this.grid.length - 1 ? booleanToInt(this.grid[row + 1][col + 1].isMine()) : 0) + //
        (row < this.grid.length - 1 ? booleanToInt(this.grid[row + 1][col].isMine()) : 0) + //
        (col > 0 && row < this.grid.length - 1 ? booleanToInt(this.grid[row + 1][col - 1].isMine()) : 0) + //
        (col > 0 ? booleanToInt(this.grid[row][col - 1].isMine()) : 0) + //
        (col > 0 && row > 0 ? booleanToInt(this.grid[row - 1][col - 1].isMine()) : 0);
  }

  /**
   * Returns the number of flagged cells in the 8 adjacent cells to the one at the specified
   * coordinates.
   * 
   * @param row the row
   * @param col the column
   * 
   * @return the number of nearby flagged cells
   */
  private int getNearbyFlagsNumber(int row, int col) {
    return (row > 0 ? booleanToInt(this.grid[row - 1][col].isFlagged()) : 0) + //
        (col < this.grid[0].length - 1 && row > 0 ? booleanToInt(this.grid[row - 1][col + 1].isFlagged()) : 0) + //
        (col < this.grid[0].length - 1 ? booleanToInt(this.grid[row][col + 1].isFlagged()) : 0) + //
        (col < this.grid[0].length - 1 && row < this.grid.length - 1 ? booleanToInt(this.grid[row + 1][col + 1].isFlagged()) : 0) + //
        (row < this.grid.length - 1 ? booleanToInt(this.grid[row + 1][col].isFlagged()) : 0) + //
        (col > 0 && row < this.grid.length - 1 ? booleanToInt(this.grid[row + 1][col - 1].isFlagged()) : 0) + //
        (col > 0 ? booleanToInt(this.grid[row][col - 1].isFlagged()) : 0) + //
        (col > 0 && row > 0 ? booleanToInt(this.grid[row - 1][col - 1].isFlagged()) : 0);
  }

  private int booleanToInt(boolean value) {
    return value ? 1 : 0;
  }
}
