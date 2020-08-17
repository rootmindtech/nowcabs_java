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
import com.rootmind.wrapper.NotificationWrapper;
import com.rootmind.wrapper.RideJobWrapper;
import com.rootmind.wrapper.RideWrapper;
import com.rootmind.wrapper.RiderWrapper;
import com.rootmind.wrapper.UsersWrapper;

public class RideHelper extends Helper{
	
	
	//-----------------Start insertRide---------------------
	
	public AbstractWrapper insertRide(UsersWrapper usersProfileWrapper, RideWrapper rideWrapper)throws Exception {
			
			Connection con = null;
			ResultSet resultSet = null;
	
			DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
			String sql=null;
			//String countryCode=null;
			
			//SimpleDateFormat dmyFormat = new SimpleDateFormat("dd-MMM-yyyy");
		
//			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
//			DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
//			symbols.setGroupingSeparator(',');
//			formatter.applyPattern("###,###,###,##0.00");
//			formatter.setDecimalFormatSymbols(symbols);
			PreparedStatement pstmt=null;
			
			
			try {
					con = getConnection();
					
					
					//----------get rider details
					sql="SELECT FirstName, MobileNo FROM Rider WHERE RiderRefNo=? AND RiderID=?"; 
					
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, Utility.trim(rideWrapper.riderRefNo));
					pstmt.setString(2, Utility.trim(rideWrapper.riderID));
					
					resultSet = pstmt.executeQuery();
					
					if(resultSet.next()) 
					{

						rideWrapper.riderName=Utility.trim(resultSet.getString("FirstName"));  
						rideWrapper.riderMobileNo=Utility.trim(resultSet.getString("MobileNo"));
					}
					resultSet.close();
					pstmt.close();
					//----------
						
					//----------get service provider details
					sql="SELECT FirstName, MobileNo, VehicleNo, VehicleType FROM Rider WHERE RiderRefNo=? AND RiderID=?"; 
					
					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, Utility.trim(rideWrapper.servicerRefNo));
					pstmt.setString(2, Utility.trim(rideWrapper.servicerID));
					
					resultSet = pstmt.executeQuery();
					
					if(resultSet.next()) 
					{

						rideWrapper.servicerName=Utility.trim(resultSet.getString("FirstName"));  
						rideWrapper.servicerMobileNo=Utility.trim(resultSet.getString("MobileNo"));
						rideWrapper.vehicleNo=Utility.trim(resultSet.getString("VehicleNo"));
						rideWrapper.vehicleType=Utility.trim(resultSet.getString("VehicleType"));
					}
					resultSet.close();
					pstmt.close();
					//--------

					sql=" INSERT INTO Ride(RideRefNo, RiderRefNo, RiderID, RiderName, RiderMobileNo, "
						+ " ServicerRefNo, ServicerID, ServicerName, ServicerMobileNo, VehicleNo, VehicleType, "
						+ " RideStartDate, RideEndDate, RideStartPoint, RideEndPoint, PickupLat, PickupLng, DropLat, "
						+ " DropLng, RideStatus, ServiceCode, AppointDateTime, MakerID, MakerDateTime) "
						+ " Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					
					System.out.println("sql " + sql + " appointDateTime : " + rideWrapper.appointDateTime);
					
					pstmt = con.prepareStatement(sql);
					rideWrapper.rideRefNo=generateRideRefNo();
					rideWrapper.rideStatus= Constants.CALLING_STATUS;
					pstmt.setString(1,Utility.trim(rideWrapper.rideRefNo));
					pstmt.setString(2,Utility.trim(rideWrapper.riderRefNo));
					pstmt.setString(3,Utility.trim(rideWrapper.riderID));
					pstmt.setString(4,Utility.trim(rideWrapper.riderName));
					pstmt.setString(5,Utility.trim(rideWrapper.riderMobileNo));
					pstmt.setString(6,Utility.trim(rideWrapper.servicerRefNo)); 
					pstmt.setString(7,Utility.trim(rideWrapper.servicerID));
					pstmt.setString(8,Utility.trim(rideWrapper.servicerName));
					pstmt.setString(9,Utility.trim(rideWrapper.servicerMobileNo));
					pstmt.setString(10,Utility.trim(rideWrapper.vehicleNo));
					pstmt.setString(11,Utility.trim(rideWrapper.vehicleType));  
					//pstmt.setString(7,Utility.trim(rideWrapper.journeyStartDate)); 
					pstmt.setTimestamp(12,Utility.getCurrentTime()); //  journeyStartDate time
					//pstmt.setString(8,Utility.trim(rideWrapper.journeyEndDate)); 
					pstmt.setTimestamp(13,Utility.getCurrentTime()); //  journeyEndDate time
					pstmt.setString(14,Utility.trim(rideWrapper.rideStartPoint)); 
					pstmt.setString(15,Utility.trim(rideWrapper.rideEndPoint));
					pstmt.setString(16,Utility.trim(rideWrapper.pickupLat));
					pstmt.setString(17,Utility.trim(rideWrapper.pickupLng));
					pstmt.setString(18,Utility.trim(rideWrapper.dropLat));
					pstmt.setString(19,Utility.trim(rideWrapper.dropLng));
					pstmt.setString(20,Utility.trim(rideWrapper.rideStatus));
					pstmt.setString(21,Utility.trim(rideWrapper.serviceCode));
					pstmt.setString(22,Utility.trim(rideWrapper.appointDateTime));
					pstmt.setString(23,Utility.trim(usersProfileWrapper.userid));
					pstmt.setTimestamp(24,Utility.getCurrentTime()); 
					
					
					pstmt.executeUpdate();
					pstmt.close();
					
					rideWrapper.recordFound=true;
				
					dataArrayWrapper.rideWrapper=new RideWrapper[1];
					dataArrayWrapper.rideWrapper[0]=rideWrapper;
					
					dataArrayWrapper.recordFound=true;
					
					System.out.println("Successfully inserted into insertRide");
					

					//-------reqeust
					NotificationHelper notificationHelper = new NotificationHelper();
					NotificationWrapper notificationWrapper = new NotificationWrapper();
					notificationWrapper.riderRefNo=rideWrapper.riderRefNo;
					notificationWrapper.riderID = rideWrapper.riderID;
					notificationWrapper.notificationCode = Constants.JOB_REQUEST_PN;
					notificationHelper.insertNotification(notificationWrapper);

					notificationWrapper.riderRefNo=rideWrapper.servicerRefNo;
					notificationWrapper.riderID = rideWrapper.servicerID;
					notificationWrapper.notificationCode = Constants.JOB_PROVIDER_PN;
					notificationHelper.insertNotification(notificationWrapper);

					
				
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
	
			return dataArrayWrapper;
		}
		
		//-----------------End insertRide---------------------

	public AbstractWrapper updateRide(UsersWrapper usersProfileWrapper, RideWrapper rideWrapper)throws Exception {
		
		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		

		PreparedStatement pstmt=null;
		
		
		
		try {
				con = getConnection();
				
	
				 pstmt = con.prepareStatement("UPDATE Ride SET RideStatus=?, ModifierID=?, ModifierDateTime=? WHERE RideRefNo=?");
			
					
					pstmt.setString(1,Utility.trim(rideWrapper.rideStatus));
					pstmt.setString(2,Utility.trim(usersProfileWrapper.userid));
					pstmt.setTimestamp(3,Utility.getCurrentTime()); 
					pstmt.setString(4,Utility.trim(rideWrapper.rideRefNo));
				
					pstmt.executeUpdate();
					pstmt.close();
	
					rideWrapper.recordFound=true;
					dataArrayWrapper.rideWrapper=new RideWrapper[1];
					dataArrayWrapper.rideWrapper[0]=rideWrapper;
					dataArrayWrapper.recordFound=true;
				
					System.out.println("Successfully Ride details Updated");
			
				
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

		return dataArrayWrapper;
}
//-----------------End updateRide--------------------
	
	//-----------------Start updateRide---------------------
//	public AbstractWrapper updateRide(UsersWrapper usersProfileWrapper, RideWrapper rideWrapper)throws Exception {
//		
//			Connection con = null;
//			ResultSet resultSet = null;
//	
//			DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
//			
//			//SimpleDateFormat dmyFormat = new SimpleDateFormat("dd-MMM-yyyy");
//		
////			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
////			DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
////			symbols.setGroupingSeparator(',');
////			formatter.applyPattern("###,###,###,##0.00");
////			formatter.setDecimalFormatSymbols(symbols);
//	
//			PreparedStatement pstmt=null;
//			
//			
//			
//			try {
//					con = getConnection();
//					
//
////					pstmt = con.prepareStatement("SELECT RideRefNo FROM Ride WHERE RideRefNo=?");
////					
////					System.out.println("Update RideRefNo  is " + rideWrapper.rideRefNo);
////					
////					pstmt.setString(1,rideWrapper.rideRefNo); //--it may null
////					
////					resultSet = pstmt.executeQuery();
////					
////					if (!resultSet.next()) 
////					{
////						resultSet.close();
////						pstmt.close();
////						dataArrayWrapper=(DataArrayWrapper)insertRide(usersProfileWrapper,rideWrapper);
////					}
////					else
////					{
////						resultSet.close();
////						pstmt.close();
//						 pstmt = con.prepareStatement("UPDATE Ride SET DriverRefNo=?, DriverID=?, VehicleType =?, "
//								 +" RideStartDate=?, RideEndDate=?, RideStartPoint=?, RideEndPoint=?, PickupTime=?, PickupDistance =?, "
//								 +" Category=?, ApproxDistance =?, ActualDistance =?, ApproxTime =?, ActualTime =?, ApproxFare =?, "
//								 +" BaseFare=?, AdditionalKMFare=?, AdditionalTimeFare=?, TotalFare=?, ServiceTax=?, CouponCode=?, "
//								 +" Discount=?, TotalBill=?, FutureRideDate=?, RideStatus=? WHERE RideRefNo=?");
//					
//						 	pstmt.setString(1,Utility.trim(rideWrapper.driverRefNo)); 
//							pstmt.setString(2,Utility.trim(rideWrapper.driverID));
//							pstmt.setString(3,Utility.trim(rideWrapper.vehicleType));  
//							//pstmt.setString(7,Utility.trim(rideWrapper.journeyStartDate)); 
//							pstmt.setTimestamp(4,Utility.getCurrentTime()); //  rideStartDate time
//							//pstmt.setString(8,Utility.trim(rideWrapper.journeyEndDate)); 
//							pstmt.setTimestamp(5,Utility.getCurrentTime()); //  rideEndDate time
//							pstmt.setString(6,Utility.trim(rideWrapper.rideStartPoint)); 
//							pstmt.setString(7,Utility.trim(rideWrapper.rideEndPoint));
//							
//							pstmt.setString(8,Utility.trim(rideWrapper.pickupTime));
//							pstmt.setString(9,Utility.trim(rideWrapper.pickupDistance));
//							pstmt.setString(10,Utility.trim(rideWrapper.category));
//							pstmt.setString(11,Utility.trim(rideWrapper.approxDistance));
//							pstmt.setString(12,Utility.trim(rideWrapper.actualDistance));
//							pstmt.setString(13,Utility.trim(rideWrapper.approxTime));
//							pstmt.setString(14,Utility.trim(rideWrapper.actualTime));
//							pstmt.setString(15,Utility.trim(rideWrapper.approxFare));
//							pstmt.setString(16,Utility.trim(rideWrapper.baseFare));
//							
//							pstmt.setString(17,Utility.trim(rideWrapper.additionalKMFare));
//							pstmt.setString(18,Utility.trim(rideWrapper.additionalTimeFare));
//							pstmt.setString(19,Utility.trim(rideWrapper.totalFare));
//							pstmt.setString(20,Utility.trim(rideWrapper.serviceTax));
//							pstmt.setString(21,Utility.trim(rideWrapper.couponCode));
//							
//							pstmt.setString(22,Utility.trim(rideWrapper.discount));
//							pstmt.setString(23,Utility.trim(rideWrapper.totalBill));
//							pstmt.setString(24,Utility.trim(rideWrapper.futureRideDate));
//							pstmt.setString(25,Utility.trim(rideWrapper.rideStatus));
//						
//							pstmt.setString(26,Utility.trim(rideWrapper.rideRefNo));
//						
//							pstmt.executeUpdate();
//							pstmt.close();
//
//							rideWrapper.recordFound=true;
//							dataArrayWrapper.rideWrapper=new RideWrapper[1];
//							dataArrayWrapper.rideWrapper[0]=rideWrapper;
//							dataArrayWrapper.recordFound=true;
//						
//							System.out.println("Successfully Ride details Updated");
//					//}
//					
//			} catch (SQLException se) {
//				se.printStackTrace();
//				throw new SQLException(se.getSQLState()+ " ; "+ se.getMessage());
//			} catch (NamingException ne) {
//				ne.printStackTrace();
//				throw new NamingException(ne.getMessage());
//			}
//			 catch (Exception ex) {
//				ex.printStackTrace();
//				throw new Exception(ex.getMessage());
//			}
//			finally
//			{
//				try
//				{
//					releaseConnection(resultSet, con);
//				} 
//				catch (SQLException se)
//				{
//					se.printStackTrace();
//					throw new Exception(se.getSQLState()+ " ; "+ se.getMessage());
//				}
//			}
//	
//			return dataArrayWrapper;
//	}
//	//-----------------End updateRide--------------------
	
	//-----------------Start fetchRide---------------------
	
	public AbstractWrapper fetchRide(UsersWrapper usersProfileWrapper, RideWrapper rideWrapper)throws Exception {

			Connection con = null;
			ResultSet resultSet = null;
			
			DataArrayWrapper dataArrayWrapper=new DataArrayWrapper();
			
			Vector<Object> vector = new Vector<Object>();
			String sql=null;
			int n = 1;
			try {
				
					con = getConnection();
					
					sql="SELECT RideRefNo, RiderRefNo, RiderID, RiderName, RiderMobileNo, " 
						+ " ServicerRefNo, ServicerID, ServicerName, ServicerMobileNo, VehicleNo, VehicleType, "
						+ " RideStartDate, RideEndDate, RideStartPoint, RideEndPoint, RideStatus, ServiceCode, AppointDateTime FROM Ride ";
					
//					PickupTime, PickupDistance, "
//							+ " Category, ApproxDistance , ActualDistance , ApproxTime , ActualTime , ApproxFare, "
//							+ " BaseFare, AdditionalKMFare, AdditionalTimeFare, TotalFare, ServiceTax, CouponCode, "
//							+ " Discount, TotalBill, FutureRideDate,"
//							+ "
					if(rideWrapper.rideType.equals(Constants.RIDER_TYPE)){
						
						sql = sql + " WHERE RiderRefNo=? and RiderID=? ";
						
					} else if(rideWrapper.rideType.equals(Constants.SERVICER_TYPE)){

						sql = sql + " WHERE ServicerRefNo=? and ServicerID=? ";

					}
					else if(rideWrapper.rideType.equals(Constants.TIMER_TYPE)){

						sql = sql + " WHERE RideRefNo=? ";

					}
					
					
					if(!Utility.isEmpty(rideWrapper.rideStatus))
					{
						sql = sql + " and RideStatus=? ";
					}
					
					sql = sql + " ORDER BY RideStartDate DESC";
					
					PreparedStatement pstmt = con.prepareStatement(sql);
					
					if(rideWrapper.rideType.equals(Constants.RIDER_TYPE)){
						
						pstmt.setString(n,Utility.trim(rideWrapper.riderRefNo));
						pstmt.setString(++n,Utility.trim(rideWrapper.riderID));
						
					} else if(rideWrapper.rideType.equals(Constants.SERVICER_TYPE)){

						pstmt.setString(n,Utility.trim(rideWrapper.servicerRefNo));
						pstmt.setString(++n,Utility.trim(rideWrapper.servicerID));

					}
					else if(rideWrapper.rideType.equals(Constants.TIMER_TYPE)){

						pstmt.setString(n,Utility.trim(rideWrapper.rideRefNo));

					}
					
					
					if(!Utility.isEmpty(rideWrapper.rideStatus))
					{
						pstmt.setString(++n,Utility.trim(rideWrapper.rideStatus));

					}
					
					resultSet = pstmt.executeQuery();
					
					while (resultSet.next()) 
					{
						
						 rideWrapper=new RideWrapper();
						
						  rideWrapper.rideRefNo =Utility.trim(resultSet.getString("RideRefNo"));
						  rideWrapper.riderRefNo =Utility.trim(resultSet.getString("RiderRefNo"));
						  rideWrapper.riderID =Utility.trim(resultSet.getString("RiderID"));
						  rideWrapper.riderName =Utility.trim(resultSet.getString("RiderName"));
						  rideWrapper.riderMobileNo =Utility.trim(resultSet.getString("RiderMobileNo"));
						  rideWrapper.servicerRefNo =Utility.trim(resultSet.getString("ServicerRefNo"));
						  rideWrapper.servicerID =Utility.trim(resultSet.getString("ServicerID"));
						  rideWrapper.servicerName =Utility.trim(resultSet.getString("ServicerName"));
						  rideWrapper.servicerMobileNo =Utility.trim(resultSet.getString("ServicerMobileNo"));
						  rideWrapper.vehicleNo =Utility.trim(resultSet.getString("VehicleNo"));
						  rideWrapper.vehicleType =Utility.trim(resultSet.getString("VehicleType"));
						  rideWrapper.rideStartDate =Utility.setDateAMPM(resultSet.getString("RideStartDate"));
						  rideWrapper.rideEndDate  =Utility.setDateAMPM(resultSet.getString("RideEndDate")); 
						  rideWrapper.rideStartPoint=Utility.trim(resultSet.getString("RideStartPoint"));
						  rideWrapper.rideEndPoint=Utility.trim(resultSet.getString("RideEndPoint"));
						   
//						  rideWrapper.pickupTime =Utility.setDate(resultSet.getString("PickupTime"));
//						  rideWrapper.pickupDistance =Utility.trim(resultSet.getString("PickupDistance"));  
//						  rideWrapper.category =Utility.trim(resultSet.getString("Category"));
//						  rideWrapper.approxDistance =Utility.trim(resultSet.getString("ApproxDistance"));
//						  rideWrapper.actualDistance =Utility.trim(resultSet.getString("ActualDistance"));
//						  rideWrapper.approxTime =Utility.setDate(resultSet.getString("ApproxTime"));
//						  rideWrapper.actualTime =Utility.setDate(resultSet.getString("ActualTime"));
//						  rideWrapper.approxFare =Utility.trim(resultSet.getString("ApproxFare"));
//						  rideWrapper.baseFare=Utility.trim(resultSet.getString("BaseFare"));
//						  rideWrapper.additionalKMFare=Utility.trim(resultSet.getString("AdditionalKMFare"));
//						  rideWrapper.additionalTimeFare=Utility.trim(resultSet.getString("AdditionalTimeFare"));
//						  rideWrapper.totalFare=Utility.trim(resultSet.getString("TotalFare"));
//						  rideWrapper.serviceTax=Utility.trim(resultSet.getString("ServiceTax"));
//						  rideWrapper.couponCode=Utility.trim(resultSet.getString("CouponCode"));
//						  rideWrapper.discount=Utility.trim(resultSet.getString("Discount"));
//						  rideWrapper.totalBill=Utility.trim(resultSet.getString("TotalBill"));
//						  rideWrapper.futureRideDate=Utility.setDate(resultSet.getString("FutureRideDate"));
						  
						  rideWrapper.rideStatus=Utility.trim(resultSet.getString("RideStatus"));
						  rideWrapper.serviceCode=Utility.trim(resultSet.getString("ServiceCode"));
						  rideWrapper.appointDateTime  =Utility.setDateAMPM(resultSet.getString("AppointDateTime")); 

						
						rideWrapper.recordFound=true;

						
						
						System.out.println("fetchRide  successful");
		
						vector.addElement(rideWrapper);
		
				}
				
				if (vector.size()>0)
				{
					dataArrayWrapper.rideWrapper = new RideWrapper[vector.size()];
					vector.copyInto(dataArrayWrapper.rideWrapper);
					dataArrayWrapper.recordFound=true;
		
					System.out.println("total trn. in fetch " + vector.size());
		
				}
				else
				{
					dataArrayWrapper.rideWrapper = new RideWrapper[1];
					dataArrayWrapper.rideWrapper[0] = new RideWrapper();
					vector.copyInto(dataArrayWrapper.rideWrapper);
					dataArrayWrapper.recordFound=true;
					
				}
				
				if (resultSet!=null)  resultSet.close();
				pstmt.close();

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

			return dataArrayWrapper;
	}
	//-----------------End fetchRide---------------------
	
	
	//-----------------Start fetchRidesearch---------------------
	
			public AbstractWrapper fetchRideSearch(UsersWrapper usersProfileWrapper,RideWrapper rideWrapper)throws Exception {

					Connection con = null;
					ResultSet resultSet = null;
					String sql=null;
					DataArrayWrapper dataArrayWrapper=new DataArrayWrapper();
		
					
					
					Vector<Object> vector = new Vector<Object>();
					
					try {
							
						    //PopoverHelper popoverHelper=new PopoverHelper();
							con = getConnection();
							

							if(!Utility.isEmpty(rideWrapper.riderID))
							{
								sql= " WHERE RiderID =?";
								
								System.out.println("rideWrapper RiderID " + sql);
								
							}
							
							else if(!Utility.isEmpty(rideWrapper.servicerID))
							{
								
								
								sql=" WHERE ServicerID =?";
								
								
								System.out.println(" servicerID " +rideWrapper.servicerID);
								
								
							}
							
						
							PreparedStatement pstmt = con.prepareStatement("SELECT RideRefNo, RiderRefNo, RiderID, ServicerRefNo, ServicerID, VehicleType, "
									+ " RideStartDate, RideEndDate, RideStartPoint, RideEndPoint, PickupTime, PickupDistance, "
									+ " Category, ApproxDistance , ActualDistance , ApproxTime , ActualTime , ApproxFare, "
									+ " BaseFare, AdditionalKMFare, AdditionalTimeFare, TotalFare, ServiceTax, CouponCode, "
									+ " Discount, TotalBill, FutureRideDate, RideStatus FROM Ride "+sql);
						
							
							
							if(!Utility.isEmpty(rideWrapper.riderID))
							{
								pstmt.setString(1, Utility.trim(rideWrapper.riderID));
								
								
							}
							
							else if(!Utility.isEmpty(rideWrapper.servicerID))
							{
								
									pstmt.setString(1, Utility.trim(rideWrapper.servicerID));
								
									
							}

		
							resultSet = pstmt.executeQuery();
							
							while (resultSet.next()) 
							{
								rideWrapper=new RideWrapper();
								
								
								  rideWrapper.rideRefNo =Utility.trim(resultSet.getString("RideRefNo"));
								  rideWrapper.riderRefNo =Utility.trim(resultSet.getString("RiderRefNo"));
								  rideWrapper.riderID =Utility.trim(resultSet.getString("RiderID"));
								  rideWrapper.servicerRefNo =Utility.trim(resultSet.getString("ServicerRefNo"));
								  rideWrapper.servicerID =Utility.trim(resultSet.getString("ServicerID"));
								  rideWrapper.vehicleType =Utility.trim(resultSet.getString("VehicleType"));
								  rideWrapper.rideStartDate =Utility.setDate(resultSet.getString("RideStartDate"));
								  rideWrapper.rideEndDate  =Utility.setDate(resultSet.getString("RideEndDate")); 
								  rideWrapper.rideStartPoint=Utility.trim(resultSet.getString("RideStartPoint"));
								  rideWrapper.rideEndPoint=Utility.trim(resultSet.getString("RideEndPoint"));
								   
								  rideWrapper.pickupTime =Utility.setDate(resultSet.getString("PickupTime"));
								  rideWrapper.pickupDistance =Utility.trim(resultSet.getString("PickupDistance"));  
								  rideWrapper.category =Utility.trim(resultSet.getString("Category"));
								  rideWrapper.approxDistance =Utility.trim(resultSet.getString("ApproxDistance"));
								  rideWrapper.actualDistance =Utility.trim(resultSet.getString("ActualDistance"));
								  rideWrapper.approxTime =Utility.setDate(resultSet.getString("ApproxTime"));
								  rideWrapper.actualTime =Utility.setDate(resultSet.getString("ActualTime"));
								  rideWrapper.approxFare =Utility.trim(resultSet.getString("ApproxFare"));
								  rideWrapper.baseFare=Utility.trim(resultSet.getString("BaseFare"));
								  rideWrapper.additionalKMFare=Utility.trim(resultSet.getString("AdditionalKMFare"));
								  rideWrapper.additionalTimeFare=Utility.trim(resultSet.getString("AdditionalTimeFare"));
								  rideWrapper.totalFare=Utility.trim(resultSet.getString("TotalFare"));
								  rideWrapper.serviceTax=Utility.trim(resultSet.getString("ServiceTax"));
								  rideWrapper.couponCode=Utility.trim(resultSet.getString("CouponCode"));
								  rideWrapper.discount=Utility.trim(resultSet.getString("Discount"));
								  rideWrapper.totalBill=Utility.trim(resultSet.getString("TotalBill"));
								  rideWrapper.futureRideDate=Utility.setDate(resultSet.getString("FutureRideDate"));
								  rideWrapper.rideStatus=Utility.trim(resultSet.getString("RideStatus"));
								  
								  
								
								rideWrapper.recordFound=true;
								
								System.out.println("Ride Details Queue fetch successful");
				
								vector.addElement(rideWrapper);
				
						}
						
						if (vector.size()>0)
						{
							dataArrayWrapper.rideWrapper = new RideWrapper[vector.size()];
							vector.copyInto(dataArrayWrapper.rideWrapper);
							dataArrayWrapper.recordFound=true;
				
							System.out.println("total trn. in fetch " + vector.size());
				
						}
						
						resultSet.close();
						pstmt.close();
		
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
		
					return dataArrayWrapper;
			}
			//-----------------End fetchtravellerSearch---------------------
	
	//-----------------generateRideRefNo-------------------------------
	public String generateRideRefNo()throws Exception {
		
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
		
		int rideRefNo=0;
		String finalRideRefNo=null;
		String rideCode=null;
		
		try {
			
			
			con = getConnection();
			
			
			sql="SELECT RideRefNo, RideCode from Parameter";
			
			PreparedStatement pstmt = con.prepareStatement(sql);
		
			resultSet = pstmt.executeQuery();
			
			if (resultSet.next()) 
			{
				
				rideRefNo=resultSet.getInt("RideRefNo");
				System.out.println("RideRefNo " + rideRefNo);
				rideCode=resultSet.getString("RideCode");
				
			}
			
			resultSet.close();
			pstmt.close();
			
			if(rideRefNo==0)
			{
				rideRefNo=1;
				
			}
			else
			{
				
				rideRefNo=rideRefNo+1;
			}
				
			sql="UPDATE Parameter set RideRefNo=?";
			
			
			System.out.println("sql " + sql);
			
			pstmt = con.prepareStatement(sql);
	
			pstmt.setInt(1,rideRefNo);
			
			pstmt.executeUpdate();
			pstmt.close();
			
			int paddingSize=6;

			finalRideRefNo=rideCode+dmyFormat.format(new java.util.Date()).toUpperCase()+String.format("%0" + paddingSize +"d",rideRefNo);
			
			System.out.println("Successfully generated RideRefNo " + finalRideRefNo);
			
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

		return finalRideRefNo;
	}
	
	//-----------------End generateRideRefNo---------------------------
	
	public AbstractWrapper insertRideJob(UsersWrapper usersProfileWrapper, RideJobWrapper[] rideJobWrapperArray)throws Exception {
		
		Connection con = null;
		ResultSet resultSet = null;

		DataArrayWrapper dataArrayWrapper = new DataArrayWrapper();
		String sql=null;
		PreparedStatement pstmt=null;
		
		
		try {
				con = getConnection();
				
				for(int i=0;i<rideJobWrapperArray.length;i++)
				{
				
						sql=" INSERT INTO RideJobs(RideRefNo, RiderRefNo, RiderID,  "
							+ " ServicerRefNo, ServicerID, JobID, JobName, ServiceCode, Rate, Status, MakerID, MakerDateTime) "
							+ " Values (?,?,?,?,?,?,?,?,?,?,?,?)";
						
						System.out.println("sql " + sql );
						
						pstmt = con.prepareStatement(sql);
						rideJobWrapperArray[i].status= Constants.ACTIVE;
						pstmt.setString(1,Utility.trim(rideJobWrapperArray[i].rideRefNo));
						pstmt.setString(2,Utility.trim(rideJobWrapperArray[i].riderRefNo));
						pstmt.setString(3,Utility.trim(rideJobWrapperArray[i].riderID));
						pstmt.setString(4,Utility.trim(rideJobWrapperArray[i].servicerRefNo)); 
						pstmt.setString(5,Utility.trim(rideJobWrapperArray[i].servicerID));
						pstmt.setString(6, Utility.trim(rideJobWrapperArray[i].jobID));
						pstmt.setString(7, Utility.trim(rideJobWrapperArray[i].jobName));
						pstmt.setString(8, Utility.trim(rideJobWrapperArray[i].serviceCode));
						pstmt.setDouble(9, rideJobWrapperArray[i].rate);
						pstmt.setString(10, Utility.trim(rideJobWrapperArray[i].status));
						pstmt.setString(11, Utility.trim(rideJobWrapperArray[i].riderID));
						pstmt.setTimestamp(12, Utility.getCurrentTime());
						
						
						pstmt.executeUpdate();
						pstmt.close();
						
						rideJobWrapperArray[i].recordFound=true;
						

				}
			
				dataArrayWrapper.rideJobWrapper=new RideJobWrapper[1];
				RideJobWrapper rideJobWrapper = new RideJobWrapper();
				rideJobWrapper.recordFound=true;
				dataArrayWrapper.rideJobWrapper[0]=rideJobWrapper;
				
				dataArrayWrapper.recordFound=true;
				
				System.out.println("Successfully inserted into insertRideJob");

				
			
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

		return dataArrayWrapper;
	}

}
