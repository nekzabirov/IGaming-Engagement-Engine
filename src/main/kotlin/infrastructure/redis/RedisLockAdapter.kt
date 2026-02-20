package com.nekgambling.infrastructure.redis

import com.nekgambling.application.adapter.ILockAdapter
import com.nekgambling.infrastructure.redis.config.RedisConfig
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.params.SetParams
import java.util.UUID

class RedisLockAdapter(config: RedisConfig) : ILockAdapter {

    private val pool: JedisPool = if (config.password != null) {
        JedisPool(JedisPoolConfig(), config.host, config.port, 2000, config.password)
    } else {
        JedisPool(JedisPoolConfig(), config.host, config.port)
    }

    companion object {
        private const val LOCK_PREFIX = "lock:"
        private const val LOCK_TTL_MS = 10_000L
        private const val RETRY_DELAY_MS = 50L
    }

    override suspend fun <T> withLock(key: String, block: suspend () -> T): T {
        val lockKey = "$LOCK_PREFIX$key"
        val lockValue = UUID.randomUUID().toString()

        try {
            acquire(lockKey, lockValue)
            return block()
        } finally {
            release(lockKey, lockValue)
        }
    }

    private fun acquire(lockKey: String, lockValue: String) {
        val deadline = System.currentTimeMillis() + LOCK_TTL_MS

        while (System.currentTimeMillis() < deadline) {
            pool.resource.use { jedis ->
                val result = jedis.set(lockKey, lockValue, SetParams().nx().px(LOCK_TTL_MS))
                if (result == "OK") return
            }
            Thread.sleep(RETRY_DELAY_MS)
        }

        throw IllegalStateException("Failed to acquire lock for key: $lockKey")
    }

    private fun release(lockKey: String, lockValue: String) {
        val script = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
        """.trimIndent()

        pool.resource.use { jedis ->
            jedis.eval(script, listOf(lockKey), listOf(lockValue))
        }
    }
}
