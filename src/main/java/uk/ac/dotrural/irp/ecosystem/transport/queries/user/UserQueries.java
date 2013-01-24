/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.transport.queries.user;

import java.util.UUID;

import uk.ac.dotrural.irp.ecosystem.core.models.jaxb.system.Query;

/**
 * @author david corsar
 * 
 */
public class UserQueries {
  public static String getUserByEmailQuery(String email) {
    return String.format(QueryReader.getString("UserQueries.query.get.email"),
        email);
  }

	public static String getUserByUriQuery(String userUri) {
		return String.format(QueryReader.getString("UserQueries.query.get.uri"),
				userUri);
	}

	public static String getExistsQuery(String email) {
		return String.format(QueryReader.getString("UserQueries.query.exists"),
				email);
	}

	public static String getUserQuery(String email, String password){
		return String.format(QueryReader.getString("UserQueries.query.get.emailPassword"),
				email, encodePassword(password));
	}
	
	public static String getCreateAuthTokenQuery(String userUri, String token){
		return String.format(QueryReader.getString("UserQueries.update.create.authToken"),
				userUri, token);
	}
	
	
	public static String getIsValidLoginCredentialsQuery(String email,
			String password) {
		return String.format(QueryReader.getString("UserQueries.query.isValidLogin"),
				email, encodePassword(password));
	}

	public static String getCreateUnactivatedUserUpdate(String nickname, String email, String password, String activationToken) {
		String userUrl = QueryReader
				.getString("UserQueries.query.create.baseNS")
				+ UUID.randomUUID().toString();
		return getCreateUnactivatedUserUpdate(userUrl, nickname, email, password, activationToken);
	}

	public static String getCreateUnactivatedUserUpdate(String userUri, String nickname, String email,
			String password, String activationToken) {
		return String.format(
				QueryReader.getString("UserQueries.update.create"), userUri, nickname,
				email, encodePassword(password), activationToken);
	}

	public static String getDeleteUserUpdate(String userUri) {
		String query = String.format(
				QueryReader.getString("UserQueries.update.delete"), userUri,
				userUri);
		return query;
	}
	
	public static String getUpdateUserUpdate(String userUri, String nickname, String email, String password){
		return String.format(QueryReader.getString("UserQueries.update"), userUri, userUri, nickname, email, password, userUri);
	}

	private static String encodePassword(String password) {
		return password;
	}

	public static String getActiviationDetailsQuery(String userEmail) {
		// to do
		return String.format(QueryReader.getString("UserQueries.query.get.activitationDetails"), userEmail);
	}

	public static String getActivateUpdate(String email, String token) {
		return String.format(QueryReader.getString("UserQueries.update.activate"), token, email, token);
	}

	public static String getCheckActivationDetails(String email, String token) {
		return String.format(QueryReader.getString("UserQueries.query.checkActiviationDetails"), email, token);
	}

	public static String getIsActivatedQuery(String email) {
		return String.format(QueryReader.getString("UserQueries.query.isActivated"), email);
	}

}
