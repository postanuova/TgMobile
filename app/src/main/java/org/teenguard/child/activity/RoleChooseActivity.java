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
import org.teenguard.parent.activity.WebFrameActivity;

public class RoleChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_choose);
        getSupportActionBar().hide(); //nasconde la barra
        viewBinding();
    }

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
                        .putBoolean("IS-CHILD-CONFIGURED",false)
                        .putBoolean("IS-PARENT", true)
                        .putBoolean("IS-PARENT-CONFIGURED",false)
                        .apply();
                MyApp.dumpSharedPreferences();
                FxUtils.shake(parentImageView);
                FxUtils.asyncToast(MyApp.getInstance().getResources().getString(R.string.str_parent_role_not_available));
                // TODO: 06/12/16 going anyway to web frame: implement parent configuration
                Intent webFrameIntent = new Intent(MyApp.getInstance().getApplicationContext(), WebFrameActivity.class);
                startActivity(webFrameIntent);
                System.out.println("RoleChooseActivity.onClick going anyway to WebFrameActivity");
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
                        .putBoolean("IS-CHILD-CONFIGURED",false)
                        .putBoolean("IS-PARENT", false)
                        .putBoolean("IS-PARENT-CONFIGURED",false)
                        .apply();
                MyApp.dumpSharedPreferences();
                //////
                System.out.println("RoleChooseActivity.onClick going to InsertPhoneNumberActivity");
                Intent intent = new Intent(MyApp.getInstance().getApplicationContext(), InsertPhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }

    private void gotoLastActivity() {
        System.out.println("skipping to last activity");
        Intent intent = new Intent(MyApp.getInstance().getApplicationContext(), ProperlySettedActivity.class);
        startActivity(intent);
        this.finish();
    }
}
