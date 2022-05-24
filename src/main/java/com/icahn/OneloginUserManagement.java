package com.icahn;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;







import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class OneloginUserManagement {
	private static final Logger logger = LogManager.getLogger(OneloginUserManagement.class);

	CloseableHttpClient client=null;
	HttpPost request;
	CloseableHttpResponse reponse;
	HttpGet getRequest;

	public JSONObject getAccessToken()
	{
		JSONObject json = null;
		//String accessToken = null;
		try{
			Config config = new Config();
			client = HttpClientBuilder.create().build();
			request = new HttpPost(Constants.accessToken_URL);
			Util util = new Util();
			Map<String, String> oneloginCreds = util.getOneloginCredentials();
			String clientId = oneloginCreds.get("clientid");
			String clientSecret = oneloginCreds.get("clientsecret");

			String credentials = String.format("%s:%s", clientId , clientSecret);
			byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes());
			String authHeader = "Basic " + new String(encodedAuth);

			request.setHeader("Authorization", authHeader);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity("{ \"grant_type\": \"client_credentials\" }", "UTF-8"));

			reponse = client.execute(request);
			String content = EntityUtils.toString(reponse.getEntity());
			json = new JSONObject(content);
			client.close();	
			reponse.close();

		}catch(IOException e){
			logger.info("Error in Getting access Token from OneLogin");
		}finally{
			try{
				client.close();	reponse.close();
			}catch(IOException e){
				//e.printStackTrace();
			}
		}
		return json;
	}
	public void revokeAccessToken(String accessToken){
		logger.info("Revoking access Token:"+accessToken);

		try{
			client = HttpClientBuilder.create().build();
			request = new HttpPost(Constants.revokeToken_URL);
			Util util = new Util();
			Map<String, String> oneloginCreds = util.getOneloginCredentials();
			String clientId = oneloginCreds.get("clientid");
			String clientSecret = oneloginCreds.get("clientsecret");

			String credentials = String.format("%s:%s", clientId , clientSecret);
			byte[] encodedAuth = Base64.getEncoder().encode(credentials.getBytes());
			String authHeader = "Basic " + new String(encodedAuth);

			request.setHeader("Authorization", authHeader);
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity("{ \"access_token\": \""+accessToken+"\" }", "UTF-8"));

			reponse = client.execute(request);
			String content = EntityUtils.toString(reponse.getEntity());
			client.close();	
			reponse.close();

		}catch(IOException e){
			logger.info("Error in Getting revoking Token from OneLogin");
		}finally{
			try{
				client.close();	reponse.close();
			}catch(IOException e){
				//e.printStackTrace();
			}
		}
		if(reponse.getStatusLine().getStatusCode()==200){
			logger.info("Successfully revoked token");
		}
	}
	public JSONObject processDbUsers(List<Employee> associateUserList,int maxUserId,boolean weeklyProcess){

		logger.info("Processing Database Users");
		ExecutorService execService=Executors.newFixedThreadPool(2);
		String userId = null; 
		JSONObject userJson;
		
		boolean continueProcess=true;
		System.out.println("before getting access token..");
		JSONObject authObj = getAccessToken();
		System.out.println("authObj: "+authObj.toString());
		String accessToken = authObj.getString("access_token");
		String createdAt = authObj.getString("created_at");
		System.out.println("after createdAt..");
		//logger.info("Created At:"+createdAt+"Access Token:"+accessToken);
		
		for(int i=0; i<associateUserList.size(); i++){   
			
			Employee dbUser = associateUserList.get(i);
			if((Integer.parseInt(dbUser.getEmployeeId())>0)){
				UserTask userTask = new UserTask(dbUser, accessToken);	
				execService.submit(userTask);	
			}
			if(i==associateUserList.size()-1){
				userId=associateUserList.get(i).getUserName();
			}
		}
		
		execService.shutdown();

		while (!execService.isTerminated()) {

		}
		if(Integer.parseInt(userId)>=maxUserId){
			continueProcess = false;
		}else{
			continueProcess = true;
		}
		userJson = new JSONObject();
		userJson.put("employeeNum", userId);
		userJson.put("continue", continueProcess);
		userJson.put("weeklyUpdate", weeklyProcess);
		return userJson;
	}
	public Map<String, String> getOneLoginUsers(String accessToken){
		boolean checkRecord=true;
		int pageNo=1;
		Map<String, String> userDetailsMap = new HashMap<String, String>();
		client = HttpClientBuilder.create().build();
		
		while(checkRecord){
			logger.info("url:"+Constants.oneloginUser_URL+"?fields=custom_attributes&custom_attributes.source=ADP-DW-Sync&page="+pageNo+"&limit="+Constants.limit);
			getRequest = new HttpGet(Constants.oneloginUser_URL+"?fields=custom_attributes&custom_attributes.source=ADP-DW-Sync&page="+pageNo+"&limit="+Constants.limit);
			String authHeader = "Bearer " + new String(accessToken);
			getRequest.setHeader("Authorization", authHeader);

			try {
				reponse = client.execute(getRequest);
				String content = EntityUtils.toString(reponse.getEntity());
				JSONArray UserInfoArray = new JSONArray(content);
				if(UserInfoArray.length()>0){
					UserInfoArray.forEach(record -> {
						JSONObject userRecord = (JSONObject)record;
						JSONObject customAttributes = userRecord.getJSONObject("custom_attributes");
						
						if(!customAttributes.isNull("employeeID") && !customAttributes.get("employeeID").equals("") && (Integer.parseInt(customAttributes.get("employeeID").toString()))>0){
							userDetailsMap.put(userRecord.get("id").toString(), customAttributes.get("employeeID").toString());
						}
					});  
				}else{
					checkRecord=false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			pageNo++;
		}
		try {
			client.close();
			reponse.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("OneLoginUserMap:"+userDetailsMap.size());
		return userDetailsMap;
	}
}
