/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;


/**
 * TreeView ���A�v���P�[�V�����Ƃ��Ďg�p����ۂ̃t���[���ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public final class TreeViewFrame extends JFrame {

    /** ���\�[�X�o���h�� */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.treeView.TreeViewResource", Locale.getDefault());

    /** �E���� UI */
    private JDesktopPane desktop = new JDesktopPane();

    /**
     * TreeView �� Frame ���쐬���܂��D
     */
    public TreeViewFrame(TreeView treeView) {
        setTitle(rb.getString("version.title"));

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JLabel statusBar = treeView.getStatusBar();
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        /*
         * statusBar.setPreferredSize(new Dimension( statusBar.getWidth() + statusBar.getInsets().left +
         * statusBar.getInsets().right, statusBar.getHeight() + statusBar.getInsets().top + statusBar.getInsets().bottom));
         * Debug.println("status bar: " + statusBar.getWidth() + ", " + statusBar.getHeight()); Debug.println("status bar: " +
         * statusBar.getPreferredSize().width + ", " + statusBar.getPreferredSize().height); Debug.println("status bar: " +
         * statusBar.getWidth() + statusBar.getInsets().left + statusBar.getInsets().right + ", " + statusBar.getHeight() +
         * statusBar.getInsets().top + statusBar.getInsets().bottom);
         */

        Dimension d = this.getToolkit().getScreenSize();

        JScrollPane scrollPane = new JScrollPane(treeView.getUI());
        scrollPane.setPreferredSize(new Dimension(d.width / 5, d.height));

        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, desktop);

        setJMenuBar(treeView.getMenuBar());
        getContentPane().add(treeView.getToolBar(), BorderLayout.NORTH);
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);

        setSize(d.width * 6 / 7, d.height * 6 / 7);

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocation(FRAME_X, FRAME_Y);

        validate();
    }

    // -------------------------------------------------------------------------

    /**
     * Gets the virtual desktop.
     */
    public JDesktopPane getUI() {
        return desktop;
    }

    // -------------------------------------------------------------------------

    /** �� */
    private static final int FRAME_WIDTH;

    /** ���� */
    private static final int FRAME_HEIGHT;

    /** ���̈ʒu */
    private static final int FRAME_X;

    /** �c�̈ʒu */
    private static final int FRAME_Y;

    static {
        Properties props = TreeView.props;

        FRAME_WIDTH = Integer.parseInt(props.getProperty("tv.frame.width"));
        FRAME_HEIGHT = Integer.parseInt(props.getProperty("tv.frame.height"));
        FRAME_X = Integer.parseInt(props.getProperty("tv.frame.x"));
        FRAME_Y = Integer.parseInt(props.getProperty("tv.frame.y"));
    }
}

/* */
