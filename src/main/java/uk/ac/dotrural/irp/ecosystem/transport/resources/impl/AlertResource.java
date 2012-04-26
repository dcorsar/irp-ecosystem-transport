package uk.ac.dotrural.irp.ecosystem.transport.resources.impl;

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
import uk.ac.dotrural.irp.ecosystem.transport.Util;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.alert.Alert;
import uk.ac.dotrural.irp.ecosystem.transport.queries.alert.BusAlertQueries;
import uk.ac.dotrural.irp.ecosystem.transport.queries.alert.QueryReader;

@Path("/alert")
@Scope("request")
public class AlertResource implements RESTFulSPARQL
{
  @Context
  private UriInfo uriInfo;
  
  private SPARQLEndpoint alertEndpoint;
  
  public void setAlertEndpoint(SPARQLEndpoint gisRoadMapEndpoint)
  {
    this.alertEndpoint = gisRoadMapEndpoint;
  }
  
  public SystemMessage init(ServiceInitialiser si)
  {
    return alertEndpoint.init(uriInfo, si);
  }

  public void update(Query query)
  {
    alertEndpoint.update(query);
  }

  public String query(Query query)
  {
    return uk.ac.dotrural.irp.ecosystem.core.util.Util.resultsetToString(alertEndpoint.query(query));
  }

  public EndpointInfo info()
  {
    return alertEndpoint.info();
  }

  @POST
  @Consumes({MediaType.APPLICATION_JSON})
  @Produces({MediaType.APPLICATION_JSON})
  @Path("create")
  public void create(Alert alert)
  {
    if(alert == null)
      throw new ExceptionReporter(new NullPointerException("No 'Alert' creation information given."));
    if(alert.getUserUri() == null || alert.getUserUri().equals(""))
      throw new ExceptionReporter(new NullPointerException("No 'userUri' given."));
    if(alert.getLineUri() == null || alert.getLineUri().equals(""))
      throw new ExceptionReporter(new NullPointerException("No 'lineUri' given."));
    if(alert.getDays() == null)
      throw new ExceptionReporter(new NullPointerException("No 'days' specified."));
    
    String newAlertUri = QueryReader.getString("BusAlertQueries.query.create.baseNS") + UUID.randomUUID().toString();
    String query = BusAlertQueries.getCreateAlertUpdate(newAlertUri, 
                                                        alert.getUserUri(), 
                                                        alert.getLineUri(), 
                                                        alert.getInbound(), 
                                                        alert.getLatitude(), alert.getLongitude(), 
                                                        alert.getStartTimeHour(), alert.getStartTimeMin(), 
                                                        alert.getEndTimeHour(), alert.getEndTimeMin(), 
                                                        alert.getMinutesBefore(), 
                                                        Util.getServiceDays(alert.getDays()));
    System.out.format("  (INFO) : %s", query);
  }
}
