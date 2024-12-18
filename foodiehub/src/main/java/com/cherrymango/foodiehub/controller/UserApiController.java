package com.cherrymango.foodiehub.controller;

import com.cherrymango.foodiehub.dto.AddAdminRequest;
import com.cherrymango.foodiehub.dto.AddUserRequest;
import com.cherrymango.foodiehub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

    // 회원가입_회원
    @PostMapping("/user")
    public String signup(@Valid AddUserRequest addUserRequest, BindingResult bindingResult) {
        // 입력값 검증 에러 처리
        if (bindingResult.hasErrors()) {
            System.out.println("BindingResult Errors: " + bindingResult.getAllErrors());
            return "signup";
        }

        // 패스워드 불일치 검증
        if (!addUserRequest.getPassword1().equals(addUserRequest.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup";
        }

        // 회원 저장 시도
        try {
            // 닉네임 중복 검사
            if(userService.isNicknameDuplicated(addUserRequest.getNickname())){
                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                return "signup";
            }
            // 사용자 저장
            System.out.println("저장 권한 singup : "+addUserRequest.getRole());
            System.out.println("Email singup : "+addUserRequest.getEmail());
            userService.save(addUserRequest);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup";
        } catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup";
        }

        return "redirect:/login"; // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }

    // 회원가입_관리자
    @PostMapping("/admin")
    public String signup(@Valid AddAdminRequest addAdminRequest, BindingResult bindingResult) {
        // 입력값 검증 에러 처리
        if (bindingResult.hasErrors()) {
            System.out.println("BindingResult Errors: " + bindingResult.getAllErrors());
            return "signup_admin";
        }

        // 패스워드 불일치 검증
        if (!addAdminRequest.getPassword1().equals(addAdminRequest.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_admin";
        }

        // 회원 저장 시도
        try {
            // 닉네임 중복 검사
            if(userService.isNicknameDuplicated(addAdminRequest.getNickname())){
                bindingResult.rejectValue("nickname","nicknameDuplicated","이미 사용 중인 닉네임 입니다.");
                return "signup_admin";
            }
            System.out.println("저장 권한 singup : "+addAdminRequest.getRole());
            System.out.println("Email singup : "+addAdminRequest.getEmail());
            userService.save_admin(addAdminRequest);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다. 회원정보를 다시 확인해주세요.");
            return "signup_admin";
        } catch (Exception e) {
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_admin";
        }

        return "redirect:/login"; // 회원 가입이 완료된 이후에 로그인 페이지로 이동
    }

    // 로그아웃
//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response){
//        new SecurityContextLogoutHandler().logout(request, response,
//                SecurityContextHolder.getContext().getAuthentication());
//        return "redirect:/login";
//
//    }

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isDuplicated = userService.isNicknameDuplicated(nickname);
        return ResponseEntity.ok().body(Map.of("isDuplicated", isDuplicated));
    }
}
