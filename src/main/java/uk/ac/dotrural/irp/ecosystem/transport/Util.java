package uk.ac.dotrural.irp.ecosystem.transport;

import java.util.ArrayList;
import java.util.List;

import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.timetable.Line;
import uk.ac.dotrural.irp.ecosystem.transport.queries.alert.ServiceDays;

public class Util {
	public static ServiceDays[] getServiceDays(boolean... days) {
		ArrayList<ServiceDays> selectedDays = new ArrayList<ServiceDays>();
		ServiceDays[] serviceDays = ServiceDays.values();

		for (int i = 0; i < days.length; i++) {
			if (days[i])
				selectedDays.add(serviceDays[i]);
		}
		return selectedDays.toArray(new ServiceDays[selectedDays.size()]);
	}
	public static Line contains(List<Line> lines, String lineUri) {
		if (lines.size() == 0)
			return null;

		for (Line route : lines) {
			if (route.getUri().equals(lineUri.trim()))
				return route;
		}

		return null;
	}
}
