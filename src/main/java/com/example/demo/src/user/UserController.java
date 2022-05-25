package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 특정 유저 피드 조회 API
     * [GET] /users/:userIdx/feed
     * @param userIdx
     * @return BaseResponse<GetUserFeedRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/feed") // GetMapping 파라미터로 GET메소드를 명시해줌. 파라미터로 명시된 게 없으면 기본값으로 (GET) 127.0.0.1:9000/users 가 들어감
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx") int userIdx) { // RequestParam~ 부분이 쿼리스트링으로 이메일을 받겠다고 명시를 해준 것
        // GetUserRes는 모델! 모델은 쉽게 말하면 응답값이다. 모델에서는 필요한 요청값과 응답값의 형식을 정의해줌
        try{
            int userIdxByJwt = jwtService.getUserIdx();

            //내 피드인지 구분하기 위해 유저 인덱스를 두개로 나눠서 줌
            GetUserFeedRes getUserFeedRes = userProvider.retrieveUserFeed(userIdxByJwt,userIdx); //retrieve = 검색하다
            return new BaseResponse<>(getUserFeedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 유저 정보 조회 API
     * [GET] /user/:userIdx
     * @param userIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx") int userIdx) { // RequestParam~ 부분이 쿼리스트링으로 이메일을 받겠다고 명시를 해준 것
        // GetUserRes는 모델! 모델은 쉽게 말하면 응답값이다. 모델에서는 필요한 요청값과 응답값의 형식을 정의해줌
        try{
            GetUserRes getUsersRes = userProvider.getUserByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 정보 변경 API
     * [PATCH] /users/:userIdx
     * @param userIdx, user
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}") // (PATCH) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getNickName());
            userService.modifyUserName(patchUserReq); // 현재는 유저 이름만 변경이 가능함

            String result = "게시물 수정 성공";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
