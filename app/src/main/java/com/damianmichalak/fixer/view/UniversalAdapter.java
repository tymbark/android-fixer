package com.damianmichalak.fixer.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.fixer.R;
import com.damianmichalak.fixer.presenter.MainActivityPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class UniversalAdapter extends RecyclerView.Adapter<UniversalAdapter.BaseViewHolder> implements Action1<List<BaseAdapterItem>> {

    private static final int ITEM_RATING = 0;
    private static final int ITEM_DATE = 1;

    private List<BaseAdapterItem> items = new ArrayList<>();

    @Inject
    LayoutInflater inflater;

    @Inject
    UniversalAdapter() {
    }

    @Override
    public UniversalAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_DATE:
                return new DateViewHolder(inflater.inflate(R.layout.date_item_layout, parent, false));
            case ITEM_RATING:
                return new RatingViewHolder(inflater.inflate(R.layout.rating_item_layout, parent, false));
            default:
                throw new IllegalStateException("Cannot find view for : " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(UniversalAdapter.BaseViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        final BaseAdapterItem baseAdapterItem = items.get(position);
        if (baseAdapterItem instanceof MainActivityPresenter.DateAdapterItem) {
            return ITEM_DATE;
        } else if (baseAdapterItem instanceof MainActivityPresenter.RatingAdapterItem) {
            return ITEM_RATING;
        } else {
            throw new IllegalStateException("Unsupported adapter item: " + baseAdapterItem.getClass().getSimpleName());
        }
    }

    @Override
    public void call(List<BaseAdapterItem> baseAdapterItems) {
        items = baseAdapterItems;
        notifyDataSetChanged();
    }

    abstract class BaseViewHolder extends RecyclerView.ViewHolder {

        BaseViewHolder(View itemView) {
            super(itemView);
        }

        abstract void bind(BaseAdapterItem item);
    }

    class DateViewHolder extends UniversalAdapter.BaseViewHolder {

        @BindView(R.id.date_item_layout_text)
        TextView date;


        DateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(BaseAdapterItem item) {
            date.setText(((MainActivityPresenter.DateAdapterItem) item).getDate());
        }


    }

    class RatingViewHolder extends UniversalAdapter.BaseViewHolder {

        @BindView(R.id.rating_item_layout_text)
        TextView rating;

        RatingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(BaseAdapterItem item) {
            final MainActivityPresenter.RatingAdapterItem adapterItem = (MainActivityPresenter.RatingAdapterItem) item;
            rating.setText(adapterItem.getName() + " " + adapterItem.getNumber());
        }

    }

}
