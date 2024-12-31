package com.cherrymango.foodiehub.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {
    @Value("${file.dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) throws IOException {
        // 고유한 파일 이름 생성
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        // 디렉토리 생성 및 파일 저장
        Files.createDirectories(filePath.getParent());

        // 파일 쓰기
        try {
            Files.write(filePath, file.getBytes());
            System.out.println("파일 저장 완료: " + filePath.toString());
        } catch (IOException e) {
            System.err.println("파일 저장 실패: " + e.getMessage());
            throw e;
        }


        // 파일 이름 반환 (경로는 반환하지 않음)
        return fileName; // 저장된 파일 이름 반환
    }

}
