package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendTask_CriticalTimezoneData implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("REQUESTVACATION_BUSINESSDAYS", execution.getVariable("REQUESTVACATION_BUSINESSDAYS"));
        hashMap.put("EMPLOYEE_VACATIONDAYS", execution.getVariable("EMPLOYEE_VACATIONDAYS"));
        hashMap.put("DEMO_BUSINESS_KEY", execution.getVariable("DEMO_BUSINESS_KEY"));

        String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        // name of the Message Event which receives the message
        runtimeService.startProcessInstanceByMessage("Critical Timezone Data", key, hashMap);       
    }
}
