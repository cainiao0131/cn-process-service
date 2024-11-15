package org.cainiao.process.controller.intersystem;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.cainiao.process.dto.FormWithVersion;
import org.cainiao.process.dto.response.FormResponse;
import org.cainiao.process.entity.FormVersion;
import org.cainiao.process.service.FormService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE;
import static org.cainiao.process.dao.DaoUtil.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("inter-system")
@Tag(name = "FormController", description = "流程表单管理")
@RequiredArgsConstructor
public class FormController {

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
        @Parameter(description = "是否归档") @RequestParam(defaultValue = "false") boolean archived,
        @Parameter(description = "页码") @RequestParam(defaultValue = DEFAULT_PAGE) int current,
        @Parameter(description = "页面大小") @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
        @Parameter(description = "搜索关键词") @RequestParam String key) {

        // 只有【技术中台】可以访问这个接口，由【系统网关】根据【服务编排】进行访问控制
        // 用户是否参与了建设这个系统的项目的数据权限校验，由【技术中台】的聚合服务完成
        return formService.forms(systemId, archived, current, size, key);
    }

    @GetMapping("form/{key}/versions")
    @Operation(summary = "表单版本")
    public List<FormVersion> versions(
        @Parameter(description = "表单 Key", required = true) @PathVariable String key) {

        return formService.formVersions(key);
    }

    @GetMapping("form/{key}")
    @Operation(summary = "表单详情")
    public FormWithVersion form(
        @Parameter(description = "表单 Key", required = true) @PathVariable String key) {

        // TODO 从 Header 中获取调用者的系统 ID
        return formService.form(0, key);
    }

    @DeleteMapping("form/{key}")
    @Operation(summary = "删除表单")
    public void deleteForm(@Parameter(description = "表单 Key", required = true) @PathVariable String key) {
        // TODO 从 Header 中获取调用者的系统 ID 和用户名
        formService.deleteForm(0, key, null);
    }
}
