package com.genuine.game.sweetspots;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.LinkedList;

public class SweetSpots {

    LinkedList<Board> boardStack;
    boolean traceOn;
    int trialCount;
    int rollbackCount;

    SweetSpots(boolean traceOn) {
            boardStack = new LinkedList<Board>();
            traceOn = traceOn;
            trialCount = 0;
            rollbackCount = 0;
    }

    int boardwalk(Board board, int startX, int startY) {  // -1=failed, 1=succeeded
        int i, j, zId;
        int size = board.size;
        int targetsPerRow = board.targetsPerRow;
        int targetsPerCol = board.targetsPerCol;
        int targetsPerZone = board.targetsPerZone;

        i = startX;
        j = startY;

        while (j < size) {
            zId = board.spots[i][j].zoneId;

            if (board.spots[i][j].value == 0) {  // current spot is empty

                if (board.getTargetCountInRow(j) < targetsPerRow
                    && board.getTargetCountInCol(i) < targetsPerCol
                    && board.getTargetCountInZone(zId) < targetsPerZone) {

                    if (board.noTargetsAround(board.spots[i][j])) {
                        // Push a snapshot of current board to the stack
                        Board boadSnapshot = new Board(board);
                        boardStack.push(boadSnapshot);

                        trialCount++;

                        if (traceOn) {
                            System.out.println("\nINFO: boardwalk() - Current board pushed into stack - Board spot values:");
                            board.printSpotValues();
                        }

                        // Put a target in the current spot
                        board.spots[i][j].value = 2;

                        // Check target counts in associated row, column and zone, and conditionally fill empty spots
                        // and adjacent spots with filler
                        board.checkAndFillSpots(board.spots[i][j], 1); 
                        board.fillEmptyAdjSpots(board.spots[i][j], 1);
                    }
                    else {
                        // Put filler in the current spot
                        board.spots[i][j].value = 1;
                    }
                }

                if (!board.allRowsValid() || !board.allColsValid() || !board.allZonesValid()) {
                    if (traceOn) {
                        System.out.println("\nINFO: boardwalk() (" + i + "," + j + "v" + board.spots[i][j].value + ") - Board invalid.");
                    }
                    return -1;
                }
            }
            else {  // current spot is not empty
                if (j == size) {
                    return 1;
                }
            }

            i++;
            if (i == size) {
                i = 0;
                j++;
            }
        }

        if (!board.allRowsValid() || !board.allColsValid() || !board.allZonesValid()) {
            if (traceOn) {
                System.out.println("\nINFO: End of boardwalk - Board invalid.");
            }
            return -1;
        }
        else
            return 1;
    }

    Board rollback() {
        if (boardStack.isEmpty()) {  // Should never happen
            if (traceOn) {
                System.out.println("\nERROR: rollback() - Stack is empty!");
            }
            return null;
        }
        else {
            // Pop the most recent board snapshot from the stack
            Board board = boardStack.pop();
            rollbackCount++;

            if (traceOn) {
                System.out.println("\nINFO: rollback() - Most recent board popped from stack - Board spot values:");
                board.printSpotValues();
            }

            int i = 0, j = 0;
            int size = board.size;

            // Locate the first empty spot
            while (j < size) {
                if (board.spots[i][j].value == 0)
                    break;
                i++;
                if (i == size) {
                    i = 0;
                    j++;
                }
            }
            if (j == size) {  // Should never happen
                if (traceOn) {
                    System.out.println("\nERROR: rollback() - No empty spot found!");
                }
                return null;
            }
            else {
                // Put filler in the spot
                board.spots[i][j].value = 1;

                return board;
            }
        }
    }
}
