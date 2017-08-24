/*
 * Copyright © 2017 Damien Vergnet
 * 
 * This file is part of GUI-Framework.
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

import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.utils.events.AbstractEvent;

/**
 * This event is fired when changing the difficulty.
 *
 * @author Damien Vergnet
 */
public class ChangeDifficultyEvent extends AbstractEvent {
  private final Difficulty difficulty;

  /**
   * Creates an event
   * 
   * @param difficulty the desired difficulty
   */
  public ChangeDifficultyEvent(Difficulty difficulty) {
    this.difficulty = difficulty;
  }

  /**
   * Returns the desired difficulty.
   */
  public Difficulty getDifficulty() {
    return this.difficulty;
  }

  @Override
  public boolean isCancelable() {
    return false;
  }
}
