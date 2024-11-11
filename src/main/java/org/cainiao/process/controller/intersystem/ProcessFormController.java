package org.cainiao.process.controller.intersystem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.dto.response.FormResponse;
import org.cainiao.process.service.FormService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE;
import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE_SIZE;

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

    @GetMapping("system/{systemId}/forms")
    @Operation(summary = "分页模糊搜索某系统中的流程表单")
    public IPage<FormResponse> forms(
        @Parameter(description = "系统 ID", required = true) @PathVariable long systemId,
        @Parameter(description = "页码") @RequestParam(defaultValue = DEFAULT_PAGE) long current,
        @Parameter(description = "页面大小") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
        @Parameter(description = "搜索关键词") @RequestParam String key) {

        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        return formService.forms(systemId, current, size, key);
    }
}
