package com.app.ptt.comnha.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.app.ptt.comnha.R;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_selection_news, linear_selection_reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        linear_selection_news = (LinearLayout) findViewById(R.id.linear_selection_news_adminact);
        linear_selection_reports = (LinearLayout) findViewById(R.id.linear_selection_reports_adminact);
        linear_selection_reports.setOnClickListener(this);
        linear_selection_news.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_selection_news_adminact:
                Intent intent_news = new Intent(this, NewsNotifyAdminActivity.class);
                startActivity(intent_news);
                break;
            case R.id.linear_selection_reports_adminact:
//                Intent intent_reports = new Intent(this, NewsNotifyAdminActivity.class);
//                startActivity(intent_reports);
                break;
        }
    }
}
