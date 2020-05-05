from typing import List
from data.Point import Point
from data.PointT import PointT
from utils.Utils import *
from csvhandler.CSVHandler import ANTENNA_SIZE
from data.AlignTagDatas import AlignTagDatas
import math
from utils.KMFilter import *


class Tracker:
    # 用于卡尔曼滤波
    H: np.matrix = np.mat(np.identity(4))
    Q: np.matrix = np.mat(np.identity(4)) * 0.01
    R: np.matrix = np.mat(np.identity(4)) * 0.01

    def __init__(self, initLocation: Point, antennaLocations: List[Point], channel: float):
        # 空间轨迹和其时间
        self.track: List[PointT] = []
        # 轨迹相位
        self.trackPhase: List[List[float]] = []
        # 轨迹信号强度
        self.trackRssi: List[List[float]] = []
        # 轨迹的三边距离
        self.trackDistance: List[List[float]] = []
        # 频率
        self.channel: float
        # 天线位置
        self.antennaLocations: List[Point] = []

        self.track.append(PointT(initLocation.x, initLocation.y, 0))

        # 初始化卡尔曼滤波的初始协方差
        self.P = np.mat(np.identity(4)) * 0.1
        # 初始化卡尔曼滤波的初始状态
        self.loc = np.mat(np.array([initLocation.x, 0, initLocation.y, 0]).reshape((4, 1)))

        distance: List[float] = [0] * 4
        for i in range(4):
            distance[i] = Point.distance(initLocation, antennaLocations[i])
        self.trackDistance.append(distance)
        self.channel = channel
        self.antennaLocations = antennaLocations

    def addAlignData(self, alignData: List[AlignTagDatas]) -> None:
        for data in alignData:
            self._addNewPoint(data.timeAligned, data.phases, data.rssis)

    def _addNewPoint(self, time: float, phases: List[float], rssis: List[float]) -> PointT:
        if len(self.trackPhase) == 0:
            self.trackPhase.append(phases)
            self.trackRssi.append(rssis)
            return None
        else:
            distance: List[float] = [0] * 4
            lastDistance: List[float] = self.trackDistance[len(self.trackDistance) - 1]
            lastPhase: List[float] = self.trackPhase[len(self.trackPhase) - 1]

            for i in range(ANTENNA_SIZE):
                radialMove: float = self._phaseSubToDistance(phases[i], lastPhase[i], self.channel)
                # 注意radialMove的符号
                distance[i] = lastDistance[i] - radialMove

            self.trackDistance.append(distance)
            self.trackPhase.append(phases)
            self.trackRssi.append(rssis)

            lastPoint: PointT = self.track[len(self.track) - 1]
            temp: Point = solveLocation(distance, self.antennaLocations, lastPoint)
            newLocation: PointT = None
            if temp is not None:
                newLocation = PointT(temp.x, temp.y, time)
                self.track.append(newLocation)

            # 卡尔曼滤波
            dt = newLocation.time - lastPoint.time
            tempList = [newLocation.x, (newLocation.x - lastPoint.x) / dt,
                        newLocation.y, (newLocation.y - lastPoint.y) / dt]
            newloc = np.mat(np.asarray(tempList).reshape((4, 1)))
            A: np.matrix = np.mat(np.identity(4))
            A[0, 1] = A[2, 3] = dt
            newloc, self.P = KM(self.loc, None, newloc, A, None, self.P, self.H, self.Q, self.R)
            self.loc = newloc
            newLocation.x = newloc[0, 0]
            newLocation.y = newloc[2, 0]

            return newLocation

    @staticmethod
    def _phaseSubToDistance(phase: float, lastPhase: float, channel: float) -> float:
        wavel: float = LIGHT_SPEED / channel / 1000000
        if math.fabs(lastPhase - phase) < math.pi:
            return ((phase - lastPhase) / (4 * math.pi)) * wavel
        elif lastPhase - phase >= math.pi:
            return ((2 * math.pi - lastPhase + phase) / (4 * math.pi)) * wavel
        else:
            return ((phase - lastPhase - 2 * math.pi) / (4 * math.pi)) * wavel
