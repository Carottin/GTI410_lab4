package model;

import java.awt.Point;
import java.util.List;

public class BsplineCurveType extends CurveType{

	public BsplineCurveType(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see model.CurveType#getNumberOfSegments(int)
	 */
	public int getNumberOfSegments(int numberOfControlPoints) {
		if (numberOfControlPoints >= 3) {
			return (numberOfControlPoints - 3);
		} else {
			return 0;
		}
	}

	/* (non-Javadoc)
	 * @see model.CurveType#getNumberOfControlPointsPerSegment()
	 */
	public int getNumberOfControlPointsPerSegment() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see model.CurveType#getControlPoint(java.util.List, int, int)
	 */
	public ControlPoint getControlPoint( List controlPoints,int segmentNumber, int controlPointNumber) {
		if(segmentNumber==0){
			return (ControlPoint)controlPoints.get(controlPointNumber);
			
		}
		else {
			return (ControlPoint)controlPoints.get(segmentNumber+controlPointNumber);
		}
		/*int controlPointIndex = segmentNumber +4 - controlPointNumber -1;
		return (ControlPoint)controlPoints.get(controlPointIndex);*/
	}

	/* (non-Javadoc)
	 * @see model.CurveType#evalCurveAt(java.util.List, double)
	 */
	public Point evalCurveAt(List controlPoints, double t) {
		//System.out.println(controlPoints);
		List tVector = Matrix.buildRowVector4(Math.pow(t, 3), Math.pow(t, 2), Math.pow(t, 1), 1);
		List gVector = Matrix.buildColumnVector4(((ControlPoint)controlPoints.get(0)).getCenter(), 
			((ControlPoint)controlPoints.get(1)).getCenter(), 
			((ControlPoint)controlPoints.get(2)).getCenter(),
			((ControlPoint)controlPoints.get(3)).getCenter());
		Point p = Matrix.eval(tVector, matrix, gVector);
		//p.setLocation(-p.getX(),-p.getY());
		System.out.println(p);
		return p;
	}

	private List bezierMatrix = 
		Matrix.buildMatrix4(-1*1/6,  3*1/6, -3*1/6, 1*1/6, 
							 3*1/6, -6*1/6,  3*1/6, 0, 
							-3*1/6,  0	  ,  3*1/6, 0, 
							 1*1/6,  4*1/6,  1*1/6, 0);
							 
	private List matrix = bezierMatrix;
}
