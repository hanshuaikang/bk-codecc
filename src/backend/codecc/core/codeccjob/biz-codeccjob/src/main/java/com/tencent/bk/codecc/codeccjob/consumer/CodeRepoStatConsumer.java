/*
 * Tencent is pleased to support the open source community by making BlueKing available.
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the MIT License (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * http://opensource.org/licenses/MIT
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.bk.codecc.codeccjob.consumer;

import static com.tencent.devops.common.auth.api.pojo.external.AuthExConstantsKt.KEY_CREATE_FROM;
import static com.tencent.devops.common.auth.api.pojo.external.AuthExConstantsKt.KEY_PROJECT_ID;
import static com.tencent.devops.common.auth.api.pojo.external.AuthExConstantsKt.PREFIX_TASK_INFO;
import static com.tencent.devops.common.web.mq.ConstantsKt.EXCHANGE_CODE_REPO_STAT;
import static com.tencent.devops.common.web.mq.ConstantsKt.QUEUE_CODE_REPO_STAT;
import static com.tencent.devops.common.web.mq.ConstantsKt.ROUTE_CODE_REPO_STAT;

import com.google.common.collect.Sets;
import com.tencent.bk.codecc.codeccjob.dao.defect.mongorepository.CodeRepoInfoRepository;
import com.tencent.bk.codecc.codeccjob.dao.defect.mongorepository.CodeRepoStatRepository;
import com.tencent.bk.codecc.defect.model.CodeRepoStatisticEntity;
import com.tencent.bk.codecc.defect.model.incremental.CodeRepoEntity;
import com.tencent.bk.codecc.defect.model.incremental.CodeRepoInfoEntity;
import com.tencent.bk.codecc.defect.vo.UploadTaskLogStepVO;
import com.tencent.bk.codecc.task.api.ServiceTaskRestResource;
import com.tencent.bk.codecc.task.vo.TaskDetailVO;
import com.tencent.devops.common.client.Client;
import com.tencent.devops.common.constant.ComConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 代码库统计消息消费类
 *
 * @version V1.0
 * @date 2021/2/22
 */

@Component
@Slf4j
public class CodeRepoStatConsumer {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CodeRepoInfoRepository codeRepoInfoRepository;
    @Autowired
    private CodeRepoStatRepository codeRepoStatRepository;
    @Autowired
    private Client client;


    /**
     * 处理代码库分支统计消息消费者
     *
     * @param uploadTaskLogStepVO vo
     */
    @RabbitListener(bindings = @QueueBinding(key = ROUTE_CODE_REPO_STAT, value = @Queue(value = QUEUE_CODE_REPO_STAT),
            exchange = @Exchange(value = EXCHANGE_CODE_REPO_STAT, durable = "false")))
    public void consumer(UploadTaskLogStepVO uploadTaskLogStepVO) {
        try {
            businessCore(uploadTaskLogStepVO);
        } catch (Throwable t) {
            log.error("CodeRepoStatConsumer error, mq obj: {}", uploadTaskLogStepVO, t);
        }
    }

    private void businessCore(UploadTaskLogStepVO uploadTaskLogStepVO) {
        if (null == uploadTaskLogStepVO) {
            log.error("CodeRepoStatConsumer uploadTaskLogStepVO is null.");
            return;
        }
        long taskId = uploadTaskLogStepVO.getTaskId();
        String buildId = uploadTaskLogStepVO.getPipelineBuildId();
        String projectId = uploadTaskLogStepVO.getProjectId();
        if (StringUtils.isBlank(projectId)) {
            projectId = (String) redisTemplate.opsForHash().get(PREFIX_TASK_INFO + taskId, KEY_PROJECT_ID);
            if (StringUtils.isBlank(projectId)) {
                TaskDetailVO taskDetail =
                        client.get(ServiceTaskRestResource.class).getTaskInfoWithoutToolsByTaskId(taskId).getData();
                if (null == taskDetail) {
                    log.error("CodeRepoStatConsumer taskDetail is null. task_id: {}", taskId);
                    return;
                }
                projectId = taskDetail.getProjectId();
                redisTemplate.opsForHash().put(PREFIX_TASK_INFO + taskId, KEY_PROJECT_ID, projectId);
            }
        }
        String taskCreateFrom = (String) redisTemplate.opsForHash().get(PREFIX_TASK_INFO + taskId, KEY_CREATE_FROM);
        String createFrom = ComConstants.DefectStatType.getDataFromByProjectId(taskCreateFrom, projectId);

        CodeRepoInfoEntity codeRepoInfoEntity = codeRepoInfoRepository.findFirstByTaskIdAndBuildId(taskId, buildId);
        if (codeRepoInfoEntity != null) {
            List<CodeRepoEntity> repoList = codeRepoInfoEntity.getRepoList();
            if (CollectionUtils.isEmpty(repoList)) {
                log.warn("CodeRepoStatConsumer codeRepoInfoEntity repoList is empty! task_id: {}, {}", taskId, buildId);
                return;
            }

            for (CodeRepoEntity codeRepoEntity : repoList) {
                String url = codeRepoEntity.getUrl();
                String branch = codeRepoEntity.getBranch();
                if (StringUtils.isBlank(url) || StringUtils.isBlank(branch)) {
                    log.warn("CodeRepoStatConsumer code repo info is null: taskId:{} buildId:{}", taskId, buildId);
                    continue;
                }

                CodeRepoStatisticEntity statisticEntity =
                        codeRepoStatRepository.findFirstByDataFromAndUrlAndBranch(createFrom, url, branch);
                if (null == statisticEntity) {
                    // 如果没有就新建一个
                    statisticEntity = new CodeRepoStatisticEntity();
                    // 检查是否有记录过该代码库
                    CodeRepoStatisticEntity entity = codeRepoStatRepository.findFirstByDataFromAndUrl(createFrom, url);
                    if (null != entity) {
                        // 有则同步首次分析时间
                        statisticEntity.setUrlFirstScan(entity.getUrlFirstScan());
                    } else {
                        statisticEntity.setUrlFirstScan(codeRepoInfoEntity.getCreatedDate());
                    }

                    statisticEntity.setUrl(url);
                    statisticEntity.setBranch(branch);
                    statisticEntity.setDataFrom(createFrom);
                    statisticEntity.setBranchFirstScan(codeRepoInfoEntity.getCreatedDate());
                }
                statisticEntity.setBranchLastScan(codeRepoInfoEntity.getUpdatedDate());

                // 增加任务id字段记录关联的代码库信息
                Set<Long> taskIdSet = statisticEntity.getTaskIdSet();
                if (null == taskIdSet) {
                    taskIdSet = Sets.newHashSet();
                }
                taskIdSet.add(taskId);
                statisticEntity.setTaskIdSet(taskIdSet);

                codeRepoStatRepository.save(statisticEntity);
            }
        }
    }

}
