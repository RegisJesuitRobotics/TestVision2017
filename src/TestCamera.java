import java.util.Iterator;
import java.util.TreeMap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CameraServerJNI;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.UsbCameraInfo;

public class TestCamera {

	public static void main(String[] args) {
		for (UsbCameraInfo camera : CameraServerJNI.enumerateUsbCameras()) {
			System.out.println("camera " + camera.name + " " + camera.dev);
		}
		UsbCamera camera = new UsbCamera("Test", 0);
		camera.setResolution(640, 480);
		System.out.println("Camer " + camera + " connected " + camera.isConnected());
		GripPipeline pipeline = new GripPipeline();
		CvSink m_cvSink = new CvSink("VisionRunner CvSink");
		m_cvSink.setSource(camera);
		Mat m_image = new Mat();
		double realTapeRatio = .4; 
		double tapeRationMargin = .09;
		double targetRatio;
		double realTargetRatio = 2.05;
		double realTargetMargin = .1;
		double camerFOV = 68.5;
		
		double halfFOV = camerFOV/2;
		double targetRectagleActualInches = 10.25;
		
		while (true) {
			long frameTime = m_cvSink.grabFrame(m_image);
			if (frameTime == 0) {
				// There was an error, report it
				String error = m_cvSink.getError();
				System.err.println("frametime error " + error);
			} else {
				pipeline.process(m_image);
				System.out.println("Image results" + pipeline.filterContoursOutput()); // Change
																						// this
																						// for
																						// final
																						// output
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
						if (Math.abs(tapeRatio - realTapeRatio) <= tapeRationMargin) {
							points.put(r.x, r);
						}
					}
					if (points.size() > 2) {
						System.out.println("Need Special Logic to handle multiple pieces of tape");
						continue;
					}
					if (points.size() == 2) {
						System.out.println("Found 2 pieces of tape " + points);
						Rect rectangle[] = new Rect[2];
						int i = 0;
						for (Rect point : points.values()) {
							rectangle[i++] = point;
						}
						int leftHeight = rectangle[0].height;
						int rightHeight = rectangle[1].height;
						int topWidth = rectangle[1].x - rectangle[0].x;
						double distance = Math.abs((targetRectagleActualInches* (m_image.width()/2))/(2*topWidth*Math.tan(halfFOV)));
						System.out.println(" halfFOV " + halfFOV + " tan " + Math.tan(halfFOV));
						System.out.println("leftHeight " + leftHeight + " rightHeight " + rightHeight + " topWidth " + topWidth + " image width " + m_image.width() + " distance " + distance);
					} 
				}
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
