package de.ostfalia.vacationrequest.demo;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import de.ostfalia.vacationrequest.DatabaseConnection;
import de.ostfalia.vacationrequest.BusinessKeyGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
public class ServiceTask_LoadEmployees implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String query = "SELECT *, CONCAT(Vorname, ' ', Nachname) AS Name FROM mitarbeiter";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        Map<Integer, String> employees = new HashMap<>();

        while (resultSet.next()) {
            employees.put(resultSet.getInt("idMitarbeiter"), resultSet.getString("Name"));
        }
        employees.put(-1, "Mitarbeiter anlegen");
        
        execution.setVariable("AVAILABLE_EMPLOYEES", Variables.objectValue(employees)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());

        resultSet.close();
        preparedStatement.close();
        connection.close();

        // generate Business Key
        if (execution.getProcessBusinessKey() == null) {
            final String key = BusinessKeyGenerator.getKey(21);
            execution.setProcessBusinessKey(key);
            execution.setVariable("DEMO_BUSINESS_KEY", key);
        } else {
            execution.setVariable("DEMO_BUSINESS_KEY", execution.getProcessBusinessKey());
        }

    }
}

