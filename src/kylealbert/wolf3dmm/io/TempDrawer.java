/**
 * 
 */
package kylealbert.wolf3dmm.io;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

import javax.swing.JComponent;

import kylealbert.wolf3dmm.main.DataBank;
import kylealbert.wolf3dmm.main.GameMap;


/**
 * @author Kyle
 *
 */
public class TempDrawer extends JComponent {

	public TempDrawer() {
		
	}
	
	public void paint(Graphics g) {
		int xCoord = 0, yCoord = 0;
		
		int[] tmp = new int[256];
		DataBank.getColorModel().getRGBs(tmp);
		
		for (int x = 0; x < tmp.length; x++) {
			g.setColor(new Color(tmp[x]));
			g.fillRect(xCoord, yCoord, 10, 10);
			//g.drawRect(xCoord, yCoord, 10, 10);
			xCoord = (x % 16)*10;
			if (x != 0 && (x % 16) == 0) yCoord += 10;
		}
		
		
		xCoord = 0; 
		yCoord = 160;
		
		for (int page = 0; page < DataBank.vswapImages.size(); page++) {
			xCoord = (page%16)*128;
			if (page != 0 && (page % 16) == 0) yCoord += 128;
			BufferedImage target = DataBank.vswapImages.get(page);
			g.drawImage(target, xCoord, yCoord, xCoord + 128, yCoord + 128, 0, 0, 64, 64, this);
		}

		this.setPreferredSize(new Dimension(6000, 20000));
		
	}
}

/*for (int g1 = 0; g1 < DataBank.vswapImages.size(); g1++) {
xCoord = (g1%8)*64;
if (g1 != 0 && (g1 % 8) == 0) yCoord += 64;

g.drawImage(DataBank.vswapImages.get(g1), xCoord, yCoord, this);

}*/

/*
yCoord = 0;

for (int g1 = 0; g1 < DataBank.vswapImages.size(); g1++) {
xCoord = (64*8) + (g1%8)*32;
if (g1 != 0 && (g1 % 8) == 0) yCoord += 32;
g.drawImage(DataBank.vswapImages.get(g1), xCoord, yCoord, xCoord + 32, yCoord + 32, 0,0,63,63, this);
}
*/

/*GameMap testMap = DataBank.mapList.get(0);
int x = 0, y = 0;
for (x = 0; x < 64; x++) {
	for (y = 0; y < 64; y++) {
		int tileInfo = testMap.getTile(x, y, 0);
		if (tileInfo <= 87)
			g.drawImage(DataBank.vswapImages.get((tileInfo - 1) * 2), x * 64, y * 64, this);
	}
}

this.setPreferredSize(new Dimension(x * 64, y * 64));*/
