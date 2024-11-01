package org.cainiao.process.controller.intersystem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.entity.SystemMetadata;
import org.cainiao.process.service.SystemMetadataService;
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
        systemMetadataService.setSystemMetadata(0, null, systemMetadata);
    }
}
