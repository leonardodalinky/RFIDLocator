import math


class Point:
    def __init__(self, x: float, y: float):
        self.x = x
        self.y = y

    @staticmethod
    def distance(p1, p2) -> float:
        p1: Point
        p2: Point
        t1 = p1.x - p2.x
        t2 = p1.y - p2.y
        return math.sqrt(t1 * t1 + t2 * t2)

