package org.opensignalfoundation.iranvpn.fallback

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.opensignalfoundation.iranvpn.model.PathKind
import org.opensignalfoundation.iranvpn.model.ServerList

/**
 * Kotlin implementation of fallback engine logic (mirrors core FallbackEngine).
 * Tries paths in order: Psiphon -> Conduit -> Xray -> Rostam.
 */
class FallbackOrchestrator(
    private val serverList: ServerList,
    private val pathRunners: Map<PathKind, PathRunner>,
) {
    private val _state = MutableStateFlow(FallbackState())
    val state: Flow<FallbackState> = _state.asStateFlow()

    private val _events = MutableStateFlow<FallbackEvent?>(null)
    val events: Flow<FallbackEvent?> = _events.asStateFlow()

    private val defaultOrder = listOf(PathKind.PSIPHON, PathKind.CONDUIT, PathKind.XRAY, PathKind.ROSTAM)

    private fun orderedIndices(): List<Int> {
        return serverList.paths.indices.sortedBy { i ->
            val kind = serverList.paths.getOrNull(i)?.kind()
            defaultOrder.indexOf(kind).let { if (it < 0) Int.MAX_VALUE else it }
        }
    }

    suspend fun run(): Result<PathKind> {
        val indices = orderedIndices()
        if (indices.isEmpty()) {
            _events.value = FallbackEvent.AllPathsFailed
            return Result.failure(Exception("no paths configured"))
        }

        var failureCount = 0
        for (idx in indices) {
            val path = serverList.paths.getOrNull(idx) ?: continue
            val kind = path.kind()
            val runner = pathRunners[kind] ?: continue

            _state.value = _state.value.copy(
                activePathIndex = idx,
                activePathKind = kind,
                connectionEstablished = false,
            )
            _events.value = FallbackEvent.Connecting(pathKind = kind, index = idx)

            val result = runner.connect(path)
            when (result) {
                is PathResult.Success -> {
                    _state.value = _state.value.copy(connectionEstablished = true, failureCount = 0)
                    _events.value = FallbackEvent.Connected(pathKind = kind, index = idx)
                    return Result.success(kind)
                }
                is PathResult.Failure -> {
                    _events.value = FallbackEvent.PathFailed(pathKind = kind, index = idx, error = result.error)
                    failureCount++
                }
            }
        }

        _state.value = _state.value.copy(
            activePathIndex = null,
            activePathKind = null,
            connectionEstablished = false,
            failureCount = failureCount,
        )
        _events.value = FallbackEvent.AllPathsFailed
        return Result.failure(Exception("all paths failed"))
    }

    suspend fun disconnect(runner: PathRunner?) {
        runner?.disconnect()
        _state.value = FallbackState()
    }
}

data class FallbackState(
    val activePathIndex: Int? = null,
    val activePathKind: PathKind? = null,
    val connectionEstablished: Boolean = false,
    val failureCount: Int = 0,
)

sealed class FallbackEvent {
    data class Connecting(val pathKind: PathKind, val index: Int) : FallbackEvent()
    data class Connected(val pathKind: PathKind, val index: Int) : FallbackEvent()
    data class PathFailed(val pathKind: PathKind, val index: Int, val error: String) : FallbackEvent()
    object AllPathsFailed : FallbackEvent()
}

sealed class PathResult {
    object Success : PathResult()
    data class Failure(val error: String) : PathResult()
}
