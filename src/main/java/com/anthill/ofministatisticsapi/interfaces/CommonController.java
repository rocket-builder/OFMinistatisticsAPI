package com.anthill.ofministatisticsapi.interfaces;

import com.anthill.ofministatisticsapi.beans.AbstractEntity;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CommonController<E extends AbstractEntity> {

    @PostMapping
    ResponseEntity<E> save(@RequestBody E entity) throws Exception;
    @PostMapping("/list")
    ResponseEntity<List<E>> saveAll(List<E> entities);

    @PutMapping
    ResponseEntity<E> update(@RequestBody E entity) throws ResourceNotFoundedException;

    @GetMapping("/{id}")
    ResponseEntity<E> findById(@PathVariable("id") long id) throws ResourceNotFoundedException;
    @GetMapping
    ResponseEntity<List<E>> findAll();

    @DeleteMapping("/{id}")
    ResponseEntity<E> deleteById(@PathVariable("id") long id) throws ResourceNotFoundedException;
    @DeleteMapping
    ResponseEntity<E> delete(E entity) throws ResourceNotFoundedException;
}
