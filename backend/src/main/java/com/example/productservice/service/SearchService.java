package com.example.productservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
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
import java.util.Locale;
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
            log.warn("Failed to initialize Elasticsearch index: {}", ex.getMessage());
        }
    }

    public void ensureIndex() {
        try {
            boolean exists = elasticsearchClient.indices().exists(r -> r.index(INDEX_NAME)).value();
            if (exists) {
                return;
            }
            elasticsearchClient.indices().create(req -> req.index(INDEX_NAME).withJson(new StringReader(buildIndexMapping())));
            operationLogService.record("SEARCH", 0L, "INDEX_CREATE", "system", "Created product search index with IK and pinyin analyzers");
        } catch (Exception ex) {
            throw new BizException("Failed to create Elasticsearch index: " + ex.getMessage());
        }
    }

    public void recreateIndex() {
        try {
            boolean exists = elasticsearchClient.indices().exists(r -> r.index(INDEX_NAME)).value();
            if (exists) {
                elasticsearchClient.indices().delete(r -> r.index(INDEX_NAME));
            }
            elasticsearchClient.indices().create(req -> req.index(INDEX_NAME).withJson(new StringReader(buildIndexMapping())));
            operationLogService.record("SEARCH", 0L, "INDEX_RECREATE", "system", "Recreated product search index");
        } catch (Exception ex) {
            throw new BizException("Failed to recreate Elasticsearch index: " + ex.getMessage());
        }
    }

    private String buildIndexMapping() {
        return """
                {
                  "settings": {
                    "index": {
                      "number_of_shards": 1,
                      "number_of_replicas": 0
                    },
                    "analysis": {
                      "filter": {
                        "product_synonym_graph": {
                          "type": "synonym_graph",
                          "synonyms_path": "analysis-ik/synonyms.txt",
                          "updateable": true
                        }
                      },
                      "tokenizer": {
                        "product_pinyin_tokenizer": {
                          "type": "pinyin",
                          "keep_first_letter": true,
                          "keep_separate_first_letter": false,
                          "keep_full_pinyin": true,
                          "keep_joined_full_pinyin": true,
                          "keep_original": true,
                          "limit_first_letter_length": 20,
                          "lowercase": true,
                          "remove_duplicated_term": true
                        }
                      },
                      "analyzer": {
                        "ik_index_analyzer": {
                          "type": "custom",
                          "tokenizer": "ik_max_word",
                          "filter": ["lowercase"]
                        },
                        "ik_search_analyzer": {
                          "type": "custom",
                          "tokenizer": "ik_smart",
                          "filter": ["lowercase", "product_synonym_graph"]
                        },
                        "pinyin_analyzer": {
                          "type": "custom",
                          "tokenizer": "product_pinyin_tokenizer",
                          "filter": ["lowercase"]
                        }
                      },
                      "normalizer": {
                        "lower_keyword": {
                          "type": "custom",
                          "filter": ["lowercase"]
                        }
                      }
                    }
                  },
                  "mappings": {
                    "properties": {
                      "spuId": { "type": "long" },
                      "skuId": { "type": "long" },
                      "skuCode": { "type": "keyword", "normalizer": "lower_keyword" },
                      "spuTitle": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer",
                        "fields": {
                          "keyword": { "type": "keyword", "ignore_above": 256, "normalizer": "lower_keyword" },
                          "pinyin": { "type": "text", "analyzer": "pinyin_analyzer", "search_analyzer": "pinyin_analyzer" }
                        }
                      },
                      "skuName": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer",
                        "fields": {
                          "keyword": { "type": "keyword", "ignore_above": 256, "normalizer": "lower_keyword" },
                          "pinyin": { "type": "text", "analyzer": "pinyin_analyzer", "search_analyzer": "pinyin_analyzer" }
                        }
                      },
                      "brandName": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer",
                        "fields": {
                          "keyword": { "type": "keyword", "ignore_above": 128, "normalizer": "lower_keyword" },
                          "pinyin": { "type": "text", "analyzer": "pinyin_analyzer", "search_analyzer": "pinyin_analyzer" }
                        }
                      },
                      "categoryName": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer",
                        "fields": {
                          "keyword": { "type": "keyword", "ignore_above": 128, "normalizer": "lower_keyword" },
                          "pinyin": { "type": "text", "analyzer": "pinyin_analyzer", "search_analyzer": "pinyin_analyzer" }
                        }
                      },
                      "description": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer"
                      },
                      "attrText": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer",
                        "fields": {
                          "pinyin": { "type": "text", "analyzer": "pinyin_analyzer", "search_analyzer": "pinyin_analyzer" }
                        }
                      },
                      "searchText": {
                        "type": "text",
                        "analyzer": "ik_index_analyzer",
                        "search_analyzer": "ik_search_analyzer",
                        "fields": {
                          "pinyin": { "type": "text", "analyzer": "pinyin_analyzer", "search_analyzer": "pinyin_analyzer" }
                        }
                      },
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
            throw new BizException("SPU not found: " + spuId);
        }
        Brand brand = brandMapper.selectById(spu.getBrandId());
        Category category = categoryMapper.selectById(spu.getCategoryId());
        if (brand == null || category == null) {
            throw new BizException("SPU brand or category does not exist, spuId=" + spuId);
        }

        ensureIndex();
        removeSpu(spuId);

        List<Sku> skuList = skuMapper.selectBySpuId(spuId);
        String spuAttrText = buildSpuAttrText(spuId);

        try {
            for (Sku sku : skuList) {
                String skuAttrText = buildSkuAttrText(sku.getId());
                String attrText = (spuAttrText + " " + skuAttrText).trim();
                SearchProductDoc doc = new SearchProductDoc();
                doc.setSpuId(spuId);
                doc.setSkuId(sku.getId());
                doc.setSkuCode(sku.getSkuCode());
                doc.setSpuTitle(spu.getTitle());
                doc.setSkuName(sku.getSkuName());
                doc.setBrandName(brand.getName());
                doc.setCategoryName(category.getName());
                doc.setDescription(spu.getDescription());
                doc.setAttrText(attrText);
                doc.setSearchText(String.join(" ", brand.getName(), category.getName(), spu.getTitle(), sku.getSkuName(),
                        Objects.toString(spu.getDescription(), ""), attrText));
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
            operationLogService.record("SEARCH", spuId, "INDEX_UPSERT", "system", "Updated SPU search documents");
        } catch (Exception ex) {
            throw new BizException("Failed to write Elasticsearch index: " + ex.getMessage());
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
            throw new BizException("Failed to remove Elasticsearch documents: " + ex.getMessage());
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
        String exactKeyword = keyword.toLowerCase(Locale.ROOT);

        try {
            SearchResponse<Map> response = elasticsearchClient.search(s -> s
                            .index(INDEX_NAME)
                            .from(from)
                            .size(safePageSize)
                            .query(query -> query.functionScore(fs -> fs
                                    .query(inner -> inner.bool(b -> {
                                        b.filter(filter -> filter.term(t -> t.field("publishStatus").value(PublishStatus.PUBLISHED.name())));
                                        if (brand != null && !brand.isBlank()) {
                                            b.filter(filter -> filter.term(t -> t.field("brandName.keyword").value(brand.toLowerCase(Locale.ROOT))));
                                        }
                                        if (category != null && !category.isBlank()) {
                                            b.filter(filter -> filter.term(t -> t.field("categoryName.keyword").value(category.toLowerCase(Locale.ROOT))));
                                        }
                                        if (keyword.isBlank()) {
                                            b.must(must -> must.matchAll(ma -> ma));
                                        } else {
                                            b.must(must -> must.bool(kb -> kb
                                                    .should(sh -> sh.term(t -> t.field("skuCode").value(exactKeyword).boost(30.0f)))
                                                    .should(sh -> sh.term(t -> t.field("brandName.keyword").value(exactKeyword).boost(18.0f)))
                                                    .should(sh -> sh.term(t -> t.field("categoryName.keyword").value(exactKeyword).boost(10.0f)))
                                                    .should(sh -> sh.term(t -> t.field("spuTitle.keyword").value(exactKeyword).boost(16.0f)))
                                                    .should(sh -> sh.term(t -> t.field("skuName.keyword").value(exactKeyword).boost(14.0f)))
                                                    .should(sh -> sh.matchPhrase(mp -> mp.field("spuTitle").query(keyword).slop(1).boost(12.0f)))
                                                    .should(sh -> sh.matchPhrase(mp -> mp.field("skuName").query(keyword).slop(1).boost(10.0f)))
                                                    .should(sh -> sh.multiMatch(mm -> mm
                                                            .query(keyword)
                                                            .fields("brandName^8", "spuTitle^6", "skuName^5", "categoryName^4", "attrText^2", "description^2", "searchText")
                                                            .type(TextQueryType.BestFields)
                                                            .boost(5.0f)))
                                                    .should(sh -> sh.multiMatch(mm -> mm
                                                            .query(keyword)
                                                            .fields("brandName.pinyin^5", "spuTitle.pinyin^4", "skuName.pinyin^4", "categoryName.pinyin^3", "attrText.pinyin^2", "searchText.pinyin")
                                                            .type(TextQueryType.BestFields)
                                                            .boost(3.0f)))
                                                    .should(sh -> sh.multiMatch(mm -> mm
                                                            .query(keyword)
                                                            .fields("spuTitle^2", "skuName^2", "searchText")
                                                            .fuzziness("AUTO")
                                                            .prefixLength(1)
                                                            .boost(1.2f)))
                                                    .minimumShouldMatch("1")));
                                        }
                                        return b;
                                    }))
                                    .functions(func -> {
                                        if (keyword.isBlank()) {
                                            return func.weight(1.0);
                                        }
                                        return func.filter(filter -> filter.term(t -> t.field("brandName.keyword").value(exactKeyword)))
                                                .weight(4.0);
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
                                    .fields("skuName", f -> f)
                                    .fields("attrText", f -> f)),
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
            throw new BizException("Search failed: " + ex.getMessage());
        }
    }

    public int reindexPublishedProducts() {
        recreateIndex();
        List<Spu> publishedSpu = spuMapper.selectByStatus(PublishStatus.PUBLISHED.name());
        for (Spu spu : publishedSpu) {
            indexSpu(spu.getId());
        }
        operationLogService.record("SEARCH", 0L, "REINDEX_ALL", "system", "Reindexed published SPU count: " + publishedSpu.size());
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
        private String skuCode;
        private String spuTitle;
        private String skuName;
        private String brandName;
        private String categoryName;
        private String description;
        private String attrText;
        private String searchText;
        private String publishStatus;
        private Integer sales;
        private String createdAt;
    }
}
