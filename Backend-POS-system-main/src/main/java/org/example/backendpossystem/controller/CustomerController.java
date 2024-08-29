package org.example.backendpossystem.controller;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backendpossystem.dao.impl.CustomerDataProcess;
import org.example.backendpossystem.dto.Customer;
import org.example.backendpossystem.util.UtilProcess;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/customer", loadOnStartup = 2)
public class CustomerController extends HttpServlet {
    Connection connection;

    @Override
    public void init(){
        try {
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/posdb");
            this.connection = pool.getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType()== null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        try(var writer = resp.getWriter()){

            Jsonb customerJson = JsonbBuilder.create();
            Customer customerDto = customerJson.fromJson(req.getReader(), Customer.class);
            CustomerDataProcess dataProcess = new CustomerDataProcess();
            customerDto.setCustomerId(UtilProcess.generateId());


            writer.write(dataProcess.saveCustomer(customerDto,connection));
            resp.setStatus(HttpServletResponse.SC_CREATED);
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        CustomerDataProcess dataProcess = new CustomerDataProcess();
        resp.setContentType("application/json")
        ;
        try(var writer = resp.getWriter()){
            var id = req.getParameter("customerId");


            boolean flag = dataProcess.deleteCustomer(id, connection);
            if(flag){
                writer.write("{\"status\":\"success\"}");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                writer.write("{\"status\":\"error\"}");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType() == null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        CustomerDataProcess dataProcess = new CustomerDataProcess();
        try(var writer = resp.getWriter()){
            String id = req.getParameter("customerId");
            Jsonb customerJson = JsonbBuilder.create();
            var updatedCustomer = customerJson.fromJson(req.getReader(), Customer.class);
            boolean b = dataProcess.updateCustomer(id, updatedCustomer, connection);
            if (b) {
                writer.write("{\"status\":\"success\"}");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                writer.write("{\"status\":\"error\"}");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        CustomerDataProcess dataProcess = new CustomerDataProcess();

        if (req.getHeader("Request-Type").equals("table")) {

            try (var writer = resp.getWriter()) {

                List<Customer> customerList = dataProcess.getAllCustomer(connection);

                JsonArrayBuilder jb = Json.createArrayBuilder();
                Jsonb jsonb = JsonbBuilder.create();

                for (Customer customerDto : customerList) {
                    var jasonObject = Json.createReader(new StringReader(jsonb.toJson(customerDto))).readObject();
                    jb.add(jasonObject);
                }
                writer.write(jb.build().toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else if (req.getHeader("Request-Type").equals("search")) {
            String query = req.getParameter("query").toLowerCase();


            try (var writer = resp.getWriter()) {
                List<Customer> customers = dataProcess.searchCustomers(query, connection);
                JsonArrayBuilder jb = Json.createArrayBuilder();
                Jsonb jsonb = JsonbBuilder.create();

                resp.setContentType("application/json");
                for (Customer customerDto : customers) {
                    var jasonObject = Json.createReader(new StringReader(jsonb.toJson(customerDto))).readObject();
                    jb.add(jasonObject);
                }
                writer.write(jb.build().toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
            }
        }
        else if (req.getHeader("Request-Type").equals("suggest")) {
            String query = req.getParameter("query").toLowerCase();

            try (var writer = resp.getWriter()) {
                List<String> names = dataProcess.getNameSuggestions(query, connection);
                resp.setContentType("application/json");

                JsonArrayBuilder jb = Json.createArrayBuilder();

                resp.setContentType("application/json");
                for (String name : names) {
                    jb.add(name);
                }
                JsonArray array = jb.build();
                writer.write(array.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public CustomerController() {
        super();
    }
}
