package org.cainiao.process.controller.intersystem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.request.StartFlowRequest;
import org.cainiao.process.dto.response.ProcessInstanceDetail;
import org.cainiao.process.dto.response.ProcessInstanceResponse;
import org.cainiao.process.dto.response.ProcessStartEventResponse;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.ProcessService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE;
import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("inter-system")
@Tag(name = "ProcessController", description = "流程管理")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    @PostMapping("system/{systemId}/process-definition")
    @Operation(summary = "为系统添加或编辑流程定义元数据")
    public void setProcessDefinitionMetadata(
        @Parameter(description = "系统 ID", required = true) @PathVariable long systemId,
        @Parameter(description = "流程定义元数据") @RequestBody ProcessDefinitionMetadata processDefinitionMetadata) {

        // TODO 从 Header 中获取调用者的用户名
        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        processService.setProcessDefinitionMetadata(systemId, processDefinitionMetadata, null);
    }

    @GetMapping("process-definitions")
    @Operation(summary = "分页模糊搜索系统下的流程定义列表")
    public IPage<ProcessDefinitionMetadata> processDefinitions(
        @Parameter(description = "页码") @RequestParam(defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
        @Parameter(description = "搜索关键词") @RequestParam String key) {

        // TODO 从 Header 中获取调用者的系统 ID
        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        return processService.processDefinitions(0, current, size, key);
    }

    @GetMapping("process-definition/{processDefinitionKey}")
    @Operation(summary = "流程定义详情")
    public ProcessDefinitionMetadata processDefinition(
        @Parameter(description = "流程定义 Key", required = true) @PathVariable String processDefinitionKey) {

        // TODO 从 Header 中获取调用者的系统 ID
        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个流程定义所属系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        return processService.processDefinition(0, processDefinitionKey);
    }

    @DeleteMapping("process-definition/{processDefinitionKey}")
    @Operation(summary = "删除流程定义")
    public void deleteProcessDefinition(
        @Parameter(description = "流程定义 Key", required = true) @PathVariable String processDefinitionKey) {

        // TODO 从 Header 中获取调用者的系统 ID 和用户名
        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个流程定义所属系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        processService.deleteProcessDefinition(0, processDefinitionKey, null);
    }

    @PostMapping("deploy/process-definition/{processDefinitionKey}")
    @Operation(summary = "部署流程定义")
    public void deployProcessDefinition(
        @Parameter(description = "流程定义 Key", required = true) @PathVariable String processDefinitionKey) {

        // TODO 从 Header 中获取调用者的系统 ID 和用户名
        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个流程定义所属系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        processService.deployProcessDefinition(0, processDefinitionKey, null);
    }

    @GetMapping("process-definition/{processDefinitionKey}/instances")
    @Operation(summary = "分页查询某流程定义下的流程实例")
    public IPage<ProcessInstanceResponse> processInstances(
        @Parameter(description = "流程定义Key", required = true) @PathVariable String processDefinitionKey,
        @Parameter(description = "是否完成") @RequestParam Boolean finished,
        @Parameter(description = "页码") @RequestParam(defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size) {

        // TODO 从 Header 中获取调用者的系统 ID
        return processService.processInstances(0, processDefinitionKey, finished, current, size);
    }

    @PostMapping("process-instance")
    @Operation(summary = "发起流程")
    public ProcessStartEventResponse startProcess(
        @Parameter(description = "发起流程请求参数") @RequestBody StartFlowRequest startFlowRequest) {

        // TODO 从 Header 中获取调用者的系统 ID 和用户名
        return processService.startProcess(0, startFlowRequest.getProcessDefinitionKey(),
            startFlowRequest.getVariables(), null);
    }

    @PostMapping("form-process-instance")
    @Operation(summary = "通过表单变量和流程定义ID发起流程")
    public String startProcessByForm(
        @Parameter(description = "发起流程请求参数") @RequestBody StartFlowRequest startFlowRequest) {

        // TODO 从 Header 中获取调用者的用户名
        return processService.startFlowByFormAndDefinitionId(startFlowRequest.getProcessDefinitionId(),
            startFlowRequest.getVariables(), null).getProcessInstanceId();
    }

    @GetMapping("process-instance/{processInstanceId}")
    @Operation(summary = "流程实例")
    public ProcessInstanceDetail processInstance(
        @Parameter(description = "流程实例 ID", required = true) @PathVariable String processInstanceId) {

        return processService.processInstance(processInstanceId);
    }

    @GetMapping("process-instance/{processInstanceId}/diagram")
    @Operation(summary = "流程图")
    public ResponseEntity<Resource> processDiagram(
        @Parameter(description = "流程实例 ID", required = true)
        @PathVariable String processInstanceId) throws IOException {

        return processService.processDiagram(processInstanceId);
    }

    @DeleteMapping("process-instance/{processInstanceId}")
    @Operation(summary = "停止流程实例")
    public void stopProcessInstance(
        @Parameter(description = "流程实例 ID", required = true) @PathVariable String processInstanceId,
        @Parameter(description = "停止理由", required = true) @RequestParam String stopReason) {

        // TODO 从 Header 中获取调用者的用户名
        processService.stopProcessInstance(processInstanceId, stopReason, null);
    }
}
