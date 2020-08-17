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

public class GroupRiderHelper extends Helper{

	
	//---------insert Rating
	public AbstractWrapper insertGroupRider(UsersWrapper usersProfileWrapper, GroupRiderWrapper groupRiderWrapper)
			throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql = null;

		PreparedStatement pstmt = null;

		try {


				con = getConnection();

				RiderHelper riderHelper = new RiderHelper();
				RiderWrapper riderWrapper = new RiderWrapper();
				riderWrapper.mobileNo = groupRiderWrapper.mobileNo;
				DataArrayWrapper riderDataArrayWrapper = (DataArrayWrapper)riderHelper.verifyRider(usersProfileWrapper, riderWrapper);
				if(riderDataArrayWrapper.recordFound==true && riderDataArrayWrapper.riderWrapper!=null && riderDataArrayWrapper.riderWrapper.length>0)
				{
					
					if(riderDataArrayWrapper.riderWrapper[0].recordFound==true)
					{
						groupRiderWrapper.riderWrapper = riderDataArrayWrapper.riderWrapper[0];
						groupRiderWrapper.linkRiderRefNo = riderDataArrayWrapper.riderWrapper[0].riderRefNo;
						groupRiderWrapper.linkRiderID = riderDataArrayWrapper.riderWrapper[0].riderID;
						
						GroupRiderWrapper verifyGroupRiderWrapper = (GroupRiderWrapper)verifyGroupRider(groupRiderWrapper);
	
						if (verifyGroupRiderWrapper==null || verifyGroupRiderWrapper.recordFound == false) 
						{
	
	
							sql = " INSERT INTO GroupsRider(GroupRefNo, GroupID, RiderRefNo, RiderID, MobileNo, Public, Status, MakerID, MakerDateTime) "
									+ " Values (?,?,?,?,?,?,?,?,?)";
	
							System.out.println("sql " + sql);
	
							pstmt = con.prepareStatement(sql);
							
							groupRiderWrapper.status= Constants.ACTIVE;
							groupRiderWrapper.publicView=Constants.YES_CODE;
							pstmt.setString(1, Utility.trim(groupRiderWrapper.groupRefNo));
							pstmt.setString(2, Utility.trim(groupRiderWrapper.groupID));
							pstmt.setString(3, Utility.trim(riderDataArrayWrapper.riderWrapper[0].riderRefNo));
							pstmt.setString(4, Utility.trim(riderDataArrayWrapper.riderWrapper[0].riderID));
							pstmt.setString(5, Utility.trim(groupRiderWrapper.mobileNo));
							pstmt.setString(6, Utility.trim(groupRiderWrapper.publicView));
							pstmt.setString(7, Utility.trim(groupRiderWrapper.status));
							pstmt.setString(8, Utility.trim(groupRiderWrapper.riderID));
							pstmt.setTimestamp(9, Utility.getCurrentTime()); // date time
	
							pstmt.executeUpdate();
							pstmt.close();
							
							
	
							groupRiderWrapper.recordFound = true;
	
							dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[1];
							dataArrayWrapper.groupRiderWrapper[0] = groupRiderWrapper;
	
							dataArrayWrapper.recordFound = true;
	
							System.out.println("Successfully inserted into insert Group");
	
						}
						else
						{
							//only for reactivation update, otherwise do not update
							if(verifyGroupRiderWrapper.status.equals(Constants.INACTIVE))
							{
								sql = " UPDATE GroupsRider SET Public=?, Status=?, ModifierID=?, ModifierDateTime=? "
										+ " WHERE GroupRefNo=? and RiderRefNo=? and RiderID=? ";
		
								System.out.println("sql " + sql);
		
								pstmt = con.prepareStatement(sql);
								groupRiderWrapper.status= Constants.ACTIVE;
								groupRiderWrapper.publicView=Constants.YES_CODE;
								pstmt.setString(1, Utility.trim(groupRiderWrapper.publicView));
								pstmt.setString(2, Utility.trim(groupRiderWrapper.status));
								pstmt.setString(3, Utility.trim(groupRiderWrapper.riderID));
								pstmt.setTimestamp(4, Utility.getCurrentTime()); // date time
								pstmt.setString(5, Utility.trim(groupRiderWrapper.groupRefNo));
								pstmt.setString(6, Utility.trim(riderDataArrayWrapper.riderWrapper[0].riderRefNo));
								pstmt.setString(7, Utility.trim(riderDataArrayWrapper.riderWrapper[0].riderID));
		
								pstmt.executeUpdate();
								pstmt.close();
		
								
								groupRiderWrapper.recordFound = true;
		
								dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[1];
								dataArrayWrapper.groupRiderWrapper[0] = groupRiderWrapper;
		
								dataArrayWrapper.recordFound = true;
		
								System.out.println("Successfully updated into update GroupsRider");
							}
							
						} //record found check for grouprider
					} //record found check for rider
				}
				else
				{
					
					groupRiderWrapper.recordFound = false;
					dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[1];
					dataArrayWrapper.groupRiderWrapper[0] = groupRiderWrapper;
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
	
	public AbstractWrapper updateGroupRider(UsersWrapper usersProfileWrapper, GroupRiderWrapper groupRiderWrapper)
			throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql = null;

		PreparedStatement pstmt = null;

		try {


				con = getConnection();

				

				//----update maintable of Rider------
				sql = " UPDATE Rider SET Public=? WHERE RiderRefNo=? and RiderID=? ";

				System.out.println("sql " + sql);

				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, Utility.trim(groupRiderWrapper.publicView));
				pstmt.setString(2, Utility.trim(groupRiderWrapper.linkRiderRefNo));
				pstmt.setString(3, Utility.trim(groupRiderWrapper.linkRiderID));

				pstmt.executeUpdate();
				pstmt.close();
				//-------end of update Rider
				
				
				sql = " UPDATE GroupsRider SET Public=?, Status=?, ModifierID=?, ModifierDateTime=? "
						+ " WHERE GroupRefNo=? and RiderRefNo=? and RiderID=? ";

				System.out.println("sql " + sql);

				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, Utility.trim(groupRiderWrapper.publicView));
				pstmt.setString(2, Utility.trim(groupRiderWrapper.status));
				pstmt.setString(3, Utility.trim(groupRiderWrapper.riderID));
				pstmt.setTimestamp(4, Utility.getCurrentTime()); // date time
				pstmt.setString(5, Utility.trim(groupRiderWrapper.groupRefNo));
				pstmt.setString(6, Utility.trim(groupRiderWrapper.linkRiderRefNo));
				pstmt.setString(7, Utility.trim(groupRiderWrapper.linkRiderID));

				pstmt.executeUpdate();
				pstmt.close();

				
				groupRiderWrapper.recordFound = true;

				dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[1];
				dataArrayWrapper.groupRiderWrapper[0] = groupRiderWrapper;

				dataArrayWrapper.recordFound = true;

				System.out.println("Successfully updated into update GroupsRider");
				
					
				

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
	
	
	public AbstractWrapper fetchGroupRider(GroupRiderWrapper groupRiderWrapper) throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper=new DataArrayWrapper();

		Vector<Object> vector = new Vector<Object>();
		String sql = null;
		PreparedStatement pstmt=null;
		
		RiderWrapper linkRiderWrapper=null;
		RiderWrapper riderWrapper =null;
		
		//GroupRiderWrapper groupRiderWrapper=null;

		try {
			con = getConnection();
			
			RiderHelper riderHelper = new RiderHelper();
			
			sql = "SELECT GroupRefNo, GroupID, RiderRefNo, RiderID, Public, Status, MakerDateTime "
					+ " FROM GroupsRider WHERE GroupRefNo=? AND Status=? ORDER BY MakerDateTime DESC";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(groupRiderWrapper.groupRefNo));
			pstmt.setString(2, Utility.trim(Constants.ACTIVE));

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				groupRiderWrapper = new GroupRiderWrapper();

				groupRiderWrapper.groupRefNo = Utility.trim(resultSet.getString("GroupRefNo"));
				groupRiderWrapper.groupID = Utility.trim(resultSet.getString("GroupID"));
				groupRiderWrapper.linkRiderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				groupRiderWrapper.linkRiderID = Utility.trim(resultSet.getString("RiderID"));
				groupRiderWrapper.publicView = Utility.trim(resultSet.getString("Public"));
				groupRiderWrapper.status = Utility.trim(resultSet.getString("Status"));
				groupRiderWrapper.makerDateTime = Utility.setDateAMPM(resultSet.getString("MakerDateTime"));
				
				groupRiderWrapper.recordFound = true;

				linkRiderWrapper = new RiderWrapper();
				linkRiderWrapper.riderRefNo = groupRiderWrapper.linkRiderRefNo;
				linkRiderWrapper.riderID = groupRiderWrapper.linkRiderID;
				riderWrapper = (RiderWrapper)riderHelper.fetchRider(linkRiderWrapper);

				if(riderWrapper!=null  && riderWrapper.recordFound==true)
				{
					groupRiderWrapper.riderWrapper = riderWrapper;

				}

				vector.add(groupRiderWrapper);

				System.out.println("fetchGroupRider  successful");

			}

			
			if (resultSet != null)
				resultSet.close();
			pstmt.close();
			

			
			if (vector.size() > 0) {
				
				dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[vector.size()];
				vector.copyInto(dataArrayWrapper.groupRiderWrapper);
				dataArrayWrapper.recordFound = true;

				System.out.println("total trn. in fetch " + vector.size());

			}
			else
			{
				
				dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[1];
				groupRiderWrapper = new GroupRiderWrapper();
				groupRiderWrapper.recordFound=false;
				dataArrayWrapper.groupRiderWrapper[0]=groupRiderWrapper;
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
	
	public AbstractWrapper verifyGroupRider(GroupRiderWrapper groupRiderWrapperParam) throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		String sql = null;
		PreparedStatement pstmt=null;

		GroupRiderWrapper groupRiderWrapper=null;

		try {
			con = getConnection();
			
			
			

			sql = "SELECT GroupRefNo, GroupID, RiderRefNo, RiderID, Public, Status "
					+ " FROM GroupsRider WHERE GroupRefNo=? and RiderRefNo=? and RiderID=?";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(groupRiderWrapperParam.groupRefNo));
			pstmt.setString(2, Utility.trim(groupRiderWrapperParam.linkRiderRefNo));
			pstmt.setString(3, Utility.trim(groupRiderWrapperParam.linkRiderID));

			resultSet = pstmt.executeQuery();

			if (resultSet.next()) {

				groupRiderWrapper = new GroupRiderWrapper();
				groupRiderWrapper.groupRefNo = Utility.trim(resultSet.getString("GroupRefNo"));
				groupRiderWrapper.groupID = Utility.trim(resultSet.getString("GroupID"));
				groupRiderWrapper.linkRiderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				groupRiderWrapper.linkRiderID = Utility.trim(resultSet.getString("RiderID"));
				groupRiderWrapper.publicView = Utility.trim(resultSet.getString("Public"));
				groupRiderWrapper.status = Utility.trim(resultSet.getString("Status"));
				

				groupRiderWrapper.recordFound = true;


				System.out.println("verifyGroupsRider  successful");

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

		return groupRiderWrapper;
	}


	
	public AbstractWrapper fetchRegisteredGroups(RiderWrapper riderWrapper) throws Exception {

		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper=new DataArrayWrapper();

		Vector<Object> vector = new Vector<Object>();
		String sql = null;
		PreparedStatement pstmt=null;
		
		//RiderWrapper linkRiderWrapper=null;
		//RiderWrapper riderWrapper =null;
		
		GroupRiderWrapper groupRiderWrapper=null;
		GroupWrapper groupWrapper=null;

		try {
			con = getConnection();
			
			//RiderHelper riderHelper = new RiderHelper();
			
			sql = "SELECT a.GroupRefNo, a.GroupID, a.RiderRefNo, a.RiderID, a.Public, a.Status as GroupRiderStatus, "
					+ " b.name, b.Status as GroupStatus, a.MakerDateTime "
					+ " FROM GroupsRider a LEFT JOIN Groups b ON a.GroupRefNo=b.GroupRefNo WHERE a.RiderRefNo=? and a.RiderID=? and a.Status=? and b.Status=?"
					+ " ORDER BY a.MakerDateTime DESC";

			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, Utility.trim(riderWrapper.riderRefNo));
			pstmt.setString(2, Utility.trim(riderWrapper.riderID));
			pstmt.setString(3, Utility.trim(Constants.ACTIVE)); //GroupRiderStatus
			pstmt.setString(4, Utility.trim(Constants.ACTIVE)); //GroupStatus

			resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				groupRiderWrapper = new GroupRiderWrapper();

				groupRiderWrapper.groupRefNo = Utility.trim(resultSet.getString("GroupRefNo"));
				groupRiderWrapper.groupID = Utility.trim(resultSet.getString("GroupID"));
				groupRiderWrapper.linkRiderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				groupRiderWrapper.linkRiderID = Utility.trim(resultSet.getString("RiderID"));
				groupRiderWrapper.publicView = Utility.trim(resultSet.getString("Public"));
				groupRiderWrapper.status = Utility.trim(resultSet.getString("GroupRiderStatus"));
				groupRiderWrapper.makerDateTime = Utility.setDateAMPM(resultSet.getString("MakerDateTime"));

				groupRiderWrapper.recordFound = true;
				
				groupWrapper = new GroupWrapper();
				
				groupWrapper.groupRefNo = Utility.trim(resultSet.getString("GroupRefNo"));
				groupWrapper.groupID = Utility.trim(resultSet.getString("GroupID"));
				groupWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
				groupWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
				groupWrapper.name = Utility.trim(resultSet.getString("name"));
				groupWrapper.status = Utility.trim(resultSet.getString("GroupStatus"));
				
				groupRiderWrapper.groupWrapper = groupWrapper;

//				linkRiderWrapper = new RiderWrapper();
//				linkRiderWrapper.riderRefNo = groupRiderWrapper.linkRiderRefNo;
//				linkRiderWrapper.riderID = groupRiderWrapper.linkRiderID;
//				riderWrapper = (RiderWrapper)riderHelper.fetchRider(linkRiderWrapper);
//
//				if(riderWrapper!=null  && riderWrapper.recordFound==true)
//				{
//					groupRiderWrapper.riderWrapper = riderWrapper;
//
//				}

				vector.add(groupRiderWrapper);

				System.out.println("fetchRegisteredGroups  successful");

			}

			
			if (resultSet != null)
				resultSet.close();
			pstmt.close();
			

			
			if (vector.size() > 0) {
				
				dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[vector.size()];
				vector.copyInto(dataArrayWrapper.groupRiderWrapper);
				dataArrayWrapper.recordFound = true;

				System.out.println("total trn. in fetch " + vector.size());

			}
			else
			{
				
				dataArrayWrapper.groupRiderWrapper = new GroupRiderWrapper[1];
				groupRiderWrapper = new GroupRiderWrapper();
				groupRiderWrapper.recordFound=false;
				dataArrayWrapper.groupRiderWrapper[0]=groupRiderWrapper;
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
