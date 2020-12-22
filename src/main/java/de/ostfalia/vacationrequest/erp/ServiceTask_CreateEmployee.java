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

        CallableStatement max = connection.prepareCall("SELECT MAX(idMitarbeiter) FROM mitarbeiter");
        ResultSet resultSet = max.executeQuery();
        int id = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;
        resultSet.close();

        execution.setVariable("EMPLOYEE_ID", id);

        String sql = "INSERT INTO `vacation_request`.`mitarbeiter` (`idMitarbeiter`, `Vorname`, `Nachname`, `Straße`, `PLZ`, `Ort`, `E-Mail`) VALUES (?, ?, ?, ?, ?, ?, ?);";

        CallableStatement callableStatement = connection.prepareCall(sql);
        callableStatement.setInt(1, id);
        callableStatement.setString(2, execution.getVariable("EMPLOYEE_FIRSTNAME").toString());
        callableStatement.setString(3, execution.getVariable("EMPLOYEE_SURNAME").toString());
        callableStatement.setString(4, execution.getVariable("EMPLOYEE_STREET").toString());
        callableStatement.setInt(5, (int) execution.getVariable("EMPLOYEE_ZIPCODE"));
        callableStatement.setString(6, execution.getVariable("EMPLOYEE_CITY").toString());
        callableStatement.setString(7, execution.getVariable("EMPLOYEE_MAIL").toString());
        callableStatement.executeUpdate();

        callableStatement.close();
        connection.close();
    }
}