class TagData:
    def __init__(self, Id: int, time: float, antennaId: int, rssi: float, phase: float, channel: float, channelNum: int,
                 epc: str):
        self.Id = Id
        self.time = time
        self.antennaId = antennaId
        self.rssi = rssi
        self.phase = phase
        self.channel = channel
        self.channelNum = channelNum
        self.epc = epc
