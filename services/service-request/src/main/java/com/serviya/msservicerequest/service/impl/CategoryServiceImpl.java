package com.serviya.msservicerequest.service.impl;

import com.serviya.msservicerequest.dto.category.CategoryRequest;
import com.serviya.msservicerequest.dto.category.CategoryResponse;
import com.serviya.msservicerequest.entity.Categoria;
import com.serviya.msservicerequest.exception.DuplicateResourceException;
import com.serviya.msservicerequest.exception.ResourceNotFoundException;
import com.serviya.msservicerequest.mapper.CategoryMapper;
import com.serviya.msservicerequest.repository.CategoriaRepository;
import com.serviya.msservicerequest.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoriaRepository categoriaRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        validateNombreDisponible(request.getNombre(), null);
        Categoria saved = categoriaRepository.save(categoryMapper.toEntity(request));
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll() {
        return categoriaRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return categoryMapper.toResponse(getCategoria(id));
    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Categoria categoria = getCategoria(id);
        validateNombreDisponible(request.getNombre(), categoria.getId());
        categoryMapper.updateEntity(categoria, request);
        return categoryMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        categoriaRepository.delete(getCategoria(id));
    }

    private Categoria getCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria con id " + id + " no encontrada"));
    }

    private void validateNombreDisponible(String nombre, Long currentId) {
        categoriaRepository.findByNombreIgnoreCase(nombre)
                .filter(item -> !item.getId().equals(currentId))
                .ifPresent(item -> {
                    throw new DuplicateResourceException("Ya existe una categoria con nombre " + nombre);
                });
    }
}
