package com.myapp.aws.workflow;

import java.util.Map;

public interface IWorkflowService {
    public Map<String, String> processRules(String s3_bucket_name, Map<String, String> mapa);
}
