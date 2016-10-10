package com.grgbanking.pdamodel.activity;

import com.grgbanking.pdamodel.R;
import com.grgbanking.pdamodel.uhf.PowerGrade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**   
 * 功能测试菜单选项
 * @version   V1.0    
 */

public class TestModelActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    /**
     * @Title: odsTest
     * @Description: TODO(条形码扫描测试)
     * @param v 
     */
    public void odsTest(View v) {
        startActivity(new Intent(this, TestODSScanActivity.class));
    }

    /**
     * @Title: odsTest
     * @Description: TODO(超高频扫描测试)
     * @param v 
     */
    public void uhfTest1(View v) {
        Intent i = new Intent(this, TestUHFActivity.class);
        i.putExtra("PowerGrade", PowerGrade.GRADE_ONE);
        startActivity(i);
    }

    /**
     * @Title: odsTest
     * @Description: TODO(超高频扫描测试)
     * @param v 
     */
    public void uhfTest2(View v) {
        Intent i = new Intent(this, TestUHFActivity.class);
        i.putExtra("PowerGrade", PowerGrade.GRADE_TWO);
        startActivity(i);
    }

    /**
     * @Title: odsTest
     * @Description: TODO(超高频扫描测试)
     * @param v 
     */
    public void uhfTest3(View v) {
        Intent i = new Intent(this, TestUHFActivity.class);
        i.putExtra("PowerGrade", PowerGrade.GRADE_THREE);
        startActivity(i);
    }

    /**
     * @Title: odsTest
     * @Description: TODO(指纹扫描测试)
     * @param v 
     */
    public void fingerprintTest(View v) {
        Intent i = new Intent(this, TestFingerPrintActivity.class);
        startActivity(i);
    }
}
