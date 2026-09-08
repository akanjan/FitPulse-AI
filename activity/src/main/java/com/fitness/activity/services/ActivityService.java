package com.fitness.activity.services;

import com.fitness.activity.dto.ActivityRequest;
import com.fitness.activity.dto.ActivityResponse;

public interface ActivityService {
    ActivityResponse trackActivity(ActivityRequest request);
}
