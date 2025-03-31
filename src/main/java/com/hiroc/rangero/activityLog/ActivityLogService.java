package com.hiroc.rangero.activityLog;


import com.hiroc.rangero.activityLog.dto.ActivityLogDTO;
import com.hiroc.rangero.exception.BadRequestException;
import com.hiroc.rangero.exception.UnauthorisedException;
import com.hiroc.rangero.projectMember.ProjectMember;
import com.hiroc.rangero.projectMember.ProjectMemberService;
import com.hiroc.rangero.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogMapper activityLogMapper;
    private final ProjectMemberService projectMemberService;



    //Shouldn't have to use this
//    public void createLogSync(@Valid ActivityLogRequest request){
//        //TODO change this if activity logger expands beyond just task logging
//
//        ActivityLog activityLog = activityLogMapper.toEntity(request);
//
//        //If one is null, the other has to be null
//        if (request.getPreviousTaskStatus()==null)activityLog.setCurrentTaskStatus(null);
//        if (request.getCurrentTaskStatus()==null) activityLog.setPreviousTaskStatus(null);
//
//        activityLogRepository.save(activityLog);
//
//    }

//    //TODO delete if not needed
//    @EventListener
//    public void createLogSync(ActivityLogEvent event){
//        ActivityLogRequest request = event.getActivity();
//        ActivityLog activityLog = activityLogMapper.toEntity(request);
//        if (request.getPreviousTaskStatus()==null) activityLog.setCurrentTaskStatus(null);
//        if (request.getCurrentTaskStatus()==null) activityLog.setPreviousTaskStatus(null);
//        activityLogRepository.save(activityLog);
//
//    }

    public Set<ActivityLogDTO> getProjectActivities(User accessor, long projectId){
        //Project activities can only be requested by a member of the project
        ProjectMember member = projectMemberService.findByUserEmailAndProjectId(accessor.getEmail(),projectId)
                .orElseThrow(UnauthorisedException::new);

        Set<ActivityLog> logs = activityLogRepository.findByProjectId(projectId);
        return logs.stream().map(activityLogMapper::toDTO).collect(Collectors.toSet());

    }

    //TODO read up more on @TransactionalEventListener
//    @EventListener
    @TransactionalEventListener //BY default the phase is : AFTER_COMMIT
    @Async
    public void createLogAsync(ActivityLogEvent event){
        ActivityLogRequest request = event.getActivity();
        ActivityLog activityLog = activityLogMapper.requestToEntity(request);
        if (request.getPreviousTaskStatus()==null) activityLog.setCurrentTaskStatus(null);
        if (request.getCurrentTaskStatus()==null) activityLog.setPreviousTaskStatus(null);
        activityLogRepository.save(activityLog);

    }
}
