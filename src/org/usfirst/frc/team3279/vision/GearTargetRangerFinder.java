package org.usfirst.frc.team3279.vision;

import java.util.Iterator;
import java.util.TreeMap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

public class GearTargetRangerFinder extends RangeFinder {
	GripPipeline pipeline;
	
	public GearTargetRangerFinder() {
		super();
		pipeline = new GripPipeline();
	}
	
	public void processRangeFinding(Mat m_image) {
		double targetRectagleWidthActualInches = 10.25;
		double tapeHeightActualInches = 5;
		double turnMargin = 1;
		
		pipeline.process(m_image);
		System.out.println("Image results" + pipeline.filterContoursOutput()); // Change
																				// from
																				// pipeline
		TreeMap<Integer, Rect> points = new TreeMap<Integer, Rect>();
		if (pipeline.filterContoursOutput().size() > 0) {
			Iterator<MatOfPoint> itMat = pipeline.filterContoursOutput().iterator();
			while (itMat.hasNext()) {
				MatOfPoint point = itMat.next();
				Rect r = Imgproc.boundingRect(point);
				double tapeRatio = ((double) r.width)/r.height;
				System.out.println("Rectagle " + r + " ratio " + tapeRatio);
				// if (Math.abs(tapeRatio - realTapeRatio) <= tapeRationMargin) {
					points.put(r.x, r);
				// }
			}
			if (points.size() > 2) {
				System.out.println("Need Special Logic to handle multiple pieces of tape");
				return;
			}
			if (points.size() == 2) {
				System.out.println("Found 2 pieces of tape " + points);
				Rect rectangle[] = new Rect[2];
				int i = 0;
				for (Rect point : points.values()) {
					rectangle[i++] = point;
				}
				int topWidth = rectangle[1].x - rectangle[0].x;
				distance = Math.abs((targetRectagleWidthActualInches* (m_image.width()/2))/(2*topWidth*Math.tan(halfFOV)));

				leftHeight = Math.abs((tapeHeightActualInches* (m_image.height()/2))/(2*rectangle[0].height*Math.tan(halfFOV)));
				rightHeight = Math.abs((tapeHeightActualInches* (m_image.height()/2))/(2*rectangle[1].height*Math.tan(halfFOV)));
				double turnValue = leftHeight - rightHeight;
				if (Math.abs(turnValue) > turnMargin) {
					if (turnValue > 0) {
						this.directionToTurn = DirectionToTurn.Right;
					}
					else {
						this.directionToTurn = DirectionToTurn.Left;
					}
				}
				else {
					this.directionToTurn = DirectionToTurn.Straight;
				}
				System.out.println(" halfFOV " + halfFOV + " tan " + Math.tan(halfFOV));
				System.out.println("leftHeight " + leftHeight + " rightHeight " + rightHeight + " topWidth " + topWidth + " image width " + m_image.width() + " distance " + distance + " Turn " + directionToTurn);
			} 
		}

	}
	
}
