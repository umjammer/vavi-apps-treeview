/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;
import vavi.util.Debug;
import vavi.util.RegexFileFilter;


/**
 * The tree view.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 020322 nsano change closing procedure order <br>
 */
public class TreeView {

    /** */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.treeView.TreeViewResource", Locale.getDefault());

    /** UI */
    private TreeViewTree tree;

    /** root tree node */
    private TreeViewTreeNode root;

    /** status bar */
    private JLabel statusBar = new JLabel(rb.getString("statusBar.welcome"));

    /** The popup menu */
    private JPopupMenu popupMenu;

    private TreeViewTreeEditor editor;

    /**
     * Creates TreeView．
     */
    public TreeView(Object userObject) throws IOException {
        popupMenu = createPopupMenu();
        setActionStates(null);

        tree = new TreeViewTree();
        tree.addEditorListener(el);
        tree.addMouseListener(ml);

        init(userObject);
        InputStream is = vavi.swing.binding.treeview.TreeView.Util.init(userObject);
        load(is);
        tree.setRoot(root);

        editor = new TreeViewTreeEditor(tree);
        editor.addEditorListener(el);
    }

    /**
     * Gets Tree UI.
     */
    public JTree getUI() {
        return tree;
    }

    /**
     * Gets the menu bar.
     */
    public JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenuItem menuItem;

        // file
        JMenu menu = new JMenu(rb.getString("menu.file"));
        menu.setMnemonic(KeyEvent.VK_F);

        menu.addSeparator();

        menuItem = menu.add(saveAction);
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuBar.add(menu);

        menu.addSeparator();

        menuItem = menu.add(exitAction);
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuBar.add(menu);

        // edit
        menu = new JMenu(rb.getString("menu.edit"));
        menu.setMnemonic(KeyEvent.VK_E);
        menuItem = menu.add(cutAction);
        menuItem = menu.add(copyAction);
        menuItem = menu.add(pasteAction);

        menu.addSeparator();

        menuItem = menu.add(deleteAction);
        menuBar.add(menu);

        // object
        objectMenu = new JMenu(rb.getString("menu.object"));
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuBar.add(objectMenu);

        // window
        windowMenu = new JMenu(rb.getString("menu.window"));
        windowMenu.setMnemonic(KeyEvent.VK_W);
        menuBar.add(windowMenu);

        // help
        menu = new JMenu(rb.getString("menu.help"));
        menu.setMnemonic(KeyEvent.VK_H);
        menuItem = menu.add(showManualAction);
        menu.addSeparator();
        menuItem = menu.add(showVersionAction);
        menuBar.add(menu);

        return menuBar;
    }

    /**
     * Gets the tool bar.
     */
    public JToolBar getToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton button;

        button = toolBar.add(cutAction);
        button.setToolTipText(button.getText());
        button = toolBar.add(copyAction);
        button.setToolTipText(button.getText());
        button = toolBar.add(pasteAction);
        button.setToolTipText(button.getText());

        toolBar.addSeparator();

        button = toolBar.add(deleteAction);
        button.setToolTipText(button.getText());

        ToolTipManager.sharedInstance().registerComponent(toolBar);

        return toolBar;
    }

    /** Gets the status bar. */
    public JLabel getStatusBar() {
        return statusBar;
    }

    /** "" menu */
    private JMenu objectMenu;

    /** "Window" menu */
    private JMenu windowMenu;

    /**
     * Gets "Window" menu.
     */
    public JMenu getWindowMenu() {
        return windowMenu;
    }

    /**
     * Creates the popup menu.
     */
    private JPopupMenu createPopupMenu() {

        JPopupMenu popupMenu = new JPopupMenu();

        popupMenu.add(openAction);

        popupMenu.addSeparator();

        popupMenu.add(copyAction);
        popupMenu.add(cutAction);
        popupMenu.add(pasteAction);
        popupMenu.add(deleteAction);

        popupMenu.addSeparator();

        // TODO add object menu

        return popupMenu;
    }

    // ----

    /** */
    private Object userObject;

    /**
     * Initializes.
     */
    public void init(Object userObject) {
        this.userObject = userObject;
    }

    /**
     * Loads a tree nodes.
     */
    @SuppressWarnings("unused")
    private void load(InputStream is) throws IOException {
        root = vavi.swing.binding.treeview.TreeView.Util.load(userObject, is);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }

    /**
     * Saves the tree nodes.
     */
    private void save(OutputStream os) throws IOException {
        vavi.swing.binding.treeview.TreeView.Util.save(userObject, os, root);
    }

    /**
     * Exits the program.
     */
    private void exit() {
        System.exit(0);
    }

    private boolean isPastable = false;

    /**
     * Displays a menu suitable for a selected node.
     * 
     * @param node selected dnode
     */
    private void setActionStates(TreeViewTreeNode node) {
        // determine a menu for displaying by checking each menu is able to execute or not.

        if (node != null) { // when item selected

            cutAction.setEnabled(node.canCut());
            copyAction.setEnabled(node.canCopy());
            pasteAction.setEnabled(node.canCopy() && isPastable);
            deleteAction.setEnabled(node.canDelete());
        } else { // when item not selected

            cutAction.setEnabled(false);
            copyAction.setEnabled(false);
            pasteAction.setEnabled(false);
            deleteAction.setEnabled(false);
        }
    }

    /** "Open" action */
    private final Action openAction = new AbstractAction(rb.getString("action.open"), (ImageIcon) UIManager.get("treeView.openIcon")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            TreeViewTreeNode node = tree.getTreeNode();
            if (node == null)
                return;

            open(node);
        }
    };

    /**
     * Processes "Open".
     * 
     * @param node
     */
    private void open(TreeViewTreeNode node) {
        try {
            tree.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            statusBar.setText(rb.getString("action.open.start"));
            node.open();
            statusBar.setText(rb.getString("action.open.end"));
            tree.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            showError(e);
        }
    }

    /** "Cut" action */
    private final Action cutAction = new AbstractAction(rb.getString("action.cut"), (ImageIcon) UIManager.get("treeView.cutIcon")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                statusBar.setText(rb.getString("action.cut.start"));
                editor.cut();
                statusBar.setText(rb.getString("action.cut.end"));
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** "Copy" action */
    private final Action copyAction = new AbstractAction(rb.getString("action.copy"), (ImageIcon) UIManager.get("treeView.copyIcon")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                statusBar.setText(rb.getString("action.copy.start"));
                editor.copy();
                statusBar.setText(rb.getString("action.copy.end"));
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** "Paste" action */
    private final Action pasteAction = new AbstractAction(rb.getString("action.paste"), (ImageIcon) UIManager.get("treeView.pasteIcon")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                statusBar.setText(rb.getString("action.paste.start"));
                editor.paste();
                statusBar.setText(rb.getString("action.paste.end"));
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** "Delete" action */
    private final Action deleteAction = new AbstractAction(rb.getString("action.delete"), (ImageIcon) UIManager.get("treeView.deleteIcon")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                statusBar.setText(rb.getString("action.delete.start"));

                // show confirm dialog
                if (JOptionPane.showConfirmDialog(null, rb.getString("action.delete.dialog"), rb.getString("dialog.title.confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    editor.delete();

                }
                statusBar.setText(rb.getString("action.delete.end"));
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** "Exit" action */
    private final Action exitAction = new AbstractAction(rb.getString("action.exit")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            // int r = JOptionPane.showConfirmDialog(null,
            // rb.getString("action.exit.dialog"),
            // rb.getString("dialog.title.exit"),
            // JOptionPane.YES_NO_OPTION);

            // switch (r) {
            // case JOptionPane.YES_OPTION: // はい -> close
            exit();
            // return true;
            // case JOptionPane.NO_OPTION: // いいえ -> cancel
            // default: // x -> cancel
            // return false;
            // }
        }
    };

    /** TODO out source */
    private static final RegexFileFilter fileFilter = new RegexFileFilter(".+\\.xml", "XML File");

    /** "Save" action */
    private final Action saveAction = new AbstractAction(rb.getString("action.save")) {
        private final JFileChooser fc = new JFileChooser();
        {
            File cwd = new File(System.getProperty("user.home"));
            fc.setCurrentDirectory(cwd);
            fc.setFileFilter(fileFilter);
        }

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fc.getSelectedFile();
                OutputStream os = new BufferedOutputStream(Files.newOutputStream(file.toPath()));

                save(os);
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** "About" action */
    private final Action showVersionAction = new AbstractAction(rb.getString("action.showVersion")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            String version = rb.getString("version.title") + "\n" + rb.getString("version.copyright") + "\n" + rb.getString("version.revision") + "\n" + rb.getString("version.build");

            JOptionPane.showMessageDialog(null, version, rb.getString("dialog.title.showVersion"), JOptionPane.INFORMATION_MESSAGE);
        }
    };

    /** "Help" action */
    private final Action showManualAction = new AbstractAction(rb.getString("action.showManual")) {

        @Override
        public void actionPerformed(ActionEvent ev) {
            try {
                Runtime.getRuntime().exec(new String[] {props.getProperty("tv.path.browser"), props.getProperty("tv.url.manual")});
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** Shows error dialog */
    private void showError(Exception e) {
// Debug.printStackTrace(e);
        statusBar.setText(e.getMessage());
        JOptionPane.showMessageDialog(null, e.getMessage(), rb.getString("dialog.title.error"), JOptionPane.ERROR_MESSAGE);
    }

    /** */
    private MouseListener ml = new MouseAdapter() {
        @Override public void mousePressed(MouseEvent e) {
            int row = tree.getRowForLocation(e.getX(), e.getY());
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (row != -1) {
                if (e.getClickCount() == 1) {
                    TreeViewTreeNode node = (TreeViewTreeNode) tree.getLastSelectedPathComponent();
Debug.println("mouse single click: " + node);
                } else if(e.getClickCount() == 2) {
                    TreeViewTreeNode node = (TreeViewTreeNode) tree.getLastSelectedPathComponent();
Debug.println("mouse double click: " + node);
                    open(node);
                    tree.expandPath(path);
                    tree.setSelectionPath(path);
                }
            }
        }
    };

    /** */
    @SuppressWarnings("unchecked")
    private final EditorListener el = ev -> {
        String name = ev.getName();
        if ("select".equals(name)) {
            select((List<TreeViewTreeNode>) ev.getArguments()[0]);
        } else if ("expand".equals(name)) {
            expand((TreeViewTreeNode) ev.getArguments()[0]);
        } else if ("popupMenu".equals(name)) {
            Object[] args = ev.getArguments();
            showPopupMenu((TreeViewTreeNode) args[0], (Point) args[1]);
        } else if ("rename".equals(name)) {
            Object[] args = ev.getArguments();
            rename((TreeViewTreeNode) args[0], (String) args[1]);
        } else if ("cut".equals(name)) {
            isPastable = true;
        } else if ("copy".equals(name)) {
            isPastable = true;
        } else if ("lostOwnership".equals(name)) {
            isPastable = false;
        }
    };

    /** */
    private void select(List<TreeViewTreeNode> selection) {
        if (selection.size() == 1) { // one item selected
            TreeViewTreeNode node = selection.get(0);
            statusBar.setText(node.getUserObject().toString());
            setActionStates(node);
            node.setActionStates();
        } else {
            setActionStates(null);
        }
    }

    /** */
    private void expand(TreeViewTreeNode node) {
// Debug.println(Debug.INFO, node);
        if (node.canOpen()) {
            open(node);
        }
    }

    /** */
    private void showPopupMenu(TreeViewTreeNode node, Point point) {
        setActionStates(node);
        popupMenu.show(tree, point.x, point.y);
    }

    /** */
    private void rename(TreeViewTreeNode node, String newValue) {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        try {
            if (!node.canRename())
                return;

            node.rename(newValue);

            // Since we've changed how the data is to be displayed,
            // message nodeChanged.
            treeModel.nodeChanged(node);
        } catch (Exception e) {
            showError(e);
        }
    }

    /** EditorEvent utility */
    private EditorSupport editorSupport = new EditorSupport();

    /** Adds an Editor listener. */
    public void addEditorListener(EditorListener l) {
        editorSupport.addEditorListener(l);
    }

    /** Removes an Editor listener. */
    public void removeEditorListener(EditorListener l) {
        editorSupport.removeEditorListener(l);
    }

    /** Fires an Editor event. */
    protected void fireEditorUpdated(EditorEvent ev) {
        editorSupport.fireEditorUpdated(ev);
    }

    /** */
    static Properties props = new Properties();

    /* */
    static {
        final String path = "TreeView.properties";
        final Class<?> clazz = TreeView.class;

        try {
            InputStream is = clazz.getResourceAsStream(path);
            props.load(is);
            is.close();

            Toolkit t = Toolkit.getDefaultToolkit();
            UIDefaults table = UIManager.getDefaults();

            int i = 0;
            while (true) {
                String key = "tv.action." + i + ".iconName";
                String name = props.getProperty(key);
                if (name == null) {
Debug.println("no property for: tv.action." + i + ".iconName");
                    break;
                }

                key = "tv.action." + i + ".icon";
                String icon = props.getProperty(key);

                table.put(name, new ImageIcon(t.getImage(clazz.getResource(icon))));

                i++;
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
