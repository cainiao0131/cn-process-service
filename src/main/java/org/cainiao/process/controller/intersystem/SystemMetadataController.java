package org.cainiao.process.controller.intersystem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.entity.SystemMetadata;
import org.cainiao.process.service.SystemMetadataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inter-system")
@Tag(name = "SystemMetadata", description = "管理流程相关的系统元数据")
@RequiredArgsConstructor
public class SystemMetadataController {

    private final SystemMetadataService systemMetadataService;

    @PostMapping("system-metadata")
    @Operation(summary = "为系统设置流程相关的元数据")
    public void setSystemMetadata(@Parameter(description = "系统元数据") @RequestBody SystemMetadata systemMetadata) {
        // TODO 从 Header 中获取调用者的系统 ID 和用户名
        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        systemMetadataService.setSystemMetadata(0, null, systemMetadata);
    }

    @GetMapping("system-metadata/{systemId}")
    @Operation(summary = "获取系统的流程相关元数据")
    public SystemMetadata getSystemMetadata(
        @Parameter(description = "系统 ID", required = true) @PathVariable Long systemId) {

        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        return systemMetadataService.getSystemMetadata(systemId);
    }
}
