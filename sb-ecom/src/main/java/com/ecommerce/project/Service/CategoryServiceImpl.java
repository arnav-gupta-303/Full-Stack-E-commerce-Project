package com.ecommerce.project.Service;

import com.ecommerce.project.Exceptions.APIException;
import com.ecommerce.project.Exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        List<Category> all = categoryRepository.findAll();
        if(all.isEmpty()){
            throw new APIException("Category List is Empty");
        }
        return all;
    }

    @Override
    public void createCategory(Category category) {
        Category savedCategory= categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!=null){
            throw new APIException("Category with name "+category.getCategoryName()+" aready exists");
        }
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        categoryRepository.delete(category);
        return "Category with categoryId: " + categoryId + " deleted successfully !!";
    }

    @Override
    public Category     updateCategory(Category category, Long categoryId) {
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId)
                );
        savedCategory.setCategoryName(category.getCategoryName());
        return categoryRepository.save(savedCategory);
    }

}
