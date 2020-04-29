package com.push.demo;

import java.util.Formatter;
import java.util.Locale;
import java.util.zip.CRC32;

public class BluetoothUtils {

    /**
     * --1byte   1byte   1byte   1byte   n byte  1byte
     * |-------|--------|------|-------|-------|-------|
     * |  head | length | op1  | op2   | data  | crc32 |
     * |-------|--------|------|-------|-------|-------|
     */

    public final static int HEAD_LEN = 1;
    public final static int LENGTH_LEN = 1;
    public final static int OP1_LEN = 1;
    public final static int OP2_LEN = 1;
    public final static int CRC_LEN = 1;

    public final static int INDEX_HEAD = 0;
    public final static int INDEX_LENGTH = INDEX_HEAD + HEAD_LEN;
    public final static int INDEX_OP1 = INDEX_LENGTH + LENGTH_LEN;
    public final static int INDEX_OP2 = INDEX_OP1 + OP1_LEN;
    public final static int INDEX_DATA = INDEX_OP2 + OP2_LEN;


    public static byte[] getAuthCmd(String bookingId) {
        byte[] data = bookingId.getBytes();
        return getAuthCmd(data);
    }

    public static byte[] getAuthCmd(byte[] data) {
        byte op1 = 0x0B;
        byte op2 = 0x00;
        return getCmd(op1, op2, data);
    }


    public static byte[] getCmd(byte op1, byte op2, byte[] data) {
        int requestLen = HEAD_LEN + LENGTH_LEN + OP1_LEN + OP2_LEN + CRC_LEN;
        if (data != null) {
            requestLen += data.length;
        }

        byte[] request = new byte[requestLen];

        setCmdIndex(request, INDEX_HEAD, (byte) 0xF0);
        setCmdIndex(request, INDEX_LENGTH, (byte) requestLen);
        setCmdIndex(request, INDEX_OP1, op1);
        setCmdIndex(request, INDEX_OP2, op2);

        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                setCmdIndex(request, INDEX_DATA + i, data[i]);
            }
        }

        setCmdIndex(request, request.length - 1, (byte) encodeCRC32(request));

        return request;
    }


    public static long encodeCRC32(byte[] data) {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }


    public static void setCmdIndex(byte[] request, int index, byte data) {
        request[index] = data;
    }


    public static byte parserHead(byte[] data) {
        return parserResponse(data, INDEX_HEAD);
    }

    public static byte parserLength(byte[] data) {
        return parserResponse(data, INDEX_LENGTH);
    }

    public static byte parserOp1(byte[] data) {
        return parserResponse(data, INDEX_OP1);
    }

    public static byte parserOp2(byte[] data) {
        return parserResponse(data, INDEX_OP2);
    }

    public static byte parserCrc32(byte[] data) {
        return parserResponse(data, data.length - 1);
    }

    public static byte[] parserData(byte[] data) {
        int requestLen = HEAD_LEN + LENGTH_LEN + OP1_LEN + OP2_LEN + CRC_LEN;
        int dataLen = data.length - requestLen;
        byte[] responseData = new byte[dataLen];

        for (int i = 0; i < responseData.length; i++) {
            setCmdIndex(responseData, i, data[INDEX_DATA + i]);
        }
        return responseData;
    }

    public static byte parserResponse(byte[] data, int index) {
        return data[index];
    }


    public static String format(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int n = 0; n < bytes.length; n++) {
            String f = String.format("%02x", (bytes[n] & 0xFF));
            buffer.append(f + ":");
        }
        return buffer.toString();
    }

    public static String format(int[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int n = 0; n < bytes.length; n++) {
            String f = String.format("%02x", (bytes[n] & 0xFF));
            buffer.append(f + ":");
        }
        return buffer.toString();
    }

    public static void main(String[] args) {
        String bookingId = "5421573F";
        // String secretKeyValue = "9f8f5421573f53c0";
        // String aesKey = "eb88275a3b444f97";

        byte[] auth = BluetoothUtils.getAuthCmd(bookingId);
        for (byte b : auth) {
            System.out.print(Integer.toHexString(b & 0xFF) + ":");
        }
        System.out.println(auth.length);
        System.out.println(format(auth));
    }
}
