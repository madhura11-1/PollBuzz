package com.PollBuzz.pollbuzz.results;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.PollBuzz.pollbuzz.Utils.firebase;

public class PieChartActivity extends AppCompatActivity {
    TextView voters, question;
    Dialog dialog;
    AnyChartView anyChartView;
    ImageButton home, logout;
    firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view = getSupportActionBar().getCustomView();
        setGlobals(view);
        setActionBarFunctionality();
        showDialog();
        createPieChart();


        // anyChartView.setProgressBar(findViewById(R.id.progress_bar));


    }

    private void createPieChart() {
        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                String t;
                if (Integer.parseInt(event.getData().get("value")) < 2)
                    t = " Vote";
                else
                    t = " Votes";
                Toast.makeText(PieChartActivity.this, event.getData().get("x") + " : " + event.getData().get("value") + t, Toast.LENGTH_SHORT).show();
            }
        });
        Map<String, Integer> map = PercentageResult.data;
        int nonZeros=0;
        List<DataEntry> data = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));
            if(entry.getValue()!=0) nonZeros++;
        }
        pie.data(data);
        pie.legend().fontSize(25).fontColor("Black").fontWeight(500).fontFamily("Maven Pro");

        //pie.title(PercentageResult.question).zIndex(10);


        pie.labels().position("outside").fontColor("Black").fontSize(15).fontStyle("Bold");
        pie.selected().fontWeight(200);


        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        anyChartView.setZoomEnabled(true);
        anyChartView.setChart(pie);
        if(nonZeros==0){
            findViewById(R.id.noVotes).setVisibility(View.VISIBLE);
        }
        dialog.dismiss();
    }

    private void setGlobals(View view) {
        anyChartView = findViewById(R.id.any_chart_view);
        voters = findViewById(R.id.voters);
        question = findViewById(R.id.question);
        question.setText(PercentageResult.question);
        String voter = "Total Voters : " + String.valueOf(PercentageResult.total);
        voters.setText(voter);
        dialog = new Dialog(PieChartActivity.this);
        logout = view.findViewById(R.id.logout);
        home = view.findViewById(R.id.home);
        fb = new firebase();

    }

    private void showDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
    }

    private void setActionBarFunctionality() {
        home.setOnClickListener(v -> {
            Intent i = new Intent(PieChartActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });
        logout.setOnClickListener(v -> {
            fb.signOut(this);
        });
    }
}
