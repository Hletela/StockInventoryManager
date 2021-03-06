/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import static Include.Common.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author terence
 */
public class Sell {
    
    private SimpleIntegerProperty sellID;
    private SimpleIntegerProperty sellPrice;
    private SimpleIntegerProperty totalPrice;
    private SimpleStringProperty sellDate;    
    private SimpleStringProperty sellName;
    private SimpleIntegerProperty sellQuantity;
    private SimpleStringProperty seller;
    private SimpleStringProperty client;
    private Product product ;

    public Sell() {
        
        this.sellID = new SimpleIntegerProperty(0);
        this.sellDate = new SimpleStringProperty("");
        this.sellPrice = new SimpleIntegerProperty(0);
        this.totalPrice = new SimpleIntegerProperty(0);
        this.sellQuantity = new SimpleIntegerProperty(0);
        this.product = new Product();
        this.sellName = new SimpleStringProperty("");
        this.seller = new SimpleStringProperty("");
        this.client = new SimpleStringProperty("");
        
    }

    public Sell(int sellID, int sellPrice, int totalPrice, String sellName, int sellQuantity, Product product) {
        this.sellID = new SimpleIntegerProperty(sellID);
        this.sellPrice = new SimpleIntegerProperty(sellPrice);
        this.totalPrice = new SimpleIntegerProperty(totalPrice);
        this.sellName = new SimpleStringProperty(sellName);
        this.sellQuantity = new SimpleIntegerProperty(sellQuantity);
        this.product = product;
    }
    

    public int getSellID() {
        return sellID.getValue();
    }

    public void setSellID(int sellID) {
        this.sellID = new SimpleIntegerProperty(sellID);
    }

    public int getSellPrice() {
        return sellPrice.getValue();
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = new SimpleIntegerProperty(sellPrice);
    }

    public String getSellDate() {
        return sellDate.getValue();
    }

    public void setSellDate(String sellDate) {
        this.sellDate = new SimpleStringProperty(sellDate);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSellName() {
        return sellName.getValue();
    }

    public void setSellName(String sellName) {
        this.sellName = new SimpleStringProperty(sellName);
    }

    public int getSellQuantity() {
        return sellQuantity.getValue();
    }

    public void setSellQuantity(int sellQuantity) {
        this.sellQuantity = new SimpleIntegerProperty(sellQuantity);
    }

    public int getTotalPrice() {
        return totalPrice.getValue();
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = new SimpleIntegerProperty(totalPrice);
    }

    public String getSeller() {
        return seller.getValue();
    }

    public void setSeller(String seller) {
        this.seller = new SimpleStringProperty(seller);
    }

    public String getClient() {
        return client.getValue();
    }

    public void setClient(String client) {
        this.client = new SimpleStringProperty(client);
    }
    

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sell other = (Sell) obj;
        return Objects.equals(this.sellID, other.sellID);
    }

    @Override
    public String toString() {
        return sellName.getValue() ;
    }
    
    public void delete() throws SQLException{
        
            try (Connection con = getConnection()) {
                String query = "DELETE FROM sell WHERE sell_id = ?";
                
                PreparedStatement ps = con.prepareStatement(query);
                
                ps.setInt(1, getSellID());
                
                ps.executeUpdate();
                
                query = "UPDATE product SET prod_quantity = prod_quantity + ?, nbrSells = nbrSells - 1 WHERE prod_id = ?";
                
                ps = con.prepareStatement(query);
                
                ps.setInt(2, getProduct().getProdID());
                ps.setInt(1, getSellQuantity());
                
                ps.executeUpdate();
                
                query = "UPDATE client SET remain = remain - ?, sells_count = sells_count - 1 WHERE fullname = ?";
                
                ps = con.prepareStatement(query);
                
                ps.setString(2, getClient());
                ps.setInt(1, getTotalPrice());
                
                ps.executeUpdate();
            
            }        
    }
    
    public String getTime() throws SQLException{

        try (Connection con = getConnection()) {
            String query = "SELECT time(sell_date) FROM sell WHERE sell_id = ?";

            PreparedStatement st;
            ResultSet rs;

            st = con.prepareStatement(query);
            st.setInt(1, getSellID());
            rs = st.executeQuery();

            while (rs.next()) {

                return rs.getString("time(sell_date)");

            }
        }

    return null;
    
    }
    
    public String getDate() throws SQLException{

        try (Connection con = getConnection()) {
            String query = "SELECT date(sell_date) FROM sell WHERE sell_id = ?";

            PreparedStatement st;
            ResultSet rs;

            st = con.prepareStatement(query);
            st.setInt(1, getSellID());
            rs = st.executeQuery();

            while (rs.next()) {

                return rs.getString("date(sell_date)");

            }
        }

    return null;
    
    }    
    
    public static ObservableList<Sell> getSellsByDate(String selectedDate, ResourceBundle rb) throws SQLException{
        
        ObservableList<Sell> data = FXCollections.observableArrayList();
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM sell INNER JOIN product ON sell.prod_id = product.prod_id INNER JOIN user ON sell.user_id = user.user_id INNER JOIN client ON sell.client_id = client.client_id WHERE date(sell_date) = ? ORDER BY time(sell_date) ASC";

            PreparedStatement st;
            ResultSet rs; 

            st = con.prepareStatement(query);
            st.setString(1,selectedDate);
            rs = st.executeQuery();

            while (rs.next()) {
                Sell sell = new Sell();
                sell.setSellID(rs.getInt("sell_id"));
                sell.setSellPrice(rs.getInt("sell.sell_price_unit"));
                sell.setTotalPrice(rs.getInt("sell.sell_price"));
                sell.setSellDate(rs.getTime("sell_date").toString());
                sell.setSellQuantity(rs.getInt("sell_quantity"));
                sell.setSeller(rs.getString("username"));
                if(rs.getString("client.fullname").equals(""))
                    sell.setClient(rb.getString("other"));
                else
                    sell.setClient(rs.getString("client.fullname"));

                Product product = new Product();
                product.setProdID(rs.getInt("prod_id"));
                product.setName(rs.getString("name"));
                product.setSellPrice(rs.getInt("product.sell_price"));
                product.setProdQuantity(rs.getInt("prod_quantity"));
                product.setAddDate(rs.getDate("add_date").toString());
                product.setNbrBuys(rs.getInt("nbrBuys"));
                product.setNbrSells(rs.getInt("nbrSells"));
                product.setImageURL(rs.getString("image_url"));

                sell.setProduct(product);
                sell.setSellName(rs.getString("name"));

                data.add(sell);
            }
        }
        
        return data;
        
    }
    
    public static ResultSet getTodayStats(String selectedDate) throws SQLException{
        
            Connection con = getConnection();
            String query = "";
            PreparedStatement st;
            ResultSet rs;
            if(selectedDate.equals("")){
                
                query = "SELECT count(*), SUM(sell_price), SUM(sell_quantity) FROM sell";
            }
            else{
                query = "SELECT count(*), SUM(sell_price), SUM(sell_quantity) FROM sell WHERE date(sell_date) = ? ";
            }
            
            st = con.prepareStatement(query);
            if(!selectedDate.equals("")){
                st.setString(1,selectedDate);
            }
            rs = st.executeQuery();
            
            return rs;
        
    }
}
