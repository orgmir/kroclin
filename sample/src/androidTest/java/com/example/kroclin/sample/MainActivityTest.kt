package com.example.kroclin.sample

import android.view.View
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.kroclin.sample.MainActivity
import dev.luisramos.kroclin.snapshot.android.assertSnapshot
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

	@get:Rule
	val rule = ActivityScenarioRule(MainActivity::class.java)

	@Test
	fun testLayout() {
		// TODO test fails since we can't write files to the test app (where this code will run)
		//  the usual way around this is to write the files to the application being tested,
		//  then (via a plugin) pull those snapshots out into the project folder
		rule.scenario.onActivity { activity ->
			val mainView = activity.findViewById<View>(android.R.id.content)
			mainView.assertSnapshot()
		}
	}
}