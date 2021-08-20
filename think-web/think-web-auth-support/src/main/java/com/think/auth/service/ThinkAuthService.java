package com.think.auth.service;

import com.think.data.provider.ThinkAuthRepository;
import com.think.moudles.auth.ThinkAuthModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThinkAuthService {

    @Autowired
    ThinkAuthRepository repository;

    /**
     * 通过 用户id（long） 登录
     * @param id
     * @param pw
     * @return
     */
    public ThinkAuthModel login(long id ,String pw){
        return repository.get(id,pw);

    }

    /**
     * 通过user id（String） 登录
     * @param id
     * @param pw
     * @return
     */
    public ThinkAuthModel login(String id ,String pw){
        return repository.get(id,pw);
    }


    public boolean updatePassword(String id ,String pw ,String newPw){
        ThinkAuthModel authModel = this.repository.get(id,pw);
        if(authModel!=null){
            return repository.updatePw(id,newPw);
        }
        return false;
    }


    /**
     * 创建用户
     * @param id
     * @param shaPw
     * @param accountId
     * @return
     */
    public boolean create(long id,String shaPw , String accountId){
        return this.repository.insert(accountId,shaPw,id);
    }


}
