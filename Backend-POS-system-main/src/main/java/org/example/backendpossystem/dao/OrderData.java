package org.example.backendpossystem.dao;

import org.example.backendpossystem.dto.Item;
import org.example.backendpossystem.dto.Order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface OrderData {
    Item getOrder(String orderId, Connection connection) throws SQLException;

    boolean saveOrder(Order orderDto, Connection connection) throws SQLException;

    boolean deleteOrder(String orderId, Connection connection) throws SQLException;

    boolean updateOrder(String orderId, Order orderDto, Connection connection) throws SQLException;

    List<Order> getAllItem(Connection connection) throws SQLException;
    public List<Order> searchItem(String query, Connection connection) throws SQLException;

    List<String> getNameSuggestions(String query, Connection connection) throws SQLException;
}
