package study;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.zaxxer.hikari.pool.TestElf.newHikariConfig;
import static org.junit.Assert.assertNotNull;

/**
 * @description:
 * @author: liuguohong
 * @create: 2020/09/27 11:54
 */

public class GetConnectionMethod {


   @Test
   public void getConnectionByDriver() {
      Connection conn = null;
      try {
         Class.forName("com.mysql.jdbc.Driver");
         conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/HikariCPTest?characterEncoding=UTF-8&useSSL=false", "root", "root");
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      assertNotNull(conn);

      try {
         conn.close();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void getConnectionByMySqlDataSource() {
      MysqlDataSource dataSource = new MysqlDataSource();
      dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/HikariCPTest?characterEncoding=UTF-8&useSSL=false");
      dataSource.setUser("root");
      dataSource.setPassword("root");
      try (Connection connection = dataSource.getConnection()){

      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   @Test
   public void getConnectionByHikariCP() {

      HikariConfig config = newHikariConfig();
      config.setJdbcUrl("jdbc:mysql://127.0.01:3306/HikariCPTest?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false");
      config.setMinimumIdle(1);
      config.setMaximumPoolSize(2);
      config.setConnectionTestQuery("SELECT 1");
      //config.setDataSourceClassName("com.mysql.jdbc.Driver");
      //config.addDataSourceProperty("jdbcUrl", "jdbc:mysql://127.0.01:3306/skynet-prod-2019-10-10-test?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false");
      config.setUsername("root");
      config.setPassword("root");

      try (HikariDataSource ds = new HikariDataSource(config)){
         try (Connection connection = ds.getConnection()) {

         } catch (Exception e) {
            e.printStackTrace();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
