package org.example.imageweb.controller;

import org.example.imageweb.entity.McpRequest;

import org.example.imageweb.entity.McpResponse;
import org.example.imageweb.service.McpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mcp")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})

public class McpController {
    private final McpService mcpService;

    public McpController() {
        this.mcpService = new McpService();
    }

    @GetMapping("/query")
    public ResponseEntity<?> queryByConversation(@RequestParam int user_id,
                                                 @RequestParam String conversation) {
        try {
            List<McpResponse> resp = mcpService.searchByConversation(new McpRequest(user_id, conversation));
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("MCP 查询失败: " + e.getMessage());
        }
    }
}
