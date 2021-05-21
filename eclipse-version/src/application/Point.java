package application;

import javax.xml.bind.annotation.XmlAttribute;

public class Point {
	@XmlAttribute
	private int x;
	@XmlAttribute
    private int y;
	@XmlAttribute
    private String position;

    public Point(int a, int b){
        this.x = a;
        this.y = b;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
    
    public void setInfos(String str) {
    	position=str;
    }
    
    public String toString() {
    	return this.x + " ; " + this.y;
    }
}
