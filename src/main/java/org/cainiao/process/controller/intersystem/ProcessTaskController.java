package org.cainiao.process.controller.intersystem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.request.CompleteTaskRequest;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.dto.response.ProcessActivityResponse;
import org.cainiao.process.dto.response.ProcessTaskResponse;
import org.cainiao.process.service.ProcessTaskService;
import org.springframework.web.bind.annotation.*;

import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE;
import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("inter-system")
@Tag(name = "ProcessTaskController", description = "流程任务管理")
@RequiredArgsConstructor
public class ProcessTaskController {

    private final ProcessTaskService processTaskService;

    @PostMapping("reassign/own-task/{taskId}")
    @Operation(summary = "改派自己的任务")
    public void reassignOwnTask(
        @Parameter(description = "流程任务 ID", required = true) @PathVariable String taskId,
        @Parameter(description = "改派自己的任务的请求参数") @RequestBody ReassignTaskRequest reassignTaskRequest) {

        // TODO 从 Header 中获取调用者的用户名
        processTaskService.reassignOwnTask(null, taskId, reassignTaskRequest);
    }

    @GetMapping("process-instance/{processInstanceId}/tasks")
    @Operation(summary = "分页模糊搜索用户的流程任务")
    public IPage<ProcessTaskResponse> tasks(
        @Parameter(description = "流程实例 ID", required = true) @PathVariable String processInstanceId,
        @Parameter(description = "页码") @RequestParam(required = false, defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String key) {

        // TODO 从 Header 中获取调用者的用户名
        return processTaskService.tasks(null, processInstanceId, current, size, key);
    }

    @GetMapping("task/{taskId}")
    @Operation(summary = "流程任务详情")
    public ProcessTaskResponse task(
        @Parameter(description = "流程任务 ID", required = true) @PathVariable String taskId) {

        return processTaskService.task(taskId);
    }

    @GetMapping("process-instance/{processInstanceId}/start-event/{elementId}")
    @Operation(summary = "开始事件")
    public ProcessActivityResponse startEventDetail(
        @Parameter(description = "流程实例 ID", required = true) @PathVariable String processInstanceId,
        @Parameter(description = "流程元素 ID", required = true) @PathVariable String elementId) {

        return processTaskService.startEventActivity(processInstanceId, elementId);
    }

    @GetMapping("process-instance/{processInstanceId}/task/{elementId}/history-records")
    @Operation(summary = "流程任务的事件列表，当一个任务被执行多次时会形成事件列表")
    public IPage<ProcessActivityResponse> taskActivities(
        @Parameter(description = "页码") @RequestParam(required = false, defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
        @Parameter(description = "流程实例ID", required = true) @PathVariable String processInstanceId,
        @Parameter(description = "流程元素ID", required = true) @PathVariable String elementId) {

        return processTaskService.taskActivities(processInstanceId, elementId, current, size);
    }

    @GetMapping("process-instance/{processInstanceId}/activities")
    @Operation(summary = "流程实例的事件列表")
    public IPage<ProcessActivityResponse> processInstanceActivities(
        @Parameter(description = "页码") @RequestParam(required = false, defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
        @Parameter(description = "流程实例 ID", required = true) @PathVariable String processInstanceId) {

        return processTaskService.processInstanceActivities(processInstanceId, current, size);
    }

    @PostMapping("complete/own-task/{taskId}")
    @Operation(summary = "完成任务")
    public void completeTask(
        @Parameter(description = "流程任务 ID", required = true) @PathVariable String taskId,
        @Parameter(description = "流程任务表单信息") @RequestBody CompleteTaskRequest completeTaskRequest) {

        // TODO 从 Header 中获取调用者的用户名
        processTaskService.completeTask(null, taskId,
            completeTaskRequest.getLocalVariables(), completeTaskRequest.getProcessVariables());
    }
}
