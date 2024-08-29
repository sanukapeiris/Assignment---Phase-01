package org.example.backendpossystem.dao.impl;

import org.example.backendpossystem.dao.ItemData;
import org.example.backendpossystem.dto.Item;
import org.example.backendpossystem.dto.OrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDataProcess implements ItemData {
    static String SAVE_ITEM = "INSERT INTO item(itemCode,itemName,itemQuantity,itemPrice) VALUES (?,?,?,?)";
    static String GET_ITEM = "SELECT * FROM item WHERE itemCode = ?";
    static String DELETE_ITEM = "DELETE FROM item WHERE itemCode = ?";
    static String UPDATE_ITEM = "UPDATE item SET itemName = ?,itemQuantity = ?, itemPrice = ? WHERE itemCode = ?";
    static  String GET_ALL_ITEM = "SELECT * FROM item";
    static String SEARCH_ITEM = "SELECT * FROM item WHERE LOWER(itemCode) = ? OR LOWER(itemName) LIKE ? ";
    static String GET_ITEM_NAME = "SELECT itemName FROM item WHERE LOWER(itemName) LIKE ?";
    static String CHANGE_ITEM_QTY = "UPDATE item SET itemQuantity = (itemQuantity - ?) WHERE LOWER(itemCode) = ?";
    @Override
    public Item getItem(String itemCode, Connection connection) throws SQLException {
        Item itemDto = new Item();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(GET_ITEM);
            preparedStatement.setString(1, itemCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                itemDto.setItemCode(resultSet.getString("itemCode"));
                itemDto.setItemName(resultSet.getString("itemName"));
                itemDto.setItemQuantity(resultSet.getInt("itemQuantity"));
                itemDto.setItemPrice(resultSet.getInt("itemPrice"));
            }
            resultSet.close();
            preparedStatement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return itemDto;    }

    @Override
    public String saveItem(Item ItemDto, Connection connection) throws SQLException {
        String message = "";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_ITEM);
            preparedStatement.setString(1,ItemDto.getItemCode());
            preparedStatement.setString(2,ItemDto.getItemName());
            preparedStatement.setInt(3,ItemDto.getItemQuantity());
            preparedStatement.setInt(4,ItemDto.getItemPrice());
            if (preparedStatement.executeUpdate() != 0){
                message = "Customer with id " + ItemDto.getItemCode() + " has been saved";
            }
            else {
                message = "Customer with id " + ItemDto.getItemCode() + " has not been saved";
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public boolean deleteItem(String itemCode, Connection connection) throws SQLException {
        try {
            PreparedStatement pstm = connection.prepareStatement(DELETE_ITEM);
            pstm.setString(1, itemCode);
            return pstm.executeUpdate() != 0;
        }
        catch (SQLException e){
            throw e;
        }
    }

    @Override
    public boolean updateItem(String itemCode, Item itemDto, Connection connection) throws SQLException {
        try(var pstm = connection.prepareStatement(UPDATE_ITEM)){
            pstm.setString(1,itemDto.getItemName());
            pstm.setInt(2,itemDto.getItemQuantity());
            pstm.setInt(3,itemDto.getItemPrice());
            pstm.setString(4,itemCode);

            return pstm.executeUpdate() != 0;
        }
        catch (SQLException e){
            throw e;
        }
    }

    @Override
    public List<Item> getAllItem(Connection connection) throws SQLException {
        List<Item> itemDtoList = new ArrayList<>();
        try(var pstm = connection.prepareStatement(GET_ALL_ITEM)){
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){
                Item itemDto = new Item(
                        resultSet.getString("itemCode"),
                        resultSet.getString("itemName"),
                        resultSet.getInt("itemQuantity"),
                        resultSet.getInt("itemPrice")
                );
                itemDtoList.add(itemDto);
            }
            resultSet.close();
            return itemDtoList;
        }
        catch (SQLException e){
            throw e;
        }
    }

    @Override
    public List<Item> searchItem(String query, Connection connection) throws SQLException {
        List<Item> itemDtoList = new ArrayList<>();
        try(var pstm = connection.prepareStatement(SEARCH_ITEM)){
            pstm.setString(1,query);
            pstm.setString(2,"%"+query+"%");
            pstm.setString(3,"%"+query+"%");
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){
                Item itemDto = new Item();
                itemDto.setItemCode(resultSet.getString("itemCode"));
                itemDto.setItemName(resultSet.getString("itemName"));
                itemDto.setItemQuantity(resultSet.getInt("itemQuantity"));
                itemDto.setItemPrice(resultSet.getInt("itemPrice"));
                itemDtoList.add(itemDto);
            }
            resultSet.close();
            return itemDtoList;
        }
        catch (SQLException e){
            throw e;
        }
    }

    @Override
    public List<String> getNameSuggestions(String query, Connection connection) throws SQLException {
        List<String> suggestions = new ArrayList<>();
        try(var pstm = connection.prepareStatement(GET_ITEM_NAME)){
            pstm.setString(1,query+"%");

            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){
                suggestions.add(resultSet.getString("itemName"));
            }
            resultSet.close();
        }
        catch (SQLException e){
            throw e;
        }
        return suggestions;
    }

    /*@Override
    public boolean updateItemQty(Connection connection, OrderDetail orderDetailDto) {
        try(var pstm = connection.prepareStatement(CHANGE_ITEM_QTY)){
            pstm.setInt(1,orderDetailDto.getQty());
            pstm.setString(2,orderDetailDto.getItemId());
            return pstm.executeUpdate() !=0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
}
