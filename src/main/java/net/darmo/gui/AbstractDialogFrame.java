package net.darmo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author Darmo
 */
public abstract class AbstractDialogFrame extends JDialog implements ActionListener {
    private static final long serialVersionUID = 5246601940536755673L;
    
    public static final String dispatchWindowClosingActionMapKey = "WINDOW_CLOSING";
    
    public AbstractDialogFrame(Frame owner, String title, boolean modal, Dimension d) {
        super(owner, title, modal);
        
        setSize(d);
        setLocationRelativeTo(owner);
        
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        closeBtn.setActionCommand("close");
        closeBtn.setFocusPainted(false);
        
        btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.add(closeBtn);
        
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
        
        installEscapeCloseOperation(this);
    }
    
    public static void installEscapeCloseOperation(final JDialog dialog) {
        Action dispatchClosing = new AbstractAction() {
            private static final long serialVersionUID = -2682313879254943757L;
            
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
            }
        };
        JRootPane root = dialog.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), dispatchWindowClosingActionMapKey);
        root.getActionMap().put(dispatchWindowClosingActionMapKey, dispatchClosing);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "close":
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }
    
    private final JPanel btnPanel;
    private final JButton closeBtn;
}
