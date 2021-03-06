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
package net.darmo_creations.minesweeper.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.minesweeper.model.Score;
import net.darmo_creations.utils.I18n;
import net.darmo_creations.utils.swing.dialog.AbstractDialog;
import net.darmo_creations.utils.swing.dialog.DefaultDialogController;

/**
 * This dialog shows the highscores.
 *
 * @author Damien Vergnet
 */
public class ScoresDialog extends AbstractDialog {
  private static final long serialVersionUID = -1996609441443859066L;

  private JTabbedPane scoresTbl;
  private Map<Difficulty, JTable> tabs;

  public ScoresDialog(JFrame owner) {
    super(owner, Mode.CLOSE_OPTION, true);

    setTitle(I18n.getLocalizedString("dialog.scores.title"));
    setPreferredSize(new Dimension(200, 200));

    this.tabs = new HashMap<>();
    this.scoresTbl = new JTabbedPane();
    for (Difficulty difficulty : Difficulty.values()) {
      JTable p = new JTable(new DefaultTableModel(
          new String[]{I18n.getLocalizedString("label.username.text"), I18n.getLocalizedString("label.time.text")}, 0));
      p.setEnabled(false);
      p.getTableHeader().setReorderingAllowed(false);
      this.tabs.put(difficulty, p);
      this.scoresTbl.addTab(difficulty.getName(), new JScrollPane(p));
    }

    add(this.scoresTbl, BorderLayout.CENTER);

    setActionListener(new DefaultDialogController<>(this));
    pack();
    setLocationRelativeTo(owner);
  }

  public void setScores(final Map<Difficulty, List<Score>> scores) {
    for (Map.Entry<Difficulty, List<Score>> entry : scores.entrySet()) {
      DefaultTableModel model = ((DefaultTableModel) this.tabs.get(entry.getKey()).getModel());

      model.setRowCount(0);
      entry.getValue().forEach(score -> {
        long time = score.getDuration().getSeconds();
        long seconds = time % 60;
        long minutes = (time / 60) % 60;
        long hours = time / 3600;
        String s;

        if (hours > 0)
          s = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
          s = String.format("%02d:%02d", minutes, seconds);

        model.addRow(new String[]{score.getUsername(), s});
      });
    }
  }
}
