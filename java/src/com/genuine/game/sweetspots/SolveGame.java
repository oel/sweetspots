package com.genuine.game.sweetspots;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.LinkedList;

public class SolveGame {

    static boolean traceOn;

    public static void main(String[] args) {
        if (args.length != 6  || !args[0].matches("\\d+")
            || !args[2].matches("\\d+") || !args[3].matches("\\d+")
            || !args[4].matches("\\d+") || !args[5].matches("\\d+")) {

            System.out.println("Usage: java -cp <classPath> com.genuine.game.sweetspots.SolveGame <trace?1:0> "
                               + "<boardZoneFile> <boardSize> <targetsPerRow> <targetsPerCol> <targetsPerZone>");
            System.out.println(" e.g.: java -cp build/sweetspots-r1.0.0.jar com.genuine.game.sweetspots.SolveGame 0 "
                               + "gamefiles/boardzones.400-1.txt 4 1 1 1");
            System.exit(0);
        }

        int trace = Integer.parseInt(args[0]);
        String boardZoneFile = args[1];
        int boardSize = Integer.parseInt(args[2]);
        int targetsPerRow = Integer.parseInt(args[3]);
        int targetsPerCol = Integer.parseInt(args[4]);
        int targetsPerZone = Integer.parseInt(args[5]);

        int[][] boardZones = new int[boardSize][boardSize];

        traceOn = (trace != 0) ? true : false;

        /*
          e.g. File boardzones.txt:
            0 1 1 2
            0 0 1 2
            2 2 2 2
            2 3 3 2
          i.e.
            zone 0: (0,0), (0,1), (1,1)
            zone 1: (1,0), (2,0), (2,1)
            zone 2: (3,0), (3,1), (0,2), (1,2), (2,2), (3,2), (0,3), (3,3)
            zone 3: (1,3), (2,3)
        */

        try {
            Scanner scanner = new Scanner(new FileInputStream(boardZoneFile));
            int i = 0, j = 0;
            while (scanner.hasNextInt() && j < boardSize) {
                boardZones[i][j] = scanner.nextInt();
                i++;
                if (i == boardSize) {
                    i = 0;
                    j++;
                }
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("\nERROR: main() - File not found!\n" + e.getMessage());
            System.exit(0);
        }

        Board game = new Board(boardSize, targetsPerRow, targetsPerCol, targetsPerZone);
        game.initializeSpots(boardZones);

        System.out.println("\nINFO: main() - Board zones (from boardzones file):");
        game.printBoardZones(boardZones);

        System.out.println("\nINFO: main() - Board spots by Rows/Columns:");
        game.printSpotValues();

        System.out.println("\nINFO: main() - Board spots by Zones:");
        game.printByZones();

        SweetSpots ss = new SweetSpots(traceOn);

        int result = 0;
        long startTime = System.currentTimeMillis();

        while ((result = ss.boardwalk(game, 0, 0)) != 1) {

            if (traceOn) {
                System.out.println("\nINFO: main() - Before rollback() - Board spot values:");
                game.printSpotValues();
            }

            game = ss.rollback();

            if (game == null) {
                System.out.println("\nERROR: main() - rollback() returns NULL board!");
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nINFO: main() - Processing time: " + (endTime - startTime) + " ms");
        System.out.println("\nINFO: main() - Trial count: " + ss.trialCount);
        System.out.println("\nINFO: main() - Rollback count: " + ss.rollbackCount);

        if (game != null) {
            System.out.println("\nINFO: main() - Final boardwalk result: " + result);
            System.out.println("\nINFO: main() - Final board spot values:");
            game.printSpotValues();
        }
        else {
            System.out.println("\nERROR: main() - Final board spot values - NULL board!");
        }
    }
}
