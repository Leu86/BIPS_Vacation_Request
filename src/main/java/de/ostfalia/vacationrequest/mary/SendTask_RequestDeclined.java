package de.ostfalia.vacationrequest.mary;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;
public class SendTask_RequestDeclined implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("REQUEST_DECISION", execution.getVariable("REQUEST_DECISION"));
        hashMap.put("EMPLOYEE_VACATIONDAYS", execution.getVariable("EMPLOYEE_VACATIONDAYS"));

        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        // name of the Message Event which receives the message       
        runtimeService.createMessageCorrelation("Request Declined")
        .processInstanceBusinessKey(key)
        .setVariables(hashMap)
        .correlate();
    }
}