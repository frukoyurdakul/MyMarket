package com.furkanyurdakul.mymarket.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.furkanyurdakul.mymarket.R;
import com.furkanyurdakul.mymarket.abstracts.BaseActivity;
import com.furkanyurdakul.mymarket.adapters.MainScreenAdapter;
import com.furkanyurdakul.mymarket.connections.ConnectionInterface;
import com.furkanyurdakul.mymarket.helpers.ThreadHelper;
import com.furkanyurdakul.mymarket.models.MainScreenModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.furkanyurdakul.mymarket.activities.SplashActivity.REMEMBER_ME_KEY;

/**
 * Contains main screen actions, which is displaying data over a JSON
 * that is fetched from a specific URL.
 */
public class MainActivity extends BaseActivity
{
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private final LoaderTask<List<MainScreenModel>> task = new LoaderTask<>(
            new LoaderTask.LoaderResultListener<List<MainScreenModel>>()
    {
        @Override
        public void onSuccessfulLoad(List<MainScreenModel> body)
        {
            // Here, process the body.
            // The views has to be initialized at this point.
            // Task is executed after views are found from the layout.
            processData(body);
        }

        @Override
        public void onFailure(int code, String message)
        {
            // Log the error. On other resume, it will continue trying to fetch data.
            Log.e(TAG, "Error while fetching data. Code: " + code + ", message: " + message);
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        loadDataIfNecessary();
    }

    @Override
    protected void onDestroy()
    {
        // An asynctask needs to be stopped if activity is going to be destroyed.
        if (task.getStatus() == AsyncTask.Status.PENDING ||
            task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        super.onDestroy();
    }

    /**
     * Initialize views. Contains settings views and adapter creations.
     */
    private void initViews()
    {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        // These two views do not need class defined variables as only
        // click listeners are required.

        findViewById(R.id.myOrdersButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // The click event does nothing but printing a toast.
                Toast.makeText(getApplicationContext(),
                        "Bir işlevi yok.", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.exitButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Show a confirmation dialog to the user about
                // exiting the app. Will redirect to Login screen after clicked.
                //
                // Wrap in try-catch as sometimes not wrapping can cause exceptions.
                try
                {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Çıkış")
                            .setMessage("Çıkış yapmak istediğinize emin misiniz?")
                            .setPositiveButton("İstiyorum", (dialog, which) ->
                            {
                                dialog.dismiss();
                                exitAndOpenLoginScreen();
                            })
                            // Null listener just causes the dialog to dismiss.
                            .setNegativeButton("İptal et", null)
                            .show();
                }
                catch (Exception ignored){}
            }
        });
    }

    /**
     * Loads the data asynchronously using {@link LoaderTask}.
     */
    private void loadDataIfNecessary()
    {
        if (!task.isExecutedSuccessfully())
        {
            // To prevent delays from Retrofit, switch between foreground and background threads
            // while initializing. The asynctask has to be started from the main thread.
            ThreadHelper.runOnBackgroundThread(() ->
            {
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(ConnectionInterface.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

                // Create retrofit interface to fetch data.
                ConnectionInterface connectionInterface = builder.build().create(ConnectionInterface.class);

                // After creating interface, switch to foreground thread to start execution
                // of asynctask.
                //
                // Ignore unsafe check as we know there would be a compilation error
                // if expected was different since it has to be same generic.
                // It also throws exception if more than one call is sent to the task.
                runOnUiThread(() -> task.execute(connectionInterface.getData()));
            });
        }
    }

    /**
     * Processes the data that has been fetched from the server.
     */
    private void processData(List<MainScreenModel> body)
    {
        // Create the adapter and hide the progress bar.
        // Linear layout manager is enough to display items here.

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new MainScreenAdapter(body));
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    // Exits the main screen and switches to login screen.
    private void exitAndOpenLoginScreen()
    {
        // Remove "Remember me" preference.
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putBoolean(REMEMBER_ME_KEY, false).apply();

        // Switch to main screen by clearing the top activity which is itself.
        startActivity(new Intent(this, LoginActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }
}

/**
 * The asynchronous loader task for loading data remotely from an URL. Returns the data
 * from {@link LoaderTask#onPostExecute(Response)}
 *
 * Written on the same file since the load only affects MainActivity.
 */
class LoaderTask<T> extends AsyncTask<Call<T>, Void, Response<T>>
{
    private static final String TAG = "LoaderTask";

    @NonNull
    private final LoaderResultListener<T> listener;

    private boolean isExecutedSuccessfully = false;

    public boolean isExecutedSuccessfully()
    {
        return isExecutedSuccessfully;
    }

    interface LoaderResultListener<U>
    {
        void onSuccessfulLoad(U body);
        void onFailure(int code, String message);
    }

    LoaderTask(LoaderResultListener<T> listener)
    {
        if (listener == null)
            throw new NullPointerException("Listener cannot be null.");
        this.listener = listener;
    }

    @SafeVarargs
    @Override
    protected final Response<T> doInBackground(Call<T>... calls)
    {
        if (calls == null || calls.length == 0)
            throw new NullPointerException("Parameters cannot be null.");
        else if (calls.length > 1)
            throw new UnsupportedOperationException("You can only execute 1 call with this library." +
                    " Construct another object if another call should be executed.");
        else
        {
            try
            {
                // Try to execute the call. Cannot return null even if the request
                // fails with another code.

                // Only execute the call if the task is not cancelled.
                if (isCancelled())
                    return null;

                return calls[0].execute();
            }
            catch (IOException e)
            {
                // If an exception occurs, return null so onPostExecute can know about it.
                Log.e(TAG, "Failed to execute call.", e);
                return null;
            }
        }
    }

    // onPostExecute is only executed if the task is not canceled.
    @Override
    protected void onPostExecute(Response<T> response)
    {
        if (response != null)
        {
            // It means the request is not null. It can still be unsuccessful or the body may be null.
            if (response.isSuccessful() && response.body() != null)
            {
                listener.onSuccessfulLoad(response.body());
                isExecutedSuccessfully = true;
            }
            else if (response.errorBody() != null)
            {
                // If an error occurs, typically this field is filled. Get the message and log it.
                try
                {
                    String text = response.errorBody().string();
                    Log.e(TAG, "Error while getting response: " + text);
                    listener.onFailure(response.code(), response.message());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    listener.onFailure(-2, "Error body is filled but " +
                            "cannot be converted. Actual message: " + response.message());
                }
            }
            else
                listener.onFailure(-3, "Response is not successful and error body is null. Cause is unknown.");
        }
        else
            listener.onFailure(-1, "An exception occured while executing the code.");
    }
}
