package com.slembers.alarmony.alarm.service;

import com.slembers.alarmony.member.dto.MemberInfoDto;
import java.util.List;

public interface GroupService {

    /**
     * 초대 가능한 멤버 리스트를 반환합니다.
     *
     * @param groupId 그룹 id
     * @param keyword 검색할 키워드
     * @return 초대 가능한 멤버 목록
     */
    List<MemberInfoDto> getInviteableMemberInfoList(Long groupId, String keyword);

    /**
     * 그룹에서 유저네임을 기준으로 멤버를 제외한다.
     * 
     * @param groupId 그룹 id
     * @param username 그룹에서 제외할 멤버 유저네임
     */
    void removeMemberByUsername(Long groupId, String username);

}
