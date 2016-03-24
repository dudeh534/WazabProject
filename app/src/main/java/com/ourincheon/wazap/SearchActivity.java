package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ourincheon.wazap.Retrofit.ContestData;
import com.ourincheon.wazap.Retrofit.Contests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    Context context;
    EditText sBox;
    Button sBtn;
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    Contests contests;
    ArrayList<ContestData> contest_list;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        mListView = (ListView) findViewById(R.id.sList);

        sBox =(EditText) findViewById(R.id.search_box);

        sBtn =(Button)findViewById(R.id.search_btn);
        sBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTitle(sBox.getText().toString());
            }
        });



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ContestData mData = mAdapter.mListData.get(position);
                // Toast.makeText(AlarmList.this, mData.msg_url, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchActivity.this, JoinActivity.class);
                intent.putExtra("id",String.valueOf(mData.getContests_id()));
                startActivity(intent);
            }
        });

        mAdapter = new ListViewAdapter(this);

        mListView.setAdapter(mAdapter);
    }

    void searchTitle(String text)
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String access_token = pref.getString("access_token", "");

        System.out.println("------------"+text+"------------"+access_token);
        Call<Contests> call = service.getSearchlist(access_token, text, 300);
        call.enqueue(new Callback<Contests>() {
            @Override
            public void onResponse(Response<Contests> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    contests = response.body();

                    String result = new Gson().toJson(contests);
                    Log.d("SUCESS-----", result);


                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        count = jsonArr.length();
                        System.out.println(count);
                        mAdapter=new ListViewAdapter(context);
                        for (int i = 0; i < count; i++) {
                                mAdapter.addItem(jsonArr.getJSONObject(i).getString("title")
                                ,Integer.parseInt(jsonArr.getJSONObject(i).getString("contests_id")));

                        }
                        mListView.setAdapter(mAdapter);
                    } catch (JSONException e) {
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

    private class ViewHolder {

        public TextView Title;

    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ContestData> mListData = new ArrayList<ContestData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String title, int id){
            ContestData addInfo = null;
            addInfo = new ContestData();
            addInfo.setTitle(title);
            addInfo.setContests_id(id);
            mListData.add(addInfo);
        }


        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.search_item, null);

                holder.Title = (TextView) convertView.findViewById(R.id.stitle);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            ContestData mData = mListData.get(position);

            holder.Title.setText(mData.getTitle());

            return convertView;
        }
    }


}


