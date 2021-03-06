package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ourincheon.wazap.Retrofit.Contests;
import com.ourincheon.wazap.Retrofit.WeeklyList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Youngdo on 2016-02-02.
 */
public class FragmentPage extends Fragment {

    private static final String ARG_POSITION = "position";
    RecyclerView content;
    LinearLayout linearLayout;
    private DataStorage storage = new DataStorage();
    private int position;
    Contests contest;
    WeeklyList weekly;
    RecyclerAdapter rec;
    contestRecyclerAdapter conRec;
    List<Recycler_item> items;
    List<Recycler_item> marketing, design, photo,it,foreign,etc;
    List<Recycler_contestItem> contestItems;
    List<Recycler_contestItem> cont_marketing, cont_design, cont_photo,cont_it,cont_foreign, cont_etc;
    Recycler_item[] item;
    Recycler_contestItem[] contestItem;
    String[] id_list,writer_list;
    Intent Joininfo;
    String access_token;

    public static FragmentPage newInstance(int position) {
        FragmentPage f = new FragmentPage();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (position) {
            case 0:
                linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_page, container, false);
                content = (RecyclerView) linearLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                content.setHasFixedSize(true);
                content.setLayoutManager(layoutManager);

                SharedPreferences pref = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                final String user_id = pref.getString("user_id", "");
                System.out.println(user_id);
                access_token = pref.getString("access_token", "");

                items = new ArrayList<>();
                marketing = new ArrayList<>();
                design= new ArrayList<>();
                photo = new ArrayList<>();
                it = new ArrayList<>();
                foreign = new ArrayList<>();
                etc = new ArrayList<>();

       /*         Bundle bundle = getArguments();
                int category = bundle.getInt("position");
                Toast.makeText(getContext(), "ccccccccccccccccc" + category, Toast.LENGTH_SHORT).show();
*/
                loadPage(access_token);//, category);



                // 전체 카드뷰 누를 경우 - 기능 없음
                content.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), content, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));


                rec = new RecyclerAdapter(getActivity(), items, R.layout.fragment_page);
                content.setAdapter(rec);
                linearLayout.removeAllViews();
                linearLayout.addView(content);
                return linearLayout;

            case 1:
                contestItems = new ArrayList<>();
                linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_page, container, false);
                content = (RecyclerView) linearLayout.findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
                content.setHasFixedSize(true);
                content.setLayoutManager(layoutManager1);

                SharedPreferences pref2 = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                access_token = pref2.getString("access_token", "");

                cont_marketing = new ArrayList<>();
                cont_design= new ArrayList<>();
                cont_photo = new ArrayList<>();
                cont_it = new ArrayList<>();
                cont_foreign = new ArrayList<>();
                cont_etc = new ArrayList<>();

                loadContest(access_token);

                content.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), content, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                }));

                conRec = new contestRecyclerAdapter(getActivity(), contestItems, R.layout.fragment_page);
                content.setAdapter(conRec);
                linearLayout.removeAllViews();
                linearLayout.addView(content);
                return linearLayout;
                
            default:
                return null;

        }
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void loadContest(String access_token)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        System.out.println("------------------------" + access_token);
        Call<WeeklyList> call = service.getWeeklylist(access_token,300);
        call.enqueue(new Callback<WeeklyList>() {
            @Override
            public void onResponse(Response<WeeklyList> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("--------------------", response.message());
                    weekly = response.body();

                   String result = new Gson().toJson(weekly);
                   Log.d("SUCESS-----", result);


                    contestItem = new Recycler_contestItem[weekly.getDatasize()];
                    Dday day = new Dday();

                    for (int i = 0; i < weekly.getDatasize(); i++) {
                        contestItem[i] = new Recycler_contestItem(weekly.getData(i).getTITLE(),
                                            weekly.getData(i).getHOSTING(),
                                            "D - " + day.dday(weekly.getData(i).getDEADLINE_DATE()),
                                            weekly.getData(i).getSTART_DATE()+" ~ "+weekly.getData(i).getDEADLINE_DATE(),
                                            weekly.getData(i).getIMG(),
                                            weekly.getData(i).getTOTALPRIZE(),
                                            weekly.getData(i).getTARGET(),
                                            weekly.getData(i).getBENEFIT(),
                                            weekly.getData(i).getFIRSTPRIZE(),
                                            weekly.getData(i).getHOMEPAGE(),
                                            weekly.getData(i).getTAG()
                                );
                        contestItems.add(contestItem[i]);

                        // 카테고리별 분류
                        String[] temp=weekly.getData(i).getTAG().split(",");
                        for(int j=0; j<temp.length; j++) {
                            if(temp[j].trim().equals("광고/아이디어/마케팅"))
                                cont_marketing.add(contestItem[i]);
                            else if(temp[j].trim().equals("디자인"))
                                cont_design.add(contestItem[i]);
                            else if(temp[j].trim().equals("사진/영상/UCC"))
                                cont_photo.add(contestItem[i]);
                            else if(temp[j].trim().equals("게임/소프트웨어"))
                                cont_it.add(contestItem[i]);
                            else if(temp[j].trim().equals("해외"))
                                cont_foreign.add(contestItem[i]);
                            else
                                cont_etc.add(contestItem[i]);
                        }
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

    void loadPage(String access_token)//, final int category)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);


        System.out.println("------------------------" + access_token);
        Call<Contests> call = service.getContests(access_token, 300);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {
                    Log.d("SUCCESS", response.message());
                    contest = response.body();

                    String result = new Gson().toJson(contest);
                    Log.d("SUCESS-----", result);

                    item = new Recycler_item[contest.getDatasize()];

                    for (int i = 0; i < contest.getDatasize(); i++) {
                        String[] parts = contest.getData(i).getPeriod().split("T");
                        Dday day = new Dday();

                        item[i] = new Recycler_item(contest.getData(i).getTitle(),
                                contest.getData(i).getHosts(), contest.getData(i).getUsername(),
                                contest.getData(i).getRecruitment(),
                                contest.getData(i).getMembers(),
                                contest.getData(i).getIs_clip(),
                                contest.getData(i).getCategories(), contest.getData(i).getCont_locate(),
                                "D - " + day.dday(parts[0]),
                                contest.getData(i).getContests_id(),
                                contest.getData(i).getCont_writer()
                        );
                        items.add(item[i]);

                        String cates = contest.getData(i).getCates().substring(1,contest.getData(i).getCates().length()-1);
                        String str="";
                        String[] temp=cates.split("\"");
                        for(int j=0; j<temp.length; j++)
                            str += temp[j]+" ";
                        String[] temp2 = str.split(" , ");

                        for(int j=0; j<temp2.length; j++) {
                            if(temp2[j].trim().equals("광고/아이디어/마케팅"))
                                marketing.add(item[i]);
                            else if(temp2[j].trim().equals("디자인"))
                                design.add(item[i]);
                            else if(temp2[j].trim().equals("사진/영상/UCC"))
                                photo.add(item[i]);
                            else if(temp2[j].trim().equals("게임/소프트웨어"))
                                it.add(item[i]);
                            else if(temp2[j].trim().equals("해외"))
                                foreign.add(item[i]);
                            else
                                etc.add(item[i]);
                        }
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
}