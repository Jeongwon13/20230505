package edu.kh.comm.member.model.service;

import java.io.IOException;
import java.util.Map;

import edu.kh.comm.member.model.vo.Member;

public interface MyPageService {

	int updateInfo(Map<String, Object> paramMap);

	int changePw(Map<String, Object> paramMap);

	int secession(Member loginMember);

	int updateProfile(Map<String, Object> map) throws IOException;

}
