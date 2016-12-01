package com.example.johanringstrom.fragment_grocode;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Connection con;
    static String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Choose starting fragment.
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLists()).commit();

        //Set toolbar(actionbar)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Creates a Connection object
        con = new Connection(MainActivity.this,Connection.clientId);
        //Starts to subscribe;
        con.subscribeToTopic("fetch-lists");
        con.subscribeToTopic("fetch");
        con.subscribeToTopic("fetch-bought");
        con.subscribeToTopic("fetch-SubscriptionList");
        con.subscribeToTopic("fetch-Notifications");
        con.subscribeToTopic("fetch-SubItems");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.navClientId);
        name.setText(con.clientId);

        if(!Connection.loggedin)
        {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_delete) {
            MyLists ListName = new MyLists();
            con.publish("lists", new String[]{"delete-list",con.clientId,ListName.getListname()});
            Toast.makeText(getApplicationContext(),"List Deleted",Toast.LENGTH_SHORT).show();
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLists()).commit();
            return true;

        }

        if (id == R.id.action_share) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.my_lists) {
            setTitle(getString(R.string.title_section1));
            //Goes to first fragment
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MyLists()).commit();

            if(con.getClient().isConnected()) {
                Log.d("StateTest", "true");

                //Publish a request
            } else {
                Log.d("StateTest", "false");
                Toast.makeText(MainActivity.this, "Not connected to the broker mother father", Toast.LENGTH_LONG).show();

            }


        } if (id == R.id.share_lists) {
            setTitle("Shared Lists");
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ShareLists()).commit();

            if(con.getClient().isConnected()) {
                Log.d("StateTest", "true");
                con.publish("lists",new String[]{"fetch-SubscriptionList",con.clientId});//get lists
            } else {
                Log.d("StateTest", "false");
                Toast.makeText(MainActivity.this, "Not connected to the broker mother father", Toast.LENGTH_LONG).show();

            }


        } if (id == R.id.notifications) {
            setTitle(getString(R.string.title_section3));
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ThirdFragmant()).commit();

            if(con.getClient().isConnected()) {
                Log.d("StateTest", "true");
                con.publish("lists",new String[]{"fetch-Notifications",con.clientId});//get lists
            } else {
                Log.d("StateTest", "false");
                Toast.makeText(MainActivity.this, "Not connected to the broker mother father", Toast.LENGTH_LONG).show();

            };

            // close connection of user
        } else if (id == R.id.logout) {
            Toast.makeText(MainActivity.this, "Logout Successful", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
