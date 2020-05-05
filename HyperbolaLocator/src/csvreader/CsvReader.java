package csvreader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CsvReader {
	private ArrayList<ArrayList<String>> content = new ArrayList<ArrayList<String>>();
	
	public CsvReader(String filePath) throws IOException, CsvReaderException {
		File file = new File(filePath);
		long fileSize = file.length();
		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
		byte[] fileBuf = new byte[(int)fileSize];
		inputStream.read(fileBuf);
		inputStream.close();
		// parse
		parse(fileBuf);
	}
	
	@SuppressWarnings("unchecked")
	private void parse(byte[] cBuf) throws CsvReaderException {
		boolean isRun = true;
		int state = 0;
		int preState = -114514;
		int front = 0, rear = 0;
		ArrayList<String> strList = new ArrayList<String>();
		while(isRun) {
			if (rear >= cBuf.length || front >= cBuf.length) {
				preState = state;
				state = -1;
			}
			switch (state) {
			case -1:
			{
				switch (preState) {
				case -114514:
				case 0:
				case 4:
				case 5:
					throw new CsvReaderException("Parser Wrong. preState = " + preState);
				default:
					if (rear > front && front < cBuf.length) {
						StringBuilder builder = new StringBuilder();
						for (int i = front;i < cBuf.length;++i) {
							builder.append((char)cBuf[i]);
						}
						strList.add(builder.toString());
						// 更新content
						content.add((ArrayList<String>) strList.clone());
						strList.clear();
					}
					isRun = false;
					break;
				}
			}
			break;
			case 0:
				// 初始化
				state = 1;
				break;
			case 1:
				// 状态1: 判断输入带不带双引号
				if (cBuf[front] != '"') {
					state = 2;
					rear = front;
				}
				else {
					state = 4;
					front = rear = front + 1;
				}
				break;
			case 2:
				// 状态2: 录入字符
				if (cBuf[rear] == ',') {
					StringBuilder builder = new StringBuilder();
					for (int i = front;i < rear;++i) {
						builder.append((char)cBuf[i]);
					}
					strList.add(builder.toString());
					front = rear = rear + 1;
					state = 1;
				}
				else if (cBuf[rear] == '"') {
					state = -1;
				}
				else if (cBuf[rear] == '\r' || cBuf[rear] == '\n') {
					StringBuilder builder = new StringBuilder();
					for (int i = front;i < rear;++i) {
						builder.append((char)cBuf[i]);
					}
					strList.add(builder.toString());
					front = rear = rear + 1;
					state = 3;
					// 更新content
					content.add((ArrayList<String>) strList.clone());
					strList.clear();
				}
				else {
					++rear;
				}
				break;
			case 3:
				// 状态3: 换行
				if (cBuf[front] == '\r' || cBuf[front] == '\n') {
					++front;
				}
				else {
					state = 1;
				}
				break;
			case 4:
				// 状态4: 双引号形式录入字符
				if (cBuf[rear] == '"') {
					state = 5;
				}
				else {
					++rear;
				}
				break;
			case 5:
				// 状态5: 双引号形式遇到中间的双引号
				if (rear < cBuf.length - 1 && cBuf[rear + 1] == '"') {
					rear += 2;
					state = 4;
				}
				else if (rear == cBuf.length - 1 || cBuf[rear + 1] == ','){
					StringBuilder builder = new StringBuilder();
					for (int i = front;i < rear;++i) {
						if (i >= front + 1 && cBuf[i - 1] == '"')
							continue;
						builder.append((char)cBuf[i]);
					}
					strList.add(builder.toString());
					front = rear = rear + 2;
					state = 1;
				}
				else if (rear < cBuf.length - 1 && (cBuf[rear + 1] == '\r' || cBuf[rear + 1] == '\n')) {
					StringBuilder builder = new StringBuilder();
					for (int i = front + 1;i < rear;++i) {
						if (i >= front + 1 && cBuf[i - 1] == '"')
							continue;
						builder.append((char)cBuf[i]);
					}
					strList.add(builder.toString());
					if (cBuf[rear + 1] == '\r')
						front = rear = rear + 3;
					else 
						front = rear = rear + 2;
					state = 1;
					// 更新content
					content.add((ArrayList<String>) strList.clone());
					strList.clear();
				}
				else {
					state = -1;
				}
				break;
			default:
				break;
			}
		}
		
		return;
	}
	
	// start from 0
	public ArrayList<String> getRow(int index) {
		if (index < 0 || index >= content.size())
			return new ArrayList<String>();
		return content.get(index);
	}
	
	// start from 0
	public ArrayList<String> getCol(int index) {
		ArrayList<String> ret = new ArrayList<String>();
		for (ArrayList<String> strs : content) {
			if (index >= 0 && index < strs.size()) {
				ret.add(strs.get(index));
			}
		}
		return ret;
	}
	
	public int getRowSize() {
		return content.size();
	}
}
