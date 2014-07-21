package com.allsrc.monstertrucks;

public final class MonsterUtils {
    static public float map(float value, float low1, float high1, float low2, float high2) {
        return low2 + (high2 - low2) * (value - low1) / (high1 - low1);
    }

    static public float map(int value, float low1, float high1, float low2, float high2) {
        return low2 + (high2 - low2) * (value - low1) / (high1 - low1);
    }
}