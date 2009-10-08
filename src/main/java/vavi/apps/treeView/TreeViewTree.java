/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DropTarget;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import vavi.awt.dnd.Draggable;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;
import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * ツリービューです．
 * 
 * @todo 複数 DnD
 * 
 * @event EditorEvent("expand")
 * @event EditorEvent("popupMenu")
 * @event EditorEvent("rename")
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 020625 nsano refine <br>
 */
public class TreeViewTree extends JTree {

    /**
     * 描画ツリーを作成します．
     * 
     * @param root ツリーのモデル
     */
    public TreeViewTree(TreeViewTreeNode root) {

        root.addEditorListener(el);

        TreeViewTreeModel treeModel = new TreeViewTreeModel(root);
        setModel(treeModel);
        setCellRenderer(tcr);
        // rename できるようにする
        setEditable(true);

        addMouseListener(ml);
        addTreeSelectionListener(tsl);

        ToolTipManager.sharedInstance().registerComponent(this);

        // ドラッグする側のクラス
        new Draggable(this, null) {
            {
Debug.println("here");
                setDragAction(DnDConstants.ACTION_COPY_OR_MOVE);
                image = (Image) UIManager.get("treeViewTree.dragImage");
            }

            /** Transferable データを取得します． */
            protected Transferable getTransferable(DragGestureEvent ev) {
                TreeViewTreeNode node = getTreeNode();
                if (node == null) {
                    return null;
                }
Debug.println("node class: " + StringUtil.getClassName(node.getClass()));
Debug.println("node: " + node);
Debug.println("node hash: " + node.hashCode());
Debug.println("node user: " + node.getUserObject());
Debug.println("node uerH: " + node.getUserObject().hashCode());
                int action = ev.getDragAction();
Debug.println("now action: " + action + ": " + ((action & DnDConstants.ACTION_COPY) != 0 ? "copy" : "") + ((action & DnDConstants.ACTION_MOVE) != 0 ? "move" : "") + ((action & DnDConstants.ACTION_LINK) != 0 ? "link" : ""));
                if ((action == DnDConstants.ACTION_COPY && node.canCopy()) || (action == DnDConstants.ACTION_MOVE && node.canCut())) {
                    setEditable(false);
                    return new TreeViewTreeNodeTransferable(node);
                } else {
                    return null;
                }
            }

            /** DnD を終了します． */
            protected void dragDropEnd(DragSourceEvent ev) {
Debug.println("here");
                setEditable(true);
            }
        };

        // ドロップされる側のクラス
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new TreeViewTreeDTListener(this), true);
    }

    // -------------------------------------------------------------------------

    /**
     * 選択されているツリーノードを返します．
     * 
     * @return a selected tree node
     */
    public TreeViewTreeNode getTreeNode() {
        TreePath tp = getSelectionPath();
        if (tp != null) {
            return (TreeViewTreeNode) tp.getLastPathComponent();
        } else {
            return null;
        }
    }

    /**
     * 現在のマウスのポジションにあるツリーノードを返します．
     * 
     * @param x x point of mouse
     * @param y y point of mouse
     * @return a pointed tree node
     */
    private TreeViewTreeNode getTreeNode(int x, int y) {
        TreePath tp = getPathForLocation(x, y);
        if (tp != null) { // TODO 意味わからん
            return (TreeViewTreeNode) tp.getLastPathComponent();
        } else {
            return null;
        }
    }

    /**
     * TreeViewTree のセレクションリスナです．
     */
    private TreeSelectionListener tsl = new TreeSelectionListener() {
        /** ツリーの選択が変更された場合に呼ばれます． */
        public void valueChanged(TreeSelectionEvent ev) {

            TreePath[] selected = getSelectionPaths();
            if (selected == null) {
                return;
            }

            Vector<Object> selection = new Vector<Object>();
            for (int i = 0; i < selected.length; i++) {
                selection.addElement(selected[i].getLastPathComponent());
            }

            // TreeViewActions
            fireEditorUpdated(new EditorEvent(TreeViewTree.this, "select", selection));
        }
    };

    /**
     * ツリー上のマウスのイベント処理を行うクラスです．
     */
    private MouseListener ml = new MouseAdapter() {

        /** マウスがクリックされたとき呼ばれます． */
        public void mouseClicked(MouseEvent ev) {

            if (ev.getClickCount() == 2) {
                TreeViewTreeNode node = getTreeNode(ev.getX(), ev.getY());
                fireEditorUpdated(new EditorEvent( // > TreeViewActions
                                                  TreeViewTree.this, "expand", node));
            }

            // ポップアップメニューをその位置に表示する
            if (SwingUtilities.isRightMouseButton(ev)) {
                // if (ev.getModifiers() == MouseEvent.BUTTON3_MASK) {
                // if (ev.isPopupTrigger()) {
                TreeViewTreeNode node = getTreeNode();
                fireEditorUpdated(new EditorEvent( // > TreeViewActions
                                                  TreeViewTree.this, "popupMenu", new Object[] {
                                                      node, ev.getPoint()
                                                  }));
            }
        }
    };

    /**
     * ツリーのモデルのクラスです．
     * 
     * @version 000214 nsano fix not renamable node bug.
     */
    private final class TreeViewTreeModel extends DefaultTreeModel {
        /** */
        public TreeViewTreeModel(TreeViewTreeNode newRoot) {
            super(newRoot);
        }

        /** */
        public void valueForPathChanged(TreePath path, Object newValue) {
            fireEditorUpdated(new EditorEvent( // > TreeViewActions
                                              this, "rename", new Object[] {
                                                  path.getLastPathComponent(), newValue
                                              }));
        }
    }

    // -------------------------------------------------------------------------

    /** */
    private EditorListener el = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("expand".equals(name)) { // フォルダの展開
                expand((TreePath) ev.getArgument());
            } else if ("delete".equals(name)) { // 削除
                Object[] args = (Object[]) ev.getArgument();
                delete((TreeNode) args[0], (int[]) args[1], (TreeNode[]) args[2]);
            } else if ("insert".equals(name)) { // 追加
                insert((TreeNode) ev.getArgument());
            }
        }
    };

    /** */
    private void expand(TreePath path) {
        this.expandPath(path);
    }

    /** */
    private void delete(TreeNode parent, int[] indices, TreeNode[] removed) {
        DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
        treeModel.nodesWereRemoved(parent, indices, removed);
    }

    /** */
    private void insert(TreeNode node) {
        DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
        treeModel.nodeStructureChanged(node);
    }

    // -------------------------------------------------------------------------

    /**
     * ツリーのセルレンダラのクラスです．
     */
    private TreeCellRenderer tcr = new DefaultTreeCellRenderer() {
        /**
         * This is messaged from JTree whenever it needs to get the size of the component or it wants to draw it. This attempts to
         * set the font based on value, which will be a TreeNode.
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);

            this.hasFocus = hasFocus;

            TreeViewTreeNode node = (TreeViewTreeNode) value;
            // Debug.println(Debug.DEBUG, "node: " + node);
            Object data = node.getUserObject();
            // Debug.println(Debug.DEBUG, "data: " + data);

            /* Set the text. */
            setText(data.toString());
            /* Tooltips used by the tree. */
            setToolTipText(stringValue);

            if (selected) {
                setForeground(getTextSelectionColor());
            } else {
                setForeground(getTextNonSelectionColor());
            }

            Icon icon = UIManager.getIcon("treeViewTree.defaultIcon");
            Icon extIcon = UIManager.getIcon("treeViewTree.defaultIcon");

            try {
                Class<?> beanClass = data.getClass();
                // Debug.println(beanClass);
                BeanInfo info = Introspector.getBeanInfo(beanClass);
                /* Set the image. */
                Image image;
                if (node instanceof FolderTreeNode) {
                    image = info.getIcon(TreeNodeInfo.ICON_COLOR_16x16_EXT1);
                    if (image != null) {
                        icon = new ImageIcon(image);
                    }
                    image = info.getIcon(TreeNodeInfo.ICON_COLOR_16x16_EXT2);
                    if (image != null) {
                        extIcon = new ImageIcon(image);
                    }
                } else {
                    image = info.getIcon(BeanInfo.ICON_COLOR_16x16);
                    if (image != null) {
                        icon = new ImageIcon(image);
                    }
                }
            } catch (Exception e) {
                Debug.println(Level.SEVERE, e);
            }

            // Debug.println(Debug.DEBUG, item);
            /* Set the image. */
            if (node.isCut()) {
                setIcon(UIManager.getIcon("treeViewTree.markIcon"));
            } else if (expanded) {
                setIcon(extIcon);
            } else if (!leaf) {
                setIcon(icon);
            } else {
                setIcon(icon);
            }

            if (selected) {
                closedIcon = icon;
                leafIcon = icon;
                openIcon = extIcon;
            }

            setComponentOrientation(tree.getComponentOrientation());

            /* Update the selected flag for the next paint. */
            this.selected = selected;

            return this;
        }
    };

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

    static {
        Toolkit t = Toolkit.getDefaultToolkit();
        Class<?> clazz = TreeViewTree.class;
        UIDefaults table = UIManager.getDefaults();
        table.put("treeViewTree.markIcon", LookAndFeel.makeIcon(clazz, "resources/mark.png"));
        table.put("treeViewTree.defaultIcon", LookAndFeel.makeIcon(clazz, "resources/default_file.png"));
        table.put("treeViewTree.dragImage", t.getImage(clazz.getResource("resources/default_file.png")));
    }
}

/* */
