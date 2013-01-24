package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StopTime {

	private String arrivalTime, departureTime;

	
	@XmlElement(name = "arrivalTime")
	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	@XmlElement(name = "departureTime")
	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}
	
	

}
