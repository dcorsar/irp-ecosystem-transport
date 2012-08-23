package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import uk.ac.dotrural.irp.ecosystem.sensor.model.Observation;

@XmlRootElement
public class LocationObservation extends Observation {

	private Long serverTime;

	@XmlElement(name = "serverTime")
	public Long getServerTime() {
		return serverTime;
	}

	public void setServerTime(Long serverTime) {
		this.serverTime = serverTime;
	}
}
