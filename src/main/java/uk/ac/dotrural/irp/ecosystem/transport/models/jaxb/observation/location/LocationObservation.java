package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Observation;

@XmlRootElement
public class LocationObservation extends Observation {

	private Long serverTime;
	private String derivedFrom;

	@XmlElement(name = "serverTime")
	public Long getServerTime() {
		return serverTime;
	}

	public void setServerTime(Long serverTime) {
		this.serverTime = serverTime;
	}

	@XmlElement(name = "derivedFrom")
	public String getDerivedFrom(){
		return this.derivedFrom;
	}
	
	public void setDerivedFrom(String derivedFrom) {
		this.derivedFrom = derivedFrom;
	}

        @Override public String toString() {
            return super.toString() +
                "; serverTime: " + serverTime +
                "; derivedFrom: " + derivedFrom;
        }
}
