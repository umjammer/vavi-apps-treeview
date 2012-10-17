/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.treeView;

import java.io.IOException;


/**
 * XMLSaver.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 041105 nsano initial version <br>
 */
public interface XMLSaver {
    /** ルート以下のツリーノードを書き込みます． */
    void writeRootTreeNode(TreeViewTreeNode root) throws IOException;
}
/* */
