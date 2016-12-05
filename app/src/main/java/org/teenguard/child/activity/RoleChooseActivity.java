package org.teenguard.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.teenguard.child.R;
import org.teenguard.child.utils.FxUtils;
import org.teenguard.child.utils.MyApp;

public class RoleChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_choose);
        getSupportActionBar().hide(); //nasconde la barra
        boolean parentConfigured = MyApp.getSharedPreferences().getBoolean("PARENT-CONFIGURED",false);
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
        final ImageView parentImageView = (ImageView) findViewById(R.id.image_parent);


        parentImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //FxUtils.changeColor(parentImageView,Color.WHITE, Color.LTGRAY,300);
                return false;
            }
        });

        parentImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("parentImageView clicked");
                //FxUtils.changeColor(parentImageView,Color.LTGRAY, Color.WHITE,300);
                MyApp.getSharedPreferences().edit()
                        .putBoolean("IS-CHILD",false)
                        .apply();
                FxUtils.shake(parentImageView);
                FxUtils.asyncToast(getString(R.string.str_not_available));
            }
        });
        // CHILD IMAGE listener
       final ImageView childImageView = (ImageView) findViewById(R.id.image_child);
        childImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("childImageView clicked");
                FxUtils.shake(childImageView);
                ///////
                MyApp.getSharedPreferences().edit()
                        .putBoolean("IS-CHILD",true)
                        .apply();
                //////

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
