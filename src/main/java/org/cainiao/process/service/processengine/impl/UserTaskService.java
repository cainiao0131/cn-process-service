package org.cainiao.process.service.processengine.impl;

import lombok.RequiredArgsConstructor;
import org.cainiao.process.dao.service.ProcessEventLogMapperService;
import org.cainiao.process.dao.service.ProcessEventLogMapperService.HttpInfo;
import org.cainiao.process.dao.service.SystemMetadataMapperService;
import org.cainiao.process.entity.ProcessEventLog.EventType;
import org.cainiao.process.entity.SystemMetadata;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.*;

import static org.cainiao.process.util.TimeUtil.SIMPLE_DATE_FORMAT;

@Service
@RequiredArgsConstructor
public class UserTaskService {

    private static final String ASSIGNEES_SEPARATOR = ",";

    private final ProcessEventLogMapperService processEventLogMapperService;
    private final SystemMetadataMapperService systemMetadataMapperService;
    private final HttpService httpService;
    private final RepositoryService repositoryService;

    /**
     * 流程引擎调用这个方法，用户任务，动态获取多个委托人
     *
     * @param execution       DelegateExecution
     * @param assigneesString 表示多个委托人的字符串，可以混合使用字面量与 EL 表达式，如："LiHong,${admin},WangMing"
     * @return 委托人列表
     */
    public List<String> getCollection(DelegateExecution execution, String assigneesString) {
        if (!StringUtils.hasText(assigneesString)) {
            return Collections.emptyList();
        }
        List<String> collection = new ArrayList<>();
        for (String assignee : assigneesString.split(ASSIGNEES_SEPARATOR)) {
            if (assignee.startsWith("${") && assignee.endsWith("}")) {
                Object assigneeObject = execution.getVariable(assignee.replaceAll("[${}]", ""));
                if (assigneeObject != null) {
                    String assignees = assigneeObject.toString();
                    if (assignees.contains(ASSIGNEES_SEPARATOR)) {
                        collection.addAll(Arrays.asList(assignees.split(ASSIGNEES_SEPARATOR)));
                    } else {
                        collection.add(assignees);
                    }
                }
            } else {
                collection.add(assignee);
            }
        }
        return collection;
    }

    /**
     * 用户任务创建事件回调<br />
     * 不要将整个方法配置为异步的，因为流程引擎在 ThreadLocal 中维护了一些对象，别的线程拿不到<br />
     * 先获取流程相关信息，然后再在异步方法中完成发送 HTTP 请求、请求数据库等 I/O 操作<br />
     * <p>
     * 另外注意，如果 Spring 的 AOP 是用的 JDK 的动态代理 API<br />
     * 那么在当前对象的方法中调用另一个加了 @Async 注解的方法，异步不会生效
     * 因为成员方法调用成员方法，用的是 this 关键字进行调用，而 this 指向的是被代理对象，而不是代理对象
     * 因此不会执行异步代理对象中调用线程池的代码
     *
     * @param task 用户任务
     */
    public void onCreate(DelegateTask task) {
        SystemMetadata systemMetadata = systemMetadataMapperService.getOne(Long.parseLong(task.getTenantId()));
        EventType eventType = EventType.CREATE;
        postRetryAndLog(getRequestBody(task, eventType), task, systemMetadata, eventType);
    }

    /**
     * 用户任务完成事件回调<br />
     * 不要将整个方法配置为异步的，因为流程引擎在 ThreadLocal 中维护了一些对象，别的线程拿不到<br />
     * 先获取流程相关信息，然后再在异步方法中完成发送 HTTP 请求、请求数据库等 I/O 操作<br />
     * <p>
     * 另外注意，如果 Spring 的 AOP 是用的 JDK 的动态代理 API<br />
     * 那么在当前对象的方法中调用另一个加了 @Async 注解的方法，异步不会生效
     * 因为成员方法调用成员方法，用的是 this 关键字进行调用，而 this 指向的是被代理对象，而不是代理对象
     * 因此不会执行异步代理对象中调用线程池的代码
     *
     * @param task 用户任务
     */
    public void onComplete(DelegateTask task) {
        SystemMetadata systemMetadata = systemMetadataMapperService.getOne(Long.parseLong(task.getTenantId()));
        EventType eventType = EventType.COMPLETE;
        Map<String, Object> requestBody = getRequestBody(task, eventType);
        Map<String, Object> taskVariables = task.getVariablesLocal();
        if (!taskVariables.isEmpty()) {
            requestBody.put("variables", taskVariables);
        }
        postRetryAndLog(requestBody, task, systemMetadata, eventType);
    }

    private void postRetryAndLog(Map<String, Object> requestBody, DelegateTask task,
                                 SystemMetadata systemMetadata, EventType eventType) {
        httpService.postRetryIfFail(requestBody, String.format("%s/workflow-webhook", systemMetadata.getWebhook()),
            // 请求成功回调
            (traceId, responseEntity) -> processEventLogMapperService.logForFlowUserTaskEvent(traceId,
                task, eventType, null,
                HttpInfo.builder().requestBody(requestBody).httpStatusCode(responseEntity.getStatusCode()).build()),
            // 请求失败回调
            (traceId, e) -> {
                HttpInfo httpInfo = HttpInfo.builder().requestBody(requestBody).build();
                if (e instanceof HttpStatusCodeException httpStatusCodeException) {
                    httpInfo.setHttpStatusCode(httpStatusCodeException.getStatusCode());
                }
                processEventLogMapperService.logForFlowUserTaskEvent(traceId, task, eventType, e, httpInfo);
            });
    }

    private Map<String, Object> getRequestBody(DelegateTask task, EventType eventType) {
        Map<String, Object> requestBody = new HashMap<>();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        requestBody.put("processDefinitionKey", bpmnModel.getMainProcess().getId().split(":")[0]);
        requestBody.put("elementName", bpmnModel.getFlowElement(taskDefinitionKey).getClass().getSimpleName());
        requestBody.put("taskDefinitionKey", taskDefinitionKey);
        requestBody.put("eventType", eventType.getName());
        requestBody.put("processInstanceId", task.getProcessInstanceId());
        requestBody.put("taskId", task.getId());
        requestBody.put("taskAssignee", task.getAssignee());
        requestBody.put("taskName", task.getName());
        requestBody.put("taskCreateTime", SIMPLE_DATE_FORMAT.format(task.getCreateTime()));
        Map<String, Object> allVariables = task.getVariables();
        if (!allVariables.isEmpty()) {
            requestBody.put("allVariables", allVariables);
        }
        return requestBody;
    }
}
