package com.serviya.msservicerequest.mapper;

import com.serviya.msservicerequest.dto.category.CategoryRequest;
import com.serviya.msservicerequest.dto.category.CategoryResponse;
import com.serviya.msservicerequest.entity.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Categoria toEntity(CategoryRequest request) {
        return Categoria.builder()
                .nombre(request.getNombre())
                .iconoUrl(request.getIconoUrl())
                .build();
    }

    public void updateEntity(Categoria categoria, CategoryRequest request) {
        categoria.setNombre(request.getNombre());
        categoria.setIconoUrl(request.getIconoUrl());
    }

    public CategoryResponse toResponse(Categoria categoria) {
        return CategoryResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .iconoUrl(categoria.getIconoUrl())
                .build();
    }
}
