package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class HSVColorMediator extends Object implements SliderObserver, ObserverIF  {

	ColorSlider hueCS;
	ColorSlider satCS;
	ColorSlider valCS;
	
	boolean hsvUpdate = false;
	
	double hue;
	double sat;
	double val;
	
	int red;
	int blue;
	int green;
	
	BufferedImage hueImage;
	BufferedImage satImage;
	BufferedImage valImage;
	
	int imagesWidth;
	int imagesHeight;
	
	ColorDialogResult result;
		
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		
		
		this.result = result;
		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		satImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		
		computeHueImage(red,green,blue); 
		computeSatImage(red,green,blue);
		computeValImage(red,green,blue); 	
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
	
	public int RGBByHSV(double hue, double sat, double val,String color) {
		
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
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		hsvUpdate = true;
		
		boolean updateHue = true;
		boolean updateSat = true;
		boolean updateVal = true;
		
		if (s == hueCS && v != hue) {
			hue = v*359/255;
			updateSat = true;
			updateVal = true;
		}
		if (s == satCS && v != sat) {
			sat = v/255.0;
			updateHue = true;
			updateVal = true;
		}
		if (s == valCS && v != val) {
			val = v/255.0;
			updateHue = true;
			updateSat = true;
		}
		
		if (updateHue) {
			computeHueImage(red,green,blue);
			hueCS.setValue((int)hue*255/359);
		}
		if (updateSat) {
			computeSatImage(red,green,blue);
			satCS.setValue((int)(sat*255));
		}
		if (updateVal) {
			computeValImage(red,green,blue);
			valCS.setValue((int)(val*255));
		}
		
		Pixel pixel = new Pixel(red, green, blue, 255);  //TODO
		result.setPixel(pixel);
		
		hsvUpdate = false;
	}
	

	
	public void computeHueImage(int red,int green, int blue) { 
		
		Pixel p = new Pixel(0,0,0, 255); 
		int i;
	
		
		if(hsvUpdate == false)
		{
			hue=HSVByRGB(red,green,blue,"hue");
			sat=HSVByRGB(red,green,blue,"sat");
			val=HSVByRGB(red,green,blue,"val");
		}
		
		this.red = RGBByHSV(hue, sat, val, "red");
		this.green = RGBByHSV(hue, sat, val, "green");
		this.blue = RGBByHSV(hue, sat, val, "blue");

		
		for (i = 0; i<imagesWidth/6; ++i) {
			
			p.setRed((int)255);
			p.setGreen((int)(((double)i / (double)imagesWidth)*(int)1550));
			p.setBlue(0);
			int rgb=p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}

		for ( i = imagesWidth/6; i<2*imagesWidth/6; ++i) {

			p.setRed((int)(((double)-i / (double)imagesWidth)*1550));
			p.setGreen((int)255);
			p.setBlue(0);
			
			int rgb=p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
			
		for ( i = 2*imagesWidth/6; i<3*imagesWidth/6; ++i) {

			p.setRed(0);
			p.setGreen((int)255);
			p.setBlue((int)(((double)i / (double)imagesWidth)*1550));
			
			int rgb=p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
			
		for ( i = 3*imagesWidth/6; i<4*imagesWidth/6; ++i) {
		
			p.setRed(0);
			p.setGreen((int)(((double)-i / (double)imagesWidth)*1550));
			p.setBlue((int)255);	
			
			int rgb=p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				hueImage.setRGB(i, j, rgb);
			}
		}
			
		for (i = 4*imagesWidth/6; i<5*imagesWidth/6; ++i) {
		
			p.setRed((int)(((double)i / (double)imagesWidth)*1550));
			p.setGreen(0);
			p.setBlue((int)255);	
		
		int rgb=p.getARGB();
		for (int j = 0; j<imagesHeight; ++j) {
			hueImage.setRGB(i, j, rgb);
		}
		}
			
		for (i = 5*imagesWidth/6; i<imagesWidth-1; ++i) {
			p.setRed((int)255);
			p.setGreen(0);
			p.setBlue((int)(((double)-i / (double)imagesWidth)*1550));
		
		int rgb=p.getARGB();
		for (int j = 0; j<imagesHeight; ++j) {
			hueImage.setRGB(i, j, rgb);
		}
		}
				
		
		if (hueCS != null) {
			hueCS.update(hueImage);
		}
	}
	
	public void computeSatImage(int red,int green, int blue) {
		Pixel p = new Pixel(0,0,0, 255); 
		
		if(hsvUpdate == false)
		{
			hue=HSVByRGB(red,green,blue,"hue");
			sat=HSVByRGB(red,green,blue,"sat");
			val=HSVByRGB(red,green,blue,"val");
		}
		
		this.red = RGBByHSV(hue, sat, val, "red");
		this.green = RGBByHSV(hue, sat, val, "green");
		this.blue = RGBByHSV(hue, sat, val, "blue");

		
		for (int i = 0; i<imagesWidth; ++i) {
			
			p.setRed(RGBByHSV(hue,(double)i/imagesWidth,val,"red")); 
			p.setGreen(RGBByHSV(hue,(double)i/imagesWidth,val,"green")); 
			p.setBlue(RGBByHSV(hue,(double)i/imagesWidth,val,"blue")); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				satImage.setRGB(i, j, rgb);
			}
		}
		if (satCS != null) {
			satCS.update(satImage);
		}
	}
	
	public void computeValImage(int red,int green, int blue) { 
		Pixel p = new Pixel(0,0,0, 255);
		
		if(hsvUpdate == false)
		{
			hue=HSVByRGB(red,green,blue,"hue");
			sat=HSVByRGB(red,green,blue,"sat");
			val=HSVByRGB(red,green,blue,"val");
		}
		
		this.red = RGBByHSV(hue, sat, val, "red");
		this.green = RGBByHSV(hue, sat, val, "green");
		this.blue = RGBByHSV(hue, sat, val, "blue");

		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(RGBByHSV(hue,sat,(double)i/imagesWidth,"red")); 
			p.setGreen(RGBByHSV(hue,sat,(double)i/imagesWidth,"green")); 
			p.setBlue(RGBByHSV(hue,sat,(double)i/imagesWidth,"blue")); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				valImage.setRGB(i, j, rgb);
			}
		}
		if (valCS != null) {
			valCS.update(valImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getValImage() {
		return valImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getSatImage() {
		return satImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
	}

	/**
	 * @param slider
	 */
	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setSatCS(ColorSlider slider) {
		satCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setValCS(ColorSlider slider) {
		valCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getVal() {
		return HSVByRGB(red,green,blue,"val");
	}

	/**
	 * @return
	 */
	public double getSat() {
		return HSVByRGB(red,green,blue,"sat");
	}

	/**
	 * @return
	 */
	public double getHue() {
		return HSVByRGB(red,green,blue,"hue");
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is already properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(red, green, blue, 255);
		
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
		
		hueCS.setValue((int)(HSVByRGB(red,green,blue,"hue")*255/359));
		satCS.setValue((int)(HSVByRGB(red,green,blue,"sat")*255));
		valCS.setValue((int)(HSVByRGB(red,green,blue,"val")*255));
	
		computeHueImage(red,green,blue);
		computeSatImage(red,green,blue);
		computeValImage(red,green,blue);

		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
	
