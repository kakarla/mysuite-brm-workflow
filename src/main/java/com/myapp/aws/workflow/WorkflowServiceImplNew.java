package com.myapp.aws.workflow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class WorkflowServiceImplNew implements IWorkflowService{
 static final String APP_NAME = "appName";
 static final String MODULE_NAME = "module";
 static final String MYEXPOSURES_APP = "MyExposures";
 static final String MYEXPOSURES_MODULE_WF = "ExpWFRules";
 static final String MYEXPOSURES_FILE_NAME_WF = "MyExposures_ApprovalRules_QA.xls";
 static final KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

 static {
  String fileName = "MyExposures_ApprovalRules_QA.xls";
  ClassLoader classLoader = WorkflowServiceImpl.class.getClassLoader();

  File file = new File(classLoader.getResource(fileName).getFile());

  //Read File Content
  byte[] bytes = null;

  try {
   bytes = Files.readAllBytes(file.toPath());
  } catch (IOException e) {
   // TODO Auto-generated catch block
   e.printStackTrace();
  }

  // create an object of type decision table configuration
  DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();

  // set the input type of configuration object
  dtconf.setInputType(DecisionTableInputType.XLS);

  System.out.println("knowledgeBuilder.add start: " + Instant.now());
  knowledgeBuilder.add(ResourceFactory.newByteArrayResource(bytes),
    ResourceType.DTABLE, dtconf);
  System.out.println("knowledgeBuilder.add end: " + Instant.now());
 }

 @Override
 public Map<String, String> processRules(String s3_bucket_name, Map<String, String> mapa) {
  String appName = mapa.get(APP_NAME);
  String moduleName = mapa.get(MODULE_NAME);
  if(mapa.get(APP_NAME) != null){
   processDrools(null, mapa);
  }
  return mapa;
 }

 private String getFileName(String appName, String moduleName){
  String fileName = null;
  if(appName.equalsIgnoreCase(MYEXPOSURES_APP) && moduleName.equalsIgnoreCase(MYEXPOSURES_MODULE_WF)){
   fileName = MYEXPOSURES_FILE_NAME_WF;
  }
  return fileName;
 }

 private Map<String, String> processDrools(byte[] bytes, Map<String, String> mapa){
  Instant start = Instant.now();

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

  System.out.println("Returned Search criteria: " + mapa);

  printTimeTaken(start, "Time take for processDrools: ");

  return mapa;
 }

 private void printTimeTaken(Instant start, String label){
  long timeElapsed = Duration.between(start, Instant.now()).toMillis(); //in millis
  System.out.println(label + timeElapsed);
 }
}