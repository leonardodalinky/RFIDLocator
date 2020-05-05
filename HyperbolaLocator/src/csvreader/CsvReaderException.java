package csvreader;

public class CsvReaderException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3734802069692645145L;

	public CsvReaderException() {
		super();
	}
	
	public CsvReaderException(String msg) {
		super(msg);
	}
	
	public CsvReaderException(Throwable th) {
		super(th);
	}
	
	public CsvReaderException(String msg, Throwable th) {
		super(msg, th);
	}
}
