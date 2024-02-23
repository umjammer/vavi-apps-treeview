/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;


/**
 * This class represents a file node.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 */
public abstract class FileTreeNode extends TreeViewTreeNode {

    /**
     * Creates file node.
     * 
     * @param data ノードのデータ
     */
    public FileTreeNode(Object data) {
        super(data);
    }

    // ----

    /**
     * Unselects targets.
     * 
     * @throws TreeViewException unselection failed
     */
    @Override
    public void delete() throws TreeViewException {
        deleteController();
    }

    /**
     * Sets targets as 'cut'．and 'paste' immediately means 'move'.
     * 
     * @throws TreeViewException selection failed
     */
    @Override
    public void cut() throws TreeViewException {
        // check to be able to cut.
        if (!canCut()) {
            throw new TreeViewException(rb.getString("action.cut.error"));
        }

        // set mode "cut"
        isCut = true;
    }

    /**
     * 選択されているターゲットをバッファにコピーします．
     * 
     * @throws TreeViewException コピーできなかった
     */
    @Override
    public void copy() throws TreeViewException {
        isCut = false;
    }

    /**
     * コピーバッファの内容を選択位置に貼り付けます． <br>
     * 前の操作が切り取りだった場合は移動になります． カットかコピーにあわせて from をそのものか コピーしたものかをユーザが設定する必要があります．
     * 
     * @throws TreeViewException できなかった場合
     */
    @Override
    public void paste(TreeViewTreeNode from) throws TreeViewException {

        if (from.isNodeChild(this)) {
            from.isCut = false;
            throw new TreeViewException(rb.getString("action.paste.nest"));
        }

        if (from.isCut()) {
            // 前の操作が切り取りだった場合は移動する
            from.move(this);
            return;
        }

        from.isCut = false;

        // 張り付けできるかどうかのチェック
        if (!this.canPaste(from)) {
            throw new TreeViewException(rb.getString("action.paste.error"));
        }

        pasteController(from);
    }

    /**
     * ツリーデータを移動します．
     * 
     * @param to 移動先
     * @throws TreeViewException できなかった場合
     */
    @Override
    protected void move(TreeViewTreeNode to) throws TreeViewException {

        isCut = false;

        // 移動対象と移動先が同じ場合は何もしない
        if (this.equals(to)) {
            throw new TreeViewException(rb.getString("action.paste.same"));
        }

        // 移動先が可能かどうか
        if (!to.canPaste(this)) {
            throw new TreeViewException(rb.getString("action.paste.error"));
        }

        moveController(to);
    }
}
