package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class YCbCrColorMediator  extends Object implements SliderObserver, ObserverIF  {
	
	// Color slider
	ColorSlider yCS;
	ColorSlider cbCS;
	ColorSlider crCS;
	
	// RGB color
	int red;
	int green;
	int blue;
	
	// Check if cmyk sliders are updated
	boolean yCbCrUpdate = false;
	
	// CMYK color
	int y;
	int cb;
	int cr;
	
	// Buffered image
	BufferedImage yImage;
	BufferedImage cbImage;
	BufferedImage crImage;
	
	// Images size
	int imagesWidth;
	int imagesHeight;
	
	ColorDialogResult result;
	
	YCbCrColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		
		// Initialize size and colors
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		if (this.red<0) this.red=0;
		if (this.green<0) this.green=0;
		if (this.blue<0) this.blue=0;
		
		this.result = result;
		result.addObserver(this);

		// Create new buffered images for each CMYK color
		yImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		cbImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		crImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		// Compute each image for each color
		computeYImage(red, green, blue);
		computeCbImage(red, green, blue);
		computeCrImage(red, green, blue); 
	}
	
	// Conversion from RGB to YCbCr
	public int rgbToYCbCr (int red, int green, int blue, String color) {
		int Y = (int)(0.299*red + 0.587*green + 0.114*blue);
		int Cb = (int)(-0.1687*red -0.3313*green + 0.5*blue + 128);
		int Cr = (int)(0.5*red - 0.4187*green - 0.0813*blue + 128);
		
		
		if(Y<0)Y=0;
		if(Y>255)Y=255;
		if(Cb<0)Cb=0;
		if(Cb>255)Cb=255;
		if(Cr<0)Cr=0;
		if(Cr>255)Cr=255;
	
		
		if (color=="y") return Y;
		if (color=="cb") return Cb;
		if (color=="cr") return Cr;
		
		return 0;		
	}
	
	// Conversion from CMYK to RGB
	public int yCbCrToRgb (int y, int cb, int cr, String color) {

		int R;
		int G;
		int B;
		
		
		
		R = (int) (y + 1.402*(cr-128));
		G = (int) (y - 0.34414*(cb-128) - 0.71414*(cr-128));
		B = (int) (y + 1.772*(cb-128));
		
		if(R<0)R=0;
		if(R>255)R=255;
		if(B<0)B=0;
		if(B>255)B=255;
		if(G<0)G=0;
		if(G>255)G=255; 
		
		
		if (color=="red")return R;
		if (color=="green")return G;
		if (color=="blue")return B;
		return 0;		
	}
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		yCbCrUpdate = true; // CMYK sliders are moving
		
		boolean updateY= false;
		boolean updateCb = false;
		boolean updateCr = false;
		
		if (s == yCS && v != y) {
			y = v;
			updateCb = true;
			updateCr = true;
		}
		if (s == cbCS && v != cb) {
			cb = v;
			updateY = true;
			updateCr = true;
		}
		if (s == crCS && v != cr) {
			cr = v;
			updateY = true;
			updateCb = true;
		}
		
		if (updateY) {
			computeYImage(red, green, blue);
			yCS.setValue((int)y);
		}
		if (updateCb) {
			computeCbImage(red, green, blue);
			cbCS.setValue((int)cb);
		}
		if (updateCr) {
			computeCrImage(red, green, blue);
			crCS.setValue((int)cr);
		}
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
		
		yCbCrUpdate = false;

	}
	
	public void computeYImage(int red, int green, int blue) { 	

		
		
		if(yCbCrUpdate==false){
			y = rgbToYCbCr(red, green, blue, "y");
			cb = rgbToYCbCr(red, green, blue, "cb");
			cr = rgbToYCbCr(red, green, blue, "cr");
		}
		
		this.red = yCbCrToRgb(y, cb, cr, "red");
		this.green = yCbCrToRgb(y, cb, cr, "green");
		this.blue = yCbCrToRgb(y, cb, cr, "blue");
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(yCbCrToRgb((int)(((double)i / (double)imagesWidth)*255.0), cb, cr, "red"));
			p.setGreen(yCbCrToRgb((int)(((double)i / (double)imagesWidth)*255.0), cb, cr, "green"));
			p.setBlue(yCbCrToRgb((int)(((double)i / (double)imagesWidth)*255.0), cb, cr, "blue"));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yImage.setRGB(i, j, rgb);
			}
		}
		if (yCS != null) {
			yCS.update(yImage);
		}
		
	}

	public void computeCbImage(int red, int green, int blue) { 	
		
		if(yCbCrUpdate==false){
			y = rgbToYCbCr(red, green, blue, "y");
			cb = rgbToYCbCr(red, green, blue, "cb");
			cr = rgbToYCbCr(red, green, blue, "cr");
		}
		
		
		this.red = yCbCrToRgb(y, cb, cr, "red");
		this.green = yCbCrToRgb(y, cb, cr, "green");
		this.blue = yCbCrToRgb(y, cb, cr, "blue");
		
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(yCbCrToRgb(y, (int)(((double)i / (double)imagesWidth)*255.0), cr, "red"));
			p.setGreen(yCbCrToRgb(y, (int)(((double)i / (double)imagesWidth)*255.0), cr, "green"));
			p.setBlue(yCbCrToRgb(y, (int)(((double)i / (double)imagesWidth)*255.0), cr, "blue"));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cbImage.setRGB(i, j, rgb);
			}
		}
		if (cbCS != null) {
			cbCS.update(cbImage);
		}
		
	}
	
	public void computeCrImage(int red, int green, int blue) { 	
		
		if(yCbCrUpdate==false){
			y = rgbToYCbCr(red, green, blue, "y");
			cb = rgbToYCbCr(red, green, blue, "cb");
			cr = rgbToYCbCr(red, green, blue, "cr");
		}
		
		
		this.red = yCbCrToRgb(y, cb, cr, "red");
		this.green = yCbCrToRgb(y, cb, cr, "green");
		this.blue = yCbCrToRgb(y, cb, cr, "blue");
		
		Pixel p = new Pixel(red, green, blue, 255); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(yCbCrToRgb(y, cb , (int)(((double)i / (double)imagesWidth)*255.0), "red"));
			p.setGreen(yCbCrToRgb(y, cb , (int)(((double)i / (double)imagesWidth)*255.0), "green"));
			p.setBlue(yCbCrToRgb(y, cb , (int)(((double)i / (double)imagesWidth)*255.0), "blue"));
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				crImage.setRGB(i, j, rgb);
			}
		}
		if (crCS != null) {
			crCS.update(crImage);
		}
	}


	
	/**
	 * @return
	 */
	public BufferedImage getYImage() {
		return yImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCbImage() {
		return cbImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCrImage() {
		return crImage;
	}


	/**
	 * @param slider
	 */
	public void setYCS(ColorSlider slider) {
		yCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setCbCS(ColorSlider slider) {
		cbCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setCrCS(ColorSlider slider) {
		crCS = slider;
		slider.addObserver(this);
	}

	
	/**
	 * @return
	 */
	public double getBlue() {
		return blue;
	}

	/**
	 * @return
	 */
	public double getGreen() {
		return green;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}
	/**
	 * @return
	 */
	public double getCb() {
		return cb;
	}
	/**
	 * @return
	 */
	public double getCr() {
		return cr;
	}



	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(red, green, blue, 255);

		
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		
		if (this.red<0) this.red=0;
		if (this.green<0) this.green=0;
		if (this.blue<0) this.blue=0;

		yCS.setValue((int)(rgbToYCbCr(red, green, blue, "y")));
		cbCS.setValue((int)(rgbToYCbCr(red, green, blue, "cb")));
		crCS.setValue((int)(rgbToYCbCr(red, green, blue, "cr")));
		
		computeYImage(red, green, blue);
		computeCbImage(red, green, blue);
		computeCrImage(red, green, blue);
		

		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
