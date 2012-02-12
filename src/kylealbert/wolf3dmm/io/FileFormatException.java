package kylealbert.wolf3dmm.io;

/**
 * Exception for invalid file formats, types, etc.
 * 
 * @author Kyle Albert
 */
public class FileFormatException extends Exception {

	private static final long serialVersionUID = -4487166880268886476L;

	public FileFormatException() {
		super();
	}

	public FileFormatException(String msg) {
		super(msg);
	}
	
}
