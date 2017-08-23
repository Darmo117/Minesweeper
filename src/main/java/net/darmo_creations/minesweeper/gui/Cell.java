package net.darmo_creations.minesweeper.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Cell extends JLabel {
    private static final long serialVersionUID = -6747640399949976280L;
    
    private boolean clicked;
    private boolean mine;
    private boolean flagged;
    private boolean marked;
    
    public Cell(boolean tabletMode) {
        super();
        mine = false;
        flagged = false;
        marked = false;
        setBorder(new BevelBorder(BevelBorder.RAISED));
        setOpaque(true);
        setBackground(Color.LIGHT_GRAY);
        
        if (tabletMode) {
            setPreferredSize(new Dimension(20 + 4, 20 + 4));
        }
        else {
            setPreferredSize(new Dimension(15 + 4, 15 + 4));
        }
        
        setIcon(Constants.EMPTY_CELL_IMG);
    }
    
    public boolean isClicked() {
        return clicked;
    }
    
    public boolean isMine() {
        return mine;
    }
    
    public void setMine(boolean mine) {
        this.mine = mine;
    }
    
    public boolean isFlagged() {
        return flagged;
    }
    
    public void setFlagged(boolean flagged) {
        if (clicked) return;
        
        marked = false;
        this.flagged = flagged;
        setIcon(flagged ? Constants.FLAG_IMG : Constants.EMPTY_CELL_IMG);
    }
    
    public boolean isMarked() {
        return marked;
    }
    
    public void setMarked(boolean marked) {
        if (clicked) return;
        
        flagged = false;
        this.marked = marked;
        setIcon(marked ? Constants.MARK_IMG : Constants.EMPTY_CELL_IMG);
    }
    
    public void click(int mines) {
        if (mines < 0 || mines > 9) throw new IllegalArgumentException("Invalid number.");
        if (clicked || flagged) return;
        
        clicked = true;
        setBorder(new CompoundBorder(new LineBorder(Color.GRAY.darker(), 1), new EmptyBorder(1, 1, 1, 1)));
        if (mine) {
            setBackground(Color.RED);
            setIcon(Constants.MINE_IMG);
        }
        else {
            setBackground(new Color(150, 150, 150));
            setIcon(Constants.NUMBER_IMGS[mines]);
        }
    }
    
    public void showMine() {
        if (clicked) return;
        
        if (mine) setIcon(Constants.MINE_IMG);
        else if (flagged) setIcon(Constants.WRONG_MINE_IMG);
    }
    
    private void setIcon(String path) {
        Dimension size = getPreferredSize();
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(path));
        Image newimg = imageIcon.getImage().getScaledInstance((int) size.getWidth() - 4, (int) size.getHeight() - 4, java.awt.Image.SCALE_SMOOTH);
        
        setIcon(new ImageIcon(newimg));
    }
}
