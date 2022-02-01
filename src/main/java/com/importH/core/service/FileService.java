package com.importH.core.service;

import com.importH.core.domain.file.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;


    @Value("${file.dir}")
    private String fileDir;


    public void saveImage(List<MultipartFile> imageFiles) {

    }
}
