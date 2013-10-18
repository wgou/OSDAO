package org.util;


import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/** *//**
 * ����properties�����õ�·����jar�������ļ����ص�classpath�С�
 * @author ��ΰ
 *
 */
public final class ExtClasspathLoader {
    /** *//** URLClassLoader��addURL���� */
    private static Method addURL = initAddMethod();

    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

    /** *//**
     * ��ʼ��addUrl ����.
     * @return �ɷ���addUrl������Method����
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            add.setAccessible(true);
            return add;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void loadClasspath(String jar) {
        File file = new File(jar);
        loopFiles(file);
    }

    /** *//**
     * ѭ������Ŀ¼���ҳ����е�jar����
     * @param file ��ǰ�����ļ�
     */
    private static void loopFiles(File file) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        }
        else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
            }
        }
    }
    /** 
     * ͨ��filepath�����ļ���classpath��
     * @param filePath �ļ�·��
     * @return URL
     * @throws Exception �쳣
     */
    private static void addURL(File file) {
        try {
            addURL.invoke(classloader, new Object[] { file.toURI().toURL() });
        }
        catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        ExtClasspathLoader.loadClasspath("D:\\jar");
    }
}