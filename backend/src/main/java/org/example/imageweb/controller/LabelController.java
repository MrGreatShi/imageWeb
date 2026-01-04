package org.example.imageweb.controller;

import org.example.imageweb.service.ImageService;
import org.example.imageweb.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/label" )
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class LabelController {
    @Autowired
    private LabelService labelService;
    @Autowired
    private ImageService imageService;

    @GetMapping("/getLabelsOfUser")
    public ResponseEntity<?> getLabelsOfUser(@RequestParam int user_id) throws Exception {
        try{
            return ResponseEntity.ok(labelService.getLabelsOfUser(user_id));
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error retrieving labels for user: " + e.getMessage());
        }
    }

    @PutMapping("/getLabelsOfImage")
    public ResponseEntity<?> getLabelsOfImage(@RequestParam int imageId) throws Exception {
        try{
            return ResponseEntity.ok(labelService.getLabelsOfImage(imageId));
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error retrieving labels for image: " + e.getMessage());
        }
    }

    public static class LabelRequest {
        public int userId;
        public String title;
    }

    @PostMapping("/addToUser")
    public ResponseEntity<?> addLabelToUser(@RequestBody LabelRequest label) throws Exception {
        try{
            System.out.println(label.userId + " "+ label.title);
            labelService.addLabelToUser(label.userId, label.title);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error adding label to user: " + e.getMessage());
        }
    }

    @PostMapping("/removeFromUser")
    public ResponseEntity<?> removeLabelFromUser(@RequestParam int labelId) throws Exception {
        try{
            labelService.removeLabelFromUser(labelId);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error removeing label from user: " + e.getMessage());
        }
    }

    public static class AddLabelRequest {
        public int id;
        public String title;
        public String labels;
    }

    @PostMapping("/addToImage")
    public ResponseEntity<?> addLabelToImage(@RequestBody AddLabelRequest request) throws Exception {
        try{
            int image_id = request.id;
            imageService.modifyImageName(image_id, request.title);
            String labels = request.labels;
            if(labels.isEmpty() || labels.equals("[]")){
                return ResponseEntity.ok().build();
            }
            int size = labels.length();
            String[] labelList = labels.substring(1,size-1).split(",");
            for(String l_id_str : labelList){
                labelService.addLabelToImage(image_id, Integer.parseInt(l_id_str));
            }
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error adding label to image: " + e.getMessage());
        }
    }

    @PostMapping("/removeFromImage")
    public ResponseEntity<?> removeLabelToImage(@RequestParam int p_id, @RequestParam int l_id) throws Exception {
        try{
            labelService.removeLabelFromImage(p_id, l_id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error removeing label to image: " + e.getMessage());
        }
    }

}
