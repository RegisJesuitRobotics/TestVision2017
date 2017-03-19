import org.opencv.core.Mat;
import org.usfirst.frc.team3279.vision.GearTargetRangerFinder;

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
		GearTargetRangerFinder rangeFinder = new GearTargetRangerFinder();
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
				rangeFinder.processRangeFinding(m_image);
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
