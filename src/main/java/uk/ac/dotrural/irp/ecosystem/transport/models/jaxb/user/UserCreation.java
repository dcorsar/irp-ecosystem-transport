package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.user;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserCreation {

	private boolean created;
//	private String userUri;
	private String reason;
	
	public UserCreation(){
		super();
	}

	@XmlElement(name="created")
	public boolean getCreated() {
		return created;
	}

	public void setCreated(boolean created) {
		this.created = created;
	}

//	@XmlElement(name="userUri")
//	public String getUserUri() {
//		return userUri;
//	}
//
//	public void setUserUri(String userUri) {
//		this.userUri = userUri;
//	}

	@XmlElement(name="reason")
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


	
	
	
}
