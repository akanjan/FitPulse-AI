package com.fitness.activity.services.impl;

import com.fitness.activity.dto.ActivityRequest;
import com.fitness.activity.dto.ActivityResponse;
import com.fitness.activity.model.Activity;
import com.fitness.activity.repository.ActivityRepository;
import com.fitness.activity.services.ActivityService;
import com.fitness.activity.services.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {
    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String, Activity> kafkaTemplate;

    @Value("${kafka.topic.name}")
    private String topicName;
    @Override
    public ActivityResponse trackActivity(ActivityRequest request) {
        boolean isValidUser = userValidationService.validateUser(request.getUserUID());
        if (!isValidUser) {
            throw new RuntimeException("Invalid User: " + request.getUserUID());
        }
        Activity activity = modelMapper.map(request, Activity.class);
        Activity savedActivity = activityRepository.save(activity);

        try{
            kafkaTemplate.send(topicName, savedActivity.getUserUID(), savedActivity);
        }catch (Exception e){
            e.printStackTrace();
        }

        return modelMapper.map(savedActivity, ActivityResponse.class);
    }
}
