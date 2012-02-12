package kylealbert.wolf3dmm.io;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import kylealbert.wolf3dmm.io.GameDataTools.DataTypes;
import kylealbert.wolf3dmm.main.DataBank;


/**
 * 
 * @author Kyle Albert
 */
public final class GraphicFileHandler {
/*=============================================================================

	 PRIVATE
	 
=============================================================================*/
			
		// --- [ FIELDS ] -------------------------------------------------------------
		/** The GAMEMAPS file object. */
		private static File vswapFile;
		/** Dimensions of the vswap graphics. */
		private static int graphicDim;
		/** Number of chunks in the VSWAP file. */
		@SuppressWarnings("unused")
		private static int numChunks;
		/** Offset to sprite chunks. */
		private static int spritePageOffset;
		/** Offset to sound chunks. */
		private static int soundPageOffset;
		/** Page offsets. */
		private static int[] pageOffsets;
		/** Page lengths. */
		private static int numGraphicChunks;
		
		private static int pageSize;
		
		private static List<BufferedImage> graphicList;
		
		private static final int NUM_DATA_OFS = 64;
			
		// --- [ METHODS ] ------------------------------------------------------------
		
		/**
		 * Parse the VSWAP header information which includes various chunk info
		 * needed for loading chunks.
		 */
		private static final void parseVSWAPHeader() throws FileFormatException, IOException {
			// Get some header information
			numChunks = GameDataTools.readData(DataTypes.WORD);
			spritePageOffset = GameDataTools.readData(DataTypes.WORD);
			soundPageOffset = GameDataTools.readData(DataTypes.WORD);
			numGraphicChunks = soundPageOffset;
			int dataStart = 0;
			
			// Get file address offsets to chunks
			pageOffsets = new int[numGraphicChunks];
			for (int x = 0; x < numGraphicChunks; x++) {
				pageOffsets[x] = GameDataTools.readData(DataTypes.DWORD);
				
				if (x == 0) 
					dataStart = pageOffsets[0];
				
				if (pageOffsets[x] != 0 && (pageOffsets[x] < dataStart || pageOffsets[x] > vswapFile.getTotalSpace()))
					throw new FileFormatException("VSWAP file '" + vswapFile.getName() + "' contains invalid page offsets.");
			}
		}
		
		private static final void buildGraphicList() throws IOException {
			////////////////////////////////
			// Begin reading in header info and set constants
			int page = 0;
			final int[] bitMasks = new int[]{(byte)0xFF};
			
			graphicList = new ArrayList<BufferedImage>(numGraphicChunks);
			IndexColorModel colorModel = DataBank.getColorModel();
			DataBufferByte dbuf = new DataBufferByte(pageSize);
			SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, graphicDim, graphicDim, bitMasks);
			WritableRaster raster = Raster.createWritableRaster(sampleModel, dbuf, null);
			
			////////////////////////////////
			// Load in the wall textures
			for (page = 0; page < spritePageOffset; page++) {
				GameDataTools.inSeek(pageOffsets[page]);
				
				for (int col = 0; col < graphicDim; col++) {
					for (int row = 0; row < graphicDim; row++) {
						dbuf.setElem(col + (graphicDim) * row, (byte)GameDataTools.readData(DataTypes.BYTE));
					}
				}
				
				BufferedImage img = new BufferedImage(graphicDim, graphicDim, BufferedImage.TYPE_BYTE_INDEXED, DataBank.getColorModel());
				img.setData(raster);
				
				graphicList.add(img);
			}
			
			////////////////////////////////
			// Load in the sprites
			for ( ; page < numGraphicChunks; page++) {
				int leftpix, rightpix, starty, endy, newstart;
				int offset = pageOffsets[page];
				int[] dataOfs = new int[NUM_DATA_OFS]; // 64 even in hires?
				long oldpos;
				byte col;
				
				// Clear the byte buffer
				Arrays.fill(dbuf.getData(), (byte)0xFF);
				
				GameDataTools.inSeek(offset);
				
				leftpix = GameDataTools.readData(DataTypes.WORD);
				rightpix = GameDataTools.readData(DataTypes.WORD);
				int totalOfs = rightpix - leftpix + 1;
				for (int j = 0; j < totalOfs; j++) {
					dataOfs[j] = GameDataTools.readData(DataTypes.WORD);
				}
				
				for (int spot = 0; leftpix <= rightpix; leftpix++, spot++) {
					GameDataTools.inSeek(offset + dataOfs[spot]);
					
					while((endy = GameDataTools.readData(DataTypes.WORD)) != 0) {
						endy >>= 1;
						newstart = GameDataTools.readData(DataTypes.WORD);
						starty = GameDataTools.readData(DataTypes.WORD) >> 1;
						oldpos = GameDataTools.getFilePos(); // reading in the colors jumps to a new spot
						
						GameDataTools.inSeek(offset + newstart + starty);
						for ( ; starty < endy; starty++) {
							col = (byte)GameDataTools.readData(DataTypes.BYTE);
							dbuf.setElem(starty * graphicDim + leftpix, col);
						}
						GameDataTools.inSeek(oldpos); // go back to "endy data"
					}
				}
				
				BufferedImage img = new BufferedImage(graphicDim, graphicDim, BufferedImage.TYPE_BYTE_INDEXED, colorModel);
				img.setData(raster);
				
				graphicList.add(img);
			}
		}
			
			
/*=============================================================================

 PUBLIC
 
 =============================================================================*/
			
		// --- [ FIELDS ] -------------------------------------------------------------	
			
		// --- [ METHODS ] ------------------------------------------------------------
		public static final void loadGraphicData(File resource, int dimension) throws FileFormatException, IOException {
			// Define some needed info...
			vswapFile = resource;
			graphicDim = dimension;
			pageSize = dimension * dimension;
		
			// Setup the stream...
			GameDataTools.setInputStream(resource);
		
			// Do the work...
			parseVSWAPHeader();
			buildGraphicList();
			
			// Store the image list...
			DataBank.storeVswapImages(graphicList);
			
			// Kill the references... we no longer need to access the VSWAP and we never modify it
			graphicList = null;
			vswapFile = null;
		}
		
		// --- [ TEMP MAIN ] ----------------------------------------------------------
		/*
		 * TEMP
		 */
		public static void main(String[] args) {
			try {
				DataBank.resetData();
				PaletteFileHandler.loadPalette(new File("sodpal.inc"));
				MapFileHandler.loadMapData(new File("GAMEMAPS.WL6"), new File("MAPHEAD.WL6"), 3);
				GraphicFileHandler.loadGraphicData(new File("VSWAP.EOD"), 64);
				JFrame f = new JFrame();
				final JPanel p = new JPanel();
				TempDrawer t = new TempDrawer();
				f.setSize(1000,600);
				
				p.setLayout(new FlowLayout());
				final JScrollPane j = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				p.add(j, FlowLayout.LEFT);
				
				f.getContentPane().add(p);
				
			    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			    f.setVisible(true);
			    f.addComponentListener(new ComponentListener() {
			    	public void componentHidden(ComponentEvent evt) {}
			    	public void componentMoved(ComponentEvent evt) {}
			    	public void componentShown(ComponentEvent evt) {}
			        public void componentResized(ComponentEvent evt) {
			            Component c = (Component)evt.getSource();
			    
			            // Get new size
			            Dimension newSize = c.getSize();
			            
			            p.setPreferredSize(new Dimension((int)newSize.getWidth() - 10, (int)newSize.getHeight() - 50));
			            j.setPreferredSize(new Dimension((int)newSize.getWidth() - 10, (int)newSize.getHeight() - 50));
			            p.revalidate();
			            j.revalidate();
			        }
			    });
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

}
