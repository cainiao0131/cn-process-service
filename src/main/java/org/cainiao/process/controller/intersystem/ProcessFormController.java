package org.cainiao.process.controller.intersystem;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("inter-system")
@Tag(name = "ProcessFormController", description = "流程表单管理")
@RequiredArgsConstructor
public class ProcessFormController {

}
