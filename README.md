## Sweet Spots - an Android board game

---

The Git repo contains source code for two separate independent applications:

1. An Android application for playing Sweet Spots
2. A Java application for solving and creating Sweet Spots games

### /sweetspots/android/ - Android application for playing Sweet Spots

This is an Android application initially developed in Summer 2013 as a mobile programming exercise, then refactored in Winter 2015 for publishing on Google Play.  The underlying game-playing logic follows an interesting board game called Alberi that is based on some mathematical puzzle designed by Giorgio Dendi.

The game consists of a square board composed of N rows x N columns of cells.  The board is also partitioned into N contiguous colored-zones.  The goal is to distribute a number of treasure chests into the area with the following rules:

1. Each role must have 1 treasure chest
2. Each column must have 1 treasure chest
3. Each zone must have 1 treasure chest
4. No treasure chests can be adjacent row-wise, column-wise or diagonally, to each other
5. There is also a variant of 2 treasure chests per row/column/zone

The code base of the Android application was repurposed from a sample app, Tic-tac-toe, that came with the Android SDK.  The Eclipse-based code has a good portion of it organized as a separate dependent project from the main project.

This application was initially developed using an old version of Eclipse (Juno) and Android SDK (17.0.0) and only minimal effort has been made to make it build-able using more contemporary versions (Eclipse Mars and Android SDK 24.0.2).

The application published on Google Play can be downloaded/installed from the following link:

https://play.google.com/store/apps/details?id=com.genuine.android.sweetspots

For a high-level walk-through of the board game application, there are a couple of blog posts at:

http://blog.genuine.com/2015/12/an-android-board-game-sweet-spots/

---

### /sweetspots/java/ - Java application for solving and creating Sweet Spots games

The games included in the Sweet Spots Android application were created using an independent Java application.  For each game, it automatically generates random colored-zones on the board and validates the game for a solution based on the rule requirement.  If no solution is found, it'll keep re-generating another random zone partitioning on the board till it finds a solution.

This Java application was also developed in Summer 2013 and refactored in Winter 2015, using the good old Ant as the build tool.

##### A. Command for creating a game:

*java -cp <classPath> com.genuine.game.sweetspots.CreateGame <trace?1:0> <boardZoneFile> <boardSize> <targetsPerRow> <targetsPerCol> <targetsPerZone>*

For instance, to create a game with 4x4 board size, go to /path/to/sweetspot/java/ and run the following command to generate the game zones in ./gamefiles/:

*java -cp build/sweetspots-r1.0.0.jar com.genuine.game.sweetspots.CreateGame 0 ./gamefiles/game-4-1.txt 4 1 1 1*

The generated game-zone file should look something like the following:

```
0 1 1 2
0 0 1 2
2 2 2 2
2 3 3 2
```

##### B. Command for solving a game:

*java -cp <classPath> com.genuine.game.sweetspots.SolveGame <trace?1:0> <boardZoneFile> <boardSize> <targetsPerRow> <targetsPerCol> <targetsPerZone>*

For instance, to solve the game created above, run the following command from /path/to/sweetspot/java/:

*java -cp build/sweetspots-r1.0.0.jar com.genuine.game.sweetspots.SolveGame 0 ./gamefiles/game-4-1.txt 4 1 1 1*

---
