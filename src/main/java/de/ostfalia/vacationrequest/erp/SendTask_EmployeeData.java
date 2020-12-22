package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendTask_EmployeeData implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("EMPLOYEE_ID", execution.getVariable("EMPLOYEE_ID"));
        hashMap.put("EMPLOYEE_FIRSTNAME", execution.getVariable("EMPLOYEE_FIRSTNAME"));
        hashMap.put("EMPLOYEE_SURNAME", execution.getVariable("EMPLOYEE_SURNAME"));
        hashMap.put("EMPLOYEE_STREET", execution.getVariable("EMPLOYEE_STREET"));
        hashMap.put("EMPLOYEE_ZIPCODE", execution.getVariable("EMPLOYEE_ZIPCODE"));
        hashMap.put("EMPLOYEE_CITY", execution.getVariable("EMPLOYEE_CITY"));
        hashMap.put("EMPLOYEE_MAIL", execution.getVariable("EMPLOYEE_MAIL"));

        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        // name of the Message Event which receives the message       
        runtimeService.createMessageCorrelation("Employee Data")
        .processInstanceBusinessKey(key)
        .setVariables(hashMap)
        .correlate();
    }
}
