//package com.hp.grocerystore.model.cart;
//
//public class CartItem {
//    private long id;
//    private String productName;
//    private String imageUrl;
//    private int stock;
//    private int quantity;
//    private double price;
//    private boolean selected = false;
//
//    public CartItem(long id, String productName, String imageUrl, int stock, int quantity, double price) {
//        this.id = id;
//        this.productName = productName;
//        this.imageUrl = imageUrl;
//        this.stock = stock;
//        this.quantity = quantity;
//        this.price = price;
//    }
//
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public int getStock() {
//        return stock;
//    }
//
//    public void setStock(int stock) {
//        this.stock = stock;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public boolean isSelected() {
//        return selected;
//    }
//
//    public void setSelected(boolean selected) {
//        this.selected = selected;
//    }
//}

package com.hp.grocerystore.model.cart;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class CartItem implements Parcelable {
    private long id;
    private String productName;
    private String imageUrl;
    private int stock;
    private int quantity;
    private double price;
    private boolean selected;

    // Default constructor
    public CartItem() {
        this.selected = false;
    }

    // Parameterized constructor
    public CartItem(long id, String productName, String imageUrl, int stock, int quantity, double price) {
        this.id = id;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.quantity = quantity;
        this.price = price;
        this.selected = false;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Parcelable implementation
    protected CartItem(Parcel in) {
        id = in.readLong();
        productName = in.readString();
        imageUrl = in.readString();
        stock = in.readInt();
        quantity = in.readInt();
        price = in.readDouble();
        selected = in.readByte() != 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(productName);
        dest.writeString(imageUrl);
        dest.writeInt(stock);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}

//package com.hp.grocerystore.model.cart;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//import androidx.annotation.NonNull;
//
//public class CartItem implements Parcelable {
//    private long id;
//    private String productName;
//    private String imageUrl;
//    private int stock;
//    private int quantity;
//    private double price;
//    private boolean selected;
//
//    // Default constructor
//    public CartItem() {
//        this.selected = false;
//    }
//
//    // Parameterized constructor
//    public CartItem(long id, String productName, String imageUrl, int stock, int quantity, double price) {
//        this.id = id;
//        this.productName = productName;
//        this.imageUrl = imageUrl;
//        this.stock = stock;
//        this.quantity = quantity;
//        this.price = price;
//        this.selected = false;
//    }
//
//    // Getters and Setters
//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public int getStock() {
//        return stock;
//    }
//
//    public void setStock(int stock) {
//        this.stock = stock;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public boolean isSelected() {
//        return selected;
//    }
//
//    public void setSelected(boolean selected) {
//        this.selected = selected;
//    }
//
//    // Parcelable implementation
//    protected CartItem(Parcel in) {
//        id = in.readLong();
//        productName = in.readString();
//        imageUrl = in.readString();
//        stock = in.readInt();
//        quantity = in.readInt();
//        price = in.readDouble();
//        selected = in.readByte() != 0;
//    }
//
//    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
//        @Override
//        public CartItem createFromParcel(Parcel in) {
//            return new CartItem(in);
//        }
//
//        @Override
//        public CartItem[] newArray(int size) {
//            return new CartItem[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel dest, int flags) {
//        dest.writeLong(id);
//        dest.writeString(productName);
//        dest.writeString(imageUrl);
//        dest.writeInt(stock);
//        dest.writeInt(quantity);
//        dest.writeDouble(price);
//        dest.writeByte((byte) (selected ? 1 : 0));
//    }
//}