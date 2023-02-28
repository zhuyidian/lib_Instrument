package com.dunn.instrument.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunn.instrument.R;

import java.util.ArrayList;
import java.util.List;


public class AppsAdapter extends BaseAdapter {
    /**
     * 当前上下文
     */
    private Context mContext;

    /**
     * 数据源
     */
    private ArrayList<AppsBean> mListData;

    public AppsAdapter(Context mContext, ArrayList<AppsBean> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }

    @Override
    public int getCount() {
        return mListData == null ? 0 : mListData.size();
    }

    @Override
    public AppsBean getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_apps, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.isa_circle);
            holder.packageName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_identity = (TextView) convertView.findViewById(R.id.tv_identity);
            holder.tv_welcome = (TextView) convertView.findViewById(R.id.tv_welcome);
            holder.tv_voice = (TextView) convertView.findViewById(R.id.tv_voice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppsBean user = mListData.get(position);
        holder.packageName.setText(user.getPackageName());
        if (user.getIdentity() != null && user.getIdentity().length() > 0) {
            holder.tv_identity.setText(user.getIdentity());
            holder.tv_identity.setTextColor(mContext.getResources().getColor(R.color.tc_33));
        } else {
            holder.tv_identity.setText("sss");
            holder.tv_identity.setTextColor(mContext.getResources().getColor(R.color.tc_99));
        }

        if (user.getWelcome() != null && user.getWelcome().length() > 0) {
            holder.tv_welcome.setText(user.getWelcome());
            holder.tv_welcome.setTextColor(mContext.getResources().getColor(R.color.tc_33));
        } else {
            holder.tv_welcome.setText("ssss");
            holder.tv_welcome.setTextColor(mContext.getResources().getColor(R.color.tc_99));
        }

        //holder.tv_voice.setText(getVoice(mListData.get(position).getNameId()));

        if (mListData.get(position).getSamplePath() != null && mListData.get(position).getSamplePath().length() > 0) {
            try {
                //ImageLoder.getLoder().dispalyNoCache(mContext, mListData.get(position).getSamplePath(), holder.isa_circle);
            } catch (Exception e) {
                e.printStackTrace();
                //LogTools.debug("ai_camera_config", "view e=" + e);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView icon;//展示app图标
        TextView packageName; //app包名
        TextView tv_identity;//身份
        TextView tv_welcome; //欢迎语
        TextView tv_voice;//客户端声音
    }

    public void refresh(ArrayList<AppsBean> mListData) {
        this.mListData = mListData;
        notifyDataSetChanged();
    }
}
