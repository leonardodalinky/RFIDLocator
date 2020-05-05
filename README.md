# RFID Locator

## Background

This project is launched for implementing the classical algorithm, for example, 'Hyperbola locating' and 'Radial shifting'.

In addition to the source code, some data are included in this project for the purpose of testing the accuracy of each algorithm.

Good works deserve harvests.

## Various Algorithm

### 1. Hyperbola Locating

Referring to the method of "Hyperbola Locating", I implement this technique on my own and make a tracking graph of the accuracy. 

All my core logical work is done in **Java**, while the visual UI is written in **Python** with matplotlib. 

The results turn into:

![H.L. Tracking Graph](https://github.com/leonardodalinky/RFIDLocator/HyperbolaLocator/pythonUI/Hyperbola.png)

The circle in black is the actual track of the stuff, while the scatterplot shows the overall prediction of the track.

### 2. Radial Shifting

Assume that the initial location is already known, the method of "Radial Shifting" performs better than "Hyperbola Locating" in terms of tracking. 

In this case, the entire code is written in **Python**.

To improve the smoothness of the track, "**Kalman Filter**" is adopted in this method.

The results turn into:

![R.S. Tracking Graph](https://github.com/leonardodalinky/RFIDLocator/RadialShifting/track_radial_kalman.gif)

The circle in black is the actual track of the stuff, while the scatterplot shows the overall prediction of the track.

### 3. To be continued...