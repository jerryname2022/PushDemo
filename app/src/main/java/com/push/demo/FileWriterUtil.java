package com.push.demo;

import java.io.*;


public class FileWriterUtil {

    public static void mkdir(String path) {
        mkdir(new File(path));
    }

    public static void mkdir(File path) {
        if (!path.exists()) {
            path.mkdirs();
        }
    }


    public static void write(String file, String data) {
        write(file, data.getBytes());
    }


    public static void write(String file, byte[] data) {
        FileOutputStream fileWriter = null;
        if (data == null) return;

        try {
            fileWriter = new FileOutputStream(file, false);
            fileWriter.write(data);
        } catch (IOException var5) {
            //  Logger.getLogger(FileWriterUtil.class).warn("write file error", var5);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String read(String file) {
        FileReader fileReader = null;
        StringBuffer buff = new StringBuffer();
        try {
            fileReader = new FileReader(file);
            char[] buffer = new char[1024];
            int len = -1;

            while ((len = fileReader.read(buffer, 0, buffer.length)) != -1) {
                buff.append(buffer, 0, len);
            }

            return buff.toString();
        } catch (IOException var5) {
            //  Logger.getLogger(FileWriterUtil.class).warn("read file error", var5);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    public static void write(File file, String data) {
        write(file.getAbsolutePath(), data);
    }

    public static void write(File file, byte[] data) {
        write(file.getAbsolutePath(), data);
    }

}
