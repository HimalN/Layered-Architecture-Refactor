package com.example.layeredarchitecture.dao.custom;

import com.example.layeredarchitecture.model.OrderSummary;

import java.sql.SQLException;

public interface SQLQueryDAO {

    OrderSummary OrderDetails() throws SQLException, ClassNotFoundException;

}
