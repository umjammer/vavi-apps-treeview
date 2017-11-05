/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;
import vavi.util.Debug;
import vavi.util.RegexFileFilter;


/**
 * ツリービューです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 020322 nsano change closing procedure order <br>
 */
public class TreeView {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.treeView.TreeViewResource", Locale.getDefault());

    /** Tree の UI */
    private TreeViewTree tree;

    /** ルートのツリーノード */
    private TreeViewTreeNode root;

    /** ステータスバー */
    private JLabel statusBar = new JLabel(rb.getString("statusBar.welcome"));

    /** The popup menu */
    private JPopupMenu popupMenu;

    private TreeViewTreeEditor editor;

    /**
     * TreeView を作成します．
     */
    public TreeView() {

        popupMenu = createPopupMenu();
        setActionStates(null);

        init();

        tree = new TreeViewTree(root);
        tree.addEditorListener(el);

        editor = new TreeViewTreeEditor(tree);
        editor.addEditorListener(el);
    }

    /**
     * Tree の UI を返します．
     */
    public JTree getUI() {
        return tree;
    }

    /**
     * メニューバーを取得します．
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
     * ツールバーを取得します．
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

    /** ステータスバーを取得します． */
    public JLabel getStatusBar() {
        return statusBar;
    }

    /** ""メニュー */
    private JMenu objectMenu;

    /** "ウインドウ"メニュー */
    private JMenu windowMenu;

    /**
     * "ウィンドウ"メニューを取得します．
     */
    public JMenu getWindowMenu() {
        return windowMenu;
    }

    /**
     * ポップアップメニューの設定をします．
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

    // -------------------------------------------------------------------------

    /** */
    private Dao dao = new XmlDao();

    /**
     * 初期状態にします．
     */
    private void init() {
        try {
            InputStream is = getClass().getResourceAsStream(props.getProperty("tv.resource.tree"));
            root = dao.read(is);
        } catch (Exception e) {
Debug.println(props.getProperty("tv.resource.tree") + " cannot read: tv.resource.tree");
Debug.printStackTrace(e);
            // System.exit(1);
        }
    }

    /**
     * ツリーノードをロードします．
     */
    @SuppressWarnings("unused")
    private void load(InputStream is) throws IOException {
        root = dao.read(is);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
    }

    /**
     * ツリーノードをセーブします．
     */
    private void save(OutputStream os) throws IOException {
        dao.write(root, os);
    }

    /**
     * 終了します．
     */
    private void exit() {
        System.exit(0);
    }

    // -------------------------------------------------------------------------

    private boolean isPastable = false;

    /**
     * 選択されているノードに応じてメニュー表示を制御します．
     * 
     * @param node 選択されたノード
     */
    private void setActionStates(TreeViewTreeNode node) {
        // それぞれのメニューが実行できるかをチェックして表示を決定

        if (node != null) { // アイテム選択時

            cutAction.setEnabled(node.canCut());
            copyAction.setEnabled(node.canCopy());
            pasteAction.setEnabled(node.canCopy() && isPastable);
            deleteAction.setEnabled(node.canDelete());
        } else { // アイテム非選択時

            cutAction.setEnabled(false);
            copyAction.setEnabled(false);
            pasteAction.setEnabled(false);
            deleteAction.setEnabled(false);
        }
    }

    // -------------------------------------------------------------------------

    /** ノードを開くアクション */
    private Action openAction = new AbstractAction(rb.getString("action.open"), (ImageIcon) UIManager.get("treeView.openIcon")) {

        public void actionPerformed(ActionEvent ev) {
            TreeViewTreeNode node = tree.getTreeNode();
            if (node == null)
                return;

            open(node);
        }
    };

    /**
     * オープンの処理を行います．
     * 
     * @param node
     */
    private void open(TreeViewTreeNode node) {
        try {
            statusBar.setText(rb.getString("action.open.start"));
            node.open();
            statusBar.setText(rb.getString("action.open.end"));
        } catch (Exception e) {
            showError(e);
        }
    }

    /** オブジェクトをカットするアクション */
    private Action cutAction = new AbstractAction(rb.getString("action.cut"), (ImageIcon) UIManager.get("treeView.cutIcon")) {

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

    /** オブジェクトをコピーするアクション */
    private Action copyAction = new AbstractAction(rb.getString("action.copy"), (ImageIcon) UIManager.get("treeView.copyIcon")) {

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

    /** オブジェクトをペーストするアクション */
    private Action pasteAction = new AbstractAction(rb.getString("action.paste"), (ImageIcon) UIManager.get("treeView.pasteIcon")) {

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

    /** オブジェクトを削除するアクション */
    private Action deleteAction = new AbstractAction(rb.getString("action.delete"), (ImageIcon) UIManager.get("treeView.deleteIcon")) {

        public void actionPerformed(ActionEvent ev) {
            try {
                statusBar.setText(rb.getString("action.delete.start"));

                // ダイアログで削除の確認をします．
                if (JOptionPane.showConfirmDialog(null, rb.getString("action.delete.dialog"), rb.getString("dialog.title.confirm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    editor.delete();

                }
                statusBar.setText(rb.getString("action.delete.end"));
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** ツリービューを終了するアクション */
    private Action exitAction = new AbstractAction(rb.getString("action.exit")) {

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

    /** */
    private static final RegexFileFilter fileFilter = new RegexFileFilter(".+\\.xml", "XML File");

    /** 初期ツリーのセーブを行うアクション */
    private Action saveAction = new AbstractAction(rb.getString("action.save")) {
        private JFileChooser fc = new JFileChooser();
        {
            File cwd = new File(System.getProperty("user.home"));
            fc.setCurrentDirectory(cwd);
            fc.setFileFilter(fileFilter);
        }

        public void actionPerformed(ActionEvent ev) {
            try {
                if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                File file = fc.getSelectedFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

                save(os);
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** バージョン表示を行うアクション */
    private Action showVersionAction = new AbstractAction(rb.getString("action.showVersion")) {

        public void actionPerformed(ActionEvent ev) {
            String version = rb.getString("version.title") + "\n" + rb.getString("version.copyright") + "\n" + rb.getString("version.revision") + "\n" + rb.getString("version.build");

            JOptionPane.showMessageDialog(null, version, rb.getString("dialog.title.showVersion"), JOptionPane.INFORMATION_MESSAGE);
        }
    };

    /** 使用説明の表示を行うアクション */
    private Action showManualAction = new AbstractAction(rb.getString("action.showManual")) {

        public void actionPerformed(ActionEvent ev) {
            try {
                Runtime.getRuntime().exec(props.getProperty("tv.path.browser") + " " + props.getProperty("tv.url.manual"));
            } catch (Exception e) {
                showError(e);
            }
        }
    };

    /** エラーメッセージのダイアログを表示します． */
    private void showError(Exception e) {
        // Debug.printStackTrace(e);
        statusBar.setText(e.getMessage());
        JOptionPane.showMessageDialog(null, e.getMessage(), rb.getString("dialog.title.error"), JOptionPane.ERROR_MESSAGE);
    }

    // -------------------------------------------------------------------------

    /** */
    private EditorListener el = new EditorListener() {
        @SuppressWarnings("unchecked")
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("select".equals(name)) {
                select((List<TreeViewTreeNode>) ev.getArgument());
            } else if ("expand".equals(name)) {
                expand((TreeViewTreeNode) ev.getArgument());
            } else if ("popupMenu".equals(name)) {
                Object[] args = (Object[]) ev.getArgument();
                showPopupMenu((TreeViewTreeNode) args[0], (Point) args[1]);
            } else if ("rename".equals(name)) {
                Object[] args = (Object[]) ev.getArgument();
                rename((TreeViewTreeNode) args[0], (String) args[1]);
            } else if ("cut".equals(name)) {
                isPastable = true;
            } else if ("copy".equals(name)) {
                isPastable = true;
            } else if ("lostOwnership".equals(name)) {
                isPastable = false;
            }
        }
    };

    /** */
    private void select(List<TreeViewTreeNode> selection) {
        if (selection.size() == 1) { // アイテム(単体)選択時
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

    // -------------------------------------------------------------------------

    /** EditorEvent 機構のユーティリティ */
    private EditorSupport editorSupport = new EditorSupport();

    /** Editor リスナーを追加します． */
    public void addEditorListener(EditorListener l) {
        editorSupport.addEditorListener(l);
    }

    /** Editor リスナーを削除します． */
    public void removeEditorListener(EditorListener l) {
        editorSupport.removeEditorListener(l);
    }

    /** EditorEvent を発行します． */
    protected void fireEditorUpdated(EditorEvent ev) {
        editorSupport.fireEditorUpdated(ev);
    }

    // -------------------------------------------------------------------------

    /** プロパティ */
    static Properties props = new Properties();

    /**
     * 初期化します．
     */
    static {
        final String path = "TreeView.properties";
        final Class<?> clazz = TreeView.class;

        try {
            Properties ps = new Properties();
            InputStream is = clazz.getResourceAsStream(path);
            ps.load(is);
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

            props = new Properties();
            is = clazz.getResourceAsStream("/local.properties");
            props.load(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();;
            System.exit(1);
        }
    }

    // -------------------------------------------------------------------------

    /**
     * プログラムエントリです．
     */
    public static void main(String[] args) throws Exception {

        TreeView tree = new TreeView();

        JFrame frame = new TreeViewFrame(tree);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

/* */
