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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.darmo_creations.gui_framework.ApplicationRegistry;
import net.darmo_creations.gui_framework.config.WritableConfig;
import net.darmo_creations.gui_framework.events.UserEvent;
import net.darmo_creations.gui_framework.gui.ApplicationFrame;
import net.darmo_creations.minesweeper.Images;
import net.darmo_creations.minesweeper.MainController;
import net.darmo_creations.minesweeper.events.CellClickedEvent;
import net.darmo_creations.minesweeper.events.ChangeDifficultyEvent;
import net.darmo_creations.minesweeper.events.EventType;
import net.darmo_creations.minesweeper.gui.options_dialog.SettingsDialog;
import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.minesweeper.model.Score;
import net.darmo_creations.utils.I18n;

public class MainFrame extends ApplicationFrame<MainController> {
  private static final long serialVersionUID = 5041068026834586876L;

  private static final String REM_TEXT = I18n.getLocalizedString("label.mines.text");

  private ScoresDialog scoresDialog;
  private SettingsDialog optionsDialog;

  private JMenu difficultyMenu;
  private JMenuItem bigButtonsItem;
  private JLabel remainingLbl, timeLbl;
  private JPanel gridPnl;

  public MainFrame(WritableConfig config) {
    super(config, true, false, true, false, null, false);
    this.scoresDialog = new ScoresDialog(this);
    this.optionsDialog = new SettingsDialog(this);
    centerFrame();
  }

  @Override
  protected MainController preInit(WritableConfig config) {
    return new MainController(this, config);
  }

  @Override
  protected void initContent(MainController controller, WritableConfig config) {
    this.remainingLbl = new JLabel(REM_TEXT);
    this.timeLbl = new JLabel();
    this.remainingLbl.setFont(this.remainingLbl.getFont().deriveFont(18f));
    this.timeLbl.setFont(this.timeLbl.getFont().deriveFont(18f));

    JPanel infoPnl = new JPanel(new BorderLayout());
    JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    leftPnl.add(this.remainingLbl);
    rightPnl.add(this.timeLbl);
    infoPnl.add(leftPnl, BorderLayout.WEST);
    infoPnl.add(rightPnl, BorderLayout.EAST);

    this.gridPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));

    getContentPanel().setLayout(new BorderLayout());
    getContentPanel().add(infoPnl, BorderLayout.NORTH);
    getContentPanel().add(this.gridPnl, BorderLayout.CENTER);
  }

  @Override
  protected JMenuBar initJMenuBar(Map<UserEvent.Type, ActionListener> listeners, WritableConfig config) {
    JMenuBar menubar = super.initJMenuBar(listeners, config);

    JMenuItem i;
    JMenu gameMenu = new JMenu(I18n.getLocalizedString("menu.game.text"));
    gameMenu.setMnemonic(I18n.getLocalizedMnemonic("menu.game"));

    gameMenu.add(i = new JMenuItem(I18n.getLocalizedString("item.new_game.text")));
    i.setMnemonic(I18n.getLocalizedMnemonic("item.new_game"));
    i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
    i.addActionListener(e -> ApplicationRegistry.EVENTS_BUS.dispatchEvent(new UserEvent(EventType.NEW_GAME)));
    gameMenu.add(i = new JMenuItem(I18n.getLocalizedString("item.scores.text")));
    i.setMnemonic(I18n.getLocalizedMnemonic("item.scores"));
    i.addActionListener(e -> ApplicationRegistry.EVENTS_BUS.dispatchEvent(new UserEvent(EventType.SHOW_SCORES)));
    gameMenu.add(i = new JMenuItem(I18n.getLocalizedString("item.exit.text")));
    i.setMnemonic(I18n.getLocalizedMnemonic("item.exit"));
    i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
    i.addActionListener(e -> ApplicationRegistry.EVENTS_BUS.dispatchEvent(new UserEvent(UserEvent.DefaultType.EXITING)));

    this.difficultyMenu = new JMenu(I18n.getLocalizedString("menu.difficulty.text"));
    this.difficultyMenu.setMnemonic(I18n.getLocalizedMnemonic("menu.difficulty"));
    ButtonGroup bg = new ButtonGroup();
    for (Difficulty diff : Difficulty.values()) {
      this.difficultyMenu.add(i = new JRadioButtonMenuItem(diff.getName(), diff == Difficulty.EASY));
      i.addActionListener(e -> ApplicationRegistry.EVENTS_BUS.dispatchEvent(new ChangeDifficultyEvent(diff)));
      bg.add(i);
      this.difficultyMenu.add(i);
    }

    menubar.add(gameMenu, 0);
    menubar.add(this.difficultyMenu, 1);

    JMenu optionsMenu = menubar.getMenu(2);
    optionsMenu.add(this.bigButtonsItem = new JMenuItem(I18n.getLocalizedString("item.settings.text")), 1);
    this.bigButtonsItem.setMnemonic(I18n.getLocalizedMnemonic("item.settings"));
    this.bigButtonsItem.addActionListener(e -> ApplicationRegistry.EVENTS_BUS.dispatchEvent(new UserEvent(EventType.SHOW_BUTTONS_SIZE)));

    return menubar;
  }

  public void updateMenus(boolean gameRunning) {
    this.bigButtonsItem.setEnabled(!gameRunning);
    this.difficultyMenu.setEnabled(!gameRunning);
  }

  public void resetGrid(Dimension size, int buttonsSize) {
    if (this.gridPnl.getComponentCount() > 0)
      this.gridPnl.remove(0);
    int rows = size.height;
    int cols = size.width;
    JPanel grid = new JPanel(new GridLayout(rows, cols));
    grid.setBorder(new BevelBorder(BevelBorder.LOWERED));

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        CellLabel b = new CellLabel(new Point(col, row), buttonsSize);
        grid.add(b);
      }
    }
    this.gridPnl.add(grid);
  }

  public CellLabel getCell(Point coordinates) {
    for (Component c : ((JPanel) this.gridPnl.getComponent(0)).getComponents()) {
      CellLabel l = (CellLabel) c;
      if (l.getCoordinates().equals(coordinates))
        return l;
    }

    return null;
  }

  /**
   * Sets the number of remaining mines.
   * 
   * @param mines number of mines
   */
  public void setRemainingMines(int mines) {
    this.remainingLbl.setText(REM_TEXT + " " + mines);
  }

  /**
   * Sets the timer.
   * 
   * @param h hours
   * @param m minutes
   * @param s seconds
   */
  public void setTimer(int h, int m, int s) {
    this.timeLbl.setText((h != 0 ? addLeadingZero(h) + ":" : "") + addLeadingZero(m) + ":" + addLeadingZero(s));
  }

  private String addLeadingZero(int v) {
    return String.format("%02d", v);
  }

  /**
   * Centers the frame on the screen.
   */
  public void centerFrame() {
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(dim.width / 2 - getPreferredSize().width / 2, dim.height / 2 - getPreferredSize().height / 2);
  }

  /**
   * Displays the scores dialog.
   * 
   * @param scores the scores
   */
  public void showScoresDialog(final Map<Difficulty, List<Score>> scores) {
    this.scoresDialog.setScores(scores);
    this.scoresDialog.setVisible(true);
  }

  /**
   * Shows the options dialog.
   * 
   * @param config the current config
   * @return the new config
   */
  public Optional<WritableConfig> showOptionsDialog(WritableConfig config) {
    this.optionsDialog.setConfig(config.clone());
    this.optionsDialog.setVisible(true);
    return this.optionsDialog.getConfig();
  }

  public final class CellLabel extends JLabel {
    private static final long serialVersionUID = 2612596853217381012L;

    public static final int PADDING = 4;

    private final Point coordinates;
    private boolean locked;

    private CellLabel(final Point coordinates, int buttonsSize) {
      this.coordinates = coordinates;
      this.locked = false;

      setIcon(Images.EMPTY_CELL);
      setBorder(new BevelBorder(BevelBorder.RAISED));
      setOpaque(true);
      setBackground(Color.LIGHT_GRAY);

      setPreferredSize(new Dimension(buttonsSize + PADDING, buttonsSize + PADDING));

      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          if (!CellLabel.this.locked && (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e))) {
            ApplicationRegistry.EVENTS_BUS.dispatchEvent(new CellClickedEvent(CellLabel.this, SwingUtilities.isLeftMouseButton(e)));
          }
        }
      });
    }

    public Point getCoordinates() {
      return this.coordinates;
    }

    public void lock() {
      this.locked = true;
    }

    public void click() {
      CellLabel.this.setBorder(new CompoundBorder(new LineBorder(Color.GRAY.darker(), 1), new EmptyBorder(1, 1, 1, 1)));
    }

    public void setIcon(ImageIcon imageIcon) {
      Dimension size = getPreferredSize();
      Image newimg = imageIcon.getImage().getScaledInstance(size.width - PADDING, size.height - PADDING, Image.SCALE_SMOOTH);

      super.setIcon(new ImageIcon(newimg));
    }
  }
}