import com.myapp.aws.s3.S3Util;
import com.myapp.aws.workflow.WorkflowServiceImpl;
import com.myapp.aws.workflow.WorkflowServiceImplNew;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class WorkflowServiceTest extends AbstractTest{
    @Test
    public void testFileUpload(){
        String filePath = "/Users/kk/Desktop/";
        String fileName = "MyExposures_ApprovalRules_QA.xls";
        S3Util s3Util = new S3Util();
        s3Util.uploadRulesFile("kkaws-brm-bucket", filePath, fileName);
        //System.out.println(user);
        //System.out.println(user.getRoles());
    }

    @Test
    public void testGetRulesFile(){
        String fileName = "MyExposures_ApprovalRules_QA.xls";
        S3Util s3Util = new S3Util();
        byte[] bytes = s3Util.getRulesFile("kkaws-brm-bucket", fileName);
        System.out.println("Bytes size: " + bytes.length);
    }

    @Test
    public void testProcessRules(){
        WorkflowServiceImpl service = new WorkflowServiceImpl();

        Map<String, String> mapa = new HashMap<String, String>();
        mapa.put("appName", "MyExposures");
        mapa.put("module", "ExpWFRules");
        mapa.put("umbrella", "GE Aviation");
        mapa.put("notionalAmt", "99999");
        mapa.put("maturityBucket", "99999");

        Map<String, String> result = service.processRules("kkaws-brm-bucket", mapa);
        //System.out.println("Returned Search criteria: " +  result);
    }

    @Test
    public void testProcessRulesNew(){
        WorkflowServiceImplNew service = new WorkflowServiceImplNew();

        Map<String, String> mapa = new HashMap<String, String>();
        mapa.put("appName", "MyExposures");
        mapa.put("module", "ExpWFRules");
        mapa.put("umbrella", "GE Aviation");
        mapa.put("notionalAmt", "99999");
        mapa.put("maturityBucket", "99999");

        Map<String, String> result = service.processRules("kkaws-brm-bucket", mapa);
        //System.out.println("Returned Search criteria: " +  result);
    }
}
