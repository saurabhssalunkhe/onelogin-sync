package com.icahn;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class Test {
	//private final Employee item = new Employee();
	
	public void gen_def_pass(Employee item) {
		StringBuilder def_pass = new StringBuilder();
		def_pass.append(Character.toString(item.getFirstName().toUpperCase().charAt(0)));
		def_pass.append(Character.toString(item.getLastName().toUpperCase().charAt(0)));
		def_pass.append("#");
		String dob = item.getDOB();
		String yy = dob.substring(0,4);
		String mm = dob.substring(5,7);
		String dd = dob.substring(8,10);
		System.out.println("yy mm dd: "+yy+mm+dd);
		def_pass.append(mm);
		def_pass.append(dd);
		def_pass.append(yy);
		
		
		
		def_pass.append(item.getAlternateId());
		System.out.println("Default password generated: "+def_pass);
	}
		
	public void gen_email(Employee item) {
		
		String companyCode = item.getCompanyCode();
		String emailHost="@autoplusap.com";

		
		
			
		
		
	}
	
	public void test(){
		
		OneloginUserManagement oneloginUsrMgmt = new OneloginUserManagement();
		Util util = new Util();
		JSONObject result = null;
		int maxUserId = util.getMaxUserId();

			
		
		List<Employee> associateUserList =  util.getDbUserData(0,false);
		
		System.out.println(associateUserList);
		
//		for(int i=0; i<2; i++) {
//			gen_def_pass(associateUserList.get(i));
//			gen_email(associateUserList.get(i));
//		}
		
		
		
		
		
		
		if(associateUserList.size()>0){
		                result = oneloginUsrMgmt.processDbUsers(associateUserList,maxUserId,false);

		            }
		}
	
	public static void main(String[] args) {
		Test t = new Test();
		t.test();
	}

}
