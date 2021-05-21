package code;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="persons")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLData {
	
	ArrayList<Person> person;
	
	public XMLData() {
		
	}
	
	XMLData(ArrayList<Person> p){
		this.person=p;
	}
	
	

}