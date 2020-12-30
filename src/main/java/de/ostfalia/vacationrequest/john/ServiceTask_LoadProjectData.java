package de.ostfalia.vacationrequest.john;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ServiceTask_LoadProjectData implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Connection connection = DatabaseConnection.getConnection();

        String query = "SELECT m.idMitarbeiter, CONCAT('[', p.name, '; ', p.Fertigstellung_bis, '] bearbeitet von: ', m.Vorname, ' ', m.Nachname, '; #NB-', p.Notbesetzung) AS Projektdaten FROM mitarbeiter m JOIN Mitarbeiter_hat_Projekte mp ON m.idMitarbeiter = mp.idMitarbeiter JOIN projekte p ON mp.idProjekte = p.idProjekte WHERE p.Fertigstellung_bis >= NOW()";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        Map<Integer, String> proectData = new HashMap<>();

        while (resultSet.next()) {
            proectData.put(resultSet.getInt("idMitarbeiter"), resultSet.getString("Projektdaten"));
        }
        
        execution.setVariable("CURRENT_PROJECTDATA", Variables.objectValue(proectData)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create());

        resultSet.close();
        preparedStatement.close();
        connection.close();   
    }
}
