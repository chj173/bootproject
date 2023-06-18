package com.chj.bootProject.controller;

import com.chj.bootProject.dto.MemberDTO;
import com.chj.bootProject.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    // 생성자 주입(@RequiredArgsConstructor)
    private final MemberService memberService;

    // 회원가입 화면
    @GetMapping("/save")
    public String saveForm() {
        return "/member/save";
    }

    // 회원가입 정보
    @PostMapping("/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        memberService.save(memberDTO);
        return "/member/login";
    }

    // 로그인화면
    @GetMapping("/login")
    public String loginForm() {
        return "/member/login";
    }

    // 로그인정보
    @PostMapping("/login")
    public String login(@ModelAttribute MemberDTO memberDTO, HttpSession session) {
        MemberDTO loginResult = memberService.login(memberDTO);
        if(loginResult != null) {
            // login 성공
            session.setAttribute("loginEmail",loginResult.getMemberEmail());
            return "/member/main";
        } else {
            // login 실패
            return "/member/login";
        }
    }

    // 전체 회원목록
    @GetMapping("/list")
    public String findAll(Model model) {
        List<MemberDTO> memberDTOList = memberService.findAll();

        model.addAttribute("memberList",memberDTOList);
        return "/member/list";
    }

    // 회원정보 조회
    @GetMapping("/list/{id}")
    public String findById(@PathVariable Long id, Model model) {
        MemberDTO memberDTO = memberService.findById(id);
        model.addAttribute("member", memberDTO);
        return "/member/detail";
    }

    // 회원정보 수정화면
    @GetMapping("/update")
    public String updateForm(HttpSession session, Model model) {
        String myEmail = (String)session.getAttribute("loginEmail");
        MemberDTO memberDTO = memberService.updateForm(myEmail);
        model.addAttribute("updateMember", memberDTO);
        return "/member/update";
    }

    // 회원정보 수정
    @PostMapping("/update")
    public String update(@ModelAttribute MemberDTO memberDTO){
        memberService.update(memberDTO);
        return "redirect:/member/list/" + memberDTO.getId();
    }

    // 회원정보 삭제
    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id){
        memberService.deleteById(id);
        return "redirect:/member/list";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // save.html ajax통신 (중복 이메일 확인)
    @PostMapping("/email-check")
    public @ResponseBody String emailCheck(@RequestParam("memberEmail") String memberEmail) {
        System.out.println("memberEmail = " + memberEmail);
        String checkResult = memberService.emailCheck(memberEmail);
        return checkResult; // 사용가능 ok 아니면 null
    }

}
