package net.darmo_creations.minesweeper.model;

import net.darmo_creations.minesweeper.gui.MainFrame.CellLabel;

public interface CellLabelProvider {
  CellLabel getLabel(int row, int col);
}
