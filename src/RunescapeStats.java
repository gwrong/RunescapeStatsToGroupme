import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RunescapeStats {
	
	private final String BOT_ID = "Bot ID Here";
	
	private final String USER_AGENT = "Mozilla/5.0";
	
	private final String CURRENT_GROUP = "Group ID Here";
	
	private final String ACCESS_TOKEN = "Access Token Here";
 
	public static void main(String[] args) throws Exception {
 
		RunescapeStats http = new RunescapeStats();
 
		System.out.println("Testing 1 - Send Http GET request");
		http.listenForStats();
	}
	
	/*
	 * Listens for requests to gather runescape stats
	 */
	private void listenForStats() throws Exception {
		
		ArrayList<String> used = new ArrayList<String>();
		
		while (true) {
			
	    	File dir = new File("C:\\Users\\Graham\\Documents\\My Programming\\Java\\Groupme\\src\\");

	    	File[] files = dir.listFiles(new FilenameFilter() { 
	    	         public boolean accept(File dir, String filename)
	    	              { return filename.endsWith(".out"); }
	    	} );
	    	for (File file : files){
	    		List<String> lines = (List<String>) Files.readAllLines(file.toPath());
	    		if (lines.isEmpty()) {
	    			String fileName = file.getName();
	    			postStats("User \"" + fileName.substring(0, fileName.indexOf(".")) + "\" does not exist");
	    		} else {
	    			String result = lines.get(0);
		    		postStats(result);
	    		}
	    		file.delete();
	    	}
	    	System.out.println(files.length);
			
			
			String urlParameters = "&since_id=0&limit=5";
			String url = "https://api.groupme.com/v3/groups/" + CURRENT_GROUP + "/messages?token=" + ACCESS_TOKEN;
			url += urlParameters;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			// optional default is GET
			con.setRequestMethod("GET");
	 
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
	 
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			JSONParser parser = new JSONParser();
			JSONObject data = null;
			try {
				data = (JSONObject) parser.parse(response.toString());
			} catch (Exception e){
				System.out.println("ended" + response.toString());
			}
			
			JSONObject responses = (JSONObject) data.get("response");


			JSONArray messages = (JSONArray) responses.get("messages");
			Iterator iterator = messages.iterator();
			
			//Go through each message
			while(iterator.hasNext()) {
				JSONObject message = (JSONObject) iterator.next();
				String messageID = (String) message.get("id");
				String text = (String) message.get("text");
				if (text.startsWith("Look up ") && !used.contains(messageID) && text.length() > 8) {
					String username = text.substring(8, text.length());
					PrintWriter writer = new PrintWriter("./src/" + username + ".in", "UTF-8");
					writer.print(username);
					writer.close();
					used.add(messageID);
					System.out.println(text);
				}
				
			}
			Thread.sleep(15000);
		}
	}
	
	/*
	 * Post the message
	 */
	private void postStats(String stats) throws Exception {

		String url = "https://api.groupme.com/v3/bots/post";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
		String urlParameters = "bot_id=" + BOT_ID + "&text=" + stats;
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
	}
	
}