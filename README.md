Sweet Spots - an Android board game

--

This is an Android application initially developed in Summer 2013 as a mobile programming exercise, then refactored in Winter 2015 for publishing on Google Play.  The underlying game-playing logic follows an interesting board game called Alberi that is based on some mathematical puzzle designed by Giorgio Dendi.

The game consists of a square board composed of N rows x N columes of cells.  The board is also partitioned into N contiguous colored-zones.  The goal is to distribute a number of treasure chests into the area with the following rules:

1. Each role must have 1 treasure chest
2. Each column must have 1 treasure chest
3. Each zone must have 1 treasure chest
4. No treasure chests can be adjacent row-wise, column-wise or diagonally, to each other
5. There is also a variant of 2 treasure chests per row/column/zone

The code base of the Android application was repurposed from a sample app, Tic-tac-toe, that came with the Android SDK.  The Eclipse-based code has a good portion of it organized as a separate dependent project from the main project.

This application was initially developed using an old version of Eclipse (Juno) and Android SDK (17.0.0) and only minimal effort has been made to make it build-able using more contemporary versions (e.g. Eclipse Mars and Android SDK 24.0.2.

The application published on Google Play can be downloaded/installed from the following link:

https://play.google.com/store/apps/details?id=com.genuine.android.sweetspots

--
