package softuni.springadvanced.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.springadvanced.models.entity.Category;
import softuni.springadvanced.models.entity.CategoryName;
import softuni.springadvanced.models.service.CategoryServiceModel;
import softuni.springadvanced.repositories.CategoryRepository;
import softuni.springadvanced.services.CategoryService;


import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @PostConstruct
    public void init() {
        if (this.getAllCategories().size() == 0) {
            this.saveCategoriesInDatabase();
        }

    }


    @Override
    public void saveCategoriesInDatabase() {

        for (CategoryName categoryName : CategoryName.values()) {
            Category category = new Category(categoryName.toString(), String.format("%s", categoryName.toString()));
            this.categoryRepository.save(category);
        }

    }

    @Override
    public List<Category> getAllCategories() {
        return this.categoryRepository.findAll();
    }

    @Override
    public CategoryServiceModel getCategoryServiceModelByName(String name) {
        CategoryServiceModel categoryServiceModel = this.modelMapper
                .map(this.categoryRepository.getByName(name), CategoryServiceModel.class);

        if (categoryServiceModel != null) {
            return categoryServiceModel;

        } else {
            return null;
        }
    }

    @Override
    public Category getCategoryByName(String name) {
        return this.categoryRepository.findByName(name).orElse(null);
    }
}
