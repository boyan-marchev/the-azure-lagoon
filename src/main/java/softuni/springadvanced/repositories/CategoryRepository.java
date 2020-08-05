package softuni.springadvanced.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.springadvanced.models.entity.Category;


import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Optional<Category> findByName(String name);

    Category getByName(String name);
}
