import com.myapp.aws.s3.S3Util;
import com.myapp.aws.workflow.WorkflowServiceImpl;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WorkflowServiceTest extends AbstractTest{
    @Test
    public void testFileUpload(){
        String filePath = "/Users/kondaiahkakarla/Desktop/";
        String fileName = "MyExposures_ApprovalRules_QA.xls";
        S3Util s3Util = new S3Util();
        s3Util.uploadRulesFile(filePath, fileName);
        //System.out.println(user);
        //System.out.println(user.getRoles());
    }

    @Test
    public void testGetRulesFile(){
        String fileName = "MyExposures_ApprovalRules_QA.xls";
        S3Util s3Util = new S3Util();
        byte[] bytes = s3Util.getRulesFile(fileName);
        System.out.println("Bytes size: " + bytes.length);
    }

    @Test
    public void testProcessRules(){
        WorkflowServiceImpl service = new WorkflowServiceImpl();

        Map<String, String> mapa = new HashMap<String, String>();
        mapa.put("appName", "MyExposures");
        mapa.put("module", "ExpWFRules");
        mapa.put("umbrella", "test");
        mapa.put("notionalAmt", "100");
        mapa.put("maturityBucket", "100");

        Map<String, String> result = service.processRules(mapa);
        System.out.println("Returned Search criteria: " +  result);
    }
}
