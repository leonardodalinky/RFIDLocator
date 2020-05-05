import numpy as np
import numpy.matlib as mat
from typing import List
from data.Point import Point
import math

# constants
ITER_TIMES = 200
PI2: float = 6.283185307179586476925286766559
ANTENNA_SIZE: int = 4
LIGHT_SPEED = 300000000


def is_number(s: str) -> bool:
    try:
        float(s)
        return True
    except ValueError:
        pass

    try:
        import unicodedata
        unicodedata.numeric(s)
        return True
    except (TypeError, ValueError):
        pass
    return False


def phaseAdd(n1: float, n2: float) -> float:
    ret = n1 + n2
    if ret > PI2:
        ret = ret - int(ret / PI2) * PI2
    elif ret < 0:
        ret = ret + (int(-ret / PI2) + 1) * PI2
    return ret


def phaseSub(n1: float, n2: float) -> float:
    if math.fabs(n2 - n1) > 5.8:
        if n1 > n2:
            return n1 - PI2 - n2
        else:
            return n1 + PI2 - n2
    else:
        return n1 - n2


def solveLocation(distance: List[float], antennaLocations: List[Point], iterPoint: Point) -> Point:
    iPoint: Point
    if iterPoint is None:
        iPoint = Point(0.1, 0.1)
    else:
        iPoint = Point(iterPoint.x, iterPoint.y)

    # 迭代求解当前位置
    R = mat.zeros((4, 1))
    J = mat.zeros((4, 2))

    cnt = 1
    while cnt < ITER_TIMES:
        dist: List[float] = [None] * 4
        for i in range(ANTENNA_SIZE):
            dist[i] = Point.distance(iPoint, antennaLocations[i])
            if dist[i] > 300:
                pass
        # 计算残差矩阵
        rawDis = np.asarray(distance)
        # rawDis = np.asarray(np.maximum(rawDis, -rawDis))
        newDis = np.asarray(dist)
        R = np.mat(rawDis.reshape(4, 1)) - np.mat(newDis.reshape(4, 1))

        jarr: List[float] = []
        for i in range(ANTENNA_SIZE):
            jarr.append((iPoint.x - antennaLocations[i].x) / dist[i])
            jarr.append((iPoint.y - antennaLocations[i].y) / dist[i])
        J = np.mat(np.asarray(jarr).reshape(4, 2))

        tempM = J.T @ J
        if not is_invertible(tempM):
            return None
        ret = np.linalg.inv(tempM) @ (J.T @ R)
        iPoint.x += ret[0, 0]
        iPoint.y += ret[1, 0]

        cnt += 1

    return iPoint


def is_invertible(a):
    return a.shape[0] == a.shape[1] and np.linalg.matrix_rank(a) == a.shape[0]


