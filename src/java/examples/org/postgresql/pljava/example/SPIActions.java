/*
 * Copyright (c) 2003, 2004 TADA AB - Taby Sweden
 * Distributed under the terms shown in the file COPYRIGHT.
 */
package org.postgresql.pljava.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Some methods used for testing the SPI JDBC driver.
 *
 * @author Thomas Hallgren
 */
public class SPIActions
{
	public static void log(String str)
	{
		Logger.getAnonymousLogger().info(str);
	}

	public static int transferPeopleWithSalary(int salary)
	throws SQLException
	{
		Connection conn = DriverManager.getConnection("jdbc:default:connection");
		PreparedStatement select = null;
		PreparedStatement insert = null;
		PreparedStatement delete = null;
		ResultSet rs = null;

		String stmt;
		try
		{
			stmt = "SELECT id, name, salary FROM employees1 WHERE salary > ?";
			log(stmt);
			select = conn.prepareStatement(stmt);

			stmt = "INSERT INTO employees2(id, name, salary) VALUES (?, ?, ?)";
			log(stmt);
			insert = conn.prepareStatement(stmt);

			stmt = "DELETE FROM employees1 WHERE id = ?";
			log(stmt);
			delete = conn.prepareStatement(stmt);

			log("assigning parameter value " + salary);
			select.setInt(1, salary);
			log("Executing query");
			rs = select.executeQuery();
			int rowNo = 0;
			log("Doing next");
			while(rs.next())
			{
				log("Processing row " + ++rowNo);
				int id = rs.getInt(1);
				String name = rs.getString(2);
				int empSal = rs.getInt(3);
				
				insert.setInt(1, id);
				insert.setString(2, name);
				insert.setInt(3, empSal);
				int nRows = insert.executeUpdate();
				log("Insert processed " + nRows + " rows");
				
				delete.setInt(1, id);
				nRows = delete.executeUpdate();
				log("Delete processed " + nRows + " rows");
				log("Doing next");
			}
			if(rowNo == 0)
				log("No row found");
			return rowNo;
		}
		finally
		{
			if(select != null)
				select.close();
			if(insert != null)
				insert.close();
			if(delete != null)
				delete.close();
			conn.close();
		}
	}

	public static int maxFromSetReturnExample(int base, int increment)
	throws SQLException
	{
		int max = Integer.MIN_VALUE;
		Connection conn = DriverManager.getConnection("jdbc:default:connection");
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = conn.prepareStatement("SELECT base FROM setReturnExample(?, ?)");
			stmt.setInt(1, base);
			stmt.setInt(2, increment);
			rs = stmt.executeQuery();
			while(rs.next())
			{
				base = rs.getInt(1);
				if(base > max)
					max = base;
			}
			return base;
		}
		finally
		{
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			conn.close();
		}
	}
}