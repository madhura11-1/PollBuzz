package com.PollBuzz.pollbuzz;

import com.google.android.material.card.MaterialCardView;

import com.PollBuzz.pollbuzz.polls.Descriptive_type_poll;
import com.PollBuzz.pollbuzz.polls.Image_type_poll;
import com.PollBuzz.pollbuzz.polls.Multiple_type_poll;
import com.PollBuzz.pollbuzz.polls.Ranking_type_poll;
import com.PollBuzz.pollbuzz.polls.Single_type_poll;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PollList extends AppCompatActivity {

    private MaterialCardView card1,card2,card3,card4,card5;
    private TextView page_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_list);
        setGlobals();
        setListeners();
    }

    private void setListeners() {
        card1.setOnClickListener(view -> {
            Intent intent = new Intent(PollList.this, Single_type_poll.class);
            startActivity(intent);
        });

        card2.setOnClickListener(view -> {
            Intent intent = new Intent(PollList.this, Multiple_type_poll.class);
            startActivity(intent);
        });

        card3.setOnClickListener(view -> {
            Intent intent = new Intent(PollList.this, Descriptive_type_poll.class);
            startActivity(intent);
        });

        card4.setOnClickListener(view -> {
            Intent intent = new Intent(PollList.this, Ranking_type_poll.class);
            startActivity(intent);
        });

        card5.setOnClickListener(view -> {
            Intent intent = new Intent(PollList.this, Image_type_poll.class);
            startActivity(intent);
        });
    }

    private void setGlobals() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        View view =getSupportActionBar().getCustomView();
        page_title=view.findViewById(R.id.page_title);
        page_title.setText("Polls");
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
    }
}
