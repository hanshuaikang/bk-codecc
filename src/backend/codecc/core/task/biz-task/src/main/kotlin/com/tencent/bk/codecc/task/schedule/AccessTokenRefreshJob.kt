package com.tencent.bk.codecc.task.schedule

import com.tencent.devops.common.auth.api.pojo.external.KEY_BACKEND_ACCESS_TOKEN
import com.tencent.devops.common.redis.lock.RedisLock
import com.tencent.devops.common.util.OkhttpUtils
import org.apache.commons.lang.StringUtils
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration // 1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling // 2.开启定时任务
class AccessTokenRefreshJob @Autowired constructor(
    private val redisTemplate: RedisTemplate<String, String>
) {

    @Value("\${auth.url:#{null}}")
    private var authUrl: String? = null

    companion object {
        private val logger = LoggerFactory.getLogger(AccessTokenRefreshJob::class.java)

        private val KEY_ACCESS_TOKEN_UPDATE_TIME = "ACCESS_TOKEN_UPDATE_TIME"

        private val TIME_OUT_MILLIS = 1000 * 60 * 5

        private val REFRESH_ACCESS_TOKEN_LOCK_KEY = "REFRESH_ACCESS_TOKEN"
    }

    // 3.添加定时任务
    @Scheduled(fixedRate = 1000 * 60 * 5)
    fun initAccessToken() {
    }

    private fun accessTokenIsTimeOut(currentTime: Long): Boolean {
        val accessTokenUpdateTime = redisTemplate.opsForValue().get(KEY_ACCESS_TOKEN_UPDATE_TIME)

        if (accessTokenUpdateTime != null && currentTime - accessTokenUpdateTime.toLong() < TIME_OUT_MILLIS) {
            return false
        } else {
            return true
        }
    }
}