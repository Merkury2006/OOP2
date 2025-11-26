package vsu.cs.oop2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vsu.cs.oop2.Entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
