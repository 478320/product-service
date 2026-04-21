package com.example.productservice.service;

import com.example.productservice.domain.entity.OperationLog;
import com.example.productservice.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public void record(String bizType, Long bizId, String action, String operator, String detail) {
        // 这里是按你的要求做的“简化日志实现”：只做关键动作落库，不做链路追踪/指标/告警平台。
        OperationLog log = new OperationLog();
        log.setBizType(bizType);
        log.setBizId(bizId);
        log.setAction(action);
        log.setOperator(operator == null ? "system" : operator);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }

    public List<OperationLog> latest(Integer limit) {
        int actual = limit == null ? 50 : Math.max(1, Math.min(limit, 500));
        return operationLogMapper.selectLatest(actual);
    }
}
