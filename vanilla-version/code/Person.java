package code;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;

import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Person {

	ArrayList<Point> points;
	@XmlAttribute
	private int id;
	private ToggleButton button;
	private boolean revealed;
	private Rectangle region;
	Tab tabView;
	
	
	public Person(int i, Rectangle r) {
		this.id = i;
		this.button = new ToggleButton("Personnage n�"+(id+1));
		this.revealed = false;
		
		
		button.setOnMouseClicked(e->{
			Main.toggleSelection(id);
		});
		
		// Hover des buttons
		button.setOnMouseEntered(e->{
			region.setOpacity(1);
		});
		button.setOnMouseExited(e->{
			region.setOpacity(0);
		});

		// Style du rectangle
		this.region = new Rectangle();
		region.setArcWidth(25);
		region.setArcHeight(25);
		region.setFill(Color.TRANSPARENT);
		region.setStroke(Color.GOLD);
		region.setStrokeType(StrokeType.OUTSIDE);
		region.setStrokeWidth(5);
		region.setOpacity(0);
		
		// Hover du rectangle
		region.setOnMouseEntered(e->{
			region.setOpacity(1);
			button.setStyle("-fx-border-color: #FFD700;");
		});
		region.setOnMouseExited(e->{
			region.setOpacity(0);
			button.setStyle("-fx-border-color: linear-gradient(#020b02, #3a3a3a);");
		});
		
		// Onglet de la personne
		this.tabView = new Tab();
		tabView.setText("Personnage n�"+(id+1));
	}
	
	public Rectangle getRegion() {
		return region;
	}

	public ToggleButton getButton() {
		return button;
	}
	
	public Tab getTab() {
		return tabView;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}
	
	public void setPoints(ArrayList<Point> p) {
		points = p;
	}
	
	public ArrayList<Point> getPoints() {
		return points;
	}
	
	// Positionne les rectangles de hover sur le visage
	public void scaleRect(double imgX, double imgY, double reducCoef) {
		
		XMLParser data = new XMLParser();
		
		int width = data.getRegionWidth(id);
		int height = data.getRegionHeight(id);
		int x1 = data.getTopLeftCoords(id).getX();
		int y1 = data.getTopLeftCoords(id).getY();
		
		region.setWidth(width * reducCoef);
		region.setHeight(height * reducCoef);
		region.setLayoutX(100 + imgX + (x1*reducCoef));
		region.setLayoutY(80 + imgY + (y1*reducCoef));
	}	
}
