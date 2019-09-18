package com.furkanyurdakul.mymarket.abstracts;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The abstract base activity class that all childs inherit from.
 * It is necessary since activities must behave the same way in
 * some cases, such as back press button.
 */
public abstract class BaseActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Hide toolbar if there is one.
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed()
    {
        // To not lose the instance of the activity, execute this code instead.
        // Run only if the activity is the task root, e.g. no other activities
        // were started above the root.
        //
        // On current occasion, every back press should move the task to back so
        // task root check is skipped.
        moveTaskToBack(true);
    }
}
