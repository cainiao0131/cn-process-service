package org.cainiao.process.controller.intersystem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.entity.ProcessDefinitionMetadata;
import org.cainiao.process.service.ProcessDefinitionMetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE;
import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("inter-system")
@Tag(name = "ProcessDefinitionMetadata", description = "管理流程定义元数据")
@RequiredArgsConstructor
public class ProcessDefinitionMetadataController {

    private final ProcessDefinitionMetadataService processDefinitionMetadataService;

    @GetMapping("process-definitions")
    @Operation(summary = "分页模糊搜索系统下的流程定义列表")
    public IPage<ProcessDefinitionMetadata> processDefinitions(
        @Parameter(description = "页码") @RequestParam(required = false, defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) long size,
        @Parameter(description = "搜索关键词") @RequestParam(required = false) String key) {

        // TODO 从 Header 中获取调用者的系统 ID
        return processDefinitionMetadataService.processDefinitions(0, current, size, key);
    }

    @GetMapping("process-definitions/{processDefinitionKey}")
    @Operation(summary = "流程定义详情")
    public ProcessDefinitionMetadata processDefinition(
        @Parameter(description = "流程定义Key", required = true) @PathVariable String processDefinitionKey) {

        // TODO 从 Header 中获取调用者的系统 ID
        return processDefinitionMetadataService.processDefinition(0, processDefinitionKey);
    }
}
