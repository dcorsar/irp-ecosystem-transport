package uk.ac.dotrural.irp.ecosystem.transport.resources.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.BusLocations;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Direction;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Directions;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Line;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Lines;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Location;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.StopTime;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.StopTimes;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.transport.BusStop;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.transport.BusStops;
import uk.ac.dotrural.irp.ecosystem.transport.queries.timetable.TimetableQueries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@Path("/timetable")
@Scope("request")
public class TimeTableResource implements RESTFulSPARQL {
	@Context
	private UriInfo uriInfo;

	private SPARQLEndpoint timeTableEndpoint;

	public void setTimeTableEndpoint(SPARQLEndpoint timeTablesEndpoint) {
		this.timeTableEndpoint = timeTablesEndpoint;
	}

	public SystemMessage init(ServiceInitialiser si) {
		return timeTableEndpoint.init(uriInfo, si);
	}

	public void update(Query query) {
		timeTableEndpoint.update(query);
	}

	public String query(Query query) {
		return Util.resultsetToString(timeTableEndpoint.query(query));
	}

	public EndpointInfo info() {
		return timeTableEndpoint.info();
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("get{Line}InAdminArea")
	public Lines getLinesInAdminArea(
			@PathParam("Line") String line,
			@DefaultValue("") @QueryParam("adminAreaUri") String adminAreaUri,
			@DefaultValue("false") @QueryParam("includeDirections") boolean includeDirections) {
		System.out.println("include directions " + includeDirections);
		line = line.trim();
		adminAreaUri = adminAreaUri.trim();

		if (adminAreaUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"'adminAreaUri' is NULL or empty."));

		String query = "";
		if (line.equals("Routes"))
			query = TimetableQueries.getRoutesInAdminAreaQuery(adminAreaUri,
					includeDirections);
		else
			query = TimetableQueries.getServicesInAdminAreaQuery(adminAreaUri,
					includeDirections);

		Query sparqlQuery = new Query(query);
		ResultSet results = timeTableEndpoint.query(sparqlQuery);

		List<Line> lines = new ArrayList<Line>();
		List<String> vars = results.getResultVars();

		while (results.hasNext()) {
			QuerySolution solution = results.next();

			String currentLineURI = Util
					.getNodeValue(solution.get(vars.get(0))).trim();
			Line route = uk.ac.dotrural.irp.ecosystem.transport.Util.contains(
					lines, currentLineURI);
			addRoute(lines, route, vars, solution);
		}

		return (new Lines(lines));
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("get{Line}Directions")
	public Directions getDirections(@PathParam("Line") String line,
			@DefaultValue("") @QueryParam("lineUri") String lineUri) {
		line = line.trim();
		lineUri = lineUri.trim();

		if (lineUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(line
					+ " 'uri' is NULL or empty."));

		String query = "";
		if (line.equals("Route"))
			query = TimetableQueries.getRouteDirectionsQuery(lineUri);
		else
			query = TimetableQueries.getServiceDirectionsQuery(lineUri);

		Query sparqlQuery = new Query(query);
		ResultSet results = timeTableEndpoint.query(sparqlQuery);

		List<Direction> directions = new ArrayList<Direction>();
		List<String> vars = results.getResultVars();
		while (results.hasNext()) {
			QuerySolution solution = results.next();

			Direction direction = new Direction();
			direction.setDirection(Util.getNodeValue(solution.get(vars.get(0)))
					.trim());
			direction.setDirectionDescription(Util.getNodeValue(
					solution.get(vars.get(1))).trim());

			directions.add(direction);
		}

		return new Directions(directions);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getServicesOnRoute")
	public Lines getServicesOnRoute(
			@DefaultValue("") @QueryParam("lineUri") String lineUri) {
		lineUri = lineUri.trim();

		if (lineUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"'routeUri' is NULL or empty."));

		String query = TimetableQueries.getServicesOnRouteQuery(lineUri);

		Query sparqlQuery = new Query(query);
		ResultSet servicesOnRoute = timeTableEndpoint.query(sparqlQuery);

		List<Line> lines = new ArrayList<Line>();
		List<String> vars = servicesOnRoute.getResultVars();
		while (servicesOnRoute.hasNext()) {
			QuerySolution solution = servicesOnRoute.next();

			Line line = new Line();
			line.setUri(Util.getNodeValue(solution.get(vars.get(0))).trim());
			line.setLabel(Util.getNodeValue(solution.get(vars.get(1))).trim());

			lines.add(line);
		}

		return new Lines(lines);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getBusLocationsOn{Line}")
	public BusLocations getBusLocations(@PathParam("Line") String line,
			@DefaultValue("") @QueryParam("lineUri") String lineUri,
			@DefaultValue("inbound") @QueryParam("direction") String direction) {
		line = line.trim();
		lineUri = lineUri.trim();
		direction = direction.trim();

		if (lineUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(line
					+ "line 'uri' is NULL or empty."));

		String query = "";
		if (line.equals("Route"))
			query = TimetableQueries.getBusLocationsOnRouteQuery(lineUri,
					direction.equals("inbound"));
		else
			query = TimetableQueries.getBusLocationsOnServiceQuery(lineUri,
					direction.equals("inbound"));

		Query sparqlQuery = new Query(query);
		ResultSet busLocationsOnRoute = timeTableEndpoint.query(sparqlQuery);

		List<Location> busLocations = new ArrayList<Location>();
		List<String> vars = busLocationsOnRoute.getResultVars();
		while (busLocationsOnRoute.hasNext()) {
			QuerySolution solution = busLocationsOnRoute.next();

			Location location = new Location();
			location.setTime(Util.getNodeValue(solution.get(vars.get(0)))
					.trim());
			location.setEasting(Double.parseDouble(Util.getNodeValue(
					solution.get(vars.get(3))).trim()));
			location.setNorthing(Double.parseDouble(Util.getNodeValue(
					solution.get(vars.get(4))).trim()));
			location.setLongitude(Double.parseDouble(Util.getNodeValue(
					solution.get(vars.get(5))).trim()));
			location.setLatitude(Double.parseDouble(Util.getNodeValue(
					solution.get(vars.get(6))).trim()));

			busLocations.add(location);
		}

		return new BusLocations(busLocations);
	}

	private void addRoute(List<Line> lines, Line line, List<String> vars,
			QuerySolution solution) {
		List<Direction> directions = null;
		if (line == null) {
			line = new Line();
			line.setUri(Util.getNodeValue(solution.get(vars.get(0))).trim());
			line.setLabel(Util.getNodeValue(solution.get(vars.get(1))).trim());
			line.setAltLabel(Util.getNodeValue(solution.get(vars.get(2)))
					.trim());

			directions = new ArrayList<Direction>();
			addDirection(directions,
					Util.getNodeValue(solution.get(vars.get(3))).trim(), Util
							.getNodeValue(solution.get(vars.get(4))).trim());

			line.setDirections(new Directions(directions));
			lines.add(line);
		} else {
			directions = line.getDirections().getDirections();
			addDirection(directions,
					Util.getNodeValue(solution.get(vars.get(3))).trim(), Util
							.getNodeValue(solution.get(vars.get(4))).trim());

			line.setDirections(new Directions(directions));
		}
	}

	private void addDirection(List<Direction> directions, String direction,
			String directionDescription) {
		Direction d = new Direction();
		d.setDirection(direction);
		d.setDirectionDescription(directionDescription);

		directions.add(d);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getStopTimesOn{Line}")
	public StopTimes getStopTimes(@PathParam("Line") String line,
			@DefaultValue("") @QueryParam("lineUri") String lineUri,
			@DefaultValue("inbound") @QueryParam("direction") String direction,
			@DefaultValue("") @QueryParam("stopUri") String stopUri) {

		line = line.trim();
		lineUri = lineUri.trim();
		direction = direction.trim();
		stopUri = stopUri.trim();

		if (lineUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(line
					+ "line 'uri' is NULL or empty."));
		if (stopUri.equals(""))
			throw new ExceptionReporter(new NullPointerException(line
					+ "stop 'uri' is NULL or empty."));

		StopTimes stopTimes = new StopTimes();
		addPreviousTimeAtStop(line, lineUri, direction, stopUri, stopTimes, 1);
		addNextTimeAtStop(line, lineUri, direction, stopUri, stopTimes, 1);

		return stopTimes;
	}

	private void addPreviousTimeAtStop(String line, String lineUri,
			String direction, String stopUri, StopTimes stopTimes, int number) {
		String query = "";
		if (line.equals("Route"))
			query = TimetableQueries.getPreviousBusAtStopOnRoute(lineUri,
					direction.equals("inbound"), stopUri, number);
		else
			query = TimetableQueries.getPreviousBusAtStopOnService(lineUri,
					direction.equals("inbound"), stopUri, number);

		Query sparqlQuery = new Query(query);
		ResultSet busLocationsOnRoute = timeTableEndpoint.query(sparqlQuery);

		List<String> vars = busLocationsOnRoute.getResultVars();
		while (busLocationsOnRoute.hasNext()) {
			QuerySolution solution = busLocationsOnRoute.next();

			StopTime st = new StopTime();
			st.setDepartureTime(Util.getNodeValue(solution.get(vars.get(0)))
					.trim());
			st.setArrivalTime(Util.getNodeValue(solution.get(vars.get(1)))
					.trim());
			stopTimes.addPreviousBus(st);

			stopTimes.setLabel(Util.getNodeValue(solution.get(vars.get(2))));
		}
	}

	private void addNextTimeAtStop(String line, String lineUri,
			String direction, String stopUri, StopTimes stopTimes, int number) {
		String query = "";
		if (line.equals("Route"))
			query = TimetableQueries.getNextBusAtStopOnRoute(lineUri,
					direction.equals("inbound"), stopUri, number);
		else
			query = TimetableQueries.getNextBusAtStopOnService(lineUri,
					direction.equals("inbound"), stopUri, number);

		Query sparqlQuery = new Query(query);
		ResultSet busLocationsOnRoute = timeTableEndpoint.query(sparqlQuery);

		List<String> vars = busLocationsOnRoute.getResultVars();
		while (busLocationsOnRoute.hasNext()) {
			QuerySolution solution = busLocationsOnRoute.next();

			StopTime st = new StopTime();
			st.setArrivalTime(Util.getNodeValue(solution.get(vars.get(0)))
					.trim());
			st.setDepartureTime(Util.getNodeValue(solution.get(vars.get(1)))
					.trim());
			stopTimes.addNextBus(st);

			stopTimes.setLabel(Util.getNodeValue(solution.get(vars.get(2))));
		}
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("getBusStopsOn{Line}")
	public BusStops getBusStopsOn(@PathParam("Line") String line,
			@DefaultValue("") @QueryParam("lineUri") String lineUri,
			@DefaultValue("false") @QueryParam("direction") String direction) {

		String query, kmlQuery;
		if ("service".equals(line.toLowerCase())) {
			query = TimetableQueries.getBusStopsOnServiceQuery(lineUri,
					"inbound".equals(direction));
			kmlQuery = TimetableQueries.getKmlServiceQuery(lineUri,
					"inbound".equals(direction));
		} else if ("route".equals(line.toLowerCase())) {
			query = TimetableQueries.getBusStopsOnRouteQuery(lineUri,
					"inbound".equals(direction));
			kmlQuery = TimetableQueries.getKmlRouteQuery(lineUri,
					"inbound".equals(direction));
		} else {
			throw new ExceptionReporter(
					"Unrecognised line type - should be Service or Route but was "
							+ line);
		}

		Query sparqlQuery = new Query(query);
		ResultSet servicesOnRoute = timeTableEndpoint.query(sparqlQuery);

		List<BusStop> stops = new ArrayList<BusStop>();
		List<String> vars = servicesOnRoute.getResultVars();
		while (servicesOnRoute.hasNext()) {
			QuerySolution solution = servicesOnRoute.next();
			BusStop stop = new BusStop();
			stop.setUri(Util.getNodeValue(solution.get(vars.get(0))).trim());
			stop.setEasting(Util.getNodeDoubleValue(solution.get(vars.get(1))));
			stop.setNorthing(Util.getNodeDoubleValue(solution.get(vars.get(2))));
			stop.setLatitude(Util.getNodeDoubleValue(solution.get(vars.get(3))));
			stop.setLongitude(Util.getNodeDoubleValue(solution.get(vars.get(4))));

			stops.add(stop);
		}
		BusStops busStops = new BusStops(stops);
		busStops.setKml(getKml(kmlQuery));
		return busStops;
	}

	private String getKml(String query){
		Query sparqlQuery = new Query(query);
		ResultSet kmlFile = timeTableEndpoint.query(sparqlQuery);

		List<String> vars = kmlFile.getResultVars();
		String kml ="null";
		if (kmlFile.hasNext()) {
			QuerySolution solution = kmlFile.next();
			kml =Util.getNodeValue(solution.get(vars.get(0))).trim();
		}
		return kml;
	}
}
