package com.dunn.instrument.function.keepalive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dunn.instrument.R;

import java.util.ArrayList;


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

    public void setList(ArrayList<AppsBean> list) {
        if (list != null) {
            mListData = (ArrayList<AppsBean>) list.clone();
            notifyDataSetChanged();
        }
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
            holder.tv_icon = (ImageView) convertView.findViewById(R.id.tv_icon);
            holder.tv_label = (TextView) convertView.findViewById(R.id.tv_label);
            holder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            holder.tv_packagename = (TextView) convertView.findViewById(R.id.tv_packagename);
            holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AppsBean user = mListData.get(position);
        //icon
        if(user.getIcon()!=null){
            holder.tv_icon.setImageDrawable(user.getIcon());
        }
        //label
        holder.tv_label.setText(user.getLabel());
        //type
//        String typeTip;
//        String typetip1="",typetip2="",typetip3="";
//        if(user.getIsSystem()){
//            typetip1="[系统应用]";
//        }
//        if(user.getIsPersistent()){
//            typetip2="[persistent应用]";
//        }
//        if(user.getIsPrivileged()){
//            typetip3="[特权应用]";
//        }
//        typeTip = typetip1+typetip2+typetip3;
//        holder.tv_type.setText(typeTip);
        //packagename
        holder.tv_packagename.setText(user.getPackageName());
        //state
        String stateTip;
        String tip1="",tip2="";
        if(user.getCtl().getIsAutoStartByUserCtl()){
            if(user.getCtl().getIsAutoStartToAllow()==1){
                tip1="[允许自启动]";
            }else{
                tip1="[不允许自启动]";
            }
        }else{
            tip1="[错误]";
        }
        if(user.getCtl().getIsBackgroundRunByUserCtl()){
            if(user.getCtl().getBackgroundRunToStrategy()==0){
                tip2="[智能调控]";
            }else if(user.getCtl().getBackgroundRunToStrategy()==2){
                tip2="[保持后台运行]";
            }else if(user.getCtl().getBackgroundRunToStrategy()==1){
                tip2="[限制后台运行]";
            }else{
                tip2="[未知]";
            }
        }else{
            tip2="[错误]";
        }
        stateTip = tip1+tip2;
        holder.tv_state.setText(stateTip);

//        if (user.getIdentity() != null && user.getIdentity().length() > 0) {
//            holder.tv_identity.setText(user.getIdentity());
//            holder.tv_identity.setTextColor(mContext.getResources().getColor(R.color.tc_33));
//        } else {
//            holder.tv_identity.setText("sss");
//            holder.tv_identity.setTextColor(mContext.getResources().getColor(R.color.tc_99));
//        }
//
//        if (user.getWelcome() != null && user.getWelcome().length() > 0) {
//            holder.tv_welcome.setText(user.getWelcome());
//            holder.tv_welcome.setTextColor(mContext.getResources().getColor(R.color.tc_33));
//        } else {
//            holder.tv_welcome.setText("ssss");
//            holder.tv_welcome.setTextColor(mContext.getResources().getColor(R.color.tc_99));
//        }

        //holder.tv_voice.setText(getVoice(mListData.get(position).getNameId()));

//        if (mListData.get(position).getSamplePath() != null && mListData.get(position).getSamplePath().length() > 0) {
//            try {
//                //ImageLoder.getLoder().dispalyNoCache(mContext, mListData.get(position).getSamplePath(), holder.isa_circle);
//            } catch (Exception e) {
//                e.printStackTrace();
//                //LogTools.debug("ai_camera_config", "view e=" + e);
//            }
//        }
        return convertView;
    }

    private class ViewHolder {
        ImageView tv_icon;//展示app图标
        TextView tv_label;//名称
        TextView tv_type;//应用类型
        TextView tv_packagename; //app包名
        TextView tv_state; //设置状态
    }

    public void refresh(ArrayList<AppsBean> mListData) {
        this.mListData = mListData;
        notifyDataSetChanged();
    }
}
