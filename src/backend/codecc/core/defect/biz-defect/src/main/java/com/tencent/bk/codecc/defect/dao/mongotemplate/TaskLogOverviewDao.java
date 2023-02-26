package com.tencent.bk.codecc.defect.dao.mongotemplate;

import com.google.common.collect.Lists;
import com.tencent.bk.codecc.defect.model.TaskLogOverviewEntity;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class TaskLogOverviewDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<TaskLogOverviewEntity> getTaskLogOverviewList(Long taskId, Pageable pageable) {

        Query query = new Query();
        query.addCriteria(Criteria.where("task_id").is(taskId));
        query.with(pageable);
        return mongoTemplate.find(query, TaskLogOverviewEntity.class);
    }

    /**
     * 根据TaskId与BuildId查询
     *
     * @param taskId
     * @param buildId
     * @return
     */
    public TaskLogOverviewEntity findOneByTaskIdAndBuildId(Long taskId, String buildId) {
        Query query = Query.query(Criteria.where("task_id").is(taskId)
                .and("build_id").is(buildId));
        return mongoTemplate.findOne(query, TaskLogOverviewEntity.class);
    }

    /**
     * 根据TaskIds与BuildId查询
     *
     * @param taskIds
     * @param buildId
     * @return
     */
    public List<TaskLogOverviewEntity> findByTaskIdsAndBuildId(List<Long> taskIds, String buildId) {
        Query query = Query.query(Criteria.where("task_id").in(taskIds)
                .and("build_id").is(buildId));
        return mongoTemplate.find(query, TaskLogOverviewEntity.class);
    }

    /**
     * 更改状态
     *
     * @param taskId
     * @param buildId
     * @return
     */
    public void updateStatus(Long taskId, String buildId, Integer status) {
        Query query = Query.query(Criteria.where("task_id").is(taskId)
                .and("build_id").is(buildId));
        Update update = Update.update("status", status);
        mongoTemplate.updateFirst(query, update, TaskLogOverviewEntity.class);
    }

    /**
     * 查询任务维度的分析次数
     *
     * @param taskIds   任务ID集合
     * @param status    分析状态
     * @param startTime 开始范围
     * @param endTime   结束范围
     * @return long
     */
    public Long queryTaskAnalyzeCount(Collection<Long> taskIds, Integer status, Long startTime, Long endTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("task_id").in(taskIds));
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
        if (startTime != null) {
            query.addCriteria(Criteria.where("start_time").gte(startTime).lte(endTime));
        }

        return mongoTemplate.count(query, TaskLogOverviewEntity.class);
    }

    /**
     * 按任务ID分组获取时间范围内的build id
     *
     * @param taskIds   任务ID集合
     * @param status    分析状态
     * @param startTime 开始范围
     * @param endTime   结束范围
     * @return list
     */
    @Deprecated
    public List<TaskLogOverviewEntity> findBuildIdsByStartTime(Collection<Long> taskIds, Integer status,
                                                               Long startTime, Long endTime) {
        MatchOperation match = getIndexMatchOpera(taskIds, status, startTime, endTime);

        // 以开始时间倒序
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "start_time");

        // 以任务ID进行分组
        GroupOperation group = Aggregation.group("task_id")
                .first("task_id").as("task_id")
                .first("build_id").as("build_id");

        // 允许磁盘操作(支持较大数据集合的处理)
        AggregationOptions options = new AggregationOptions.Builder().allowDiskUse(true).build();
        Aggregation agg = Aggregation.newAggregation(match, sort, group).withOptions(options);

        AggregationResults<TaskLogOverviewEntity> queryResult =
                mongoTemplate.aggregate(agg, "t_task_log_overview", TaskLogOverviewEntity.class);
        return queryResult.getMappedResults();
    }

    /**
     * 按任务ID获取时间范围内的最新分析状态
     *
     * @param taskIds 任务ID集合
     * @param buildIds 构建ID集合
     * @param status  分析状态
     * @return list
     */
    public List<TaskLogOverviewEntity> findLatestAnalyzeStatus(Collection<Long> taskIds, Collection<String> buildIds,
            Integer status) {
        List<Criteria> criteriaList = Lists.newArrayList();

        // 指定任务ID集合
        if (CollectionUtils.isNotEmpty(taskIds)) {
            criteriaList.add(Criteria.where("task_id").in(taskIds));
        }

        if (CollectionUtils.isNotEmpty(buildIds)) {
            criteriaList.add(Criteria.where("build_id").in(buildIds));
        }

        // 筛选分析状态 enum ScanStatus
        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        Criteria criteria = new Criteria();
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        MatchOperation match = Aggregation.match(criteria);
        // 根据开始时间倒序
//        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "start_time");
        // 以任务ID进行分组
        GroupOperation group = Aggregation.group("task_id")
                .first("task_id").as("task_id")
                .first("build_id").as("build_id")
                .first("build_num").as("build_num")
                .first("start_time").as("start_time")
                .first("end_time").as("end_time")
                .first("status").as("status");

        // 允许磁盘操作(支持较大数据集合的处理)
        AggregationOptions options = new AggregationOptions.Builder().allowDiskUse(true).build();
        Aggregation agg = Aggregation.newAggregation(match, group).withOptions(options);
        AggregationResults<TaskLogOverviewEntity> queryResult =
                mongoTemplate.aggregate(agg, "t_task_log_overview", TaskLogOverviewEntity.class);
        return queryResult.getMappedResults();
    }

    public List<TaskLogOverviewEntity> findByTaskIdAndStatusOrderByEndTimeDescLimit(
            long taskId, int status, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("task_id").is(taskId).and("status").is(status));
        query.with(Sort.by(Sort.Direction.DESC, "end_time"));
        query.limit(limit);
        return mongoTemplate.find(query, TaskLogOverviewEntity.class);
    }

    public List<TaskLogOverviewEntity> findLatestTaskLog(Collection<Long> taskIds) {
        Criteria criteria = Criteria.where("task_id").in(taskIds);
        MatchOperation match = Aggregation.match(criteria);
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "start_time");
        GroupOperation group = Aggregation.group("task_id")
                .first("task_id").as("task_id")
                .first("build_id").as("build_id");
        Aggregation agg = Aggregation.newAggregation(match, sort, group);
        AggregationResults<TaskLogOverviewEntity> queryResult =
                mongoTemplate.aggregate(agg, "t_task_log_overview", TaskLogOverviewEntity.class);
        return queryResult.getMappedResults();
    }

    /**
     * 组装索引筛选条件
     *
     * @return match
     */
    @NotNull
    private MatchOperation getIndexMatchOpera(Collection<Long> taskIds, Integer status, Long startTime, Long endTime) {
        List<Criteria> criteriaList = Lists.newArrayList();

        // 指定任务ID集合
        if (CollectionUtils.isNotEmpty(taskIds)) {
            criteriaList.add(Criteria.where("task_id").in(taskIds));
        }

        // 筛选分析状态 enum ScanStatus
        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        }

        // 筛选执行时间
        if (startTime != null && endTime != null) {
            criteriaList.add(Criteria.where("start_time").gte(startTime).lte(endTime));
        }

        Criteria criteria = new Criteria();
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }

        return Aggregation.match(criteria);
    }
}
