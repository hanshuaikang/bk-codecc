package com.tencent.devops.common.auth.api.external

import com.tencent.devops.common.auth.api.pojo.external.CodeCCAuthAction
import com.tencent.devops.common.auth.api.pojo.external.model.BkAuthExResourceActionModel

interface AuthExPermissionApi {

    /**
     * 查询指定用户特定权限下的流水线清单
     */
    fun queryPipelineListForUser(
        user: String,
        projectId: String,
        actions: Set<String>
    ): Set<String>

    /**
     * 查询指定用户特定权限下的代码检查任务清单
     */
    fun queryTaskListForUser(
        user: String,
        projectId: String,
        actions: Set<String>
    ): Set<String>

    /**
     * 查询指定流水线下特定权限的用户清单
     */
    fun queryPipelineUserListForAction(
        taskId: String,
        projectId: String,
        actions: Set<String>
    ): List<String>

    /**
     * 查询指定代码检查任务下特定权限的用户清单
     */
    fun queryTaskUserListForAction(
        taskId: String,
        projectId: String,
        actions: Set<String>
    ): List<String>

    /**
     * 批量校验权限
     */
    fun validatePipelineBatchPermission(
        user: String,
        taskId: String,
        projectId: String,
        actions: Set<String>
    ): List<BkAuthExResourceActionModel>

    /**
     * 批量校验权限
     */
    fun validateTaskBatchPermission(
        user: String,
        taskId: String,
        projectId: String,
        actions: Set<String>
    ): List<BkAuthExResourceActionModel>

    /**
     * 校验用户是否是管理员
     */
    fun isAdminMember(
        user: String
    ): Boolean

    /**
     * 校验用户是否是管理员
     */
    fun getAdminMembers(
    ): List<String>

    /**
     * 校验用户是否是BG管理员
     */
    fun isBgAdminMember(
        user: String,
        taskId: String,
        createFrom: String?
    ): Boolean

    /**
     * 校验是否项目管理员
     *
     * @param projectId
     * @param user
     * @return
     */
    fun authProjectManager(projectId: String, user: String): Boolean

    /**
     * 校验是否项目角色
     */
    fun authProjectMultiManager(projectId: String, user: String): Boolean


    /**
     * 校验是否项目角色
     */
    fun authProjectRole(projectId: String, user: String, role: String?): Boolean


    fun authDefectOpsPermissions(taskId: Long, projectId: String, username: String, createFrom: String,
                                 actions: List<CodeCCAuthAction>): Boolean

    fun checkProjectIsRbacPermissionByCache(projectId: String, needRefresh: Boolean? = false): Boolean
}
