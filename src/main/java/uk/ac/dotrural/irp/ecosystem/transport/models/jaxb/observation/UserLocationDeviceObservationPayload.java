package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import uk.ac.dotrural.irp.ecosystem.sensor.model.ObservationPayload;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location.LocationDeviceObservationPayload;

@XmlRootElement
public class UserLocationDeviceObservationPayload {

	
	private String userUri;
	private String authenticationToken;
	private LocationDeviceObservationPayload payload;
	
	@XmlElement(name="userUri")
	public String getUserUri() {
		return userUri;
	}
	public void setUserUri(String userUri) {
		this.userUri = userUri;
	}
	@XmlElement(name="authenticationToken")
	public String getAuthenticationToken() {
		return authenticationToken;
	}
	public void setAuthenticationToken(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	@XmlElement(name="payload")
	public LocationDeviceObservationPayload getPayload() {
		return payload;
	}
	public void setPayload(LocationDeviceObservationPayload payload) {
		this.payload = payload;
	}
	
	
	

}
