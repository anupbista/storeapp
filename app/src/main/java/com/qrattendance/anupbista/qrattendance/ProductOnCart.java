package com.qrattendance.anupbista.qrattendance;

public class ProductOnCart {

    private String productID;
    private String productOnCartID;
    private String productName;
    private String productCat;
    private String productSize;
    private String productBrand;
    private String productColor;
    private String productPrice;
    private String productDesc;
    private String productQuantity;

    public ProductOnCart(String productName, String productCat, String productSize, String productBrand, String productColor, String productPrice, String productDesc, String productQuantity) {
        this.productName = productName;
        this.productCat = productCat;
        this.productSize = productSize;
        this.productBrand = productBrand;
        this.productColor = productColor;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productQuantity = productQuantity;
    }

    public ProductOnCart(String productID, String productOnCartID, String productName, String productCat, String productSize, String productBrand, String productColor, String productPrice, String productDesc, String productQuantity) {
        this.productID = productID;
        this.productOnCartID = productOnCartID;
        this.productName = productName;
        this.productCat = productCat;
        this.productSize = productSize;
        this.productBrand = productBrand;
        this.productColor = productColor;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productQuantity = productQuantity;
    }

    public ProductOnCart(String productID, String productName, String productCat, String productSize, String productBrand, String productColor, String productPrice, String productDesc, String productQuantity) {
        this.productID = productID;
        this.productName = productName;
        this.productCat = productCat;
        this.productSize = productSize;
        this.productBrand = productBrand;
        this.productColor = productColor;
        this.productPrice = productPrice;
        this.productDesc = productDesc;
        this.productQuantity = productQuantity;
    }

    public String getProductID() {
        return productID;
    }

    public String getProductOnCartID() {
        return productOnCartID;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCat() {
        return productCat;
    }

    public String getProductSize() {
        return productSize;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public String getProductColor() {
        return productColor;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductQuantity() {
        return productQuantity;
    }
}
