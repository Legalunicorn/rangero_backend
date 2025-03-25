package com.hiroc.rangero.activityLog;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private ActivityLogRepository activityLogRepository;


    public void createActivityLog(@Valid ActivityLogRequest request){
        //Already validated
        //the only thing we need to ch
        ActivityLog  activityLog = ActivityLog.builder()
                .project(request.getProject())
                .user(request.getUser())
                .task(request.getTask())
                .action(request.getAction())
                .build();
        if (request.getCurrentTaskStatus()!=null && request.getPreviousTaskStatus()!=null){
            //add both
            activityLog.setCurrentTaskStatus(request.getCurrentTaskStatus());
            activityLog.setPreviousTaskStatus(request.getPreviousTaskStatus());
        }
        //save the request to the database
        activityLogRepository.save(activityLog);

    }
}
