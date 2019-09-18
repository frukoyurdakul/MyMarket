package com.furkanyurdakul.mymarket.abstracts;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The abstract base activity class that all childs inherit from.
 * It is necessary since activities must behave the same way in
 * some cases, such as back press button.
 */
public abstract class BaseActivity extends AppCompatActivity
{
    @Override
    public void onBackPressed()
    {
        // To not lose the instance of the activity, execute this code instead.
        // Run only if the activity is the task root, e.g. no other activities
        // were started above the root.
        if (isTaskRoot())
            moveTaskToBack(true);
        else if (!getSupportFragmentManager().isStateSaved())
        {
            // Executing a back press can create some exceptions on specific API levels
            // below Android Marshmallow. Perform back press only if the state is not saved.
            super.onBackPressed();
        }
    }
}
