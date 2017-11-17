/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package controller;
import model.*;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageLineFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barré-Brisebois, Éric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.12 $
 */
public class ImageLineFiller extends AbstractTransformer {
	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 0;
	private int saturationThreshold = 0;
	private int valueThreshold = 0;
	
	/**
	 * Creates an ImageLineFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageLineFiller() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();
					fill(ptTransformed);
					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void fill(Point ptClicked) {
		Stack stack = new Stack();
		stack.push(ptClicked);
		Pixel targetColor = currentImage.getPixel((int)(ptClicked.getX()), (int)(ptClicked.getY())); // on récupère la couleur du pixel sélectionner
		// Si le mode floodFill est activé
		if(floodFill){
			while (!stack.empty()) {
				Point current = (Point)stack.pop();
				if (0 <= current.y && current.y < currentImage.getImageHeight() && 0 <= current.x && current.x < currentImage.getImageWidth() &&
						!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
					
						currentImage.setPixel(current.x, current.y, fillColor);
						// Next points to fill.
						Point nextBottom = new Point(current.x, current.y-1);
						Point nextTop = new Point(current.x, current.y+1);
						Point nextLeft = new Point(current.x-1, current.y);
						Point nextRight = new Point(current.x+1, current.y);
						
						if (currentImage.getPixel((int)(nextBottom.getX()), (int)(nextBottom.getY())).equals(targetColor,hueThreshold,saturationThreshold,valueThreshold) && nextBottom.getY()>0)
							stack.push(nextBottom);
						if (currentImage.getPixel((int)(nextTop.getX()), (int)(nextTop.getY())).equals(targetColor,hueThreshold,saturationThreshold,valueThreshold) && nextTop.getY()<currentImage.getImageHeight()-1)
							stack.push(nextTop);
						if (currentImage.getPixel((int)(nextLeft.getX()), (int)(nextLeft.getY())).equals(targetColor,hueThreshold,saturationThreshold,valueThreshold) && nextLeft.getX()>0)
							stack.push(nextLeft);
						if (currentImage.getPixel((int)(nextRight.getX()), (int)(nextRight.getY())).equals(targetColor,hueThreshold,saturationThreshold,valueThreshold) && nextRight.getX()<currentImage.getImageWidth())
							stack.push(nextRight);
									}
			}
		}
		
		if (!floodFill){
			
			while (!stack.empty()) {
				Point current = (Point)stack.pop();
				if (0 <= current.y && current.y < currentImage.getImageHeight() && 0 <= current.x && current.x < currentImage.getImageWidth() &&
						!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
					
						currentImage.setPixel(current.x, current.y, fillColor);
						// Next points to fill.
						Point nextBottom = new Point(current.x, current.y-1);
						Point nextTop = new Point(current.x, current.y+1);
						Point nextLeft = new Point(current.x-1, current.y);
						Point nextRight = new Point(current.x+1, current.y);
						
						if (!currentImage.getPixel((int)(nextBottom.getX()), (int)(nextBottom.getY())).equals(borderColor,hueThreshold,saturationThreshold,valueThreshold) && nextBottom.getY()>0)
							stack.push(nextBottom);
						if (!currentImage.getPixel((int)(nextTop.getX()), (int)(nextTop.getY())).equals(borderColor,hueThreshold,saturationThreshold,valueThreshold) && nextTop.getY()<currentImage.getImageHeight()-1)
							stack.push(nextTop);
						if (!currentImage.getPixel((int)(nextLeft.getX()), (int)(nextLeft.getY())).equals(borderColor,hueThreshold,saturationThreshold,valueThreshold) && nextLeft.getX()>0)
							stack.push(nextLeft);
						if (!currentImage.getPixel((int)(nextRight.getX()), (int)(nextRight.getY())).equals(borderColor,hueThreshold,saturationThreshold,valueThreshold) && nextRight.getX()<currentImage.getImageWidth())
							stack.push(nextRight);
					}
			}
		}
		// TODO EP In this method, we are creating many new Point instances. 
		//      We could try to reuse as many as possible to be more efficient.
		// TODO EP In this method, we could be creating many Point instances. 
		//      At some point we can run out of memory. We could create a new point
		//      class that uses shorts to cut the memory use.
		// TODO EP In this method, we could test if a pixel needs to be filled before
		//      adding it to the stack (to reduce memory needs and increase efficiency).
	}
	
public double RGBtoHSV(int red,int green,int blue,String color) {
		
		double R=red/255.0;
		double G=green/255.0;
		double B=blue/255.0;
		double Cmax=Math.max(Math.max(R,G),B);
		double Cmin=Math.min(Math.min(R,G),B);
		
		double Delta=Cmax-Cmin;
		double hue=0,sat=0,val=0;
		if (Delta==0.0) {
				hue=0;}
		else {
			if (Cmax==R) { hue=60*(((G-B)/Delta)%6);}
			if (Cmax==G) { hue=60*(((B-R)/Delta)+2);}
			if (Cmax==B) { hue=60*(((R-G)/Delta)+4);}	
		}
		
		
		if (Cmax==0) sat=0;
		else sat=Delta/Cmax;
		val=Cmax;
		
		if(hue<0)hue=hue+60;
		if(hue>255)hue=255;
		if(sat<0)sat=0;
		if(sat>255)sat=255;
		if(val<0)val=0;
		if(val>255)val=255;
		
		if (color=="hue")return hue;
		if (color=="sat")return sat;
		if (color=="val")return val;
		return -1;
		
	}
	
	public int HSVtoRGB(double hue, double sat, double val,String color) {
		
		double C=val*sat;
		double X= C*(1-Math.abs((hue/60)%2-1));
		double m= val-C;
		double R=0,G=0,B=0;
		if (hue>=0 &&hue<60) {
			R=C;
			G=X;
			B=0;			
		}else if (hue>=60 &&hue<120) {
			R=X;
			G=C;
			B=0;			
		}else if (hue>=120 &&hue<180) {
			R=0;
			G=C;
			B=X;			
		}else if (hue>=180 &&hue<240) {
			R=0;
			G=X;
			B=C;			
		}else if (hue>=240 &&hue<300) {
			R=X;
			G=0;
			B=C;			
		}else if (hue>=300 &&hue<360) {
			R=C;
			G=0;
			B=X;			
		} else return -1;
		
		
		if (color=="red")return (int)((R+m)*255);
		if (color=="green")return (int)((G+m)*255);
		if (color=="blue")return (int)((B+m)*255);
		
		return -1;
	}
	
	public Boolean isHueCorrect(Pixel a, Pixel b){

		
		if(Math.abs(RGBtoHSV(a.getRed(), a.getGreen(), a.getBlue(), "hue") - RGBtoHSV(b.getRed(), b.getGreen(), b.getBlue(), "hue")) < hueThreshold)
			{return true; }
		else
			return false;		
	}
	
	public Boolean isValCorrect(Pixel a, Pixel b){

		if(Math.abs(RGBtoHSV(a.getRed(), a.getGreen(), a.getBlue(), "val") - RGBtoHSV(b.getRed(), b.getGreen(), b.getBlue(), "val")) < valueThreshold)
			return true;
		else
			return false;		
	}
	
	public Boolean isSatCorrect(Pixel a, Pixel b){

		if(Math.abs(RGBtoHSV(a.getRed(), a.getGreen(), a.getBlue(), "sat") - RGBtoHSV(b.getRed(), b.getGreen(), b.getBlue(), "sat")) < saturationThreshold)
			return true;
		else
			return false;		
	}
	
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}

}
