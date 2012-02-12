package kylealbert.wolf3dmm.main;
import java.util.List;

/**
 * 
 */

/**
 * @author Kyle
 *
 */
public final class GameMap {
/*=============================================================================

 PRIVATE
 
 =============================================================================*/
	
// --- [ FIELDS ] -------------------------------------------------------------
	/** Contains all plane data for this map (size of planeData = number of planes). */
	private List<int[][]> planeData;
	/** The name of the map. */
	private String mapName;
	/** Map height and width. */
	private int mapHeight, mapWidth;
	
	
// --- [ METHODS ] ------------------------------------------------------------

	
/*=============================================================================

 PUBLIC
 
 =============================================================================*/
	
// --- [ FIELDS ] -------------------------------------------------------------
	
// --- [ CONSTRUCTORS ] -------------------------------------------------------
	public GameMap(List<int[][]> planes, String name, int height, int width) {
		this.planeData = planes;
		this.mapName = name;
		this.mapHeight = height;
		this.mapWidth = width;
	}
	
// --- [ METHODS ] ------------------------------------------------------------
	
	//
	/**
	 * Sets a single tile (coordinates [x,y] on the map) on a plane to a new value.
	 * 
	 * @param x x coordinate of map to go to
	 * @param y y coordinate of map to go to
	 * @param plane the plane to access
	 * @param type mode of editing (0 = 16 byte, 1 = low 8 bytes, 2 = high 8 bytes)
	 * 
	 * @alters tile at (x,y,plane)
	 * @requires 0 <= plane <= [number of planes map has - 1]
	 * 			 0 <= x <= [width of map - 1]
	 * 			 0 <= y <= [height of map - 1]
	 * @ensures specified tile is set to the new value.
	 */
	public void setTile(int x, int y, int plane, int type, int newValue) {
		planeData.get(plane)[y][x] = newValue; // TODO: is it [x][y] or [y][x]? gg
	}
	
	/**
	 * Sets a rectangular chunk of tile to a new value with the bounds specified
	 * by startx, starty, endx, and endy.  The number of entries changed will be
	 * equal to (endx-startx)*(endy-starty).
	 * 
	 * @param startx upper left bounding x coordinate
	 * @param starty upper left bounding y coordinate
	 * @param endx lower right bounding x coordinate
	 * @param endy lower right bounding y coordinate
	 * @param plane the plane to access
	 * @param type mode of editing (0 = 16 byte, 1 = low 8 bytes, 2 = high 8 bytes)
	 * @param newValue the value to set all tiles within bounds to
	 * 
	 * @requires 0 <= startx <= endx <= [map width - 1]
	 * 			 0 <= starty <= endy <= [map height - 1]
	 * 			 0 <= plane <= [number of planes map has - 1]
	 * @ensures tile chunk is set to newValue
	 */
	public void setTileRectangle(int startx, int starty, int endx, int endy, int plane, int type, int newValue) {
		int[][] planeRef = planeData.get(plane);
		
		for (int y = starty; y <= endy; y++) {
			for (int x = startx; x <= endx; x++) {
				planeRef[y][x] = newValue;
			}
		}
	}
	
	/**
	 * Gets a single tile (coordinates [x,y] on the map) from a specified plane.
	 * Each tile is 2 bytes of data, however the data is stored as an integer.
	 * To get high-byte data from a tile, use the bitmask 0xFF00; for low-byte
	 * data, use the bitmask 0x00FF.
	 * 
	 * @param x x coordinate of map to go to
	 * @param y y coordinate of map to go to
	 * @param plane the plane to access
	 * 
	 * @requires plane is less than or equal to number of planes the map has.
	 * 			 0 <= x <= [width of map - 1]
	 * 			 0 <= y <= [height of map - 1]
	 * 
	 * @return the tile info for the given map coordinates
	 */
	public int getTile(int x, int y, int plane) {
		return planeData.get(plane)[y][x];
	}
	
	/**
	 * Sets the name of the map.
	 * 
	 * @param newName the new name of the map
	 */
	public void setName(String newName) {
		mapName = newName;
	}
	
	/**
	 * Returns the name of the map
	 * @return
	 */
	public String getName() {
		return mapName;
	}
	
	/**
	 * Returns the number of planes this map has.
	 * 
	 * @return the number of planes this map has.
	 */
	public int getNumPlanes() {
		return planeData.size();
	}
	
	public int getMapHeight() {
		return mapHeight;
	}
	
	public int getMapWidth() {
		return mapWidth;
	}
	
}
