package com.example.productservice.service;

import com.example.productservice.common.BizException;
import com.example.productservice.domain.entity.PublishTask;
import com.example.productservice.domain.entity.Spu;
import com.example.productservice.domain.enums.PublishStatus;
import com.example.productservice.mapper.BannedWordMapper;
import com.example.productservice.mapper.PublishTaskMapper;
import com.example.productservice.mapper.ReviewRecordMapper;
import com.example.productservice.mapper.SkuMapper;
import com.example.productservice.mapper.SpuMapper;
import com.example.productservice.web.dto.SubmitReviewRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PublishEngineServiceTest {

    @Mock
    private SpuMapper spuMapper;
    @Mock
    private SkuMapper skuMapper;
    @Mock
    private PublishTaskMapper publishTaskMapper;
    @Mock
    private ReviewRecordMapper reviewRecordMapper;
    @Mock
    private BannedWordMapper bannedWordMapper;
    @Mock
    private ProductModelingService productModelingService;
    @Mock
    private SearchService searchService;
    @Mock
    private OperationLogService operationLogService;

    @InjectMocks
    private PublishEngineService publishEngineService;

    @Test
    void shouldRejectScheduledStrategyWithoutTime() {
        Spu spu = new Spu();
        spu.setId(1L);
        spu.setPublishStatus(PublishStatus.DRAFT.name());
        when(productModelingService.getSpuOrThrow(1L)).thenReturn(spu);

        SubmitReviewRequest request = new SubmitReviewRequest();
        request.setSpuId(1L);
        request.setStrategy("SCHEDULED");

        Assertions.assertThrows(BizException.class, () -> publishEngineService.submitReview(request));
    }

    @Test
    void shouldRejectWhenStatusIsPublished() {
        Spu spu = new Spu();
        spu.setId(1L);
        spu.setPublishStatus(PublishStatus.PUBLISHED.name());
        when(productModelingService.getSpuOrThrow(1L)).thenReturn(spu);

        SubmitReviewRequest request = new SubmitReviewRequest();
        request.setSpuId(1L);
        request.setStrategy("IMMEDIATE");

        Assertions.assertThrows(BizException.class, () -> publishEngineService.submitReview(request));
    }

    @Test
    void shouldSubmitReviewWhenValid() {
        Spu spu = new Spu();
        spu.setId(1L);
        spu.setPublishStatus(PublishStatus.DRAFT.name());
        when(productModelingService.getSpuOrThrow(1L)).thenReturn(spu);
        when(productModelingService.buildSpuSearchableText(1L)).thenReturn("巴黎世家 连衣裙");
        when(bannedWordMapper.selectEnabled()).thenReturn(java.util.List.of());
        when(publishTaskMapper.selectById(any())).thenReturn(new PublishTask());

        SubmitReviewRequest request = new SubmitReviewRequest();
        request.setSpuId(1L);
        request.setStrategy("IMMEDIATE");
        request.setOperator("OPERATOR");

        PublishTask task = publishEngineService.submitReview(request);
        Assertions.assertNotNull(task);
    }
}
