package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class Normalize0To255 extends ImageConversionStrategy  {

	@Override
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		PixelDouble highestRGBPixel = new PixelDouble(); // Highest RGB value pixel
		PixelDouble lowestRGBPixel = new PixelDouble(255,255,255,1); // Highest RGB value pixel
		
		// Values to normalize pixels
		double transformRedValue = 0; 
		double transformGreenValue = 0; 
		double transformBlueValue = 0; 
		
		newImage.beginPixelUpdate();
		// Remove negative values		
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				
				// Get highest RGB value pixel
				if(curPixelDouble.getRed() + curPixelDouble.getGreen() + curPixelDouble.getBlue()
				   > highestRGBPixel.getRed() + highestRGBPixel.getGreen() + highestRGBPixel.getBlue()){
					highestRGBPixel = curPixelDouble;
				}
				// Get highest RGB value pixel
				if(curPixelDouble.getRed() + curPixelDouble.getGreen() + curPixelDouble.getBlue()
				   < highestRGBPixel.getRed() + highestRGBPixel.getGreen() + highestRGBPixel.getBlue()){
					lowestRGBPixel = curPixelDouble;
				}
				
			}		
		}
		// get the transform values
		transformRedValue = highestRGBPixel.getRed()/255;
		transformGreenValue = highestRGBPixel.getGreen()/255;
		transformBlueValue = highestRGBPixel.getBlue()/255;

		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				newImage.setPixel(x, y, new Pixel((int)(curPixelDouble.getRed()*transformRedValue - lowestRGBPixel.getRed()),
												  (int)(curPixelDouble.getGreen()*transformGreenValue - lowestRGBPixel.getGreen()),
												  (int)(curPixelDouble.getBlue()*transformBlueValue - lowestRGBPixel.getBlue())));
			}		
		}
		
		newImage.endPixelUpdate();
		return newImage;
	}

}