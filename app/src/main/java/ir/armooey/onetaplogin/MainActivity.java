package ir.armooey.onetaplogin;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {
    //Creating UI element variables
    EditText user, pass;
    Button ok;
    TextView con;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Connecting variables to UI layout
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        ok = findViewById(R.id.ok);
        con = findViewById(R.id.connect);
        pb = findViewById(R.id.progressBar);

        //Setting all UI elements INVISIBLE
        user.setVisibility(View.INVISIBLE);
        pass.setVisibility(View.INVISIBLE);
        ok.setVisibility(View.INVISIBLE);
        con.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.INVISIBLE);

        //Checking for first run
        SharedPreferences pref = MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        boolean firstRun = pref.getBoolean("firstRun", true);
        if(firstRun)
        {
            //making the inputs VISIBLE
            user.setVisibility(View.VISIBLE);
            pass.setVisibility(View.VISIBLE);
            ok.setVisibility(View.VISIBLE);

            //Clicking on ok
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String usernameInput = user.getText().toString(), passwordInput = pass.getText().toString();
                    if(usernameInput.equals("")  || passwordInput.equals(""))
                        Snackbar.make(v,"Fill the inputs",Snackbar.LENGTH_LONG).show();
                    else {
                        new Connect(MainActivity.this).execute(usernameInput,passwordInput);
                    }

                }
            });
        }
        else
        {
            //Not the first run
            new Connect(MainActivity.this).execute(pref.getString("username",""),pref.getString("password",""));
        }
    }
}
