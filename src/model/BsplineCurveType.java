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
		System.out.println("coucou");
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
	public ControlPoint getControlPoint(
		List controlPoints,
		int segmentNumber,
		int controlPointNumber) 
	{
		int controlPointIndex = segmentNumber * 3 + controlPointNumber;
		return (ControlPoint)controlPoints.get(controlPointIndex);
	}

	/* (non-Javadoc)
	 * @see model.CurveType#evalCurveAt(java.util.List, double)
	 */
	public Point evalCurveAt(List controlPoints, double t) {
		List tVector = Matrix.buildRowVector4(t*t*t, t*t, t, 1);
		List gVector = Matrix.buildColumnVector4(((ControlPoint)controlPoints.get(0)).getCenter(), 
			((ControlPoint)controlPoints.get(1)).getCenter(), 
			((ControlPoint)controlPoints.get(2)).getCenter(),
			((ControlPoint)controlPoints.get(3)).getCenter());
		Point p = Matrix.eval(tVector, matrix, gVector);
		return p;
	}

	private List bezierMatrix = 
		Matrix.buildMatrix4(-1,  3, -3, 1, 
							 3, -6,  3, 0, 
							-3,  0,  3, 0, 
							 1,  4,  1, 0);
							 
	private List matrix = bezierMatrix;
}
