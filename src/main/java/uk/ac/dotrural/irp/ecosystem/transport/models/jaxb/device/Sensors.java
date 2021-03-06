package uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.device;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensor;

@XmlRootElement
public class Sensors
{
  private List<Sensor> sensors;

  @XmlElement(name="sensors")
  public List<Sensor> getSensors()
  {
    return sensors;
  }

  public void setSensors(List<Sensor> sensors)
  {
    this.sensors = sensors;
  }
}
