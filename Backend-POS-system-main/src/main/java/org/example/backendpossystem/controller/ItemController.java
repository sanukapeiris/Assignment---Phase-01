package org.example.backendpossystem.controller;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.backendpossystem.dto.Item;
import org.example.backendpossystem.dao.impl.ItemDataProcess;
import org.example.backendpossystem.util.UtilProcess;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/item")
public class ItemController extends HttpServlet {
    Connection connection;

    @Override
    public void init()  {
        try{
            var ctx = new InitialContext();
            DataSource pool = (DataSource) ctx.lookup("java:comp/env/jdbc/posdb");
            this.connection = pool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        ItemDataProcess itemDataProcess = new ItemDataProcess();

        if (req.getHeader("Request-Type").equals("table")){
            try(var writer = resp.getWriter()){
                List<Item> itemList = itemDataProcess.getAllItem(connection);
                JsonArrayBuilder itemJb = Json.createArrayBuilder();
                Jsonb jsonb = JsonbBuilder.create();

                for (Item itemDto : itemList){
                    var jObject = Json.createReader(new StringReader(jsonb.toJson(itemDto))).readObject();
                    itemJb.add(jObject);
                }
                writer.write(itemJb.build().toString());
                resp.setStatus(HttpServletResponse.SC_OK);

            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (req.getHeader("Request-Type").equals("search")) {
            String qur = req.getParameter("query").toLowerCase();

            try(var writer = resp.getWriter()){
                List<Item> itemList = itemDataProcess.searchItem(qur,connection);
                JsonArrayBuilder jb = Json.createArrayBuilder();
                Jsonb jsonb = JsonbBuilder.create();


                for (Item itemDto : itemList){
                    var jObject = Json.createReader(new StringReader(jsonb.toJson(itemDto))).readObject();
                    jb.add(jObject);
                }
                writer.write(jb.build().toString());
                resp.setStatus(HttpServletResponse.SC_OK);

            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (req.getHeader("Request-Type").equals("suggest")) {
            String qur = req.getParameter("query").toLowerCase();
            try(var writer = resp.getWriter()){
                List<String> suggestions = itemDataProcess.getNameSuggestions(qur,connection);
                JsonArrayBuilder jb = Json.createArrayBuilder();

                for (String suggestion : suggestions){
                    jb.add(suggestion);
                }
                JsonArray array = jb.build();
                writer.write(array.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType()== null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        try(var writer = resp.getWriter()){
            Jsonb jsonb = JsonbBuilder.create();
            Item itemDto = jsonb.fromJson(req.getReader(), Item.class);
            ItemDataProcess itemDataProcess = new ItemDataProcess();
            itemDto.setItemCode(UtilProcess.generateId());
            writer.write(itemDataProcess.saveItem(itemDto,connection));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!req.getContentType().toLowerCase().startsWith("application/json")||req.getContentType()== null){
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        }
        ItemDataProcess itemDataProcess = new ItemDataProcess();
        try(var writer = resp.getWriter()){
            String itemCode = req.getParameter("itemCode");
            Jsonb jsonb = JsonbBuilder.create();
            var updatedItem = jsonb.fromJson(req.getReader(), Item.class);
            boolean b = itemDataProcess.updateItem(itemCode, updatedItem, connection);
            if (b) {
                writer.write("Item ID "+ itemCode+ " Updated");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                writer.write("Item ID "+ itemCode+ " Not Updated");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ItemDataProcess itemDataProcess = new ItemDataProcess();
        try(var writer = resp.getWriter()){
            String itemCode = req.getParameter("itemCode");
            boolean b = itemDataProcess.deleteItem(itemCode, connection);
            if (b) {
                writer.write("Item ID "+ itemCode+ " Deleted");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                writer.write("Item ID "+ itemCode+ " Not Deleted");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    public ItemController() {
        super();
    }
}
