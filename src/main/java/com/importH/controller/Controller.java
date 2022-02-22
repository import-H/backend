//package com.importH.controller;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.PropertyNamingStrategy;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//
//@RestController
//@RequiredArgsConstructor
//public class Controller {
//
//    @GetMapping(value = "/v1/login/google")
//    public ResponseEntity<GoogleLoginDto> redirectGoogleLogin(
//            @RequestParam(value = "code") String authCode
//    ) {
//        // HTTP 통신을 위해 RestTemplate 활용
//        RestTemplate restTemplate = new RestTemplate();
//        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
//                .clientId(configUtils.getGoogleClientId())
//                .clientSecret(configUtils.getGoogleSecret())
//                .code(authCode)
//                .redirectUri(configUtils.getGoogleRedirectUri())
//                .grantType("authorization_code")
//                .build();
//
//        try {
//            // Http Header 설정
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
//            ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(configUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);
//
//            // ObjectMapper를 통해 String to Object로 변환
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
//            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});
//
//            // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
//            String jwtToken = googleLoginResponse.getIdToken();
//
//            // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
//            String requestUrl = UriComponentsBuilder.fromHttpUrl(configUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();
//
//            String resultJson = restTemplate.getForObject(requestUrl, String.class);
//
//            if(resultJson != null) {
//                GoogleLoginDto userInfoDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});
//
//                return ResponseEntity.ok().body(userInfoDto);
//            }
//            else {
//                throw new Exception("Google OAuth failed!");
//            }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return ResponseEntity.badRequest().body(null);
//    }
//}
