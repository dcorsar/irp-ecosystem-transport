package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.transport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BusStop {
	private String uri;
	private String prefLabel;
	private String street;

	private double easting;
	private double northing;
	private double longitude;
	private double latitude;

	@XmlElement(name = "uri")
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@XmlElement(name = "prefLabel")
	public String getPrefLabel() {
		return prefLabel;
	}

	public void setPrefLabel(String prefLabel) {
		this.prefLabel = prefLabel;
	}

	@XmlElement(name = "street")
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@XmlElement(name = "easting")
	public double getEasting() {
		return easting;
	}

	public void setEasting(double easting) {
		this.easting = easting;
	}

	@XmlElement(name = "northing")
	public double getNorthing() {
		return northing;
	}

	public void setNorthing(double northing) {
		this.northing = northing;
	}

	@XmlElement(name = "longitude")
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@XmlElement(name = "latitude")
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
