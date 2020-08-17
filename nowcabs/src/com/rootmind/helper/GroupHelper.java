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
import com.rootmind.wrapper.GroupRiderWrapper;
import com.rootmind.wrapper.GroupWrapper;
import com.rootmind.wrapper.RiderWrapper;
import com.rootmind.wrapper.UsersWrapper;

public class GroupHelper extends Helper{

	
	//---------insert Rating
	public AbstractWrapper insertGroup(UsersWrapper usersProfileWrapper, GroupWrapper groupWrapper)
			throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql = null;

		PreparedStatement pstmt = null;

		try {


				con = getConnection();

			
//					GroupWrapper verifyGroupWrapper = (GroupWrapper)verifyGroup(groupWrapper);
//
//
//					if (verifyGroupWrapper.recordFound == false) 
//					{


						sql = " INSERT INTO Groups(GroupRefNo, GroupID, RiderRefNo, RiderID, Name, Status, MakerID, MakerDateTime) "
								+ " Values (?,?,?,?,?,?,?,?)";

						System.out.println("sql " + sql);

						pstmt = con.prepareStatement(sql);
						
						groupWrapper.groupRefNo=generateGroupsRefNo();
						groupWrapper.status= Constants.ACTIVE;
						pstmt.setString(1, Utility.trim(groupWrapper.groupRefNo));
						pstmt.setString(2, Utility.trim(groupWrapper.groupID));
						pstmt.setString(3, Utility.trim(groupWrapper.riderRefNo));
						pstmt.setString(4, Utility.trim(groupWrapper.riderID));
						pstmt.setString(5, Utility.trim(groupWrapper.name));
						pstmt.setString(6, Utility.trim(groupWrapper.status));
						pstmt.setString(7, Utility.trim(groupWrapper.riderID));
						pstmt.setTimestamp(8, Utility.getCurrentTime()); // date time

						pstmt.executeUpdate();
						pstmt.close();
						
						

						groupWrapper.recordFound = true;

						dataArrayWrapper.groupWrapper = new GroupWrapper[1];
						dataArrayWrapper.groupWrapper[0] = groupWrapper;

						dataArrayWrapper.recordFound = true;

						System.out.println("Successfully inserted into insert Group");

					//}
//					else
//					{
//						sql = " UPDATE Groups SET GroupID=?, Name=?, Status=?, ModifierID=?, ModifierDateTime=? "
//								+ " WHERE GroupRefNo=? ";
//
//						System.out.println("sql " + sql);
//
//						pstmt = con.prepareStatement(sql);
//						pstmt.setString(1, Utility.trim(groupWrapper.groupID));
//						pstmt.setString(2, Utility.trim(groupWrapper.name));
//						pstmt.setString(3, Utility.trim(groupWrapper.status));
//						pstmt.setString(4, Utility.trim(groupWrapper.riderID));
//						pstmt.setTimestamp(5, Utility.getCurrentTime()); // date time
//						pstmt.setString(6, Utility.trim(groupWrapper.groupRefNo));
//
//						pstmt.executeUpdate();
//						pstmt.close();
//
//						
//						groupWrapper.recordFound = true;
//
//						dataArrayWrapper.groupWrapper = new GroupWrapper[1];
//						dataArrayWrapper.groupWrapper[0] = groupWrapper;
//
//						dataArrayWrapper.recordFound = true;
//
//						System.out.println("Successfully updated into update Groups");
//						
//					}
//				

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
	
	public AbstractWrapper updateGroup(UsersWrapper usersProfileWrapper, GroupWrapper groupWrapper)
			throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql = null;

		PreparedStatement pstmt = null;

		try {


				con = getConnection();

				GroupRiderHelper groupRiderHelper = new GroupRiderHelper();
				GroupRiderWrapper groupRiderWrapper = new GroupRiderWrapper();
				groupRiderWrapper.groupRefNo = groupWrapper.groupRefNo;
				
				DataArrayWrapper groupDataArrayWrapper = (DataArrayWrapper)groupRiderHelper.fetchGroupRider(groupRiderWrapper);
				
				if(groupDataArrayWrapper.recordFound==true && groupDataArrayWrapper.groupRiderWrapper!=null && groupDataArrayWrapper.groupRiderWrapper.length>0)
				{
					
					//if no record found then update status
					if(groupDataArrayWrapper.groupRiderWrapper[0].recordFound==false)
					{
						sql = " UPDATE Groups SET Status=?, ModifierID=?, ModifierDateTime=? "
									+ " WHERE GroupRefNo=? "; //GroupID=?, Name=?, 
		
						System.out.println("sql " + sql);
		
						pstmt = con.prepareStatement(sql);
						//pstmt.setString(1, Utility.trim(groupWrapper.groupID));
						//pstmt.setString(2, Utility.trim(groupWrapper.name));
						pstmt.setString(1, Utility.trim(groupWrapper.status));
						pstmt.setString(2, Utility.trim(groupWrapper.riderID));
						pstmt.setTimestamp(3, Utility.getCurrentTime()); // date time
						pstmt.setString(4, Utility.trim(groupWrapper.groupRefNo));
		
						pstmt.executeUpdate();
						pstmt.close();
		
						
						groupWrapper.recordFound = true;
		
						dataArrayWrapper.groupWrapper = new GroupWrapper[1];
						dataArrayWrapper.groupWrapper[0] = groupWrapper;
		
						dataArrayWrapper.recordFound = true;

						System.out.println("Successfully updated into update Groups");
					}
					else
					{
						groupWrapper.recordFound = false;
						dataArrayWrapper.groupWrapper = new GroupWrapper[1];
						dataArrayWrapper.groupWrapper[0] = groupWrapper;
						dataArrayWrapper.recordFound = true;

					}
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
	
	public AbstractWrapper fetchGroup(RiderWrapper riderWrapper) throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper=new DataArrayWrapper();

		Vector<Object> vector = new Vector<Object>();
		String sql = null;
		PreparedStatement pstmt=null;
		
		GroupWrapper groupWrapper=null;

		try {
			con = getConnection();
			

			
			sql = "SELECT GroupRefNo, GroupID, RiderRefNo, RiderID, Name, Status, MakerDateTime  "
					+ " FROM Groups WHERE RiderRefNo=? AND RiderID=? AND Status=? ORDER BY MakerDateTime DESC";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(riderWrapper.riderRefNo));
			pstmt.setString(2, Utility.trim(riderWrapper.riderID));
			pstmt.setString(3, Utility.trim(Constants.ACTIVE));

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				groupWrapper = new GroupWrapper();

				groupWrapper.groupRefNo = Utility.trim(resultSet.getString("GroupRefNo"));
				groupWrapper.groupID = Utility.trim(resultSet.getString("GroupID"));
				groupWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				groupWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
				groupWrapper.name = Utility.trim(resultSet.getString("Name"));
				groupWrapper.status = Utility.trim(resultSet.getString("Status"));
				groupWrapper.makerDateTime = Utility.setDateAMPM(resultSet.getString("MakerDateTime"));
				
				groupWrapper.recordFound = true;

				vector.add(groupWrapper);

				System.out.println("fetchGroup  successful");

			}

			
			if (resultSet != null)
				resultSet.close();
			pstmt.close();
			

			
			if (vector.size() > 0) {
				
				dataArrayWrapper.groupWrapper = new GroupWrapper[vector.size()];
				vector.copyInto(dataArrayWrapper.groupWrapper);
				dataArrayWrapper.recordFound = true;

				System.out.println("total trn. in fetch " + vector.size());

			}
			else
			{
				
				dataArrayWrapper.groupWrapper = new GroupWrapper[1];
				groupWrapper = new GroupWrapper();
				groupWrapper.recordFound=false;
				dataArrayWrapper.groupWrapper[0]=groupWrapper;
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
	
	public AbstractWrapper verifyGroup(GroupWrapper groupWrapper) throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		String sql = null;
		PreparedStatement pstmt=null;
		

		try {
			con = getConnection();
			
			
			

			sql = "SELECT GroupRefNo, GroupID, RiderRefNo, RiderID, Name, Status "
					+ " FROM Groups WHERE GroupRefNo=?";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(groupWrapper.groupRefNo));

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				groupWrapper = new GroupWrapper();
				groupWrapper.groupRefNo = Utility.trim(resultSet.getString("GroupRefNo"));
				groupWrapper.groupID = Utility.trim(resultSet.getString("GroupID"));
				groupWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				groupWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
				groupWrapper.name = Utility.trim(resultSet.getString("Name"));
				groupWrapper.status = Utility.trim(resultSet.getString("Status"));
				

				groupWrapper.recordFound = true;


				System.out.println("verifyGroups  successful");

			}

			if (resultSet != null)
				resultSet.close();
			pstmt.close();
			


			

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

		return groupWrapper;
	}


	
	
	

	
			
	public String generateGroupsRefNo()throws Exception {
		
		Connection con = null;
		ResultSet resultSet = null;

		//DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql=null;
		
		SimpleDateFormat dmyFormat = new SimpleDateFormat("ddMMMyyyy");
	
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		formatter.applyPattern("###,###,###,##0.00");
		formatter.setDecimalFormatSymbols(symbols);
//		
		int groupsRefNo=0;
		String finalGroupsRefNo=null;
		String groupsCode=null;
		
		try {
			
			
			con = getConnection();
			
			
			sql="SELECT GroupsRefNo, GroupsCode from Parameter";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
		
			resultSet = pstmt.executeQuery();
			
			if (resultSet.next()) 
			{
				
				groupsRefNo=resultSet.getInt("GroupsRefNo");
				System.out.println("GroupsRefNo " + groupsRefNo);
				groupsCode=resultSet.getString("GroupsCode");
				
			}
			
			resultSet.close();
			pstmt.close();
			
			if(groupsRefNo==0)
			{
				groupsRefNo=1;
				
			}
			else
			{
				
				groupsRefNo=groupsRefNo+1;
			}
				
			sql="UPDATE Parameter set GroupsRefNo=?";
			
			
			System.out.println("sql " + sql);
			
			pstmt = con.prepareStatement(sql);
	
			pstmt.setInt(1,groupsRefNo);
			
			pstmt.executeUpdate();
			pstmt.close();
			
			int paddingSize=4;

			finalGroupsRefNo=groupsCode+dmyFormat.format(new java.util.Date()).toUpperCase()+String.format("%0" + paddingSize +"d",groupsRefNo);
			
			System.out.println("Successfully generated GroupsRefNo " + finalGroupsRefNo);
			
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

		return finalGroupsRefNo;
	}
	
	//-----------------End generateRideRefNo---------------------------
}
