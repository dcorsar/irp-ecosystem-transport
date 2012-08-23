package uk.ac.dotrural.irp.ecosystem.transport.resources.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.context.annotation.Scope;

import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.EndpointInfo;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.Query;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.ServiceInitialiser;
import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.SystemMessage;
import uk.ac.dotrural.irp.ecosystem.core.resources.RESTFulSPARQL;
import uk.ac.dotrural.irp.ecosystem.core.resources.support.reporters.ExceptionReporter;
import uk.ac.dotrural.irp.ecosystem.core.services.SPARQLEndpoint;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Property;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensing;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensor;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.device.Device;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.device.Sensors;
import uk.ac.dotrural.irp.ecosystem.transport.queries.alert.QueryReader;
import uk.ac.dotrural.irp.ecosystem.transport.queries.device.DeviceQueries;

@Path("/device")
@Scope("request")
public class DeviceResource implements RESTFulSPARQL {
	@Context
	private UriInfo uriInfo;

	private SPARQLEndpoint deviceEndpoint;

	public void setDeviceEndpoint(SPARQLEndpoint deviceEndpoint) {
		this.deviceEndpoint = deviceEndpoint;
	}

	public SystemMessage init(ServiceInitialiser si) {
		return deviceEndpoint.init(uriInfo, si);
	}

	public void update(Query query) {
		deviceEndpoint.update(query);
	}

	public String query(Query query) {
		return uk.ac.dotrural.irp.ecosystem.core.util.Util
				.resultsetToString(deviceEndpoint.query(query));
	}

	public EndpointInfo info() {
		return deviceEndpoint.info();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("create")
	public Device create(Device device) {
		if (device == null)
			throw new ExceptionReporter(new NullPointerException(
					"No 'Device' creation information given."));

		List<Query> queries = new ArrayList<Query>();

		if (device.getUri() == null) {
			String phoneUri = DeviceQueries.getBaseNS()
					+ UUID.randomUUID().toString();
			String locationDeviceUri = DeviceQueries.getBaseNS()
					+ UUID.randomUUID().toString();
			List<Sensing> sensingMethod = new ArrayList<Sensing>();
			if ("android".equals(device.getType())) {
				queries.add(new Query(DeviceQueries.getCreateAndroidPhone(
						phoneUri, locationDeviceUri,
						device.getOperatingSystemVersion(),
						device.getUniqueId())));
				sensingMethod.add(new Sensing(DeviceQueries
						.getAndroidNetworkLocationSensing()));
				sensingMethod.add(new Sensing(DeviceQueries
						.getAndroidPassiveLocationSensing()));
				sensingMethod.add(new Sensing(DeviceQueries
						.getAndroidPassiveLocationSensing()));
			} else if ("iPhone".equals(device.getType())) {
				queries.add(new Query(DeviceQueries.getCreateiPhone(phoneUri,
						locationDeviceUri, device.getOperatingSystemVersion(),
						device.getUniqueId())));
				sensingMethod.add(new Sensing(DeviceQueries
						.getiOSLocationSensing()));
			}
			// TODO add iPad and unknown devices
			Sensor locationSensor = new Sensor();
			locationSensor.setUri(locationDeviceUri);
			locationSensor
					.setObserves(new Property(
							"http://www.dotrural.ac.uk/irp/uploads/ontologies/sensors/location"));
			locationSensor.setImplements(sensingMethod);
			device.setUri(phoneUri);
			List<Sensor> sensorList = new ArrayList<Sensor>();
			sensorList.add(locationSensor);
			Sensors sensors = new Sensors();
			sensors.setSensors(sensorList);
			device.setSensors(sensors);

			System.out.format("  (INFO) : %s", queries.toString());

			deviceEndpoint.update(queries);
		}
		return device;

	}
}
