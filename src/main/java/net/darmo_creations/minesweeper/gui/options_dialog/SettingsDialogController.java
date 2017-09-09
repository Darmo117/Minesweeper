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
package net.darmo_creations.minesweeper.gui.options_dialog;

import java.awt.event.ActionEvent;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.darmo_creations.gui_framework.config.WritableConfig;
import net.darmo_creations.gui_framework.config.tags.IntegerTag;
import net.darmo_creations.minesweeper.ConfigTags;
import net.darmo_creations.utils.swing.dialog.DefaultDialogController;

class SettingsDialogController extends DefaultDialogController<SettingsDialog> implements ChangeListener {
  private WritableConfig config;

  public SettingsDialogController(SettingsDialog dialog) {
    super(dialog);
  }

  public WritableConfig getConfig() {
    return this.config;
  }

  public void setConfig(WritableConfig config) {
    this.config = config;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);

    if (this.dialog.isVisible()) {
      if ("default_size".equals(e.getActionCommand())) {
        IntegerTag tag = ConfigTags.BUTTONS_SIZE;
        int value = WritableConfig.getDefaultValue(tag);

        this.config.setValue(tag, value);
        this.dialog.setButtonsSizeValue(value);
      }
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    JSpinner spin = (JSpinner) e.getSource();

    if ("buttons_size".equals(spin.getName()))
      this.config.setValue(ConfigTags.BUTTONS_SIZE, (int) spin.getValue());
  }
}
