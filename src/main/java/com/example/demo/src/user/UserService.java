package com.example.demo.src.user;


import com.example.demo.config.BaseException;

import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인 (의미적 validation! controller에서 형식적 validation을 처리, provider와 service에서 논리적 validation 처리)
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            // 특이한 점: Dao가 아닌 Provider로 넘기고있음!
            // 무언가를 체크하는 것도 조회의 의미를 가지고 있기 때문에 provider에서 그 역할을 해주어야 한다.
            // 그래서 service에서 provider의 함수를 가져다 쓰는 것! 이 점을 꼭 유의하자
            // (참고: 조회하는 것은 provider, 생성하는 것은 service에서 처리한다!)
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String encryptPw; //암호화되는 비밀번호
        try{
            //암호화
            encryptPw = new SHA256().encrypt(postUserReq.getPw());
            postUserReq.setPw(encryptPw);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try{
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0) {
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
