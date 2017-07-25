package com.damianmichalak.fixer.view;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.damianmichalak.fixer.R;
import com.damianmichalak.fixer.presenter.OpenDetailsActivityArguments;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    private static final String EXTRA_ARGS = "extra_args";

    @BindView(R.id.details_name)
    TextView nameTextView;
    @BindView(R.id.details_rating)
    TextView ratingTextView;
    @BindView(R.id.details_text)
    TextView text;

    public static Intent newIntent(@Nonnull Context context, OpenDetailsActivityArguments args) {
        return new Intent(context, DetailsActivity.class).putExtra(EXTRA_ARGS, args);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.details_activity_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        final String name = ((OpenDetailsActivityArguments) getIntent().getSerializableExtra(EXTRA_ARGS)).getName();
        final String rating = ((OpenDetailsActivityArguments) getIntent().getSerializableExtra(EXTRA_ARGS)).getRating();
        final String date = ((OpenDetailsActivityArguments) getIntent().getSerializableExtra(EXTRA_ARGS)).getDate();

        nameTextView.setText(name);
        ratingTextView.setText(rating);
        text.setText(getString(R.string.details_activity_text, date));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
