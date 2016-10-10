package com.seatwe.zsws.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seatwe.zsws.R;
import com.seatwe.zsws.model.Module;

import java.util.List;

public class ModuleAdapter extends BaseAdapter {
    private Activity context;

    private List<Module> info;

    public ModuleAdapter(Activity context, List<Module> info) {
        super();
        this.context = context;
        this.info = info;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int count = 0;
        if (info != null) {
            count = info.size();
        }
        return count;
    }

    @Override
    public Module getItem(int position) {
        // TODO Auto-generated method stub
        Module item = null;
        if (info != null) {
            item = info.get(position);
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_main_module, null);
            viewHolder.tv_moduleName = (TextView) convertView.findViewById(R.id.tv_layout_main_module);
            viewHolder.iv_modulePic = (ImageView) convertView.findViewById(R.id.iv_layout_main_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Module item = getItem(position);
        if (null != item) {
            viewHolder.tv_moduleName.setText(item.getModuleName());
            viewHolder.iv_modulePic.setBackgroundResource(item.getModulePic());

        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv_moduleName;// 模块名称

        ImageView iv_modulePic;// 模块图片
    }

}
