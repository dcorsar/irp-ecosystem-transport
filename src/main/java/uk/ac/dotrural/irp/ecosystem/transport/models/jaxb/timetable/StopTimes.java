package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StopTimes {

	private String label;
	private List<StopTime> nextBuses, previousBuses;

	public StopTimes(){
		super();
		this.nextBuses = new ArrayList<StopTime>();
		this.previousBuses = new ArrayList<StopTime>();
	}
	
	public void addNextBus(StopTime stopTime){
		this.nextBuses.add(stopTime);
	}
	
	public void addPreviousBus(StopTime stopTime){
		this.previousBuses.add(stopTime);
	}
	
	@XmlElement(name = "nextBuses")
	public List<StopTime> getNextBuses() {
		return nextBuses;
	}

	public void setNextBuses(List<StopTime> nextBuses) {
		this.nextBuses = nextBuses;
	}
	@XmlElement(name = "previousBuses")
	public List<StopTime> getPreviousBuses() {
		return previousBuses;
	}

	public void setPreviousBuses(List<StopTime> previousBuses) {
		this.previousBuses = previousBuses;
	}

	@XmlElement(name = "label")
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
	
}
