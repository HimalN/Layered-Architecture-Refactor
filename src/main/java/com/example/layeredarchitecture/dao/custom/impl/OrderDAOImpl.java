package com.example.layeredarchitecture.dao.custom.impl;

import com.example.layeredarchitecture.dao.SQLUtil;
import com.example.layeredarchitecture.dao.custom.ItemDAO;
import com.example.layeredarchitecture.dao.custom.OrderDAO;
import com.example.layeredarchitecture.dao.custom.OrderDetailDAO;
import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.OrderDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    /*To refactor the transaction we need to brake down the code for smaller parts
      1.Need to have a orderDetailDAO to implementation of the saving the order details
      2.Need to update the Item quantity
      3.Need to save the order we can leave the order saving logic here because there is no need to create another class
        just for saving the orders by doing that we can achieve High Cohesion*/

    /*The transaction starts when the setAutoCommit is false in state by doing this we can pass the values to the database temporarily ane the database will not save those data
      until the setAutoCommit is set to true*/
    /*The main advantage is we can rollback which means simply calling the rollback method if the order or item saving have an issue the database won't save the data until the error is fixed*/
    /*So the refactoring goes the same like items and customers create a DAO class and implement tbe queries then to achieve loosely coupling
    create an interface then override those methods through the inter face*/
    /*SO we can achieve the High Cohesion,less Boiler Plate Cods as possible,Loosely Coupling and Property Injection*/

    private OrderDetailDAO orderDetailImpl = new OrderDetailImpl();

    private ItemDAO itemDAO = new ItemDAOImpl();

    @Override
    public boolean existItem(String code) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT code FROM Item WHERE code=?",code);
        return resultSet.next();
    }

    @Override
    public boolean existCustomer(String id) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT id FROM Customer WHERE id=?",id);
        return resultSet.next();
    }

    @Override
    public String generateNextOrderID() throws SQLException, ClassNotFoundException {
        ResultSet rst = SQLUtil.execute("SELECT oid FROM `Orders` ORDER BY oid DESC LIMIT 1;");

        return rst.next() ? String.format("OID-%03d", (Integer.parseInt(rst.getString("oid").replace("OID-", "")) + 1)) : "OID-001";
    }

    @Override
    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException {
        /*Transaction*/
        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();
            boolean isExist = exist(orderId);
            /*if order id already exist*/
            if (isExist) {
                return false;
            }

            connection.setAutoCommit(false);

            /*Refactored*/
            boolean isSaved = save(new OrderDTO(orderId,orderDate,customerId));
            if (isSaved) {
                boolean isOrderDetailSaved = orderDetailImpl.saveDetails(orderDetails);
                if (isOrderDetailSaved) {
                    boolean isUpdated = itemDAO.updateItem(orderDetails);
                    if (isUpdated) {
                        connection.commit();
                    }
                }
            }
            connection.rollback();
            connection.setAutoCommit(true);
            return true;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public ArrayList<OrderDTO> getAll() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(OrderDTO dto) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO `Orders` (oid, date, customerID) VALUES (?,?,?)",dto.getOrderId(),
                dto.getOrderDate(),dto.getCustomerId());
    }

    @Override
    public boolean update(OrderDTO dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public void delete(String id) throws SQLException, ClassNotFoundException {

    }

    @Override
    public boolean exist(String id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public String generateID() throws SQLException, ClassNotFoundException {
        return null;
    }
}
