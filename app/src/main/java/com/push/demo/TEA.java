package com.push.demo;

import android.content.Intent;

public class TEA {

    /**
     * Primary encryption algorithm<br>
     */
    public static class Engine {
        private static final int delta = 0x9E3779B9;

        private final int rounds;
        private final int[] key;
        private final int keybits;

        public Engine(int[] key) {
            this(key, 32);
        }

        public Engine(int[] key, int rounds) {
            if (rounds <= 0)
                throw new IllegalArgumentException();
            if (key.length == 0 || (key.length & 3) != 0)
                throw new IllegalArgumentException();
            this.key = key;
            this.rounds = rounds;
            this.keybits = key.length - 1;
        }

        public void encrypt(int[] data) {
            if (data.length == 0 || (data.length & 1) != 0)
                throw new IllegalArgumentException();

            for (int idx = 0; idx < data.length; idx += 2)
                encrypt(data, idx);
        }

        public void encrypt(int[] data, int idx) {
            if (data.length < idx + 1)
                throw new IllegalArgumentException();

            int sum = 0;
            int v0 = data[idx];
            int v1 = data[idx + 1];

            for (int round = 0; round < rounds; ++round) {
                v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[(idx + sum) & keybits]);
                sum += delta;
                v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(idx + (sum >>> 11)) & keybits]);
            }

            data[idx] = v0;
            data[idx + 1] = v1;
        }

        public void decrypt(int[] data) {
            if (data.length == 0 || (data.length & 1) != 0)
                throw new IllegalArgumentException();

            for (int idx = 0; idx < data.length; idx += 2)
                decrypt(data, idx);
        }

        public void decrypt(int[] data, int idx) {
            if (data.length < idx + 1)
                throw new IllegalArgumentException();

            int sum = delta * rounds;
            int v0 = data[idx];
            int v1 = data[idx + 1];

            for (int round = 0; round < rounds; ++round) {
                v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + key[(idx + (sum >>> 11)) & keybits]);
                sum -= delta;
                v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + key[(idx + sum) & keybits]);
            }

            data[idx] = v0;
            data[idx + 1] = v1;
        }
    }


    private final int[] key;

    public TEA(String key) {
        this(toByteArray(key));
    }

    private static byte[] toByteArray(String key) {
        byte[] bkey = new byte[key.length()];
        for (int i = 0; i < key.length(); ++i) {
            bkey[i] = (byte) key.charAt(i);
        }
        return bkey;
    }

    public TEA(byte[] key) {
        if (key.length < 1)
            throw new IllegalArgumentException();

        this.key = new int[alignInt(alignX(key.length, 16))];
        for (int i = 0; i < this.key.length; i += 1)
            this.key[i] = makeInt(key, i << 2);
    }

    public byte[] encrypt(byte[] data) {
        int[] buffer = toIntArray(data);
        new Engine(key).encrypt(buffer);
        return toByteArray(buffer);
    }

    public byte[] decrypt(byte[] data) {
        int[] buffer = toIntArray2(data);
        new Engine(key).decrypt(buffer);
        return toByteArray2(buffer);
    }

    public byte[] encryptString(String text) {
        return encrypt(toByteArray(text));
    }

    public String decryptString(byte[] data) {
        return new String(toCharArray(decrypt(data)));
    }

    private char[] toCharArray(byte[] data) {
        char[] chars = new char[data.length];
        for (int i = 0; i < data.length; ++i)
            chars[i] = (char) data[i];
        return chars;
    }

    private static int alignX(int length, int i) {
        int r = length / i * i;
        if (r != length)
            r += i;
        return r;
    }

    private static int alignInt(int length) {
        int r = length >> 2;
        if ((r << 2) != length)
            r += 1;
        return r;
    }

    private static int[] toIntArray(byte[] data) {
        int[] buffer = new int[alignInt(alignX(data.length + 4, 8))];
        buffer[0] = data.length;
        for (int i = 1; i < buffer.length; i++)
            buffer[i] = makeInt(data, (i - 1) << 2);
        return buffer;
    }

    private static int[] toIntArray2(byte[] data) {
        int[] buffer = new int[alignInt(alignX(data.length, 8))];
        for (int i = 0; i < buffer.length; i++)
            buffer[i] = makeInt(data, i << 2);
        return buffer;
    }

    private static int makeInt(byte[] bytes, int offset) {
        int r = 0;
        for (int n = 0; n < 4; ++n)
            r = (r << 8) | (bytes[offset++ % bytes.length] & 0xff);
        return r;
    }

    private static byte[] toByteArray(int[] buffer) {
        byte[] encrypted = new byte[buffer.length << 2];
        for (int i = 0; i < buffer.length; ++i)
            toByteArray(encrypted, i << 2, buffer[i]);
        return encrypted;
    }

    private static byte[] toByteArray2(int[] buffer) {
        byte[] encrypted = new byte[buffer[0]];
        for (int i = 0; i < encrypted.length; i += 4)
            toByteArray(encrypted, i, buffer[i / 4 + 1]);
        return encrypted;
    }

    private static void toByteArray(byte[] encrypted, int idx, int val) {
        encrypted[idx++] = (byte) (((val >>> 24)) & 0xff);
        if (idx < encrypted.length) {
            encrypted[idx++] = (byte) (((val >>> 16)) & 0xff);
            if (idx < encrypted.length) {
                encrypted[idx++] = (byte) (((val >>> 8)) & 0xff);
                if (idx < encrypted.length) {
                    encrypted[idx] = (byte) (((val)) & 0xff);
                }
            }
        }
    }


    public static byte[] xTeaEncipher(byte[] random, String bookingId, String secretKey, String sesKey) {
        int[] vIn = segmentList(byte2Int(random), 2);
        int[] key = segmentList(byte2Int(secretKey.getBytes()), 4);
        int[] encrypt = TEAUtils.encrypt(key, vIn, 32);

        return int2Byte(encrypt);
    }


    public static int[] byte2Int(byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0; i < bytes.length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }


    public static byte[] int2Byte(int[] ints) {
        byte[] bytes = new byte[ints.length * 4];

        int index = 0;
        for (int i = 0; i < ints.length; i++) {
            int value = ints[i];
            index = i * 4;
            bytes[index] = (byte) ((value >> 24) & 0xFF);
            bytes[index + 1] = (byte) ((value >> 16) & 0xFF);
            bytes[index + 2] = (byte) ((value >> 8) & 0xFF);
            bytes[index + 3] = (byte) (value & 0xFF);
        }

        return bytes;
    }


    public static void main(String[] args) {

        int[] secretKeyValue = {0x011, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28};

        int[] gens = {0x22, 0x33, 0x44, 0x66, 0x77, 0x88};
        int[] test = segmentList(gens, 2);

        int[] vin = segmentList(gens, 2);// {0x00223344, 0x00667788};
        int[] key = segmentList(secretKeyValue, 4);//{0x11121314, 0x15161718, 0x21222324, 0x25262728};
        int[] out = {0xB1401D74, 0xC100E232};

        System.out.println(vin[0] + " " + vin[1] + " -- " + test[0] + " " + test[1]);

        vin = TEAUtils.encrypt(key, vin, 32);

        System.out.println(" " + (vin[0] == out[0]) + " " + (vin[1] == out[1]));

        vin = TEAUtils.decrypt(key, vin, 32);

        System.out.println(" " + (vin[0] == 0x00223344) + " " + (vin[1] == 0x00667788));
    }


    public static int[] segmentList(int[] data, int len) {

        int[] segments = new int[len];
        int count = data.length / len;
        if (count * len < data.length) {
            count += 1;
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("00");

        int index = 0;
        int flag = 0;
        for (int i = 0; i < data.length; i++) {
            if (flag >= count) {
                segments[index] = Integer.parseInt(buffer.toString(), 16);

                buffer.delete(0, buffer.length());
                flag = 0;
                index++;
                buffer.append("00");
            }

            String format = String.format("%02x", (data[i] & 0xFF));
            buffer.append(format);
            flag++;
        }

        segments[index] = Integer.parseInt(buffer.toString(), 16);
        return segments;
    }


}
