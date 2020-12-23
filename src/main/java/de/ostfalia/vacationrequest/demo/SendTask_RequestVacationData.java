package de.ostfalia.vacationrequest.demo;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.util.HashMap;

public class SendTask_RequestVacationData implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
       // String key = (String) execution.getVariable("DEMO_BUSINESS_KEY");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("REQUESTVACATION_START", execution.getVariable("REQUESTVACATION_START"));
        hashMap.put("REQUESTVACATION_END", execution.getVariable("REQUESTVACATION_END"));
        
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
        // name of the Message Event which receives the message       
         runtimeService.createMessageCorrelation("Request Vacation Data")
       // .processInstanceBusinessKey(key)
        .setVariables(hashMap)
        .correlate(); 
    }
}