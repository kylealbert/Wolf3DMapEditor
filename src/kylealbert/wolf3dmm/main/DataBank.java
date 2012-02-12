package kylealbert.wolf3dmm.main;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Kyle Albert
 */
public final class DataBank {
//-----------------------------------------------------------------------------
// PUBLIC DATA STRUCTURES
//-----------------------------------------------------------------------------
	/** 
	 * Ordered list of all uncompressed maps.  This is generated by MapFileParser
	 * and used for generating a GAMEMAPS and MAPHEADER file. */
	public static List<GameMap> mapList;
	/** Maps palette indices to colors. */
	public static IndexColorModel vswapColorModel;
	/** Collection of all vswap images in an order list. */
	public static List<BufferedImage> vswapImages;
	/** Object list data structure.  A List for unlimited planes and a Map to link
	 * map ID's to vswap images. */
	public static List< HashMap<Integer, BufferedImage> > objList;
	
	public static int numPlanes;
	
	public static int vswapDimension;
	
//-----------------------------------------------------------------------------
// DATA STRUCTURE INTERFACE ("bank teller" methods)
//-----------------------------------------------------------------------------
	// --- [ MAP STUFF ] ------------------------------------------------------
	/**
	 * Adds a map to the map list in the next open spot.
	 */
	public static void cacheIntoMapList(GameMap map) {
		mapList.add(map);
	}
	
	/**
	 * Adds a map to the map list in the next open spot. To be used for adding chunks
	 */
	public static void cacheIntoMapList(GameMap map, int index) {
		mapList.add(index, map);
	}
	
	/**
	 * Removes a map from the map list
	 */
	public static void removeFromMapList(int index) {
		mapList.remove(index);
	}
	
	// --- [ PALETTE / COLOR MODEL STUFF ] ------------------------------------
	/**
	 * Stores a {@link java.awt.image.IndexColorModel} for use in drawing VSWAP images.
	 */
	public static void storeColorModel(IndexColorModel cm) {
		vswapColorModel = cm;
	}
	
	/**
	 * Returns an {@link java.awt.image.IndexColorModel} for use in drawing VSWAP images.
	 * @return {@link java.awt.image.IndexColorModel} for VSWAP images.
	 */
	public static IndexColorModel getColorModel() {
		return vswapColorModel;
	}
	
	/**
	 * 
	 * @param images
	 */
	public static void storeVswapImages(List<BufferedImage> images) {
		vswapImages = images;
	}
	
	// --- [ MANAGER METHODS ] ------------------------------------------------
	/**
	 * 
	 */
	public static void resetData() {
		mapList = new ArrayList<GameMap>();
		vswapColorModel = null;
		vswapImages = null;
		objList = new ArrayList < HashMap <Integer, BufferedImage> >();
	}
}