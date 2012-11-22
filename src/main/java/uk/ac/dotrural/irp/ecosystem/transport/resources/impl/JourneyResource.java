package uk.ac.dotrural.irp.ecosystem.transport.resources.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import uk.ac.dotrural.irp.ecosystem.core.util.Util;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Property;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensing;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensor;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.device.Device;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.device.Sensors;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.journey.Journey;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.journey.JourneyPayload;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Line;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.user.User;
import uk.ac.dotrural.irp.ecosystem.transport.queries.device.DeviceQueries;
import uk.ac.dotrural.irp.ecosystem.transport.queries.journey.BusJourneyQueries;
import uk.ac.dotrural.irp.ecosystem.transport.queries.journey.QueryReader;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@Path("/journey")
@Scope("request")
public class JourneyResource implements RESTFulSPARQL {
	@Context
	private UriInfo uriInfo;

	private SPARQLEndpoint journeyEndpoint;

	public void setJourneyEndpoint(SPARQLEndpoint routesEndpoint) {
		this.journeyEndpoint = routesEndpoint;
	}

	public SystemMessage init(ServiceInitialiser si) {
		return journeyEndpoint.init(uriInfo, si);
	}

	public void update(Query query) {
		journeyEndpoint.update(query);
	}

	public String query(Query query) {
		return Util.resultsetToString(journeyEndpoint.query(query));
	}

	public EndpointInfo info() {
		return journeyEndpoint.info();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("create")
	public Journey create(JourneyPayload journeyPayload) {
		if (journeyPayload == null)
			throw new ExceptionReporter(new NullPointerException(
					"No journey details given."));
		if (journeyPayload.getAuthenticationToken() == null
				|| journeyPayload.getAuthenticationToken().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"No authentication token given."));
		if (journeyPayload.getType() == null)
			throw new ExceptionReporter(new NullPointerException(
					"No journey type given."));
		if (journeyPayload.getType().getLineUri() == null
				|| "".equals(journeyPayload.getType().getLineUri().trim()))
			throw new ExceptionReporter(new NullPointerException(
					"No lineUri given in journey type"));
		if (journeyPayload.getType().getDirection() == null
				|| "".equals(journeyPayload.getType().getDirection().trim()))
			throw new ExceptionReporter(new NullPointerException(
					"No direction given in journey type"));
		if (!("inbound".equals(journeyPayload.getType().getDirection().trim()))
				&& !("outbound".equals(journeyPayload.getType().getDirection()
						.trim())))
			throw new ExceptionReporter(
					new NullPointerException(
							"Invalid direction given in journey type: must be 'inbound' or 'outbound'"));
		if (journeyPayload.getType().getUri() == null
				|| "".equals(journeyPayload.getType().getUri().trim()))
			throw new ExceptionReporter(new NullPointerException(
					"No uri given in journey type"));
		if (journeyPayload.getUserUri() == null
				|| journeyPayload.getUserUri().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"No 'userUri' given."));
		if (journeyPayload.getDevice() == null)
			throw new ExceptionReporter(new NullPointerException(
					"No 'device' given."));

		String uri = QueryReader.getString("JourneyQueries.baseNS")
				+ UUID.randomUUID().toString();

		List<Query> queries = null;
		if (journeyPayload.getDevice().getUri() == null) {
			queries = new ArrayList<Query>();
			String phoneUri = DeviceQueries.getBaseNS()
					+ UUID.randomUUID().toString();
			String locationDeviceUri = DeviceQueries.getBaseNS()
					+ UUID.randomUUID().toString();
			List<Sensing> sensingMethod = new ArrayList<Sensing>();
			if ("Android".equals(journeyPayload.getDevice().getType())) {
				queries.add(new Query(DeviceQueries.getCreateAndroidPhone(
						phoneUri, locationDeviceUri, journeyPayload.getDevice()
								.getOperatingSystemVersion(), journeyPayload
								.getDevice().getUniqueId())));
				sensingMethod.add(new Sensing(DeviceQueries
						.getAndroidNetworkLocationSensing()));
				sensingMethod.add(new Sensing(DeviceQueries
						.getAndroidPassiveLocationSensing()));
				sensingMethod.add(new Sensing(DeviceQueries
						.getAndroidGPSLocationSensing()));
			} else if ("iPhone".equals(journeyPayload.getDevice().getType())) {
				queries.add(new Query(DeviceQueries.getCreateiPhone(phoneUri,
						locationDeviceUri, journeyPayload.getDevice()
								.getOperatingSystemVersion(), journeyPayload
								.getDevice().getUniqueId())));
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
			journeyPayload.getDevice().setUri(phoneUri);
			List<Sensor> sensorList = new ArrayList<Sensor>();
			sensorList.add(locationSensor);
			Sensors sensors = new Sensors();
			sensors.setSensors(sensorList);
			journeyPayload.getDevice().setSensors(sensors);
		}

		String query = BusJourneyQueries.getCreateBusJourneyUpdate(
				journeyPayload.getUserUri(), journeyPayload.getDevice()
						.getUri(), journeyPayload.getType().getLineUri(), uri,
				journeyPayload.getType().getDirection().trim());
		if (queries != null) {
			queries.add(new Query(query));
			journeyEndpoint.update(queries);
		} else {
			Query sparqlQuery = new Query(query);
			journeyEndpoint.update(sparqlQuery);
		}

		Journey journey = new Journey();
		journey.setUri(uri);
		journey.setDevice(journeyPayload.getDevice());

		return journey;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("get")
	public Journey get(
			@DefaultValue("") @QueryParam("journeyUri") String journeyUri) {
		journeyUri = journeyUri.trim();

		if (journeyUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"'journeyUri' is needed to retrieve the journey."));

		String query = BusJourneyQueries.getBusJourneyQuery(journeyUri);
		Query sparqlQuery = new Query(query);
		ResultSet results = journeyEndpoint.query(sparqlQuery);

		List<String> vars = results.getResultVars();
		Journey journey = new Journey();

		while (results.hasNext()) {
			QuerySolution solution = results.next();

			journey.setLine(new Line(Util.getNodeValue(
					solution.get(vars.get(0))).trim()));
			journey.setDirection(Util.getNodeValue(
					solution.get(vars.get(1))).trim());
			journey.setDevice(new Device(Util.getNodeValue(
					solution.get(vars.get(2))).trim()));
			journey.setUser(new User(Util.getNodeValue(
					solution.get(vars.get(3))).trim()));
		}

		return journey;
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("delete")
	public void delete(
			@DefaultValue("") @QueryParam("journeyUri") String journeyUri) {
		journeyUri = journeyUri.trim();

		if (journeyUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"No journey uri given."));

		String query = BusJourneyQueries.getDeleteBusJourneyUpdate(journeyUri);
		Query sparqlQuery = new Query(query);
		journeyEndpoint.update(sparqlQuery);
	}
}
