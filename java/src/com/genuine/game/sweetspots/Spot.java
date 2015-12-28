package com.genuine.game.sweetspots;

public class Spot {
    int coordX;
    int coordY;
    int value;  // 0=empty, 1=filler, 2=target
    int zoneId;  // -1=undefined

    // Default value to 0
    Spot(int x, int y) {
        this(x, y, 0);
    }

    // Default zone id to -1
    Spot(int x, int y, int v) {
        this(x, y, v, -1);
    }

    Spot(int x, int y, int v, int z) {
        coordX = x;
        coordY = y;
        value = v;
        zoneId = z;
    }

    Spot(Spot s) {  // For spot cloning
        coordX = s.coordX;
        coordY = s.coordY;
        value = s.value;
        zoneId = s.zoneId;
    }

    void printXYV() {
        System.out.print("(" + coordX +"," + coordY + "v" + value + ") ");
    }

    void printXYZ() {
        System.out.print("(" + coordX +"," + coordY + "z" + zoneId + ") ");
    }
}
