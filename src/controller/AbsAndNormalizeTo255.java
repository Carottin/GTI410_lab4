package controller;

import model.ImageDouble;
import model.ImageX;
import model.Pixel;
import model.PixelDouble;

public class AbsAndNormalizeTo255 extends ImageConversionStrategy  {

	@Override
	public ImageX convert(ImageDouble image) {
		int imageWidth = image.getImageWidth();
		int imageHeight = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, imageWidth, imageHeight);
		PixelDouble curPixelDouble = null;

		PixelDouble highestRGBPixel = new PixelDouble(); // Highest RGB value pixel
		
		// Values to normalize pixels
		double transformRedValue = 0; 
		double transformGreenValue = 0; 
		double transformBlueValue = 0; 
		double transformAlphaValue = 0; 
		
		newImage.beginPixelUpdate();
		// Remove negative values		
		for (int x = 0; x < imageWidth; x++) {
			for (int y = 0; y < imageHeight; y++) {
				curPixelDouble = image.getPixel(x,y);
				newImage.setPixel(x, y, new Pixel((int)Math.abs((curPixelDouble.getRed())),
												  (int)Math.abs((curPixelDouble.getGreen())),
												  (int)Math.abs((curPixelDouble.getBlue())),
												  (int)Math.abs((curPixelDouble.getAlpha()))));
				
				// Get highest RGB value pixel
				if(curPixelDouble.getRed() + curPixelDouble.getGreen() + curPixelDouble.getBlue()
				   > highestRGBPixel.getRed() + highestRGBPixel.getGreen() + highestRGBPixel.getBlue()){
					highestRGBPixel = curPixelDouble;
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
				newImage.setPixel(x, y, new Pixel((int)(curPixelDouble.getRed()*transformRedValue),
												  (int)(curPixelDouble.getGreen()*transformGreenValue),
												  (int)(curPixelDouble.getBlue()*transformBlueValue)));
			}		
		}
		
		newImage.endPixelUpdate();
		return newImage;
	}

}