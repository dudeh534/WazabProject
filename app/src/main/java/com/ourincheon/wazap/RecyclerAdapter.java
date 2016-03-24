package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Youngdo on 2016-01-19.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Recycler_item> items;
    int item_layout;
    String user_id,access_token;
    Intent intent;

    public RecyclerAdapter(Context context, List<Recycler_item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;//번호별로 상세페이지
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Recycler_item item = items.get(position);

        SharedPreferences pref2 = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        user_id = pref2.getString("user_id", "");
        access_token = pref2.getString("access_token", "");

        Log.d("SUCESS-----", item.getTitle());
        holder.title.setText(item.getTitle());
        holder.name.setText(item.getName());
        holder.text.setText(item.getText());

        //// 카테고리 명에 맞는 이미지 출력 ////
        String[] temp = item.getCategory().split(" ");
     /*    System.out.println("!!!!!!!!!!!!!!!!!!!!" + item.getCategory());
       holder.category1.setText(item.getCategory());*/
        if(temp.length == 2 ) {
            holder.category1.setText(temp[1]);

            if(temp[1].equals("사진/영상/UCC"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_video);
            else if(temp[1].equals("디자인"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_design);
            else if(temp[1].equals("게임/소프트웨어"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_it);
            else if(temp[1].equals("해외"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_idea);
            else if(temp[1].equals("광고/아이디어/마케팅"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_marketing);
            else
                holder.c1.setBackgroundResource(R.drawable.detail_icon_scenario);

            holder.category2.setText(" ");
            holder.c2.setVisibility(View.INVISIBLE);
        }
        else if(temp.length > 3) {
            holder.category1.setText(temp[1]);
            if(temp[1].equals("사진/영상/UCC"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_video);
            else if(temp[1].equals("디자인"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_design);
            else if(temp[1].equals("게임/소프트웨어"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_it);
            else if(temp[1].equals("해외"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_idea);
            else if(temp[1].equals("광고/아이디어/마케팅"))
                holder.c1.setBackgroundResource(R.drawable.detail_icon_marketing);
            else
                holder.c1.setBackgroundResource(R.drawable.detail_icon_scenario);

            holder.category2.setText(temp[3]);
            holder.c2.setVisibility(View.VISIBLE);
            if(temp[3].equals("사진/영상/UCC"))
                holder.c2.setBackgroundResource(R.drawable.detail_icon_video);
            else if(temp[3].equals("디자인"))
                holder.c2.setBackgroundResource(R.drawable.detail_icon_design);
            else if(temp[3].equals("게임/소프트웨어"))
                holder.c2.setBackgroundResource(R.drawable.detail_icon_it);
            else if(temp[3].equals("해외"))
                holder.c2.setBackgroundResource(R.drawable.detail_icon_idea);
            else if(temp[3].equals("광고/아이디어/마케팅"))
                holder.c2.setBackgroundResource(R.drawable.detail_icon_marketing);
            else
                holder.c2.setBackgroundResource(R.drawable.detail_icon_scenario);
        }

        holder.loc.setText(item.getLoc());
        holder.recruit.setText(" / " + String.valueOf(item.getRecruit()));
        holder.member.setText(String.valueOf(item.getMember()));
        holder.day.setText(item.getDay());

        // 카드뷰 터치시-> 상세페이지로
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!item.getWriter().equals(user_id))
                    intent = new Intent(context,JoinActivity.class);
                else
                    intent = new Intent(context,MasterJoinActivity.class);

                intent.putExtra("id",String.valueOf(item.getId()));
                context.startActivity(intent);
            }
        });

        if(item.getClip()==0)
            holder.heart.setBackgroundResource(R.drawable.heart1);
        else
            holder.heart.setBackgroundResource(R.drawable.heart2);

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!item.getWriter().equals(user_id))
                    pickContest(String.valueOf(item.getId()),access_token);
                else
                    Toast.makeText(context, "글 작성자는 스크랩할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });


    }


    void pickContest(final String num, final String access_token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        System.out.println("-------------------" + access_token);
        Call<LinkedTreeMap> call = service.clipContests(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (!msg.equals("이미 찜한 게시물 입니다.")) {
                        if (result) {
                            Log.d("저장 결과: ", msg);
                            Toast.makeText(context, "찜 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("저장 실패: ", msg);
                            Toast.makeText(context, "찜 안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        removeClip(num);
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

    void removeClip(String num)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        Call<LinkedTreeMap> call = service.delClip(num, access_token);
        call.enqueue(new Callback<LinkedTreeMap>() {
            @Override
            public void onResponse(Response<LinkedTreeMap> response) {
                if (response.isSuccess() && response.body() != null) {

                    LinkedTreeMap temp = response.body();

                    boolean result = Boolean.parseBoolean(temp.get("result").toString());
                    String msg = temp.get("msg").toString();

                    if (result) {
                        Log.d("저장 결과: ", msg);
                        Toast.makeText(context, "찜 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("저장 실패: ", msg);
                        Toast.makeText(context, "찜 취소안됬습니다.다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, text, name,recruit, member,loc,category1,category2,day;
        ImageView c1,c2;
        CardView cardview;
        Button heart;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            text = (TextView) itemView.findViewById(R.id.text);
            loc = (TextView) itemView.findViewById(R.id.loc);
            category1 = (TextView) itemView.findViewById(R.id.category1);
            category2 = (TextView) itemView.findViewById(R.id.category2);
            title = (TextView) itemView.findViewById(R.id.title);
            recruit = (TextView) itemView.findViewById(R.id.recruit);
            member = (TextView) itemView.findViewById(R.id.member);
            cardview = (CardView) itemView.findViewById(R.id.cardView);
            heart = (Button) itemView.findViewById(R.id.hbutton);
            day = (TextView) itemView.findViewById(R.id.day);

            c1 =(ImageView) itemView.findViewById(R.id.imageView1);
            c2 =(ImageView) itemView.findViewById(R.id.imageView2);
        }
    }
}