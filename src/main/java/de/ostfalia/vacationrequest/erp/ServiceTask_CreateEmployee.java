package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

public class ServiceTask_CreateEmployee implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        CallableStatement max = connection.prepareCall("SELECT MAX(idMitarbeiter) FROM Mitarbeiter");
        ResultSet resultSet = max.executeQuery();
        int id = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;
        resultSet.close();
        //max.close();

        execution.setVariable("EMPLOYEE_ID", id);

        String sql = "INSERT INTO `vacation_request`.`Mitarbeiter` (`idMitarbeiter`, `Name`, `Adresse`, `E-Mail`) VALUES (?, ?, ?, ?);";

        CallableStatement callableStatement = connection.prepareCall(sql);
        callableStatement.setInt(1, id);
        callableStatement.setString(2, execution.getVariable("EMPLOYEE_NAME").toString());
        callableStatement.setString(3, execution.getVariable("EMPLOYEE_ADDRESS").toString());
        callableStatement.setString(4, execution.getVariable("EMPLOYEE_MAIL").toString());
        callableStatement.executeUpdate();

        callableStatement.close();
        connection.close();
    }
}