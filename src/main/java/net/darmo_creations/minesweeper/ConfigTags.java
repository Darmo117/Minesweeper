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

import net.darmo_creations.gui_framework.config.tags.BooleanTag;
import net.darmo_creations.gui_framework.config.tags.IntegerTag;

public final class ConfigTags {
  public static final BooleanTag SEND_SCORES = new BooleanTag("send_scores");
  public static final IntegerTag BUTTONS_SIZE = new IntegerTag("buttons_size");

  private ConfigTags() {}
}
