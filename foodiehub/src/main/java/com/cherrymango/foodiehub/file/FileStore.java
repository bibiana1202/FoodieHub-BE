package com.cherrymango.foodiehub.file;

import com.cherrymango.foodiehub.dto.UploadImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadImageDto> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadImageDto> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }
        return storeFileResult;
    }

    public UploadImageDto storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 파일 저장 경로 디렉토리 확인 및 생성
        createDirectoryIfNotExists(fileDir);

        try {
            multipartFile.transferTo(new File(getFullPath(storeFileName)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new UploadImageDto(originalFilename, storeFileName);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring((pos + 1));
    }

    private void createDirectoryIfNotExists(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public boolean deleteFile(String storeFileName) {
        String fullPath = getFullPath(storeFileName);
        File file = new File(fullPath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
