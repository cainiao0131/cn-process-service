package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessActivityResponse;
import org.cainiao.process.dto.response.ProcessTaskResponse;

public interface ProcessTaskService {

    ProcessActivityResponse startEventActivity(String processInstanceId, String elementId);

    void reassignOwnTask(String userName, ReassignTaskRequest reassignTaskRequest);

    IPage<ProcessTaskResponse> tasks(String userName, String processInstanceId,
                                     long current, int size, String searchKey);

    ProcessTaskResponse task(String taskId);

    IPage<ProcessActivityResponse> processInstanceActivities(String processInstanceId, long current, int size);

    IPage<ProcessActivityResponse> taskActivities(String processInstanceId, String elementId, long current, int size);
}
