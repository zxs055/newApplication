package com.kingl.zxs.klapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 网格视图
 * Created by Administrator on 2018/6/25.
 */

public class TagsGridView extends GridView {
    public TagsGridView(Context context) {
        super(context);
    }

    public TagsGridView(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
    }

    public TagsGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //就是生成一个符合MeasureSpec的一个32位的包含测量模式和测量高度的int值。
        int expandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
//        最大模式（MeasureSpec.AT_MOST）
//        这个也就是父组件，能够给出的最大的空间
        super.onMeasure(widthMeasureSpec, expandSpec);//代表测量的高度
    }
}
