/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.swing.binding.treeview;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

import vavi.apps.treeView.TreeViewTreeNode;


/**
 * TreeView.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022/12/11 nsano initial version <br>
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TreeView {

    String init() default "init";

    String load() default "load";

    String save() default "save";

    /**
     * TODO when annotated to method
     */
    class Util {

        private Util() {
        }

        /** */
        public static InputStream init(Object bean) {
            //
            TreeView annotation = bean.getClass().getAnnotation(TreeView.class);
            if (annotation == null) {
                throw new IllegalArgumentException("bean is not annotated with @TreeView");
            }

            String name = annotation.init();

            Class<?> clazz = bean.getClass();
            while (clazz != null) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(name)) {
                        try {
                            return (InputStream) method.invoke(bean);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            throw new NoSuchElementException(name);
        }

        /** */
        public static TreeViewTreeNode load(Object bean, InputStream is) {
            //
            TreeView annotation = bean.getClass().getAnnotation(TreeView.class);
            if (annotation == null) {
                throw new IllegalArgumentException("bean is not annotated with @TreeView");
            }

            String name = annotation.load();

            Class<?> clazz = bean.getClass();
            while (clazz != null) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(name)) {
                        try {
                            return (TreeViewTreeNode) method.invoke(bean, is);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            throw new NoSuchElementException(name);
        }

        /** */
        public static void save(Object bean, OutputStream os, TreeViewTreeNode root) {
            //
            TreeView annotation = bean.getClass().getAnnotation(TreeView.class);
            if (annotation == null) {
                throw new IllegalArgumentException("bean is not annotated with @TreeView");
            }

            String name = annotation.save();

            Class<?> clazz = bean.getClass();
            while (clazz != null) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.getName().equals(name)) {
                        try {
                            method.invoke(bean, os, root);
                            return;
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

            throw new NoSuchElementException(name);
        }
    }
}

/* */
