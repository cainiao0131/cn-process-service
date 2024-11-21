package org.cainiao.process.dao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.cainiao.process.dao.mapper.ProcessEventLogMapper;
import org.cainiao.process.entity.ProcessEventLog;
import org.cainiao.process.entity.ProcessEventLog.ElementTypeEnum;
import org.cainiao.process.entity.ProcessEventLog.EventType;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <br />
 * <p>
 * Author: Cai Niao(wdhlzd@163.com)<br />
 */
@Service
public class ProcessEventLogMapperService extends ServiceImpl<ProcessEventLogMapper, ProcessEventLog>
    implements IService<ProcessEventLog> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEventLogMapperService.class);

    /**
     * 为工作流用户任务的各种事件记录日志
     */
    public void logForFlowUserTaskEvent(String traceId, DelegateTask delegateTask,
                                        EventType eventType, Exception exception, HttpInfo httpInfo) {
        Map<String, Object> context = new HashMap<>();
        context.put("assignee", delegateTask.getAssignee());
        context.put("name", delegateTask.getName());
        HttpStatusCode httpStatusCode = httpInfo.getHttpStatusCode();

        if (!save(ProcessEventLog.builder()
            .processDefinitionKey(delegateTask.getProcessDefinitionId().split(":")[0])
            .processInstanceId(delegateTask.getProcessInstanceId())
            .elementType(ElementTypeEnum.USER_TASK)
            .elementInstanceId(delegateTask.getId())
            .eventType(eventType)
            .traceId(traceId)
            .requestBody(httpInfo.getRequestBody())
            .httpStatusCode(httpStatusCode == null ? null : httpStatusCode.value())
            .context(context)
            .exceptionMessage(exception == null ? null : exception.getMessage())
            .build())) {

            LOGGER.error("添加流程事件日志失败！");
        }
    }

    @AllArgsConstructor
    @Data
    @Builder
    public static class HttpInfo {
        private Map<String, Object> requestBody;
        private HttpStatusCode httpStatusCode;
    }
}
