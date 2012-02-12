package kylealbert.wolf3dmm.io;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for performing various actions on game data files (such as
 * GAMEMAPS, MAPHEAD, and possibly VSWAP).  An important note about game data files
 * is that all data in game files is little-endian, and Java is big-endian...
 * 
 * @author Kyle Albert
 */
public final class GameDataTools {
/*=============================================================================

 PRIVATE
 
 =============================================================================*/
	
	// --- [ FIELDS ] ---------------------------------------------------------
	/** The source to read from. */
	private static RandomAccessFile in;
	
	private static ByteBuffer byteBuff;
	/** Signal byte used in Carmack compression. */
	private static final int CARMACK_NEAR = 0xA7;
	/** Signal byte used in Carmack compression. */
	private static final int CARMACK_FAR  = 0xA8;
	
	// --- [ METHODS ] --------------------------------------------------------
	/**
	 * Static utility class for game data stuff.
	 */
	private GameDataTools() { }
	
	/**
	 * Reads a word from the 'in' stream in little endian format and returns the
	 * result.  This also advances the position of the stream by 2 bytes.
	 * 
	 * @return next data type from 'in' stream
	 */
	public static int readData (DataTypes type) throws IOException {
		if (type == DataTypes.BYTE) {
			return in.read();
		} else {
			byte[] inputBuff = new byte[4]; // most we'll ever need is 4 bytes
			
			in.read( inputBuff, 0, type.getLength() ); // read x number of bytes from current line
			byteBuff = ByteBuffer.wrap(inputBuff).order(ByteOrder.LITTLE_ENDIAN);
			
			return byteBuff.getInt();
		}
	}
	
	/**
	 * Hack for BufferedInputStream and skipping...
	 * Due to the behavior of BufferedInputStream, sometimes it doesn't skip
	 * the number bytes you want it to...
	 * 
	 * TODO: Get rid of BufferedInputStream hack?
	 * 
	 * @param numBytes
	 * @return
	 */
	public static final void inSeek(long pos) throws IOException {
		in.seek(pos);
	}
	
	/**
	 * Given an offset to a data chunk compressed using the Carmack algorithm,
	 * this method will return the decompressed chunk as an array of words (words
	 * being 2 bytes of data in the file).  The first word of the chunk is the
	 * expanded chunk length in bytes.
	 * 
	 * @param fileOffset the offset in the file to a carmack compressed chunk
	 * @return decompressed array of words
	 */
	private static int[] carmackExpand(int fileOffset) throws IOException {
		////////////////////////////
		// Get to the correct chunk
		int length;
		int ch, chhigh, count, offset, index;
		
		inSeek(fileOffset);
		
		// First word is expanded length
		length = readData(DataTypes.WORD);
		int[] expandedWords = new int[length]; // array of WORDS
		
		length /= 2;
		
		index = 0;
		
		while (length > 0) {
			ch = readData(DataTypes.WORD);
			chhigh = ch >> 8;
			
			if (chhigh == CARMACK_NEAR) {
				count = (ch & 0xFF);
				
				if (count == 0) {
					ch |= readData(DataTypes.BYTE);
					expandedWords[index++] = ch;
					length--;
				} else {
					offset = readData(DataTypes.BYTE);
					length -= count;
					if (length < 0) 
						return expandedWords;
					while ((count--) > 0) {
						expandedWords[index] = expandedWords[index - offset];
						index++;
					}
				}
			} else if (chhigh == CARMACK_FAR) {
				count = (ch & 0xFF);
				
				if (count == 0) {
					ch |= readData(DataTypes.BYTE);
					expandedWords[index++] = ch;
					length--;
				} else {
					offset = readData(DataTypes.WORD);
					length -= count;
					if (length < 0) 
						return expandedWords;
					while ((count--) > 0) {
						expandedWords[index++] = expandedWords[offset++];
					}
					
				}
			} else {
				expandedWords[index++] = ch;
				length--;
			}
		}
		
		return expandedWords;
	}
	
	/**
	 * 
	 */
	private static int[] RLEWExpand(int[] carmackExpanded, int length, int tag) {
		int[] rawMapData = new int[length];
		int value,count,i,src_index,dest_index;
		
		src_index = 1;
		dest_index = 0;

		do {
			value = carmackExpanded[src_index++]; // WORDS!!
			if (value != tag) {
				// uncompressed
				rawMapData[dest_index++] = value;
			} else {
				// compressed string
				count = carmackExpanded[src_index++];
				value = carmackExpanded[src_index++];
				for (i = 1; i <= count; i++) {
					rawMapData[dest_index++] = value;
				}
			}
			
		} while (dest_index < length);
		
		return rawMapData;
	}
	
	/**
	 * Used for converting plane arrays into 2D arrays to make them easier to use
	 * and also allows for the simplification of certain algorithms (like AutoFloor).
	 * The use of this is specialized so there is no requirement other than to make
	 * sure the parameters make sense.
	 * 
	 * @param array one dimensional array to convert
	 * @param width two dimension array width
	 * @param height two dimension array height
	 * @return converted 2D array
	 */
	private static int[][] convertTo2DArray(int[] array, int width, int height) {
		int[][] convertedArray = new int[height][width];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				convertedArray[y][x] = array[x + y*width];
			}
		}
		return convertedArray;
	}
	
	
/*=============================================================================

 PUBLIC
 
 =============================================================================*/
	
	// --- [ METHODS ] --------------------------------------------------------
	/**
	 * Sets the source stream for the methods this module provides.
	 */
	public static void setInputStream(File resource) throws IOException {
		in = new RandomAccessFile(resource, "r");
	}
	
	/**
	 * Decompresses given list of map planes and returns the decompressed data in
	 * same format.
	 * 
	 * @param planes the list of planes to decompress
	 * @return list of decompressed planes (data is raw)
	 */
	public static List<int[][]> decompressPlanes(int[] planeOffsets, int mapHeight, int mapWidth, int RLEWTag) throws IOException {
		List<int[][]> decompressedPlanes = new ArrayList<int[][]>(planeOffsets.length);
		
		for (int x = 0; x < planeOffsets.length; x++) {
			decompressedPlanes.add(convertTo2DArray(RLEWExpand(carmackExpand(planeOffsets[x]), mapHeight*mapWidth, RLEWTag), mapWidth, mapHeight));
		}
		
		return decompressedPlanes;
	}
	
	public static long getFilePos() throws IOException {
		return in.getFilePointer();
	}
	
	public static void closeInputStream() {
		try {
			in.close();
		} catch (Exception e) { }
	}
	
	// TODO: Add a public method to compress map
	
	// --- [ DATA ENUM ] ------------------------------------------------------
	public enum DataTypes {
		BYTE(1),
		WORD(2),
		DWORD(4);
		
		private int length;
		
		private DataTypes(int numBytes) {
			this.length = numBytes;
		}
		
		public int getLength() {
			return this.length;
		}
	}
}
