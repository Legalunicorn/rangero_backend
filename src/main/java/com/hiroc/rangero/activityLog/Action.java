package com.hiroc.rangero.activityLog;

/*
should this be a task activity logger or a project activity logger?
It seems like every action is attached to a task


for a project, we need to fetch all the actions though
if we only reference actoins to tasks and not projects
-> project get all task
-> task get all logs

=> unnecessary step to fetch all task?

=> we should just link to the project and the task
 */
public enum Action {
    CREATE_TASK,
    ASSIGN_TASK,
    DELETE_TASK,
    UPDATE_TASK_DETAILS,
    UPDATE_TASK_STATUS,
    UPDATE_TASK_DEPENDENCIES,
    UPDATE_PROJECT,


}
