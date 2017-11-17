package controller;

import model.*;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

public class MySeedFill extends AbstractTransformer{
	
	


	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = false;
	private boolean boundaryFill = false;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
	public MySeedFill() {
	}
	
	public int getID() { return ID_FLOODER; } 
	
	private void fill(Point ptClicked){
		Stack stack = new Stack(); // Création de la pile
		stack.push(ptClicked); // on met le point cliqué dans la pile
		
		while (!stack.empty()) { // tant que la pile n'est pas vide
			Point current = (Point)stack.pop(); // on récupère le point du dessus de lapile
			if (0 <= current.x && current.x < currentImage.getImageWidth() &&
				!currentImage.getPixel(current.x, current.y).equals(fillColor)) {
				currentImage.setPixel(current.x, current.y, fillColor);
				
				// Next points to fill.
				Point nextLeft = new Point(current.x-1, current.y);
				Point nextRight = new Point(current.x+1, current.y);
				Point nextBottom = new Point(current.x, current.y-1);
				Point nextTop = new Point(current.x, current.y+1);
				stack.push(nextLeft);
				stack.push(nextRight);
				stack.push(nextBottom);
				stack.push(nextTop);
			}
		}
	}
	
	// Récupère le pixel clické et applique la méthode fill dessus
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public ImageX getCurrentImage() {
		return currentImage;
	}

	public void setCurrentImage(ImageX currentImage) {
		this.currentImage = currentImage;
	}

	public int getCurrentImageWidth() {
		return currentImageWidth;
	}

	public void setCurrentImageWidth(int currentImageWidth) {
		this.currentImageWidth = currentImageWidth;
	}

	public Pixel getFillColor() {
		return fillColor;
	}

	public void setFillColor(Pixel fillColor) {
		this.fillColor = fillColor;
	}

	public Pixel getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Pixel borderColor) {
		this.borderColor = borderColor;
	}

	public boolean isFloodFill() {
		return floodFill;
	}

	public void setFloodFill(boolean floodFill) {
		this.floodFill = floodFill;
	}

	public boolean isBoundaryFill() {
		return boundaryFill;
	}

	public void setBoundaryFill(boolean boundaryFill) {
		this.boundaryFill = boundaryFill;
	}

	public int getHueThreshold() {
		return hueThreshold;
	}

	public void setHueThreshold(int hueThreshold) {
		this.hueThreshold = hueThreshold;
	}

	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	public void setSaturationThreshold(int saturationThreshold) {
		this.saturationThreshold = saturationThreshold;
	}

	public int getValueThreshold() {
		return valueThreshold;
	}

	public void setValueThreshold(int valueThreshold) {
		this.valueThreshold = valueThreshold;
	}
	

}
