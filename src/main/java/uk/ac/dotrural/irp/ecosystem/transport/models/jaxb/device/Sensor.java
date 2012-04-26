package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.device;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Sensor
{
  private String uri;
  private String observes;
  private List<String> sensingMethod;
  
  @XmlElement(name="uri")
  public String getUri()
  {
    return uri;
  }
  
  public void setUri(String uri)
  {
    this.uri = uri;
  }
  
  @XmlElement(name="observes")
  public String getObserves()
  {
    return observes;
  }
  
  public void setObserves(String observes)
  {
    this.observes = observes;
  }
  
  @XmlElement(name="sensingMethod")
  public List<String> getSensingMethod()
  {
    return sensingMethod;
  }
  
  public void setSensingMethod(List<String> sensingMethod)
  {
    this.sensingMethod = sensingMethod;
  }
}
