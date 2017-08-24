/*
 * Copyright © 2017 Damien Vergnet
 * 
 * This file is part of Minesweeper.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.darmo_creations.minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Random;

import javax.swing.JOptionPane;

import net.darmo_creations.gui_framework.ApplicationRegistry;
import net.darmo_creations.gui_framework.config.WritableConfig;
import net.darmo_creations.gui_framework.controllers.ApplicationController;
import net.darmo_creations.gui_framework.events.UserEvent;
import net.darmo_creations.minesweeper.MainFrame.CellLabel;
import net.darmo_creations.minesweeper.events.CellClickedEvent;
import net.darmo_creations.minesweeper.events.ChangeDifficultyEvent;
import net.darmo_creations.minesweeper.events.EventType;
import net.darmo_creations.minesweeper.model.Cell;
import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.minesweeper.model.Timer;
import net.darmo_creations.utils.I18n;
import net.darmo_creations.utils.events.SubsribeEvent;

public class MainController extends ApplicationController<MainFrame> {
  private Cell[][] grid;
  private Difficulty difficulty;
  private boolean started;
  private boolean finished;
  /** Number of used flags */
  private int flags;
  private Timer timer;

  private boolean tabletMode;

  public MainController(MainFrame frame, WritableConfig config) {
    super(frame, config);
  }

  @Override
  public void init() {
    super.init();
    this.tabletMode = this.config.getValue(ConfigTags.TABLET_MODE);
    setGameDifficulty(Difficulty.EASY);
    resetGame();
  }

  @Override
  @SubsribeEvent
  public void onUserEvent(UserEvent e) {
    super.onUserEvent(e);

    UserEvent.Type type = e.getType();
    if (type instanceof EventType) {
      switch ((EventType) type) {
        case NEW_GAME:
          resetGame();
          break;
        case TOGGLE_TABLET_MODE:
          toggleTabletMode();
          break;
      }
    }
  }

  @SubsribeEvent
  public void onChangeDifficulty(ChangeDifficultyEvent e) {
    setGameDifficulty(e.getDifficulty());
  }

  private void toggleTabletMode() {
    this.tabletMode = !this.tabletMode;
    resetGame();
  }

  private void setGameDifficulty(Difficulty difficulty) {
    if (!this.started || this.finished) {
      this.difficulty = difficulty;
      this.frame.setTitle(ApplicationRegistry.getApplication().getName() + " - " + difficulty.getName());
      resetGame();
    }
  }

  private void resetGame() {
    if (this.timer != null && this.timer.isAlive())
      this.timer.interrupt();

    this.timer = new Timer();

    this.started = this.finished = false;
    this.flags = this.difficulty.getMines();
    this.grid = new Cell[this.difficulty.getRows()][this.difficulty.getColumns()];

    // Grid generation.
    for (int row = 0; row < this.difficulty.getRows(); row++) {
      for (int col = 0; col < this.difficulty.getColumns(); col++) {
        this.grid[row][col] = new Cell(row, col);
      }
    }

    this.frame.resetGrid(new Dimension(this.difficulty.getColumns(), this.difficulty.getRows()), this.tabletMode);
    this.frame.setRemainingMines(this.flags);
    this.frame.setTimer(0, 0, 0);
    this.frame.updateMenus(false);
    this.frame.pack();
    this.frame.repaint();
  }

  @SubsribeEvent
  public void onCellClicked(CellClickedEvent e) {
    Point p = e.getCell().getCoordinates();
    if (!this.started) {
      startGame(p);
    }

    Cell cell = this.grid[p.y][p.x];
    CellLabel label = e.getCell();

    if (e.isMainClick() && !cell.isFlagged()) {
      int nearbyMines = getNearbyMinesNumber(p.y, p.x);
      int res = cell.click(nearbyMines);

      switch (res) {
        case Cell.NOTHING:
          label.setBackground(new Color(150, 150, 150));
          label.setIcon(Images.NUMBERS[nearbyMines]);
          if (nearbyMines == 0)
            exploreGrid(p.y, p.x);
          if (checkVictory()) {
            endGame(true);
          }
          break;
        case Cell.MINE:
          label.setBackground(Color.RED);
          label.setIcon(Images.MINE);
          endGame(false);
          break;
      }
    }
    else {
      if (cell.isFlagged()) {
        cell.setMarked(true);
        label.setIcon(Images.MARK);
        if (this.started && !this.finished)
          this.flags++;
        this.frame.setRemainingMines(this.flags);
      }
      else if (cell.isMarked()) {
        cell.setMarked(false);
        label.setIcon(Images.EMPTY_CELL);
      }
      else {
        cell.setFlagged(true);
        label.setIcon(Images.FLAG);
        if (this.started && !this.finished)
          this.flags--;
        this.frame.setRemainingMines(this.flags);
      }
    }
  }

  /**
   * Starts a new game.
   * 
   * @param clickedCell the clicked cell
   */
  private void startGame(Point clickedCell) {
    this.started = true;
    this.frame.updateMenus(true);

    // Mines generation
    for (int i = 0; i < this.difficulty.getMines(); i++) {
      int col, row;
      Random rand = new Random();

      do {
        col = rand.nextInt(this.difficulty.getColumns());
        row = rand.nextInt(this.difficulty.getRows());
      } while (this.grid[row][col].isMine() || row == clickedCell.y && col == clickedCell.x);

      this.grid[row][col].setMine(true);
    }

    this.timer.start();
  }

  /**
   * Ends the game. Disables all the cells.
   * 
   * @param victory tells if the player has won or not
   */
  private void endGame(boolean victory) {
    String title = victory ? I18n.getLocalizedString("popup.victory.title") : I18n.getLocalizedString("popup.game_over.title");
    String msg = (victory ? I18n.getLocalizedString("popup.victory.text") : I18n.getLocalizedString("popup.game_over.text")) + "\n"
        + I18n.getLocalizedString("popup.play_again.text");

    this.finished = true;
    this.timer.interrupt();

    for (int row = 0; row < this.difficulty.getRows(); row++) {
      for (int col = 0; col < this.difficulty.getColumns(); col++) {
        Cell cell = this.grid[row][col];
        CellLabel label = this.frame.getCell(new Point(col, row));

        label.lock();
        if (cell.isMine()) {
          label.setIcon(Images.MINE);
        }
        else if (!cell.isMine() && cell.isMarked()) {
          label.setIcon(Images.WRONG_MINE);
        }
      }
    }
    this.frame.updateMenus(false);

    int choice = JOptionPane.showConfirmDialog(this.frame, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (choice == JOptionPane.YES_OPTION)
      resetGame();
  }

  /**
   * Explores all the non-clicked cells from the given starting cell that don't have any mines
   * nearby.
   * 
   * @param row the starting row
   * @param col the starting column
   */
  private void exploreGrid(int row, int col) {
    int mines = getNearbyMinesNumber(row, col);

    if (!this.grid[row][col].isClicked())
      ApplicationRegistry.EVENTS_BUS.dispatchEvent(new CellClickedEvent(this.frame.getCell(new Point(col, row)), true));
    if (mines == 0) {
      if (row > 0)
        exploreGrid(row - 1, col);
      if (row > 0 && col < this.difficulty.getColumns() - 1)
        exploreGrid(row - 1, col + 1);
      if (col < this.difficulty.getColumns() - 1)
        exploreGrid(row, col + 1);
      if (row < this.difficulty.getRows() - 1 && col < this.difficulty.getColumns() - 1)
        exploreGrid(row + 1, col + 1);
      if (row < this.difficulty.getRows() - 1)
        exploreGrid(row + 1, col);
      if (row < this.difficulty.getRows() - 1 && col > 0)
        exploreGrid(row + 1, col - 1);
      if (col > 0)
        exploreGrid(row, col - 1);
      if (row > 0 && col > 0)
        exploreGrid(row - 1, col - 1);
    }
  }

  /**
   * Returns {@code true} if the only remaining cells contain mines; false otherwise.
   */
  private boolean checkVictory() {
    if (!this.started)
      return false;

    boolean win = true;

    loop: for (int row = 0; row < this.difficulty.getRows(); row++) {
      for (int col = 0; col < this.difficulty.getColumns(); col++) {
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

  private int booleanToInt(boolean value) {
    return value ? 1 : 0;
  }
}
