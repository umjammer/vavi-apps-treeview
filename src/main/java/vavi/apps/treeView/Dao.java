/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Dao.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 041105 nsano initial version <br>
 */
public interface Dao {

    /** ルートのツリーノードを取得します． */
    TreeViewTreeNode read(InputStream is) throws IOException;

    /** ルート以下のツリーノードを書き込みます． */
    void write(TreeViewTreeNode root, OutputStream os) throws IOException;
}

/* */
