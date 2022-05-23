package com.example.newinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private Button login;
    private Button register;
    private ImageView icon;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        icon=findViewById(R.id.imageView);
        linearLayout=findViewById(R.id.linear_layout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,Login.class));
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,Register.class));
                finish();
            }
        });

        linearLayout.animate().alpha(0f).setDuration(85);

        TranslateAnimation anime=new TranslateAnimation(0,0,0,-1000);
        anime.setDuration(1000);
        anime.setFillAfter(false);
        anime.setAnimationListener(new myanimationlistiner());

        icon.setAnimation(anime);
    }
    private class myanimationlistiner implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon.clearAnimation();
            icon.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            startActivity(new Intent(StartActivity.this,MainActivity.class).
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

            finish();
        }
    }
}