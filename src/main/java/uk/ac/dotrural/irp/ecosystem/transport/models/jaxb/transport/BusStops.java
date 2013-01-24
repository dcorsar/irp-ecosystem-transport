package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.transport;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BusStops {
	private List<BusStop> busStops;
	private String kml;

	public BusStops() {
	}

	public BusStops(List<BusStop> busStops) {
		this.busStops = busStops;
	}

	@XmlElement(name = "busStops")
	public List<BusStop> getBusStops() {
		return busStops;
	}

	public void setBusStops(List<BusStop> busStops) {
		this.busStops = busStops;
	}

	@XmlElement(name = "kml")
	public String getKml() {
		return kml;
	}

	public void setKml(String kml) {
		this.kml = kml;
	}

}
