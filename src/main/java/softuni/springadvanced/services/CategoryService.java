package softuni.springadvanced.services;



import softuni.springadvanced.models.entity.Category;
import softuni.springadvanced.models.service.CategoryServiceModel;

import java.util.List;

public interface CategoryService {

    void saveCategoriesInDatabase();

    List<Category> getAllCategories();

    CategoryServiceModel getCategoryServiceModelByName(String name);

    Category getCategoryByName(String name);
}
