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
        hashMap.put("EMPLOYEE_NAME", execution.getVariable("EMPLOYEE_NAME"));
        hashMap.put("EMPLOYEE_ADDRESS", execution.getVariable("EMPLOYEE_ADDRESS"));
        hashMap.put("EMPLOYEE_MAIL", execution.getVariable("EMPLOYEE_MAIL"));

        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");

        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        // name of the Message Event which receives the message       
        //runtimeService.correlateMessage("Employee Data", key, hashMap);
        //runtimeService.createMessageCorrelation("Employee Data").correlateWithResult();
        runtimeService.createMessageCorrelation("Employee Data")
        .processInstanceBusinessKey(key)
        .setVariables(hashMap)
        .correlate();
    }
}
