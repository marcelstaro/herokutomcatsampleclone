package servlet;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class UserService {


	private static File databaseFile = new File( 
			new File( System.getProperty( "catalina.base" ) ).getAbsoluteFile(),
			"webapps/RaspberryPi/WEB-INF/TemperatureReadings.db"
			);

	public static String URL=databaseFile.getAbsolutePath();	

	public static int AUTH_SUCCESS=0;
	public static int AUTH_INCORRECT_PWD=1;
	public static int AUTH_DOESNT_EXIST=2;
	public static int AUTH_FAILED=-1;
	public static int CREATE_SUCCESS=0;
	public static int CREATE_EXISTS=1;
	public static int CREATE_FAILED=-1;

	public static int create(String username,String password){
		int RESULT=CREATE_FAILED;
		Connection c = null;
		PreparedStatement stmt = null;

		if( (username!=null) && (password!=null))
		{
			if( (username.trim().length()>0) && (password.trim().length()>0) 
					&& (authenticate(username,password)==AUTH_DOESNT_EXIST) )
			{
				try {
					Class.forName("org.sqlite.JDBC");					
					c = DriverManager.getConnection("jdbc:sqlite:"+UserService.URL);						
					c.setAutoCommit(false);

					String sql = "INSERT INTO USER(USERNAME, PASSWORD) " +
							"VALUES(?,?)";

					stmt = c.prepareStatement(sql);
					stmt.setString(1, username);
					stmt.setString(2, password);

					stmt.executeUpdate();		     
					c.commit();

					RESULT= CREATE_SUCCESS;
				} catch (Exception e) { e.printStackTrace(); }

				finally {
					try { if (stmt != null) stmt.close();	} catch (SQLException e) { e.printStackTrace(); }

					try { if (c != null) c.close(); } catch (SQLException e) { e.printStackTrace(); }
					
				}

			}				
		}									

		return RESULT;
	}

	public static int authenticate(String username, String password){

		int RESULT=AUTH_FAILED;


		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;

		if( (username!=null) && (password!=null))
		{
			if( (username.trim().length()>0) && (password.trim().length()>0) ){

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:"+UserService.URL);
					c.setAutoCommit(false);

					stmt = c.createStatement();
					rs = stmt.executeQuery( "SELECT PASSWORD FROM USER WHERE USERNAME='"+username+"';" );

					if(rs.next()){
						if(rs.getString(1).equals(password)){
							RESULT= AUTH_SUCCESS;
						}
						else{
							RESULT= AUTH_INCORRECT_PWD;
						}
					}
					else{
						RESULT= AUTH_DOESNT_EXIST;
					}

				} catch (Exception e) { e.printStackTrace(); }

				finally {
					try { if (rs != null) rs.close();	} catch (SQLException e) { e.printStackTrace(); }
					
					try { if (stmt != null) stmt.close();	} catch (SQLException e) { e.printStackTrace(); }
							
					try { if (c != null) c.close();	} catch (SQLException e) { e.printStackTrace(); }
				}
			}
		}					

		return RESULT;
	}
}
