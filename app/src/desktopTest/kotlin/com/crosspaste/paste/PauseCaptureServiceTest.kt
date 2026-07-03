package com.crosspaste.paste

import com.crosspaste.config.TestAppConfig
import com.crosspaste.config.TestConfigManager
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class PauseCaptureServiceTest {

    @Test
    fun `pauseFor stops capture and auto-resumes after the duration`() =
        runTest {
            val configManager = TestConfigManager(TestAppConfig(enablePasteboardListening = true))
            val pasteboardService = mockk<PasteboardService>(relaxed = true)
            val service = PauseCaptureService(configManager, pasteboardService, backgroundScope)

            service.pauseFor(1.minutes)
            runCurrent()

            verify(exactly = 1) { pasteboardService.stop() }
            assertIs<PauseCaptureState.Until>(service.state.value)

            advanceTimeBy(2.minutes)
            runCurrent()

            verify(exactly = 1) { pasteboardService.start() }
            assertEquals(PauseCaptureState.Off, service.state.value)
        }

    @Test
    fun `pauseUntilResumed stays paused until resume is called`() =
        runTest {
            val configManager = TestConfigManager(TestAppConfig(enablePasteboardListening = true))
            val pasteboardService = mockk<PasteboardService>(relaxed = true)
            val service = PauseCaptureService(configManager, pasteboardService, backgroundScope)

            service.pauseUntilResumed()
            runCurrent()

            advanceTimeBy(1.hours)
            runCurrent()

            verify(exactly = 0) { pasteboardService.start() }
            assertEquals(PauseCaptureState.UntilResumed, service.state.value)

            service.resume()
            verify(exactly = 1) { pasteboardService.start() }
            assertEquals(PauseCaptureState.Off, service.state.value)
        }

    @Test
    fun `resume without a pause is a no-op`() =
        runTest {
            val configManager = TestConfigManager(TestAppConfig(enablePasteboardListening = true))
            val pasteboardService = mockk<PasteboardService>(relaxed = true)
            val service = PauseCaptureService(configManager, pasteboardService, backgroundScope)

            service.resume()

            verify(exactly = 0) { pasteboardService.start() }
            assertEquals(PauseCaptureState.Off, service.state.value)
        }

    @Test
    fun `re-enabling listening in settings clears an active pause`() =
        runTest {
            val configManager = TestConfigManager(TestAppConfig(enablePasteboardListening = true))
            val pasteboardService = mockk<PasteboardService>(relaxed = true)
            val service = PauseCaptureService(configManager, pasteboardService, backgroundScope)

            service.pauseUntilResumed()
            runCurrent()
            assertEquals(PauseCaptureState.UntilResumed, service.state.value)

            // The user turns listening off and back on in settings: the monitor
            // is running again, so the pause state must clear to match.
            configManager.updateConfig("enablePasteboardListening", false)
            runCurrent()
            configManager.updateConfig("enablePasteboardListening", true)
            runCurrent()

            assertEquals(PauseCaptureState.Off, service.state.value)
        }

    @Test
    fun `pause and resume do not touch a monitor that is disabled in settings`() =
        runTest {
            val configManager = TestConfigManager(TestAppConfig(enablePasteboardListening = false))
            val pasteboardService = mockk<PasteboardService>(relaxed = true)
            val service = PauseCaptureService(configManager, pasteboardService, backgroundScope)

            service.pauseUntilResumed()
            runCurrent()
            verify(exactly = 0) { pasteboardService.stop() }

            service.resume()
            verify(exactly = 0) { pasteboardService.start() }
        }
}
