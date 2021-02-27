package com.test.mytel.customer.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.test.mytel.customer.dto.CustomerDTO;
import com.test.mytel.customer.dto.LoginDTO;
import com.test.mytel.customer.dto.PlanDTO;
import com.test.mytel.customer.service.CustomerService;

@RestController
public class CustomerController {

	Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	CustomerService custService;
	
	@Autowired
	RestTemplate template;
	
	@Autowired
	private DiscoveryClient client;

	
	// Create a new customer
	@PostMapping(value = "/customers",  consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer "+ custDTO);
		custService.createCustomer(custDTO);
	}

	// Login
	@PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for customer "+loginDTO.getPhoneNo()+" with password "+loginDTO.getPassword());
		return custService.login(loginDTO);
	}

	// Fetches full profile of a specific customer
	@GetMapping(value = "/customers/{phoneNo}",  produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {
		
		logger.info("Profile request for customer " +phoneNo);
		CustomerDTO custDTO=custService.getCustomerProfile(phoneNo);
		List<ServiceInstance> listOfPlanInstances = client.getInstances("PlanMS");
		String planUri=null;
	    if (listOfPlanInstances != null && !listOfPlanInstances.isEmpty() ) {
	        planUri=listOfPlanInstances.get(0).getUri().toString();
	    }
		PlanDTO planDTO=template.getForObject(planUri+"/plans/"+custDTO.getCurrentPlan().getPlanId(), PlanDTO.class);
		custDTO.setCurrentPlan(planDTO);
		
		List<ServiceInstance> listOfFriendInstances = client.getInstances("FriendFamilyMS");
		String friendUri = null;
		if (listOfFriendInstances != null && !listOfFriendInstances.isEmpty()) {
			friendUri = listOfFriendInstances.get(0).getUri().toString();
		}		
		@SuppressWarnings("unchecked")
		List<Long> friends=template.getForObject(friendUri+"/customers/"+phoneNo+"/friends", List.class);
		custDTO.setFriendAndFamily(friends);
		
		return custDTO;
	}



}
