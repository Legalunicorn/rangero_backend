package com.hiroc.rangero.task.helper;

import com.hiroc.rangero.task.Task;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CycleDetection {
    public static void detectCycle(Map<Long,Set<Long>> graph, Long startId){
        detectCycleHelper(graph,startId,new HashSet<Long>());
    }

    private static void detectCycleHelper(Map<Long, Set<Long>> graph, Long currId,Set<Long> seen){
        if (seen.contains(currId)){
            throw new RuntimeException("Linking of task dependency has failed. Task of Id:"+currId+" has caused a circular dependency");
        }
        seen.add(currId);

        for (Long dependencyId: graph.get(currId)){
            detectCycleHelper(graph,dependencyId,seen);
        }
    }

}
