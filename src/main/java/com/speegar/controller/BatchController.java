package com.speegar.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.speegar.MessageWebSocketHandler;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@CrossOrigin(origins = "http://localhost:4200")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job csvToKafkaJob;
    
    @Autowired
    private MessageWebSocketHandler webSocketHandler;

    @Value("${csv.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.endsWith(".csv")) {
                response.put("success", false);
                response.put("message", "Only CSV files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            webSocketHandler.broadcastMessage("status", 
                Map.of("status", "UPLOADING", "message", "File upload started: " + originalFilename));

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path filepath = Paths.get(uploadDir, filename);
            Files.copy(file.getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);

            webSocketHandler.broadcastMessage("status", 
                Map.of("status", "UPLOADED", "message", "File uploaded successfully"));

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("inputFile", filepath.toString())
                    .toJobParameters();

            webSocketHandler.broadcastMessage("status", 
                Map.of("status", "PROCESSING", "message", "Batch job started"));

            JobExecution execution = jobLauncher.run(csvToKafkaJob, jobParameters);

            response.put("success", true);
            response.put("message", "Batch job started successfully");
            response.put("jobId", execution.getId());
            response.put("filename", filename);

            webSocketHandler.broadcastMessage("status", 
                Map.of("status", "COMPLETED", "message", "Batch job completed", "jobId", execution.getId()));
           
                
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            
            webSocketHandler.broadcastMessage("status", 
                Map.of("status", "ERROR", "message", "Error: " + e.getMessage()));
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startBatchJob() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();
            
            JobExecution execution = jobLauncher.run(csvToKafkaJob, jobParameters);
            
            response.put("success", true);
            response.put("message", "Batch job started");
            response.put("jobId", execution.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
