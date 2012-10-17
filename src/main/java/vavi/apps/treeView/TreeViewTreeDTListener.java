/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.logging.Level;

import javax.swing.tree.TreePath;

import vavi.awt.dnd.BasicDTListener;
import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * DTListener a listener that tracks the state of the operation.
 * 
 * @see java.awt.dnd.DropTargetListener
 * @see java.awt.dnd.DropTarget
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public class TreeViewTreeDTListener extends BasicDTListener {

    /** The Tree Component */
    private TreeViewTree tree;

    /**
     * Creates a drag target listener.
     */
    public TreeViewTreeDTListener(TreeViewTree tree) {
        this.tree = tree;
        this.dragAction = DnDConstants.ACTION_COPY_OR_MOVE;
    }

    /**
     * Called by isDragOk Checks to see if the flavor drag flavor is acceptable
     * 
     * @param ev the DropTargetDragEvent object
     * @return whether the flavor is acceptable
     */
    protected boolean isDragFlavorSupported(DropTargetDragEvent ev) {
        return ev.isDataFlavorSupported(TreeViewTreeNodeTransferable.flavor);
    }

    /**
     * Called by drop Checks the flavors and operations
     * 
     * @param ev the DropTargetDropEvent object
     * @return the chosen DataFlavor or null if none match
     */
    protected DataFlavor chooseDropFlavor(DropTargetDropEvent ev) {
        if (ev.isLocalTransfer() == true && ev.isDataFlavorSupported(TreeViewTreeNodeTransferable.flavor)) {
            return TreeViewTreeNodeTransferable.flavor;
        }
        DataFlavor chosen = null;
        if (ev.isDataFlavorSupported(TreeViewTreeNodeTransferable.flavor)) {
            chosen = TreeViewTreeNodeTransferable.flavor;
        }
        return chosen;
    }

    /** ドラッグソースのノード */
    private TreeViewTreeNode sourceNode;

    /**
     * ドラッグ開始時に呼ばれます．
     */
    public void dragEnter(DropTargetDragEvent ev) {
        super.dragEnter(ev);
        // ソースを保持します．
        sourceNode = tree.getTreeNode();
Debug.println("src: " + sourceNode);
Debug.println("src hash: " + sourceNode.hashCode());
    }

    /**
     * ドラッグ動作中に呼ばれます．
     */
    public void dragOver(DropTargetDragEvent ev) {
        super.dragOver(ev);
        // マウスの位置のノードを選択します．
        Point point = ev.getLocation();
        TreePath path = tree.getPathForLocation(point.x, point.y);
        if (path == null) {
            tree.clearSelection();
        } else {
            tree.setSelectionPath(path);
        }
    }

    /**
     * You need to implement here dropping procedure. data はシリアライズされたものをデシリアライズした ものなのでクローンです．
     * 
     * @param data ドロップされたデータ
     */
    protected boolean dropImpl(DropTargetDropEvent ev, Object data) {

Debug.println("data class: " + StringUtil.getClassName(data.getClass()));
Debug.println("data: " + data);
Debug.println("data hash: " + data.hashCode());
Debug.println("data user: " + ((TreeViewTreeNode) data).getUserObject());
Debug.println("data usrH: " + ((TreeViewTreeNode) data).getUserObject().hashCode());

        if (!(data instanceof TreeViewTreeNode)) {
            Debug.println(Level.WARNING, "data is not node");
            return false;
        }

        // target path
        TreePath path = tree.getPathForLocation((int) ev.getLocation().getX(), (int) ev.getLocation().getY());
        TreeViewTreeNode targetNode = null;
        if (path == null) {
            tree.clearSelection();
            Debug.println(Level.WARNING, "target is not node");
            return false;
        } else {
            tree.setSelectionPath(path);
            targetNode = (TreeViewTreeNode) path.getLastPathComponent();
            // Debug.println("target class: " + targetNode.getClass().getName());
Debug.println("target: " + targetNode);
            // Debug.println("target hash: " + targetNode.hashCode());
        }

        try {
            int action = ev.getDropAction();
Debug.println("action: " + action);
            if (action == DnDConstants.ACTION_COPY) {
Debug.println("copy action");
                // sourceNode.copy();
                targetNode.paste((TreeViewTreeNode) data);
Debug.println("copy: " + sourceNode + " to " + targetNode);
            } else if (action == DnDConstants.ACTION_MOVE) {
Debug.println("move action");
                sourceNode.cut();
                targetNode.paste(sourceNode);
Debug.println("move: " + sourceNode + " to " + targetNode);
            } else {
Debug.println("unknown action: " + action);
            }

Debug.println("success");
            return true;
        } catch (Exception e) {
Debug.println(e);
            return false;
        }
    }
}

/* */
