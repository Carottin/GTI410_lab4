package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

public class CMYKColorMediator  extends Object implements SliderObserver, ObserverIF  {
	
	// Color slider
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;
	
	// RGB color
	int red;
	int green;
	int blue;
	
	// Check if cmyk sliders are updated
	boolean cmykUpdate = false;
	
	// CMYK coolor
	double cyan;
	double magenta;
	double yellow;
	double black;
	
	// Buffered image
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	
	// Images size
	int imagesWidth;
	int imagesHeight;
	
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		
		// Initialize size and colors
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.red = result.getPixel().getRed();
		this.green = result.getPixel().getGreen();
		this.blue = result.getPixel().getBlue();
		
		this.result = result;
		result.addObserver(this);

		// Create new buffered images for each CMYK color
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		
		// Compute each image for each color
		computeCyanImage(red, green, blue);
		computeMagentaImage(red, green, blue);
		computeYellowImage(red, green, blue); 	
		computeBlackImage(red, green, blue); 	
	}
	
	// Conversion from RGB to CMYK
	public double rgbToCmyk (int red, int green, int blue, String color) {
		double R = red/255.0;
		double G = green/255.0;
		double B = blue/255.0;
		double K;
		double C;
		double M;
		double Y;
		

		
		K = 1-Math.max(R,Math.max(G, B));		
		if(K!=1)
		{
			C = (1-R-K) / (1-K);
			M = (1-G-K) / (1-K);
			Y = (1-B-K) / (1-K);
		}
		else{
			C = 1 - R;
			M = 1 - G;
			Y = 1 - B;
		}
		
		if(R==0 && G==0 && B==0){
			C=0;
			M=0;
			Y=0;
			K=1;
		}
		
		if (color=="cyan") return C;
		if (color=="magenta") return M;
		if (color=="yellow") return Y;
		if (color=="black") return K;
		
		return 0;		
	}
	
	// Conversion from CMYK to RGB
	public int cmykToRgb (double cyan, double magenta, double yellow, double black, String color) {
		int R;
		int G;
		int B;
		if (black != 255)
		{
			R = (int)(255 * (1-cyan) * (1-black));
			G = (int)(255 * (1-magenta) * (1-black));
			B = (int)(255 * (1-yellow) * (1-black));

		}
		else {
            R = (int)(255 - cyan);
            G = (int)(255 - magenta);
            B = (int)(255 - yellow);
		}	
        
		
		if (color=="red")return R;
		if (color=="green")return G;
		if (color=="blue")return B;
		return 0;		
	}
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		cmykUpdate = true; // CMYK sliders are moving
		
		boolean updateCyan= false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
		boolean updateBlack = false;
		
		if (s == cyanCS && v != cyan) {
			cyan = v/255.0;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == magentaCS && v != magenta) {
			magenta = v/255.0;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == yellowCS && v != yellow) {
			yellow = v/255.0;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == blackCS && v != black) {
			black = v/255.0;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		
		if (updateCyan) {
			computeCyanImage(red, green, blue);
			cyanCS.setValue((int)(cyan*255));
		}
		if (updateMagenta) {
			computeMagentaImage(red, green, blue);
			magentaCS.setValue((int)(magenta*255));
		}
		if (updateYellow) {
			computeYellowImage(red, green, blue);
			yellowCS.setValue((int)(yellow*255));
		}
		if (updateBlack) {
			computeBlackImage(red, green, blue);
			blackCS.setValue((int)(black*255));
		}
		
		Pixel pixel = new Pixel(red, green, blue, 255);
		result.setPixel(pixel);
		
		cmykUpdate = false; // CMYK sliders are not moving anymore

	}
	
	public void computeCyanImage(int red, int green, int blue) { 	
		Pixel p = new Pixel(red, green, blue, 255); 

		// Cursor RGB
		if(cmykUpdate == false){
			cyan = rgbToCmyk(red, green, blue, "cyan");
			magenta = rgbToCmyk(red, green, blue, "magenta");
			yellow = rgbToCmyk(red, green, blue, "yellow");
			black = rgbToCmyk(red, green, blue, "black");
		}
		
		// not cursor RGB
		this.red = cmykToRgb(cyan, magenta, yellow, black, "red");
		this.green = cmykToRgb(cyan, magenta, yellow, black, "green");
		this.blue = cmykToRgb(cyan, magenta, yellow, black, "blue");
		
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(cmykToRgb((double)i/imagesWidth, magenta, yellow, black, "red"));
			p.setGreen(cmykToRgb((double)i/imagesWidth, magenta, yellow, black, "green"));
			p.setBlue(cmykToRgb((double)i/imagesWidth, magenta, yellow, black, "blue"));
			
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cyanImage.setRGB(i, j, rgb);
			}
		}
		if (cyanCS != null) {
			cyanCS.update(cyanImage);
		}
		
	}
	
	public void computeMagentaImage(int red, int green, int blue) { 
		Pixel p = new Pixel(red, green, blue, 255); 
		if(cmykUpdate == false)
		{	cyan = rgbToCmyk(red, green, blue, "cyan");
			magenta = rgbToCmyk(red, green, blue, "magenta");
			yellow = rgbToCmyk(red, green, blue, "yellow");
			black = rgbToCmyk(red, green, blue, "black");
		}
		
		this.red = cmykToRgb(cyan, magenta, yellow, black, "red");
		this.green = cmykToRgb(cyan, magenta, yellow, black, "green");
		this.blue = cmykToRgb(cyan, magenta, yellow, black, "blue");
		
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(cmykToRgb(cyan, (double)i/imagesWidth, yellow, black, "red"));
			p.setGreen(cmykToRgb(cyan, (double)i/imagesWidth, yellow, black, "green"));
			p.setBlue(cmykToRgb(cyan, (double)i/imagesWidth, yellow, black, "blue"));
			
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				magentaImage.setRGB(i, j, rgb);
			}
		}
		if (magentaCS != null) {
			magentaCS.update(magentaImage);
		}
	}
	
	public void computeYellowImage(int red, int green, int blue) { 
		Pixel p = new Pixel(red, green, blue, 255); 
		if(cmykUpdate == false)
		{	cyan = rgbToCmyk(red, green, blue, "cyan");
			magenta = rgbToCmyk(red, green, blue, "magenta");
			yellow = rgbToCmyk(red, green, blue, "yellow");
			black = rgbToCmyk(red, green, blue, "black");
		}
		
		this.red = cmykToRgb(cyan, magenta, yellow, black, "red");
		this.green = cmykToRgb(cyan, magenta, yellow, black, "green");
		this.blue = cmykToRgb(cyan, magenta, yellow, black, "blue");
		
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(cmykToRgb(cyan, magenta, (double)i/imagesWidth, black, "red"));
			p.setGreen(cmykToRgb(cyan, magenta, (double)i/imagesWidth, black, "green"));
			p.setBlue(cmykToRgb(cyan, magenta, (double)i/imagesWidth, black, "blue"));
			
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yellowImage.setRGB(i, j, rgb);
			}
		}
		if (yellowCS != null) {
			yellowCS.update(yellowImage);
		}
	}
	
	public void computeBlackImage(int red, int green, int blue) { 
		Pixel p = new Pixel(red, green, blue, 255); 
		if(cmykUpdate == false){
			cyan = rgbToCmyk(red, green, blue, "cyan");
			magenta = rgbToCmyk(red, green, blue, "magenta");
			yellow = rgbToCmyk(red, green, blue, "yellow");
			black = rgbToCmyk(red, green, blue, "black");
		}
		
		this.red = cmykToRgb(cyan, magenta, yellow, black, "red");
		this.green = cmykToRgb(cyan, magenta, yellow, black, "green");
		this.blue = cmykToRgb(cyan, magenta, yellow, black, "blue");
		
		for (int i = 0; i<imagesWidth; ++i) {
			p.setRed(cmykToRgb(cyan, magenta, yellow, (double)i/imagesWidth, "red"));
			p.setGreen(cmykToRgb(cyan, magenta, yellow, (double)i/imagesWidth, "green"));
			p.setBlue(cmykToRgb(cyan, magenta, yellow, (double)i/imagesWidth, "blue"));

			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				blackImage.setRGB(i, j, rgb);
			}
		}
		if (blackCS != null) {
			blackCS.update(blackImage);
		}
	}

	
	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}
	
	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}
	
	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
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
	public double getCyan() {
		return cyan;
	}
	/**
	 * @return
	 */
	public double getMagenta() {
		return magenta;
	}
	/**
	 * @return
	 */
	public double getYellow() {
		return yellow;
	}
	/**
	 * @return
	 */
	public double getBlack() {
		return black;
	}



	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = new Pixel(red, green, blue, 255);
		
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		red = result.getPixel().getRed();
		green = result.getPixel().getGreen();
		blue = result.getPixel().getBlue();
		
		cyanCS.setValue((int)(rgbToCmyk(red, green, blue, "cyan")*255));
		magentaCS.setValue((int)(rgbToCmyk(red, green, blue, "magenta")*255));
		yellowCS.setValue((int)(rgbToCmyk(red, green, blue, "yellow")*255));
		blackCS.setValue((int)(rgbToCmyk(red, green, blue, "black")*255));
		
		computeCyanImage(red, green, blue);
		computeMagentaImage(red, green, blue);
		computeYellowImage(red, green, blue);
		computeBlackImage(red, green, blue);
	
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}
