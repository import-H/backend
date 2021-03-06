package com.importH.domain.image;

import com.importH.global.response.ResponseService;
import com.importH.global.response.SingleResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

@Api(tags = "4. File")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/file/")
public class FileController {


    private final ResponseService responseService;
    private final FileService fileService;

    
    @ApiOperation(value = "이미지 업로드", notes = "이미지 파일을 업로드 합니다.")
    @PostMapping("upload")
    public SingleResult<ImageDto.Response> uploadImage(@ApiParam(value = "파일 요청 DTO") ImageDto.Request imageRequest, HttpServletRequest request) throws URISyntaxException {

        return responseService.getSingleResult(fileService.uploadImage(imageRequest,request));

    }


    @ApiOperation(value = "이미지 조회", notes = "이미지 파일을 조회 합니다.")
    @GetMapping("upload/{filename}")
    public Resource downloadImage(@ApiParam(value = "저장된 파일 이름") @PathVariable String filename) throws MalformedURLException {
        log.info("PATH : {} " , fileService.getFullPath(filename));
        return new UrlResource("file:" + fileService.getFullPath(filename));
    }


}
