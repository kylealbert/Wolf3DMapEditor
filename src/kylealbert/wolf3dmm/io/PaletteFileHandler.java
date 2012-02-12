package kylealbert.wolf3dmm.io;

import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import kylealbert.wolf3dmm.main.DataBank;


/**
 * Reads the project palette file in and stores the color information into the
 * {@link kylealbert.wolf3dmm.main.DataBank}.  This module supports the reading in the following
 * palette file types: JASC palettes (*.pal), WOLF4SDL palettes (*.inc).
 * 
 * @author Kyle Albert
 */
public final class PaletteFileHandler {
/*=============================================================================

 PRIVATE
 
 =============================================================================*/
	
	// --- [ FIELDS ] ----------------------------------------------------------
	/** The file containing the palette information. */
	private static File paletteFile;
	/** The color model to create from the palette file. */
	private static IndexColorModel paletteModel;
	/** Input stream for the palette file. */
	private static BufferedReader in;
	/** Wolf 3d mods are strictly 256 colors. */
	private static final int FIXED_NUM_COLORS = 256;
	
	// --- [ METHODS ] ---------------------------------------------------------
	/**
	 * Creates an IndexColorModel from a JASC palette file.
	 * 
	 * @ensures an {@link java.awt.image.IndexColorModel} is created from the
	 * 			file data and stored in the {@link kylealbert.wolf3dmm.main.DataBank} if the format
	 * 			of the palette file is correct.
	 * 
	 * @throws IOException thrown when given file cannot be read or found.
	 * @throws FileFormatException thrown when given file does not conform to its specified format.
	 */
	private static final void createColorModelFromPAL() throws IOException, FileFormatException {
		// Check for correct headers
		if (!in.readLine().equals("JASC-PAL") || !in.readLine().equals("0100")) {
			throw new FileFormatException("Specified palette '" + paletteFile.getName() + "' is incorrectly formatted.");
		}
		
		// Get the number of colors in the palette, should always be 256 for Wolf3D
		int numColors = Integer.parseInt(in.readLine());
		if (numColors != FIXED_NUM_COLORS) {
			throw new FileFormatException("Specified palette '" + paletteFile.getName() + "' does not contain exactly 256 colors.");
		}
		
		// Read in the RGB values to create an IndexColorModel
		String line = null;
		String[] tokens = null;
		byte[] r = new byte[FIXED_NUM_COLORS];
		byte[] g = new byte[FIXED_NUM_COLORS];
		byte[] b = new byte[FIXED_NUM_COLORS];
		
		for (int x = 0; x < FIXED_NUM_COLORS; x++) {
			line = in.readLine(); // get the color line "r g b"
			
			if (line == null) {
				throw new FileFormatException("Specified palette '" + paletteFile.getName() + "' is incorrectly formatted.");
			}
			
			tokens = line.split("\\s");
			if (tokens.length != 3) { // should always have 3 numbers
				throw new FileFormatException("Specified palette '" + paletteFile.getName() + "' is incorrectly formatted.");
			}
			
			r[x] = (byte)Integer.parseInt(tokens[0]);
			g[x] = (byte)Integer.parseInt(tokens[1]);
			b[x] = (byte)Integer.parseInt(tokens[2]);
		}
		
		paletteModel = new IndexColorModel(8, FIXED_NUM_COLORS, r, g, b, 255); // 255 = transperancy for Wolf3D
	}
	/**
	 * Creates an {@link java.awt.image.IndexColorModel} from a INC palette file.
	 * 
	 * @ensures an {@link java.awt.image.IndexColorModel} is created from the
	 * 			file data and stored in the {@link kylealbert.wolf3dmm.main.DataBank} if the format
	 * 			of the palette file is correct.
	 * 
	 * @throws IOException thrown when given file cannot be read or found.
	 * @throws FileFormatException thrown when given file does not conform to its specified format.
	 */
	private static final void createColorModelFromINC() throws IOException, FileFormatException {
		byte [] r = new byte[FIXED_NUM_COLORS];
		byte [] g = new byte[FIXED_NUM_COLORS];
		byte [] b = new byte[FIXED_NUM_COLORS];

		int colorsReadIn = 0;
		Scanner s = new Scanner(in).useDelimiter("\\)*,*\\s*(RGB\\(\\s*)|\\)\\s*");
		
		while (s.hasNext()) {
			String[] rgbValues = s.next().split(",\\s+");
			
			r[colorsReadIn] = (byte)(Integer.parseInt(rgbValues[0]) * 4);
			g[colorsReadIn] = (byte)(Integer.parseInt(rgbValues[1]) * 4);
			b[colorsReadIn] = (byte)(Integer.parseInt(rgbValues[2]) * 4);
			
			colorsReadIn++;
		}
		
		paletteModel = new IndexColorModel(8, FIXED_NUM_COLORS, r, g, b, 256);	
	}
	
	private static final void createColorModelFromACT() throws IOException, FileFormatException {
		
	}
	
/*=============================================================================

 PUBLIC
 
 =============================================================================*/
	
	// --- [ FIELDS ] ----------------------------------------------------------
	
	// --- [ METHODS ] ---------------------------------------------------------
	/**
	 * Loads palette information into the {@link kylealbert.wolf3dmm.main.DataBank}.  The following
	 * file types are supported: JASC palettes (*.pal), Wolf4SDL palettes (*.inc).
	 * 
	 * @ensures {@link java.awt.image.IndexColorModel} is created and stored in
	 * 			the {@link kylealbert.wolf3dmm.main.DataBank} if no exceptions occurred.
	 * 
	 * @throws IOException thrown when given file cannot be read or found.
	 * @throws FileFormatException thrown when given file does not conform to its specified format.
	 */
	public static final void loadPalette(File palFile) throws FileFormatException, IOException {
		paletteFile = palFile;
		in = new BufferedReader(new FileReader(paletteFile));
		
		// Get extension to determine how to read in data
		String fileName = paletteFile.getName();
		int extIndex = fileName.lastIndexOf('.');
		String extension = fileName.substring(extIndex + 1, fileName.length()).toLowerCase();
		
		if (extension.equals("pal")) {
			createColorModelFromPAL();
		} else if (extension.equals("inc")) {
			createColorModelFromINC();
		} else if (extension.equals("act")) {
			createColorModelFromACT();
		} else {
			throw new FileFormatException("Unsupported palette file type.  Supported file types are *.pal, *.inc, and *.act.");
		}
		
		DataBank.storeColorModel(paletteModel);
	}
	
	// --- [ TEMP ] ------------------------------------------------------------
	public static void main(String[] args) {
		try {
			PaletteFileHandler.loadPalette(new File ("sodpal.inc"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
