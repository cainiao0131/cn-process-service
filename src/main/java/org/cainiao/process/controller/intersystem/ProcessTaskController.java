package org.cainiao.process.controller.intersystem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.request.ReassignTaskRequest;
import org.cainiao.process.service.ProcessTaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inter-system")
@Tag(name = "ProcessTaskController", description = "流程任务管理")
@RequiredArgsConstructor
public class ProcessTaskController {

    private final ProcessTaskService processTaskService;

    @PostMapping("reassign/own-task")
    @Operation(summary = "改派自己的任务")
    public void reassignOwnTask(
        @Parameter(description = "改派自己的任务的请求参数") @RequestBody ReassignTaskRequest reassignTaskRequest) {

        // TODO 从 Header 中获取调用者的用户名
        processTaskService.reassignOwnTask(null, reassignTaskRequest);
    }
}
