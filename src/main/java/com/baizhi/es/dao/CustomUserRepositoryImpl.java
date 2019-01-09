package com.baizhi.es.dao;

import com.baizhi.entity.User;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author gaozhy
 * @date 2019/1/1.23:11
 */
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @Autowired
    private ElasticsearchTemplate template;

    /**
     * ====================================
     * {
     * "query": {
     * "match_all": {}
     * },
     * "from":1,     //从第几条开始    (从0开始)
     * "size":1      //大小
     * }
     * ====================================
     *
     * @param nowPage
     * @param pageSize
     * @return
     */
    @Override
    public List<User> findByPageable(int nowPage, int pageSize) {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withPageable(new PageRequest((nowPage - 1) * pageSize, pageSize))
                .build();

        return template.queryForList(query, User.class);
    }

    /**
     * @param field
     * @return
     */
    @Override
    public List<User> findByFieldDesc(String field) {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSort(SortBuilders.fieldSort(field).order(SortOrder.DESC))
                .build();
        return template.queryForList(query, User.class);
    }

    /**
     * 高亮
     *
     * @param realName
     * @return
     */
    @Override
    public List<User> findByRealNameLikeAndHighLight(String realName) {
        SearchQuery query = new NativeSearchQueryBuilder()
                //.withQuery(matchQuery("realname", realName))
                .withQuery(matchAllQuery())
                .withHighlightFields(new HighlightBuilder.Field("realname"))
                .withPageable(new PageRequest(0,5))
                .build();
        AggregatedPage<User> users = template.queryForPage(query, User.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                ArrayList<User> users = new ArrayList<>();
                SearchHits searchHits = searchResponse.getHits();
                for (SearchHit searchHit : searchHits) {
                    if (searchHits.getHits().length <= 0) {
                        return null;
                    }
                    User user = new User();
                    user.setId(searchHit.getId());
                    // searchHit.getSourceAsMap().forEach((k, v) -> System.out.println(k + " " + v));
                    user.setName(searchHit.getSourceAsMap().get("name").toString());
                    user.setAddress(searchHit.getSourceAsMap().get("address").toString());
                    user.setAge(Integer.parseInt(searchHit.getSourceAsMap().get("age").toString()));
                    user.setBirthday(new Date(Long.parseLong(searchHit.getSourceAsMap().get("birthday").toString())));
                    user.setSalary(Double.parseDouble(searchHit.getSourceAsMap().get("salary").toString()));
                    //String realname = searchHit.getHighlightFields().get("realname").fragments()[0].toString();
                    //user.setRealname(realname);

                    users.add(user);
                }
                return new AggregatedPageImpl<>((List<T>) users,pageable,searchResponse.getHits().getTotalHits());
            }
        });

        System.out.println("总页数："+users.getTotalPages());
        return users.getContent();
    }

    @Override
    public List<User> findByNameWithTermFilter(String... terms) {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withFilter(termsQuery("name",terms))
                .build();
        System.out.println(query.getFilter());
        return template.queryForList(query,User.class);
    }

    @Override
    public List<User> findByAgeWithRangeFilter(int start, int end) {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withFilter(rangeQuery("age").gte(start).lte(end))
                .build();
        System.out.println(query.getQuery());
        System.out.println(query.getFilter());
        return template.queryForList(query,User.class);
    }

    @Override
    public Map<String, Aggregation> findByNameStartingWithAndAggregations(String prefixName) {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(prefixQuery("name",prefixName))
                // 度量聚合结果的别名
                .addAggregation(AggregationBuilders.avg("result").field("age"))
                .build();
        Aggregations aggregations = template.query(query, new ResultsExtractor<Aggregations>() {
            @Override
            public Aggregations extract(SearchResponse searchResponse) {
                Aggregations aggregations = searchResponse.getAggregations();
                return aggregations;
            }
        });
        Map<String, Aggregation> map = aggregations.getAsMap();
        return map;
    }

    @Override
    public Map aggregationsWithHistogramAndMax() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .addAggregation(AggregationBuilders.histogram("result").field("age").interval(5)
                        .subAggregation(AggregationBuilders.max("max_salary").field("salary")))
                .build();
        Aggregations aggregations = template.query(query, new ResultsExtractor<Aggregations>() {

            @Override
            public Aggregations extract(SearchResponse searchResponse) {
                return searchResponse.getAggregations();
            }
        });
        return aggregations.getAsMap();
    }

    @Override
    public Map aggregationsWithDateHistogram() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .addAggregation(AggregationBuilders.dateHistogram("result").field("birthday").format("yyyy-MM-dd").dateHistogramInterval(DateHistogramInterval.YEAR))
                .build();
        Aggregations aggregations = template.query(query, new ResultsExtractor<Aggregations>() {

            @Override
            public Aggregations extract(SearchResponse searchResponse) {
                return searchResponse.getAggregations();
            }
        });
        return aggregations.getAsMap();
    }
}
