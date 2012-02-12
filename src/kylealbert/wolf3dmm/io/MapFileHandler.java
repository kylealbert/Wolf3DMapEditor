package kylealbert.wolf3dmm.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kylealbert.wolf3dmm.io.GameDataTools.DataTypes;
import kylealbert.wolf3dmm.main.DataBank;
import kylealbert.wolf3dmm.main.GameMap;


/**
 * Module for reading in the Gamemaps and Maphead files.
 * 
 * DATA READING INFO:
 *     word -> 2 bytes
 *     dword -> 4 bytes
 *     
 * FUTURE 3+ PLANE SUPPORT:
 * 		Reference WDC's help message on MAXPLANES
 * 		Use Lists to store plane information
 * 
 * @author Kyle Albert
 */
public final class MapFileHandler {
/*=============================================================================

 PRIVATE
 
 =============================================================================*/
	
// --- [ FIELDS ] -------------------------------------------------------------
	/** The GAMEMAPS file object. */
	private static File gameMapsFile;
	/** The MAPHEAD file object. */
	private static File mapHeadFile;
	/** The data input stream for reading the files. */
	/** An arraylist of offsets to mapdata in the gamemaps file. */
	private static List<Integer> mapHeaderLocations;
	/** Number of map chunks. */
	private static int numMaps;
	/** Number of planes for each map. */
	private static int numPlanes;
	
	/** This is the fixed number of data pointers in the maphead file. */
	private static int RLEWTag;
	private static final int NUM_DATA_POINTERS = 100;
	//private static final int MAP_AREA = 64*64;
	//private static final int MAPHEADER_LENGTH = 38; // 38 bytes (42?)
	private static final int MAP_NAME_LENGTH = 16; // 16 byte map name
	
// --- [ METHODS ] ------------------------------------------------------------
	
	/**
	 * Parses the maphead file and builds an array of locations for map headers in
	 * the gamemaps file.  The maphead file is RLEW compressed and must be uncompressed.
	 * The first two bytes of the maphead file are the RLEW magic word that specifies
	 * the number of bytes.
	 */
	private static void parseMapHead() {
		try {
			GameDataTools.setInputStream(mapHeadFile);
			
			// -------------------------------------------------------
			// Skip the RLEW Tag (specifies the # of uncompressed bytes - MAPHEAD is not compressed for Wolfenstein 3D)
			//     
			RLEWTag = GameDataTools.readData(DataTypes.WORD);
			
			// -------------------------------------------------------
			// Multi-plane support
			//
			
			// -------------------------------------------------------		
			// Read in the offsets (DWORDs (4bytes))
			//
			for (int x = 0; x < NUM_DATA_POINTERS; x++) {
				int location = GameDataTools.readData(DataTypes.DWORD);
				if (location > 0) {
					numMaps++;
					mapHeaderLocations.add(location);
				}
			}
			GameDataTools.closeInputStream();
		} catch (IOException e) {
			System.err.println("ERROR: Couldn't read in the maphead file " + mapHeadFile.getName() );
			throw new RuntimeException();
		}
	}
	
	private static void parseGameMaps() {
		try {
			GameDataTools.setInputStream(gameMapsFile);
			//GameDataTools.markInputStream();
			
			//////////////////////////////////////
			// Read map headers for each map and build map objects
			for (int m = 0; m < numMaps; m++) {
				int mapHeight, mapWidth;
				StringBuilder mapName = new StringBuilder(); // 16 chars
				
				////////////////////////////////
				// Skip to header offset
				GameDataTools.inSeek(mapHeaderLocations.get(m));
					
				////////////////////////////////
				// Read in map chunk information
				
				int[] planeOffsets = new int[numPlanes];
				int[] planeCmpLength = new int[numPlanes]; // ARE THESE USED FOR ANYTHING???
				// location of compressed plane data
				for (int i = 0; i < numPlanes; i++) {
					planeOffsets[i] = GameDataTools.readData(DataTypes.DWORD);
				}

				// # bytes in uncompressed plane data 
				for (int i = 0; i < numPlanes; i++) {
					planeCmpLength[i] = GameDataTools.readData(DataTypes.WORD);
				}
				
				// map width and height
				mapWidth = GameDataTools.readData(DataTypes.WORD);
				mapHeight = GameDataTools.readData(DataTypes.WORD);
				// map name
				for (int i = 0; i < MAP_NAME_LENGTH; i++) {
					mapName.append((char)GameDataTools.readData(DataTypes.BYTE));
				}
				
				//System.out.println("Plane 0 compressed length : " + p0CmpLength + " offset: " + p0DataOffset + ", Map height: " + mapHeight + ", Map width: " + mapWidth) ;
				List<int[][]> decompressedPlanes 
				= GameDataTools.decompressPlanes(planeOffsets, mapHeight, mapWidth, RLEWTag);
				
				DataBank.cacheIntoMapList(new GameMap(decompressedPlanes, mapName.toString(), mapHeight, mapWidth));
				
				//GameDataTools.resetInputStream();
			}
			GameDataTools.closeInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
	}
	
/*=============================================================================

 PUBLIC
 
 =============================================================================*/
	
// --- [ FIELDS ] -------------------------------------------------------------
	
// --- [ METHODS ] ------------------------------------------------------------
	
	/**
	 * Loads all gamemaps into the DataBank map list.
	 */
	public static void loadMapData(File g, File h, int planes) {
		gameMapsFile = g;
		mapHeadFile = h;
		
		mapHeaderLocations = new ArrayList<Integer>();
		numMaps = 0;
		numPlanes = planes;
		RLEWTag = 0;
		
		parseMapHead();
		parseGameMaps();
	}
	

// --- [ TEMP MAIN ] ----------------------------------------------------------
	/*
	 * TEMP
	 */
	public static void main(String[] args) {
		MapFileHandler.loadMapData(new File("GAMEMAPS.WL6"), new File("MAPHEAD.WL6"), 3);
	}

}
