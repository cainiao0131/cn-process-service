package org.cainiao.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.cainiao.common.exception.BusinessException;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessTaskResponse;
import org.cainiao.process.service.ProcessTaskService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.cainiao.process.util.TimeUtil.SIMPLE_DATE_FORMAT;

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

    @Override
    public IPage<ProcessTaskResponse> tasks(String userName, String processInstanceId,
                                            long current, int size, String searchKey) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(userName);
        if (StringUtils.hasText(processInstanceId)) {
            taskQuery.processInstanceId(processInstanceId);
        }
        if (StringUtils.hasText(searchKey)) {
            searchKey = String.format("%%%s%%", searchKey);
            taskQuery = taskQuery.or().taskNameLike(searchKey).taskDescriptionLike(searchKey).endOr();
        }
        long count = taskQuery.count();
        List<Task> tasks = taskQuery.orderByTaskCreateTime().desc().listPage((int) (current - 1) * size, size);
        List<ProcessTaskResponse> taskList = tasks.stream()
            .map(task -> ProcessTaskResponse.from(task, SIMPLE_DATE_FORMAT)).toList();
        IPage<ProcessTaskResponse> page = new Page<>(current, size);
        page.setRecords(taskList);
        page.setTotal(count);
        return page;
    }
}
