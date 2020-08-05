package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Role;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByAuthority(String authority);

    @Query("select r from Role as r")
    Set<Role> findAllAuthorities();
}
