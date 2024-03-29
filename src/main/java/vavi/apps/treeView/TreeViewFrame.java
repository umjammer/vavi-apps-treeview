/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

import vavi.util.Debug;


/**
 * TreeView をアプリケーションとして使用する際のフレームです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public final class TreeViewFrame extends JFrame {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.treeView.TreeViewResource", Locale.getDefault());

    /** 右側の UI */
    private JDesktopPane desktop = new JDesktopPane();

    /**
     * TreeView の Frame を作成します．
     */
    public TreeViewFrame(TreeView treeView) {
        setTitle(rb.getString("version.title"));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel statusBar = treeView.getStatusBar();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

//statusBar.setPreferredSize(new Dimension(statusBar.getWidth() + statusBar.getInsets().left + statusBar.getInsets().right, statusBar.getHeight() + statusBar.getInsets().top + statusBar.getInsets().bottom));
//Debug.println("status bar: " + statusBar.getWidth() + ", " + statusBar.getHeight());
//Debug.println("status bar: " + statusBar.getPreferredSize().width + ", " + statusBar.getPreferredSize().height);
//Debug.println("status bar: " + statusBar.getWidth() + statusBar.getInsets().left + statusBar.getInsets().right + ", " + statusBar.getHeight() + statusBar.getInsets().top + statusBar.getInsets().bottom);

        Dimension d = this.getToolkit().getScreenSize();

        JScrollPane scrollPane = new JScrollPane(treeView.getUI());
        scrollPane.setPreferredSize(new Dimension(d.width / 5, d.height));

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, desktop);
        sp.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
Debug.println("split out: " + (double) (int) e.getNewValue() / sp.getMaximumDividerLocation());
            prefs.putDouble("tv.frame.split", (double) (int) e.getNewValue() / sp.getMaximumDividerLocation());
        });
Debug.println("split in: " + prefs.getDouble("tv.frame.split", 0.3));
        sp.setDividerLocation(prefs.getDouble("tv.frame.split", 0.3));

        setJMenuBar(treeView.getMenuBar());
        getContentPane().add(treeView.getToolBar(), BorderLayout.NORTH);
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        setSize(d.width * 6 / 7, d.height * 6 / 7);

        int width = prefs.getInt("tv.frame.width", 800);
        int height = prefs.getInt("tv.frame.height", 640);
        int x = prefs.getInt("tv.frame.x", 100);
        int y = prefs.getInt("tv.frame.y", 200);

        setPreferredSize(new Dimension(width, height));
        setLocation(x, y);

        pack();

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                prefs.putInt("tv.frame.width", getWidth());
                prefs.putInt("tv.frame.height", getHeight());
            }
            @Override public void componentMoved(ComponentEvent e) {
                prefs.putInt("tv.frame.x", getX());
                prefs.putInt("tv.frame.y", getY());
            }
        });
    }


    /**
     * Gets the virtual desktop.
     */
    public JDesktopPane getUI() {
        return desktop;
    }

    /** */
    private static Preferences prefs = Preferences.userNodeForPackage(TreeView.class);
}

/* */
