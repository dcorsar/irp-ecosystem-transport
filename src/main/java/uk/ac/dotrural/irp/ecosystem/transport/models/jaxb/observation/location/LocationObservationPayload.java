package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import uk.ac.dotrural.irp.ecosystem.sensor.model.ObservationPayload;

@XmlRootElement
public class LocationObservationPayload extends ObservationPayload {

	private String type;

	@XmlElement(name = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
