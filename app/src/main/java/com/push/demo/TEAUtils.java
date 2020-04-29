package com.push.demo;

public class TEAUtils {
    private static final int DELTA = 0x9E3779B9;

    public static int[] encrypt(int[] keys, int[] data, int rounds) {
        if (data.length == 0 || (data.length & 1) != 0)
            throw new IllegalArgumentException();

        int[] out = copy(data);

        for (int idx = 0; idx < data.length; idx += 2) {
            encrypt(keys, out, rounds, idx);
        }

        return out;
    }


    public static int[] copy(int[] data) {
        int[] copy = new int[data.length];
        for (int idx = 0; idx < data.length; idx++) {
            copy[idx] = data[idx];
        }
        return copy;
    }


    private static void encrypt(int[] keys, int[] data, int rounds, int idx) {
        if (data.length < idx + 1)
            throw new IllegalArgumentException();

        int keyBits = keys.length - 1;

        int sum = 0;
        int v0 = data[idx];
        int v1 = data[idx + 1];

        for (int round = 0; round < rounds; ++round) {
            v0 += (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + keys[(idx + sum) & keyBits]);
            sum += DELTA;
            v1 += (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + keys[(idx + (sum >>> 11)) & keyBits]);
        }

        data[idx] = v0;
        data[idx + 1] = v1;
    }

    public static int[] decrypt(int[] keys, int[] data, int rounds) {
        if (data.length == 0 || (data.length & 1) != 0)
            throw new IllegalArgumentException();

        int[] out = copy(data);

        for (int idx = 0; idx < data.length; idx += 2)
            decrypt(keys, out, rounds, idx);

        return out;
    }

    private static void decrypt(int[] keys, int[] data, int rounds, int idx) {
        if (data.length < idx + 1)
            throw new IllegalArgumentException();

        int keyBits = keys.length - 1;
        int sum = DELTA * rounds;
        int v0 = data[idx];
        int v1 = data[idx + 1];

        for (int round = 0; round < rounds; ++round) {
            v1 -= (((v0 << 4) ^ (v0 >>> 5)) + v0) ^ (sum + keys[(idx + (sum >>> 11)) & keyBits]);
            sum -= DELTA;
            v0 -= (((v1 << 4) ^ (v1 >>> 5)) + v1) ^ (sum + keys[(idx + sum) & keyBits]);
        }

        data[idx] = v0;
        data[idx + 1] = v1;
    }

}
