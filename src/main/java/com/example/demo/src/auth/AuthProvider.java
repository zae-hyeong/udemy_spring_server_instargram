package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

//Provider : Read의 비즈니스 로직 처리
@Service
public class AuthProvider {
    private final AuthDao authDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthProvider(AuthDao authDao, JwtService jwtService) {
        this.authDao = authDao;
        this.jwtService = jwtService;
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return authDao.checkEmail(email);   // Dao에서 이메일을 체크하는 걸 넘겨줌, checkEmail()에는 현재 이메일이 존재하는지 확인하는 쿼리가 있음
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}