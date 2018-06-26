package com.kingl.zxs.klapplication.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingl.zxs.klapplication.R;
import com.kingl.zxs.klapplication.adapter.GridViewAdapter;
import com.kingl.zxs.klapplication.view.TagsGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkFragment extends Fragment implements AdapterView.OnItemClickListener{
    private Context context;
    private TagsGridView gridView;
    private GridViewAdapter mGridViewAdapter;
    private TextView titiltxt;
    private ImageButton backImgBtn;
    private ImageView scanImgBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_work,null);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.titiltxt=(TextView)getView().findViewById(R.id.titletxt);
        this.titiltxt.setText("主控台");
        this.backImgBtn=(ImageButton)getView().findViewById(R.id.back);
        this.scanImgBtn=(ImageView)getView().findViewById(R.id.scanImgBtn);
        this.backImgBtn.setVisibility(View.INVISIBLE);
        this.scanImgBtn.setVisibility(View.INVISIBLE);
        this.gridView=(TagsGridView)getView().findViewById(R.id.gridview);
        this.gridView.setOnItemClickListener(this);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        //获取屏幕分辨率信息
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.mGridViewAdapter=new GridViewAdapter(this.context,displayMetrics.widthPixels);
        this.gridView.setAdapter(mGridViewAdapter);
        new functionAsyncTask().execute(new Void[0]);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switchFunctionItem(((Map)((Adapter) parent.getAdapter()).getItem(position)).get("name").toString());
    }

    private void switchFunctionItem(String itemName) {
        Intent intent=new Intent();
        if(itemName.equals("销售管理")){
            intent.setClass(this.context,SaleActivity.class);
            startActivity(intent);
            return;
        }
    }

    class functionAsyncTask extends AsyncTask<Void, Void, List<Map<String, Object>>>
    {
        functionAsyncTask()
        {
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        protected List<Map<String, Object>> doInBackground(Void[] paramArrayOfVoid)
        {
            ArrayList localArrayList = new ArrayList();
            String[] arrayOfString = {  "采购管理", "","销售管理" ,"","仓库管理","","数字车间"};
            int[] arrayOfInt = { R.mipmap.ic_zhu_1, R.drawable.app_click_white,R.mipmap.ic_zhu_4 ,R.drawable.app_click_white, R.mipmap.ic_zhu_2, R.drawable.app_click_white,R.mipmap.ic_zhu_3};

            for (int i = 0; i<7; i++)
            {
                HashMap localHashMap = new HashMap();
                localHashMap.put("name", arrayOfString[i]);
                localHashMap.put("image", Integer.valueOf(arrayOfInt[i]));
                localArrayList.add(localHashMap);
            }
            return localArrayList;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */

        protected void onPostExecute(List<Map<String, Object>> paramList)
        {
            super.onPostExecute(paramList);
            WorkFragment.this.mGridViewAdapter.setData(paramList);
        }
    }
}
