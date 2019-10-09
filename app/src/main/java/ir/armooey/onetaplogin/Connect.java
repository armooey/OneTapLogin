package ir.armooey.onetaplogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class Connect extends AsyncTask<String, Void, Void> {
    //creating UI variables to have refrence of them in asyncTask
    EditText user, pass;
    Button ok;
    TextView con;
    ProgressBar pb;

    int result;
    String username,password;
    Activity mainActivity;

    //Constructor that gets a weak reference from main activity
    public Connect(Activity activity)
    {
        mainActivity = new WeakReference<>(activity).get();
        user = mainActivity.findViewById(R.id.user);
        pass = mainActivity.findViewById(R.id.pass);
        ok = mainActivity.findViewById(R.id.ok);
        con = mainActivity.findViewById(R.id.connect);
        pb = mainActivity.findViewById(R.id.progressBar);
    }


    //Updating UI elements before running the connection
    @Override
    protected void onPreExecute() {
        user.setVisibility(View.INVISIBLE);
        pass.setVisibility(View.INVISIBLE);
        ok.setVisibility(View.INVISIBLE);
        con.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);

    }

    //Handling the connection
    //I saved connection situation in result variable to do the UI thing in post execute
    //result = 0 -----> Every thing is awesome
    //result = 1 -----> Already Logged In in network default login page
    //result = 2 -----> Wrong username or password
    //result = 3 -----> Having trouble with connection to URL
    @Override
    protected Void doInBackground(String... strings) {
        try {
            username = strings[0];
            password = strings[1];
            Connection.Response loginFormResponse = Jsoup.connect("https://internet.aut.ac.ir")
                    .method(Connection.Method.GET)
                    .execute();
            Document parsedPage = loginFormResponse.parse();
            if(parsedPage.toString().contains("خروج از اینترنت"))
            {
                result = 1;
            }
            else {
                FormElement loginForm = (FormElement) parsedPage.select(".form").first();
                Element usernameField = loginForm.select(".input-group > input:nth-child(2)").first();
                Element passField = loginForm.select("div.form-group:nth-child(2) > input:nth-child(2)").first();
                usernameField.val(username);
                passField.val(password);
                Connection.Response loginActionResponse = loginForm.submit()
                        .cookies(loginFormResponse.cookies())
                        .execute();
                result = 0;
                if (loginActionResponse.parse().toString().contains("رمز ورود"))
                {
                    result = 2;
                }
            }
        } catch (IOException e) {
            result = 3;
        }
        return null;
    }


    //Handling result of connection in here
    @Override
    protected void onPostExecute(Void aVoid) {
        if(result == 0) {
            SharedPreferences pref = mainActivity.getSharedPreferences("data", Context.MODE_PRIVATE);
            if (pref.getBoolean("firstRun", true)) {
                final SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("firstRun", false);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();
            }
            Toast.makeText(mainActivity.getApplicationContext(), ".: Connected :.", Toast.LENGTH_LONG).show();
            mainActivity.finish();
        }
        else if(result == 1) {
            Toast.makeText(mainActivity.getApplicationContext(), "Already Connected!", Toast.LENGTH_LONG).show();
            mainActivity.finish();
        }
        else if(result == 2)
        {
            Toast.makeText(mainActivity.getApplicationContext(),"Wrong Username or Password!",Toast.LENGTH_LONG).show();
            user.setVisibility(View.VISIBLE);
            pass.setVisibility(View.VISIBLE);
            ok.setVisibility(View.VISIBLE);
            con.setVisibility(View.INVISIBLE);
            pb.setVisibility(View.INVISIBLE);
            user.setText("");
            pass.setText("");
            cancel(true);
        }
        else if(result == 3) {
            Toast.makeText(mainActivity.getApplicationContext(), "Connection Failed!", Toast.LENGTH_LONG).show();
            mainActivity.finish();
        }
    }
}
