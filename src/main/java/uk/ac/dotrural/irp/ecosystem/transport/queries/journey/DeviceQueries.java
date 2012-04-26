package uk.ac.dotrural.irp.ecosystem.transport.queries.journey;



/**
 * 
 * @author david corsar
 *
 */
public class DeviceQueries {
	public static String getCreateAndroidPhone(String phoneUri, String locationSensingDeviceUri, String osVersion, String deviceId){
		return getCreatePhoneString(QueryReader.getString("DeviceQueries.update.create.iPhone"), phoneUri, locationSensingDeviceUri, osVersion, deviceId);
	}

	public static String getCreateiPhone(String phoneUri, String locationSensingDeviceUri, String osVersion, String deviceId){
		return getCreatePhoneString(QueryReader.getString("DeviceQueries.update.create.android"), phoneUri, locationSensingDeviceUri, osVersion, deviceId);
	}
	
	private static String getCreatePhoneString(String queryString, String phoneUri, String locationSensingDeviceUri, String osVersion, String deviceId){
		return String.format(queryString, locationSensingDeviceUri, phoneUri, phoneUri, osVersion, deviceId);
	}
	
}
