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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
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
import net.darmo_creations.minesweeper.events.CellClickedEvent;
import net.darmo_creations.minesweeper.events.ChangeDifficultyEvent;
import net.darmo_creations.minesweeper.events.EventType;
import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.utils.I18n;

public class MainFrame extends ApplicationFrame<MainController> {
  private static final long serialVersionUID = 5041068026834586876L;

  private static final String REM_TEXT = I18n.getLocalizedString("label.mines.text");

  private JMenu difficultyMenu;
  private JCheckBoxMenuItem modeItem;
  private JLabel remainingLbl, timeLbl;
  private JPanel gridPnl;

  public MainFrame(WritableConfig config) {
    super(config, true, false, true, false, null, false);
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
    this.remainingLbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
    this.timeLbl.setFont(new Font("Tahoma", Font.PLAIN, 18));

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
    gameMenu.add(
        this.modeItem = new JCheckBoxMenuItem(I18n.getLocalizedString("item.tablet_mode.text"), config.getValue(ConfigTags.TABLET_MODE)));
    this.modeItem.setMnemonic(I18n.getLocalizedMnemonic("item.tablet_mode"));
    this.modeItem.addActionListener(e -> ApplicationRegistry.EVENTS_BUS.dispatchEvent(new UserEvent(EventType.TOGGLE_TABLET_MODE)));
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

    return menubar;
  }

  public void updateMenus(boolean gameRunning) {
    this.modeItem.setEnabled(!gameRunning);
    this.difficultyMenu.setEnabled(!gameRunning);
  }

  public void resetGrid(Dimension size, boolean tabletMode) {
    if (this.gridPnl.getComponentCount() > 0)
      this.gridPnl.remove(0);
    int rows = size.height;
    int cols = size.width;
    JPanel grid = new JPanel(new GridLayout(rows, cols));
    grid.setBorder(new BevelBorder(BevelBorder.LOWERED));

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        CellLabel b = new CellLabel(new Point(col, row), tabletMode);
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

  // TODO use String.format
  private String addLeadingZero(int v) {
    return v >= 0 && v < 10 ? "0" + v : "" + v;
  }

  /**
   * Centers the frame on the screen.
   */
  public void centerFrame() {
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation(dim.width / 2 - getPreferredSize().width / 2, dim.height / 2 - getPreferredSize().height / 2);
  }

  public final class CellLabel extends JLabel {
    private static final long serialVersionUID = 2612596853217381012L;

    public static final int PADDING = 4;
    public static final int NORMAL_SIZE = 15;
    public static final int BIG_SIZE = 20;

    private final Point coordinates;
    private boolean clicked;

    private CellLabel(final Point coordinates, boolean tabletMode) {
      this.coordinates = coordinates;
      this.clicked = false;

      setIcon(Images.EMPTY_CELL);
      setBorder(new BevelBorder(BevelBorder.RAISED));
      setOpaque(true);
      setBackground(Color.LIGHT_GRAY);

      if (tabletMode)
        setPreferredSize(new Dimension(BIG_SIZE + PADDING, BIG_SIZE + PADDING));
      else
        setPreferredSize(new Dimension(NORMAL_SIZE + PADDING, NORMAL_SIZE + PADDING));

      addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          if (!CellLabel.this.clicked && (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e))) {
            ApplicationRegistry.EVENTS_BUS.dispatchEvent(new CellClickedEvent(CellLabel.this, SwingUtilities.isLeftMouseButton(e)));
          }
        }
      });
    }

    public Point getCoordinates() {
      return this.coordinates;
    }

    public void lock() {
      this.clicked = true;
    }

    public void click() {
      CellLabel.this.setBorder(new CompoundBorder(new LineBorder(Color.GRAY.darker(), 1), new EmptyBorder(1, 1, 1, 1)));
      CellLabel.this.clicked = true;
    }

    public void setIcon(ImageIcon imageIcon) {
      Dimension size = getPreferredSize();
      Image newimg = imageIcon.getImage().getScaledInstance(size.width - PADDING, size.height - PADDING, Image.SCALE_SMOOTH);

      super.setIcon(new ImageIcon(newimg));
    }
  }
}