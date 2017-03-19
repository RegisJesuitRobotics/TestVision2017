import java.util.Iterator;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
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
		System.out.println("Camer " + camera + " connected " + camera.isConnected());
		GripPipeline pipeline = new GripPipeline();
		CvSink m_cvSink = new CvSink("VisionRunner CvSink");
		m_cvSink.setSource(camera);
		Mat m_image = new Mat();

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
				if (pipeline.filterContoursOutput().size() > 0) {
					Iterator<MatOfPoint> itMat = pipeline.filterContoursOutput().iterator();
					while (itMat.hasNext()) {
						MatOfPoint point = itMat.next();
						Rect r = Imgproc.boundingRect(point);
						float ratio = ((float) r.width)/r.height;
						System.out.println("Rectagle " + r + " ratio " + ratio);
						// System.out.println("point " + point.toArray());
//						Point[] points = point.toArray();
//						for (int i = 0; i < points.length; i++) {
//							System.out.println("x " + points[i].x + "y " + points[i].y);
//						}
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
