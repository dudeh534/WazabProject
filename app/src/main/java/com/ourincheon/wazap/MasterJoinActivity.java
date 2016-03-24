package com.ourincheon.wazap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.reqContest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MasterJoinActivity extends AppCompatActivity {

    public static Context mContext;
    reqContest contest;
    ContestData contestData;
    TextView jTitle,jCTitle,jButton,jmList,jCate,jApply,jRec,jName,jCover,jMem,jDate,jHost,jLoc,jPos,jKakao;
    Button eBtn,jBefore;
    ImageView jImg;
    String access_token,num;
    AlertDialog.Builder ad,deleteD;
    CharSequence list[] = {"수정하기", "삭제하기","취소"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_join);

        mContext = this;

        contestData = new ContestData();

        jTitle = (TextView) findViewById(R.id.jmTitle);
        jCTitle = (TextView) findViewById(R.id.jmCTitle);
        jCate =  (TextView) findViewById(R.id.jmCate);
        jApply = (TextView) findViewById(R.id.jmApply);
        jRec = (TextView) findViewById(R.id.jmRec);
        jName = (TextView) findViewById(R.id.jmName);
        jCover = (TextView) findViewById(R.id.jmCover);
        jMem = (TextView) findViewById(R.id.jmMem);
        jDate = (TextView) findViewById(R.id.jmDate);
        jHost = (TextView) findViewById(R.id.jmHost);
        jLoc = (TextView) findViewById(R.id.jmLoc);
        jPos = (TextView) findViewById(R.id.jmPos);
        jKakao = (TextView) findViewById(R.id.jmKakao);

        jImg = (ImageView) findViewById(R.id.jmImg);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        access_token = pref.getString("access_token", "");

        Intent intent = getIntent();
        num =  intent.getExtras().getString("id");
        loadPage(num);


        deleteD = new AlertDialog.Builder(this);
        deleteD.setMessage("모집글을 마감하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endContest();
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // 마감버튼까
        jButton = (TextView) findViewById(R.id.jmButton);
        jButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alert = deleteD.create();
                alert.show();
            }
        });

        jBefore = (Button) findViewById(R.id.jmBefore);
        jBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ad = new AlertDialog.Builder(this);
        ad.setTitle("팀원모집");
        ad.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    editCont();
                if (which == 1)
                    delCont();
                if (which == 2)
                    dialog.cancel();
            }
        });

        // 수정,삭제버튼
        eBtn = (Button) findViewById(R.id.jmEdit);
        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
            }
        });

        //신청자리스트보기 버튼
        jmList = (TextView) findViewById(R.id.jmList);
        jmList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MasterJoinActivity.this, ApplierList.class);
                intent.putExtra("id", num);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPage(num);
    }

    void editCont()
    {
        Intent intent = new Intent(MasterJoinActivity.this, RecruitActivity.class);
        intent.putExtra("edit",1);
        intent.putExtra("contestD",contestData);
        //intent.putExtra("contestD",contestData.getContests_id());
        startActivity(intent);
    }

    void delCont()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("!!!!!!!!!!!!!!!!!!!"+access_token);
        System.out.println("!!!!!!!!!!!!!!!!!!!"+num);

        Call<LinkedTreeMap> call = service.delContest(num,access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("삭제 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Log.d("삭제 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "삭제 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
            }
        });
    }

    void endContest( )
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------"+num);

        Call<LinkedTreeMap> call = service.finishContest(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(getApplicationContext(), "마감되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(getApplicationContext(), "마감 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
            }
        });
    }

    void loadPage(String num)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<reqContest> call = service.getConInfo(num,access_token);
        call.enqueue(new Callback<reqContest>() {
            @Override
            public void onResponse(Response<reqContest> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    contest = response.body();

                    Log.d("SUCCESS", contest.getMsg());

                    System.out.println(contest.getData().getTitle());
                    jTitle.setText(contest.getData().getTitle());
                    jCTitle.setText(contest.getData().getCont_title());
                    jCate.setText(contest.getData().getCategories());
                    jApply.setText(String.valueOf(contest.getData().getAppliers()));
                    jMem.setText(String.valueOf(contest.getData().getMembers()));
                    jRec.setText(" / "+String.valueOf(contest.getData().getRecruitment()));
                    jName.setText(contest.getData().getUsername());
                    jCover.setText(contest.getData().getCover());
                    jHost.setText(contest.getData().getHosts());
                    jLoc.setText(contest.getData().getCont_locate());
                    jPos.setText(contest.getData().getPositions());
                    jKakao.setText(contest.getData().getKakao_id());

                    try {
                        String thumb = URLDecoder.decode(contest.getData().getProfile_img(), "EUC_KR");
                        Glide.with(mContext).load(thumb).error(R.drawable.icon_user).override(50,50).crossFade().into(jImg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    String[] parts = contest.getData().getPeriod().split("T");
                    Dday day = new Dday();
                    jDate.setText("D - "+day.dday(parts[0]));

                    contestData = contest.getData();

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Error", t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join, menu);
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

        return super.onOptionsItemSelected(item);
    }
}

