package com.furkanyurdakul.mymarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.furkanyurdakul.mymarket.R;
import com.furkanyurdakul.mymarket.models.MainScreenModel;

import java.time.Month;
import java.util.List;
import java.util.Locale;

/**
 * The adapter that main screen is using after fetching data from server.
 */
public class MainScreenAdapter extends RecyclerView.Adapter<ViewHolder>
{
    private final List<MainScreenModel> list;

    public MainScreenAdapter(List<MainScreenModel> list)
    {
        this.list = list;
    }

    public void addItem(MainScreenModel item)
    {
        list.add(item);
        notifyItemInserted(list.size() - 1);
    }

    public void removeItem(MainScreenModel item)
    {
        removeItemAtPosition(list.indexOf(item));
    }

    public void removeItemAtPosition(int position)
    {
        if (position < 0 || position >= list.size())
            throw new IllegalStateException("Position is not valid.");

        list.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Every data placement is handled within holder.bind()
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    private MainScreenModel getItem(int position)
    {
        return list.get(position);
    }
}

/**
 * The class that wraps the view data.
 */
class ViewHolder extends RecyclerView.ViewHolder
{
    // Month names are only in Turkish.
    private static final String[] MONTHS = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};

    // Format is only for Turkish.
    private static final String PRICEFORMAT = "%.1f TL";

    private final TextView dayTextView, monthTextView,
        marketNameTextView, orderNameTextView, priceTextView,
        orderStatusTextView, priceTextView2, productDetailsTextView;

    private final View arrowImageView, orderStatusView, orderStatusLayout, mainLayout;

    public ViewHolder(@NonNull View itemView)
    {
        super(itemView);
        dayTextView = itemView.findViewById(R.id.dayTextView);
        monthTextView = itemView.findViewById(R.id.monthTextView);
        marketNameTextView = itemView.findViewById(R.id.marketNameTextView);
        orderNameTextView = itemView.findViewById(R.id.orderNameTextView);
        priceTextView = itemView.findViewById(R.id.priceTextView);
        orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
        priceTextView2 = itemView.findViewById(R.id.priceTextView2);
        productDetailsTextView = itemView.findViewById(R.id.productDetailsTextView);
        arrowImageView = itemView.findViewById(R.id.arrowImageView);
        orderStatusView = itemView.findViewById(R.id.orderStatusView);
        orderStatusLayout = itemView.findViewById(R.id.orderStatusLayout);
        mainLayout = itemView.findViewById(R.id.mainLayout);
    }

    void bind(MainScreenModel item)
    {
        // Simple setting data calls. Null checks are done in getters.
        dayTextView.setText(item.getDate());
        monthTextView.setText(MONTHS[Integer.valueOf(item.getMonth()) - 1]);
        marketNameTextView.setText(item.getMarketName());
        orderNameTextView.setText(item.getOrderName());
        priceTextView.setText(String.format(Locale.getDefault(),
                PRICEFORMAT, item.getProductPrice()));
        orderStatusTextView.setText(item.getProductState());
        orderStatusTextView.setTextColor(getStatusTextColorByState(orderStatusView.getContext(),
                item.getProductState()));
        orderStatusView.setBackground(ContextCompat.getDrawable(orderStatusView.getContext(),
                getOrderStatusBackgroundByState(item.getProductState())));

        // Always check for null state.
        if (item.getProductDetail() != null)
        {
            productDetailsTextView.setText(item.getProductDetail().getOrderDetail());
            priceTextView2.setText(String.format(Locale.getDefault(),
                    PRICEFORMAT, item.getProductDetail().getSummaryPrice()));
        }

        // Here, if the status was tapped before, open the status layout, hide otherwise.
        orderStatusLayout.setVisibility(item.isOpened() ?
                View.VISIBLE : View.GONE);

        // Here, if the status was tapped before, rotate image view, keep same otherwise.
        arrowImageView.setRotation(item.isOpened() ? 90 : 0);

        // Set the click listener to toggle product detail state.
        // The state will change at "switchAndGetOpened" and the new state
        // will determine whether the details should be visible or not.
        mainLayout.setOnClickListener(v ->
        {
            boolean state = item.switchAndGetOpened();
            orderStatusLayout.setVisibility(state ? View.VISIBLE : View.GONE);

            // Rotate the arrow image view as well to show the user
            // that the details are open. Should be what it is after
            // closing the details.
            arrowImageView.setRotation(state ? 90 : 0);
        });
    }

    @DrawableRes
    private int getOrderStatusBackgroundByState(String state)
    {
        switch (state)
        {
            case "Hazırlanıyor":
                return R.drawable.orderstatus_pending_background;
            case "Onay Bekliyor":
                return R.drawable.orderstatus_error_background;
            default:
                return R.drawable.orderstatus_success_background;
        }
    }

    @ColorInt
    private int getStatusTextColorByState(Context context, String state)
    {
        switch (state)
        {
            case "Hazırlanıyor":
                return ContextCompat.getColor(context, R.color.pendingTextColor);
            case "Onay Bekliyor":
                return ContextCompat.getColor(context, R.color.errorTextColor);
            default:
                return ContextCompat.getColor(context, R.color.successTextColor);
        }
    }
}
