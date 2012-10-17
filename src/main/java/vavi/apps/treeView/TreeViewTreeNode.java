/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import vavi.awt.Selectable;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;


/**
 * The base node class for TreeView.
 * 
 * @event EditorEvent("expand")
 * @event EditorEvent("delete")
 * @event EditorEvent("insert")
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public abstract class TreeViewTreeNode extends DefaultMutableTreeNode implements Selectable {

    /** リソースバンドル */
    protected static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.treeView.TreeViewResource", Locale.getDefault());

    /**
     * カットされたかどうか
     */
    protected boolean isCut = false;

    /** */
    public boolean isCut() {
        return isCut;
    }

    /**
     * TreeView で使用する基本のツリーノードを作成します．
     * 
     * @param userObject ノードのデータ
     */
    public TreeViewTreeNode(Object userObject) {
        super(userObject);
        this.userObject = userObject;
    }

    /** */
    public String toString() {
        return userObject.toString();
    }

    /** */
    public void setActionStates() {
    }

    // -------------------------------------------------------------------------

    /**
     * オープンします．
     * 
     * @throws TreeViewException できなかった場合
     */
    public void open() throws TreeViewException {
        throw new TreeViewException(rb.getString("action.open.error"));
    }

    /**
     * 名前を変更します．
     * 
     * @param name 変更後の表示名
     * @throws TreeViewException できなかった場合
     */
    public void rename(String name) throws TreeViewException {
        throw new TreeViewException(rb.getString("action.rename.error"));
    }

    /**
     * カットします．
     * 
     * @throws TreeViewException できなかった場合
     */
    public void cut() throws TreeViewException {
        throw new TreeViewException(rb.getString("action.cut.error"));
    }

    /**
     * コピーします．
     * 
     * @throws TreeViewException コピーできなかった
     */
    public void copy() throws TreeViewException {
        throw new TreeViewException(rb.getString("action.copy.error"));
    }

    /**
     * ペーストします．
     * 
     * @throws TreeViewException できなかった場合
     */
    public void paste(TreeViewTreeNode from) throws TreeViewException {
        throw new TreeViewException(rb.getString("action.paste.error"));
    }

    /**
     * 移動します．
     * 
     * @throws TreeViewException できなかった場合
     */
    protected void move(TreeViewTreeNode to) throws TreeViewException {
        throw new TreeViewException(rb.getString("action.paste.error"));
    }

    /**
     * 削除します．
     * 
     * @throws TreeViewException できなかった場合
     */
    public void delete() throws TreeViewException {
        throw new TreeViewException(rb.getString("action.delete.error"));
    }

    // -------------------------------------------------------------------------

    /** オープンしたときビューを変更します． */
    protected void openController() {
        fireEditorUpdated(new EditorEvent(this, "expand", new TreePath(getPath())));
    }

    /** 削除したときビューを変更します． */
    protected void deleteController() {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) getParent();

        // node
        int index = parent.getIndex(this);
        parent.remove(index);
        this.removeFromParent();

        // model
        int[] indices = new int[1];
        indices[0] = index;
        DefaultMutableTreeNode[] removed = new DefaultMutableTreeNode[1];
        removed[0] = this;

        fireEditorUpdated(new EditorEvent(this, "delete", new Object[] {
            parent, indices, removed
        }));
    }

    /**
     * ペーストしたときビューを変更します． カットかコピーにあわせて from をそのものか コピーしたものかをユーザが設定する必要があります．
     */
    protected void pasteController(TreeViewTreeNode from) {
        addController(from);
    }

    /** 移動したときビューを変更します． */
    protected void moveController(TreeViewTreeNode to) {
        deleteController();
        to.addController(this);
    }

    /** 追加したときビューを変更します． */
    protected void addController(DefaultMutableTreeNode toAdd) {
        this.add(toAdd);

        fireEditorUpdated(new EditorEvent(this, "insert", this));

        openController();
    }

    // -------------------------------------------------------------------------

    /** オープンできるかどうか */
    public boolean canOpen() {
        return false;
    }

    /** リネームできるかどうか */
    public boolean canRename() {
        return false;
    }

    /** カットできるかどうか */
    public boolean canCut() {
        return false;
    }

    /** コピーできるかどうか */
    public boolean canCopy() {
        return false;
    }

    /** ペーストできるかどうか */
    public boolean canPaste(TreeViewTreeNode from) {
        return false;
    }

    /** 削除できるかどうか */
    public boolean canDelete() {
        return false;
    }

    // -------------------------------------------------------------------------

    /**
     * {@link DefaultMutableTreeNode#getUserObject()} は transient です。
     * このツリーノードを直列化するためにオーバライドします。
     */
    protected Object userObject;

    /**
     * {@link DefaultMutableTreeNode#getUserObject()} は transient です。
     * このツリーノードを直列化するためにオーバライドします。
     */
    public void setUserObject(Object userObject) {
        this.userObject = userObject;
    }

    /**
     * {@link DefaultMutableTreeNode#getUserObject()} は transient です。
     * このツリーノードを直列化するためにオーバライドします。
     */
    public Object getUserObject() {
        return userObject;
    }

    //----

    /** EditorEvent 機構のユーティリティ */
    private static EditorSupport editorSupport = new EditorSupport();

    /** Editor リスナーを追加します． */
    public synchronized void addEditorListener(EditorListener l) {
        editorSupport.addEditorListener(l);
    }

    /** Editor リスナーを削除します． */
    public synchronized void removeEditorListener(EditorListener l) {
        editorSupport.removeEditorListener(l);
    }

    /** EditorEvent を発行します． */
    protected void fireEditorUpdated(EditorEvent ev) {
        editorSupport.fireEditorUpdated(ev);
    }

    //----

    private boolean isSelected;

    /** */
    public boolean isSelected() {
        return isSelected;
    }

    /** */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}

/* */
