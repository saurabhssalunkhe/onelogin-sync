package com.icahn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;


public class ManageUserHandler implements RequestStreamHandler{

	private static final Logger logger = LogManager.getLogger(ManageUserHandler.class);

	@Override
	public void handleRequest(InputStream input, OutputStream output,
			Context context) throws IOException {
		JSONObject result = null;
		boolean weeklyProcess=false;
		try{
			InputStreamReader isReader = new InputStreamReader(input);
			BufferedReader reader = new BufferedReader(isReader);
			StringBuffer sb = new StringBuffer();
			OneloginUserManagement oneloginUsrMgmt = new OneloginUserManagement();

			String str;
			while((str = reader.readLine())!= null){
				sb.append(str);
			}
			System.out.println(sb.toString());
			JSONObject obj = new JSONObject(sb.toString());
			Util util = new Util();
			int lastProcessedUserId = obj.getInt("employeeNum");     //test 0
			
			if(lastProcessedUserId ==0){
				Config config = Config.getInstance();
				String weeklyUpdateDay = ((System.getenv("weeklyupdate")!=null)?System.getenv("weeklyupdate"):config.getValue("weeklyupdate"));
				weeklyProcess = (LocalDate.now().getDayOfWeek().name().equalsIgnoreCase(weeklyUpdateDay))?true:false;
				oneloginUsrMgmt.revokeAccessToken(oneloginUsrMgmt.getAccessToken().getString("access_token"));
			}else{
				weeklyProcess = obj.getBoolean("weeklyUpdate");   //test false
			}
			logger.info("Weekly process:"+weeklyProcess);
			
			List<Employee> associateUserList =  util.getDbUserData(lastProcessedUserId,weeklyProcess);
			int maxUserId = util.getMaxUserId();
			logger.info("maxUserId:"+maxUserId);
			logger.info("Associate User List Size:"+associateUserList.size());
				
			if(associateUserList.size()>0){
				result = oneloginUsrMgmt.processDbUsers(associateUserList,maxUserId,weeklyProcess);

			}
		}catch(Exception e){
			logger.info("Error in creating/Updating User");
			e.printStackTrace();
		}
		if(result!=null){
			System.out.println("Output:"+result.toString());
			output.write(result.toString().getBytes());
		}else{
			output.write(new JSONObject().put("continue", false).toString().getBytes());
		}
	}
}
