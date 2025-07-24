package com.example.forever.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config.file:firebase-service-account-key.json}")
    private String firebaseConfigFile;
    
    @Value("${firebase.credentials.json:}")
    private String firebaseCredentialsJson;
    
    @Value("${firebase.project.id:}")
    private String firebaseProjectId;

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials credentials = getGoogleCredentials();
            
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(credentials);
            
            // 프로젝트 ID가 환경변수로 제공된 경우
            if (StringUtils.hasText(firebaseProjectId)) {
                optionsBuilder.setProjectId(firebaseProjectId);
            }
            
            FirebaseOptions options = optionsBuilder.build();
            
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료 - 프로젝트 ID: {}", 
                        StringUtils.hasText(firebaseProjectId) ? firebaseProjectId : "설정파일에서 로드");
            } else {
                log.info("Firebase 이미 초기화됨");
            }
        } catch (Exception e) {
            log.error("Firebase 초기화 실패: {}", e.getMessage());
            log.warn("Firebase 설정 없이 애플리케이션을 계속 실행합니다.");
        }
    }
    

    private GoogleCredentials getGoogleCredentials() throws IOException {
        if (StringUtils.hasText(firebaseCredentialsJson)) {
            InputStream credentialsStream = new ByteArrayInputStream(
                    firebaseCredentialsJson.getBytes());
            return GoogleCredentials.fromStream(credentialsStream);
        }
        
        ClassPathResource resource = new ClassPathResource(firebaseConfigFile);
        return GoogleCredentials.fromStream(resource.getInputStream());
    }
}
