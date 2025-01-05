package com.cherrymango.foodiehub.service;

import com.cherrymango.foodiehub.domain.Store;
import com.cherrymango.foodiehub.domain.StoreImage;
import com.cherrymango.foodiehub.dto.image.UploadImageDto;
import com.cherrymango.foodiehub.file.FileStore;
import com.cherrymango.foodiehub.repository.StoreImageRepository;
import com.cherrymango.foodiehub.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreImageService {
    final private StoreImageRepository storeImageRepository;
    final private StoreRepository storeRepository;
    final private FileStore fileStore;

    public Long save(Long storeId, MultipartFile image){
        Store store = storeRepository.findById(storeId).get();
        if (image != null) {
            UploadImageDto uploadImageDto = fileStore.storeFile(image);
            StoreImage storeImage = StoreImage.createStoreImage(uploadImageDto.getUploadFileName(), uploadImageDto.getStoreFileName(), store);
            storeImageRepository.save(storeImage);
            return storeImage.getId();
        }
        return null;
    }

    public List<StoreImage> findImages(Store store) {
        return storeImageRepository.findByStore(store);
    }

    public StoreImage findOne(Long id) {
        return storeImageRepository.findById(id).get();
    }

    public void delete(Long id) {
        StoreImage storeImage = findOne(id);
        fileStore.deleteFile(storeImage.getStoreImageName());
        storeImageRepository.deleteById(id);
    }
}
