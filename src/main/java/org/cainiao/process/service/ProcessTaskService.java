package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessActivity;
import org.cainiao.process.dto.response.ProcessTaskResponse;
import org.cainiao.process.dto.response.WorkflowActivityResponse;

public interface ProcessTaskService {

    WorkflowActivityResponse startEventDetail(String processInstanceId, String elementId);

    void reassignOwnTask(String userName, ReassignTaskRequest reassignTaskRequest);

    IPage<ProcessTaskResponse> tasks(String userName, String processInstanceId,
                                     long current, int size, String searchKey);

    ProcessTaskResponse task(String taskId);

    IPage<ProcessActivity> processInstanceActivities(String processInstanceId, long current, int size);
}
