package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.domain.Role;
import com.cherrymango.foodiehub.domain.SiteUser;
import com.cherrymango.foodiehub.dto.*;
import com.cherrymango.foodiehub.repository.SiteUserRepository;
import com.cherrymango.foodiehub.service.FileUploadService;
import com.cherrymango.foodiehub.service.SiteUserService;
import com.cherrymango.foodiehub.util.JwtUtil;
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
    @PostMapping("/api/auth/user")
    public ResponseEntity<?> signup(@Valid @RequestBody AddUserRequest addUserRequest, BindingResult bindingResult) {
        // 입력값 검증 에러 처리
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errorMessages));
        }

        // 패스워드 불일치 검증
        if (!addUserRequest.getPassword1().equals(addUserRequest.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("2개의 패스워드가 일치하지 않습니다.")));
        }

        // 회원 저장 시도
        try {
            // 닉네임 중복 검사
            if(siteUserService.isNicknameDuplicated(addUserRequest.getNickname())){
                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
            }
            // 사용자 저장
            System.out.println("저장 권한 singup : "+addUserRequest.getRole());
            System.out.println("Email singup : "+addUserRequest.getEmail());
            siteUserService.save(addUserRequest);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 등록된 사용자입니다.")));
        } catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("errors", List.of(e.getMessage())));
        }

        // 성공 응답 반환
        return ResponseEntity.ok(Map.of("message", "회원 가입 성공"));
    }

    // 회원가입_관리자
    @PostMapping("/api/auth/admin")
    public ResponseEntity<?> signup(@Valid @RequestBody AddAdminRequest addAdminRequest, BindingResult bindingResult) {
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
    // 사용자 정보 반환
//    @GetMapping("/api/auth/me")
//    public ResponseEntity<?> getUserInfo(Principal principal) {
//        String email ="";
//
//        // OAuth2 로그인 처리
//        if (principal != null && principal instanceof OAuth2AuthenticationToken) {
//            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;
//            Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
//            email = (String) attributes.get("email");
//        } else {
//            // 폼 로그인 처리
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null) {
//                email = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기
//            }
//        }
//
//        // 이메일이 없을 경우 에러 반환
//        if (email == null || email.isEmpty() || email.equals("anonymousUser")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is missing");
//        }
//
//
//        // 사용자 데이터 조회
//        Optional<SiteUser> siteUser = siteUserService.findByEmail(email);
//        if (siteUser.isPresent()) {
//            SiteUser user = siteUser.get();
//            Long userId = user.getId();
//            String cellPhone = user.getCellphone();
//            String name = user.getName();
//            String nickName= user.getNickname();
//            String provider = user.getProvider();
//            Role role = user.getRole();
//            String businessNo = user.getBusinessno();
//            String getProfileImageUrl =user.getProfileImageUrl();
//
//
//            // DTO를 사용해 응답 반환
//            // DTO 생성
//            SiteUserInfoResponse response = SiteUserInfoResponse.builder()
//                    .userid(userId)
//                    .cellphone(cellPhone)
//                    .email(email)
//                    .name(name)
//                    .nickname(nickName)
//                    .provider(provider)
//                    .role(role)
//                    .businessno(businessNo) // 필요 없으면 null 처리
//                    .profileimageurl(getProfileImageUrl)
//                    .build();
//
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        }
//    }

    // 로그인 성공 시 사용자 정보 반환
    @GetMapping("/api/auth/header")
    public Map<String, Object> loginsuccess(HttpServletRequest request,Principal principal) {// 세션 정보 출력
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("ls세션 ID: " + session.getId());
            System.out.println("ls세션 속성:");
            session.getAttributeNames().asIterator().forEachRemaining(attr -> System.out.println(attr + ": " + session.getAttribute(attr)));
        } else {
            System.out.println("ls세션이 존재하지 않습니다.");
        }


        Map<String, Object> response = new HashMap<>();
        System.out.println("principal 권한: " + principal);

        String username = "";
        String username_google = "";
        String username_kakao = "";
        String email = "";
        String role = "";

        if (principal != null) {
            // OAuth2 로그인 처리
            if (principal instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) principal;

                // 사용자 속성 가져오기
                Map<String, Object> attributes = authToken.getPrincipal().getAttributes();
                System.out.println("oauth2 권한: " + attributes);

                username_google = (String) attributes.get("name"); // Google 로그인 이름
                username_kakao = (String) attributes.get("nickname"); // Kakao 로그인 이름
                email = (String) attributes.get("email");

                // 권한 정보 가져오기
                role = authToken.getAuthorities().stream()
                        .findFirst()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .orElse("ROLE_USER");

                // 이름 설정
                username = (username_kakao != null) ? username_kakao : username_google;

            } else {
                // 폼 로그인 처리
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("권한: " + authentication);

                if (authentication != null) {
                    username = authentication.getName();//authentication.getName(); // 사용자 이름 가져오기

                    // 권한 정보 설정 (첫 번째 권한만 가져오기)
                    role = authentication.getAuthorities().stream()
                            .findFirst()
                            .map(grantedAuthority -> grantedAuthority.getAuthority())
                            .orElse("ROLE_USER");
                }
            }
        }

        // JSON 응답 데이터 구성
        response.put("username", username);
        response.put("email", email);
        response.put("role", role);

        return response;
    }

    // 회원정보수정
    @PostMapping("/api/user/update-profile")
    public ResponseEntity<?> updateUserProfile(@Valid @ModelAttribute SiteUserProfileRequest profileRequest, HttpServletRequest request,BindingResult bindingResult){
        System.out.println("updateUserProfile!!!");
        String profileImageUrl = null;

//        SiteUser siteUser;
//        String authHeader = request.getHeader("Authorization");
//        System.out.println(authHeader);
//        // JWT 검증 및 사용자 정보 추출
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(401).body("Authorization header missing or invalid");
//        }
//        String token = authHeader.substring(7); // "Bearer " 이후의 토큰 값
//        try {
//            String email = jwtUtil.extractEmail(token); // 이메일 추출
//            Long userId = jwtUtil.extractUserId(token); // 유저 ID 추출
//            String role = jwtUtil.extractUserRole(token); // 역할(Role) 추출
//            siteUser = siteUserService.findById(userId);
//            System.out.println(siteUser);
//            System.out.println(siteUser.getEmail());
//
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body(Map.of("errors", List.of("Invalid or expired token")));
//        }
        SiteUser siteUser =  tokenUtil.getSiteUserFromRequest(request);
        System.out.println("라라라"+siteUser.getEmail());




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
            // 닉네임 중복 검사
//            System.out.println("닉네임 중복 검사");
//            System.out.println(profileRequest.getNickname());
//            if(userService.isNicknameDuplicated(profileRequest.getNickname())){
//                System.out.println("닉네임 중복검사 여기는 들어옵니까?");
//                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
//                return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
//            }

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
