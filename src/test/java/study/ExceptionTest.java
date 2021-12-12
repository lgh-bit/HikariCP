package study;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zaxxer.hikari.pool.TestElf.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

/**
 * @description: 错误重新
 * @author: liuguohong
 * @create: 2020/01/07 14:56
 */

public class ExceptionTest {
   private final Logger logger = LoggerFactory.getLogger(ExceptionTest.class);

   @Test
   public void testTimeoutException() throws InterruptedException, SQLException
   {
      HikariConfig config = newHikariConfig();
      config.setMinimumIdle(2);
      config.setMaximumPoolSize(4);
      config.setConnectionTestQuery("SELECT 1");
      config.setJdbcUrl("jdbc:mysql://127.0.01:3306/skynet-prod-2019-10-10-test?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&useSSL=false");
      config.setUsername("root");
      config.setPassword("root");

      System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "3000");
      ExecutorService executorService = Executors.newFixedThreadPool(5);
      try (HikariDataSource ds = new HikariDataSource(config)) {
         System.clearProperty("com.zaxxer.hikari.housekeeping.periodMs");

         SECONDS.sleep(1);

         HikariPool pool = getPool(ds);

         getUnsealedConfig(ds).setIdleTimeout(3000);
         getUnsealedConfig(ds).setConnectionTimeout(500);

         assertEquals("Total connections not as expected", 2, pool.getTotalConnections());
         assertEquals("Idle connections not as expected", 2, pool.getIdleConnections());

         for (int i = 0; i < 7; i++) {
            try {
               Connection connection = ds.getConnection();
               Assert.assertNotNull(connection);
               pool.logPoolState("test: ");
               executorService.execute(()-> this.selectTest(connection));
            } catch (SQLException e) {
               logger.error("error when {}", i + 1, e);
            }

         }
         SECONDS.sleep(20);
      }
   }

   public void selectTest(Connection connection) {
      try {
         SECONDS.sleep(5);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      try (PreparedStatement preparedStatement = connection.prepareStatement("select * from basic_pool_test")){

         ResultSet resultSet = preparedStatement.executeQuery();
         System.out.println(resultSet.toString());
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
}
