package com.rootmind.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Vector;

import javax.naming.NamingException;

import com.rootmind.wrapper.AbstractWrapper;
import com.rootmind.wrapper.DataArrayWrapper;
import com.rootmind.wrapper.JobWrapper;
import com.rootmind.wrapper.RiderWrapper;
import com.rootmind.wrapper.ServiceWrapper;
import com.rootmind.wrapper.JobWrapper;
import com.rootmind.wrapper.UsersWrapper;

public class JobHelper extends Helper {

	public AbstractWrapper insertJob(UsersWrapper usersProfileWrapper, JobWrapper jobWrapper) throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql = null;
		PreparedStatement pstmt = null;

		try {
			con = getConnection();

			sql = " INSERT INTO JobsRate(RiderRefNo, RiderID, JobID, JobName, "
					+ " ServiceCode, Rate, Status, MakerID, MakerDateTime) Values (?,?,?,?,?,?,?,?,?)";

			System.out.println("sql " + sql  );

			pstmt = con.prepareStatement(sql);
			if(Utility.isEmpty(jobWrapper.jobID))
			{
				jobWrapper.jobID = generateJobID(); //while populating from list do not create new jobid
			}
			jobWrapper.status = Constants.ACTIVE;
			pstmt.setString(1, Utility.trim(jobWrapper.riderRefNo));
			pstmt.setString(2, Utility.trim(jobWrapper.riderID));
			pstmt.setString(3, Utility.trim(jobWrapper.jobID));
			pstmt.setString(4, Utility.trim(jobWrapper.jobName));
			pstmt.setString(5, Utility.trim(jobWrapper.serviceCode));
			pstmt.setDouble(6, jobWrapper.rate);
			pstmt.setString(7, Utility.trim(jobWrapper.status));
			pstmt.setString(8, Utility.trim(jobWrapper.riderID));
			pstmt.setTimestamp(9, Utility.getCurrentTime());

			pstmt.executeUpdate();
			pstmt.close();

			jobWrapper.recordFound = true;

			dataArrayWrapper.jobWrapper = new JobWrapper[1];
			dataArrayWrapper.jobWrapper[0] = jobWrapper;

			dataArrayWrapper.recordFound = true;

			System.out.println("Successfully inserted into insertJob");

		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException(se.getSQLState() + " ; " + se.getMessage());
		} catch (NamingException ne) {
			ne.printStackTrace();
			throw new NamingException(ne.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			try {
				releaseConnection(resultSet, con);
			} catch (SQLException se) {
				se.printStackTrace();
				throw new Exception(se.getSQLState() + " ; " + se.getMessage());
			}
		}

		return dataArrayWrapper;
	}

	public AbstractWrapper fetchJob(UsersWrapper usersProfileWrapper, RiderWrapper riderWrapperParam) throws Exception {

		PreparedStatement pstmt = null;
		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();

		Vector<Object> vector = new Vector<Object>();
		String sql = null;

		JobWrapper jobWrapper = null;
		JobWrapper[] jobListWrapperArray = null;
		JobWrapper[] jobWrapperArray = null;
		try {

			con = getConnection();

			// ----------------------
			sql = "SELECT JobID, JobName, ServiceCode, Status FROM Jobs";
			pstmt = con.prepareStatement(sql);
			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				jobWrapper = new JobWrapper();

				jobWrapper.jobID = Utility.trim(resultSet.getString("JobID"));
				jobWrapper.jobName = Utility.trim(resultSet.getString("JobName"));
				jobWrapper.serviceCode = Utility.trim(resultSet.getString("ServiceCode"));
				jobWrapper.status = Utility.trim(resultSet.getString("Status"));

				jobWrapper.recordFound = true;

				vector.add(jobWrapper);

				System.out.println("JobList  successful");

			}

			if (vector.size() > 0) {

				jobListWrapperArray = new JobWrapper[vector.size()];
				vector.copyInto(jobListWrapperArray);
				System.out.println("total trn. in fetch " + vector.size());

			}

			vector.clear();
			if (resultSet != null)
				resultSet.close();
			pstmt.close();
			// ---------------------

			sql = "SELECT RiderRefNo, RiderID, JobID, JobName, ServiceCode, Rate, Status, MakerDateTime FROM JobsRate "
					+ " WHERE RiderRefNo=? and RiderID=? and Status=? ORDER BY ServiceCode, MakerDateTime DESC ";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(riderWrapperParam.riderRefNo));
			pstmt.setString(2, Utility.trim(riderWrapperParam.riderID));
			pstmt.setString(3,Constants.ACTIVE);
			
			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				jobWrapper = new JobWrapper();

				jobWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				jobWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
				jobWrapper.jobID = Utility.trim(resultSet.getString("JobID"));
				jobWrapper.jobName = Utility.trim(resultSet.getString("JobName"));
				jobWrapper.serviceCode = Utility.trim(resultSet.getString("ServiceCode"));
				jobWrapper.rate = resultSet.getDouble("Rate");
				jobWrapper.status = Utility.trim(resultSet.getString("Status"));
				jobWrapper.makerDateTime = Utility.setDateAMPM(resultSet.getString("MakerDateTime"));


				jobWrapper.recordFound = true;

				System.out.println("fetchJob  successful");

				vector.addElement(jobWrapper);

			}
			if (vector.size() > 0) {
				jobWrapperArray = new JobWrapper[vector.size()];
				vector.copyInto(jobWrapperArray);
			}
			if (resultSet != null)
				resultSet.close();
			pstmt.close();

			boolean contains = false;
			if(jobListWrapperArray!=null)
			{
				for (int i = 0; i < jobListWrapperArray.length; i++) {
	
					contains = false;
					for (int j = 0; j < (jobWrapperArray != null ? jobWrapperArray.length : 0); j++) {
						if (jobWrapperArray[j].jobID.equals(jobListWrapperArray[i].jobID)) {
	
							contains = true;
						}
	
					}
					if (contains == false) {
	
						jobWrapper = new JobWrapper();
	
						jobWrapper.riderRefNo = riderWrapperParam.riderRefNo;
						jobWrapper.riderID = riderWrapperParam.riderID;
						jobWrapper.jobID = jobListWrapperArray[i].jobID;
						jobWrapper.jobName = jobListWrapperArray[i].jobName;
						jobWrapper.serviceCode = jobListWrapperArray[i].serviceCode;
						jobWrapper.rate = 0;
						jobWrapper.status = Constants.ACTIVE;
						jobWrapper.recordFound = true;
	
						vector.add(jobWrapper);
						
						insertJob(usersProfileWrapper, jobWrapper);
	
					}
	
				}
			}

			if (vector.size() > 0) {
				dataArrayWrapper.jobWrapper = new JobWrapper[vector.size()];
				vector.copyInto(dataArrayWrapper.jobWrapper);
				dataArrayWrapper.recordFound = true;

				System.out.println("total trn. in fetch " + vector.size());

			} else {
				dataArrayWrapper.jobWrapper = new JobWrapper[1];
				dataArrayWrapper.jobWrapper[0] = new JobWrapper();
				vector.copyInto(dataArrayWrapper.jobWrapper);
				dataArrayWrapper.recordFound = true;

			}


		} catch (SQLException se) {

			se.printStackTrace();
			throw new SQLException(se.getSQLState() + " ; " + se.getMessage());

		} catch (NamingException ne) {

			ne.printStackTrace();
			throw new NamingException(ne.getMessage());
		} catch (Exception ex) {

			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}

		finally {
			try {
				releaseConnection(resultSet, con);
			} catch (SQLException se) {
				se.printStackTrace();
				throw new Exception(se.getSQLState() + " ; " + se.getMessage());
			}
		}

		return dataArrayWrapper;
	}

	public AbstractWrapper updateJob(UsersWrapper usersProfileWrapper, JobWrapper jobWrapper)
			throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql = null;

		PreparedStatement pstmt = null;

		try {

				con = getConnection();

				sql = " UPDATE JobsRate SET JobName=?, Rate=?, Status=?, ModifierID=?, ModifierDateTime=? "
						+ " WHERE RiderRefNo=? AND RiderID=? AND JobID=? AND ServiceCode=?";

				System.out.println("sql " + sql);

				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, Utility.trim(jobWrapper.jobName));
				pstmt.setDouble(2, jobWrapper.rate);
				pstmt.setString(3, Utility.trim(jobWrapper.status));
				pstmt.setString(4, Utility.trim(jobWrapper.riderID));
				pstmt.setTimestamp(5, Utility.getCurrentTime()); // date time
				pstmt.setString(6, Utility.trim(jobWrapper.riderRefNo));
				pstmt.setString(7, Utility.trim(jobWrapper.riderID));
				pstmt.setString(8, Utility.trim(jobWrapper.jobID));
				pstmt.setString(9, Utility.trim(jobWrapper.serviceCode));

				pstmt.executeUpdate();
				pstmt.close();

				jobWrapper.recordFound = true;

				dataArrayWrapper.jobWrapper = new JobWrapper[1];
				dataArrayWrapper.jobWrapper[0] = jobWrapper;

				dataArrayWrapper.recordFound = true;


				System.out.println("Successfully updated into updateJob");
						
				

		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException(se.getSQLState() + " ; " + se.getMessage());
		} catch (NamingException ne) {
			ne.printStackTrace();
			throw new NamingException(ne.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			try {
				releaseConnection(resultSet, con);
			} catch (SQLException se) {
				se.printStackTrace();
				throw new Exception(se.getSQLState() + " ; " + se.getMessage());
			}
		}

		return dataArrayWrapper;
	}
	
	public String generateJobID()throws Exception {
		
		Connection con = null;
		ResultSet resultSet = null;

		//DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql=null;
//		
//		SimpleDateFormat dmyFormat = new SimpleDateFormat("ddMMMyyyy");
//	
//		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
//		symbols.setGroupingSeparator(',');
//		formatter.applyPattern("###,###,###,##0.00");
//		formatter.setDecimalFormatSymbols(symbols);
////		
		int jobID=0;
		String finalJobID=null;
		String jobCode=null;
		
		try {
			
			
			con = getConnection();
			
			
			sql="SELECT JobID, JobCode from Parameter";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
		
			resultSet = pstmt.executeQuery();
			
			if (resultSet.next()) 
			{
				
				jobID=resultSet.getInt("JobID");
				System.out.println("jobID " + jobID);
				jobCode=resultSet.getString("JobCode");
				
			}
			
			resultSet.close();
			pstmt.close();
			
			if(jobID==0)
			{
				jobID=1;
				
			}
			else
			{
				
				jobID=jobID+1;
			}
				
			sql="UPDATE Parameter set JobID=?";
			
			
			System.out.println("sql " + sql);
			
			pstmt = con.prepareStatement(sql);
	
			pstmt.setInt(1,jobID);
			
			pstmt.executeUpdate();
			pstmt.close();
			
			int paddingSize=6;

			finalJobID=jobCode+String.format("%0" + paddingSize +"d",jobID);
			
			System.out.println("Successfully generated finalJobID " + finalJobID);
			
		} catch (SQLException se) {
			se.printStackTrace();
			throw new SQLException(se.getSQLState()+ " ; "+ se.getMessage());
		} catch (NamingException ne) {
			ne.printStackTrace();
			throw new NamingException(ne.getMessage());
		}
		 catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}
		finally
		{
			try
			{
				releaseConnection(resultSet, con);
			} 
			catch (SQLException se)
			{
				se.printStackTrace();
				throw new Exception(se.getSQLState()+ " ; "+ se.getMessage());
			}
		}

		return finalJobID;
	}
	
	//this function is for Rider while calling the Service Provider
	public AbstractWrapper fetchJobRider(UsersWrapper usersProfileWrapper, RiderWrapper riderWrapperParam) throws Exception {

		PreparedStatement pstmt = null;
		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();

		Vector<Object> vector = new Vector<Object>();
		String sql = null;

		JobWrapper jobWrapper = null;
		try {

			con = getConnection();

			

			sql = "SELECT RiderRefNo, RiderID, JobID, JobName, ServiceCode, Rate, Status, MakerDateTime FROM JobsRate "
					+ " WHERE RiderRefNo=? and RiderID=? and Status=? and ServiceCode=? ";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(riderWrapperParam.riderRefNo));
			pstmt.setString(2, Utility.trim(riderWrapperParam.riderID));
			pstmt.setString(3,Constants.ACTIVE);
			pstmt.setString(2, Utility.trim(riderWrapperParam.serviceCode));
			
			
			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				jobWrapper = new JobWrapper();

				jobWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				jobWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
				jobWrapper.jobID = Utility.trim(resultSet.getString("JobID"));
				jobWrapper.jobName = Utility.trim(resultSet.getString("JobName"));
				jobWrapper.serviceCode = Utility.trim(resultSet.getString("ServiceCode"));
				jobWrapper.rate = resultSet.getDouble("Rate");
				jobWrapper.status = Utility.trim(resultSet.getString("Status"));
				jobWrapper.makerDateTime = Utility.setDateAMPM(resultSet.getString("MakerDateTime"));


				jobWrapper.recordFound = true;

				System.out.println("fetchJob  successful");

				vector.addElement(jobWrapper);

			}
			if (resultSet != null)
				resultSet.close();
			pstmt.close();

			if (vector.size() > 0) {
				dataArrayWrapper.jobWrapper = new JobWrapper[vector.size()];
				vector.copyInto(dataArrayWrapper.jobWrapper);
				dataArrayWrapper.recordFound = true;

				System.out.println("total trn. in fetch " + vector.size());

			} else {
				dataArrayWrapper.jobWrapper = new JobWrapper[1];
				dataArrayWrapper.jobWrapper[0] = new JobWrapper();
				vector.copyInto(dataArrayWrapper.jobWrapper);
				dataArrayWrapper.recordFound = true;

			}


		} catch (SQLException se) {

			se.printStackTrace();
			throw new SQLException(se.getSQLState() + " ; " + se.getMessage());

		} catch (NamingException ne) {

			ne.printStackTrace();
			throw new NamingException(ne.getMessage());
		} catch (Exception ex) {

			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		}

		finally {
			try {
				releaseConnection(resultSet, con);
			} catch (SQLException se) {
				se.printStackTrace();
				throw new Exception(se.getSQLState() + " ; " + se.getMessage());
			}
		}

		return dataArrayWrapper;
	}
	
}
