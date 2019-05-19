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

import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.darmo_creations.gui_framework.config.WritableConfig;
import net.darmo_creations.minesweeper.ConfigTags;
import net.darmo_creations.utils.I18n;
import net.darmo_creations.utils.swing.dialog.AbstractDialog;

public class SettingsDialog extends AbstractDialog {
  private static final long serialVersionUID = -8937031056348949112L;

  private SettingsDialogController controller;
  private JSpinner buttonsSizeSpin;

  public SettingsDialog(JFrame owner) {
    super(owner, Mode.VALIDATE_CANCEL_OPTION, true);

    setTitle(I18n.getLocalizedString("dialog.settings.title"));
    setResizable(false);

    this.controller = new SettingsDialogController(this);

    this.buttonsSizeSpin = new JSpinner(new SpinnerNumberModel(15, 15, 100, 1));
    this.buttonsSizeSpin.setName("buttons_size");
    this.buttonsSizeSpin.addChangeListener(this.controller);
    JButton defaultSizeBtn = new JButton(I18n.getLocalizedString("button.default.text"));
    defaultSizeBtn.addActionListener(this.controller);
    defaultSizeBtn.setActionCommand("default_size");
    JPanel p = new JPanel();
    p.add(new JLabel(I18n.getLocalizedString("label.buttons_size.text")));
    p.add(this.buttonsSizeSpin);
    p.add(defaultSizeBtn);
    add(p);

    setActionListener(this.controller);

    pack();
    setLocationRelativeTo(owner);
  }

  void setButtonsSizeValue(int value) {
    this.buttonsSizeSpin.setValue(value);
  }

  public void setConfig(WritableConfig config) {
    this.controller.setConfig(config);
    setButtonsSizeValue(config.getValue(ConfigTags.BUTTONS_SIZE));
  }

  public Optional<WritableConfig> getConfig() {
    return Optional.ofNullable(isCancelled() ? null : this.controller.getConfig());
  }
}
