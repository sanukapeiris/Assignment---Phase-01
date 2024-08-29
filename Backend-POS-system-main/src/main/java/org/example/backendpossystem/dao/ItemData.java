package org.example.backendpossystem.dao;

import org.example.backendpossystem.dto.Item;
import org.example.backendpossystem.dto.OrderDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ItemData {
    Item getItem(String itemCode, Connection connection) throws SQLException;

    String saveItem(Item ItemDto, Connection connection) throws SQLException;

    boolean deleteItem(String itemCode, Connection connection) throws SQLException;

    boolean updateItem(String itemCode, Item itemDto, Connection connection) throws SQLException;

    List<Item> getAllItem(Connection connection) throws SQLException;
    public List<Item> searchItem(String query, Connection connection) throws SQLException;

    List<String> getNameSuggestions(String query, Connection connection) throws SQLException;

    /*boolean updateItemQty(Connection connection, OrderDetail orderDetailDto);*/
}
