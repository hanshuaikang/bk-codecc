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

package com.tencent.devops.common.auth.api.external

import com.google.common.collect.Sets
import com.tencent.devops.common.auth.api.pojo.external.CodeCCAuthAction
import com.tencent.devops.common.auth.api.pojo.external.PipelineAuthAction
import com.tencent.devops.common.auth.api.service.AuthTaskService
import com.tencent.devops.common.auth.api.util.AuthActionConvertUtils
import com.tencent.devops.common.auth.api.util.AuthApiUtils
import com.tencent.devops.common.client.Client
import com.tencent.devops.common.constant.ComConstants
import com.tencent.devops.common.constant.RedisKeyConstants
import com.tencent.devops.common.service.utils.SpringContextUtil
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import kotlin.streams.toList

abstract class AbstractAuthExPermissionApi @Autowired constructor(
        val client: Client,
        val redisTemplate: RedisTemplate<String, String>
) : AuthExPermissionApi {

    /**
     * 校验用户是否是管理员
     */
    override fun isAdminMember(
            user: String
    ): Boolean {
        return AuthApiUtils.isAdminMember(redisTemplate, user)
    }

    override fun getAdminMembers(): List<String> {
        return AuthApiUtils.getAdminMember(redisTemplate)
    }

    /**
     * 校验用户是否是BG管理员
     */
    override fun isBgAdminMember(
        user: String,
        taskId: String,
        createFrom: String?
    ): Boolean {
        val authTaskService = SpringContextUtil.getBean(AuthTaskService::class.java)
        var createFromStr = createFrom ?: authTaskService.getTaskCreateFrom(taskId.toLong())
        if (ComConstants.DefectStatType.GONGFENG_SCAN.value() != createFromStr) {
            createFromStr = ComConstants.DefectStatType.USER.value()
        }
        val bgIdStr = redisTemplate.opsForHash<String, String>().get(RedisKeyConstants.KEY_TASK_BG_MAPPING, taskId)
        val bgId = if (null == bgIdStr || bgIdStr == "0" || bgIdStr == "-1") {
            authTaskService.getTaskBgId(taskId.toLong())
        } else {
            bgIdStr
        }

        logger.info("judge user is bg admin member: $user taskId: $taskId bgId: $bgId")
        val isMember = redisTemplate.opsForSet().isMember("${RedisKeyConstants.PREFIX_BG_ADMIN}$createFromStr:$user",
            bgId)
        return if (isMember == true) {
            logger.info("Is bg admin member: $user")
            true
        } else {
            logger.info("Not bg admin member: $user")
            false
        }
    }

    override fun authDefectOpsPermissions(
        taskId: Long,
        projectId: String,
        username: String,
        createFrom: String,
        actions: List<CodeCCAuthAction>
    ): Boolean {
        return checkPipelineDefectOpsPermissions(taskId, projectId, username, actions)
    }

    fun checkPipelineDefectOpsPermissions(
        taskId: Long, projectId: String, username: String,
        actions: List<CodeCCAuthAction>
    ): Boolean {
        // 判断来源是流水线还是CodeCC任务，如果是流水线校验流水线权限
        val authTaskService = SpringContextUtil.getBean(AuthTaskService::class.java)
        val createFromStr = authTaskService.getTaskCreateFrom(taskId)
        if (StringUtils.isBlank(createFromStr)) {
            return false
        }
        if (createFromStr == ComConstants.BsTaskCreateFrom.BS_PIPELINE.value()) {
            val pipelineActions = AuthActionConvertUtils.covert(actions).map { it.actionName }.toSet()
            val result = validatePipelineBatchPermission(
                username, taskId.toString(),
                projectId, pipelineActions
            )
            if (CollectionUtils.isEmpty(result)) {
                return false
            }
            for (actionResult in result) {
                if ((actionResult.isPass != null) && actionResult.isPass) {
                    return true
                }
            }
        } else if (createFromStr == ComConstants.BsTaskCreateFrom.BS_CODECC.value()) {
            val actionsStr = actions.stream().map { it.actionName }.toList().toSet()
            val result = validateTaskBatchPermission(
                username, taskId.toString(),
                projectId, actionsStr
            )
            if (CollectionUtils.isEmpty(result)) {
                return false
            }
            for (actionResult in result) {
                if ((actionResult.isPass != null) && actionResult.isPass) {
                    return true
                }
            }
        }
        return false
    }

    override fun checkProjectIsRbacPermissionByCache(projectId: String, needRefresh: Boolean?): Boolean {
        return false
    }

    override fun validateUserRulesetPermission(
        projectId: String,
        userId: String,
        action: String,
        resourceType: String
    ): Boolean {
        return false
    }

    override fun validateUserIgnoreTypePermission(
        projectId: String,
        userId: String,
        action: String,
        resourceType: String
    ): Boolean {
        return false
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AbstractAuthExPermissionApi::class.java)
    }
}
