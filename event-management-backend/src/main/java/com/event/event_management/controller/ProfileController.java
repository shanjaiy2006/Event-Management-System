package com.event.event_management.controller;

import com.event.event_management.dto.UpdatePasswordRequest;
import com.event.event_management.dto.UpdateProfileRequest;
import com.event.event_management.entity.User;
import com.event.event_management.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.event.event_management.service.CloudinaryService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Profile APIs")
@RestController
@RequestMapping("/profile")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            // Validation
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File is empty"));
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File size exceeds 5MB"));
            }
            String contentType = file.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid file type. Only JPG, PNG, and WebP are allowed"));
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            // Delete old picture from Cloudinary if exists
            if (user.getProfilePicturePath() != null && user.getProfilePicturePath().contains("cloudinary.com")) {
                try {
                    String url = user.getProfilePicturePath();
                    int uploadIndex = url.indexOf("/upload/");
                    if (uploadIndex != -1) {
                        String afterUpload = url.substring(uploadIndex + 8);
                        int slashIndex = afterUpload.indexOf("/");
                        if (slashIndex != -1 && afterUpload.startsWith("v")) {
                            afterUpload = afterUpload.substring(slashIndex + 1);
                        }
                        int dotIndex = afterUpload.lastIndexOf(".");
                        if (dotIndex != -1) {
                            String publicId = afterUpload.substring(0, dotIndex);
                            cloudinaryService.deleteImage(publicId);
                        }
                    }
                } catch (Exception ignored) {}
            }

            // Upload new picture to Cloudinary
            Map uploadResult = cloudinaryService.uploadImage(file);
            String fileUrl = uploadResult.get("secure_url").toString();
            
            user.setProfilePicturePath(fileUrl);
            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile picture updated successfully");
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Failed to upload file"));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, String> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        
        if (user.getProfilePicturePath() != null) {
            response.put("profilePicture", user.getProfilePicturePath());
        } else {
            response.put("profilePicture", "");
        }
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!email.equals(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Email already in use"));
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody UpdatePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Incorrect current password"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
}
