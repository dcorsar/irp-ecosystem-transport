package uk.ac.dotrural.irp.ecosystem.transport.queries.observation;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class ObservationQueries {

	public static String createLocationDeviceObservationUpdate(
			String journeyUri, double latitude, double longitude, long gpsTime,
			long deviceTime, Integer accuracy, Double heading, Double speed,
			String observationUri, String outputUri, String valueUri,
			long serverTime, String deviceSensorUri,
			String deviceLocationSensingUri) {

		// create all the value triples for each optional property
		StringBuilder values = new StringBuilder();
		if (accuracy != null) {
			values.append(String.format(
					QueryReader
							.getString("ObservationQueries.locationDevice.update.create.accuracy"),
					accuracy));
		}
                if (heading != null) {
			values.append(String.format(
					QueryReader
							.getString("ObservationQueries.locationDevice.update.create.heading"),
					heading));
		}
                if (speed != null) {
			values.append(String.format(
					QueryReader
							.getString("ObservationQueries.locationDevice.update.create.speed"),
					speed));

		}

		OSRef osRef = new LatLng(latitude, longitude).toOSRef();
		// determine easting and northing
		double easting = osRef.getEasting();
		double northing = osRef.getNorthing();

		// create value triple for sensing method used by if provided
		String sensingMethodUsedTriple = (null == deviceLocationSensingUri) ? ""
				: String.format(
						QueryReader
								.getString("ObservationQueries.locationDevice.update.create.sensingMethodUsed"),
						deviceLocationSensingUri);

		String updateQuery = String.format(QueryReader
				.getString("ObservationQueries.locationDevice.update.create"),
				valueUri, values.toString(), latitude, longitude, easting,
				northing, outputUri, valueUri, observationUri, outputUri,
				sensingMethodUsedTriple, journeyUri, deviceSensorUri,
				deviceTime, gpsTime, serverTime);
		return updateQuery.toString();
	}

	public static String createMapMatchedLocationObservation(String valueUri,
			double easting, double northing, double distanceMoved,
			String sensorOutputUri, String observationUri, String journeyUri,
			long resultTime, long samplingTime, String originalObservationUri, String nextNode) {

		// determine lat long
		LatLng latlng = new OSRef(easting, northing).toLatLng();

		String updateQuery = String
				.format(QueryReader
						.getString("ObservationQueries.mapmatchedlocation.update.create"),
						valueUri, latlng.getLat(), latlng.getLng(), easting, northing,
						distanceMoved, nextNode, sensorOutputUri, valueUri,
						observationUri, sensorOutputUri, journeyUri,
						resultTime, samplingTime, resultTime, originalObservationUri

				);
		return updateQuery;

	}

	public static String deleteLocationObservationUpdate(String observationUri) {
		return String.format(
				QueryReader.getString("ObservationQueries.update.delete"),
				observationUri, observationUri);
	}

	public static String getLocationDeviceObservationQuery(String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.query.get.observation"),
						observationUri, observationUri, observationUri, observationUri);
	}

	public static String getLocationDeviceSensorOutputQuery(
			String sensorOutputUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.query.get.sensorOutput"),
						sensorOutputUri);
	}

	public static String getLocationDeviceObservationValueQuery(
			String observationValueUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.query.get.observationValue"),
						observationValueUri, observationValueUri,
						observationValueUri, observationValueUri, observationValueUri);
	}

	public static String getLocationDeviceObservationValueForObservationQuery(
			String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.query.get.outputValueForObservation"),
						observationUri);
	}

	public static Object getLocationDeviceObservationType() {
		return QueryReader
				.getString("ObservationQueries.type.locationDeviceObservation");
	}

	public static String deleteLocationDeviceObservation(String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.update.delete.observation"),
						observationUri, observationUri);

	}

	public static String deleteLocationDeviceObservationSensingMethodUsed(
			String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.update.delete.observation.sensingMethodUsed"),
						observationUri, observationUri);
	}

	public static String deleteLocationDeviceObservationSpeed(
			String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.update.delete.observation.speed"),
						observationUri);
	}

	public static String deleteLocationDeviceObservationHeading(
			String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.update.delete.observation.heading"),
						observationUri);
	}

	public static String deleteLocationDeviceObservationAccuracy(
			String observationUri) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.update.delete.observation.accuracy"),
						observationUri);
	}

	public static String getUsersSubmittedSince(String lineUri,
			String direction, long time) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.get.line.usersInLast"),
						lineUri, direction, time);
	}

//	public static String getLatestMapMatchedLocationFromUser(String userUri,
//			String lineUri, String direction, String foi) {
//		return String
//				.format(QueryReader
//						.getString("ObservationQueries.locationDevice.get.line.userLatestMapMatched"),foi,foi,
//						lineUri, direction, userUri);
//	}

        public static String getLatestMapMatchedFromUser(String userUri,
			String lineUri, String direction, String foi) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.get.line.userLatestMapMatched"),foi,foi,
						lineUri, direction, userUri);
	}

	public static String getLatestLocationFromUser(String userUri,
			String lineUri, String direction, String foi) {
		return String
				.format(QueryReader
						.getString("ObservationQueries.locationDevice.get.line.userLatestRaw"),foi,foi,
						lineUri, direction, userUri);
	}
}
