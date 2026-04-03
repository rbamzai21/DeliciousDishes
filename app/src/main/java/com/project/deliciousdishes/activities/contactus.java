package com.project.deliciousdishes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.project.deliciousdishes.R;
import java.util.Calendar;



public class contactus extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "prodbyrishab@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Delicious Dishes Recipe Share");
        email.putExtra(Intent.EXTRA_TEXT, "Please provide ingredient list and instructions...\n\n");

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
        finish();
    }
}
