package com.example.BackgroundProcessKiller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class KillIt extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final EditText packageNameEditText = (EditText) findViewById(R.id.et_packageName);
        final Button killer = (Button) findViewById(R.id.btn_die);
        final KillIt killingActivity = this;
        killer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String packageName = packageNameEditText.getText().toString();
                if(TextUtils.isEmpty(packageName)){
                    Toast.makeText(killingActivity, "package name kya papa ji bataayenge", Toast.LENGTH_LONG).show();
                }else{
                    ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                    am.killBackgroundProcesses(packageName);
                    Toast.makeText(killingActivity, packageName+ " is no more ;)", Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button language = (Button) findViewById(R.id.btn_lang);
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings", "com.android.settings.LanguageSettings");
                startActivity(intent);
                finish();
            }
        });

    }


}
