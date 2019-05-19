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
package net.darmo_creations.minesweeper.events;

import net.darmo_creations.minesweeper.gui.MainFrame.CellLabel;
import net.darmo_creations.utils.events.AbstractEvent;

/**
 * This type of event is fired when a cell in the grid is clicked.
 *
 * @author Damien Vergnet
 */
public class CellClickedEvent extends AbstractEvent {
  private final CellLabel cell;
  private final boolean isMainClick;

  public CellClickedEvent(CellLabel cell, boolean isMainClick) {
    super(false);
    this.cell = cell;
    this.isMainClick = isMainClick;
  }

  public CellLabel getCell() {
    return this.cell;
  }

  public boolean isMainClick() {
    return this.isMainClick;
  }
}
