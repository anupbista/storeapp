package com.anupbista.store;

public class CheckoutItemsBill {
    private String productIDBill;
    private String productNameBill;
    private String productQuantityBill;
    private String productPriceBill;

    public CheckoutItemsBill(String productIDBill, String productNameBill, String productQuantityBill, String productPriceBill) {
        this.productIDBill = productIDBill;
        this.productNameBill = productNameBill;
        this.productQuantityBill = productQuantityBill;
        this.productPriceBill = productPriceBill;
    }

    public String getProductIDBill() {
        return productIDBill;
    }

    public String getProductNameBill() {
        return productNameBill;
    }

    public String getProductQuantityBill() {
        return productQuantityBill;
    }

    public String getProductPriceBill() {
        return productPriceBill;
    }
}
