from typing import List
from data.PointT import PointT
import numpy as np
import matplotlib
import  matplotlib.pyplot as plt
import matplotlib.animation as animation


def drawCircle(x, y, r, color='k', count=1000):
    xarr=[]
    yarr=[]
    for i in range(count):
        j = float(i)/count * 2 * np.pi
        xarr.append(x+r*np.cos(j))
        yarr.append(y+r*np.sin(j))
    plt.plot(xarr, yarr, c=color)


def update_points(num, *args):
    '''
    更新数据点
    '''
    point_ani = args[0]
    data = args[1]
    point_ani.set_data(data[num].x, data[num].y)
    return point_ani,


def show(data: List[PointT]) -> None:
    fig = plt.gcf()
    point_ani, = plt.plot(data[0].x, data[0].y, "ro")
    plt.grid(ls="--")
    ani = animation.FuncAnimation(fig, update_points, frames=np.arange(0, len(data)), fargs=(point_ani, data),
                                  interval=100, blit=True)
    ani.save('track_radial.gif', writer='imagemagick', fps=10)
    plt.show()