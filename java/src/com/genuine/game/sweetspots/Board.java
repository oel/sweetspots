package com.genuine.game.sweetspots;

import java.util.HashSet;

public class Board {
    int size;
    int targetsPerRow;
    int targetsPerCol;
    int targetsPerZone;
    Spot[][] spots;

    Board(int boardSize, int rowTargets, int colTargets, int zoneTargets) {
        size = boardSize;
        targetsPerRow = rowTargets;
        targetsPerCol = colTargets;
        targetsPerZone = zoneTargets;

        spots = new Spot[size][size];
    }

    Board(Board b) {  // For board cloning
        size = b.size;
        targetsPerRow = b.targetsPerRow;
        targetsPerCol = b.targetsPerCol;
        targetsPerZone = b.targetsPerZone;

        spots = new Spot[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                spots[i][j] = new Spot(b.spots[i][j]);
            }
        }
    }

    // Initialize spots using boardZones data
    void initializeSpots(int[][] bZones) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int zId = bZones[i][j];
                spots[i][j] = new Spot(i, j, 0, zId);
            }
        }
    }

    // Empty spots (i.e. value = 0) left in the row
    HashSet<Spot> emptySpotsInRow(int rowId) {
        HashSet<Spot> emptySpots = new HashSet<Spot>();
        for (int i = 0; i < size; i++) {
            if (spots[i][rowId].value == 0) {
                emptySpots.add(spots[i][rowId]);
            }
        }
        return emptySpots;
    }

    // Empty spots (i.e. value = 0) left in the column
    HashSet<Spot> emptySpotsInCol(int colId) {
        HashSet<Spot> emptySpots = new HashSet<Spot>();
        for (int i = 0; i < size; i++) {
            if (spots[colId][i].value == 0) {
                emptySpots.add(spots[colId][i]);
            }
        }
        return emptySpots;
    }

    // Empty spots (i.e. value = 0) left in the zone
    HashSet<Spot> emptySpotsInZone(int zId) {
        HashSet<Spot> emptySpots = new HashSet<Spot>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (spots[i][j].zoneId == zId && spots[i][j].value == 0)
                    emptySpots.add(spots[i][j]);
            }
        }
        return emptySpots;
    }

    int getTargetCountInRow(int rowId) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (spots[i][rowId].value == 2) {
                count++;
            }
        }
        return count;
    }

    int getTargetCountInCol(int colId) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (spots[colId][i].value == 2) {
                count++;
            }
        }
        return count;
    }

    int getTargetCountInZone(int zId) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (spots[i][j].zoneId == zId && spots[i][j].value == 2)
                    count++;
            }
        }
        return count;
    }

    boolean isRowValid(int rowId) {
        int currTargetCount = getTargetCountInRow(rowId);
        return ((targetsPerRow - currTargetCount) <= emptySpotsInRow(rowId).size());
    }

    boolean isColValid(int colId) {
        int currTargetCount = getTargetCountInCol(colId);
        return ((targetsPerCol - currTargetCount) <= emptySpotsInCol(colId).size());
     }

    boolean isZoneValid(int zId) {
        int currTargetCount = getTargetCountInZone(zId);
        return ((targetsPerZone - currTargetCount) <= emptySpotsInZone(zId).size());
     }

    boolean allRowsValid() {
        boolean isValid = true;
        for (int i = 0; i < size; i++) {
            if (!isRowValid(i)) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    boolean allColsValid() {
        boolean isValid = true;
        for (int i = 0; i < size; i++) {
            if (!isColValid(i)) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    boolean allZonesValid() {
        boolean isValid = true;
        for (int i = 0; i < size; i++) {
            if (!isZoneValid(i)) {
                isValid = false;
                break;
            }
        }
        return isValid;
    }

    void fillEmptySpotsInRow(int rowId, int v) {
        for (int i = 0; i < size; i++) {
            if (spots[i][rowId].value == 0) {
                spots[i][rowId].value = v;
            }
        }
    }

    void fillEmptySpotsInCol(int colId, int v) {
        for (int i = 0; i < size; i++) {
            if (spots[colId][i].value == 0) {
                spots[colId][i].value = v;
            }
        }
    }

    void fillEmptySpotsInZone(int zId, int v) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (spots[i][j].zoneId == zId && spots[i][j].value == 0)
                    spots[i][j].value = v;
            }
        }
    }

    // Check target counts in associated row, column and zone, and conditionally fill empty spots
    void checkAndFillSpots(Spot spot, int v) {
        int rowId = spot.coordY;
        int colId = spot.coordX;
        int zId = spot.zoneId;
        if (getTargetCountInRow(rowId) == targetsPerRow) {
            fillEmptySpotsInRow(rowId, v);
        }
        if (getTargetCountInCol(colId) == targetsPerCol) {
            fillEmptySpotsInCol(colId, v);
        }
        if (getTargetCountInZone(zId) == targetsPerZone) {
            fillEmptySpotsInZone(zId, v);
        }
    }

    // Fill empty adjacent spots with specific value
    void fillEmptyAdjSpots(Spot spot, int v) {
        int x = spot.coordX;
        int y = spot.coordY;
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if ((i >= 0 && i < size) && (j >= 0 && j < size) && !(i == x && j == y)) {
                    if (spots[i][j].value == 0) {
                        spots[i][j].value = v;
                    }
                }
            }
        }
    }

    boolean noTargetsAround(Spot spot) {
        boolean noAdjTargets = true;
        int x = spot.coordX;
        int y = spot.coordY;
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if ((i >= 0 && i < size) && (j >= 0 && j < size) && !(i == x && j == y)) {
                    if (spots[i][j].value == 2) {
                        noAdjTargets = false;
                        break;
                    }
                }
            }
        }
        return noAdjTargets;
    }

    void printBoardZones(int[][] bZones) {
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                System.out.print(bZones[i][j] + " ");
            }
            System.out.println();
        }
    }

    void printSpotValues() {
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                spots[i][j].printXYV();
            }
            System.out.println();
        }
    }

    void printSpotZones() {
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                spots[i][j].printXYZ();
            }
            System.out.println();
        }
    }

    void printByZones() {
        for (int k = 0; k < size; k++) {
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < size; i++) {
                    if (spots[i][j].zoneId == k)
                        spots[i][j].printXYV();
                }
            }
            System.out.println();
        }
    }
}
