package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.domain.Role;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.service.FileUploadService;
import com.cherrymango.foodiehub.service.SiteUserService;
import com.cherrymango.foodiehub.util.JwtUtil;
import com.cherrymango.foodiehub.util.LoginUtil;
import com.cherrymango.foodiehub.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class SiteUserController {
    private final SiteUserService siteUserService;
    @Autowired
    private FileUploadService fileUploadService; // 파일 저장 서비스 주입
    private final TokenUtil tokenUtil;
    private final LoginUtil loginUtil;
    @Autowired
    private SiteUserRepository siteUserRepository;

    // 닉네임 중복 확인
    @GetMapping("/api/user/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        System.out.println("닉네임 중복확인" + nickname);
        boolean isDuplicated = siteUserService.isNicknameDuplicated(nickname);
        return ResponseEntity.ok().body(Map.of("isDuplicated", isDuplicated));
    }

    // 회원가입_회원
//    @PostMapping("/api/auth/user")
//    public ResponseEntity<?> signup(@Valid @RequestBody AddUserRequest addUserRequest, BindingResult bindingResult) {
//        // 입력값 검증 에러 처리
//        if (bindingResult.hasErrors()) {
//            List<String> errorMessages = bindingResult.getAllErrors().stream()
//                    .map(error -> error.getDefaultMessage())
//                    .collect(Collectors.toList());
//            return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
//        }
//
//        // 패스워드 불일치 검증
//        if (!addUserRequest.getPassword1().equals(addUserRequest.getPassword2())) {
//            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
//            return ResponseEntity.badRequest().body(Map.of("errors", List.of("2개의 패스워드가 일치하지 않습니다.")));
//        }
//
//        // 회원 저장 시도
//        try {
//            // 닉네임 중복 검사
//            if(siteUserService.isNicknameDuplicated(addUserRequest.getNickname())){
//                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
//                return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
//            }
//            // 사용자 저장
//            System.out.println("저장 권한 singup : "+addUserRequest.getRole());
//            System.out.println("Email singup : "+addUserRequest.getEmail());
//            siteUserService.save(addUserRequest);
//        } catch (DataIntegrityViolationException e) {
//            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
//            return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 등록된 사용자입니다.")));
//        } catch (Exception e) {
//            bindingResult.reject("signupFailed", e.getMessage());
//            return ResponseEntity.status(500).body(Map.of("errors", List.of(e.getMessage())));
//        }
//
//        // 성공 응답 반환
//        return ResponseEntity.ok(Map.of("message", "회원 가입 성공"));
//    }

    // 회원가입_관리자
    @PostMapping("/api/auth/admin")
    public ResponseEntity<?> signup(@RequestBody @Valid AddAdminRequest addAdminRequest, BindingResult bindingResult) {
        // 입력값 검증 에러 처리
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
        }

        // 패스워드 불일치 검증
        if (!addAdminRequest.getPassword1().equals(addAdminRequest.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("2개의 패스워드가 일치하지 않습니다.")));
        }

        // 회원 저장 시도
        try {
            // 닉네임 중복 검사
            if(siteUserService.isNicknameDuplicated(addAdminRequest.getNickname())){
                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
            }
            addAdminRequest.setRole("ROLE_ADMIN");
            System.out.println("저장 권한 singup : "+addAdminRequest.getRole());
            System.out.println("Email singup : "+addAdminRequest.getEmail());
            siteUserService.save_admin(addAdminRequest);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다. 회원정보를 다시 확인해주세요.");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 등록된 사용자입니다.")));
        } catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("errors", List.of(e.getMessage())));
        }

        return ResponseEntity.ok(Map.of("message", "회원 가입 성공"));
    }

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> getUserInfo(Principal principal,HttpServletRequest request){
        // OAuth2 로그인 처리
        if (principal != null && principal instanceof OAuth2AuthenticationToken) {

            try {
                // 사용자 정보를 TokenUtil에서 가져오기
                SiteUser user = tokenUtil.getSiteUserFromRequest(request);

                // 사용자 정보 추출
                Long userId = user.getId();
                String email = user.getEmail();
                String cellPhone = user.getCellphone();
                String nickName = user.getNickname();
                Role role = user.getRole();
                String provider = user.getProvider();
                String businessNo = user.getBusinessno();
                String getProfileImageUrl = user.getProfileImageUrl();

                // 응답 생성
                SiteUserInfoResponse response = SiteUserInfoResponse.builder()
//                        .userid(userId)
//                        .email(email)
//                        .cellphone(cellPhone)
                        .nickname(nickName)
                        .role(role)
//                        .provider(provider)
//                        .businessno(businessNo)
                        .profileimageurl(getProfileImageUrl)
                        .build();

                return ResponseEntity.ok(response);
            } catch (Exception e) {
                // 기존: return ResponseEntity.status(401).body("Invalid or expired token");
                // 수정: JSON으로 감싸서 응답
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid or expired token"));
            }
        } else if(principal != null) {
            // 폼 로그인 처리
            String email ="";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                email = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기
            }
            // 사용자 데이터 조회
            Optional<SiteUser> siteUser = siteUserService.findByEmail(email);
            if (siteUser.isPresent()) {
                SiteUser user = siteUser.get();
                Long userId = user.getId();
                String cellPhone = user.getCellphone();
                String name = user.getName();
                String nickName= user.getNickname();
                String provider = user.getProvider();
                Role role = user.getRole();
                String businessNo = user.getBusinessno();
                String getProfileImageUrl =user.getProfileImageUrl();


                // DTO를 사용해 응답 반환
                // DTO 생성
                SiteUserInfoResponse response = SiteUserInfoResponse.builder()
//                        .userid(userId)
//                        .cellphone(cellPhone)
//                        .email(email)
//                        .name(name)
                        .nickname(nickName)
//                        .provider(provider)
                        .role(role)
//                        .businessno(businessNo) // 필요 없으면 null 처리
                        .profileimageurl(getProfileImageUrl)
                        .build();

                return ResponseEntity.ok(response);
            } else {
                // 기존: return ResponseEntity.status(404).body("User not found");
                // 수정: JSON 형태
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

        }else {
            // principal == null → 로그인되어 있지 않은 경우
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));

        }
    }
//    @GetMapping("/api/auth/me")
//    public ResponseEntity<?> getUserInfo(Principal principal,HttpServletRequest request){
//
//        SiteUser siteUser = loginUtil.getSiteUserFromLogin(request,principal);
//
//        if(siteUser != null){
//            try {
//                // 사용자 정보 추출
//                Long userId = siteUser.getId();
//                String email = siteUser.getEmail();
//                String cellPhone = siteUser.getCellphone();
//                String nickName = siteUser.getNickname();
//                Role role = siteUser.getRole();
//                String provider = siteUser.getProvider();
//                String businessNo = siteUser.getBusinessno();
//                String getProfileImageUrl = siteUser.getProfileImageUrl();
//
//                // 응답 생성
//                SiteUserInfoResponse response = SiteUserInfoResponse.builder()
////                        .userid(userId)
////                        .email(email)
////                        .cellphone(cellPhone)
//                        .nickname(nickName)
//                        .role(role)
////                        .provider(provider)
////                        .businessno(businessNo)
//                        .profileimageurl(getProfileImageUrl)
//                        .build();
//
//                return ResponseEntity.ok(response);
//            } catch (Exception e) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("message", "Invalid or expired token"));
//            }
//        }
//        else{
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("message", "User not authenticated"));
//        }
//
//
//    }



    // 회원정보수정 POST
    @PostMapping("/api/user/update-profile")
    public ResponseEntity<?> updateUserProfile(@ModelAttribute @Valid  SiteUserProfileRequest profileRequest, BindingResult bindingResult, HttpServletRequest request,Principal principal){
        System.out.println("updateUserProfile!!!");
        String profileImageUrl = null;

        // 입력값 검증 에러 처리
        if (bindingResult.hasErrors()) {
            System.out.println("입력값 검증 에러 처리");
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
        }

        // 파일 업로드 처리
        try {
            System.out.println("파일 업로드 처리"+profileRequest.getProfileImage());
            if (profileRequest.getProfileImage() != null && !profileRequest.getProfileImage().isEmpty()) {
                // 새로운 이미지가 업로드된 경우
                String savedFilePath = fileUploadService.saveFile(profileRequest.getProfileImage());
                profileImageUrl = "/uploads/" + savedFilePath; // 파일 URL 생성
                System.out.println("저장된 파일 경로: " + profileImageUrl);
            } else {
                // 이미지 수정이 없을 경우 기존 이미지 URL 유지
                profileImageUrl = profileRequest.getExistingImageUrl();
                System.out.println("기존 이미지 유지: " + profileImageUrl);
            }

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("errors", List.of("파일 업로드 실패: " + e.getMessage())));
        }

        // 회원 저장 시도
        try {
            //닉네임 중복 검사
            System.out.println("닉네임 중복 검사");
            SiteUser siteUser =loginUtil.getSiteUserFromLogin(request,principal);
            // 본인 닉네임인지 확인해서 같지 않으면 중복검사함.
            if(!siteUser.getNickname().equals(profileRequest.getNickname())){
                if(siteUserService.isNicknameDuplicated(profileRequest.getNickname())){
                    bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                    return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
                }
            }


            // 사용자 저장
            System.out.println("사용자 저장");
            // 서비스 호출
            boolean isUpdated = siteUserService.updateUserProfile(profileRequest, profileImageUrl);
            if (isUpdated) {

                System.out.println("업데이트 성공");
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "회원 수정 성공",
                        "profileimageurl", profileImageUrl
                ));
            } else {
                // 업데이트 실패
                System.out.println("업데이트 실패");
                return ResponseEntity.badRequest().body(Map.of("errors", List.of("회원 수정 실패")));
            }

        } catch (DataIntegrityViolationException e) {
            System.out.println("이미 등록된 사용자입니다.");
            bindingResult.reject("updateFailed", "이미 등록된 사용자입니다.");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 등록된 사용자입니다.")));
        } catch (IllegalArgumentException e) {
            System.out.println("현재 비밀번호가 일치하지 않습니다");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("현재 비밀번호가 일치하지 않습니다")));

        }
        catch (Exception e) {
            System.out.println("updateFailed");
            bindingResult.reject("updateFailed", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("errors", List.of(e.getMessage())));
        }

    }

    // 회원정보수정 GET
    @GetMapping("/api/user/profile")
    public ResponseEntity<?> getProfile(Principal principal,HttpServletRequest request){
        System.out.println("유저프로필겟!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        // OAuth2 로그인 처리
        if (principal != null && principal instanceof OAuth2AuthenticationToken) {

            try {
                // 사용자 정보를 TokenUtil에서 가져오기
                SiteUser user = tokenUtil.getSiteUserFromRequest(request);

                // 사용자 정보 추출
                Long userId = user.getId();
                String email = user.getEmail();
                String cellPhone = user.getCellphone();
                String nickName = user.getNickname();
                Role role = user.getRole();
                String provider = user.getProvider();
                String businessNo = user.getBusinessno();
                String getProfileImageUrl = user.getProfileImageUrl();

                // 응답 생성
                SiteUserProfileResponse response = SiteUserProfileResponse.builder()
//                        .userid(userId)
                        .email(email)
                        .cellphone(cellPhone)
                        .nickname(nickName)
                        .role(role)
//                        .provider(provider)
//                        .businessno(businessNo)
                        .profileimageurl(getProfileImageUrl)
                        .build();

                System.out.println("회원정보수정겟!!!: "+response.getEmail());

                // success 필드 추가하여 반환
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "user", response
                ));
            } catch (Exception e) {
                // 기존: return ResponseEntity.status(401).body("Invalid or expired token");
                // 수정: JSON으로 감싸서 응답
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid or expired token"));
            }
        } else if(principal != null) {
            // 폼 로그인 처리
            String email ="";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                email = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기
            }
            // 사용자 데이터 조회
            Optional<SiteUser> siteUser = siteUserService.findByEmail(email);
            if (siteUser.isPresent()) {
                SiteUser user = siteUser.get();
                Long userId = user.getId();
                String cellPhone = user.getCellphone();
                String name = user.getName();
                String nickName= user.getNickname();
                String provider = user.getProvider();
                Role role = user.getRole();
                String businessNo = user.getBusinessno();
                String getProfileImageUrl =user.getProfileImageUrl();


                // DTO를 사용해 응답 반환
                // DTO 생성
                SiteUserProfileResponse response = SiteUserProfileResponse.builder()
//                        .userid(userId)
                        .cellphone(cellPhone)
                        .email(email)
//                        .name(name)
                        .nickname(nickName)
//                        .provider(provider)
                        .role(role)
//                        .businessno(businessNo) // 필요 없으면 null 처리
                        .profileimageurl(getProfileImageUrl)
                        .build();

                System.out.println("회원정보수정겟!!!: "+response.getEmail());
                // success 필드 추가하여 반환
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "user", response
                ));
            } else {
                // 기존: return ResponseEntity.status(404).body("User not found");
                // 수정: JSON 형태
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found"));
            }

        }else {
            // principal == null → 로그인되어 있지 않은 경우
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));

        }
    }

}
