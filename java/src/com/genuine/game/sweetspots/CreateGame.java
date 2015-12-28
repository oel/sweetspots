package com.genuine.game.sweetspots;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.LinkedList;

public class CreateGame {

    static boolean traceOn;
    static int[][] boardZones;
    static int boardSize;
    static final int undefined = -1;

    // Initialize zone id to undefined
    static void initBoardZones() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                boardZones[i][j] = undefined;
            }
        }
    }

    // For initial testing (Not being used)
    static int linearRandom(int lowerNum, int upperNum) {
        int randomNum;
        Random random = new Random();
        randomNum = lowerNum + random.nextInt(upperNum - lowerNum);
        return randomNum;
    }

    static int weightedRandom(int lowerNum, int upperNum) {
        double rand = Math.random();
        double[] probDist;

        probDist = triangularProbDist(lowerNum, upperNum);

        int offset = 0;
        while (rand > probDist[offset])
            offset++;

        return lowerNum + offset;
    }

    // Triangular probability distribution (discrete)
    static double[] triangularProbDist(int lowerNum, int upperNum) {
        int numCount = upperNum - lowerNum + 1;
        int halfCount = numCount / 2;
        boolean isEvenCount = (numCount % 2 == 0);
        double[] probDist = new double[numCount];

        for (int i = 0; i <= halfCount; i++) {
            probDist[i] = i + 1;
            if (i < halfCount || isEvenCount) {
                probDist[numCount - i - 1] = probDist[i];
            }
        }

        int sum = 0;
        for (int i = 0; i < numCount; i++) {  // Change to cumulative distrubution
            sum += probDist[i];
            probDist[i] = sum;
        }

        for (int i = 0; i < numCount; i++) {  // Normalize the distrubution
            probDist[i] = probDist[i] / sum;
        }

        return probDist;
    }

    // For testing only
    static void testWeightedRandom(int testCount, int lowerNum, int upperNum) {
        int numCount = upperNum - lowerNum + 1;
        int resultZWalkNum;
        int[] resultZWalkCount = new int[numCount];

        System.out.println("\nINFO: testWeightedRandom() - (" + lowerNum + " .. " + upperNum + ") testing " + testCount + " times ...");
        for (int i = 0; i < testCount; i++) {
            resultZWalkNum = weightedRandom(lowerNum, upperNum);
            resultZWalkCount[resultZWalkNum - lowerNum]++;
        }

        for (int i = 0; i < numCount; i++) {
            System.out.println("\nINFO: testWeightedRandom() - Count of " + (lowerNum + i) + ": " + resultZWalkCount[i]);
        }
    }

    // Generate random sizes for all zones
    static int[] genZoneSizes() {

        int[] zoneSizes = new int[boardSize];

        int minZoneSize = 3;
        int avgZoneSize = (minZoneSize + boardSize) / 2;  // Average of minimum and actual average
        int availZoneSize = boardSize * boardSize;
        int maxZoneSize;
        int currZoneSize;

        // testWeightedRandom(10000, 3, 9);  // For testing only

        for (int i = 0; i < boardSize - 1; i++) {
            maxZoneSize = availZoneSize - (avgZoneSize * (boardSize - i - 1));
            currZoneSize = weightedRandom(minZoneSize, maxZoneSize);
            zoneSizes[i] = currZoneSize;
            availZoneSize -= currZoneSize;
        }
        zoneSizes[boardSize - 1] = availZoneSize;

        for (int i = 0; i < boardSize; i++) {
            if (traceOn) {
                System.out.println("\nINFO: genZoneSizes() - Size of zone " + i + ": " + zoneSizes[i]);
            }
        }

        return zoneSizes;
    }

    static void transposeBoard() {
        int zIdHolder;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < i; j++) {
                zIdHolder = boardZones[i][j];
                boardZones[i][j] = boardZones[j][i];
                boardZones[j][i] = zIdHolder;
            }
        }
    }

    static int zoneWalk(int zId, int zSize) {  // -1=failed, 1=succeeded
        int i = 0, j = 0;
        int currZSize = 0;
        boolean towardsRight;

        // Locate first available spot by row by col
        towardsRight = (j % 2 == 0);
        while (boardZones[i][j] != undefined) {
            i = towardsRight ? i+1 : i-1;
            
            if ((towardsRight && i > boardSize-1) || (!towardsRight && i < 0)) {
                i = towardsRight ? i-1 : i+1;  // Step back inside the border
                j++;
                if (j == boardSize) {
                    return -1;  // No available spot
                }
                towardsRight = (j % 2 == 0);
            }
        }

        towardsRight = (j % 2 == 0);
        int colHolder = i;  // Save the column index
        int k;

        while (currZSize < zSize && boardZones[i][j] == undefined) {
            boardZones[i][j] = zId;
            currZSize++;
            if (currZSize == zSize)
                return 1;

            i = towardsRight ? i+1 : i-1;

            if ((towardsRight && i > boardSize-1) || (!towardsRight && i < 0) || (boardZones[i][j] != undefined)) {
                i = towardsRight ? i-1 : i+1;  // Step back

                // Check and fill available spots in the same row on opposite side of the previous row-change column index
                if (!(towardsRight && colHolder == 0) && !(!towardsRight && colHolder == boardSize-1)) {

                    k = towardsRight ? colHolder-1 : colHolder+1;  // Next spot in oppositive direction

                    while (currZSize < zSize && boardZones[k][j] == undefined) {
                        boardZones[k][j] = zId;
                        currZSize++;
                        if (currZSize == zSize)
                            return 1;
                        k = towardsRight ? k-1 : k+1;  // In opposite direction
                        if ((towardsRight && k < 0) || (!towardsRight && k > boardSize-1))
                            break;
                    }
                }

                colHolder = i;  // Save the column index
                j++;
                if (j == boardSize)
                    return -1;
                towardsRight = (j % 2 == 0);
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        if (args.length != 6  || !args[0].matches("\\d+")
            || !args[2].matches("\\d+") || !args[3].matches("\\d+")
            || !args[4].matches("\\d+") || !args[5].matches("\\d+")) {

            System.out.println("Usage: java -cp <classPath> com.genuine.game.sweetspots.CreateGame <trace?1:0> "
                               + "<boardZoneFile> <boardSize> <targetsPerRow> <targetsPerCol> <targetsPerZone>");
            System.out.println(" e.g.: java -cp build/sweetspots-r1.0.0.jar com.genuine.game.sweetspots.CreateGame 0 "
                               + "gamefiles/testbzones.400-1.txt 4 1 1 1");
            System.exit(0);
        }

        int trace = Integer.parseInt(args[0]);
        String boardZoneFile = args[1];
        int size = Integer.parseInt(args[2]);
        int targetsPerRow = Integer.parseInt(args[3]);
        int targetsPerCol = Integer.parseInt(args[4]);
        int targetsPerZone = Integer.parseInt(args[5]);

        traceOn = (trace != 0) ? true : false;
        boardSize = size;

        Board game = null;

        int grandTrials = 0;
        boolean doneTrials = false;
        long startTime = System.currentTimeMillis();

        while (!doneTrials) {

            // Create board zones BEGIN
            boardZones = new int[boardSize][boardSize];

            int[] zoneSizes;
            int resultZWalk;
            boolean doneZWalk = false;

            while (!doneZWalk) {
                zoneSizes = genZoneSizes();

                initBoardZones();
                doneZWalk = true;

                for (int i = 0; i < boardSize; i++) {
                    resultZWalk = zoneWalk(i, zoneSizes[i]);

                    if (traceOn) {
                        System.out.println("\nINFO: main() - Called zoneWalk(" + i + "," + zoneSizes[i] + ") ... resultZWalk = " + resultZWalk);
                    }

                    if (resultZWalk == -1) {
                        if (traceOn) {
                            System.out.println("\nERROR: main() - Zone " + i + " in zoneWalk() failed! Re-generating all zones ...");
                        }
                        doneZWalk = false;
                        break;
                    }
                    transposeBoard();
                }
            }  // END while (!doneZWalk)
            // Create board zones END

            // Check if game solvable BEGIN
            game = new Board(boardSize, targetsPerRow, targetsPerCol, targetsPerZone);
            game.initializeSpots(boardZones);

            SweetSpots ss = new SweetSpots(traceOn);

            int resultBWalk = 0;

            while ((resultBWalk = ss.boardwalk(game, 0, 0)) != 1) {

                if (ss.traceOn) {
                    System.out.println("\nINFO: main() - Before rollback() - Board spot values:");
                    game.printSpotValues();
                }

                game = ss.rollback();

                if (game == null) {
                    if (ss.traceOn) {
                        System.out.println("\nERROR: main() - rollback() returns NULL board!");
                    }
                    break;
                }
            }
            // Check if game solvable END

            grandTrials++;

            doneTrials = (game != null);

        }  // END while (!doneTrials)

        long endTime = System.currentTimeMillis();
        System.out.println("\nINFO: main() - Processing time: " + (endTime - startTime) + " ms");
        System.out.println("\nINFO: main() - Trial count: " + grandTrials);

        System.out.println("\nINFO: main() - Board zones:");
        for (int j = 0; j < boardSize; j++) {
            for (int i = 0; i < boardSize; i++) {
                System.out.print(boardZones[i][j] + " ");
            }
            System.out.println();
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(boardZoneFile)));

            int i = 0, j = 0;
            while (j < boardSize) {
                writer.write(Integer.toString(boardZones[i][j]) + " ");
                i++;
                if (i == boardSize) {
                    writer.newLine();
                    i = 0;
                    j++;
                }
            }
            writer.close();
            System.out.println("\nINFO: main() - Board zones written to: " + boardZoneFile);
        }
        catch (IOException e) {
            System.out.println("\nERROR: main() - I/O access problem!\n" + e.getMessage());
            System.exit(0);
        }

        if (game != null) {
            System.out.println("\nINFO: main() - Final board spot values:");
            // game.printSpotValues();
            for (int j = 0; j < boardSize; j++) {
                for (int i = 0; i < boardSize; i++) {
                    System.out.print(game.spots[i][j].value + " ");
                }
                System.out.println();
            }
        }
        else {
            System.out.println("\nERROR: main() - Final board spot values - NULL board!");
        }
    }
}
