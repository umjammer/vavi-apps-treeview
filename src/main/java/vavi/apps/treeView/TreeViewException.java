/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

/**
 * TreeView exception．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010820 nsano initial version <br>
 *          0.01 010822 nsano add inclusive exception <br>
 */
public final class TreeViewException extends Exception {

    /** 内包エラー */
    private Throwable throwable;

    /**
     * 詳細メッセージを持たない TreeViewException を構築します．
     */
    public TreeViewException() {
        super();
    }

    /**
     * 詳細メッセージを持つ TreeViewException を構築します．
     * 
     * @param s 詳細メッセージ
     */
    public TreeViewException(String s) {
        super(s);
    }

    /**
     * 内包エラーを持つ TreeViewException を構築します．
     * 
     * @param t 内包するエラー
     */
    public TreeViewException(Throwable t) {
        this.throwable = t;
    }

    /**
     * 内包エラーを返します．
     */
    public Throwable getThrowable() {
        return throwable;
    }
}

/* */
