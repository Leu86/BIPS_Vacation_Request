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

        String sql = "SELECT Urlaubsantrag.idUrlaubsantrag, (Mitarbeiter.Anzahl_Urlaubstage - Urlaubsantrag.Arbeitstage) AS temp_Urlaubstage FROM Urlaubsantrag INNER JOIN Mitarbeiter ON Urlaubsantrag.idMitarbeiter=Mitarbeiter.idMitarbeiter WHERE idUrlaubsantrag = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, (int) execution.getVariable("REQUESTVACATION_ID"));

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            execution.setVariable("EMPLOYEE_VACATIONDAYS", resultSet.getInt("temp_Urlaubstage"));
        }   
        resultSet.close();    
        preparedStatement.close();

        if((int) execution.getVariable("EMPLOYEE_VACATIONDAYS") >= 0) {
            sql = "UPDATE Mitarbeiter SET Anzahl_Urlaubstage = ? WHERE idMitarbeiter = ?";
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, (int) execution.getVariable("EMPLOYEE_VACATIONDAYS"));
            preparedStatement.setInt(2, (int) execution.getVariable("EMPLOYEE_ID"));
            preparedStatement.executeUpdate();
        }        
        connection.close();
    }
}
