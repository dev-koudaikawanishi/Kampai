package com.kampai.repository;

import com.kampai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // メールアドレスでユーザーを検索するメソッド（ログイン等で使う想定）
    Optional<User> findByEmail(String email);

    // ユーザー名で検索したい場合も追加可能
    Optional<User> findByUsername(String username);
}
