package com.icahn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.icahn.Util;
import com.icahn.Config;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

public class DisableUserHandler implements RequestStreamHandler{

	private static final Logger logger = LogManager.getLogger(DisableUserHandler.class);
	@Override
	public void handleRequest(InputStream input, OutputStream output,Context context) throws IOException {
		JSONObject result = new JSONObject();
		InputStreamReader isReader = new InputStreamReader(input);
		BufferedReader reader = new BufferedReader(isReader);
		StringBuffer inputBuffer = new StringBuffer();
		String str;
		while((str = reader.readLine())!= null){
			inputBuffer.append(str);
		}
		JSONObject inputObject = new JSONObject(inputBuffer.toString());
		boolean continueProcess = true;

		try{
			Config config = Config.getInstance();
			String weeklyUpdateDay = ((System.getenv("weeklyupdate")!=null)?System.getenv("weeklyupdate"):config.getValue("weeklyupdate"));
			boolean weeklyProcess = (LocalDate.now().getDayOfWeek().name().equalsIgnoreCase(weeklyUpdateDay))?true:false;
			logger.info("Weekly process:"+weeklyProcess);
			if(weeklyProcess){
				Util util = new Util();
				List<String> associateUserIdList =  util.getDbUserIdList(0);
				OneloginUserManagement oneloginUsrMgmt = new OneloginUserManagement();
				String accessToken = oneloginUsrMgmt.getAccessToken().getString("access_token");
				if(accessToken!=null){

					ExecutorService execService=Executors.newFixedThreadPool(2);
					Map<String, String> oneLoginUserMap = oneloginUsrMgmt.getOneLoginUsers(accessToken);
					Set<String> valueSet = new HashSet<String>(oneLoginUserMap.values());
					logger.info("count before removing:"+valueSet.size());

					valueSet.removeAll(associateUserIdList);
					
					logger.info("count after removing:"+valueSet.size());
					
					 Map<String, String> olMapEntry = oneLoginUserMap.entrySet() 
					          .stream() 
					          .filter(map -> valueSet.contains(map.getValue())) 
					          .collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));  
					 logger.info("Disable User Map"+olMapEntry);
					
					List<String> disableUserList = new ArrayList<String>(olMapEntry.keySet());
					logger.info("disable user List Size:"+disableUserList.size());
					Collections.sort(disableUserList);

					int end=disableUserList.size();
					logger.info("count after removing:"+disableUserList.size());
					int batchSize = inputObject.getInt("batchsize");
					int batchCount = disableUserList.size() / batchSize;
					int remainder = disableUserList.size() % batchSize;
					int start = inputObject.getInt("start");
					if(end>batchSize){
						end = inputObject.getInt("end");
					}else{
						batchCount = 1;
					}
					if(end>0){
						int batchNumber = inputObject.getInt("batchnumber");
						for(int counter = start ; counter < end ; counter ++)
						{
							String oneLoginUserId = disableUserList.get(counter);
							logger.info("UserId to disable:"+oneLoginUserId);
							DisableUserTask userTask = new DisableUserTask(accessToken,oneLoginUserId);	
							execService.submit(userTask);	
						}
						start = start + batchSize;
						end = end + batchSize;
						if(batchNumber==batchCount){
							if(remainder != 0)
							{
								end = end - batchSize + remainder;
								for(int counter = start ; counter < end ; counter ++)
								{
									String oneLoginUserId = disableUserList.get(counter);
									logger.info("UserId to disable:"+oneLoginUserId);
									DisableUserTask userTask = new DisableUserTask(accessToken,oneLoginUserId);	
									execService.submit(userTask);	
								}
							}
							result.put("continue", false);
						}else{
							result.put("start", start);
							result.put("end", end);
							result.put("batchnumber",batchNumber+1);
							result.put("continue", true);
							result.put("batchsize",batchSize);
						}
						execService.shutdown();

						while (!execService.isTerminated()) {

						}
					}else{
						continueProcess = false;
						result.put("continue", continueProcess);
					}
				}
			}else{
				continueProcess = false;
				result.put("continue", continueProcess);

			}

		}catch(Exception e){
			logger.info("Error in creating/Updating User");
			e.printStackTrace();
		}
		System.out.println("Output:"+result.toString());
		output.write(result.toString().getBytes());
	}
}
