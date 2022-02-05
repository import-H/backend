package com.importH.core.service;

import com.importH.core.domain.image.Image;
import com.importH.core.domain.image.ImageRepository;
import com.importH.core.dto.post.ImageDto;
import com.importH.core.error.code.FileErrorCode;
import com.importH.core.error.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final ImageRepository imageRepository;

    @Value("${file.dir}")
    private String fileDir;


    public ImageDto.Response uploadImage(ImageDto.Request requestDto, HttpServletRequest request) throws URISyntaxException {

        if (requestDto.getImage() == null) {
            throw new FileException(FileErrorCode.NOT_FOUND_IMAGE);
        }

        String originalFilename = requestDto.getImage().getOriginalFilename();
        String storeFilename = createStoreFileName(originalFilename);

        saveImage(originalFilename, storeFilename);

        try {
            requestDto.getImage().transferTo(new File(fileDir + storeFilename));
        } catch (IOException e) {
            log.info("[Error] : {} ", e.getMessage());
            throw new FileException(FileErrorCode.FAIL_FILE_SAVE);
        }

        return ImageDto.Response.builder()
                .imageURL(request.getRequestURI() + "/" + storeFilename)
                .build();
    }

    private void saveImage(String originalFilename, String storeFilename) {
        imageRepository.save(Image.builder().storeFileName(storeFilename)
                .uploadFileName(originalFilename)
                .build());
    }

    private String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + ext;
    }

    private String extractExt(String originalFilename) {
        int idx = originalFilename.lastIndexOf(".");
        String ext = originalFilename.substring(idx);
        return ext;
    }

    public String  getFullPath(String filename) {
        return fileDir + filename;
    }
}
