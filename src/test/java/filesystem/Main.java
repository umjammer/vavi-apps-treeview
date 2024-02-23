/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package filesystem;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Stream;
import javax.swing.JFrame;

import sample.Test;
import vavi.apps.treeView.FileTreeNode;
import vavi.apps.treeView.FolderTreeNode;
import vavi.apps.treeView.TreeNodeInfo;
import vavi.apps.treeView.TreeViewException;
import vavi.apps.treeView.TreeViewFrame;
import vavi.apps.treeView.TreeViewTreeNode;
import vavi.nio.file.googledrive.GoogleDriveFileSystemProvider;
import vavi.swing.binding.treeview.TreeView;
import vavi.util.Debug;


/**
 * Main.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-12-11 nsano initial version <br>
 */
@TreeView
public class Main {

    /** for node display name */
    static class WrappedPath {
        Path path;
        public WrappedPath(Path path) {
            this.path = path;
        }
        @Override public String toString() { return path.getFileName() != null ? path.getFileName().toString() : "/"; }
    }

    /** file */
    private static class FSFolderTreeNode extends FolderTreeNode {

        public FSFolderTreeNode(Path path) {
            super(new WrappedPath(path));
        }

        @Override public void open() throws TreeViewException {
Debug.println("open: " + userObject + ", " + Files.isDirectory($()));
            try {
                if (Files.isDirectory($())) {
                    try (Stream<Path> files = Files.list($())) {
                        files.forEach(p -> add(Files.isDirectory(p) ? new FSFolderTreeNode(p)
                                                                    : new FSFileTreeNode(p)));
                    }
                }
            } catch (IOException e) {
Debug.printStackTrace(e);
                throw new TreeViewException(e);
            }
        }

        private Path $() {
            return ((WrappedPath) userObject).path;
        }
    }

    /** folder */
    private static class FSFileTreeNode extends FileTreeNode {

        public FSFileTreeNode(Path path) {
            super(new WrappedPath(path));
        }
    }

    /** bean info */
    public static class WrappedPathBeanInfo extends SimpleBeanInfo implements TreeNodeInfo {

        @Override
        public BeanDescriptor getBeanDescriptor() {
            return new BeanDescriptor(WrappedPathBeanInfo.class, null);
        }

        @Override
        public Image getIcon(int iconKind) {
            return switch (iconKind) {
                case ICON_COLOR_16x16_EXT2 -> loadImage("/vavi/apps/treeView/node/default_open.png");
                case ICON_COLOR_16x16_EXT1 -> loadImage("/vavi/apps/treeView/node/default_close.png");
                default -> loadImage("/vavi/apps/treeView/node/default_file.png");
            };
        }
    }

    /** */
    private Path root;

    /** */
    public void init() throws IOException {
        String email = System.getenv("GOOGLE_TEST_ACCOUNT");

        URI uri = URI.create("googledrive:///?id=" + email);
        FileSystem fs = new GoogleDriveFileSystemProvider().newFileSystem(uri, Collections.emptyMap());

        root = fs.getRootDirectories().iterator().next();
//        root = Paths.get("/Users/nsano/GoogleDrive/My Drive/Books");
//        root = Paths.get("/Users/nsano/Downloads/JDownloader");
    }

    /** */
    public TreeViewTreeNode load(InputStream is) throws IOException {
        return new FSFolderTreeNode(root);
    }

    /** */
    public void save(OutputStream os, TreeViewTreeNode root) throws IOException {
    }

    /**
     * The program entry pont.
     */
    public static void main(String[] args) throws Exception {

        vavi.apps.treeView.TreeView tree = new vavi.apps.treeView.TreeView(new Main());

        JFrame frame = new TreeViewFrame(tree);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
