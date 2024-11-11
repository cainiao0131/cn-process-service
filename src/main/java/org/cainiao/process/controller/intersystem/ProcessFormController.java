package org.cainiao.process.controller.intersystem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.service.FormService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inter-system")
@Tag(name = "ProcessFormController", description = "流程表单管理")
@RequiredArgsConstructor
public class ProcessFormController {

    private final FormService formService;

    @PostMapping("form")
    @Operation(summary = "添加或编辑流程表单")
    public void addOrEditForm(@Parameter(description = "流程表单") @RequestBody FormWithVersion formWithVersion) {
        // TODO 从 Header 中获取调用者的系统 ID 和用户名
        formService.addOrEditForm(0, formWithVersion, null);
    }
}
