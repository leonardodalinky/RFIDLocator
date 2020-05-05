# -*- coding: utf-8 -*-
import matplotlib.pyplot as plt
import numpy as np
import sys

class PointT:
    def __init__(self, t, x, y):
        self.x = x
        self.y = y
        self.t = t


def loadPoints(filePath):
    file = open(filePath, 'r')
    lines = file.readlines()
    ansList = []
    for line in lines:
        strs = line.split(',')
        point = PointT(int(strs[0]), float(strs[1]), float(strs[2]))
        ansList.append(point)
    file.close()
    return ansList



def circle(x, y, r, color='k', count=1000):
    xarr=[]
    yarr=[]
    for i in range(count):
        j = float(i)/count * 2 * np.pi
        xarr.append(x+r*np.cos(j))
        yarr.append(y+r*np.sin(j))
    plt.plot(xarr,yarr,c=color)


def main():
    points = loadPoints(sys.argv[1])
    # paint the 2-dimension scatter map
    fig, ax = plt.subplots()
    if len(sys.argv) <= 2:
        plt.title(u'Location Scatter Map')
    else:
        plt.title(sys.argv[2])
    plt.xlabel(u'x')
    plt.ylabel(u'y')
    for p in points:
        plt.scatter(p.x, p.y)
    # paint the center circle
    circle(0, 0.93, 0.53)
    plt.scatter(0, 0.93, s=3, c='k')
    plt.show()


# entry point
if __name__ == "__main__":
    main()