/*
 * Tencent is pleased to support the open source community by making BK-CODECC 蓝鲸代码检查平台 available.
 *
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-CODECC 蓝鲸代码检查平台 is licensed under the MIT license.
 *
 * A copy of the MIT License is included in this file.
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tencent.bk.codecc.codeccjob.dao.mongorepository;

import com.tencent.bk.codecc.defect.model.defect.LintDefectV2Entity;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 查询分析记录持久层代码
 *
 * @version V1.0
 * @date 2019/5/5
 */
@Repository
public interface LintDefectV2Repository extends MongoRepository<LintDefectV2Entity, String> {

    @Query(fields = "{'author':1}", value = "{'task_id': ?0, 'tool_name': ?1, 'status': ?2, 'author': {'$in': ?3}}")
    List<LintDefectV2Entity> findDefectsNeedTransferAuthor(
            long taskId,
            String toolName,
            int status,
            Set<String> authorSet
    );

    @Query(
            fields = "{'checker':1, 'status': 1, 'exclude_time': 1}",
            value = "{'task_id': ?0, 'tool_name': ?1, 'status': {'$nin': ?2}, 'checker': {'$in': ?3}}"
    )
    List<LintDefectV2Entity> findDefectsByCheckers(
            long taskId,
            String toolName,
            Set<Integer> statusSet,
            Set<String> checkerSet
    );

    List<LintDefectV2Entity> findByTaskIdAndStatus(long taskId, int status);

    List<LintDefectV2Entity> findByTaskIdAndToolNameAndStatus(Long taskId, String toolName, int status);

    List<LintDefectV2Entity> findByTaskIdAndToolNameAndStatusAndSeverityIn(
            Long taskId,
            String toolName,
            int status,
            Set<Integer> severity
    );

    List<LintDefectV2Entity> findByEntityIdIn(Collection<String> ids);
}

