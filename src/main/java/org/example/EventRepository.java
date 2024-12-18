package org.example;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EventRepository {

    public static void addEvent(Event event, int refund100, boolean refund24) throws SQLException {
        PreparedStatement pr = Connect.SQLConnection("INSERT INTO events (title,description,price,category,date_and_time,place, image_url, refund_100, refund_24) VALUES (?,?,?,?,?,?,?,?,?)");
        pr.setString(1, event.getTitle());
        pr.setString(2, event.getDescription());
        pr.setDouble(3, event.getPrice());
        pr.setString(4, event.getPlace());
        pr.setString(5, String.valueOf(event.getDateAndTime()));
        pr.setString(6, event.getCategory());
        pr.setString(7, event.getImageUrl());
        pr.setInt(8, refund100);
        pr.setBoolean(9, refund24);
        pr.execute();
    }
}
