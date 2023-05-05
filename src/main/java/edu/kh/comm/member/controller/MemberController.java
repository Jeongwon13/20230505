package edu.kh.comm.member.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.comm.member.model.service.MemberService;
import edu.kh.comm.member.model.vo.Member;

@Controller
@RequestMapping("/member")
@SessionAttributes({"loginMember"})
public class MemberController {
	 
	Logger logger = LoggerFactory.getLogger(MemberController.class);

	@Autowired
	private MemberService service;
	
	@PostMapping("/login")
	public String login(@ModelAttribute Member inputMember,
						Model model,
						RedirectAttributes ra,
						HttpServletRequest req,
						HttpServletResponse resp,
						@RequestParam(value="saveId", required = false) String saveId
						) {
		
		logger.info("로그인 기능 수행됨");
		
		Member loginMember = service.login(inputMember);
		
		if(loginMember != null) {
			model.addAttribute("loginMember", loginMember);
			
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			
			if(saveId != null) {
				cookie.setMaxAge(60 * 60 * 24 * 365);
			} else {
				cookie.setMaxAge(0);
			}
			
			cookie.setPath(req.getContextPath());
			
			resp.addCookie(cookie);
			
		} else {
			ra.addFlashAttribute("message", "아이디 또는 비번 일치하지 않라~");
		}
		
		return "redirect:/";
	 
		
	}
	
	
	@GetMapping("/logout")
	public String logout(SessionStatus status) {
		logger.info("로그아웃 수행되었셈 ㅋㅋ");
		status.setComplete();
		
		return "redirect:/";
	} 
	
	@GetMapping("/signUp")
	public String signUp() {
		return "member/signUp";
	}
	 
	@GetMapping("/emailDupCheck")
	@ResponseBody
	public int emailDupCheck(String memberEmail) {
		return service.emailDupCheck(memberEmail);
	} 
	
	@ResponseBody
	@GetMapping("/nicknameDupCheck")
	public int nicknameDupCheck(String memberNickname) {
		int result = service.nicknameDupCheck(memberNickname);
		return result;
	}
	
	
	@PostMapping("/signUp")
	public String signUp(Member inputMember, String[] memberAddress, RedirectAttributes ra) {
		
		// 클라이언트가 입력한 주소를 ,, 구분자 사용해서 구분해놔
		inputMember.setMemberAddress(String.join(",,", memberAddress));
		
		// Member vo에 저장된 주소가 ",,,,"이라면 inputMember.setMemberAddress(null)해
		// 왜냐면 값이 null이라도 있어야 에러가 나지 않거든.
		if(inputMember.getMemberAddress().equals(",,,,")) {
			inputMember.setMemberAddress(null);
		}
		
		int result = service.signUp(inputMember);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "회원가입 성고옹ㅋㅋ";
			path = "redirect:/";
		} else {
			message = "회원가입 실패애ㅋㅋㅋ";
			path = "redirect:/member/signUp";
		}
		
		ra.addFlashAttribute("message", message);
		
		return path;
		
	}
	
	
	
	
}
