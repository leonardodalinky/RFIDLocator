from data.Point import Point


class PointT(Point):
    def __init__(self, x: float, y: float, t: float):
        super(PointT, self).__init__(x, y)
        self.time = t
