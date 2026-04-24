package com.serviya.msservicerequest.service;

import com.serviya.msservicerequest.dto.category.CategoryRequest;
import com.serviya.msservicerequest.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
