package uk.ac.dotrural.irp.ecosystem.transport.queries.device;

import uk.ac.dotrural.irp.ecosystem.transport.queries.journey.QueryReader;

/**
 * 
 * @author david corsar
 * 
 */
public class DeviceQueries {
	public static String getCreateAndroidPhone(String phoneUri,
			String locationSensingDeviceUri, String osVersion, String deviceId) {
		return getCreatePhoneString(
				QueryReader.getString("DeviceQueries.update.create.iPhone"),
				phoneUri, locationSensingDeviceUri, osVersion, deviceId);
	}

	public static String getCreateiPhone(String phoneUri,
			String locationSensingDeviceUri, String osVersion, String deviceId) {
		return getCreatePhoneString(
				QueryReader.getString("DeviceQueries.update.create.android"),
				phoneUri, locationSensingDeviceUri, osVersion, deviceId);
	}

	private static String getCreatePhoneString(String queryString,
			String phoneUri, String locationSensingDeviceUri, String osVersion,
			String deviceId) {
		return String.format(queryString, locationSensingDeviceUri, phoneUri,
				phoneUri, osVersion, deviceId);
	}

	public static String getAndroidNetworkLocationSensing() {
		return QueryReader
				.getString("DeviceQueries.androidNetworkLocationSensing");
	}

	public static String getAndroidPassiveLocationSensing() {
		return QueryReader
				.getString("DeviceQueries.androidPassiveLocationSensing");
	}

	public static String getAndroidGPSLocationSensing() {
		return QueryReader
				.getString("DeviceQueries.androidGPSLocationSensinghttp");
	}

	public static String getiOSLocationSensing() {
		return QueryReader.getString("DeviceQueries.iOSLocationSensing");
	}

	public static String getBaseNS() {
		return QueryReader.getString("DeviceQueries.baseNS");
	}
}
