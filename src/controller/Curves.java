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

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;

import view.Application;
import view.CurvesPanel;

import model.BezierCurveType;
import model.BsplineCurveType;
import model.ControlPoint;
import model.Curve;
import model.CurvesModel;
import model.DocObserver;
import model.Document;
import model.HermiteCurveType;
import model.PolylineCurveType;
import model.Shape;

/**
 * <p>Title: Curves</p>
 * <p>Description: (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 Sébastien Bois, Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.9 $
 */
public class Curves extends AbstractTransformer implements DocObserver {
		
	/**
	 * Default constructor
	 */
	public Curves() {
		Application.getInstance().getActiveDocument().addObserver(this);
	}	

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_CURVES; }
	
	public void activate() {
		firstPoint = true;
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		boolean selectionIsACurve = false; 
		if (selectedObjects.size() > 0){
			Shape s = (Shape)selectedObjects.get(0);
			if (s instanceof Curve){
				curve = (Curve)s;
				firstPoint = false;
				cp.setCurveType(curve.getCurveType());
				cp.setNumberOfSections(curve.getNumberOfSections());
			}
			else if (s instanceof ControlPoint){
				curve = (Curve)s.getContainer();
				firstPoint = false;
			}
		}
		
		if (firstPoint) {
			// First point means that we will have the first point of a new curve.
			// That new curve has to be constructed.
			curve = new Curve(100,100);
			setCurveType(cp.getCurveType());
			setNumberOfSections(cp.getNumberOfSections());
		}
	}
    
	/**
	 * 
	 */
	protected boolean mouseReleased(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();

		if (firstPoint) {
			firstPoint = false;
			Document doc = Application.getInstance().getActiveDocument();
			doc.addObject(curve);
		}
		ControlPoint cp = new ControlPoint(mouseX, mouseY);
		curve.addPoint(cp);
				
		return true;
	}

	/**
	 * @param string
	 */
	public void setCurveType(String string) {
		if (string == CurvesModel.BEZIER) {
			curve.setCurveType(new BezierCurveType(CurvesModel.BEZIER));
		} else if (string == CurvesModel.LINEAR) {
			curve.setCurveType(new PolylineCurveType(CurvesModel.LINEAR));
		} else if (string == CurvesModel.HERMITE) {
			curve.setCurveType(new HermiteCurveType(CurvesModel.HERMITE));
		}else if (string == CurvesModel.BSPLINE) {
			curve.setCurveType(new BsplineCurveType(CurvesModel.BSPLINE));
		}
		else {
			System.out.println("Curve type [" + string + "] is unknown.");
		}
	}
	/* Alignement de type G1*/
	public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					if (curve.getCurveType()== "Hermite"|| curve.getCurveType()=="Bezier")
					{
						ControlPoint cp = new ControlPoint(0,0); 	//Point a traiter
						ControlPoint cpPlus = new ControlPoint(0,0);	// Point definissant le vecteur sortant
						ControlPoint cpMoins = new ControlPoint(0,0);	//point definissant le vecteur entrant
						cpMoins = (ControlPoint)curve.getShapes().get(controlPointIndex-1);
						cp = (ControlPoint)curve.getShapes().get(controlPointIndex);
						cpPlus = (ControlPoint)curve.getShapes().get(controlPointIndex+1);
						/*****
						 * Si le point est une untersection de courbes
						 * 
						 * Utilisation des extreme des tangentes pour tracer une courbes virtuelle
						 * Positionnement du point en question sur le point de cette courbe le plus proche de
						 * sa position initiale
						 */
						if (controlPointIndex %3 == 0)
						{
							double sx1 = cpMoins.getCenter().x;
							double sy1 = cpMoins.getCenter().y;
							double sx2 = cpPlus.getCenter().x;
							double sy2 = cpPlus.getCenter().y;
							double px = cp.getCenter().x;
							double py = cp.getCenter().y;
						    double xDelta = sx2 - sx1;
						    double yDelta = sy2 - sy1;
						    double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

						    if (u < 0)
						    {
						    	cp.getCenter().setLocation(sx1, sy1);
						    }
						    else if (u > 1)
						    {
						    	cp.getCenter().setLocation(sx2, sy2);
						    }
						    else
						    {
						    	cp.getCenter().setLocation((sx1 + u * xDelta), (sy1 + u * yDelta));
						    }						
						    curve.getShapes().set(controlPointIndex, cp);
							curve.update(); //Mise a jour de la courbe en graphique
						}
						
					}
				}
			}
			
		}
	}

	/* Alignement de type C1*/
	public void symetricControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int controlPointIndex = curve.getShapes().indexOf(s);
					if (curve.getCurveType()== "Hermite"|| curve.getCurveType()=="Bezier")
					{
						ControlPoint cp = new ControlPoint(0,0);		//Point a traiter
						ControlPoint cpPlus = new ControlPoint(0,0);	// Point definissant le vecteur sortant
						ControlPoint cpMoins = new ControlPoint(0,0); 	//point definissant le vecteur entrant
						cpMoins = (ControlPoint)curve.getShapes().get(controlPointIndex-1);
						cp = (ControlPoint)curve.getShapes().get(controlPointIndex);
						cpPlus = (ControlPoint)curve.getShapes().get(controlPointIndex+1);
						/*****
						 * Si le point est une untersection de courbes
						 * 
						 * Utilisation des extreme des tangentes pour tracer une courbes virtuelle
						 * Positionnement du point en question au millieu de cette courbe
						 */
						if (controlPointIndex %3 == 0)
						{
							double vectPreX= cp.getCenter().getX()-cpMoins.getCenter().getX();
							double vectPreY= cp.getCenter().getY()-cpMoins.getCenter().getY();
							double vectNextX= cpPlus.getCenter().getX()-cp.getCenter().getX();
							double vectNextY= cpPlus.getCenter().getY()-cp.getCenter().getY();
							if (!((vectPreX == vectNextX)&&(vectPreY == vectNextY))||((vectPreX == -vectNextX)&&(vectPreY == -vectNextY)))
							{
								cp.getCenter().setLocation((cpPlus.getCenter().getX()+cpMoins.getCenter().getX())/2, (cpPlus.getCenter().getY()+cpMoins.getCenter().getY())/2);
								curve.getShapes().set(controlPointIndex, cp);
								curve.update(); //Mise a jour graphique
							}
						}
						
					}
				}
			}
			
		}
	}

	public void setNumberOfSections(int n) {
		curve.setNumberOfSections(n);
	}
	
	public int getNumberOfSections() {
		if (curve != null)
			return curve.getNumberOfSections();
		else
			return Curve.DEFAULT_NUMBER_OF_SECTIONS;
	}
	
	public void setCurvesPanel(CurvesPanel cp) {
		this.cp = cp;
	}
	
	/* (non-Javadoc)
	 * @see model.DocObserver#docChanged()
	 */
	public void docChanged() {
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docSelectionChanged()
	 */
	public void docSelectionChanged() {
		activate();
	}

	private boolean firstPoint = false;
	private Curve curve;
	private CurvesPanel cp;
}
