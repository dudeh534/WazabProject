package com.ourincheon.wazap;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/*
* TODO - TABLayout RecyclerView insert
* TODO - ListView Subpage*/
public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    private BackPressCloseHandler backPressCloseHandler;

    ImageView profileImg;
    String thumbnail;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable drawable = getResources().getDrawable(R.drawable.detail_title_banner);
        toolbar.setBackground(drawable);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, RecruitActivity.class);
                i.putExtra("edit", 0);
                //i.putExtra("KakaoInfo",kakao);
                //i.putExtra("Nickname",nick);
                startActivity(i);
                //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });


  /*      final TextView spinnerT =(TextView)findViewById(R.id.spinnerT);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //spinnerT.setText(""+parent.getItemAtPosition(position));기

                *//****fragment로 값보내****//*
                FragmentPage fragment = new FragmentPage();
                Bundle bundle = new Bundle();
                bundle.putInt("category",position);
                fragment.setArguments(bundle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationIcon(R.drawable.list_icon);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentPage(), "팀원모집");
        adapter.addFragment(new FragmentPage(), "공모전리스트");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.GRAY, Color.BLACK);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.wazab));


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_nevigation);
        TextView nickname = (TextView)header.findViewById(R.id.nickname);

        // 사용자 이름, 이미지 불러오기
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
      //  Intent intent = getIntent();
       // kakao = (infoKaKao)intent.getSerializableExtra("KakaoInfo");
       nickname.setText(pref.getString("name",""));

        profileImg = (ImageView)header.findViewById(R.id.imageView);
       thumbnail = pref.getString("profile_img","");
        ThumbnailImage thumb = new ThumbnailImage(thumbnail,profileImg);
        thumb.execute();

        ImageView profileBtn = (ImageView)header.findViewById(R.id.showProBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(getApplicationContext(), "프로필 보기", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, showMypageActivity.class);
                i.putExtra("flag",0);
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String user_id = pref.getString("user_id", "");
                i.putExtra("user_id",user_id);
                // i.putExtra("KakaoInfo",kakao);
                startActivity(i);
            }
        });

        Button side_Button1 = (Button) findViewById(R.id.side_button1);//custom navigation view Button setOnClickListener
        side_Button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClipList.class);
                startActivity(intent);
            }
        });

        Button side_Button2 = (Button) findViewById(R.id.side_button2);
        side_Button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ApplyList.class);
                startActivity(intent);
            }
        });


        Button side_Button3 = (Button) findViewById(R.id.side_button3);
        side_Button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContestList.class);
                startActivity(intent);

            }
        });


        ImageView alarmBtn = (ImageView) findViewById(R.id.alarmBtn);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlarmList.class);
                startActivity(intent);

            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
        backPressCloseHandler.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.cart) {//not work
            Intent intent = new Intent(this, ClipList.class);
            startActivity(intent);

        } else if (id == R.id.require) {
            Intent intent = new Intent(this, ApplyList.class);
            startActivity(intent);

        } else if (id == R.id.gonggu) {
            Intent intent = new Intent(this, ContestList.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}