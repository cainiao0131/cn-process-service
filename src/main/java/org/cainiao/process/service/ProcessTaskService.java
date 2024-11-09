package org.cainiao.process.service;

import org.cainiao.process.dto.request.ReassignTaskRequest;

public interface ProcessTaskService {

    void reassignOwnTask(String userName, ReassignTaskRequest reassignTaskRequest);
}
