package servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Servlet implementation class RaspberryPi
 */
@WebServlet("/Temperature.json")
public class RaspberryPi extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RaspberryPi() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		ServletOutputStream output=response.getOutputStream();
		output.println("Since we implemented authentication, this server will no "
				+ "longer support GET methods, because passwords can be cached and bookmarked with GET"
				+ " (You can literally see the password in the request url string!!!)"
				+ ", which is why we use post instead.");
		output.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.reset();
		response.resetBuffer();
		response.setContentType("application/json");

		ServletOutputStream output=response.getOutputStream();
		String username=request.getParameter("username");
		String password=request.getParameter("password");	


		Connection c = null;
		Statement stmt = null;
		ResultSet rs = null;

		if(UserService.authenticate(username, password)==UserService.AUTH_SUCCESS){

			try {

				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:"+UserService.URL);
				c.setAutoCommit(false);

				stmt = c.createStatement();
				rs = stmt.executeQuery( "SELECT * FROM TEMPERATURE WHERE USERNAME='"+username+ "';" );

				JSONArray array=new JSONArray();

				while ( rs.next() ) {
					int id = rs.getInt("id");
					String  date = rs.getString("DATE");
					String tempF=rs.getString("TEMPERATURE_FARENHEIT");

					JSONObject obj=new JSONObject();				
					obj.put("id", id);
					obj.put("date",date);
					obj.put("temp",tempF);
					array.add(obj);
				}

				JSONObject finalObj=new JSONObject();
				finalObj.put("temperature",array);
				String jsonText = JSONValue.toJSONString(finalObj);

				output.println(jsonText);
			} catch (Exception e) { e.printStackTrace(); }

			finally {
				try { if (rs != null) rs.close();	} catch (SQLException e) { e.printStackTrace(); }

				try { if (stmt != null) stmt.close();	} catch (SQLException e) { e.printStackTrace(); }

				try { if (c != null) c.close();	} catch (SQLException e) { e.printStackTrace(); }
			}
		}
		
		try { if (output != null) output.close();	} catch (Exception e) { e.printStackTrace(); }

	}
}
