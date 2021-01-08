package authServer;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
    private void classLogger(String msg) {
    	System.out.println("[AuthDatabase]: " + msg);
    }
	
	/*
	public static void main(String[] args) {
		
		String url = "jdbc:mysql://localhost:3306/authdb";
		String db_user = "authsrv";
		String db_passwd = "azerty";
		
		

		
		try {
			Statement stmt = db_connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM auth_user;");
			while (rs.next()) {
				  String lastName = rs.getString("name");
				  System.out.println(lastName + "\n");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	*/
}
