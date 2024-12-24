package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.dto.AddAdminRequest;
import com.cherrymango.foodiehub.dto.AddUserRequest;
import com.cherrymango.foodiehub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 닉네임 중복 확인
    @GetMapping("/api/user/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        System.out.println("닉네임 중복확인" + nickname);
        boolean isDuplicated = userService.isNicknameDuplicated(nickname);
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
            if(userService.isNicknameDuplicated(addUserRequest.getNickname())){
                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
            }
            // 사용자 저장
            System.out.println("저장 권한 singup : "+addUserRequest.getRole());
            System.out.println("Email singup : "+addUserRequest.getEmail());
            userService.save(addUserRequest);
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
            if(userService.isNicknameDuplicated(addAdminRequest.getNickname())){
                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 사용 중인 닉네임 입니다.")));
            }
            addAdminRequest.setRole("ROLE_ADMIN");
            System.out.println("저장 권한 singup : "+addAdminRequest.getRole());
            System.out.println("Email singup : "+addAdminRequest.getEmail());
            userService.save_admin(addAdminRequest);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다. 회원정보를 다시 확인해주세요.");
            return ResponseEntity.badRequest().body(Map.of("errors", List.of("이미 등록된 사용자입니다.")));
        } catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("errors", List.of(e.getMessage())));
        }

        return ResponseEntity.ok(Map.of("message", "회원 가입 성공"));
    }




}
