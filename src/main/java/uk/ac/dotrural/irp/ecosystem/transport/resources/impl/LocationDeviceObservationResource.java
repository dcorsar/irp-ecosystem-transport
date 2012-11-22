package uk.ac.dotrural.irp.ecosystem.transport.resources.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import uk.ac.dotrural.irp.ecosystem.sensor.model.FeatureOfInterest;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Observation;
import uk.ac.dotrural.irp.ecosystem.sensor.model.ObservationPayload;
import uk.ac.dotrural.irp.ecosystem.sensor.model.ObservationValue;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensing;
import uk.ac.dotrural.irp.ecosystem.sensor.model.Sensor;
import uk.ac.dotrural.irp.ecosystem.sensor.model.SensorOutput;
import uk.ac.dotrural.irp.ecosystem.timetable.SegmentDistance;
import uk.ac.dotrural.irp.ecosystem.timetable.ServiceMapMatcher;
import uk.ac.dotrural.irp.ecosystem.timetable.model.Point;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.UserLocationDeviceObservationPayload;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location.LocationDeviceObservation;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location.LocationDeviceObservationPayload;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location.LocationDeviceObservationValue;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location.LocationDeviceValues;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.observation.location.LocationObservation;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.BusLocations;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Location;
import uk.ac.dotrural.irp.ecosystem.transport.queries.observation.ObservationQueries;
import uk.ac.dotrural.irp.ecosystem.transport.queries.observation.QueryReader;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@Path("/observation")
@Scope("request")
public class LocationDeviceObservationResource implements RESTFulSPARQL {
	@Context
	private UriInfo uriInfo;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss z");

	private SPARQLEndpoint observationEndpoint;

	public void setObservationEndpoint(SPARQLEndpoint observationsEndpoint) {
		this.observationEndpoint = observationsEndpoint;
	}

	public SystemMessage init(ServiceInitialiser si) {
		return observationEndpoint.init(uriInfo, si);
	}

	public void update(Query query) {
		observationEndpoint.update(query);
	}

	public String query(Query query) {
		return Util.resultsetToString(observationEndpoint.query(query));
	}

	public EndpointInfo info() {
		return observationEndpoint.info();
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("create")
	public LocationDeviceObservation create(
			UserLocationDeviceObservationPayload payload) {
		// @QueryParam("userUri") String userUri,
		// @QueryParam("authenticationToken") String authenticationToken,
		// @QueryParam("locationObservation") LocationDeviceObservationPayload
		// locationObservationPayload) {
		if (payload == null) {
			throw new ExceptionReporter(new NullPointerException(
					"No payload given"));
		}

		if (payload.getUserUri() == null || "".equals(payload.getUserUri())) {
			throw new ExceptionReporter(
					new NullPointerException(
							"User uri required in payload to create the location observation"));
		}

		LocationDeviceObservationPayload locationObservationPayload = payload
				.getPayload();

		if (locationObservationPayload == null) {
			throw new ExceptionReporter(
					new NullPointerException(
							"Payload required in payload to create the location observation"));
		}

		if (locationObservationPayload.getType() == null
				|| "".equals(locationObservationPayload.getType().trim())) {
			throw new ExceptionReporter(new NullPointerException(
					"Type required to create the location observation"));
		}

		if (locationObservationPayload.getObservedBy() == null
				|| "".equals(locationObservationPayload.getObservedBy().trim())) {
			throw new ExceptionReporter(new NullPointerException(
					"Sensor required to create the location observation"));
		}
		if (locationObservationPayload.getFeatureOfInterest() == null
				|| "".equals(locationObservationPayload.getFeatureOfInterest()
						.trim())) {
			throw new ExceptionReporter(
					new NullPointerException(
							"Feature of interest required to create the location observation"));
		}

		if (locationObservationPayload.getValues() == null) {
			throw new ExceptionReporter(
					new NullPointerException(
							"Location values required to create the location observation"));
		}
		if (ObservationQueries.getLocationDeviceObservationType().equals(
				locationObservationPayload.getType().trim())) {
			// create the location device observation
			LocationDeviceValues values = locationObservationPayload
					.getValues();
			if (values.getLatitude() == null) {
				throw new ExceptionReporter(new NullPointerException(
						"Latitude required to create the location observation"));
			}
			if (values.getLongitude() == null) {
				throw new ExceptionReporter(
						new NullPointerException(
								"Longitude required to create the location observation"));
			}
			if (values.getGpsTime() == null) {
				throw new ExceptionReporter(
						new NullPointerException(
								"GPS timestamp required to create the location observation"));
			}

			if (values.getDeviceTime() == null) {
				throw new ExceptionReporter(
						new NullPointerException(
								"Device time required to create the location observation"));
			}

			// build query
			String observationUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			String outputUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			String valueUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			long serverTime = System.currentTimeMillis();
			String deviceSensorUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			String sensingMethodUsedUri = (locationObservationPayload
					.getSensingMethodUsed() == null || ""
					.equals(locationObservationPayload.getSensingMethodUsed())) ? null
					: locationObservationPayload.getSensingMethodUsed();

			String queryStr = ObservationQueries
					.createLocationDeviceObservationUpdate(
							locationObservationPayload.getFeatureOfInterest()
									.trim(),
							values.getLatitude().doubleValue(), values
									.getLongitude().doubleValue(), values
									.getGpsTime().longValue(), values
									.getDeviceTime().longValue(), values
									.getAccuracy(), values.getHeading(), values
									.getSpeed(), observationUri, outputUri,
							valueUri, serverTime, deviceSensorUri,
							sensingMethodUsedUri);

			// perform update
			Query query = new Query(queryStr);
			observationEndpoint.update(query);

			// build result object
			LocationDeviceObservation observation = new LocationDeviceObservation();
			observation.setUri(observationUri);

			 mapMatchObservation(values.getLongitude().doubleValue(), values
			 .getLatitude().doubleValue(), locationObservationPayload
			 .getFeatureOfInterest().trim(), observationUri);

			return observation;
		}

		throw new ExceptionReporter(new IllegalArgumentException(
				"Unrecognised observation type"));
	}

	private void mapMatchObservation(double longitude, double latitude,
			String journeyUri, String originalObservationUri) {
		ServiceMapMatcher matcher = new ServiceMapMatcher();
		OSRef osRef = new LatLng(latitude, longitude).toOSRef();
		double easting = osRef.getEasting();
		double northing = osRef.getNorthing();
		Point point = new Point(easting, northing);
		long startTime = System.currentTimeMillis();

		SegmentDistance sd = matcher.mapToRouteFromJourney(point, 500,
				journeyUri, this.observationEndpoint.getQueryURI(),
				"http://localhost:8096/mapnodes/query");

		if (sd != null) {

			String valueUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			String sensorOutputUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			String observationUri = QueryReader
					.getString("ObservationQueries.baseNs") + UUID.randomUUID();
			Point mappedPoint = sd.getMappedPoint();

			String query = ObservationQueries
					.createMapMatchedLocationObservation(valueUri,
							mappedPoint.getEasting(),
							mappedPoint.getNorthing(), sd.getDistance(),
							sensorOutputUri, observationUri, journeyUri,
							System.currentTimeMillis(), startTime,
							originalObservationUri);

			observationEndpoint.update(new Query(query));
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("get")
	public Observation get(
			@DefaultValue("") @QueryParam("observationUri") String observationUri) {
		observationUri = observationUri.trim();
		if ("".equals(observationUri)) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"observationUri is null or empty"));
		}

		String query = ObservationQueries
				.getLocationDeviceObservationQuery(observationUri);
		ResultSet results = observationEndpoint.query(new Query(query));
		LocationObservation obs = new LocationObservation();
		obs.setUri(observationUri);
		List<String> vars = results.getResultVars();
		// ?observationResult ?foi ?observedBy ?sensingMethodUsed
		// ?osbservationResultTime ?observationSamplingTime ?serverTime
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			obs.setObservationResult(new SensorOutput(Util.getNodeValue(
					solution.get(vars.get(0))).trim()));
			obs.setFeatureOfInterest(new FeatureOfInterest(Util.getNodeValue(
					solution.get(vars.get(1))).trim()));

			String observedBy = Util.getNodeValue(solution.get(vars.get(2)));
			if (observedBy != null && !("".equals(observedBy.trim()))) {
				obs.setObservedBy(new Sensor(observedBy.trim()));
			}

			String method = Util.getNodeValue(solution.get(vars.get(3)));
			if (null != method && !("".equals(method.trim()))) {
				obs.setSensingMethodUsed(new Sensing(method.trim()));
			}
			obs.setObservationResultTime(Util.getNodeLongValue(solution
					.get(vars.get(4))));
			obs.setObservationSamplingTime(Util.getNodeLongValue(solution
					.get(vars.get(5))));
			obs.setServerTime(Util.getNodeLongValue(solution.get(vars.get(6))));
			String derivedFrom = Util.getNodeValue(solution.get(vars.get(7)));
			if (derivedFrom != null && !("".equals(derivedFrom.trim()))) {
				obs.setDerivedFrom(derivedFrom.trim());
			}
		}
		return obs;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getSensorOutput")
	public SensorOutput getSensorOutput(
			@DefaultValue("") @QueryParam("sensorOutputUri") String sensorOutputUri) {
		sensorOutputUri = sensorOutputUri.trim();
		if ("".equals(sensorOutputUri)) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"observationUri is null or empty"));
		}

		// setup and execute the query
		String query = ObservationQueries
				.getLocationDeviceSensorOutputQuery(sensorOutputUri);
		ResultSet results = observationEndpoint.query(new Query(query));

		List<String> resultVars = results.getResultVars();

		// process the results
		SensorOutput so = new SensorOutput();
		if (results.hasNext()) {
			QuerySolution qs = results.next();
			so.setUri(sensorOutputUri);
			ObservationValue value = new ObservationValue();
			value.setUri(Util.getNodeValue(qs.get(resultVars.get(0))));
			so.setHasValue(value);
		}
		return so;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getObservationValue")
	public ObservationValue getObservationValue(
			@DefaultValue("") @QueryParam("observationValueUri") String observationValueUri) {
		observationValueUri = observationValueUri.trim();
		if ("".equals(observationValueUri)) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"observationUri is null or empty"));
		}

		// setup and execute the query
		String query = ObservationQueries
				.getLocationDeviceObservationValueQuery(observationValueUri);
		return extractLocationDeviceObservationValue(observationValueUri, query);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getValueForObservation")
	public ObservationValue getValueForObservation(
			@DefaultValue("") @QueryParam("observationUri") String observationUri) {
		observationUri = observationUri.trim();
		if ("".equals(observationUri)) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"observationUri is null or empty"));
		}

		// setup and execute the query
		String query = ObservationQueries
				.getLocationDeviceObservationValueForObservationQuery(observationUri);
		return extractLocationDeviceObservationValue(observationUri, query);
	}

	private ObservationValue extractLocationDeviceObservationValue(String uri,
			String query) {
		ResultSet results = observationEndpoint.query(new Query(query));

		List<String> resultVars = results.getResultVars();
		// process the results
		LocationDeviceObservationValue value = new LocationDeviceObservationValue();
		if (results.hasNext()) {
			QuerySolution qs = results.next();
			value.setLatitude(Util.getNodeDoubleValue(qs.get(resultVars.get(0))));
			value.setLongitude(Util.getNodeDoubleValue(qs.get(resultVars.get(1))));
			value.setEasting(Util.getNodeDoubleValue(qs.get(resultVars.get(2))));
			value.setNorthing(Util.getNodeDoubleValue(qs.get(resultVars.get(3))));
			value.setAccuracy(Util.getNodeDoubleValue(qs.get(resultVars.get(4))));
			value.setHeading(Util.getNodeDoubleValue(qs.get(resultVars.get(5))));
			value.setSpeed(Util.getNodeDoubleValue(qs.get(resultVars.get(6))));
			String valueUri = Util.getNodeValue(qs.get(resultVars.get(7)));
			if (valueUri != null && !("".equals(valueUri.trim()))) {
				value.setUri(valueUri.trim());
			} else
				value.setUri(uri);
			value.setDistanceMoved(Util.getNodeDoubleValue(qs.get(resultVars
					.get(8))));
		}
		return value;
	}

	@DELETE
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("delete")
	public void delete(
			@DefaultValue("") @QueryParam("observationUri") String observationUri,
			@DefaultValue("") @QueryParam("authenticationToken") String authenticationToken) {
		observationUri = observationUri.trim();
		authenticationToken = authenticationToken.trim();

		if (observationUri.equals("") && authenticationToken.equals(""))
			throw new ExceptionReporter(
					new NullPointerException(
							"'observationUri' or 'authenticationToken' is needed to delete the user."));

		String accuracyQuery = ObservationQueries
				.deleteLocationDeviceObservationAccuracy(observationUri);
		String headingQuery = ObservationQueries
				.deleteLocationDeviceObservationHeading(observationUri);
		String speedQuery = ObservationQueries
				.deleteLocationDeviceObservationSpeed(observationUri);
		String methodQuery = ObservationQueries
				.deleteLocationDeviceObservationSensingMethodUsed(observationUri);
		String obsQuery = ObservationQueries
				.deleteLocationDeviceObservation(observationUri);

		observationEndpoint.update(new Query(accuracyQuery), new Query(
				headingQuery), new Query(speedQuery), new Query(methodQuery),
				new Query(obsQuery));
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getBusLocationsOn{Line}")
	public BusLocations getBusLocationsOn(@PathParam("Line") String line,
			@DefaultValue("") @QueryParam("lineUri") String lineUri,
			@DefaultValue("") @QueryParam("direction") String direction) {

		if ("".equals(lineUri.trim())) {
			throw new ExceptionReporter(new NullPointerException(
					"'lineUri' is null or empty"));
		}
		if ("".equals(direction.trim())) {
			throw new ExceptionReporter(new IllegalArgumentException(
					"'direction' is null or an empty "));
		}

		long now = System.currentTimeMillis();
		// get all the users on that line that have contributed a location in
		// the last 5 minutes
		String usersQuery = ObservationQueries.getUsersSubmittedSince(lineUri,
				direction, now - 300000L);

		// for each user, get their latest location
		Query usersSparqlQuery = new Query(usersQuery);
		ResultSet usersSet = observationEndpoint.query(usersSparqlQuery);
		List<String> usersVars = usersSet.getResultVars();

		// somewhere to store the bus locations
		List<Location> busLocations = new ArrayList<Location>();

		while (usersSet.hasNext()) {
			QuerySolution solution = usersSet.next();
			String userUri = Util.getNodeValue(solution.get(usersVars.get(0)));
			String journeyUri = Util
					.getNodeValue(solution.get(usersVars.get(1)));

			// get the latest map matched location from that user
			Location l = executeBusLocationQuery(ObservationQueries
					.getLatestMapMatchedLocationFromUser(userUri, lineUri,
							direction, journeyUri));
			if (l != null) {
				busLocations.add(l);
			} else {
				l = executeBusLocationQuery(ObservationQueries
						.getLatestLocationFromUser(userUri, lineUri, direction,
								journeyUri));
				System.out.println("raw obs " + (l != null));
				if (l != null) {
					busLocations.add(l);
				}
			}

		}

		return new BusLocations(busLocations);
	}

	private Location executeBusLocationQuery(String query) {
		Query locationSparqlQuery = new Query(query);

		ResultSet busLocationsOnRoute = observationEndpoint
				.query(locationSparqlQuery);
		List<String> locationVars = busLocationsOnRoute.getResultVars();
		Location location = null;
		if (busLocationsOnRoute.hasNext()) {
			QuerySolution locationSolution = busLocationsOnRoute.next();

			location = new Location();
			location.setTime(Util.getNodeValue(
					locationSolution.get(locationVars.get(0))).trim());
			location.setEasting(Double.parseDouble(Util.getNodeValue(
					locationSolution.get(locationVars.get(1))).trim()));
			location.setNorthing(Double.parseDouble(Util.getNodeValue(
					locationSolution.get(locationVars.get(2))).trim()));
			location.setLongitude(Double.parseDouble(Util.getNodeValue(
					locationSolution.get(locationVars.get(3))).trim()));
			location.setLatitude(Double.parseDouble(Util.getNodeValue(
					locationSolution.get(locationVars.get(4))).trim()));
			location.setUri(Util.getNodeValue(
					locationSolution.get(locationVars.get(5))).trim());
			if (locationVars.size() == 7)
				location.setDerivedFrom(Util.getNodeValue(
						locationSolution.get(locationVars.get(6))).trim());
		} else {
			System.out.println("  No match for bus location query " + query);
		}
		return location;
	}
}
