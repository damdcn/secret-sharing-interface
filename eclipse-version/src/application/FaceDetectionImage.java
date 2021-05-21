package application;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

public class FaceDetectionImage {

	private static String file = null;
	private static int nbFace = 0;
	private ArrayList<ArrayList<application.Point>> points = new ArrayList<ArrayList<application.Point>>();
	private ArrayList<Rect> regionsOfInterest = new ArrayList<Rect>();
	private int imageWidth;
	private int imageHeight;
	
	public int getImageWidth() {
		return imageWidth;
	}


	public int getImageHeight() {
		return imageHeight;
	}


	public FaceDetectionImage(String f) {
		FaceDetectionImage.file = f;
	}

	
	public boolean doOverlap(Rect r1, Rect r2) {
	    if (r1.tl().x > r2.br().x || r1.br().x < r2.tl().x || r1.tl().y > r2.br().y || r1.br().y < r2.tl().y) {
	        return false;
	    } else {
	        return true;
	    }
	}
	
	public Rect moveRectangle(Rect r1,Rect r2,Mat src) {
		Point new_bottom_left = new Point(r1.x - r2.x,r1.y-r2.y);
		Point new_top_right = new Point ((r1.x + r1.width) - (r2.x + r2.width), (r1.y + r1.height) - (r2.y + r2.height));
		return  new Rect(new_bottom_left,
				new_top_right// // top right                                                    // RGB colour
				);
		
	}
	
	
	public WritableImage process () {
		// Loading the OpenCV core library
		File opencvLib = new File("src/libs/x64/opencv_java451.dll");
		System.load( opencvLib.getAbsolutePath() );

		// Reading the Image from the file and storing it in to a Matrix object
		Mat src = Imgcodecs.imread(file);
		
		// Save image size in attribute
		this.imageHeight = src.height();
		this.imageWidth = src.width();

		// Instantiating the CascadeClassifier
		String xmlFile = "./src/xml/haarcascade_frontalface_alt_tree.xml";
		CascadeClassifier classifier = new CascadeClassifier(xmlFile);

		// Detecting the face in the snap
		MatOfRect faceDetections = new MatOfRect();
		classifier.detectMultiScale(src, faceDetections);
		nbFace = faceDetections.toArray().length;
		//System.out.println(String.format("Detected %s faces", 
		//		faceDetections.toArray().length));
		
		// Detecting intersection
		ArrayList<Rect> listeRect = new ArrayList<Rect>();
		boolean intersection = false;
		
		for (Rect rect : faceDetections.toArray()) {
			if(listeRect.size()>0) {
				for (Rect rect2 : listeRect) {
					if(doOverlap(rect,rect2)) {
						intersection = true ;
					}
				}
			}
			listeRect.add(rect);
		}

		// Drawing boxes
		for (Rect rect : faceDetections.toArray()) {
			
			// If there is intersection -> rescale selection
			if(intersection) {
				Imgproc.rectangle(
						src,                                               														// where to draw the box
						new Point(rect.x + (rect.width *0.15)  , rect.y + (rect.height * 0.15)),                            	// bottom left
						new Point((rect.x + rect.width) - (0.15*(rect.width)),( rect.y + rect.height) - (rect.height * 0.15)), 	// top right
						new Scalar(0, 0, 255),
						3                                                     													// RGB colour
						);
				// Adding coords to a liste
				ArrayList<application.Point> pts = new ArrayList<application.Point>();
				pts.add(new application.Point((int) (rect.x + (rect.width *0.15))  ,(int) (rect.y + (rect.height * 0.15))));
				pts.add(new application.Point((int) ((rect.x + rect.width) - (0.15*(rect.width))) , (int) (( rect.y + rect.height) - (rect.height * 0.15))));
				points.add(pts);
				
				// Adding coords to a matrix (for encryption)
				Rect rectFinal = new Rect(new Point(rect.x + (rect.width *0.15)  , rect.y + (rect.height * 0.15)),new Point((rect.x + rect.width) - (0.15*(rect.width)),( rect.y + rect.height) - (rect.height * 0.15)));
				Mat roi= new Mat(src,rectFinal);
				regionsOfInterest.add(rectFinal);
				
			// If there is no intersection -> standard selection
			} else {
				Imgproc.rectangle(
						src,                                               		// where to draw the box
						new Point(rect.x, rect.y),                            	// bottom left
						new Point(rect.x + rect.width, rect.y + rect.height), 	// top right
						new Scalar(0, 0, 255),
						7                   	                                // RGB colour
						);
				
				// Adding coords to a liste
				ArrayList<application.Point> pts = new ArrayList<application.Point>();
				pts.add(new application.Point(rect.x, rect.y));
				pts.add(new application.Point(rect.x + rect.width, rect.y + rect.height));
				points.add(pts);
				
				
	
				// Adding coords to a matrix (for encryption)
				Rect rectFinal = new Rect(new Point(rect.x + (rect.width *0.15)  , rect.y + (rect.height * 0.15)),new Point((rect.x + rect.width) - (0.15*(rect.width)),( rect.y + rect.height) - (rect.height * 0.15)));
				Mat roi= new Mat(src,rectFinal);
				regionsOfInterest.add(rectFinal);
			}
		}

		//Si je veux dessiner mes rectangles apres
		/*
				for (ArrayList<Point> arrayList : points) {
					Imgproc.rectangle(src,arrayList.get(0),arrayList.get(1),new Scalar(0, 0, 255),3);
				}
		 */


		//Si je veux traiter l'image (floutage,...)
		/*
				for (Mat roi : regionsOfInterest) {
					   Imgproc.blur(roi,roi, new Size(15,15));

				}
		 */
		
		
		// Creating BuffredImage from the matrix
        BufferedImage image = new BufferedImage(src.width(), src.height(),
           BufferedImage.TYPE_3BYTE_BGR);
        
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        src.get(0, 0, data);

        //this.src = src;
          
        // Creating the Writable Image
        WritableImage writableImage = SwingFXUtils.toFXImage(image, null);
        
        return writableImage;

		// Writing the image
		//Imgcodecs.imwrite("./output", src);

		//System.out.println("Image Processed");
	}
	
	public int getFacesNumber() {
		return nbFace;
	}
	
	public ArrayList<Rect> getSelectionRect(){
		
		ArrayList<Rect> orderedRegion = regionsOfInterest;
		
		Comparator<Rect> PointXComparator = new Comparator<Rect>()
	    {
	        public int compare(Rect p1, Rect p2)
	        {
	            return Double.compare(p1.x, p2.x);
	        }
	    };
	    
	    Collections.sort(orderedRegion, PointXComparator);
		
		return orderedRegion;
	}
	
	public ArrayList<ArrayList<application.Point>> getPoints() {
		
		ArrayList<ArrayList<application.Point>> orderedPoints= points;
		
		Comparator<ArrayList<application.Point>> PointXComparator = new Comparator<ArrayList<application.Point>>()
	    {
	        public int compare(ArrayList<application.Point> p1, ArrayList<application.Point> p2)
	        {
	            return Double.compare(p1.get(0).getX(), p2.get(0).getX());
	        }
	    };

	    Collections.sort(orderedPoints, PointXComparator);
		
		return orderedPoints;
	}

	public void printPoints() {
		for (ArrayList<application.Point> arrayList : points) {
			System.out.println("[");
			for (application.Point pts : arrayList) {
				System.out.println(pts);
			}
			System.out.println("]");
		}
	}
}