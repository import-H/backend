package com.importH.core.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@ApiModel(value = "이미지 DTO")
public class ImageDto {

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @ApiModel(value = "이미지 요청 DTO")
    public static class Request {

        @ApiModelProperty(value = "이미지 데이터", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAfEAAAD4CAYAAAD8St8BAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAABRySURBVHhe7d3rk1xlnQfwVG3tv7Ev8o5aX1DlC6t8Ia/Mq1Rl")
        private MultipartFile image;

    }

    @Builder
    @Getter
    @ApiModel(value = "이미지 응답 DTO")
    public static class Response {

        @ApiModelProperty(value = "이미지 저장 위치", example = "https://images.velog.io/images/wkahd01/post/264cfa8c-11a3-4113-afeb-8417fcf504cf/%EC%BA%A1%EC%B2%98.PNG")
        private String imageURL;


    }
}
