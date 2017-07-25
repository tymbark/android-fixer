package com.damianmichalak.fixer.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.damianmichalak.fixer.R;
import com.damianmichalak.fixer.presenter.MainActivityPresenter;
import com.damianmichalak.fixer.presenter.OpenDetailsActivityArguments;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class UniversalAdapter extends RecyclerView.Adapter<UniversalAdapter.BaseViewHolder> implements Action1<List<BaseAdapterItem>>, HeaderDecoration.StickyHeaderInterface {

    private static final int ITEM_RATING = 0;
    private static final int ITEM_DATE = 1;
    private static final int ITEM_PROGRESS = 2;

    private List<BaseAdapterItem> items = new ArrayList<>();

    @Inject
    LayoutInflater inflater;

    @Inject
    Activity activity;

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
            case ITEM_PROGRESS:
                return new ProgressViewHolder(inflater.inflate(R.layout.progress_item_layout, parent, false));
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
        } else if (baseAdapterItem instanceof MainActivityPresenter.ProgressLoadingItem) {
            return ITEM_PROGRESS;
        } else {
            throw new IllegalStateException("Unsupported adapter item: " + baseAdapterItem.getClass().getSimpleName());
        }
    }

    @Override
    public void call(List<BaseAdapterItem> baseAdapterItems) {
        items = baseAdapterItems;
        notifyDataSetChanged();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.date_item_layout;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        final BaseAdapterItem item = items.get(headerPosition);
        ((TextView) header.findViewById(R.id.date_item_layout_text))
                .setText(((MainActivityPresenter.DateAdapterItem) item).getDate());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        return items.get(itemPosition).isHeader();
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

        @BindView(R.id.rating_item_layout_text_value)
        TextView value;
        @BindView(R.id.rating_item_layout_text_currency)
        TextView currency;

        private final View itemView;

        RatingViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bind(BaseAdapterItem item) {
            final MainActivityPresenter.RatingAdapterItem adapterItem = (MainActivityPresenter.RatingAdapterItem) item;
            currency.setText(adapterItem.getName());
            value.setText(String.valueOf(adapterItem.getNumber()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = DetailsActivity.newIntent(activity, new OpenDetailsActivityArguments(adapterItem.getName(), String.valueOf(adapterItem.getNumber()), adapterItem.getDate()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        final Pair<View, String> pair1 = Pair.<View, String>create(value, value.getTransitionName());
                        final Pair<View, String> pair2 = Pair.<View, String>create(currency, currency.getTransitionName());

                        //noinspection unchecked
                        ActivityCompat.startActivity(activity, intent,
                                ActivityOptionsCompat
                                        .makeSceneTransitionAnimation(activity, pair1, pair2)
                                        .toBundle());
                    } else {
                        activity.startActivity(intent);
                    }
                }
            });
        }

    }

    private class ProgressViewHolder extends UniversalAdapter.BaseViewHolder {

        ProgressViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(BaseAdapterItem item) {
            ((MainActivityPresenter.ProgressLoadingItem) item).getLoadMore().onNext(null);
        }

    }

}
