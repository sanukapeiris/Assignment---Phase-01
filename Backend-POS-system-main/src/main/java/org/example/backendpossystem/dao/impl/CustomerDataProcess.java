package org.example.backendpossystem.dao.impl;

import org.example.backendpossystem.dao.CustomerData;
import org.example.backendpossystem.dto.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDataProcess implements CustomerData {

    static String SAVE_CUSTOMER = "INSERT INTO customer(customerId,customerName,customerEmail,customerAddress,customerPhone) VALUES (?,?,?,?,?)";
    static String GET_CUSTOMER = "SELECT * FROM customer WHERE customerId = ?";
    static String DELETE_CUSTOMER = "DELETE FROM customer WHERE customerId = ?";
    static String UPDATE_CUSTOMER = "UPDATE customer SET customerName = ?,customerEmail = ?,customerAddress = ?,customerPhone = ? WHERE customerId = ?";
    static  String GET_ALL_CUSTOMER = "SELECT * FROM customer";
    static String SEARCH_CUSTOMER = "SELECT * FROM customer WHERE LOWER(customerId) = ? OR LOWER(customerName) LIKE ? OR LOWER(customerEmail) LIKE ? OR LOWER(customerAddress) LIKE ? OR LOWER(customerPhone) = ?";
    static String GET_CUSTOMER_NAME = "SELECT customerName FROM customer WHERE LOWER(customerName) LIKE ?";


    @Override
    public Customer getCustomer(String customerId, Connection connection) throws SQLException {
        Customer customer = new Customer();
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(GET_CUSTOMER);
            preparedStatement.setString(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                customer.setCustomerId(resultSet.getString("customerId"));
                customer.setCustomerName(resultSet.getString("customerName"));
                customer.setCustomerEmail(resultSet.getString("customerEmail"));
                customer.setCustomerAddress(resultSet.getString("customerAddress"));
                customer.setCustomerPhone(resultSet.getString("customerPhone"));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    @Override
    public String saveCustomer(Customer customer, Connection connection) throws SQLException {
        String message = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CUSTOMER);
            preparedStatement.setString(1, customer.getCustomerId());
            preparedStatement.setString(2, customer.getCustomerName());
            preparedStatement.setString(3, customer.getCustomerEmail());
            preparedStatement.setString(4, customer.getCustomerAddress());
            preparedStatement.setString(5, customer.getCustomerPhone());
            if(preparedStatement.executeUpdate() != 0){
                message ="Customer with id "+ customer.getCustomerId() + "has been saved";
            }
            else {
                message = "Customer with id "+ customer.getCustomerId() + "has not been saved";
            }
            preparedStatement.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public boolean deleteCustomer(String customerId, Connection connection) throws SQLException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CUSTOMER);
            preparedStatement.setString(1, customerId);
            return preparedStatement.executeUpdate() !=0;
        } catch (SQLException e){
            throw e;
        }
    }

    @Override
    public boolean updateCustomer(String customerId, Customer customerDto, Connection connection) throws SQLException {
        try(var pstm = connection.prepareStatement(UPDATE_CUSTOMER)) {
            pstm.setString(5,customerDto.getCustomerId());
            pstm.setString(1,customerDto.getCustomerName());
            pstm.setString(2,customerDto.getCustomerEmail());
            pstm.setString(3,customerDto.getCustomerAddress());
            pstm.setString(4,customerDto.getCustomerPhone());

            return pstm.executeUpdate() != 0;
        } catch (SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Customer> getAllCustomer(Connection connection) throws SQLException {
        List<Customer> customerList =new ArrayList<>();
        try(var pstm = connection.prepareStatement(GET_ALL_CUSTOMER)) {
            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){
                Customer customer = new Customer(
                        resultSet.getString("customerId"),
                        resultSet.getString("customerName"),
                        resultSet.getString("customerEmail"),
                        resultSet.getString("customerAddress"),
                        resultSet.getString("customerPhone")
                );
                customerList.add(customer);
            }
            resultSet.close();
            return customerList;
        }catch (SQLException e){
            throw e;
        }
    }

    @Override
    public List<Customer> searchCustomers(String query, Connection connection) throws SQLException {
        List<Customer> customerList = new ArrayList<>();

        try(PreparedStatement pstm = connection.prepareStatement(SEARCH_CUSTOMER)){
            pstm.setString(1,query);
            pstm.setString(2,"%" + query + "%");
            pstm.setString(3,"%" + query + "%");
            pstm.setString(4,"%" + query + "%");
            pstm.setString(5,query);

            ResultSet resultSet = pstm.executeQuery();
            while (resultSet.next()){
                Customer customer = new Customer();
                customer.setCustomerId(resultSet.getString("customerId"));
                customer.setCustomerName(resultSet.getString("customerName"));
                customer.setCustomerEmail(resultSet.getString("customerEmail"));
                customer.setCustomerAddress(resultSet.getString("customerAddress"));
                customer.setCustomerPhone(resultSet.getString("customerPhone"));
                customerList.add(customer);
            }
        }
        return customerList;
    }

    @Override
    public List<String> getNameSuggestions(String query, Connection connection) throws SQLException {
        List<String> nameList =new ArrayList<>();
        try(PreparedStatement pstm = connection.prepareStatement(GET_CUSTOMER_NAME)) {
            pstm.setString(1, query + "%");

            ResultSet rst = pstm.executeQuery();
            while (rst.next()){
                nameList.add(rst.getString("customerName"));
            }
        }catch (SQLException e){
            throw(e);
        }
        return nameList;
    }
}
