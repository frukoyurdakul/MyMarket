package com.furkanyurdakul.mymarket.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductDetail implements Parcelable
{
    @SerializedName("orderDetail")
    @Expose
    private String orderDetail;
    @SerializedName("summaryPrice")
    @Expose
    private Double summaryPrice;

    protected ProductDetail(Parcel in)
    {
        orderDetail = in.readString();
        if (in.readByte() == 0)
        {
            summaryPrice = null;
        }
        else
        {
            summaryPrice = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(orderDetail);
        if (summaryPrice == null)
        {
            dest.writeByte((byte) 0);
        }
        else
        {
            dest.writeByte((byte) 1);
            dest.writeDouble(summaryPrice);
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<ProductDetail> CREATOR = new Creator<ProductDetail>()
    {
        @Override
        public ProductDetail createFromParcel(Parcel in)
        {
            return new ProductDetail(in);
        }

        @Override
        public ProductDetail[] newArray(int size)
        {
            return new ProductDetail[size];
        }
    };

    public Double getSummaryPrice()
    {
        return summaryPrice != null ? summaryPrice : 0;
    }

    public String getOrderDetail()
    {
        return orderDetail != null ? orderDetail : "";
    }
}
