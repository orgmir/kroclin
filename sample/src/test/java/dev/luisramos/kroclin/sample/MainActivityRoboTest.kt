package dev.luisramos.kroclin.sample

import android.view.LayoutInflater
import androidx.test.platform.app.InstrumentationRegistry
import dev.luisramos.kroclin.snapshot.android.assertSnapshot
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityRoboTest {

	@Test
	fun testViewSnapshot() {
		val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
		val view = LayoutInflater.from(context)
			.inflate(R.layout.view_card, null, false)
		view.assertSnapshot()
	}
}