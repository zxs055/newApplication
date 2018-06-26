package com.kingl.zxs.klapplication.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingl.zxs.klapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/6/25.
 */

public class GridViewAdapter extends BaseAdapter {
    private int ScreenWidth;
    private Context context;
    private LayoutInflater infater;
    private List<Map<String, Object>> mlist = new ArrayList();

    public GridViewAdapter(Context paramContext, int paramInt)
    {
        this.context = paramContext;
        this.infater = LayoutInflater.from(paramContext);
        this.ScreenWidth = paramInt;
    }

    private int dp2px(int paramInt)
    {
        return (int) TypedValue.applyDimension(1, paramInt, this.context.getResources().getDisplayMetrics());
    }

    public int getCount()
    {
        return this.mlist.size();
    }

    public Object getItem(int paramInt)
    {
        return this.mlist.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
        ViewHolder localViewHolder;
        Object localObject2;
        TextView localTextView;
        if (paramView == null)
        {
            localViewHolder = new ViewHolder();
            paramView = this.infater.inflate(R.layout.item_gridview_layout, null);
            localViewHolder.imageview = ((ImageView)paramView.findViewById(R.id.gridtupian));
            localViewHolder.textview = ((TextView)paramView.findViewById(R.id.gridtxt));
            paramView.setTag(localViewHolder);
            Map localMap = (Map)this.mlist.get(paramInt);
            Object localObject1 = localMap.get("image");
            if (localObject1 != null)
                localViewHolder.imageview.setImageResource(Integer.parseInt(localObject1.toString()));
            localObject2 = localMap.get("name");
            localTextView = localViewHolder.textview;
            if (localObject2 != null)
            {
                String str = localObject2.toString();
                localTextView.setText(str);
                localViewHolder = (ViewHolder)paramView.getTag();
            }
            if(paramInt%2==0){

            }else{
                Drawable drawable=context.getResources().getDrawable(R.drawable.app_click_white);
                paramView.setBackground(drawable);
            }
        }
        return paramView;
    }

    public void setData(List<Map<String, Object>> paramList)
    {
        this.mlist = paramList;
        notifyDataSetChanged();
    }

    class ViewHolder
    {
        ImageView imageview = null;
        TextView textview = null;

        ViewHolder()
        {
        }
    }
}
