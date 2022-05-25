package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.src.auth.model.PostSignupReq;
import com.example.demo.src.auth.model.PostSignupRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
//import static com.example.demo.utils.ValidationRegex.isRegexNickName;

@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;

    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService){
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    /**
     * 회원가입 API
     * [POST] /auth/signup
     * @param postSignupReq
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/signup") // (POST) 127.0.0.1:9000/users
    public BaseResponse<PostSignupRes> createUser(@RequestBody PostSignupReq postSignupReq) {
        //email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postSignupReq.getEmail() == null){ //이메일 없음
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(!isRegexEmail(postSignupReq.getEmail())){ // 이메일 정규표현
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try{
            PostSignupRes postSignupRes = authService.createUser(postSignupReq);
            return new BaseResponse<>(postSignupRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /auth/login
     * @param postLoginReq
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
        try{
            PostLoginRes postLoginRes= authService.login(postLoginReq);

            //validation 처리
            if(postLoginReq.getEmail() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if(postLoginReq.getPw() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            if(!isRegexEmail(postLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }

            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

}