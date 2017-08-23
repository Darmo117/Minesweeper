package net.darmo_creations.minesweeper.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class CellClickListener extends MouseAdapter {
    private MainFrame frame;
    private Cell cell;
    private int row, col;
    
    public CellClickListener(MainFrame frame, Cell cell, int row, int col) {
        this.frame = frame;
        this.cell = cell;
        this.row = row;
        this.col = col;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (cell.isClicked() || frame.gameIsFinished()) return;
        
        if (!frame.gameIsStarted()) frame.startGame(row, col);
        
        if (SwingUtilities.isRightMouseButton(e)) {
            if (!cell.isFlagged() && !cell.isMarked()) {
                cell.setFlagged(true);
                frame.putFlag();
            }
            else if (cell.isFlagged()) {
                cell.setMarked(true);
                frame.removeFlag();
            }
            else {
                cell.setMarked(false);
            }
        }
        else if (!cell.isFlagged()) {
            int minesNb = frame.getNearbyMinesNumber(row, col);
            
            if (cell.isMine()) {
                cell.click(minesNb);
                frame.endGame(false);
                
                return;
            }
            else if (minesNb == 0) {
                frame.exploreGrid(row, col);
            }
            cell.click(minesNb);
            
            if (frame.checkVictory()) {
                frame.endGame(true);
            }
        }
    }
    
    public Cell getCell() {
        return cell;
    }
    
    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }
}
