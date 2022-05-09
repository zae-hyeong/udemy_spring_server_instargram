package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.src.auth.model.User;
import com.example.demo.src.post.model.PatchPostReq;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AuthService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;

    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        User user = authDao.getUser(postLoginReq);
        String encryptPw;

        try{
            //암호화 : 이렇게 하려면 비밀번호를 암호화해서 DB에 넣는 전처리 과정이 필요함
            encryptPw = new SHA256().encrypt(postLoginReq.getPw());
        } catch(Exception exception){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        if(user.getPw().equals(encryptPw)) {
            //jwt 생성
            int userIdx= user.getUserIdx();
            String jwt= jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        }
        else throw new BaseException(FAILED_TO_LOGIN);
    }
}