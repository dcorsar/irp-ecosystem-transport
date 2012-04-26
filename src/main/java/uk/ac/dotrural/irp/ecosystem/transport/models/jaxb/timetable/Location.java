package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Location
{
  private String time;
  private double easting;
  private double westing;
  private double longitude;
  private double latitude;
  
  @XmlElement(name="time")
  public String getTime()
  {
    return time;
  }
  
  public void setTime(String time)
  {
    this.time = time;
  }
  
  @XmlElement(name="easting")
  public double getEasting()
  {
    return easting;
  }
  
  public void setEasting(double easting)
  {
    this.easting = easting;
  }
  
  @XmlElement(name="westing")
  public double getWesting()
  {
    return westing;
  }
  
  public void setWesting(double westing)
  {
    this.westing = westing;
  }
  
  @XmlElement(name="longitude")
  public double getLongitude()
  {
    return longitude;
  }
  
  public void setLongitude(double longitude)
  {
    this.longitude = longitude;
  }
  
  @XmlElement(name="latitude")
  public double getLatitude()
  {
    return latitude;
  }
  
  public void setLatitude(double latitude)
  {
    this.latitude = latitude;
  }
}