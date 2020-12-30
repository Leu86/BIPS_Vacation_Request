package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ServiceTask_CheckCriticalTimezone implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM vacation_request.ferien_nds_2021 WHERE Ende >= ? AND Beginn <= ? LIMIT 1";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, execution.getVariable("REQUESTVACATION_START"));
        preparedStatement.setObject(2, execution.getVariable("REQUESTVACATION_END"));

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            execution.setVariable("VACATION_NAME", resultSet.getString("Name"));
            execution.setVariable("REQUESTVACATION_CRITICAL", true);
        } else {
            execution.setVariable("REQUESTVACATION_CRITICAL", false);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}