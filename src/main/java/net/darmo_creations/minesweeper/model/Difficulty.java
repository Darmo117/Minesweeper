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

import net.darmo_creations.utils.I18n;

/**
 * This enum defines difficulty levels.
 *
 * @author Damien Vergnet
 */
public enum Difficulty {
  EASY(9, 9, 10, I18n.getLocalizedString("difficulty.easy")),
  NORMAL(16, 16, 40, I18n.getLocalizedString("difficulty.normal")),
  HARD(32, 16, 100, I18n.getLocalizedString("difficulty.hard")),
  EXPERT(32, 32, 200, I18n.getLocalizedString("difficulty.expert"));

  private int columns, rows, mines;
  private String name;

  private Difficulty(int cols, int rows, int mines, String name) {
    this.columns = cols;
    this.rows = rows;
    this.mines = mines;
    this.name = name;
  }

  /**
   * The grid's number of columns.
   */
  public int getColumns() {
    return this.columns;
  }

  /**
   * The grid's number of rows.
   */
  public int getRows() {
    return this.rows;
  }

  /**
   * The grid's number of mines.
   */
  public int getMines() {
    return this.mines;
  }

  /**
   * Difficulty's localized name.
   */
  public String getName() {
    return this.name;
  }
}
