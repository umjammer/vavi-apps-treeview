/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import vavi.awt.SelectionTransferable;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;
import vavi.util.Debug;


/**
 * JTree のエディタです．
 * 
 * @event EditorEvent("cut", List<Component>)
 * @event EditorEvent("copy", List<Component>)
 * @event EditorEvent("delete", List<Component>)
 * @event EditorEvent("lostOwnership")
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020625 nsano initial version <br>
 */
public class TreeViewTreeEditor {

    /** */
    TreeViewTree tree;

    /** */
    public TreeViewTreeEditor(TreeViewTree tree) {
        tree.addEditorListener(el);
    }

    // -------------------------------------------------------------------------

    /** */
    private List<TreeViewTreeNode> selection;

    /** */
    public void cut() throws TreeViewException, IOException {
        currentClipboard = systemClipboard;
        Transferable transferable = new SelectionTransferable(selection);
        currentClipboard.setContents(transferable, clipboardOwner);

        for (TreeViewTreeNode treeViewTreeNode : selection) {
            treeViewTreeNode.cut();
        }

        fireEditorUpdated(new EditorEvent(this, "cut", selection));
    }

    /** */
    public void copy() throws TreeViewException, IOException {
        currentClipboard = systemClipboard;
        Transferable transferable = new SelectionTransferable(selection);
        currentClipboard.setContents(transferable, clipboardOwner);

        for (TreeViewTreeNode treeViewTreeNode : selection) {
            treeViewTreeNode.copy();
        }

        fireEditorUpdated(new EditorEvent(this, "copy", selection));
    }

    /** */
    @SuppressWarnings("unchecked")
    public void paste() throws TreeViewException, IOException, UnsupportedFlavorException {

        TreeViewTreeNode node = tree.getTreeNode();
        if (node == null) {
            return;
        }

        DataFlavor flavor = SelectionTransferable.selectionFlavor;
        Transferable transferable = currentClipboard.getContents(this);
        selection = (List<TreeViewTreeNode>) transferable.getTransferData(flavor);

        for (TreeViewTreeNode treeViewTreeNode : selection) {
            node.paste(treeViewTreeNode);
        }
    }

    /** */
    public void delete() throws TreeViewException {
        for (TreeViewTreeNode node : selection) {
            node.delete();
        }

        selection.clear();
        fireEditorUpdated(new EditorEvent(this, "select", selection));
    }

    // -------------------------------------------------------------------------

    /** */
    private final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    /** */
    private final Clipboard localClipboard = new Clipboard(this.getClass().getName());

    /** */
    private Clipboard currentClipboard;

    /** */
    private ClipboardOwner clipboardOwner = new ClipboardOwner() {
        /** Called when lost the ownership. */
        @Override public void lostOwnership(Clipboard clipboard, Transferable contents) {
            if (clipboard == systemClipboard) {
Debug.println(clipboard.getName());
                localClipboard.setContents(contents, this);
                currentClipboard = localClipboard;
            } else {
Debug.println("???: " + clipboard.getName());
                fireEditorUpdated(new EditorEvent(TreeViewTreeEditor.this, "lostOwnership"));
            }
        }
    };

    // -------------------------------------------------------------------------

    /** */
    @SuppressWarnings("unchecked")
    private final EditorListener el = new EditorListener() {
        @Override public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("select".equals(name)) {
                selection = (List<TreeViewTreeNode>) ev.getArguments()[0];
            }
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
}

/* */
