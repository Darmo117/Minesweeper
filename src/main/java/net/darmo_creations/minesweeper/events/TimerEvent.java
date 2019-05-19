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

import net.darmo_creations.utils.events.AbstractEvent;

/**
 * This type of event is fired the Timer class.
 *
 * @author Damien Vergnet
 */
public class TimerEvent extends AbstractEvent {
  private final int hours, minutes, seconds;

  public TimerEvent(int hours, int minutes, int seconds) {
    super(false);
    this.hours = hours;
    this.minutes = minutes;
    this.seconds = seconds;
  }

  public int getHours() {
    return this.hours;
  }

  public int getMinutes() {
    return this.minutes;
  }

  public int getSeconds() {
    return this.seconds;
  }
}
