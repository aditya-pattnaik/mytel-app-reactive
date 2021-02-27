package com.test.mytel.friend.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.mytel.friend.dto.FriendFamilyDTO;
import com.test.mytel.friend.entity.FriendFamily;
import com.test.mytel.friend.repository.FriendFamilyRepository;

@Service
public class FriendFamilyService {
	Log logger = LogFactory.getLog(FriendFamilyService.class);

	@Autowired
	FriendFamilyRepository friendRepo;

	// Create Friend Family
	
	public void saveFriend(Long phoneNo, FriendFamilyDTO friendDTO) {
		logger.info("Creation request for customer "+phoneNo+" with data "+friendDTO);
		friendDTO.setPhoneNo(phoneNo);
		FriendFamily friend = friendDTO.createFriend();
		friendRepo.save(friend);
	}
	
	// Get friend and family phone number list of a given customer
	public List<Long> getSpecificFriends(Long phoneNo){
		logger.info("Friend and family detailsfor customer "+ phoneNo);
		List<Long> friendList= new ArrayList<>();
		List<FriendFamily> friends=friendRepo.getByPhoneNo(phoneNo);
		for (FriendFamily friendFamily : friends) {
			friendList.add(friendFamily.getFriendAndFamily());
		}
		logger.info("The friend list for customer"+phoneNo+" is "+friendList);
		return friendList;
	}

}
