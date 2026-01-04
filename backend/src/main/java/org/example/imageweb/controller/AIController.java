package org.example.imageweb.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.imageweb.entity.Label;
import org.example.imageweb.service.LabelService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AIController {
    private final LabelService labelService;
    private final ObjectMapper objectMapper;

    public AIController() {
        this.labelService = new LabelService();
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/suggestLabels")
    public ResponseEntity<?> recommendTags(@RequestParam("file") MultipartFile file,
                                           @RequestParam("user_id") int user_id) {
        try {
            List<Label> labels = labelService.getLabelsOfUser(user_id);
            String labelsJson = objectMapper.writeValueAsString(labels);
            //System.out.println(labelsJson);

            String aiServiceUrl = "http://localhost:5000/predict_tags";
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 关键修改：先读取为 byte[]，使用 ByteArrayResource 包装，避免流被多次读取
            byte[] fileBytes = file.getBytes();
            ByteArrayResource bar = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
                }
                @Override
                public long contentLength() {
                    return fileBytes.length;
                }
            };

            body.add("file", bar);
            body.add("labels", labelsJson);

            // 不手动设置 Content-Type，让 RestTemplate 生成带 boundary 的 multipart Content-Type
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> aiResp = restTemplate.postForEntity(aiServiceUrl, request, String.class);

            // System.out.println("AI service body: " + aiResp.getBody());

            try {
                Object parsed = objectMapper.readValue(aiResp.getBody(), new TypeReference<Object>(){});
                return ResponseEntity.status(aiResp.getStatusCode()).body(parsed);
            } catch (Exception ignore) {
                return ResponseEntity.status(aiResp.getStatusCode()).body(aiResp.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI标签服务异常: " + e.getMessage());
        }
    }
}