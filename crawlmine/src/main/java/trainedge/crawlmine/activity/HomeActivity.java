package trainedge.crawlmine.activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;

import java.util.zip.Inflater;

import trainedge.crawlmine.R;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText etFeedEmail1;
    private String m;
    private ImageView userImage;
    private TextView userEmail;
    private TextView userName;
    private LinearLayout headBackground;
    private static final int REQUEST_INVITE = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       etFeedEmail1 = (EditText) findViewById(R.id.etFeedEmail1);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeActivity.this,CameraActivity.class);
                startActivity(i);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        userImage = headerView.findViewById(R.id.userImage);

        userEmail = headerView.findViewById(R.id.userEmail);
        userName = headerView.findViewById(R.id.userName);

        headBackground = headerView.findViewById(R.id.headBackground);

       String  displayEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Uri displayImage = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
       String  displayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        Glide.with(context).load(displayImage.toString()).into(userImage);
        userImage.setVisibility(View.VISIBLE);

        userName.setText(displayName);
        userEmail.setText(displayEmail);

        //round imageview of user image
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.id.userImage);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        userImage.setImageDrawable(roundedBitmapDrawable);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id=item.getItemId();
        switch (id){
            case R.id.settings:
                return true;

            case R.id.feedback:
                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater = HomeActivity.this.getLayoutInflater();
                View view = inflater.inflate(R.layout.feedback_dialog, null);
                builder.setView(view);
                etFeedEmail1 = (EditText)view.findViewById(R.id.etFeedEmail1);

                m = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                etFeedEmail1.setText(m);


                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create();
                builder.show();

                break;


            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(HomeActivity.this, MainActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                finish();

                break;



        }
        return super.onOptionsItemSelected(item);



    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Intent i= new Intent(HomeActivity.this,CanvasActivity.class);
            startActivity(i);

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent i=new Intent(HomeActivity.this,QRActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


            AppInviteInvitation.IntentBuilder intent = new AppInviteInvitation
                    .IntentBuilder("Send Invitations for Smart Scanner app")
                    // Ensure valid length for any message used before calling otherwise this will throw
                    // an IllegalArgumentException if greater than MAX_MESSAGE_LENGTH.
                    .setMessage("Try out Smart Scanner app now")
                    .setDeepLink(Uri.parse("//https://play.google.com/store/apps/details?id=com.trainedge.crawlmine&hl=en"))
                    .setCallToActionText("Find data");
            Intent i = intent.build();
            startActivityForResult(i, REQUEST_INVITE);

            //https://play.google.com/store/apps/details?id=com.trainedge.crawlmine&hl=en
            ////xyz.com/offer/free_trial_campaign



        } else if (id == R.id.nav_complaint) {

            Intent intent = new Intent(HomeActivity.this, FeedbackActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
