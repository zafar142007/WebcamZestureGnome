package com.zafar.core;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.sarxos.webcam.Webcam;

public class EnterTheZesture {
	
	private LinkedBlockingQueue<BufferedImage> video = new LinkedBlockingQueue<BufferedImage>();

	private GestureRecogniser recogniser = new GestureRecogniser();

	public EnterTheZesture() {
		recogniser.setVideo(video);
	}

	public static void main(String[] args) {
		EnterTheZesture gesture = new EnterTheZesture();
		new Thread(gesture.recogniser).start();
		gesture.readFromCam();
	}

	public void readFromCam() {
		Webcam webcam=null;
		try {
		
			List<Webcam> w = Webcam.getWebcams();
			BufferedImage image;
			System.out.println(w.size() + w.get(0).getName());
			webcam = w.get(0);
			webcam.setViewSize(new Dimension(320, 240));
			webcam.open();
			while(!Thread.interrupted()) {

				image = webcam.getImage();
				video.put(image);
				// System.out.println("put "+i);

			}

			// ImageIO.write(image, "PNG", new File("motion"+i+".png"));


		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			webcam.close();
		}
	}

}
