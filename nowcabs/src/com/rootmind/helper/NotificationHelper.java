package com.rootmind.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.naming.NamingException;

import com.rootmind.wrapper.AbstractWrapper;
import com.rootmind.wrapper.DataArrayWrapper;
import com.rootmind.wrapper.NotificationWrapper;

public class NotificationHelper extends Helper{
	
	
	//---------insert Notification
	public AbstractWrapper insertNotification( NotificationWrapper notificationWrapper)
				throws Exception {

			Connection con = null;
			ResultSet resultSet = null;
			String sql = null;

			PreparedStatement pstmt = null;

			try {


				con = getConnection();

	
					sql = " INSERT INTO Notification(RiderRefNo, RiderID, NotificationCode, Status, MakerID, MakerDateTime) "
							+ " Values (?,?,?,?,?,?)";

					System.out.println("sql " + sql);

					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, Utility.trim(notificationWrapper.riderRefNo));
					pstmt.setString(2, Utility.trim(notificationWrapper.riderID));
					pstmt.setString(3, Utility.trim(notificationWrapper.notificationCode));
					pstmt.setString(4, Constants.PENDING);
					pstmt.setString(5, Utility.trim(notificationWrapper.riderID));
					pstmt.setTimestamp(6, Utility.getCurrentTime()); // date time

					pstmt.executeUpdate();
					pstmt.close();
					

					System.out.println("Successfully inserted into insert notification");
	

							
				

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

			return null;
		}
		
	public AbstractWrapper updateNotification( NotificationWrapper notificationWrapper)
			throws Exception {

		Connection con = null;
		ResultSet resultSet = null;
		String sql = null;

		PreparedStatement pstmt = null;

		try {


			con = getConnection();


			sql = " UPDATE Notification SET Status=?, ModifierID=?, ModifierDateTime=? "
					+ " WHERE RiderRefNo=? AND RiderID=? AND NotificationCode=? AND SeqNo=?";

			System.out.println("sql " + sql);

			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, Constants.SENT);
			pstmt.setString(2, Utility.trim(notificationWrapper.riderID));
			pstmt.setTimestamp(3, Utility.getCurrentTime()); // date time
			pstmt.setString(4, Utility.trim(notificationWrapper.riderRefNo));
			pstmt.setString(5, Utility.trim(notificationWrapper.riderID));
			pstmt.setString(6, Utility.trim(notificationWrapper.notificationCode));
			pstmt.setInt(7, notificationWrapper.seqNo);

			pstmt.executeUpdate();
			pstmt.close();

			

			System.out.println("Successfully updated into updateNotification");
						
			

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

		return null;
	}
	
		
		public AbstractWrapper fetchNotification() throws Exception {

			Connection con = null;
			ResultSet resultSet = null;

			DataArrayWrapper dataArrayWrapper=new DataArrayWrapper();

			Vector<Object> vector = new Vector<Object>();
			String sql = null;
			PreparedStatement pstmt=null;
			
			NotificationWrapper notificationWrapper=null;


			try {
				con = getConnection();
				

				sql = "SELECT SeqNo, RiderRefNo, RiderID, NotificationCode, Status "
						+ " FROM Notification WHERE Status=?";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, Constants.PENDING);

				resultSet = pstmt.executeQuery();

				while (resultSet.next()) {

					notificationWrapper = new NotificationWrapper();

					notificationWrapper.seqNo = resultSet.getInt("SeqNo");
					notificationWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
					notificationWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
					notificationWrapper.notificationCode = Utility.trim(resultSet.getString("NotificationCode"));
					notificationWrapper.status = Utility.trim(resultSet.getString("Status"));
					

					notificationWrapper.recordFound = true;

					vector.add(notificationWrapper);

					System.out.println("fetchNotification  successful");

				}


				
				if (resultSet != null)
					resultSet.close();
				pstmt.close();
				

				
				if (vector.size() > 0) {
					
					dataArrayWrapper.notificationWrapper = new NotificationWrapper[vector.size()];
					vector.copyInto(dataArrayWrapper.notificationWrapper);
					dataArrayWrapper.recordFound = true;
	
					System.out.println("total trn. in fetch " + vector.size());
	
				}
				else
				{
					
					dataArrayWrapper.notificationWrapper = new NotificationWrapper[1];
					NotificationWrapper notificationWrapper2= new NotificationWrapper();
					notificationWrapper2.recordFound=false;
					dataArrayWrapper.notificationWrapper[0]=notificationWrapper2;
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