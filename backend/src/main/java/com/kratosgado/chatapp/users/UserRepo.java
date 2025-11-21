
package com.kratosgado.chatapp.users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, String> {
  boolean existsByEmail(String email);
}
