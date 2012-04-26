package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location;

import javax.xml.bind.annotation.XmlElement;

public class LocationDeviceValues {

	private Long  gpsTime, deviceTime;
	private Integer accuracy;
	private Double latitude, longitude, heading, speed;
	
	@XmlElement(name="gpsTime")
	public Long getGpsTime() {
		return this.gpsTime;
	}
	public void setGpsTime(Long gpsTime) {
		this.gpsTime = gpsTime;
	}
	@XmlElement(name="deviceTime")
	public Long getDeviceTime() {
		return deviceTime;
	}
	public void setDeviceTime(Long deviceTime) {
		this.deviceTime = deviceTime;
	}
	@XmlElement(name="accuracy")
	public Integer getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(Integer accuracy) {
		this.accuracy = accuracy;
	}
	@XmlElement(name="latitude")
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	@XmlElement(name="longitude")
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	@XmlElement(name="heading")
	public Double getHeading() {
		return heading;
	}
	public void setHeading(Double heading) {
		this.heading = heading;
	}
	@XmlElement(name="speed")
	public Double getSpeed() {
		return speed;
	}
	public void setSpeed(Double speed) {
		this.speed = speed;
	}
	
	
	
}
