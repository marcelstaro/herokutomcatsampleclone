package servlet;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NewRecord
 */
@WebServlet("/NewRecord")
public class NewRecord extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public NewRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String str_date=request.getParameter("date");
		String temp=request.getParameter("temp");	

		String username=request.getParameter("username");
		String password=request.getParameter("password");	
		ServletOutputStream output=response.getOutputStream();

		Connection c = null;
		PreparedStatement stmt = null;


		if( (str_date!=null) && (temp!=null))
		{
			if( (str_date.trim().length()>0) && (temp.trim().length()>0)
					&& (UserService.authenticate(username, password)==UserService.AUTH_SUCCESS) )
			{

				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection("jdbc:sqlite:"+UserService.URL);
					c.setAutoCommit(false);

					String sql = "INSERT INTO TEMPERATURE(DATE, TEMPERATURE_FARENHEIT, USERNAME) " +
							"VALUES(?,?,?)";
					stmt = c.prepareStatement(sql);			
					stmt.setString(1, str_date);
					stmt.setString(2, temp);
					stmt.setString(3, username);
					stmt.executeUpdate();		
					c.commit();
					output.println("success");					
				} catch (Exception e) { e.printStackTrace(); }
					
				finally {
					try { if (output != null) output.close();	} catch (Exception e) { e.printStackTrace(); }

					try { if (stmt != null) stmt.close();	} catch (SQLException e) { e.printStackTrace(); }

					try { if (c != null) c.close();	} catch (SQLException e) { e.printStackTrace(); }
				}
				
			}
			else 
				output.println("failed: date is empty");
		}
		else
			output.println("failed: date is null");

		try { if (output != null) output.close();	} catch (Exception e) { e.printStackTrace(); }

	}

}
