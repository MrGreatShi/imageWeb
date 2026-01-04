package org.example.imageweb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDataSourceConnection() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            assertTrue(conn != null && conn.isValid(2), "数据库连接不可用");
        }
    }
}