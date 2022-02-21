package com.importH.core.service;

import com.importH.core.domain.image.Image;
import com.importH.core.domain.image.ImageRepository;
import com.importH.core.domain.post.Post;
import com.importH.core.dto.post.ImageDto;
import com.importH.error.code.FileErrorCode;
import com.importH.error.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final ImageRepository imageRepository;

    @Value("${file.dir}")
    private String fileDir;


    @Transactional
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

    public void deleteImage(String imgUrl) {
        File deleteFie = new File(fileDir + imgUrl);
        if (deleteFie.exists()) {
            deleteFie.delete();
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }


    /**
     * 해당 게시글의 이미지 가져오기
     */
    public List<Image> getPostImages(List<String> images) {
           return imageRepository.findAllByStoreFileNameIn(images);
    }
}
