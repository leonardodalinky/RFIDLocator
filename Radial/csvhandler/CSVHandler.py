from typing import List

from data.AlignTagDatas import AlignTagDatas
from data.TagData import TagData
from utils.Utils import *
from queue import Queue



def getTagDatas(filepath: str) -> List[TagData]:
    file = open(filepath, 'r')
    lines = file.readlines()
    ansList = []
    for line in lines:
        strs = line.split(',')
        if not is_number(strs[0]):
            continue
        elif len(strs) != 8:
            continue
        tagdata: TagData = TagData(int(strs[0]), float(strs[1]), int(strs[2]), float(strs[3]),
                                   float(strs[4]), float(strs[5]), int(strs[6]), strs[7])
        ansList.append(tagdata)
    file.close()
    return ansList


def getAlignTagDatas(filepath: str) -> List[AlignTagDatas]:
    rawData: List[TagData] = getTagDatas(filepath)
    ansList: List[AlignTagDatas] = []
    dataBuf: List[TagData] = [None] * 8
    lastDatas: AlignTagDatas = None
    dataCnt: int = 0
    for tagData in rawData:
        if dataCnt >= 8:
            dataBuf.pop(0)
            dataBuf.append(tagData)
        if dataCnt < 8:
            dataBuf[dataCnt] = tagData
            dataCnt += 1
        if dataCnt < 8:
            continue
        isValidBuf: bool = True
        for i in range(8):
            if dataBuf[i].antennaId != int(i / 2):
                isValidBuf = False
        if not isValidBuf:
            continue

        ansList.append(calculateAlign(dataBuf, lastDatas))
        lastDatas = ansList[len(ansList) - 1]

    return ansList


def calculateAlign(dataBuf: List[TagData], lastDatas: AlignTagDatas) -> AlignTagDatas:
    ret: AlignTagDatas = AlignTagDatas()
    times: List[float] = [0] * 4
    for i in range(ANTENNA_SIZE):
        times[i] = (dataBuf[2 * i].time + dataBuf[2 * i + 1].time) / 2.0
        ret.rssis[i] = (dataBuf[2 * i].rssi + dataBuf[2 * i + 1].rssi) / 2.0
        ret.phases[i] = phaseAdd(dataBuf[2 * i].phase,
                                 phaseSub(dataBuf[2 * i + 1].phase, dataBuf[2 * i].phase) / 2.0)

    # begin align
    ret.timeAligned = 0
    for t in times:
        ret.timeAligned += t
    ret.timeAligned /= 4.0
    if lastDatas is not None:
        for i in range(ANTENNA_SIZE):
            phaseDiff = phaseSub(ret.phases[i], lastDatas.phases[i])
            rssiDiff = ret.rssis[i] - lastDatas.rssis[i]
            timeDiff = times[i] - lastDatas.timeAligned
            ret.phases[i] = phaseAdd(lastDatas.phases[i], (phaseDiff / timeDiff) * (ret.timeAligned - lastDatas.timeAligned))
            ret.rssis[i] = lastDatas.rssis[i] + (rssiDiff / timeDiff) * (ret.timeAligned - lastDatas.timeAligned)

    return ret
