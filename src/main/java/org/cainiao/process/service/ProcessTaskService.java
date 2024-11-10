package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessActivityResponse;
import org.cainiao.process.dto.response.ProcessTaskResponse;

import java.util.Map;

public interface ProcessTaskService {

    ProcessActivityResponse startEventActivity(String processInstanceId, String elementId);

    void reassignOwnTask(String userName, String taskId, ReassignTaskRequest reassignTaskRequest);

    IPage<ProcessTaskResponse> tasks(String userName, String processInstanceId,
                                     long current, int size, String searchKey);

    ProcessTaskResponse task(String taskId);

    IPage<ProcessActivityResponse> processInstanceActivities(String processInstanceId, long current, int size);

    IPage<ProcessActivityResponse> taskActivities(String processInstanceId, String elementId, long current, int size);

    void completeTask(String userName, String taskId,
                      Map<String, Object> localVariables, Map<String, Object> processVariables);
}
