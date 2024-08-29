package org.example.backendpossystem.dao;

import org.example.backendpossystem.dto.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface CustomerData {
    Customer getCustomer(String customerId, Connection connection) throws SQLException;

    String saveCustomer(Customer customerDto, Connection connection) throws SQLException;

    boolean deleteCustomer(String customerId, Connection connection) throws SQLException;

    boolean updateCustomer(String customerId, Customer customerDto, Connection connection) throws SQLException;

    List<Customer> getAllCustomer(Connection connection) throws SQLException;
    public List<Customer> searchCustomers(String query, Connection connection) throws SQLException;

    List<String> getNameSuggestions(String query, Connection connection) throws SQLException;
}
