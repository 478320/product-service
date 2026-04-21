package com.example.productservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch._types.query_dsl.ZeroTermsQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.example.productservice.common.BizException;
import com.example.productservice.domain.entity.Brand;
import com.example.productservice.domain.entity.Category;
import com.example.productservice.domain.entity.Sku;
import com.example.productservice.domain.entity.SkuAttrValue;
import com.example.productservice.domain.entity.Spu;
import com.example.productservice.domain.entity.SpuAttrValue;
import com.example.productservice.domain.enums.PublishStatus;
import com.example.productservice.mapper.BrandMapper;
import com.example.productservice.mapper.CategoryMapper;
import com.example.productservice.mapper.SkuAttrValueMapper;
import com.example.productservice.mapper.SkuMapper;
import com.example.productservice.mapper.SpuAttrValueMapper;
import com.example.productservice.mapper.SpuMapper;
import com.example.productservice.web.dto.PageResult;
import com.example.productservice.web.dto.SearchProductItem;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    public static final String INDEX_NAME = "product_search_v1";

    private final ElasticsearchClient elasticsearchClient;
    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;
    private final SpuAttrValueMapper spuAttrValueMapper;
    private final SkuAttrValueMapper skuAttrValueMapper;
    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    private final OperationLogService operationLogService;

    @PostConstruct
    public void initIndexOnBoot() {
        try {
            ensureIndex();
        } catch (Exception ex) {
            // ES 可能在应用启动后才可用，这里只记录，不阻断应用启动。
            log.warn("初始化 ES 索引失败: {}", ex.getMessage());
        }
    }

    public void ensureIndex() {
        try {
            boolean exists = elasticsearchClient.indices().exists(r -> r.index(INDEX_NAME)).value();
            if (exists) {
                return;
            }
            try {
                elasticsearchClient.indices().create(req -> req.index(INDEX_NAME).withJson(new StringReader(buildIndexMapping(true))));
                operationLogService.record("SEARCH", 0L, "INDEX_CREATE", "system", "使用 IK 分词创建索引");
            } catch (Exception ikEx) {
                // 生产推荐 IK；但本地实验若镜像不可用 IK 插件，这里自动降级为 standard，保证链路可跑通。
                log.warn("IK 分词索引创建失败，回退 standard 分词: {}", ikEx.getMessage());
                elasticsearchClient.indices().create(req -> req.index(INDEX_NAME).withJson(new StringReader(buildIndexMapping(false))));
                operationLogService.record("SEARCH", 0L, "INDEX_CREATE_FALLBACK", "system", "IK 不可用，回退 standard 分词");
            }
        } catch (Exception ex) {
            throw new BizException("创建 ES 索引失败: " + ex.getMessage());
        }
    }

    private String buildIndexMapping(boolean useIk) {
        if (useIk) {
            return """
                    {
                      "settings": {
                        "analysis": {
                          "analyzer": {
                            "ik_index_analyzer": { "type": "ik_max_word" },
                            "ik_search_analyzer": { "type": "ik_smart" }
                          }
                        }
                      },
                      "mappings": {
                        "properties": {
                          "spuId": { "type": "long" },
                          "skuId": { "type": "long" },
                          "spuTitle": {
                            "type": "text",
                            "analyzer": "ik_index_analyzer",
                            "search_analyzer": "ik_search_analyzer"
                          },
                          "skuName": {
                            "type": "text",
                            "analyzer": "ik_index_analyzer",
                            "search_analyzer": "ik_search_analyzer"
                          },
                          "brandName": {
                            "type": "text",
                            "analyzer": "ik_index_analyzer",
                            "search_analyzer": "ik_search_analyzer",
                            "fields": { "keyword": { "type": "keyword" } }
                          },
                          "categoryName": {
                            "type": "text",
                            "analyzer": "ik_index_analyzer",
                            "search_analyzer": "ik_search_analyzer",
                            "fields": { "keyword": { "type": "keyword" } }
                          },
                          "attrText": {
                            "type": "text",
                            "analyzer": "ik_index_analyzer",
                            "search_analyzer": "ik_search_analyzer"
                          },
                          "publishStatus": { "type": "keyword" },
                          "sales": { "type": "integer" },
                          "createdAt": { "type": "date" }
                        }
                      }
                    }
                    """;
        }
        return """
                {
                  "mappings": {
                    "properties": {
                      "spuId": { "type": "long" },
                      "skuId": { "type": "long" },
                      "spuTitle": { "type": "text" },
                      "skuName": { "type": "text" },
                      "brandName": {
                        "type": "text",
                        "fields": { "keyword": { "type": "keyword" } }
                      },
                      "categoryName": {
                        "type": "text",
                        "fields": { "keyword": { "type": "keyword" } }
                      },
                      "attrText": { "type": "text" },
                      "publishStatus": { "type": "keyword" },
                      "sales": { "type": "integer" },
                      "createdAt": { "type": "date" }
                    }
                  }
                }
                """;
    }

    public void indexSpu(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        if (spu == null) {
            throw new BizException("SPU 不存在: " + spuId);
        }
        Brand brand = brandMapper.selectById(spu.getBrandId());
        Category category = categoryMapper.selectById(spu.getCategoryId());
        if (brand == null || category == null) {
            throw new BizException("SPU 关联品牌或类目不存在, spuId=" + spuId);
        }

        ensureIndex();
        removeSpu(spuId);

        List<Sku> skuList = skuMapper.selectBySpuId(spuId);
        String spuAttrText = buildSpuAttrText(spuId);

        try {
            for (Sku sku : skuList) {
                String skuAttrText = buildSkuAttrText(sku.getId());
                SearchProductDoc doc = new SearchProductDoc();
                doc.setSpuId(spuId);
                doc.setSkuId(sku.getId());
                doc.setSpuTitle(spu.getTitle());
                doc.setSkuName(sku.getSkuName());
                doc.setBrandName(brand.getName());
                doc.setCategoryName(category.getName());
                doc.setAttrText((spuAttrText + " " + skuAttrText).trim());
                doc.setPublishStatus(spu.getPublishStatus());
                doc.setSales(sku.getSales() == null ? 0 : sku.getSales());
                LocalDateTime createdAt = sku.getCreatedAt() == null ? spu.getCreatedAt() : sku.getCreatedAt();
                doc.setCreatedAt(createdAt == null ? LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        : createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                elasticsearchClient.index(i -> i.index(INDEX_NAME)
                        .id(String.valueOf(sku.getId()))
                        .document(doc)
                        .refresh(Refresh.WaitFor));
            }
            operationLogService.record("SEARCH", spuId, "INDEX_UPSERT", "system", "SPU 索引已更新");
        } catch (Exception ex) {
            throw new BizException("写入 ES 索引失败: " + ex.getMessage());
        }
    }

    public void removeSpu(Long spuId) {
        try {
            boolean exists = elasticsearchClient.indices().exists(r -> r.index(INDEX_NAME)).value();
            if (!exists) {
                return;
            }
            elasticsearchClient.deleteByQuery(d -> d.index(INDEX_NAME)
                    .query(q -> q.term(t -> t.field("spuId").value(spuId)))
                    .refresh(true));
        } catch (Exception ex) {
            throw new BizException("删除 ES 索引失败: " + ex.getMessage());
        }
    }

    public PageResult<SearchProductItem> search(String q,
                                                String brand,
                                                String category,
                                                Integer pageNo,
                                                Integer pageSize) {
        ensureIndex();
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        int from = (safePageNo - 1) * safePageSize;
        String keyword = q == null ? "" : q.trim();

        try {
            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .from(from)
                            .size(safePageSize)
                            .query(query -> query.functionScore(fs -> fs
                                    .query(inner -> inner.bool(b -> b
                                            .filter(filter -> filter.term(t -> t.field("publishStatus").value(PublishStatus.PUBLISHED.name())))
                                            .filter(filter -> {
                                                if (brand != null && !brand.isBlank()) {
                                                    return filter.term(t -> t.field("brandName.keyword").value(brand));
                                                }
                                                return filter.matchAll(ma -> ma);
                                            })
                                            .filter(filter -> {
                                                if (category != null && !category.isBlank()) {
                                                    return filter.term(t -> t.field("categoryName.keyword").value(category));
                                                }
                                                return filter.matchAll(ma -> ma);
                                            })
                                            .must(must -> {
                                                if (keyword.isBlank()) {
                                                    return must.matchAll(ma -> ma);
                                                }
                                                return must.multiMatch(mm -> mm
                                                        .query(keyword)
                                                        .fields("brandName^8", "spuTitle^5", "skuName^4", "categoryName^3", "attrText^1")
                                                        .type(TextQueryType.BestFields)
                                                        .zeroTermsQuery(ZeroTermsQuery.All));
                                            })
                                    ))
                                    .functions(func -> {
                                        if (keyword.isBlank()) {
                                            return func.weight(1.0);
                                        }
                                        return func.filter(filter -> filter.term(t -> t.field("brandName.keyword").value(keyword)))
                                                .weight(6.0);
                                    })
                                    .scoreMode(FunctionScoreMode.Sum)
                                    .boostMode(co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Sum)
                            ))
                            .sort(sort -> sort.score(sc -> sc.order(SortOrder.Desc)))
                            .sort(sort -> sort.field(f -> f.field("sales").order(SortOrder.Desc)))
                            .sort(sort -> sort.field(f -> f.field("createdAt").order(SortOrder.Desc)))
                            .highlight(h -> h
                                    .fields("brandName", f -> f)
                                    .fields("spuTitle", f -> f)
                                    .fields("skuName", f -> f)),
                    Map.class);

            List<SearchProductItem> result = new ArrayList<>();
            for (Hit<Map> hit : response.hits().hits()) {
                Map<?, ?> source = hit.source();
                if (source == null) {
                    continue;
                }
                SearchProductItem item = new SearchProductItem();
                item.setSpuId(asLong(source.get("spuId")));
                item.setSkuId(asLong(source.get("skuId")));
                item.setSpuTitle(Objects.toString(source.get("spuTitle"), ""));
                item.setSkuName(Objects.toString(source.get("skuName"), ""));
                item.setBrandName(Objects.toString(source.get("brandName"), ""));
                item.setCategoryName(Objects.toString(source.get("categoryName"), ""));
                item.setSales(asInt(source.get("sales")));
                if (hit.highlight() != null && !hit.highlight().isEmpty()) {
                    Map<String, List<String>> highlight = hit.highlight();
                    List<String> merged = new ArrayList<>();
                    highlight.values().forEach(merged::addAll);
                    item.setHighlight(String.join(" | ", merged));
                }
                result.add(item);
            }

            long total = response.hits().total() == null ? result.size() : response.hits().total().value();
            return new PageResult<>(total, safePageNo, safePageSize, result);
        } catch (Exception ex) {
            throw new BizException("搜索失败: " + ex.getMessage());
        }
    }

    public int reindexPublishedProducts() {
        ensureIndex();
        List<Spu> publishedSpu = spuMapper.selectByStatus(PublishStatus.PUBLISHED.name());
        try {
            elasticsearchClient.deleteByQuery(d -> d.index(INDEX_NAME).query(q -> q.matchAll(m -> m)).refresh(true));
        } catch (Exception ex) {
            throw new BizException("清理旧索引失败: " + ex.getMessage());
        }
        for (Spu spu : publishedSpu) {
            indexSpu(spu.getId());
        }
        operationLogService.record("SEARCH", 0L, "REINDEX_ALL", "system", "重建索引数量: " + publishedSpu.size());
        return publishedSpu.size();
    }

    private String buildSpuAttrText(Long spuId) {
        List<SpuAttrValue> attrs = spuAttrValueMapper.selectBySpuId(spuId);
        return attrs.stream()
                .map(v -> v.getAttrName() + " " + v.getAttrValue())
                .collect(Collectors.joining(" "));
    }

    private String buildSkuAttrText(Long skuId) {
        List<SkuAttrValue> attrs = skuAttrValueMapper.selectBySkuId(skuId);
        return attrs.stream()
                .map(v -> v.getAttrName() + " " + v.getAttrValue())
                .collect(Collectors.joining(" "));
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private Integer asInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    @Data
    public static class SearchProductDoc {
        private Long spuId;
        private Long skuId;
        private String spuTitle;
        private String skuName;
        private String brandName;
        private String categoryName;
        private String attrText;
        private String publishStatus;
        private Integer sales;
        private String createdAt;
    }
}
