package com.slembers.alarmony.member.service;

import com.slembers.alarmony.member.dto.response.CheckDuplicateDto;
import com.slembers.alarmony.member.entity.Member;
import com.slembers.alarmony.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepo;

    /**
     * 아이디 중복체크
     * @param username 유저 아이디
     * @return 존재여부
     **/

    @Override
    public CheckDuplicateDto checkForDuplicateId(String username) {

        return CheckDuplicateDto.builder().isDuplicated(memberRepo.existsByUsername(username)).build();

    }

    /**
     * 이메일 중복 체크
     * @param email :이메일주소
     * @return 존재여부
     */
    @Override
    public CheckDuplicateDto checkForDuplicateEmail(String email) {
        return CheckDuplicateDto.builder().isDuplicated(memberRepo.existsByEmail(email)).build();
    }


    /**
     * 닉네임 중복 체크
     * @param nickname : 닉네임
     * @return 존재여부
     */
    @Override
    public CheckDuplicateDto checkForDuplicateNickName(String nickname) {
        return CheckDuplicateDto.builder().isDuplicated(memberRepo.existsByNickname(nickname)).build();
    }

    @Override
    public Member getMemberByUsername(String username) {
        return null;
    }

    @Override
    public Member getMemberByNickName(String nickname) {
        return null;
    }
}
