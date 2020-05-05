package csvparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import csvreader.CsvReader;
import csvreader.CsvReaderException;
import data.AlignTagDatas;
import data.MathUtils;
import data.TagData;

public class CsvParser {
	public static final int ANTENNA_SIZE = 4;
	
	public static ArrayList<TagData> getTagDatas(String filePath) throws CsvReaderException, IOException {
		CsvReader reader = new CsvReader(filePath);
		ArrayList<TagData> ret = new ArrayList<TagData>(reader.getRowSize());
		for (int i = 1;i < reader.getRowSize();++i) {
			ArrayList<String> strs = reader.getRow(i);
			if (strs.size() == 0) continue;
			TagData tagData = new TagData();
			tagData.setId(Integer.valueOf(strs.get(0)));
			tagData.setTime(Long.valueOf(strs.get(1)));
			tagData.setAntennaId(Integer.valueOf(strs.get(2)));
			tagData.setRssi(Double.valueOf(strs.get(3)));
			tagData.setPhase(Double.valueOf(strs.get(4)));
			tagData.setChannel(Double.valueOf(strs.get(5)));
			tagData.setChannelNum(Integer.valueOf(strs.get(6)));
			tagData.setEpc(strs.get(7));
			ret.add(tagData);
		}
		return ret;
	}
	
	public static ArrayList<AlignTagDatas> getAlignTagDatas(String filePath) throws CsvReaderException, IOException {
		ArrayList<AlignTagDatas> ret = new ArrayList<AlignTagDatas>();
		ArrayList<TagData> rawData = getTagDatas(filePath);
		TagData[] dataBuffer = new TagData[8];
		// record whether the stack is full
		int flag = 0;
		int prevNum = -1;
		AlignTagDatas lastDatas = null;
		for (TagData tagData : rawData) {
			int nowAntennaId = tagData.getAntennaId();
			if (prevNum != -1 && prevNum != nowAntennaId) {
				// delete some non-consecutive TagData
				dataBuffer[2 * prevNum] = null;
				dataBuffer[2 * prevNum + 1] = null;
				prevNum = -1;
			}
			else if (prevNum == nowAntennaId) {
				dataBuffer[2 * nowAntennaId + 1] = tagData;
				flag |= (1 << nowAntennaId);
				prevNum = -1;
			}
			
			if ((flag & (1 << nowAntennaId)) != 0) {
				// already have this antenna in buffer
				dataBuffer[2 * nowAntennaId] = dataBuffer[2 * nowAntennaId + 1];
				dataBuffer[2 * nowAntennaId + 1] = tagData;
				prevNum = -1;
			}
			else {
				// not have this antenna in buffer
				dataBuffer[2 * nowAntennaId] = tagData;
				prevNum = nowAntennaId;
			}
			
			
			if (flag == 0xf) {
				// calculate alignData
				AlignTagDatas alignTagDatas = calculateAlign(dataBuffer, lastDatas);
				lastDatas = alignTagDatas;
				ret.add(alignTagDatas);
				Arrays.fill(dataBuffer, null);
				flag = 0;
				prevNum = -1;
			}
		}
		
		return ret;
	}
	
	private static AlignTagDatas calculateAlign(TagData[] dataBuffer, AlignTagDatas lastDatas) {
		AlignTagDatas ret = new AlignTagDatas();
		double[] times = new double[ANTENNA_SIZE];
		for (int i = 0;i < ANTENNA_SIZE;++i) {
			times[i] = (dataBuffer[2 * i].getTime() + dataBuffer[2 * i + 1].getTime()) / 2.0;
			ret.rssis[i] = (dataBuffer[2 * i].getRssi() + dataBuffer[2 * i + 1].getRssi()) / 2.0;
			ret.phases[i] = MathUtils.phaseAdd(dataBuffer[2 * i].getPhase(), 
					MathUtils.phaseSub(dataBuffer[2 * i + 1].getPhase(), dataBuffer[2 * i].getPhase()) / 2.0);
		}
		
		// begin align
		ret.timeAligned = 0;
		for (double t : times) {
			ret.timeAligned += t;
		}
		ret.timeAligned /= 4.0;
		if (lastDatas != null) {
			for (int i = 0;i < ANTENNA_SIZE;++i) {
				double phaseDiff = MathUtils.phaseSub(ret.phases[i], lastDatas.phases[i]);
				double rssiDiff = ret.rssis[i] - lastDatas.rssis[i];
				double timeDiff = times[i] - lastDatas.timeAligned;
				ret.phases[i] = MathUtils.phaseAdd(lastDatas.phases[i], (phaseDiff / timeDiff) * (ret.timeAligned - lastDatas.timeAligned));
				ret.rssis[i] = lastDatas.rssis[i] + (rssiDiff / timeDiff) * (ret.timeAligned - lastDatas.timeAligned);
			}
		}
		return ret;
	}
}
