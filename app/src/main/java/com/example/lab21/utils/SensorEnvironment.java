package com.example.lab21.utils;

import android.hardware.Sensor;
import android.os.Build;

public class SensorEnvironment {

    private SensorEnvironment() {
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.FINGERPRINT.contains("emulator")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for")
                || Build.DEVICE.contains("goldfish")
                || Build.DEVICE.contains("ranchu")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk");
    }

    public static float magnitude(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static boolean isValidScalarValue(int sensorType, float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return false;
        }

        if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            return value > -30 && value < 70;
        }

        if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
            return value >= 0 && value <= 100;
        }

        if (sensorType == Sensor.TYPE_PROXIMITY) {
            return value >= 0 && value <= 20;
        }

        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            return value >= 0 && value <= 300;
        }

        return true;
    }

    public static float simulateScalarValue(int sensorType, float t) {
        if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            return 24f + (float) Math.sin(t / 4f) * 3f;
        }

        if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY) {
            return 55f + (float) Math.sin(t / 5f) * 18f;
        }

        if (sensorType == Sensor.TYPE_PROXIMITY) {
            return t % 8 < 4 ? 0f : 5f;
        }

        if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            return 45f + (float) Math.sin(t / 3f) * 15f;
        }

        return (float) Math.sin(t);
    }

    public static float[] simulateMotionValues(int sensorType, float t) {
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float x = (float) Math.sin(t / 4f) * 2.2f;
            float y = (float) Math.cos(t / 5f) * 1.8f;
            float z = 9.81f + (float) Math.sin(t / 3f) * 0.7f;
            return new float[]{x, y, z};
        }

        if (sensorType == Sensor.TYPE_GRAVITY) {
            float x = (float) Math.sin(t / 8f) * 1.2f;
            float y = (float) Math.cos(t / 8f) * 1.2f;
            float z = 9.81f;
            return new float[]{x, y, z};
        }

        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            float x = (float) Math.sin(t / 3f) * 0.8f;
            float y = (float) Math.cos(t / 4f) * 0.6f;
            float z = (float) Math.sin(t / 5f) * 0.4f;
            return new float[]{x, y, z};
        }

        return new float[]{0f, 0f, 0f};
    }

    public static String directionName(float degree) {
        if (degree >= 337.5 || degree < 22.5) {
            return "Nord";
        } else if (degree < 67.5) {
            return "Nord-Est";
        } else if (degree < 112.5) {
            return "Est";
        } else if (degree < 157.5) {
            return "Sud-Est";
        } else if (degree < 202.5) {
            return "Sud";
        } else if (degree < 247.5) {
            return "Sud-Ouest";
        } else if (degree < 292.5) {
            return "Ouest";
        } else {
            return "Nord-Ouest";
        }
    }
}