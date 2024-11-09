package org.cainiao.process.service.impl;

import lombok.RequiredArgsConstructor;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.service.ProcessTaskService;
import org.flowable.engine.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
@RequiredArgsConstructor
public class ProcessTaskServiceImpl implements ProcessTaskService {

    private final TaskService taskService;

    @Override
    public void reassignOwnTask(String userName, ReassignTaskRequest reassignTaskRequest) {
        String toUserName = reassignTaskRequest.getToUserName();
        if (userName.equals(toUserName)) {
            return;
        }
        String taskId = reassignTaskRequest.getTaskId();
        String assignee = taskService.createTaskQuery().taskId(taskId).singleResult().getAssignee();
        if (!StringUtils.hasText(assignee) || !assignee.equals(userName)) {
            throw new BusinessException("只能对分配给自己的任务进行改派!");
        }
        taskService.setAssignee(taskId, toUserName);
    }
}
