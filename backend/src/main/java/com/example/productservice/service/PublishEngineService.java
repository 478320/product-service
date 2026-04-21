package com.example.productservice.service;

import com.example.productservice.common.BizException;
import com.example.productservice.domain.entity.BannedWord;
import com.example.productservice.domain.entity.PublishTask;
import com.example.productservice.domain.entity.ReviewRecord;
import com.example.productservice.domain.entity.Spu;
import com.example.productservice.domain.enums.PublishStatus;
import com.example.productservice.domain.enums.PublishStrategyType;
import com.example.productservice.domain.enums.ReviewDecision;
import com.example.productservice.mapper.BannedWordMapper;
import com.example.productservice.mapper.PublishTaskMapper;
import com.example.productservice.mapper.ReviewRecordMapper;
import com.example.productservice.mapper.SkuMapper;
import com.example.productservice.mapper.SpuMapper;
import com.example.productservice.web.dto.ExecutePublishRequest;
import com.example.productservice.web.dto.OffShelfRequest;
import com.example.productservice.web.dto.ReviewRequest;
import com.example.productservice.web.dto.SubmitReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishEngineService {

    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;
    private final PublishTaskMapper publishTaskMapper;
    private final ReviewRecordMapper reviewRecordMapper;
    private final BannedWordMapper bannedWordMapper;
    private final ProductModelingService productModelingService;
    private final SearchService searchService;
    private final OperationLogService operationLogService;

    @Transactional
    public PublishTask submitReview(SubmitReviewRequest request) {
        Spu spu = productModelingService.getSpuOrThrow(request.getSpuId());
        if (!PublishStatus.DRAFT.name().equals(spu.getPublishStatus())
                && !PublishStatus.REVIEW_REJECTED.name().equals(spu.getPublishStatus())) {
            throw new BizException("当前状态不允许提交审核: " + spu.getPublishStatus());
        }

        PublishStrategyType strategy = parseStrategy(request.getStrategy());
        if (strategy == PublishStrategyType.SCHEDULED && request.getScheduledPublishTime() == null) {
            throw new BizException("定时发布必须传 scheduledPublishTime");
        }
        productModelingService.validateRequiredAttributesForPublish(spu.getId());
        validateBannedWords(spu.getId());

        spuMapper.updatePublishInfo(spu.getId(), PublishStatus.PENDING_REVIEW.name(), strategy.name(),
                request.getScheduledPublishTime(), null);

        PublishTask task = new PublishTask();
        task.setSpuId(spu.getId());
        task.setStrategy(strategy.name());
        task.setScheduledTime(request.getScheduledPublishTime());
        task.setTaskStatus("PENDING_REVIEW");
        task.setCreatedBy(request.getOperator() == null ? "operator" : request.getOperator());
        publishTaskMapper.insert(task);

        operationLogService.record("PUBLISH_TASK", task.getId(), "SUBMIT_REVIEW", task.getCreatedBy(),
                "提交审核, strategy=" + strategy.name());
        return publishTaskMapper.selectById(task.getId());
    }

    @Transactional
    public PublishTask approve(Long taskId, ReviewRequest request) {
        PublishTask task = getTaskOrThrow(taskId);
        if (!"PENDING_REVIEW".equals(task.getTaskStatus())) {
            throw new BizException("任务状态不是待审核: " + task.getTaskStatus());
        }
        Spu spu = productModelingService.getSpuOrThrow(task.getSpuId());
        if (!PublishStatus.PENDING_REVIEW.name().equals(spu.getPublishStatus())) {
            throw new BizException("SPU 当前不是待审核状态");
        }

        String reviewer = normalizeReviewer(request.getReviewer());
        ReviewRecord record = new ReviewRecord();
        record.setPublishTaskId(taskId);
        record.setSpuId(spu.getId());
        record.setDecision(ReviewDecision.APPROVE.name());
        record.setComment(request.getComment());
        record.setReviewer(reviewer);
        reviewRecordMapper.insert(record);

        spuMapper.updatePublishInfo(spu.getId(), PublishStatus.REVIEW_PASSED.name(), task.getStrategy(), task.getScheduledTime(), null);
        publishTaskMapper.updateStatus(taskId, "REVIEW_PASSED", null);
        operationLogService.record("REVIEW", taskId, "APPROVE", reviewer, "审核通过");

        PublishStrategyType strategy = parseStrategy(task.getStrategy());
        if (strategy == PublishStrategyType.IMMEDIATE) {
            return executePublish(task, reviewer, "审核通过立即发布");
        }
        spuMapper.updatePublishInfo(spu.getId(), PublishStatus.WAITING_PUBLISH.name(), task.getStrategy(), task.getScheduledTime(), null);
        publishTaskMapper.updateStatus(taskId, "WAITING_PUBLISH", null);
        return publishTaskMapper.selectById(taskId);
    }

    @Transactional
    public PublishTask reject(Long taskId, ReviewRequest request) {
        PublishTask task = getTaskOrThrow(taskId);
        if (!"PENDING_REVIEW".equals(task.getTaskStatus())) {
            throw new BizException("任务状态不是待审核: " + task.getTaskStatus());
        }
        Spu spu = productModelingService.getSpuOrThrow(task.getSpuId());

        String reviewer = normalizeReviewer(request.getReviewer());
        ReviewRecord record = new ReviewRecord();
        record.setPublishTaskId(taskId);
        record.setSpuId(spu.getId());
        record.setDecision(ReviewDecision.REJECT.name());
        record.setComment(request.getComment() == null ? "审核驳回" : request.getComment());
        record.setReviewer(reviewer);
        reviewRecordMapper.insert(record);

        publishTaskMapper.updateStatus(taskId, "REJECTED", record.getComment());
        spuMapper.updatePublishInfo(spu.getId(), PublishStatus.REVIEW_REJECTED.name(), task.getStrategy(), task.getScheduledTime(), record.getComment());
        operationLogService.record("REVIEW", taskId, "REJECT", reviewer, "审核驳回");
        return publishTaskMapper.selectById(taskId);
    }

    @Transactional
    public PublishTask executeManualPublish(ExecutePublishRequest request) {
        PublishTask task = publishTaskMapper.selectLatestBySpuId(request.getSpuId());
        if (task == null) {
            throw new BizException("未找到发布任务");
        }
        if (!"WAITING_PUBLISH".equals(task.getTaskStatus())) {
            throw new BizException("当前任务不是待发布状态: " + task.getTaskStatus());
        }
        if (!PublishStrategyType.MANUAL_AFTER_REVIEW.name().equals(task.getStrategy())) {
            throw new BizException("该任务不是人工触发发布策略");
        }
        return executePublish(task, normalizeOperator(request.getOperator()), "人工触发发布");
    }

    @Transactional
    public void offShelf(OffShelfRequest request) {
        Spu spu = productModelingService.getSpuOrThrow(request.getSpuId());
        if (!PublishStatus.PUBLISHED.name().equals(spu.getPublishStatus())) {
            throw new BizException("仅已发布商品允许下架");
        }
        spuMapper.updatePublishInfo(spu.getId(), PublishStatus.OFF_SHELF.name(), spu.getPublishStrategy(), null, null);
        skuMapper.updateStatusBySpuId(spu.getId(), "OFF_SHELF");
        searchService.removeSpu(spu.getId());
        operationLogService.record("SPU", spu.getId(), "OFF_SHELF", normalizeOperator(request.getOperator()), "商品下架");
    }

    public List<PublishTask> listLatestTasks(Integer limit) {
        int actual = limit == null ? 100 : Math.max(1, Math.min(limit, 500));
        return publishTaskMapper.selectLatest(actual);
    }

    @Scheduled(fixedDelay = 30000)
    public void runScheduledPublish() {
        List<PublishTask> tasks = publishTaskMapper.selectDueScheduledTasks(LocalDateTime.now());
        for (PublishTask task : tasks) {
            try {
                executePublish(task, "scheduler", "定时任务自动发布");
            } catch (Exception ex) {
                log.error("定时发布失败, taskId={}, err={}", task.getId(), ex.getMessage());
            }
        }
    }

    private PublishTask executePublish(PublishTask task, String operator, String source) {
        Spu spu = productModelingService.getSpuOrThrow(task.getSpuId());
        if (!PublishStatus.WAITING_PUBLISH.name().equals(spu.getPublishStatus())
                && !PublishStatus.REVIEW_PASSED.name().equals(spu.getPublishStatus())) {
            throw new BizException("SPU 状态不允许发布: " + spu.getPublishStatus());
        }

        validateBannedWords(spu.getId());
        spuMapper.updatePublishInfo(spu.getId(), PublishStatus.PUBLISHED.name(), task.getStrategy(), task.getScheduledTime(), null);
        skuMapper.updateStatusBySpuId(spu.getId(), "ON_SALE");
        publishTaskMapper.updateStatus(task.getId(), "PUBLISHED", null);
        searchService.indexSpu(spu.getId());
        operationLogService.record("PUBLISH_TASK", task.getId(), "PUBLISH_SUCCESS", operator, source);
        return publishTaskMapper.selectById(task.getId());
    }

    private void validateBannedWords(Long spuId) {
        String allText = productModelingService.buildSpuSearchableText(spuId).toLowerCase();
        List<BannedWord> words = bannedWordMapper.selectEnabled();
        for (BannedWord bannedWord : words) {
            String word = bannedWord.getWord();
            if (word != null && !word.isBlank() && allText.contains(word.toLowerCase())) {
                throw new BizException("命中违禁词: " + word);
            }
        }
    }

    private PublishTask getTaskOrThrow(Long taskId) {
        PublishTask task = publishTaskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException("发布任务不存在: " + taskId);
        }
        return task;
    }

    private PublishStrategyType parseStrategy(String strategy) {
        try {
            return PublishStrategyType.valueOf(strategy.trim().toUpperCase());
        } catch (Exception ex) {
            throw new BizException("非法发布策略: " + strategy);
        }
    }

    private String normalizeReviewer(String reviewer) {
        return reviewer == null || reviewer.isBlank() ? "reviewer" : reviewer;
    }

    private String normalizeOperator(String operator) {
        return operator == null || operator.isBlank() ? "operator" : operator;
    }
}
