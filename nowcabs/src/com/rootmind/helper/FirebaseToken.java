package com.rootmind.helper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseToken {

	
	
	public void firebaseInit() {
		// TODO Auto-generated constructor stub

		String icon=null;
		
		try {

			if (FirebaseApp.getApps().size() <= 0) {

				// System.out.println(new File(".").getAbsolutePath());
				// System.out.println("path " +
				// FCMNotification.class.getClassLoader().getResource("").getPath());

				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

				InputStream serviceAccount = classLoader
						.getResourceAsStream("nowcabs-firebase-adminsdk-crv2v-475b4b9c21.json");

				InputStream iconStream = classLoader.getResourceAsStream("nowcabs_icon48x48.png");
				icon = ImageIO.read(iconStream).toString();

				// FileInputStream serviceAccount = new FileInputStream(input.read());

				FirebaseOptions options = new FirebaseOptions.Builder()
						.setCredentials(GoogleCredentials.fromStream(serviceAccount))
						.setDatabaseUrl("https://nowcabs.firebaseio.com").build();

				FirebaseApp.initializeApp(options);

				System.out.println("FirebaseApp initiated");
			} else {

				System.out.println("FirebaseApp already initiated");

			}
		} catch (FileNotFoundException ex) {
			System.out.println("File Not found exception");
			ex.printStackTrace();

		} catch (IOException ex) {
			System.out.println("IO exception");
			ex.printStackTrace();

		} catch (Exception ex) {

			System.out.println("FCM exception");
			ex.printStackTrace();

		}

	}
	
}
