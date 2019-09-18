package com.furkanyurdakul.mymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.furkanyurdakul.mymarket.abstracts.BaseActivity;

/**
 * The splash screen that redirects to main screen or login screen based on the user
 * has logged in with "Remember me" option.
 */
public class SplashActivity extends BaseActivity
{
    public static final String REMEMBER_ME_KEY = "com.furkanyurdakul.mymarket.REMEMBER_ME_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Check if user has logged in successfully with "Remember me" option open.
        if (PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(REMEMBER_ME_KEY, false))
        {
            // Open main screen if condition is true.
            startActivity(new Intent(this, MainActivity.class));
        }
        else
        {   // Open login screen if condition is false.
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    @Override
    public void startActivity(Intent intent)
    {
        // Since the activities should be started with the same flags, just
        // override the function and add flags to intent here.
        super.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
