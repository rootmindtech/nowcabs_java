package com.rootmind.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

//import org.quartz.Job;
////import org.quartz.JobDataMap;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;

import com.rootmind.wrapper.DataArrayWrapper;
import com.rootmind.wrapper.NotificationWrapper;
import com.rootmind.wrapper.RiderWrapper;

public class JobScheduler extends Helper   {

	
	//implements Job
	
		public void fetchNotification() throws Exception {

			Connection con = null;
			ResultSet resultSet = null;
			PreparedStatement pstmt = null;
			RiderWrapper riderWrapper = null;

			try {
				
				
				System.out.println("fetchNotification");

				
				NotificationHelper notificationHelper = new NotificationHelper();
				DataArrayWrapper dataArrayWrapper = (DataArrayWrapper) notificationHelper.fetchNotification();
				if (dataArrayWrapper.recordFound == true && dataArrayWrapper.notificationWrapper != null
						&& dataArrayWrapper.notificationWrapper.length > 0) {

					con = getConnection();
					NotificationWrapper notificationWrapper=null;
					String sql=null;
					String MSG_CODE=null;
					String message=null;
					
					for(int i=0;i<dataArrayWrapper.notificationWrapper.length;i++)
					{
						notificationWrapper = dataArrayWrapper.notificationWrapper[i];
						
						if (notificationWrapper.recordFound == true) {
							

							sql="SELECT RiderRefNo, RiderID, FirstName, FCMToken  FROM Rider WHERE RiderRefNo=? and RiderID=?";
							
							pstmt = con.prepareStatement(sql);

							pstmt.setString(1, Utility.trim(notificationWrapper.riderRefNo));
							pstmt.setString(2, Utility.trim(notificationWrapper.riderID));

							resultSet = pstmt.executeQuery();
							if (resultSet.next()) {
								riderWrapper = new RiderWrapper();
								riderWrapper.riderRefNo = Utility.trim(resultSet.getString("RiderRefNo"));
								riderWrapper.riderID = Utility.trim(resultSet.getString("RiderID"));
								riderWrapper.firstName = Utility.trim(resultSet.getString("FirstName"));
								riderWrapper.fcmToken = Utility.trim(resultSet.getString("FCMToken"));

								System.out.println("FCM riderWrapper.fcmToken " + riderWrapper.fcmToken);

							}
							if (resultSet != null)
								resultSet.close();
							pstmt.close();

							if (riderWrapper != null && !Utility.isEmpty(riderWrapper.fcmToken) && !Utility.isEmpty(notificationWrapper.notificationCode)) {


								switch (notificationWrapper.notificationCode) {
								case Constants.REGISTER_PN:
									MSG_CODE="REGISTER";
									message="You have registered for NOW";
									break;
								case Constants.REG_SERVICE_PN:
									MSG_CODE="SERVICE";
									message="You have registered for Service";
									break;
								case Constants.JOB_REQUEST_PN:
									MSG_CODE="JOB";
									message="You have requested for a Job";
									break;
								case Constants.JOB_PROVIDER_PN:
									MSG_CODE="JOB";
									message="You have received a request for Job";
									break;
								default:
									break;
								}
								Messaging.sendOverrideMessage(MSG_CODE, riderWrapper.fcmToken, message);

							}
							
							notificationHelper.updateNotification(notificationWrapper);
							
							
						}
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

			return;
		}

//		@Override
//		public void execute(JobExecutionContext context) throws JobExecutionException {
//			// TODO Auto-generated method stub
//
//			try {
////				JobDataMap data = context.getJobDetail().getJobDataMap();
////				String refID = data.getString("refID");
//
//				fetchNotification();
//				
//				
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//
//		}
	
}

//
//Scheduler scheduler = null;
//
//public void jobScheduler()
//
//{
//
//	try {
//
//		if(scheduler==null)
//		{
//			// schedule the job
//			SchedulerFactory schFactory = new StdSchedulerFactory();
//			Scheduler scheduler = schFactory.getScheduler();
//			scheduler.start();
//		}
//
//	} catch (SchedulerException ex) {
//		ex.printStackTrace();
//	}
//
//}
//
//public void triggerJobRegService(String refID) {
//	try {
//		// specify the running period of the job
//		Trigger triggerRegService = TriggerBuilder.newTrigger().withIdentity("triggerRegService", "FCMGroup")
//				.startNow().forJob(JobKey.jobKey("JOB_REGSERVICE", "FCMGroup"))
//				// .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever())
//				.build();
//
//		
//		// specify the job' s details..
//		JobDetail jobRegService = JobBuilder.newJob(FCMNotification.class)
//				.withIdentity("JOB_REGSERVICE", "FCMGroup").storeDurably().build();
//		jobRegService.getJobDataMap().put("SERVICE", "JOB_REGSERVICE");
//		jobRegService.getJobDataMap().put("RefID",refID);
//		scheduler.addJob(jobRegService, false);
//
//		scheduler.scheduleJob(triggerRegService);
//
//	} catch (Exception ex) {
//		ex.printStackTrace();
//
//	}
//}