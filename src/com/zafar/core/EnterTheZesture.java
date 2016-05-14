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
		BufferedImage image;
		boolean status=false;
		try {
			List<Webcam> webcams=Webcam.getWebcams();
			for(Webcam cam:webcams){
				try{
					webcam = cam;
					System.out.println("Trying capturing "+webcam.getName());
					
					webcam.setViewSize(new Dimension(320, 240));
					if(status=webcam.open()){
						break;
					}
				}
				catch (Exception e) {
					System.out.println("Your camera "+webcam.getName()+" may be blocked / in use");
					e.printStackTrace();
				}
			}
			if(!status)
				return;
			while(!Thread.interrupted()) {

				image = webcam.getImage();
				video.put(image);
				// System.out.println("put "+i);

			}

			// ImageIO.write(image, "PNG", new File("motion"+i+".png"));


		} catch (Exception e) {
			System.out.println("Some error occurred");
			e.printStackTrace();
		}
		finally{
			webcam.close();
		}
	}

}
