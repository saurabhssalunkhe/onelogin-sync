package com.icahn;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

class UserTask implements Runnable{
	private static final Logger logger = LogManager.getLogger(OneloginUserManagement.class);

	private final Employee item;
	private final String accessToken;
	public UserTask(Employee item, String accessToken){
		this.item = item;
		this.accessToken = accessToken;
	}
	@Override
	public void run() {
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse reponse = null;

		JSONObject user;
		JSONObject customAttribute;
		List<Employee> finalUserList;
		String authHeader = "Bearer " + new String(accessToken);
		
		System.out.println("run method called");
	/*
		List<Employee> oneloginEmp = getUserDetailsList(accessToken,"?custom_attributes.employeeID="+item.getEmployeeId());
		
		List<Employee> adSyncUserList = new ArrayList<Employee>();
		
		if(oneloginEmp.size()>1){

			oneloginEmp.forEach(olUser ->{
				if(!olUser.getSource().equals("ADP-DW-Sync")){
					adSyncUserList.add(olUser);
				}
			});
		}
		if(adSyncUserList.size()>0){
			finalUserList = adSyncUserList;
			oneloginEmp.removeAll(finalUserList);
			oneloginEmp.forEach(olUser -> {   
				deleteUser(olUser.getOneLoginId(), authHeader); 
			});
		}else{
			finalUserList = oneloginEmp;
		}
		
		if(finalUserList.size()>0){
		
			if(item.getEmployeeStatus().equals("T")){  
				 
				if((ChronoUnit.DAYS.between(item.getTerminationDate(), LocalDate.now()))>30){ 
					finalUserList.forEach(olUser ->{
						Employee oneLoginUser = getUserDetails(accessToken, "/"+olUser.getOneLoginId());
						if(oneLoginUser.getSource().equals(Constants.source)){
							logger.info("Deleting user -- Employee_Num:"+item.getEmployeeId()+" -Employee Status:"+item.getEmployeeStatus()+" -OneLoginId:"+olUser.getOneLoginId());
							deleteUser(olUser.getOneLoginId(), authHeader);
						}
					});
				}else{
					user = new JSONObject();
					user.put("status", 0);
					finalUserList.forEach(olUser ->{
						Employee oneLoginUser = getUserDetails(accessToken, "/"+olUser.getOneLoginId());
						if(oneLoginUser.getSource().equals(Constants.source)){
							logger.info("Disabling user -- Employee_Num:"+item.getEmployeeId()+" -Employee Status:"+item.getEmployeeStatus()+" -OneLoginId:"+oneLoginUser.getOneLoginId());
							updateUser(user.toString(), olUser.getOneLoginId(), authHeader);
						}
					});
				}	
			}else if((item.getEmployeeStatus().equals("S") || item.getEmployeeStatus().equals("L"))){
				user = new JSONObject();
				
				user.put("status", 0);
				finalUserList.forEach(olUser ->{
					Employee oneLoginUser = getUserDetails(accessToken, "/"+olUser.getOneLoginId());
					if(oneLoginUser.getSource().equals(Constants.source)){
						logger.info("Disabling user -- Employee_Num:"+item.getEmployeeId()+" -Employee Status:"+item.getEmployeeStatus()+" -OneLoginId:"+oneLoginUser.getOneLoginId());
						updateUser(user.toString(), olUser.getOneLoginId(), authHeader);
					}
				});
				

			}else if(item.getEmployeeStatus().equals("A")){ 

				finalUserList.forEach(olUser ->{
					Employee finalManager = null;
					JSONObject userUpdate = new JSONObject();
					JSONObject customAttributesUpdate = new JSONObject();
					Employee oneLoginUser = getUserDetails(accessToken, "/"+olUser.getOneLoginId());
					if(item.getDepartment()!=null && item.getDepartment().equals(oneLoginUser.getDepartment())==false){userUpdate.put("department", item.getDepartment());}
					if(item.getTitle()!=null && !item.getTitle().equals(oneLoginUser.getTitle().replaceAll("&amp;","&"))){userUpdate.put("title", item.getTitle());}
					
					if(oneLoginUser.getSource().equals("ADP-DW-Sync")) {
						
						if(oneLoginUser.getEmployeeStatus().equals("0") || oneLoginUser.getEmployeeStatus().equals("2")){userUpdate.put("status", 1);}
					}
					List<Employee> manager = getUserDetailsList(accessToken,"?custom_attributes.employeeID="+item.getManager());
				
					if(!manager.isEmpty()) {
					
						
						try {
							finalManager = manager.stream().filter(usr-> !usr.getSource().equals("ADP-DW-Sync")).findFirst().get();
						} catch (Exception e) {}
						if(finalManager==null){
							finalManager = manager.get(0);
						}
						if(manager.size()>0 && !finalManager.getOneLoginId().equals(oneLoginUser.getManager())){
							userUpdate.put("manager_user_id",finalManager.getOneLoginId());
						}		
					
					}

					if(item.getCompanyCode()!=null && !item.getCompanyCode().equals(oneLoginUser.getCompanyCode())){customAttributesUpdate.put("companyCode",item.getCompanyCode());}
					if(item.getShortLocCode()!=null && !item.getShortLocCode().equals(oneLoginUser.getShortLocCode())){customAttributesUpdate.put("shortLocCode",item.getShortLocCode());}
					if(item.getCoAlign()!=null && !item.getCoAlign().equals(oneLoginUser.getCoAlign())){customAttributesUpdate.put("coAlign",item.getCoAlign());}
					if(item.getUserVerify()!=null && !item.getUserVerify().equals(oneLoginUser.getUserVerify())){customAttributesUpdate.put("userVerify",item.getUserVerify());}
					if(item.getHomeCity()!=null && !item.getHomeCity().equals(oneLoginUser.getHomeCity())){customAttributesUpdate.put("homeCity",item.getHomeCity());}
					if(item.getHomeState()!=null && !item.getHomeState().equals(oneLoginUser.getHomeState())){customAttributesUpdate.put("homeState",item.getHomeState());}
					if(item.getHomeZip()!=null && !item.getHomeZip().equals(oneLoginUser.getHomeZip())){customAttributesUpdate.put("homeZip",item.getHomeZip());}
					if(item.getRegionCode()!=null && !item.getRegionCode().equals(oneLoginUser.getRegionCode())){customAttributesUpdate.put("regionCode",item.getRegionCode());}
					if(item.getRegion()!=null && !item.getRegion().equals(oneLoginUser.getRegion())){customAttributesUpdate.put("region",item.getRegion());}
					if(item.getAreaCode()!=null && !item.getAreaCode().equals(oneLoginUser.getAreaCode())){customAttributesUpdate.put("areaCode",item.getAreaCode());}
					if(item.getArea()!=null && !item.getArea().equals(oneLoginUser.getArea())){customAttributesUpdate.put("area",item.getArea());}
					if(item.getLocationCode()!=null && !item.getLocationCode().equals(oneLoginUser.getLocationCode())){customAttributesUpdate.put("locationCode",item.getLocationCode());}
					if(item.getLocation()!=null && !item.getLocation().equals(oneLoginUser.getLocation())){customAttributesUpdate.put("location",item.getLocation());}
					if(item.getDepartmentCode()!=null && !item.getDepartmentCode().equals(oneLoginUser.getDepartmentCode())){customAttributesUpdate.put("departmentCode",item.getDepartmentCode());}
					if(item.getTitleCode()!=null && !item.getTitleCode().equals(oneLoginUser.getTitleCode())){customAttributesUpdate.put("titleCode", item.getTitleCode());}
					if(item.getDepartmentCode()!=null && !item.getEmployeeId().equals(oneLoginUser.getEmployeeId())){customAttributesUpdate.put("employeeID", item.getEmployeeId());}
					if(item.getSupervisorEmpNumber()!=null && !item.getSupervisorEmpNumber().equals(oneLoginUser.getSupervisorEmpNumber())){customAttributesUpdate.put("managerEmpID", item.getSupervisorEmpNumber());}
					if(item.getWorkInState()!=null && !item.getWorkInState().equals(oneLoginUser.getWorkInState())){customAttributesUpdate.put("workInState", item.getWorkInState());}
					if(item.getManagerLevel()!=null && !item.getManagerLevel().equals(oneLoginUser.getManagerLevel())){customAttributesUpdate.put("managerLevel", item.getManagerLevel());}
					if(item.getAlternateEmail()!=null && !item.getAlternateEmail().equals(oneLoginUser.getAlternateEmail())){customAttributesUpdate.put("alternateEmail", item.getAlternateEmail());}
					
					if(!customAttributesUpdate.isEmpty()){
						userUpdate.put("custom_attributes", customAttributesUpdate);
					}
					if(!userUpdate.isEmpty()){
						logger.info("updating user -- Employee_Num:"+item.getEmployeeId()+" -OneLoginId:"+oneLoginUser.getOneLoginId()+ " Request:"+userUpdate.toString());
						updateUser(userUpdate.toString(), oneLoginUser.getOneLoginId(), authHeader);  //updating user in  OL
					}
				});
			}
		}else{   //creating new user
			if(item.getEmployeeStatus().equals("A")){
				user = new JSONObject();
				customAttribute = new JSONObject();


				String companyCode = item.getCompanyCode();
				String emailHost;

		
								
				
				emailHost="@autoplusap.com";
				user.put("username", item.getEmployeeId());
				user.put("email", item.getEmployeeId()+emailHost);
				
				user.put("firstname", item.getFirstName());
				user.put("lastname", item.getLastName());


				//mycode
				StringBuilder def_pass = new StringBuilder();
				def_pass.append(Character.toString(item.getFirstName().toUpperCase().charAt(0)));
				def_pass.append(Character.toString(item.getLastName().toUpperCase().charAt(0)));
				def_pass.append("#");
				String dob = item.getDOB();
				String yy = dob.substring(0,4);
				String mm = dob.substring(5,7);
				String dd = dob.substring(8,10);
				def_pass.append(mm);
				def_pass.append(dd);
				def_pass.append(yy);				
				def_pass.append(item.getAlternateId());
				user.put("password", def_pass.toString());
				user.put("password_confirmation", def_pass.toString());
				
				
				user.put("department", item.getDepartment());
				user.put("title", item.getTitle());
				user.put("status", 4);
				List<Employee> manager = getUserDetailsList(accessToken,"?custom_attributes.employeeID="+item.getManager());
				if(manager.size()>0){
					user.put("manager_user_id",manager.get(0).getOneLoginId());
				}

				customAttribute.put("employeeID", item.getEmployeeId());
				customAttribute.put("companyCode",item.getCompanyCode());
				customAttribute.put("shortLocCode",item.getShortLocCode());
				customAttribute.put("coAlign",item.getCoAlign());
				customAttribute.put("userVerify",item.getUserVerify());
				customAttribute.put("homeCity",item.getHomeCity());
				customAttribute.put("homeState",item.getHomeState());
				customAttribute.put("homeZip",item.getHomeZip());				
				customAttribute.put("regionCode",item.getRegionCode());
				customAttribute.put("region",item.getRegion());
				customAttribute.put("areaCode",item.getAreaCode());
				customAttribute.put("area",item.getArea());
				customAttribute.put("locationCode",item.getLocationCode());
				customAttribute.put("location",item.getLocation());
				customAttribute.put("departmentCode",item.getDepartmentCode());
				customAttribute.put("source", Constants.source);    //source
				customAttribute.put("titleCode", item.getTitleCode());
				customAttribute.put("managerEmpID", item.getSupervisorEmpNumber());
				customAttribute.put("workInState", item.getWorkInState());
				customAttribute.put("managerLevel", item.getManagerLevel());
				customAttribute.put("alternateEmail", item.getAlternateEmail());

				user.put("custom_attributes", customAttribute);
				logger.info("Creating User:"+item.getEmployeeId());

				HttpPost postRequest = new HttpPost(Constants.oneloginUser_URL);
				postRequest.setHeader("Authorization", authHeader);
				postRequest.setEntity(new StringEntity(user.toString(),ContentType.APPLICATION_JSON));
				try {
					reponse = client.execute(postRequest);    //create user request
					logger.info("Create User Response:"+EntityUtils.toString(reponse.getEntity())) ;
				} catch (ClientProtocolException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}
			}
		}*/
	}
	private List<Employee> getUserDetailsList(String accessToken, String path){
		//logger.info("getting :"+path);
		List<Employee> oneLoginUserListByEmpId = new ArrayList<Employee>();
		CloseableHttpResponse reponse = null;
		HttpGet getRequest;

		CloseableHttpClient client = HttpClientBuilder.create().build();
		getRequest = new HttpGet(Constants.oneloginUser_URL+path);
		String authHeader = "Bearer " + new String(accessToken);
		getRequest.setHeader("Authorization", authHeader);

		try {
			reponse = client.execute(getRequest);
			String content = EntityUtils.toString(reponse.getEntity());
			//	logger.info("content: "+content);
			Object obj = new JSONTokener(content).nextValue();

			if(obj instanceof JSONArray && ((JSONArray) obj).length()>0){
				JSONArray respArray = (JSONArray)obj;
				for(int i=0; i<respArray.length(); i++){
					JSONObject userJson = (JSONObject)((JSONArray) obj).getJSONObject(i);
					Employee oneLoginUser = getUserDetails(accessToken, userJson.get("id").toString());
					Employee user = new Employee();

					user.setUserName(oneLoginUser.getUserName());
					user.setFirstName(oneLoginUser.getFirstName());
					user.setLastName(oneLoginUser.getLastName());
					user.setOneLoginId(oneLoginUser.getOneLoginId());
					user.setEmployeeStatus(oneLoginUser.getEmployeeStatus());
					user.setTitle(oneLoginUser.getTitle());
					user.setEmail(oneLoginUser.getEmail());
					user.setDepartment(oneLoginUser.getDepartment());
					user.setManager(oneLoginUser.getManager());

					user.setArea(oneLoginUser.getArea());
					user.setAreaCode(oneLoginUser.getAreaCode());
					user.setEmployeeId(oneLoginUser.getEmployeeId());
					user.setLocation(oneLoginUser.getLocation());
					user.setShortLocCode(oneLoginUser.getShortLocCode());
					user.setCoAlign(oneLoginUser.getCoAlign());
					user.setUserVerify(oneLoginUser.getUserVerify());
					user.setHomeCity(oneLoginUser.getHomeCity());
					user.setHomeState(oneLoginUser.getHomeState());
					user.setHomeZip(oneLoginUser.getHomeZip());
					user.setDepartmentCode(oneLoginUser.getDepartmentCode());
					user.setRegionCode(oneLoginUser.getRegionCode());
					user.setRegion(oneLoginUser.getRegion());
					user.setLocationCode(oneLoginUser.getLocationCode());
					user.setTitleCode(oneLoginUser.getTitleCode());
					user.setCompanyCode(oneLoginUser.getCompanyCode());
					user.setSource(oneLoginUser.getSource());
					user.setSupervisorEmpNumber(oneLoginUser.getSupervisorEmpNumber());
					user.setWorkInState(oneLoginUser.getWorkInState());
					user.setManagerLevel(oneLoginUser.getManagerLevel());
					user.setAlternateEmail(oneLoginUser.getAlternateEmail());
					oneLoginUserListByEmpId.add(user);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		try { client.close(); reponse.close();} 
		catch (IOException e) {	e.printStackTrace();}

		return oneLoginUserListByEmpId;
	}
	private void updateUser(String userJson, String oneLoginUserId, String authHeader){
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse reponse = null;
		HttpPut putRequest = new HttpPut(Constants.oneloginUser_URL+"/"+oneLoginUserId);
		putRequest.setHeader("Authorization", authHeader);
		putRequest.setEntity(new StringEntity(userJson.toString(),ContentType.APPLICATION_JSON));
		try {
			reponse = client.execute(putRequest);
			logger.info("Update Response:"+EntityUtils.toString(reponse.getEntity()));
		} 
		catch (ClientProtocolException e) { e.printStackTrace();} 
		catch (IOException e) {	e.printStackTrace();}
	}
	private Employee getUserDetails(String accessToken, String oneloginId){

		CloseableHttpResponse reponse = null;
		HttpGet getRequest;

		Employee oneLoginUser = null;
		CloseableHttpClient client = HttpClientBuilder.create().build();
		getRequest = new HttpGet(Constants.oneloginUser_URL+"/"+oneloginId);
		String authHeader = "Bearer " + new String(accessToken);
		getRequest.setHeader("Authorization", authHeader);

		try {
			reponse = client.execute(getRequest);
			String content = EntityUtils.toString(reponse.getEntity());
			logger.info("UserDetails id:"+oneloginId+" content: "+content);
			Object obj = new JSONTokener(content).nextValue();
			JSONObject userDetailJson = null;

			userDetailJson = new JSONObject(content);
			if(userDetailJson !=null){
				if(!userDetailJson.has("statusCode")){
					oneLoginUser = new Employee();
					oneLoginUser.setUserName((userDetailJson.get("username")!=JSONObject.NULL)?userDetailJson.get("username").toString():"");
					oneLoginUser.setFirstName((userDetailJson.get("firstname")!=JSONObject.NULL)?userDetailJson.get("firstname").toString():"");
					oneLoginUser.setLastName((userDetailJson.get("lastname")!=JSONObject.NULL)?userDetailJson.get("lastname").toString():"");
					oneLoginUser.setOneLoginId(userDetailJson.get("id").toString());
					oneLoginUser.setEmployeeStatus(userDetailJson.get("status").toString());
					if(userDetailJson.has("title")){
						oneLoginUser.setTitle((userDetailJson.get("title")!=JSONObject.NULL)?userDetailJson.get("title").toString():"");
					}
					oneLoginUser.setEmail((userDetailJson.get("email")!=JSONObject.NULL)?userDetailJson.get("email").toString():"");
					if(userDetailJson.has("department")){
						oneLoginUser.setDepartment((userDetailJson.get("department")!=JSONObject.NULL)?userDetailJson.get("department").toString():"");
					}
					if(userDetailJson.has("manager_user_id")){
						oneLoginUser.setManager((userDetailJson.get("manager_user_id")!=JSONObject.NULL)?userDetailJson.get("manager_user_id").toString():"");
					}

					if(userDetailJson.has("custom_attributes")){
						JSONObject custom_attributes = userDetailJson.getJSONObject("custom_attributes");  
						oneLoginUser.setArea((custom_attributes.get("area")!=JSONObject.NULL)?custom_attributes.get("area").toString():"");
						oneLoginUser.setAreaCode((custom_attributes.get("areaCode")!=JSONObject.NULL)?custom_attributes.get("areaCode").toString():"");
						oneLoginUser.setEmployeeId((custom_attributes.get("employeeID")!=JSONObject.NULL)?custom_attributes.get("employeeID").toString():"");
						oneLoginUser.setLocation((custom_attributes.get("location")!=JSONObject.NULL)?custom_attributes.get("location").toString():"");
						oneLoginUser.setShortLocCode((custom_attributes.get("shortLocCode")!=JSONObject.NULL)?custom_attributes.get("shortLocCode").toString():"");
						oneLoginUser.setCoAlign((custom_attributes.get("coAlign")!=JSONObject.NULL)?custom_attributes.get("coAlign").toString():"");
						oneLoginUser.setUserVerify((custom_attributes.get("userVerify")!=JSONObject.NULL)?custom_attributes.get("userVerify").toString():"");
						oneLoginUser.setHomeCity((custom_attributes.get("homeCity")!=JSONObject.NULL)?custom_attributes.get("homeCity").toString():"");
						oneLoginUser.setHomeState((custom_attributes.get("homeState")!=JSONObject.NULL)?custom_attributes.get("homeState").toString():"");
						oneLoginUser.setHomeZip((custom_attributes.get("homeZip")!=JSONObject.NULL)?custom_attributes.get("homeZip").toString():"");
						oneLoginUser.setDepartmentCode((custom_attributes.get("departmentCode")!=JSONObject.NULL)?custom_attributes.get("departmentCode").toString():"");
						oneLoginUser.setRegionCode((custom_attributes.get("regionCode")!=JSONObject.NULL)?custom_attributes.get("regionCode").toString():"");
						oneLoginUser.setRegion((custom_attributes.get("region")!=JSONObject.NULL)?custom_attributes.get("region").toString():"");
						oneLoginUser.setLocationCode((custom_attributes.get("locationCode")!=JSONObject.NULL)?custom_attributes.get("locationCode").toString():"");
						oneLoginUser.setLocation((custom_attributes.get("location")!=JSONObject.NULL)?custom_attributes.get("location").toString():"");
						oneLoginUser.setTitleCode((custom_attributes.get("titleCode")!=JSONObject.NULL)?custom_attributes.get("titleCode").toString():"");
						oneLoginUser.setCompanyCode((custom_attributes.get("companyCode")!=JSONObject.NULL)?custom_attributes.get("companyCode").toString():"");
						oneLoginUser.setSource((custom_attributes.get("source")!=JSONObject.NULL)?custom_attributes.get("source").toString():"");
						oneLoginUser.setSupervisorEmpNumber((custom_attributes.get("managerEmpID")!=JSONObject.NULL)?custom_attributes.get("managerEmpID").toString():"");
						oneLoginUser.setWorkInState((custom_attributes.get("workInState")!=JSONObject.NULL)?custom_attributes.get("workInState").toString():"");
						oneLoginUser.setManagerLevel((custom_attributes.get("managerLevel")!=JSONObject.NULL)?custom_attributes.get("managerLevel").toString():"");
						oneLoginUser.setAlternateEmail((custom_attributes.get("alternateEmail")!=JSONObject.NULL)?custom_attributes.get("alternateEmail").toString():"");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		try { client.close(); reponse.close();} 
		catch (IOException e) {	e.printStackTrace();}

		return oneLoginUser;
	}
	private void deleteUser(String oneLoginId, String authHeader){

		HttpDelete deleteRequest = new HttpDelete(Constants.oneloginUser_URL+"/"+oneLoginId);
		deleteRequest.setHeader("Authorization", authHeader);
		CloseableHttpClient client = HttpClientBuilder.create().build();

		try {
			CloseableHttpResponse deleteResponse = client.execute(deleteRequest);
			if(deleteResponse.getEntity()!=null){
				logger.info("Delete User Response:"+EntityUtils.toString(deleteResponse.getEntity())) ;
			}
		} 
		catch (ClientProtocolException e) {e.printStackTrace();}
		catch (IOException e) {e.printStackTrace();}
	
	}
}

class DisableUserTask implements Runnable{
	private static final Logger logger = LogManager.getLogger(DisableUserTask.class);

	String accessToken;
	String oneLoginUserId;
	public DisableUserTask(String accessToken, String oneLoginUserId){
		this.accessToken = accessToken;
		this.oneLoginUserId = oneLoginUserId;

	}
	@Override
	public void run() {
		String authHeader = "Bearer " + new String(this.accessToken);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse reponse = null;
		HttpPut putRequest = new HttpPut(Constants.oneloginUser_URL+"/"+oneLoginUserId);
		putRequest.setHeader("Authorization", authHeader);
		putRequest.setEntity(new StringEntity("{\"status\":0}",ContentType.APPLICATION_JSON));

		try {
			reponse = client.execute(putRequest);
			logger.info(EntityUtils.toString(reponse.getEntity()));
		} 
		catch (ClientProtocolException e) { e.printStackTrace();} 
		catch (IOException e) {	e.printStackTrace();}


	}

}