package com.myapp.aws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.myapp.aws.workflow.WorkflowServiceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class S3Util {
    private static AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();

    public void uploadRulesFile(String s3_bucket_name, String filePath, String fileName) {
        Instant start = Instant.now();
        try{
            File fileToUpload = new File(filePath + fileName);
            System.out.println("Object upload started");
            PutObjectResult s3Result = s3.putObject(s3_bucket_name, fileName, fileToUpload);

            /*TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(s3)
                    .build();

            Upload upload = tm.upload(bucketName, fileName, fileToUpload);
             */


            System.out.println("Object upload complete");
        } catch (AmazonServiceException e) {
        // The call was transmitted successfully, but Amazon S3 couldn't process
        // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        printTimeTaken(start, "Time take for uploadRulesFile: ");
    }

    public byte[] getRulesFile(String s3_bucket_name, String fileName) {
        Instant start = Instant.now();
        S3Object headerOverrideObject = null;

        try {
            // Get an object and print its contents.
            System.out.println("Downloading an object");

            // Get an entire object, overriding the specified response headers, and print the object's content.
            ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                    .withCacheControl("No-cache")
                    .withContentDisposition("attachment; filename=example.txt");
            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(s3_bucket_name, fileName)
                    .withResponseHeaders(headerOverrides);
            headerOverrideObject = s3.getObject(getObjectRequestHeaderOverride);

            InputStream in = headerOverrideObject.getObjectContent();

            byte[] bytes = IOUtils.toByteArray(in);
            in.close();
            printTimeTaken(start, "Time take for getRulesFile: ");
            return bytes;

        }catch(Exception ex) {
            ex.printStackTrace();
        }

        return  null;
    }

    private void printTimeTaken(Instant start, String label){
        long timeElapsed = Duration.between(start, Instant.now()).toMillis();  //in millis
        System.out.println(label + timeElapsed);
    }

}
