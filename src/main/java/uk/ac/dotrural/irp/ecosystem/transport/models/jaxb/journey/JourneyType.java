package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.journey;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JourneyType
{
  private String uri;
  private String lineUri;
//  private String serviceUri;
  private String direction;
  
  @XmlElement(name="uri")
  public String getUri()
  {
    return uri;
  }
  
  public void setUri(String uri)
  {
    this.uri = uri;
  }
  
  @XmlElement(name="lineUri")
  public String getLineUri()
  {
    return lineUri;
  }
  
  public void setLineUri(String lineUri)
  {
    this.lineUri = lineUri;
  }
  
 /* @XmlElement(name="serviceUri")
  public String getServiceUri()
  {
    return serviceUri;
  }
  
  public void setServiceUri(String service)
  {
    this.serviceUri = service;
  }
  */
  @XmlElement(name="direction")
  public String getDirection()
  {
    return direction;
  }
  
  public void setDirection(String direction)
  {
    this.direction = direction;
  }
}
