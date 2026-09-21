package pims.model;

public class SaleItem {
    private int medicineId, quantitySold;
    private String medicineName;
    private double priceAtSale;

    public SaleItem(int medicineId, String medicineName, int quantitySold, double priceAtSale) {
        this.medicineId = medicineId; this.medicineName = medicineName;
        this.quantitySold = quantitySold; this.priceAtSale = priceAtSale;
    }
    public int getMedicineId()      { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantitySold()    { return quantitySold; }
    public double getPriceAtSale()  { return priceAtSale; }
    public double getLineTotal()    { return quantitySold * priceAtSale; }
}