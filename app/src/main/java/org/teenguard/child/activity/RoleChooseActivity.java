package org.teenguard.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.teenguard.child.R;
import org.teenguard.child.utils.MyApp;

public class RoleChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_choose);
        getSupportActionBar().hide(); //nasconde la barra
        boolean parentConfigured = MyApp.getPreferences().getBoolean("PARENT-CONFIGURED",false);
        System.out.println("RoleChooseActivity parentConfigured = " + parentConfigured);
        if(parentConfigured) {//device already configured,skip all activities
            gotoLastActivity();
        }

        viewBinding();

    }
    //activity lifecycle: add on resume in every activity  if parent is configured
    /*public void onResume() {
        super.onResume();
        System.out.println("onresumeeeeeeee " + true);
        //gotoLastActivity();
        this.finish();
    }*/

    private void viewBinding() {
        //  PARENT IMAGE listener
        ImageView parentImageView = (ImageView) findViewById(R.id.image_parent);
        parentImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("parentImageView clicked");
                /*Intent intent = new Intent(MyApp.getContext(), RoleChooseActivity.class);
                startActivity(intent);*/
            }
        });
        // CHILD IMAGE listener
        ImageView childImageView = (ImageView) findViewById(R.id.image_child);
        childImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("childImageView clicked");
                Intent intent = new Intent(MyApp.getContext(), InsertPhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }

    private void gotoLastActivity() {
        System.out.println("skipping to last activity");
        Intent intent = new Intent(MyApp.getContext(), ProperlySettedActivity.class);
        startActivity(intent);
        this.finish();
    }
}
