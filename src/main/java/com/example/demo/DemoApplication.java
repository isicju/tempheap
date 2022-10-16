package com.example.demo;

import com.example.demo.dao.Employee;
import com.example.demo.dao.UserRepository;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {


    static List<Connection> connectionList = new ArrayList<>();
    static List<Employee> employees = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        (new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    Thread.sleep(5);
                    employees.add(new Employee());
                }
            }
        }, "thread name " + System.currentTimeMillis())).start();


        while (true) {
            Thread.sleep(300);
            DataSource dataSource = context.getBean("mariaDbDataSource", DataSource.class);
            Connection connection = dataSource.getConnection();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("SELECT * FROM employees");
            connectionList.add(connection);
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {

                    }
                }
            }, "thread name " + System.currentTimeMillis())).start();
        }
    }


}
