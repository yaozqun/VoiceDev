package com.seatwe.zsws.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.seatwe.zsws.R;
import com.seatwe.zsws.model.Module;
import com.seatwe.zsws.ui.adapter.ModuleAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

  private ListView lv_module;

  private String[] modules_str;// 模块名称

  private int[] mudules_pic;// 模块图片

  private List<Module> modules;

  private ModuleAdapter adapter;// 适配器

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
  }

  /**
   * 初始化
   */
  public void init() {
    initData();
    initView();
  }

  /**
   * 初始化UI
   */
  public void initView() {
    lv_module = (ListView) findViewById(R.id.lv_module);
    // 设置适配器
    adapter = new ModuleAdapter(MainActivity.this, modules);
    lv_module.setAdapter(adapter);
  }

  /**
   * 初始化数据
   */
  public void initData() {
    modules_str = new String[] { getResources().getString(R.string.module_line),
        getResources().getString(R.string.module_task),
        getResources().getString(R.string.module_box) };
    mudules_pic = new int[] { R.mipmap.module_line, R.mipmap.module_task,
        R.mipmap.module_box };

    modules = new ArrayList<Module>();
    for (int i = 0; i < modules_str.length; i++) {
      Module module = new Module();
      module.setModuleName(modules_str[i]);
      module.setModulePic(mudules_pic[i]);
      modules.add(module);
    }
  }

}
