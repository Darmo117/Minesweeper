package net.darmo_creations.minesweeper.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * @author Darmo
 */
class AboutFrame extends AbstractDialogFrame {
    private static final long serialVersionUID = -5229282462470684652L;
    
    private static final String TEXT_1 = "This application is licensed under the Creative Commons ";
    private static final String TEXT_2 = " license.";
    private static final String TEXT_3 = "For more information, please visit ";
    private static final String TEXT_4 = ".";
    private static final String CC_TEXT = "Attribution-NonCommercial-ShareAlike 4.0 International (CC BY-NC-SA 4.0)";
    private static final String CC_LINK = "http://creativecommons.org/licenses/by-nc-sa/4.0/";
    private static final String DC_TEXT = "darmo-creations.net";
    private static final String DC_LINK = "http://darmo-creations.net/show.php?id=5";
    
    public AboutFrame(Frame owner, boolean modal) {
        super(owner, null, modal, new Dimension(410, 205));
        
        setResizable(false);
        setTitle("About");
        
        title = new JLabel("Minesweeper");
        title.setFont(title.getFont().deriveFont(20F));
        
        titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(title);
        
        license = new JEditorPane();
        license.setOpaque(false);
        license.setEditable(false);
        license.setBorder(new EmptyBorder(5, 5, 5, 5));
        license.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        
        HTMLEditorKit kit = new HTMLEditorKit();
        license.setEditorKit(kit);
        
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body {font-family: Tahoma; font-size: 11px; text-align: justify;}");
        
        license.setText(TEXT_1 + toLink(CC_LINK, CC_TEXT) + TEXT_2 + "<br/>" + TEXT_3 + toLink(DC_LINK, DC_TEXT) + TEXT_4);
        
        license.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        }
                        catch (IOException | URISyntaxException ex) {}
                    }
                }
            }
        });
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(license, BorderLayout.CENTER);
        
        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }
    
    private String toLink(String url, String text) {
        return "<a href='" + url + "'>" + (text != null ? text : url.substring(url.indexOf("//") + 2)) + "</a>";
    }
    
    private final JPanel mainPanel, titlePanel;
    private final JLabel title;
    private final JEditorPane license;
}
