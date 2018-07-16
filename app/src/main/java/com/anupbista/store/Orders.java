package com.anupbista.store;

import android.graphics.Bitmap;

public class Orders {
    private String orderID;
    private String username;
    private String productname;
    private String productID;
    private String productquantity;
    private String orderDate;
    private String orderPrice;
    private String orderSize;
    private String orderColor;
    private String orderTime;
    private String orderProductID;
    private Bitmap orderProductImage;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Orders(String productname, String productquantity, String orderPrice, String orderSize, String orderColor, Bitmap orderProductImage, String status) {
        this.productname = productname;
        this.productquantity = productquantity;
        this.orderPrice = orderPrice;
        this.orderSize = orderSize;
        this.orderColor = orderColor;
        this.orderProductImage = orderProductImage;
        this.status = status;
    }

    public Bitmap getOrderProductImage() {
        return orderProductImage;
    }

    public void setOrderProductImage(Bitmap orderProductImage) {
        this.orderProductImage = orderProductImage;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(String productquantity) {
        this.productquantity = productquantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(String orderSize) {
        this.orderSize = orderSize;
    }

    public String getOrderColor() {
        return orderColor;
    }

    public void setOrderColor(String orderColor) {
        this.orderColor = orderColor;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderProductID() {
        return orderProductID;
    }

    public void setOrderProductID(String orderProductID) {
        this.orderProductID = orderProductID;
    }
}
