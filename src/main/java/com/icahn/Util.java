package com.icahn;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;

public class Util {
	private static final Logger logger = LogManager.getLogger(Util.class);

	protected List<Employee> getDbUserData(int userId,boolean weeklyProcess){
		Connection con=null;
		Statement stmt;
		ResultSet rs;
		List<Employee> associateList = new ArrayList<Employee>();
		Config config = Config.getInstance();
		try{
			Map<String, String> dbDetails = getDbdetails();
			//String weeklyUpdateDay = ((System.getenv("weeklyupdate")!=null)?System.getenv("weeklyupdate"):config.getValue("weeklyupdate"));
			String query;
			
			Boolean weeklyEnabled = Boolean.valueOf(config.getValue("weeklyEnabled"));
			
			if(weeklyEnabled) {
				if(weeklyProcess){
					query = "select * from UDBPROD.ONELOGIN_ASSOCIATE WHERE EMPLOYEE_NUM>"+userId+" ORDER BY EMPLOYEE_NUM LIMIT "+config.getValue("limit");
				}else{
					query = "select * from udbdev.ONELOGIN_ASSOCIATE WHERE EMPLOYEE_NUM>"+userId+" ORDER BY EMPLOYEE_NUM LIMIT "+config.getValue("limit");
					//query = "select * from UDBPROD.ONELOGIN_ASSOCIATE_MODS WHERE EMPLOYEE_NUM>"+userId+" ORDER BY EMPLOYEE_NUM LIMIT "+config.getValue("limit");	
				}
			}
			else {
				query = "select * from udbdev.ONELOGIN_ASSOCIATE WHERE EMPLOYEE_NUM>"+userId+" ORDER BY EMPLOYEE_NUM LIMIT "+config.getValue("limit");
			}
			logger.info("get User Query:"+query);

			logger.info("Connecting to Database....");
			Class.forName(Constants.driverName);
			con = DriverManager.getConnection (dbDetails.get("url"),dbDetails.get("username"), dbDetails.get("password"));
			stmt = con.createStatement();
			rs = stmt.executeQuery(query); 
			Employee associateUser;
			logger.info("Connected successfully to the Database.");
			
			while (rs.next()) {
				associateUser = new Employee();
				associateUser.setEmployeeId(rs.getString("EMPLOYEE_NUM"));
				associateUser.setUserName(rs.getString("EMPLOYEE_NUM"));
				associateUser.setCompanyCode(rs.getString("COMPANY_CD"));
				associateUser.setDefaultPassword(rs.getString("DEF_PASS"));
				associateUser.setShortLocCode(rs.getString("LOCATION_ID_SHORT"));
				associateUser.setCoAlign(rs.getString("TIME_DIVISION"));
				//associateUser.setUserVerify(rs.getString("DEF_PASS_MASK"));
				associateUser.setDepartmentCode(rs.getString("DEPT_NBR"));
				associateUser.setHomeCity(rs.getString("ADDRESS_CITY"));
				associateUser.setHomeState(rs.getString("ADDRESS_STATE_CD"));
				associateUser.setHomeZip(rs.getString("ADDRESS_ZIP_CD"));
				associateUser.setRegionCode(rs.getString("ORG_DIVISION_CD"));
				associateUser.setRegion(rs.getString("ORG_DIVISION_DESC"));
				associateUser.setAreaCode(rs.getString("ORG_AREA_CD"));
				associateUser.setArea(rs.getString("ORG_AREA_DESC"));
				associateUser.setLocationCode(rs.getString("ORG_LOCATION_CD"));
				associateUser.setLocation(rs.getString("ORG_LOCATION_DESC"));
				associateUser.setDepartment(rs.getString("ORG_DEPT_DESC"));
				associateUser.setFirstName(rs.getString("FIRST_NAME"));
				associateUser.setLastName(rs.getString("LAST_NAME"));
				associateUser.setTitleCode(rs.getString("TITLE_CD"));
				associateUser.setTitle(rs.getString("TITLE_DESC"));
				associateUser.setManager(rs.getString("SUPERVISOR_EMPLOYEE_NUM"));
				String status = rs.getString("EMPLOYEE_STATUS_CD1");
				associateUser.setEmployeeStatus(rs.getString("EMPLOYEE_STATUS_CD1"));
				associateUser.setSupervisorEmpNumber(rs.getString("SUPERVISOR_EMPLOYEE_NUM"));
				associateUser.setWorkInState(rs.getString("WORK_IN_STATE_CD"));
				associateUser.setManagerLevel(rs.getString("MANAGER_LEVEL"));
				associateUser.setAlternateEmail(rs.getString("ALTERNATE_EMAIL"));
				int alternateId = rs.getInt("ALTERNATE_ID");
				associateUser.setAlternateId(String.valueOf(alternateId));
				Date dob = rs.getDate("BIRTH_DT");
				//System.out.println(dob.toString());
				associateUser.setDOB(dob.toString());
				
				if(status.equals("T")){
					associateUser.setTerminationDate(LocalDate.parse(rs.getString("TERMINATION_DT")));
				}

				associateList.add(associateUser);
			}
			System.out.println("associate list count:"+associateList.size());
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return associateList;
		
	}
	protected String getSecretsKey(String endpoint, String region, String secretName){
		GetSecretValueResult getSecretValueResult = null;
		String secret = null;

		try {
			if(endpoint!=null){
				System.out.println("End point: "+endpoint);
				AwsClientBuilder.EndpointConfiguration config = new AwsClientBuilder.EndpointConfiguration(endpoint, region);
				AWSSecretsManagerClientBuilder clientBuilder = AWSSecretsManagerClientBuilder.standard();
				clientBuilder.setEndpointConfiguration(config);
				AWSSecretsManager client = clientBuilder.build();

				GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
				.withSecretId(secretName).withVersionStage("AWSCURRENT");

				getSecretValueResult = client.getSecretValue(getSecretValueRequest);
			}
		} catch(ResourceNotFoundException e) {
			System.out.println("The requested secret " + secretName + " was not found");
		} catch (InvalidRequestException e) {
			System.out.println("The request was invalid due to: " + e.getMessage());
		} catch (InvalidParameterException e) {
			System.out.println("The request had invalid params: " + e.getMessage());
		}catch(Exception e){
			System.out.println("error here");
			e.printStackTrace();
		}

		if(getSecretValueResult == null) {
			return null;
		}
		secret = getSecretValueResult.getSecretString();
		//logger.info("Secret Manager: "+secret);
		return secret;

	}
	protected Map<String,String> getDbdetails(){
		String hostName = null;
		String port;
		String database;
		String username;
		String password;
		Map<String, String> dbDetails = new HashMap<String, String>();
		Config config = Config.getInstance();
		String endpoint = config.getValue("endpoint");
		String region = config.getValue("region");
		String secretName = config.getValue("secretname");
		String secretValue = getSecretsKey(endpoint, region, secretName);
		//logger.info("secretValue:"+secretValue);
		if(secretValue !=null){
			JSONObject secretObj = new JSONObject(secretValue);
			hostName = secretObj.getString("host");
			port = secretObj.getString("port");
			database = secretObj.getString("dbname");
			username = secretObj.getString("username");
			password = secretObj.getString("password");

		}else{
			hostName = config.getValue("host");
			port = config.getValue("port");
			database = config.getValue("database");
			username = config.getValue("dbUser");
			password = config.getValue("dbPassword");
		}
		String url = config.getValue("urlPrefix")+"//"+hostName+":"+port+"/"+database;
		//logger.info("DB url:"+url+" Username"+username+" pass:"+password);

		dbDetails.put("url", url);
		dbDetails.put("username", username);
		dbDetails.put("password", password);

		return dbDetails;

	}
	protected Map<String,String> getOneloginCredentials(){
		String clientId;
		String clientSecret;
		Map<String, String> dbDetails = new HashMap<String, String>();
		Config config = Config.getInstance();
		String endpoint = config.getValue("endpoint");
		String region = config.getValue("region");
		String secretName = "api/onelogin";
		String secretValue = getSecretsKey(endpoint, region, secretName);
		//logger.info("secretValue:"+secretValue);
		if(secretValue !=null){
			//logger.info("Getting onelogin id and secret from secrets manager");
			JSONObject secretObj = new JSONObject(secretValue);
			clientId = secretObj.getString("clientid");
			clientSecret = secretObj.getString("clientsecret");
		}else{
			clientId = config.getValue("clientid");
			clientSecret = config.getValue("clientsecret");
		}
		dbDetails.put("clientid", clientId);
		dbDetails.put("clientsecret", clientSecret);

		return dbDetails;

	}
	protected Integer getMaxUserId(){
		int maxUserId = 0;
		try {
			ResultSet rs;
			String query;
			Config config = Config.getInstance();
			Map<String, String> dbDetails = getDbdetails();
			Class.forName(Constants.driverName);
			Connection con = DriverManager.getConnection (dbDetails.get("url"),dbDetails.get("username"), dbDetails.get("password"));
			Statement stmt = con.createStatement();

			String weeklyUpdateDay = ((System.getenv("weeklyupdate")!=null)?System.getenv("weeklyupdate"):config.getValue("weeklyupdate"));


			if(LocalDate.now().getDayOfWeek().name().equalsIgnoreCase(weeklyUpdateDay)){
				query = "SELECT MAX(EMPLOYEE_NUM) FROM UDBPROD.ONELOGIN_ASSOCIATE";
			}else{
				query = "SELECT MAX(EMPLOYEE_NUM) FROM UDBPROD.ONELOGIN_ASSOCIATE_MODS";
			}

			rs = stmt.executeQuery(query);

			logger.info("Connected successfully to the Database.");
			while (rs.next()) {
				maxUserId = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return maxUserId;

	}
	protected List<String> getDbUserIdList(int userId){
		logger.info("get Database User Id greater than:"+userId);
		Connection con=null;
		Statement stmt;
		ResultSet rs;
		List<String> associateList = new ArrayList<String>();
		Config config = Config.getInstance();
		try{
			Map<String, String> dbDetails = getDbdetails();
			String query = "select EMPLOYEE_NUM from UDBPROD.ONELOGIN_ASSOCIATE ORDER BY EMPLOYEE_NUM";
			logger.info("disable user query:"+query);

			logger.info("Connecting to Database....");
			Class.forName(Constants.driverName);
			con = DriverManager.getConnection (dbDetails.get("url"),dbDetails.get("username"), dbDetails.get("password"));
			stmt = con.createStatement();
			rs = stmt.executeQuery(query); 
			logger.info("Connected successfully to the Database.");
			while (rs.next()) {
				associateList.add(rs.getString(1));
			}
			System.out.println("associate list count:"+associateList.size());
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return associateList;

	}
}
