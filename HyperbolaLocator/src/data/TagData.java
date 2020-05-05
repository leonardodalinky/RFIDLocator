package data;

public class TagData {
	private int id = -1;
	private long time = -1;
	private int antennaId = -1;
	private double rssi = 0;
	private double phase = 0;
	// frequency of channel, MHz
	private double channel = 0;
	private int channelNum = 0;
	private String epc = null;
	
	public TagData() {
		
	}
	
	public TagData(int id, long time, int antennaId, int rssi, double phase, double channel, int channelNum, String epc) {
		this.setId(id);
		this.setTime(time);
		this.setAntennaId(antennaId);
		this.setRssi(rssi);
		this.setPhase(phase);
		this.setChannel(channel);
		this.setChannelNum(channelNum);
		this.setEpc(epc);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getAntennaId() {
		return antennaId;
	}

	public void setAntennaId(int antennaId) {
		this.antennaId = antennaId;
	}

	public double getRssi() {
		return rssi;
	}

	public void setRssi(double rssi) {
		this.rssi = rssi;
	}

	public double getPhase() {
		return phase;
	}

	public void setPhase(double phase) {
		this.phase = phase;
	}

	public double getChannel() {
		return channel;
	}

	public void setChannel(double channel) {
		this.channel = channel;
	}

	public int getChannelNum() {
		return channelNum;
	}

	public void setChannelNum(int channelNum) {
		this.channelNum = channelNum;
	}

	public String getEpc() {
		return epc;
	}

	public void setEpc(String epc) {
		this.epc = epc;
	}
}
