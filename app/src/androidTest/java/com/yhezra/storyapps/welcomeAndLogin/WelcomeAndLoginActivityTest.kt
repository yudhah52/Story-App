package com.yhezra.storyapps.welcomeAndLogin

import androidx.recyclerview.widget.RecyclerView
import com.yhezra.storyapps.R
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import androidx.test.platform.app.InstrumentationRegistry
import com.yhezra.storyapps.data.remote.utils.EspressoIdlingResource
import com.yhezra.storyapps.ui.main.MainActivity
import com.yhezra.storyapps.ui.welcome.WelcomeActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WelcomeAndLoginActivityTest {
    private val sampleName = "Siuuu"

    private val email = "siuuu@gmail.com"
    private val password = "siuuu123"

    private val wrongEmail = "yuddddddd@gmail.com"
    private val wrongPassword = "yuddddddddd"

    private val invalidEmail = "yudddddd"
    private val invalidPassword = "yud"

    @Before
    fun setUp() = runTest {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        ActivityScenario.launch(WelcomeActivity::class.java)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginSuccess() {
        Intents.init()

        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailEditText)).perform(typeText(email))
        onView(withId(R.id.emailEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(password))
        onView(withId(R.id.passwordEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
        // AlertDialog click
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.text("Selanjutnya")), 3000)
        val button = device.findObject(By.text("Selanjutnya"))
        button.click()
        Thread.sleep(2000)
        onView(withId(R.id.rv_user_story)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_user_story)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                10
            )
        )

        Intents.intended(hasComponent(MainActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun logoutSuccess() {
        Intents.init()
        Thread.sleep(2000)
        onView(withId(R.id.rv_user_story)).check(matches(isDisplayed()))
        onView(withId(R.id.action_logout)).perform(click())

        Intents.intended(hasComponent(WelcomeActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun welcomeComponentShowSuccess() {
        onView(withId(R.id.iv_welcome)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.descTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()))
    }

    @Test
    fun loginComponentShowSuccess() {
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.ivLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.emailTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.emailEditTextLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditTextLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun signUpComponentShowSuccess() {
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()))
        onView(withId(R.id.signupButton)).perform(click())
        onView(withId(R.id.ivSignup)).check(matches(isDisplayed()))
        onView(withId(R.id.titleTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.nameTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.nameEditTextLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.emailTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.emailEditTextLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditTextLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()))
    }

    @Test
    fun loginWrong() {
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailEditText)).perform(typeText(wrongEmail))
        onView(withId(R.id.emailEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(wrongPassword))
        onView(withId(R.id.passwordEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
    }

    @Test
    fun loginInvalid() {
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        onView(withId(R.id.loginButton)).perform(click())
        onView(withId(R.id.emailEditText)).perform(typeText(invalidEmail))
        onView(withId(R.id.emailEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(invalidPassword))
        onView(withId(R.id.passwordEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())
    }

    @Test
    fun signUpWrong() {
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()))
        onView(withId(R.id.signupButton)).perform(click())
        onView(withId(R.id.nameEditText)).perform(typeText(sampleName))
        onView(withId(R.id.nameEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.emailEditText)).perform(typeText(email))
        onView(withId(R.id.emailEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(password))
        onView(withId(R.id.passwordEditText)).perform(closeSoftKeyboard())
        onView(withId(R.id.signupButton)).perform(click())
    }
}