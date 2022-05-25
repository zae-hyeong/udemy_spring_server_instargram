package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.src.auth.model.User;
import com.example.demo.src.auth.model.PostSignupReq;
import com.example.demo.src.auth.model.PostSignupRes;
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

    public PostSignupRes createUser(PostSignupReq postSignupReq) throws BaseException {
        // 이메일 중복 확인 (의미적 validation! controller에서 형식적 validation을 처리, provider와 service에서 논리적 validation 처리)
        if(authProvider.checkEmail(postSignupReq.getEmail()) ==1){
            // 특이한 점: Dao가 아닌 Provider로 넘기고있음!
            // 무언가를 체크하는 것도 조회의 의미를 가지고 있기 때문에 provider에서 그 역할을 해주어야 한다.
            // 그래서 service에서 provider의 함수를 가져다 쓰는 것! 이 점을 꼭 유의하자
            // (참고: 조회하는 것은 provider, 생성하는 것은 service에서 처리한다!)
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String encryptPw; //암호화되는 비밀번호
        try{
            //암호화
            encryptPw = new SHA256().encrypt(postSignupReq.getPw());
            postSignupReq.setPw(encryptPw);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = authDao.createUser(postSignupReq);
            //jwt 발급
            String jwt = jwtService.createJwt(userIdx);
            return new PostSignupRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        User user = authDao.getUserPw(postLoginReq);
        String encryptPw;

        try{
            //암호화 : 이렇게 하려면 비밀번호를 암호화해서 DB에 넣는 전처리 과정이 필요함
            //지금은 로그인이기 때문에 입력받은 비밀번호의 암호화 버전과 DB의 압호화된 비밀번호가 일치하는지 비교
            new SHA256();
            encryptPw = SHA256.encrypt(postLoginReq.getPw());
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