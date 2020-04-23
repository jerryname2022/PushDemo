package com.example.jpushdemo;


import java.security.MessageDigest;
import java.util.Random;

public class TestBookId {
    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static String decryptByMD5(String data) {
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            result = buf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static String[] SOURCE = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    public static void main(String[] args) {


        /**
         * =================================
         * oHveUV3X4ZoQXZs
         * eqAG13YsXXGCHat
         * =================================
         * bEftNKTo4Tt13bt
         * MMwHMrodb7Fxvgu
         */

        String bookId1 = genBookingId("oHveUV3X4ZoQXZs");
        System.out.println("=================================");
        String bookId2 = genBookingId("eqAG13YsXXGCHat");
        System.out.println("=================================");
        String bookId3 = genBookingId("bEftNKTo4Tt13bt");
        System.out.println("=================================");
        String bookId4 = genBookingId("MMwHMrodb7Fxvgu");

//        List<String> bookingIds = new ArrayList<>();
//        List<String> secrets = new ArrayList<>();
//        Map<String, List<String>> bookingIdMaps = new HashMap<>();
//
//        int sameCount = 0;
//        for (int i = 0; i < 100000; i++) {
//            String newSecret = randomNumber(15);
//            if (!secrets.contains(newSecret)) {
//                String bookId = genBookingId(newSecret);
//
//                if (bookingIds.contains(bookId)) {
//                    sameCount++;
//                }
//
//                bookingIds.add(bookId);
//                secrets.add(newSecret);
//
//                List<String> strings = bookingIdMaps.get(bookId);
//                if (strings == null) {
//                    strings = new ArrayList<>();
//                }
//                strings.add(newSecret);
//                bookingIdMaps.put(bookId, strings);
//            }
//            System.out.println("=================================");
//        }
//        System.out.println("sameCount=" + sameCount);
//
//        Collection<List<String>> values = bookingIdMaps.values();
//
//        if (values != null) {
//            for (List<String> v : values) {
//                if (v.size() > 1) {
//                    System.out.println("=================================");
//                    for (String s : v) {
//                        System.out.println(s);
//                    }
//                }
//            }
//        }

    }


    static String genBookingId(String secret) {
        System.out.println("secret=" + secret);
        //MD5 32位加密
        String md32 = decryptByMD5(secret);
        System.out.println("md32=" + md32);
        //截取4至12位
        String bookingId = md32.substring(4, 12);
        System.out.println("bookingId=" + bookingId);
        return bookingId;
    }


    public static String randomNumber(int length) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            String item = SOURCE[random.nextInt(SOURCE.length)];
            buffer.append(item);
        }
        return buffer.toString();
    }

}
