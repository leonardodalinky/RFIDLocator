import sys
from show import animation
from csvhandler import CSVHandler
from locator.Tracker import Tracker
from data.Point import Point
from data.PointT import PointT

antennaLocation = [Point(1.14, 0), Point(0, 0), Point(0, 1.14), Point(1.14, 1.14)]
initialPos = Point(0.1803, 0.9597)
channel = 920.625

def main():
    if len(sys.argv) <= 1:
        raise SystemError("Please input valid filepath.")
    data = CSVHandler.getAlignTagDatas(sys.argv[1])
    # f = open("log", 'w')
    # for d in data:
    #     f.write(str(d.timeAligned) + ',')
    #     for i in range(4):
    #         f.write(str(d.phases[i]) + ',')
    #     f.write('\n')
    # f.close()
    tracker = Tracker(initialPos, antennaLocation, channel)
    tracker.addAlignData(data)
    animation.drawCircle(0.57, 0.57, 0.54)
    animation.show(tracker.track)


if __name__ == '__main__':
    main()