package edu.kh.comm.member.model.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.kh.comm.common.Util;
import edu.kh.comm.member.model.dao.MyPageDAO;
import edu.kh.comm.member.model.vo.Member;

@Service
public class MyPageServiceImpl implements MyPageService {
	
	@Autowired
	private MyPageDAO dao;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	@Override
	public int updateInfo(Map<String, Object> paramMap) {
		return dao.updateInfo(paramMap);
	}

	@Override
	public int changePw(Map<String, Object> paramMap) {
		
		String encPw = dao.selectEncPw((int)paramMap.get("memberNo"));
		
		// 내가 입력한 비밀번호랑 DB에 저장되어있는 비밀번호가 일치하면 바꾼다.
		if(bcrypt.matches((String)paramMap.get("currentPw"), encPw)) {
			// 새비밀번호 입력한거를, 새비밀번호 암호화한걸로 바꿔서 둘다 저장해 map에
			paramMap.put("newPw", bcrypt.encode((String)paramMap.get("newPw")));
			
			return dao.changePw(paramMap);
		}
		return 0;
	}

	@Override
	public int secession(Member loginMember) {
		// 입력한 비밀번호를 암호화 해
		String encPw = dao.selectEncPw(loginMember.getMemberNo());
		
		// 저장된 비밀번호와 입력한 비밀번호를 비교해서 같다면
		if(bcrypt.matches(loginMember.getMemberPw(), encPw)) {

			// dao에 loginMember 주고 받아와
			return dao.secession(loginMember.getMemberNo());
			
		}
		return 0;
	}

	@Override
	public int updateProfile(Map<String, Object> map) throws IOException {
						// webPath, folderPath, uploadImage, delete, memberNo
		
		// 자주쓰는 값 변수에 저장
		MultipartFile uploadImage = (MultipartFile) map.get("uploadImage");
		String delete = (String) map.get("delete"); // "0" (변경) / "1" (삭제)
		
		// 프로필 이미지 삭제여부를 확인해서
		// 삭제가 아닌 경우(== 새 이미지로 변경) -> 업로드된 파일명을 변경
		// 삭제가 된 경우 -> NULL 값을 준비 (DB update)
		
		String renameImage = null; //변경된 파일명 저장
		
		if( delete.equals("0") ) { // 이미지가 변경된 경우
			
			// 파일명 변경
			// uploadImage.getOriginalFilename() : 원본 파일명
			renameImage = Util.fileRename(uploadImage.getOriginalFilename());
			// 202304228154532.png
			
			map.put("profileImage", map.get("webPath") + renameImage);
			//    /resources/images/memberProfile/202304228154532.png
			
		} else {
			
			map.put("profileImage", null); 
		}
		
		
		// DAO를 호출해서 프로필 이미지 수정
		int result = dao.updateProfile(map);
		
		// DB 수정 성공 시 메모리에 임시 저장되어있는 파일을 서버에 저장
		if(result > 0 && map.get("profileImage") != null) {
			uploadImage.transferTo( new File( map.get("folderPath") + renameImage ));
		}
		
		
		
		return result;
	}

	
	
	
	
	
	
	
	
	
	
	
}
