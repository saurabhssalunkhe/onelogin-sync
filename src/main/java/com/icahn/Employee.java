package com.icahn;

import java.time.LocalDate;

public class Employee {
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		if(employeeId!=null){this.employeeId = employeeId.trim();}
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		if(companyCode!=null){this.companyCode = companyCode.trim();}
	}
	public String getShortLocCode() {
		return shortLocCode;
	}
	public void setShortLocCode(String shortLocCode) {
		if(shortLocCode!=null){this.shortLocCode = shortLocCode.trim();}
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		if(regionCode!=null){this.regionCode = regionCode.trim();}
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		if(region!=null){this.region = region.trim();}
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		if(areaCode!=null){this.areaCode = areaCode.trim();}
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		if(area!=null){this.area = area.trim();}
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		if(locationCode!=null){this.locationCode = locationCode.trim();}
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		if(location!=null){this.location = location.trim();}
	}
	public String getDepartmentCode() {
		return departmentCode;
	}
	public void setDepartmentCode(String departmentCode) {
		if(departmentCode!=null){this.departmentCode = departmentCode.trim();}
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		if(department!=null){this.department = department.trim();}
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		if(firstName!=null){this.firstName = firstName.trim();}
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		if(lastName!=null){this.lastName = lastName.trim();}
	}
	public String getTitleCode() {
		return titleCode;
	}
	public void setTitleCode(String titleCode) {
		if(titleCode!=null){this.titleCode = titleCode.trim();}
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		if(title!=null){this.title = title.trim();}
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		if(manager!=null){this.manager = manager.trim();}
	}
	public String getDefaultPassword() {
		return defaultPassword;
	}
	public void setDefaultPassword(String defaultPassword) {
		if(defaultPassword!=null){this.defaultPassword = defaultPassword.trim();}
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		if(email!=null){this.email = email.trim();}
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getOneLoginId() {
		return oneLoginId;
	}
	public void setOneLoginId(String oneLoginId) {
		this.oneLoginId = oneLoginId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName.trim();
	}
	public LocalDate getTerminationDate() {
		return terminationDate;
	}
	public void setTerminationDate(LocalDate terminationDate) {
		this.terminationDate = terminationDate;
	}
	public String getEmployeeStatus() {
		return employeeStatus;
	}
	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}
	
	public String getSupervisorEmpNumber() {
		return supervisorEmpNumber;
	}
	public void setSupervisorEmpNumber(String supervisorEmpNumber) {
		this.supervisorEmpNumber = supervisorEmpNumber;
	}

	public String getWorkInState() {
		return workInState;
	}
	public void setWorkInState(String workInState) {
		this.workInState = workInState;
	}

	public String getManagerLevel() {
		return managerLevel;
	}
	public void setManagerLevel(String managerLevel) {
		this.managerLevel = managerLevel;
	}

	public String getAlternateEmail() {
		return alternateEmail;
	}
	public void setAlternateEmail(String alternateEmail) {
		if(alternateEmail!=null){this.alternateEmail = alternateEmail.trim();}
	}

	public String getCoAlign() {
		return coAlign;
	}
	public void setCoAlign(String coAlign) {
		if(coAlign!=null) {
			coAlign = coAlign.trim();
		}
		this.coAlign = coAlign;
	}
	
	public String getHomeCity() {
		return homeCity;
	}
	public void setHomeCity(String homeCity) {
		if(homeCity!=null) {
			homeCity = homeCity.trim();
		}
		this.homeCity = homeCity;
	}
	
	public String getHomeZip() {
		return homeZip;
	}
	public void setHomeZip(String homeZip) {
		if(homeZip!=null) {
			homeZip = homeZip.trim();
		}
		this.homeZip = homeZip;
	}

	public String getUserVerify() {
		return userVerify;
	}
	public void setUserVerify(String userVerify) {
		this.userVerify = userVerify;
	}
	
	public String getHomeState() {
		return homeState;
	}
	public void setHomeState(String homeState) {
		this.homeState = homeState;
	}

	private String employeeId="";
	private String companyCode="";
	private String shortLocCode="";
	private String regionCode="";
	private String region="";
	private String areaCode="";
	private String area="";
	private String locationCode="";
	private String location="";
	private String departmentCode="";
	private String department="";
	private String firstName="";
	private String lastName="";
	private String titleCode="";
	private String title="";
	private String manager="";
	private String defaultPassword="";
	private String email="";
	private String source="";
	private String oneLoginId="";
	private String userName="";
	private String employeeStatus="";
	private LocalDate terminationDate;
	private String supervisorEmpNumber="";
	private String workInState="";
	private String managerLevel="";
	private String alternateEmail="";
	private String coAlign="";
	private String userVerify="";
	private String homeCity="";
	private String homeState="";
	private String homeZip="";
	private String alternateId="";
	private String DOB = "";
	
	public String getDOB() {
		return DOB;
	}
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	public String getAlternateId() {
		return alternateId;
	}
	public void setAlternateId(String alternateId) {
		this.alternateId = alternateId;
	}


//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		Employee employee = (Employee) o;
//		return employeeId.equals(employee.employeeId) &&
//				this.area.equals(employee.area) &&
//				this.areaCode.equals(employee.areaCode) &&
//				this.companyCode.equals(employee.companyCode) &&
//				this.department.equals(employee.department) &&
//				this.departmentCode.equals(employee.departmentCode) &&
//				this.shortLocCode.equals(employee.shortLocCode) &&
//				this.title.equals(employee.title) &&
//				this.titleCode.equals(employee.titleCode) &&
//				this.location.equals(employee.location) &&
//				this.locationCode.equals(employee.locationCode) &&
//				this.region.equals(employee.region) &&
//				this.regionCode.equals(employee.regionCode) &&
//				this.manager.equals(employee.manager);
//		
//	}
//
//	@Override
//	public int hashCode() {
//		int hash = 7;
//		hash = 31 * hash + Integer.parseInt(employeeId);
//		hash = 31 * hash + (firstName == null ? 0 : firstName.hashCode());
//		return hash;
//	}
}
