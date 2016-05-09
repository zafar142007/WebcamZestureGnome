package com.zafar.core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.zafar.control.CommandMaster;

public class GestureRecogniser implements Runnable {

	private LinkedBlockingQueue<BufferedImage> video;
	private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>(
			Configuration.WINDOW_SIZE);

	private CommandMaster master = new CommandMaster();

	@Override
	public void run() {
		String currentDirection = "";
		int countLeft = 0, countRight = 0, countDown = 0, countUp = 0, countNone = 0;
		BufferedImage image = null, previousImage = null;
		java.awt.Point point = new java.awt.Point(0, 0), previousPoint = null;
		int i = 0;
		try {
			while (!Thread.interrupted()) {
				previousImage = image;
				image = video.take();
				i++;
				// System.out.println("read "+i);
				if (previousImage == null)
					previousImage = image;
				previousPoint = point;
				point = findMovingCentroid(image, previousImage,
						Configuration.CHANGE_THRESHOLD);
				currentDirection = findDirection(point, previousPoint);
				// System.out.println(currentDirection);

				if (queue.size() == Configuration.WINDOW_SIZE) {
					String expelled = queue.poll();

					switch (expelled) {
					case Direction.LEFT:
						countLeft--;
						break;

					case Direction.RIGHT:
						countRight--;
						break;

					case Direction.UP:
						countUp--;
						break;

					case Direction.DOWN:
						countDown--;
						break;

					default:
						countNone--;
					}
				}
				queue.add(currentDirection);
				switch (currentDirection) {
				case Direction.LEFT:
					countLeft++;
					if (countLeft > Configuration.REPETITION_THRESHOLD) {
						System.out.println("Gesture detected: "
								+ Direction.LEFT);
						List<String> list= (new ArrayList<String>());
						list.add(Direction.LEFT);
						queue.removeAll(list);//this is so that this gesture is not recognised repeatedly in succession
						countLeft=0;
						master.executeCommand(Direction.LEFT);
					}
					break;

				case Direction.RIGHT:
					countRight++;
					if (countRight > Configuration.REPETITION_THRESHOLD) {
						System.out.println("Gesture detected: "
								+ Direction.RIGHT);
						List<String> list= (new ArrayList<String>());
						list.add(Direction.RIGHT);
						queue.removeAll(list);
						countRight=0;
						
						master.executeCommand(Direction.RIGHT);
					}
					break;

				case Direction.UP:
					countUp++;
					if (countUp > Configuration.REPETITION_THRESHOLD) {
						System.out.println("Gesture detected: " + Direction.UP);
						List<String> list= (new ArrayList<String>());
						list.add(Direction.UP);
						queue.removeAll(list);
						countUp=0;
						master.executeCommand(Direction.UP);
					}

					break;

				case Direction.DOWN:
					countDown++;
					if (countDown > Configuration.REPETITION_THRESHOLD) {
						System.out.println("Gesture detected: "
								+ Direction.DOWN);
						List<String> list= (new ArrayList<String>());
						list.add(Direction.DOWN);
						queue.removeAll(list);
						countDown=0;
						master.executeCommand(Direction.DOWN);
					}

					break;

				default:

					countNone++;
					if (countNone > Configuration.REPETITION_THRESHOLD)
					{	
						System.out.println("Gesture detected: "
								+ Direction.NONE);
						List<String> list= (new ArrayList<String>());
						list.add(Direction.NONE);
						queue.removeAll(list);
						countNone=0;
					}

				}

			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String findDirection(java.awt.Point point,
			java.awt.Point previousPoint) {

		double slope = 0;
		if (!(point.x - previousPoint.x == 0))
			slope = (double) (point.y - previousPoint.y)
					/ (double) (point.x - previousPoint.x);
		else
			slope = 1.5;
		// System.out.println(slope+" "+point+" <== "+previousPoint);
		if (previousPoint.x < point.x && slope < 1 && slope >= -1)
			return Direction.RIGHT;
		else if (previousPoint.y > point.y && (slope >= 1 || slope < -1))
			return Direction.UP;
		else if (previousPoint.x > point.x && slope < 1 && slope >= -1)
			return Direction.LEFT;
		else if (previousPoint.y < point.y && (slope >= 1 || slope < -1))
			return Direction.DOWN;
		else
			return Direction.NONE;

	}

	private java.awt.Point findMovingCentroid(BufferedImage image,
			BufferedImage previousImage, double threshold) {

		double differenceValue = 0;
		double centroidX = 0, centroidY = 0;
		int count = 0;
		for (int x = 0; x < image.getWidth(); ++x)
			for (int y = 0; y < image.getHeight(); ++y) {
				differenceValue = Math.abs(image.getRGB(x, y)
						- previousImage.getRGB(x, y));
				if (differenceValue > threshold) {
					centroidX += x;
					centroidY += y;
					count++;
				}
			}
		return new java.awt.Point((int) (centroidX / count),
				(int) (centroidY / count));
	}

	public LinkedBlockingQueue<BufferedImage> getVideo() {
		return video;
	}

	public void setVideo(LinkedBlockingQueue<BufferedImage> video) {
		this.video = video;
	}

}
