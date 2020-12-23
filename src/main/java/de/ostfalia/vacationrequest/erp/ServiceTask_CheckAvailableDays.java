package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServiceTask_CheckAvailableDays implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT Anzahl_Urlaubstage FROM Mitarbeiter WHERE idMitarbeiter = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (int) execution.getVariable("EMPLOYEE_ID"));

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            execution.setVariable("VACATION_DAYS", resultSet.getInt("Anzahl_Urlaubstage"));
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
