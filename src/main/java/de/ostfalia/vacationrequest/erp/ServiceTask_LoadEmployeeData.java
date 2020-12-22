package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServiceTask_LoadEmployeeData implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM Mitarbeiter WHERE idMitarbeiter = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (int) execution.getVariable("EMPLOYEE_ID"));

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            execution.setVariable("EMPLOYEE_FIRSTNAME", resultSet.getString("Vorname"));
            execution.setVariable("EMPLOYEE_SURNAME", resultSet.getString("Nachname"));
            execution.setVariable("EMPLOYEE_STREET", resultSet.getString("Straße"));
            execution.setVariable("EMPLOYEE_ZIPCODE", resultSet.getInt("PLZ"));
            execution.setVariable("EMPLOYEE_CITY", resultSet.getString("Ort"));
            execution.setVariable("EMPLOYEE_MAIL", resultSet.getString("E-Mail"));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
