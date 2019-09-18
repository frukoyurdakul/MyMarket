package com.furkanyurdakul.mymarket.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.furkanyurdakul.mymarket.R;
import com.furkanyurdakul.mymarket.abstracts.BaseActivity;

import static com.furkanyurdakul.mymarket.activities.SplashActivity.REMEMBER_ME_KEY;

/**
 * Contains login screen actions.
 */
public class LoginActivity extends BaseActivity
{
    public static final String REQUIRED_USERNAME = "kariyer";
    public static final String REQUIRED_PASSWORD = "2019ADev";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews()
    {
        // No class variable is required here as we can process
        // all the elements here.

        EditText userNameEditText = findViewById(R.id.userNameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        SwitchCompat rememberMeSwitch = findViewById(R.id.rememberMeSwitch);
        View loginButton = findViewById(R.id.loginButton);

        // The button that logs in the user to main screen.

        loginButton.setOnClickListener(v ->
        {
            // Check edit texts first. Shows errors if one of the fields are empty or
            // they're simply entered wrongly.

            String userName = userNameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (userName.length() == 0 || password.length() == 0)
                showToast("Kullanıcı adı veya şifre boş olmamalıdır.");
            else if (!REQUIRED_USERNAME.equals(userName) || !REQUIRED_PASSWORD.equals(password))
                showToast("Kullanıcı adı veya şifre yanlış.");
            else
            {
                // Log in should be successful. Redirect to main page.
                showToast("Giriş başarılı.");

                // This code should close the soft keyboard since it is focused on an edit text.
                if (getCurrentFocus() != null)
                    getCurrentFocus().clearFocus();

                // Set the "remember me" key with the switch value. If true, the app
                // will redirect to main screen directly once it opens back.
                PreferenceManager.getDefaultSharedPreferences(this)
                        .edit().putBoolean(REMEMBER_ME_KEY, rememberMeSwitch.isChecked()).apply();

                // Close the login screen and switch to main screen.
                startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        });

        // The switch needs to stay on left and text must stay on right,
        // so the alternative solution was to use a switch and a textview together in a layout,
        // and manage the switch from there.
        findViewById(R.id.rememberMeLayout).setOnClickListener(v ->
                rememberMeSwitch.setChecked(!rememberMeSwitch.isChecked()));

        // On password edit text, the IME action is "ActionDone" so act like it's a
        // login button click.
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                boolean shouldProcess = actionId == EditorInfo.IME_ACTION_DONE;
                if (shouldProcess)
                {
                    // Call the listener on login button, since
                    // "Done" is pressed from the soft keyboard.
                    loginButton.performClick();
                }
                return shouldProcess;
            }
        });
    }

    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text)
    {
        try
        {
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
        catch (Exception ignored){}
    }
}
