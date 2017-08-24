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

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.darmo_creations.gui_framework.ApplicationRegistry;
import net.darmo_creations.minesweeper.events.TimerEvent;

public class Timer extends Thread {
  @Override
  public void run() {
    long startTime = System.currentTimeMillis();

    while (!currentThread().isInterrupted()) {
      GregorianCalendar cal = new GregorianCalendar();

      cal.setTimeInMillis(System.currentTimeMillis() - startTime);
      ApplicationRegistry.EVENTS_BUS.dispatchEvent(
          new TimerEvent(cal.get(Calendar.HOUR_OF_DAY) - 1, cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)));
      try {
        Thread.sleep(500);
      }
      catch (InterruptedException ex) {
        return;
      }
    }
  }
}
