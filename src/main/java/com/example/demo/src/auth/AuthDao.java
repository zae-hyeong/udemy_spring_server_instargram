package com.example.demo.src.auth;

import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.User;
import com.example.demo.src.auth.model.PostSignupReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class AuthDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public User getUserPw(PostLoginReq postLoginReq){
        String getPwQuery = "select userIdx, name, nickName, email, password from User where email=?";
        String getPwParams = postLoginReq.getEmail();
        return this.jdbcTemplate.queryForObject(getPwQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
//                        rs.getString("phone"),
                        rs.getString("password")),
                getPwParams);
    }

    public int createUser(PostSignupReq postSignupReq){
        String createUserQuery = "insert into User (name, nickName, email, password) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postSignupReq.getName(), postSignupReq.getNickName(), postSignupReq.getEmail(), postSignupReq.getPw()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";   // 현재 이메일이 존재하는지 확인하는 쿼리
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }
}