package org.cainiao.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessTaskResponse;

public interface ProcessTaskService {

    void reassignOwnTask(String userName, ReassignTaskRequest reassignTaskRequest);

    IPage<ProcessTaskResponse> tasks(String userName, String processInstanceId,
                                     long current, int size, String searchKey);

    ProcessTaskResponse task(String taskId);
}
