package com.ocbc.controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.sql.*;
import java.util.logging.Logger;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.*;

@CrossOrigin(allowCredentials="true")
@RestController
public class LoginController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(LoginController.class);
	
	@Value("${oracle.jndi}")
	String oracleJdbc;
	@Value("${oracle.username}")
	String oracleUserName;
	@Value("${oracle.password}")
	String oraclePassword;
	@Value("${saltDb}")
	String saltDb;
	
	public LoginController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {
			//Get the available amount from login person account
			String username = request.getParameter("username");
			String type=request.getParameter("type");
			if(("loginPersonBalanace").equalsIgnoreCase(type)) {
				try(InputStream custSaltInStream = this.getClass().getResourceAsStream(saltDb)){
					String resolvedValue = oraclePassword;
					try (Connection conn = DriverManager.getConnection(oracleJdbc, oracleUserName, resolvedValue)){
						System.out.println("Login " + username);
						System.out.println("Hello, " + username);
						getBalanaceAmount(username, conn);
					}
				}catch(IOException | SQLException e) {
					LOGGER.error(e.getMessage(),e);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Autowired
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {
			
			String type=request.getParameter("type");
			String username = request.getParameter("username");
			String balance = request.getParameter("balance");
			String payTo = request.getParameter("payTo");
			String amount = request.getParameter("amount");
			//check valid username and payto person name available in Database
			if(("transfer").equalsIgnoreCase(type)) {
				checkValidLogin(username);
				checkValidLogin(payTo);
				transferAnmount(username,balance,payTo,amount);
			}
			
		}catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	/*
	 * This methid is used to get the available amount from given username
	 */
	public int getBalanaceAmount(String username, Connection con) throws SQLException{
		
		String query = "select amount from login where username='"+username;
		PreparedStatement pstm = null;
		int amount = 0;
		ResultSet rs = null;
		try {
			pstm = con.prepareStatement(query);
			rs = pstm.executeQuery();
			if(rs.next()) {
				amount = rs.getInt("amount");
				System.out.println("your balance is : " + amount);
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
		}finally {
			pstm.close();
			rs.close();
			con.close();
		}
		return amount;
	}
	
	/*
	 * This method is used to validate given username is available in DataBase
	 */
	public void checkValidLogin(String username) {
		
		try(InputStream custSaltInStream = this.getClass().getResourceAsStream(saltDb)){
			String resolvedValue = oraclePassword;
			try (Connection conn = DriverManager.getConnection(oracleJdbc, oracleUserName, resolvedValue)){
				
				String query = "select * from login where username='"+username;
				PreparedStatement pstm = null;
				int count = 0;
				ResultSet rs = null;
				try {
					pstm = conn.prepareStatement(query);
					rs = pstm.executeQuery();
					if(rs.next()) {
						count = 1;
					}
					if( count == 0) {
						System.out.println("Invalid User Id");
					}
				}catch(Exception e) {
					LOGGER.error(e.getMessage(),e);
				}finally {
					pstm.close();
					rs.close();
					conn.close();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * This is the method is verify all the logic.
	 */
	public void transferAnmount(String username, String balance, String payTo, String amount) throws IOException, SQLException{
		
		try(InputStream custSaltInStream = this.getClass().getResourceAsStream(saltDb)){
			String resolvedValue = oraclePassword;
			try (Connection conn = DriverManager.getConnection(oracleJdbc, oracleUserName, resolvedValue)){
				
				
				if(!username.equalsIgnoreCase(payTo)) {
					// Debit login person amount
					int newBalance = Integer.parseInt(balance) - Integer.parseInt(amount);
				
					//Update new Balance amount to login person amount
					if(Integer.parseInt(amount) > Integer.parseInt(balance)) {
						int owingAmt = Integer.parseInt(amount) - Integer.parseInt(balance);
						owingAmount(username,owingAmt,conn);
					}
						updateAmount(username, payTo, newBalance, conn);
					
				}
				//Get Pay to person total amount
				int paytoPersonBalanceAmount = getBalanaceAmount(payTo, conn);
				paytoPersonBalanceAmount = paytoPersonBalanceAmount + Integer.parseInt(amount);
				//Update PayTo person Amount
				updateAmount(username, payTo, paytoPersonBalanceAmount, conn);
				
				//MainTain all Transaction Details in Transaction Table
				transactionDetails(username, payTo, amount, conn);
				
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
	}
	
	/*
	 * This method is used to update the amount to login person account and payto Person amount based on given username
	 */
	public void updateAmount(String username, String payTo, int amount, Connection conn) throws SQLException{
		PreparedStatement pstm = null;
		PreparedStatement pstm1 = null;
		try {
			String owingQuery = "select owingAmt from login where username='"+username;
			int owingAmt = 0;
			ResultSet rs = null;
			try {
				pstm1 = conn.prepareStatement(owingQuery);
				rs = pstm.executeQuery();
				if(rs.next()) {
					owingAmt = rs.getInt("owingAmt");
				}
			}catch(Exception e) {
				LOGGER.error(e.getMessage(),e);
			}finally {
				pstm.close();
				rs.close();
				con.close();
			}
			
			if( owingAmt > 0 ) {
				String query = "update login set amount=? where username=?";
				pstm = conn.prepareStatement(query);
				pstm.setInt(1, amount);
				pstm.setString(2, username);
				pstm.execute();
				if(username.equalsIgnoreCase(payTo)) {
					System.out.println("topup : " + amount);
				}
			}else {
				amount = amount - owingAmt;
				String query = "update login set amount=? where username=?";
				pstm = conn.prepareStatement(query);
				pstm.setInt(1, amount);
				pstm.setString(2, username);
				pstm.execute();
			}
		}catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
		}finally {
			pstm.close();
			conn.close();
		}
	}
	
	/*
	 * This method is used to update the amount to login person account and payto Person amount based on given username
	 */
	public void owingAmount(String username, int owingAmt,  Connection conn) throws SQLException{
		PreparedStatement pstm = null;
		try {
			String query = "update login set owingAmt=? where username=?";
			pstm = conn.prepareStatement(query);
			pstm.setInt(1, owingAmt);
			pstm.setString(2, username);
			pstm.execute();
				System.out.println("Owing  : " + owingAmt +" to " + username);
		}catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
		}finally {
			pstm.close();
			conn.close();
		}
	}
	
	/*
	 * This method is used to maintain the all transaction details in Transaction Table.
	 */
	public void transactionDetails(String username, String payTo, String amount, Connection conn) throws SQLException{
		PreparedStatement pstm = null;
		try {
			String query = "insert into transaction(username, payto, amount) values(?,?,?)";
			pstm = conn.prepareStatement(query);
			pstm.setString(1, username);
			pstm.setString(2, payTo);
			pstm.setInt(3, Integer.parseInt(amount));
			pstm.execute();
			System.out.println("Transferred " + amount + "to " + payTo);
		}catch(Exception e) {
			LOGGER.error(e.getMessage(),e);
		}finally {
			pstm.close();
			conn.close();
		}
	}
}
