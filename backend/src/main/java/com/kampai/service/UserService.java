package com.kampai.service;

import com.kampai.entity.User;
import com.kampai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * ユーザー登録
     * @param user 登録するユーザー情報
     * @return 登録後のユーザー情報
     */
    public User registerUser(User user) {
        // ここでパスワードのハッシュ化やバリデーションを入れることも検討
        return userRepository.save(user);
    }

    /**
     * メールアドレスでユーザー取得
     * @param email 検索するメールアドレス
     * @return ユーザー情報（なければempty）
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * IDでユーザー取得
     * @param id ユーザーID
     * @return ユーザー情報（なければempty）
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
