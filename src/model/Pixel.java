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

package model;

import java.awt.Color;

/**
 * <p>Title: Pixel</p>
 * <p>Description: Class that handles Pixel data in various formats</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author Colin Barr�-Brisebois
 * @version $Revision: 1.11 $
 */
public class Pixel {
    /** ARGB Pixel value */
    private int valueARGB;
    
    /**
     * Pixel default constructor
     */
    public Pixel() {
		valueARGB = 0;
    }
    
	/**
	 * Pixel constructor with a specified ARGB value
	 * @param valueARGB the pixel's ARGB value
	 */
    public Pixel(int valueARGB) {
        this.valueARGB = valueARGB;
    }
    
    public Pixel(int rValue, int gValue, int bValue) {
    	setRed(rValue);
    	setGreen(gValue);
    	setBlue(bValue);
    	setAlpha(255);
    }
    
	public Pixel(int rValue, int gValue, int bValue, int alpha) {
		setRed(rValue);
		setGreen(gValue);
		setBlue(bValue);
		setAlpha(alpha);
	}    
    
    public Pixel(PixelDouble pixel) {
		setRed((int)pixel.getRed());
		setGreen((int)pixel.getGreen());
		setBlue((int)pixel.getBlue());
		setAlpha((int)pixel.getAlpha());
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's ARGB value
	 */    
    public int getARGB() { 
    	return (valueARGB); 
    }

	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's ALPHA value
	 */        
    public int getAlpha() { 
    	return ((valueARGB >> 24) & 0xff); 
    }

	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's RED value
	 */            
    public int getRed() { 
    	return ((valueARGB >> 16) & 0xff); 
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's GREEN value
	 */            
    public int getGreen() { 
    	return ((valueARGB >> 8) & 0xff); 
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's BLUE value
	 */            
    public int getBlue() { 
    	return ((valueARGB) & 0xff); 
    }
    
	/**
	 * Sets an attribute of the pixel
	 * @param valueARGB the pixel's ARGB value
	 */            
    public void setARGB(int valueARGB) { 
		this.valueARGB = valueARGB; 
    }

	/** Sets the color, ignores null pixel. */
    public void setColor(Pixel p) {
	    if (p == null) return;
	    setARGB(p.valueARGB);
	}
	
	/**
	 * Sets an attribute of the pixel
	 * @param valueAlpha the pixel's ALPHA value
	 */               	
    public void setAlpha(int valueAlpha) { 
    	valueARGB = (valueARGB & 0x00ffffff) | ((valueAlpha & 0xff) << 24);
    }

	/**
	 * Sets an attribute of the pixel
	 * @param valueRed the pixel's RED value
	 */               
	public void setRed(int valueRed) { 
		valueARGB = (valueARGB & 0xff00ffff) | ((valueRed & 0xff) << 16);
	}

	/**
	 * Sets an attribute of the pixel
	 * @param valueGreen the pixel's GREEN value
	 */               
	public void setGreen(int valueGreen) { 
		valueARGB = (valueARGB & 0xffff00ff) | ((valueGreen & 0xff) << 8);
	}

	/**
	 * Sets an attribute of the pixel
	 * @param valueBlue the pixel's BLUE value
	 */               
	public void setBlue(int valueBlue) { 
		valueARGB = (valueARGB & 0xffffff00) | ((valueBlue & 0xff));
	}

	/**
	 * Object's toString() method redefinition
	 */               
    public String toString() {
        return new String("(R-" + getRed() + 
                          " G-" + getGreen() + 
                          " B-" + getBlue() + 
                          " A-" + getAlpha() + 
                          ")");
    }

	//Temp/Will see if keeping    
    /**
     * Convert pixel to Color
     * @return color value
     */
    public Color toColor() {
		return new Color((float)getRed() / 255.0F, 
		             	 (float)getGreen() / 255.0F,
		             	 (float)getBlue() / 255.0F);    	
    }
    
    /* 
     * Compute if two colors are the same, based on their ARGB values.
     */ 
    public boolean equals(Object o) {
    	if (o instanceof Pixel) {
    		return (((Pixel)o).getARGB() == getARGB());
    	}
    	return false;
    }
    
    public boolean equals(Object o, float rangeH, float rangeS, float rangeV)
    {
    	rangeH=rangeH*(255/180);
    	rangeS/=255;
    	rangeV/=255;
    	Pixel p = (Pixel)o;
    	double myHue = HSVByRGB(getRed(),getGreen(),getBlue(),"hue");
    	double mySat = HSVByRGB(getRed(),getGreen(),getBlue(),"sat");
    	double myVal = HSVByRGB(getRed(),getGreen(),getBlue(),"val");
    	
    	double wantedHue = HSVByRGB(p.getRed(),p.getGreen(),p.getBlue(),"hue");
    	double wantedSat = HSVByRGB(p.getRed(),p.getGreen(),p.getBlue(),"sat");
    	double wantedVal = HSVByRGB(p.getRed(),p.getGreen(),p.getBlue(),"val");
    	
    	//System.out.println("my :"+myHue+" "+mySat+" "+myVal);
    	//System.out.println("wanted :"+ wantedHue+" "+wantedSat+" "+wantedVal);
    	//System.out.println("range :"+rangeH+" "+rangeS+" "+rangeV);
    	
    	
		
    	if (o instanceof Pixel){
    		if (myHue>=wantedHue-rangeH && myHue <=wantedHue+rangeH && mySat>=wantedSat-rangeS && mySat <=wantedSat+rangeS && myVal>=wantedVal-rangeV && myVal <=wantedVal+rangeV) {
    			return true;
    		}
    	}
    	return false;
    }
    
public double HSVByRGB(int red,int green,int blue,String color) {
		
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
}