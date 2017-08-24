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

import javax.swing.ImageIcon;

import net.darmo_creations.gui_framework.util.ImagesUtil;

/**
 * This class holds all the images used in this application.
 * 
 * @author Damien Vergnet
 */
public final class Images {
  /** Digits 0 through 8. The index corresponds to the digits. */
  public static final ImageIcon[] NUMBERS;
  /** Empty image */
  public static final ImageIcon EMPTY_CELL = ImagesUtil.getIcon("/assets/icons/tiles/empty.png");
  /** Flag */
  public static final ImageIcon FLAG = ImagesUtil.getIcon("/assets/icons/tiles/flag.png");
  /** Question mark */
  public static final ImageIcon MARK = ImagesUtil.getIcon("/assets/icons/tiles/mark.png");
  /** Mine */
  public static final ImageIcon MINE = ImagesUtil.getIcon("/assets/icons/tiles/mine.png");
  /** Crossed out mine */
  public static final ImageIcon WRONG_MINE = ImagesUtil.getIcon("/assets/icons/tiles/mine_wrong.png");

  static {
    NUMBERS = new ImageIcon[9];
    NUMBERS[0] = EMPTY_CELL;
    for (int i = 1; i < NUMBERS.length; i++) {
      NUMBERS[i] = ImagesUtil.getIcon("/assets/icons/tiles/" + i + ".png");
    }
  }

  private Images() {}
}
