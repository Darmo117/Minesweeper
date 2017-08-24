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
package net.darmo_creations.minesweeper.model;

/**
 * This class represents a grid cell.
 *
 * @author Damien Vergnet
 */
public class Cell {
  private boolean clicked;
  private boolean mine;
  private boolean flagged;
  private boolean marked;

  /**
   * Creates a cell.
   * 
   * @param tabletMode if true, cells will be bigger
   */
  public Cell(int row, int column) {
    this.mine = false;
    this.flagged = false;
    this.marked = false;
  }

  public boolean isClicked() {
    return this.clicked;
  }

  public boolean isMine() {
    return this.mine;
  }

  public void setMine(boolean mine) {
    if (!isClicked())
      this.mine = mine;
  }

  public boolean isFlagged() {
    return this.flagged;
  }

  public void setFlagged(boolean flagged) {
    if (!this.clicked) {
      this.marked = false;
      this.flagged = flagged;
    }
  }

  public boolean isMarked() {
    return this.marked;
  }

  public void setMarked(boolean marked) {
    if (!this.clicked) {
      this.flagged = false;
      this.marked = marked;
    }
  }

  public static final int ALREADY_CLICKED = -2;
  public static final int CANNOT_CLICK = -1;
  public static final int NOTHING = 0;
  public static final int MINE = 1;

  /**
   * Clicks this cell then returns the result for this action.
   * 
   * @param mines the number of mine around this cell
   * @return one of those values: {@code ALREADY_CLICKED}, {@code CANNOT_CLICK}, {@code NOTHING},
   *         {@code MINE}
   */
  public int click(int mines) {
    if (mines < 0 || mines > 9)
      throw new IllegalArgumentException("mines number should be inside [0, 8], given " + mines);

    if (this.clicked)
      return ALREADY_CLICKED;
    if (this.flagged)
      return CANNOT_CLICK;

    this.clicked = true;
    if (this.mine) {
      return MINE;
    }
    return NOTHING;
  }
}
