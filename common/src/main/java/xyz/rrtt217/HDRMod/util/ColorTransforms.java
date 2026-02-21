package xyz.rrtt217.HDRMod.util;

public class ColorTransforms {
    public static final float[][] linear709to2020Matrix =  new float[][]{
        {0.6274039149284363f, 0.3292830288410187f, 0.04331306740641594f},
        {0.06909728795289993f, 0.9195404052734375f, 0.01136231515556574f},
        {0.0163914393633604f, 0.08801330626010895f, 0.8955952525138855f}
    };

    public static float[] sRGBDecodeSafe(float[] c) {
        if (c.length < 3) {
            throw new IllegalArgumentException("There must be at least three elements in a color array");
        }
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            float val = c[i];
            float sign = (val > 0) ? 1.0f : (val < 0) ? -1.0f : 0.0f;
            float absVal = Math.abs(val);

            float decoded;
            if (absVal < 0.04045f) {
                decoded = absVal / 12.92f;
            } else {
                decoded = (float) Math.pow((absVal + 0.055f) / 1.055f, 2.4f);
            }
            result[i] = decoded * sign;
        }
        return result;
    }

    public static float[] PQEncode(float[] scRGB, float referenceWhiteNits) {
        // PQ constants (SMPTE ST 2084)
        final float m1 = 2610.0f / 16384.0f;
        final float m2 = 2523.0f / 4096.0f * 128.0f;
        final float c1 = 3424.0f / 4096.0f;         // 0.8359375
        final float c2 = 2413.0f / 4096.0f * 32.0f; // 18.8515625
        final float c3 = 2392.0f / 4096.0f * 32.0f; // 18.6875

        float[] pq = new float[3];
        for (int i = 0; i < 3; i++) {
            // scRGB to absolute brightness
            float linearNits = scRGB[i] * referenceWhiteNits;

            // Clamp
            linearNits = Math.max(linearNits, 0.0f);

            // Normalise
            float Y = linearNits / 10000.0f;
            Y = Math.min(Y, 1.0f); // 规范要求 Y ≤ 1

            // PQ encode
            float Ypow = (float) Math.pow(Y, m1);
            float num = c1 + c2 * Ypow;
            float den = 1.0f + c3 * Ypow;
            float V = (float) Math.pow(num / den, m2);

            pq[i] = V;
        }
        return pq;
    }

    public static float[] linearColorspaceTransform(float[] originalData, float[][] transformMatrix){
        float r = originalData[0];
        float g = originalData[1];
        float b = originalData[2];

        float rnew = transformMatrix[0][0] * r + transformMatrix[0][1] * g + transformMatrix[0][2] * b;
        float gnew = transformMatrix[1][0] * r + transformMatrix[1][1] * g + transformMatrix[1][2] * b;
        float bnew = transformMatrix[2][0] * r + transformMatrix[2][1] * g + transformMatrix[2][2] * b;

        return new float[]{rnew, gnew, bnew};
    }

    // Const gamma=2.2
    private static final float GAMMA = 2.2f;

    public static float sRGBEncodeSafe(float c) {
        if (c <= 0.0031308f) {
            return c * 12.92f;
        } else {
            return 1.055f * (float) Math.pow(c, 1.0 / 2.4) - 0.055f;
        }
    }

    private static float sign(float x) {
        if (x > 0) return 1.0f;
        if (x < 0) return -1.0f;
        return 0.0f;
    }

    public static float[] eotfEmulate(float[] color, float threshold) {
        if (color.length < 3) {
            throw new IllegalArgumentException("There must be at least three elements in a color array");
        }
        float[] result = new float[3];
        for (int i = 0; i < 3; i++) {
            float val = color[i] / threshold;
            float s = sign(val);
            float a = Math.abs(val);

            float encoded = sRGBEncodeSafe(a);
            float corrected = (float) Math.pow(encoded, GAMMA);

            float temp = (a < 1.0f) ? (s * corrected) : val;

            result[i] = temp * threshold;
        }
        return result;
    }

}
