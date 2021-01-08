package authServer;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class AuthDatabase {
	
	private String url = "jdbc:mysql://localhost:3306/authdb";
	private String dbUser = "authsrv";
	private String dbPassword = "azerty";
	Connection dbConnection;
	
	public AuthDatabase() {
		dbConnection = null;
		try {
			dbConnection = DriverManager.getConnection(url, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public int insertUserRow(String name, String lastName, String salt1) {
		final String SQL_INSERT = "INSERT INTO auth_user VALUES (?,?,?,?,?,?,?)";
	
		try  {
			dbConnection = DriverManager.getConnection(url, dbUser, dbPassword);
			
			classLogger("Adding user in DB with parameters : name=" + name + "; lastName=" + lastName + "; salt1=" + salt1 );
			
			PreparedStatement preparedStatement = dbConnection.prepareStatement(SQL_INSERT);
	        preparedStatement.setString(1, String.valueOf((name+lastName+salt1).hashCode())); // To change
	        preparedStatement.setString(2, name);
	        preparedStatement.setString(3, lastName);
	        preparedStatement.setString(4, "123"); // Not implemented
	        preparedStatement.setString(5, "123"); // Not implemented
	        preparedStatement.setString(6, "123"); // Not implemented
	        preparedStatement.setString(7, salt1);
	        
	        int row = preparedStatement.executeUpdate();
	        
	        if (row > 0) {
    			classLogger("User registration on DB succeeded");
	        	return 0;
	        } else {
	        	classLogger("User registration on DB failed");
	        	return -1;
	        }
		} catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

		return 0;
	}
	
	public int insertSessionRow(String salt2, String userId) {
		final String SQL_INSERT = "INSERT INTO auth_session VALUES (?,?)";
	
		try  {
			dbConnection = DriverManager.getConnection(url, dbUser, dbPassword);
			
			classLogger("Adding session token in DB");
			
			PreparedStatement preparedStatement = dbConnection.prepareStatement(SQL_INSERT);
	        preparedStatement.setString(1, salt2);
	        preparedStatement.setString(2, userId);

			int row = preparedStatement.executeUpdate();
	        
	        if (row > 0) {
    			classLogger("Link between user and session on DB succeeded");
	        	return 0;
	        } else {
	        	classLogger("Link between user and session on DB failed");
	        	return -1;
	        }
		} catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

		return 0;
	}

	/***
	 * Find out if user exists using his name and last name
	 * TODO: add biometric informations to get unique result
	 */
	public String userExistsNameLastName(String userName, String userLastName) {
		final String SQL_SELECT = "SELECT * FROM auth_user WHERE name=? AND last_name=?";
		String userId = "";

		try  {
			dbConnection = DriverManager.getConnection(url, dbUser, dbPassword);
			
			classLogger("Looking for user in DB with name=" + userName + "; lastName=" + userLastName );
			
			PreparedStatement preparedStatement = dbConnection.prepareStatement(SQL_SELECT);
	        preparedStatement.setString(1, userName);
	        preparedStatement.setString(2, userLastName);
	        
	        ResultSet resultSet = preparedStatement.executeQuery();


	        if (resultSet.next() != false) {
    			classLogger("User exists in DB");
				do {
					userId = resultSet.getString("user_id");
				} while (resultSet.next());
				return userId;
	        } else {
	        	classLogger("User does not exist in DB");
	        	return "";
	        }
		} catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

		return "";
	}

	public String getTokenUserId(String userId) {
		final String SQL_SELECT = "SELECT * FROM auth_session WHERE user_id=?";
		String sessionToken = "";

		try  {
			dbConnection = DriverManager.getConnection(url, dbUser, dbPassword);
			
			classLogger("Looking for token (SALT2) for user " + userId);
			
			PreparedStatement preparedStatement = dbConnection.prepareStatement(SQL_SELECT);
	        preparedStatement.setString(1, userId);
	        
	        ResultSet resultSet = preparedStatement.executeQuery();


	        if (resultSet.next() != false) {
    			classLogger("Session token found in DB");
				do {
					sessionToken = resultSet.getString("token");
				} while (resultSet.next());
				return sessionToken;
	        } else {
	        	classLogger("Session token not found in DB");
	        	return "";
	        }
		} catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

		return "";
	}

	public String getHashPasswordUserId(String userId) {
		final String SQL_SELECT = "SELECT * FROM auth_user WHERE user_id=?";
		String hashPassword = "";

		try  {
			dbConnection = DriverManager.getConnection(url, dbUser, dbPassword);
			
			classLogger("Looking for H(SALT1 + password) for " + userId);
			
			PreparedStatement preparedStatement = dbConnection.prepareStatement(SQL_SELECT);
	        preparedStatement.setString(1, userId);
	        
	        ResultSet resultSet = preparedStatement.executeQuery();


	        if (resultSet.next() != false) {
    			classLogger("H(SALT1 + password) found in DB");
				do {
					hashPassword = resultSet.getString("password");
				} while (resultSet.next());
				return hashPassword;
	        } else {
	        	classLogger("H(SALT1 + password) not found in DB");
	        	return "";
	        }
		} catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

		return "";
	}


    private void classLogger(String msg) {
    	System.out.println("[AuthDatabase]: " + msg);
    }
	
}
