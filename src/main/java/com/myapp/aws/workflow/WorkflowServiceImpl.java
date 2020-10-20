package com.myapp.aws.workflow;

import com.myapp.aws.s3.S3Util;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.*;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public class WorkflowServiceImpl implements IWorkflowService{
    static final String RULE_NAME = "ruleName";
    static final String MYEXPOSURES_RULE_NAME = "MyExposureApprovalRules";
    static final String MYEXPOSURES_FILE_NAME = "MyExposures_ApprovalRules_QA.xls";

    @Override
    public Map<String, String> processRules(Map<String, String> mapa) {
        String ruleName = mapa.get(RULE_NAME);
        if(mapa.get(RULE_NAME) != null){
            String fileName = getFileName(ruleName);
            if(fileName != null){
                S3Util s3Util = new S3Util();
                byte[] bytes = s3Util.getRulesFile(fileName);
                processDrools(bytes, mapa);
            }
        }
        return  mapa;
    }

    private String getFileName(String ruleName){
        String fileName = null;
        if(ruleName.equalsIgnoreCase(MYEXPOSURES_RULE_NAME)){
            fileName = MYEXPOSURES_FILE_NAME;
        }
        return  fileName;
    }

    private Map<String, String> processDrools(byte[] bytes, Map<String, String> mapa){
        Instant start = Instant.now();
        // create an object of type decision table configuration
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();

        // set the input type of configuration object
        dtconf.setInputType(DecisionTableInputType.XLS);

        // create knowledge builder object
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();

        knowledgeBuilder.add(ResourceFactory.newByteArrayResource(bytes),
                ResourceType.DTABLE, dtconf);

        if (knowledgeBuilder.hasErrors()) {
            // System.out.println("errors from knowledge builder"+
            // knowledgeBuilder.getErrors() );
            String errorMessage = "";
            for (KnowledgeBuilderError error : knowledgeBuilder.getErrors())
                errorMessage = errorMessage + error.getMessage() + "\n";

        }
        // create knowledgebase object
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

        // add knowledgepackages to this object
        knowledgeBase.addKnowledgePackages(knowledgeBuilder
                .getKnowledgePackages());

        StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

        Set<String> s = mapa.keySet();
        int i = s.size();
        ksession.insert(mapa);
        try {
            ksession.fireAllRules();

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }

        Set<String> ss = mapa.keySet();
        int j = ss.size();
        if (j <= i) {

            System.out.println("No rules are found for this criteria");
        }

        System.out.println("Returned Search criteria: " +  mapa);

        printTimeTaken(start, "Time take for processDrools: ");

        return mapa;
    }

    private void printTimeTaken(Instant start, String label){
        long timeElapsed = Duration.between(start, Instant.now()).toMillis();  //in millis
        System.out.println(label + timeElapsed);
    }
}
