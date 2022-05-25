package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedRes {
    private boolean _isMyFeed; // 내 피드냐 아니냐에 따라 보이는 정보가 약간씩 달라짐 -> 이걸 구분하기 위한 정보
    private GetUserInfoRes getUserInfo; //유저 정보
    private List<GetUserPostsRes> getUserPosts; //유저 게시물
}
