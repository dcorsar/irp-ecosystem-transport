package uk.ac.dotrural.irp.ecosystem.transport.queries.timetable;

import java.util.Calendar;

public class TimetableQueries {

	public static String getRoutesInAdminAreaQuery(String adminAreaUri,
			boolean includeDirections) {
		return getLinesInAdminAreaQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.route.type"),
				(includeDirections) ? String.format(
						QueryReader
								.getString("TimetableQueries.query.get.lines.direction"),
						QueryReader
								.getString("TimetableQueries.query.get.lines.route.property"))
						: "", adminAreaUri);
	}

	public static String getServicesInAdminAreaQuery(String adminAreaUri,
			boolean includeDirections) {
		return getLinesInAdminAreaQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.type"),
				(includeDirections) ? String.format(
						QueryReader
								.getString("TimetableQueries.query.get.lines.direction"),
						QueryReader
								.getString("TimetableQueries.query.get.lines.service.property"))
						: "", adminAreaUri);
	}

	private static String getLinesInAdminAreaQuery(String lineType,
			String directionQuery, String adminAreaUri) {
		return String.format(
				QueryReader.getString("TimetableQueries.query.get.lines"),
				lineType, adminAreaUri, directionQuery);
	}

	public static String getRouteDirectionsQuery(String routeUri) {
		return getLineDirectionsQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.route.property"),
				routeUri);
	}

	public static String getServiceDirectionsQuery(String serviceUri) {
		return getLineDirectionsQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.property"),
				serviceUri);

	}

	private static String getLineDirectionsQuery(String lineLinkProperty,
			String lineUri) {
		return String.format(
				QueryReader.getString("TimetableQueries.query.get.direction"),
				lineLinkProperty, lineUri);
	}

	public static String getServicesOnRouteQuery(String routeUri) {
		return String.format(QueryReader
				.getString("TimetableQueries.query.get.servicesOnRoute"),
				routeUri);
	}

	public static String getBusLocationsOnRouteQuery(String routeUri,
			boolean inbound) {
		return getBusLocationsOnLine(
				QueryReader
						.getString("TimetableQueries.query.get.lines.route.property"),
				routeUri, inbound);
	}

	public static String getBusLocationsOnServiceQuery(String serviceUri,
			boolean inbound) {
		return getBusLocationsOnLine(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.property"),
				serviceUri, inbound);
	}

	private static String getBusLocationsOnLine(String lineLink,
			String lineUri, boolean inbound) {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = getDayOfWeekProperty(cal);
		String time = currentTimeString(cal).toString();
		String query = String
				.format(QueryReader
						.getString("TimetableQueries.query.get.busLocation"),
						lineLink,
						lineUri,
						((inbound) ? QueryReader
								.getString("TimetableQueries.query.get.busLocation.inbound")
								: QueryReader
										.getString("TimetableQueries.query.get.busLocation.outbound")),
						dayOfWeek, time, time);
		return query;
	}

	public static String getPreviousBusAtStopOnRoute(String routeUri,
			boolean inbound, String stopUri, int number) {
		return getPreviousBusAtStop(
				QueryReader
						.getString("TimetableQueries.query.get.lines.route.property"),
				routeUri, inbound, stopUri, number);
	}

	public static String getPreviousBusAtStopOnService(String serviceUri,
			boolean inbound, String stopUri, int number) {
		return getPreviousBusAtStop(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.property"),
				serviceUri, inbound, stopUri, number);
	}

	public static String getPreviousBusAtStop(String lineLink, String lineUri,
			boolean inbound, String stopUri, int number) {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = getDayOfWeekProperty(cal);
		String time = currentTimeString(cal).toString();
		String query = String
				.format(QueryReader
						.getString("TimetableQueries.query.get.previousBusAtStop"),
						lineLink,
						lineUri,
						((inbound) ? QueryReader
								.getString("TimetableQueries.query.get.busLocation.inbound")
								: QueryReader
										.getString("TimetableQueries.query.get.busLocation.outbound")),
						dayOfWeek, time, stopUri, stopUri, Integer
								.toString(number));
		return query;
	}

	public static String getNextBusAtStopOnRoute(String routeUri,
			boolean inbound, String stopUri, int number) {
		return getNextBusAtStop(
				QueryReader
						.getString("TimetableQueries.query.get.lines.route.property"),
				routeUri, inbound, stopUri, number);
	}

	public static String getNextBusAtStopOnService(String serviceUri,
			boolean inbound, String stopUri, int number) {
		return getNextBusAtStop(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.property"),
				serviceUri, inbound, stopUri, number);
	}

	public static String getNextBusAtStop(String lineLink, String lineUri,
			boolean inbound, String stopUri, int number) {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = getDayOfWeekProperty(cal);
		String time = currentTimeString(cal).toString();
		String query = String
				.format(QueryReader
						.getString("TimetableQueries.query.get.nextBusAtStop"),
						lineLink,
						lineUri,
						((inbound) ? QueryReader
								.getString("TimetableQueries.query.get.busLocation.inbound")
								: QueryReader
										.getString("TimetableQueries.query.get.busLocation.outbound")),
						dayOfWeek, time, stopUri, stopUri, Integer
								.toString(number));
		return query;
	}

	private static String getDayOfWeekProperty(Calendar cal) {
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.monday");
		case Calendar.TUESDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.tuesday");
		case Calendar.WEDNESDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.wednesday");
		case Calendar.THURSDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.thursday");
		case Calendar.FRIDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.friday");
		case Calendar.SATURDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.saturday");
		case Calendar.SUNDAY:
			return QueryReader
					.getString("TimetableQueries.query.get.busLocation.sunday");
		}
		return "[]";
	}

	public static String getBusStopsOnServiceQuery(String serviceUri,
			boolean inbound) {
		return getBusStopsQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.property"),
				serviceUri,
				((inbound) ? QueryReader
						.getString("TimetableQueries.query.get.busLocation.inbound")
						: QueryReader
								.getString("TimetableQueries.query.get.busLocation.outbound")));
	}

	public static String getBusStopsOnRouteQuery(String routeUri,
			boolean inbound) {
		return getBusStopsQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.route.property"),
				routeUri,
				((inbound) ? QueryReader
						.getString("TimetableQueries.query.get.busLocation.inbound")
						: QueryReader
								.getString("TimetableQueries.query.get.busLocation.outbound")));
	}

	private static String getBusStopsQuery(String lineProperty, String lineUri,
			String direction) {
		Calendar cal = Calendar.getInstance();
		String dayOfWeek = getDayOfWeekProperty(cal);
		return String.format(
				QueryReader.getString("TimetableQueries.query.get.stopsOn"),
				lineProperty, lineUri, direction, dayOfWeek);
	}

	private static StringBuilder currentTimeString(Calendar cal) {
		StringBuilder time = new StringBuilder("");
		if (cal.get(Calendar.HOUR_OF_DAY) < 10) {
			time.append("0");
		}
		time.append(cal.get(Calendar.HOUR_OF_DAY)).append(":");
		if (cal.get(Calendar.MINUTE) < 10) {
			time.append("0");
		}
		time.append(cal.get(Calendar.MINUTE)).append(":00");
		return time;
	}

	public static String getKmlRouteQuery(String lineUri, boolean inbound) {
		return getKmlQuery(QueryReader
				.getString("TimetableQueries.query.get.lines.route.property"),
		lineUri,
		((inbound) ? QueryReader
				.getString("TimetableQueries.query.get.busLocation.inbound")
				: QueryReader
						.getString("TimetableQueries.query.get.busLocation.outbound")));
	}

	public static String getKmlServiceQuery(String lineUri, boolean inbound) {
		return getKmlQuery(
				QueryReader
						.getString("TimetableQueries.query.get.lines.service.property"),
				lineUri,
				((inbound) ? QueryReader
						.getString("TimetableQueries.query.get.busLocation.inbound")
						: QueryReader
								.getString("TimetableQueries.query.get.busLocation.outbound")));
	}

	private static String getKmlQuery(String lineProperty, String lineUri,
			String direction) {
		return String.format(
				QueryReader.getString("TimetableQueries.query.get.kml"),
				direction, lineProperty, lineUri);
	}
}
