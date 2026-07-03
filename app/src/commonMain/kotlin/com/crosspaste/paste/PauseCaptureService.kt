package com.crosspaste.paste

import com.crosspaste.config.CommonConfigManager
import com.crosspaste.utils.DateUtils
import com.crosspaste.utils.ioDispatcher
import com.crosspaste.utils.namedScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration

sealed interface PauseCaptureState {
    data object Off : PauseCaptureState

    data class Until(
        val resumeAtEpochMillis: Long,
    ) : PauseCaptureState

    data object UntilResumed : PauseCaptureState
}

/**
 * Temporarily pauses clipboard capture without touching the persisted
 * enablePasteboardListening setting, so a pause never survives a restart the
 * way a forgotten settings toggle would. Timed pauses resume automatically.
 */
class PauseCaptureService(
    private val configManager: CommonConfigManager,
    private val pasteboardService: PasteboardService,
    scope: CoroutineScope? = null,
) {
    private val serviceScope = scope ?: namedScope(ioDispatcher, "PauseCaptureService")

    private val _state = MutableStateFlow<PauseCaptureState>(PauseCaptureState.Off)

    val state: StateFlow<PauseCaptureState> = _state.asStateFlow()

    private var resumeJob: Job? = null

    init {
        // If the user flips the listening setting back on while paused, the
        // monitor is running again: clear the pause so state and UI agree.
        // drop(1) skips the subscription-time snapshot so only real changes
        // count — otherwise the initial emission could race a fresh pause.
        serviceScope.launch {
            configManager.config
                .map { it.enablePasteboardListening }
                .distinctUntilChanged()
                .drop(1)
                .collect { enabled ->
                    if (enabled && _state.value != PauseCaptureState.Off) {
                        resumeJob?.cancel()
                        resumeJob = null
                        _state.value = PauseCaptureState.Off
                    }
                }
        }
    }

    fun pauseFor(duration: Duration) {
        pause(
            PauseCaptureState.Until(
                DateUtils.nowEpochMilliseconds() + duration.inWholeMilliseconds,
            ),
        )
        resumeJob =
            serviceScope.launch {
                delay(duration)
                resume()
            }
    }

    fun pauseUntilResumed() {
        pause(PauseCaptureState.UntilResumed)
    }

    fun resume() {
        resumeJob?.cancel()
        resumeJob = null
        if (_state.value == PauseCaptureState.Off) return
        _state.value = PauseCaptureState.Off
        if (configManager.getCurrentConfig().enablePasteboardListening) {
            pasteboardService.start()
        }
    }

    private fun pause(newState: PauseCaptureState) {
        resumeJob?.cancel()
        resumeJob = null
        if (_state.value == PauseCaptureState.Off &&
            configManager.getCurrentConfig().enablePasteboardListening
        ) {
            pasteboardService.stop()
        }
        _state.value = newState
    }
}
