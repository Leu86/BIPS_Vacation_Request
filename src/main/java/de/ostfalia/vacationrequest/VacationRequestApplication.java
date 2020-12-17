package de.ostfalia.vacationrequest;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;

/**
 * Process Application exposing this application's resources the process engine.
 */
@ProcessApplication("Vacation Request App")
public class VacationRequestApplication extends ServletProcessApplication {
  public VacationRequestApplication() throws Exception {
    Class.forName("com.mysql.jdbc.Driver").newInstance();
  }
}
