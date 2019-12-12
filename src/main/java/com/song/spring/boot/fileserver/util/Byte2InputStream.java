package com.song.spring.boot.fileserver.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Byte2InputStream {
    /**
     * 功能描述: byte数组转 InputStream
     *
     * @param bytes byte数组
     * @return java.io.InputStream
     * @author xiaobu
     * @date 2019/3/28 16:01
     * @version 1.0
     */
    public static InputStream byte2InputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }


    /**
     * 功能描述:
     *
     * @param inputStream 输入流
     * @return byte[] 数组
     * @author xiaobu
     * @date 2019/3/28 16:03
     * @version 1.0
     */
    public static byte[] inputStream2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[10240];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 10240)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
