package com.example.layeredarchitecture.dao.custom;

import com.example.layeredarchitecture.dao.CrudDAO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDAO extends CrudDAO<OrderDetailDTO> {
    boolean saveDetails(List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException;

}
