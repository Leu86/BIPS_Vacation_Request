package de.ostfalia.vacationrequest.mary;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ServiceTask_RequestDeclined implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        int temp_vacationDays = (int) execution.getVariable("EMPLOYEE_VACATIONDAYS") + (int) execution.getVariable("REQUESTVACATION_BUSINESSDAYS");
        execution.setVariable(("EMPLOYEE_VACATIONDAYS"), temp_vacationDays);

        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE Mitarbeiter SET Anzahl_Urlaubstage = ? WHERE idMitarbeiter = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setInt(1, (int) execution.getVariable("EMPLOYEE_VACATIONDAYS"));
        preparedStatement.setInt(2, (int) execution.getVariable("EMPLOYEE_ID"));
        preparedStatement.executeUpdate();
                
        connection.close();

        execution.setVariable("REQUEST_DECISION", "abgelehnt");
    }
}
