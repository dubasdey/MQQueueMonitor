package org.erc.qmm.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;


/**
 * The Class GraphicUtils.
 */
public abstract class GraphicUtils {

	/**
	 * Creates the color icon.
	 *
	 * @param color the color
	 * @return the image icon
	 */
	public static ImageIcon createColorIcon(Color color){
		BufferedImage buffImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics g = buffImage.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, 16, 16);
		g.setColor(Color.black);
		g.drawRect(0, 0, 15, 15);
		return new ImageIcon(buffImage);
	}
}
