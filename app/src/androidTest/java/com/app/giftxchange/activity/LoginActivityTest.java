package com.app.giftxchange.activity;

import static org.junit.Assert.*;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.app.giftxchange.ExampleInstrumentedTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends LoginActivity {

  /*  @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);
*/
    @Test
    public void testIsValidEmail_ValidEmail() {
        // Arrange
        LoginActivity loginActivity = new LoginActivity();

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            // Initialize your activity here if needed
            // This will ensure it runs on the main thread
        });
        // Act
        boolean isValid = loginActivity.isValidEmail("test@example.com");

        // Assert
        assertTrue(isValid);
    }

    @Test
    public void testIsValidEmail_InvalidEmail() {
        // Arrange
        LoginActivity loginActivity = new LoginActivity();

        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            // Initialize your activity here if needed
            // This will ensure it runs on the main thread
        });
        // Act
        boolean isValid = loginActivity.isValidEmail("invalid_email");

        // Assert
        assertFalse(isValid);
    }
}