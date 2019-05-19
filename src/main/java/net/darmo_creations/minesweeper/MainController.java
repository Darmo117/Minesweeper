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

import java.awt.Dimension;
import java.awt.Point;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import net.darmo_creations.gui_framework.config.WritableConfig;
import net.darmo_creations.gui_framework.controllers.ApplicationController;
import net.darmo_creations.gui_framework.events.UserEvent;
import net.darmo_creations.minesweeper.events.CellClickedEvent;
import net.darmo_creations.minesweeper.events.ChangeDifficultyEvent;
import net.darmo_creations.minesweeper.events.EventType;
import net.darmo_creations.minesweeper.events.TimerEvent;
import net.darmo_creations.minesweeper.gui.MainFrame;
import net.darmo_creations.minesweeper.gui.MainFrame.CellLabel;
import net.darmo_creations.minesweeper.model.CellLabelProvider;
import net.darmo_creations.minesweeper.model.Difficulty;
import net.darmo_creations.minesweeper.model.Grid;
import net.darmo_creations.minesweeper.model.Score;
import net.darmo_creations.minesweeper.model.Timer;
import net.darmo_creations.utils.I18n;
import net.darmo_creations.utils.events.SubsribeEvent;

/**
 * Application's controller.
 *
 * @author Damien Vergnet
 */
public class MainController extends ApplicationController<MainFrame> implements CellLabelProvider {
  private Difficulty difficulty;
  private Grid grid;
  private boolean started;
  private boolean finished;
  private Timer timer;
  private int lastTime;
  private Map<Difficulty, List<Score>> scores;

  public MainController(MainFrame frame, WritableConfig config) {
    super(frame, config);
  }

  @Override
  public void init() {
    super.init();
    this.scores = new TreeMap<>(ScoresDao.getInstance().load());
    sortScores(null);
    this.lastTime = 0;
    setGameDifficulty(Difficulty.EASY);
    resetGame();
  }

  @Override
  @SubsribeEvent
  public void onUserEvent(UserEvent e) {
    super.onUserEvent(e);

    if (e.getType() == UserEvent.DefaultType.EXITING && !e.isCanceled()) {
      this.timer.interrupt();
      ScoresDao.getInstance().save(this.scores);
    }
    else {
      UserEvent.Type type = e.getType();
      if (type instanceof EventType) {
        switch ((EventType) type) {
          case NEW_GAME:
            resetGame();
            break;
          case SHOW_BUTTONS_SIZE:
            setButtonsSize();
            break;
          case SHOW_SCORES:
            this.frame.showScoresDialog(this.scores);
            break;
        }
      }
    }
  }

  /**
   * Sorts the scores for the given difficulty. If the parameter is null, all scores will be sorted.
   * 
   * @param difficultyToSort the difficulty scores to sort
   */
  private void sortScores(Difficulty difficultyToSort) {
    if (difficultyToSort == null) {
      for (List<Score> s : this.scores.values()) {
        Collections.sort(s);
      }
    }
    else {
      Collections.sort(this.scores.get(difficultyToSort));
    }
  }

  private void setButtonsSize() {
    Optional<WritableConfig> opt = this.frame.showOptionsDialog(this.config);

    if (opt.isPresent()) {
      this.config.setValue(ConfigTags.BUTTONS_SIZE, opt.get().getValue(ConfigTags.BUTTONS_SIZE));
      resetGame();
    }
  }

  @SubsribeEvent
  public void onChangeDifficulty(ChangeDifficultyEvent e) {
    setGameDifficulty(e.getDifficulty());
  }

  private void setGameDifficulty(Difficulty difficulty) {
    if (!this.started || this.finished) {
      this.difficulty = difficulty;
      this.frame.setTitle(this.frame.getBaseTitle() + " - " + difficulty.getName());
      resetGame();
    }
  }

  @SubsribeEvent
  public void onCellClicked(CellClickedEvent e) {
    if (!this.started) {
      startGame(e.getCell().getCoordinates());
    }

    if (!this.finished) {
      if (e.isMainClick()) {
        clickCell(e);
      }
      else {
        this.grid.performSecondaryClick(e);
        this.frame.setRemainingMines(this.grid.getRemainingFlags());
      }
    }
  }

  private void clickCell(CellClickedEvent event) {
    int result = this.grid.performMainClick(event);
    if (result == Grid.WIN) {
      endGame(true);
    }
    else if (result == Grid.LOST) {
      endGame(false);
    }
  }

  @SubsribeEvent
  public void onTimerEvent(TimerEvent e) {
    this.lastTime = e.getHours() * 3600 + e.getMinutes() * 60 + e.getSeconds();
    this.frame.setTimer(e.getHours(), e.getMinutes(), e.getSeconds());
  }

  private void resetGame() {
    if (this.timer != null && this.timer.isAlive())
      this.timer.interrupt();

    this.timer = new Timer();

    this.started = this.finished = false;
    this.grid = new Grid(this.difficulty, this);

    this.frame.resetGrid(new Dimension(this.difficulty.getColumns(), this.difficulty.getRows()),
        this.config.getValue(ConfigTags.BUTTONS_SIZE));
    this.frame.setRemainingMines(this.grid.getRemainingFlags());
    this.frame.setTimer(0, 0, 0);
    this.frame.updateMenus(false);
    this.frame.pack();
    this.frame.repaint();
  }

  /**
   * Starts a new game.
   * 
   * @param clickedCell the clicked cell
   */
  private void startGame(Point clickedCell) {
    this.started = true;
    this.frame.updateMenus(true);
    this.grid.generateMines(clickedCell.y, clickedCell.x);
    this.timer.start();
  }

  /**
   * Ends the game. Disables all the cells.
   * 
   * @param victory tells if the player has won or not
   */
  private void endGame(boolean victory) {
    String title = victory ? I18n.getLocalizedString("popup.victory.title") : I18n.getLocalizedString("popup.game_over.title");
    String msg = (victory ? I18n.getLocalizedString("popup.victory.text") : I18n.getLocalizedString("popup.game_over.text"));

    this.finished = true;
    this.timer.interrupt();

    this.grid.endGame();
    this.frame.updateMenus(false);
    int choice = 0;

    if (victory) {
      msg += "\n" + I18n.getLocalizedString("popup.enter_name.text");
      String name = JOptionPane.showInputDialog(this.frame, msg, title, JOptionPane.QUESTION_MESSAGE);

      if (name != null) {
        Score score = new Score(name, Duration.ofSeconds(this.lastTime));

        this.scores.get(this.difficulty).add(score);
        sortScores(this.difficulty);
      }
      choice = JOptionPane.showConfirmDialog(this.frame, I18n.getLocalizedString("popup.play_again.text"), title, JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);
    }
    else {
      choice = JOptionPane.showConfirmDialog(this.frame, msg + "\n" + I18n.getLocalizedString("popup.play_again.text"), title,
          JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    if (choice == JOptionPane.YES_OPTION)
      resetGame();
  }

  @Override
  public CellLabel getLabel(int row, int col) {
    return this.frame.getCell(new Point(col, row));
  };
}
