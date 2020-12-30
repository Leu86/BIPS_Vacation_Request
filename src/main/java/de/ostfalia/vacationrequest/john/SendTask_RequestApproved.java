package de.ostfalia.vacationrequest.john;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.RuntimeService;

import java.util.HashMap;
public class SendTask_RequestApproved implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("REQUEST_DECISION", "genehmigt");
        hashMap.put("EMPLOYEE_VACATIONDAYS", execution.getVariable("EMPLOYEE_VACATIONDAYS"));

        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        // name of the Message Event which receives the message       
        runtimeService.createMessageCorrelation("Request Approved")
        .processInstanceBusinessKey(key)
        .setVariables(hashMap)
        .correlate();
    }
}