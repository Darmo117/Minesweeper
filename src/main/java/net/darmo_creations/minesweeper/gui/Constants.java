package net.darmo_creations.minesweeper.gui;

/**
 * This class holds all the constants used in this application.
 * 
 * @author Darmo
 */
public abstract class Constants {
  /**
   * Mine.
   */
  public static final String FRAME_ICON = "/assets/images/icon.png";
  /**
   * Digits 0 through 8. The index corresponds to the digits.
   */
  public static final String[] NUMBER_IMGS;
  /**
   * Empty image.
   */
  public static final String EMPTY_CELL_IMG = "/assets/images/tiles/empty.png";
  /**
   * Flag.
   */
  public static final String FLAG_IMG = "/assets/images/tiles/flag.png";
  /**
   * Question mark.
   */
  public static final String MARK_IMG = "/assets/images/tiles/mark.png";
  /**
   * Mine.
   */
  public static final String MINE_IMG = "/assets/images/tiles/mine.png";
  /**
   * Crossed out mine.
   */
  public static final String WRONG_MINE_IMG = "/assets/images/tiles/mine_wrong.png";

  static {
    NUMBER_IMGS = new String[9];
    NUMBER_IMGS[0] = EMPTY_CELL_IMG;
    for (int i = 1; i < NUMBER_IMGS.length; i++) {
      NUMBER_IMGS[i] = "/assets/images/tiles/" + i + ".png";
    }
  }
}
