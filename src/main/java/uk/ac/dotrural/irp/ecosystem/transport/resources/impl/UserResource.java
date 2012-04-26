package uk.ac.dotrural.irp.ecosystem.transport.resources.impl;

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
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.user.User;
import uk.ac.dotrural.irp.ecosystem.transport.models.jaxb.user.UserCreation;
import uk.ac.dotrural.irp.ecosystem.transport.queries.user.QueryReader;
import uk.ac.dotrural.irp.ecosystem.transport.queries.user.UserQueries;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

@Path("/user")
@Scope("request")
public class UserResource implements RESTFulSPARQL {
	@Context
	private UriInfo uriInfo;

	private SPARQLEndpoint userEndpoint;

	public void setUserEndpoint(SPARQLEndpoint userEndpoint) {
		this.userEndpoint = userEndpoint;
	}

	public SystemMessage init(ServiceInitialiser si) {
		return userEndpoint.init(uriInfo, si);
	}

	public void update(Query query) {
		userEndpoint.update(query);
	}

	public String query(Query query) {
		return Util.resultsetToString(userEndpoint.query(query));
	}

	public EndpointInfo info() {
		return userEndpoint.info();
	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("create")
	public UserCreation create(User userDetails) {
		System.out.println("creating user 1");
		if (userDetails == null)
			throw new ExceptionReporter(new NullPointerException(
					"No 'UserDetails' given."));

		if (userDetails.getNickname() == null
				|| userDetails.getNickname().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Nickname needed to create the user."));

		if (userDetails.getEmail() == null
				|| userDetails.getEmail().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Email needed to create the user."));

		if (userDetails.getPassword() == null
				|| userDetails.getPassword().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Password needed to create the user."));

		System.out.println("creating users");
		String userUrl = "";
		UserCreation newUser = new UserCreation();
		String query = UserQueries.getExistsQuery(userDetails.getEmail());
		Query sparqlQuery = new Query(query);
		if (!userEndpoint.ask(sparqlQuery)) {
			userUrl = QueryReader.getString("UserQueries.baseNS")
					+ UUID.randomUUID().toString();
			query = UserQueries.getCreateUserUpdate(userUrl.trim(), userDetails
					.getNickname().trim(), userDetails.getEmail().trim(),
					userDetails.getPassword().trim());
			sparqlQuery = new Query(query);
			userEndpoint.update(sparqlQuery);
			System.out.println("created user");
			newUser.setCreated(true);
			newUser.setUserUri(userUrl);
		} else{
			newUser.setCreated(false);
			newUser.setReason("User with email " + userDetails.getEmail() + " already exists");
		}
		return newUser;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("get")
	public User get(
			@DefaultValue("") @QueryParam("email") String email,
			@DefaultValue("") @QueryParam("userUri") String userUri,
			@DefaultValue("") @QueryParam("authenticationToken") String authenticationToken) {
//		email = email.trim();
//		userUri = userUri.trim();
//		authenticationToken = authenticationToken.trim();

		if ("".equals(email.trim()) && "".equals(userUri.trim())
				&& "".equals(authenticationToken.trim()))
			throw new ExceptionReporter(
					new NullPointerException(
							"'email' or 'userUri' or 'authenticationToken' is needed to retrieve the user."));

		User user = getUser(email, userUri);

		return user;
	}

	/**
	 * If email is an empty string, then gets user by URI; if userUri is null,
	 * then gets user by email
	 * 
	 * @param email
	 * @param userUri
	 * @return
	 */
	private User getUser(String email, String userUri) {
		String query = "";
		if (!email.equals(""))
			query = UserQueries.getUserByEmailQuery(email);
		else if (!userUri.equals(""))
			query = UserQueries.getUserByUriQuery(userUri);
		else
			return new User();

		Query sparqlQuery = new Query(query);
		ResultSet results = userEndpoint.query(sparqlQuery);

		List<String> vars = results.getResultVars();
		User user = new User();

		while (results.hasNext()) {
			QuerySolution solution = results.next();

			user.setNickname(Util.getNodeValue(solution.get(vars.get(0)))
					.trim());
			user.setUserUri(Util.getNodeValue(solution.get(vars.get(1))).trim());
			user.setExists(true);
		}
		return user;
	}

	@DELETE
	@Path("delete")
	public void delete(
			@DefaultValue("") @QueryParam("userUri") String userUri,
			@DefaultValue("") @QueryParam("authenticationToken") String authenticationToken) {
		userUri = userUri.trim();
		authenticationToken = authenticationToken.trim();

		if (userUri.equals("") && authenticationToken.equals(""))
			throw new ExceptionReporter(
					new NullPointerException(
							"'email' or 'userUri' or 'authenticationToken' is needed to delete the user."));

		String query = UserQueries.getDeleteUserUpdate(userUri);
		Query sparqlQuery = new Query(query);
		userEndpoint.update(sparqlQuery);
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("update")
	public void update(User userDetails) {
		if (userDetails == null)
			throw new ExceptionReporter(new NullPointerException(
					"No 'UserDetails' given."));

		if (userDetails.getUserUri() == null
				|| userDetails.getUserUri().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"User URI needed to update the user."));

		if (userDetails.getAuthenticationToken() == null
				|| userDetails.getAuthenticationToken().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Authentication token needed to update the user."));

		if (userDetails.getEmail() == null
				|| userDetails.getEmail().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Email needed to update the user."));

		if (userDetails.getPassword() == null
				|| userDetails.getPassword().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Password needed to update the user."));

		if (userDetails.getNickname() == null
				|| userDetails.getNickname().trim().equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"Nickname needed to update the user."));

		String query = UserQueries.getUpdateUserUpdate(userDetails.getUserUri()
				.trim(), userDetails.getNickname().trim(), userDetails
				.getEmail().trim(), userDetails.getPassword().trim());
		Query sparqlQuery = new Query(query);
		userEndpoint.update(sparqlQuery);
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("isValidLoginCredentials")
	public User isValidLoginCredentials(
			@DefaultValue("") @QueryParam("email") String email,
			@DefaultValue("") @QueryParam("password") String password) {
		email = email.trim();
		password = password.trim();

		if (email.equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"No 'email' given."));
		if (password.equals(""))
			throw new ExceptionReporter(new NullPointerException(
					"No 'password' given."));

		String query = UserQueries.getIsValidLoginCredentialsQuery(email,
				password);
		System.out.println( query);
		Query sparqlQuery = new Query(query);
		boolean isValid = userEndpoint.ask(sparqlQuery);

		User u = null;
		if (isValid) {
			u = getUser(email, "");
			String token = generateAuthToken();
			String update = UserQueries.getCreateAuthTokenQuery(u.getUserUri(), token);
			userEndpoint.update(new Query(update));
			u.setAuthenticationToken(token);
		} else {
			u = new User(false);
		}

		return u;
	}
	
	private String generateAuthToken(){
		return UUID.randomUUID().toString();
	}

}
