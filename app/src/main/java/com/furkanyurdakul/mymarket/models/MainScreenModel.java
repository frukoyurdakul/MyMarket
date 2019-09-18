package com.furkanyurdakul.mymarket.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainScreenModel implements Parcelable
{
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("month")
    @Expose
    private String month;
    @SerializedName("marketName")
    @Expose
    private String marketName;
    @SerializedName("orderName")
    @Expose
    private String orderName;
    @SerializedName("productPrice")
    @Expose
    private Double productPrice;
    @SerializedName("productState")
    @Expose
    private String productState;
    @SerializedName("productDetail")
    @Expose
    private ProductDetail productDetail;

    private boolean isOpened = false;

    protected MainScreenModel(Parcel in)
    {
        date = in.readString();
        month = in.readString();
        marketName = in.readString();
        orderName = in.readString();
        if (in.readByte() == 0)
        {
            productPrice = null;
        }
        else
        {
            productPrice = in.readDouble();
        }
        productState = in.readString();
        productDetail = in.readParcelable(ProductDetail.class.getClassLoader());
        isOpened = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(date);
        dest.writeString(month);
        dest.writeString(marketName);
        dest.writeString(orderName);
        if (productPrice == null)
        {
            dest.writeByte((byte) 0);
        }
        else
        {
            dest.writeByte((byte) 1);
            dest.writeDouble(productPrice);
        }
        dest.writeString(productState);
        dest.writeParcelable(productDetail, flags);
        dest.writeByte((byte) (isOpened ? 1 : 0));
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<MainScreenModel> CREATOR = new Creator<MainScreenModel>()
    {
        @Override
        public MainScreenModel createFromParcel(Parcel in)
        {
            return new MainScreenModel(in);
        }

        @Override
        public MainScreenModel[] newArray(int size)
        {
            return new MainScreenModel[size];
        }
    };

    public String getDate()
    {
        return date != null ? date : "1";
    }

    public String getMonth()
    {
        return month != null ? month : "";
    }

    public String getMarketName()
    {
        return marketName != null ? marketName : "";
    }

    public String getOrderName()
    {
        return orderName != null ? orderName : "";
    }

    public Double getProductPrice()
    {
        return productPrice != null ? productPrice : 0;
    }

    public String getProductState()
    {
        return productState != null ? productState : "";
    }

    public ProductDetail getProductDetail()
    {
        return productDetail;
    }

    public boolean switchAndGetOpened()
    {
        isOpened = !isOpened;
        return isOpened;
    }

    public boolean isOpened()
    {
        return isOpened;
    }
}
