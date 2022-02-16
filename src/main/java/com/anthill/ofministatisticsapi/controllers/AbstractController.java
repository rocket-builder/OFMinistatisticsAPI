package com.anthill.ofministatisticsapi.controllers;

import com.anthill.ofministatisticsapi.beans.AbstractEntity;
import com.anthill.ofministatisticsapi.exceptions.ResourceNotFoundedException;
import com.anthill.ofministatisticsapi.interfaces.CommonController;
import com.anthill.ofministatisticsapi.interfaces.CommonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public abstract class AbstractController<E extends AbstractEntity, R extends CommonRepository<E>>
    implements CommonController<E> {

    protected final R repos;

    protected AbstractController(R repos) {
        this.repos = repos;
    }

    @Override
    public ResponseEntity<E> save(@RequestBody E entity) throws Exception {
        E res = repos.save(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<List<E>> saveAll(@RequestBody List<E> entities) {
        repos.saveAll(entities);

        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<E> update(@RequestBody E entity) throws ResourceNotFoundedException {
        E res = repos.save(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<E> findById(@RequestParam long id) throws ResourceNotFoundedException {

        var entity = repos.findById(id);
        if(entity.isPresent()){
            return new ResponseEntity<>(entity.get(), HttpStatus.OK);
        }

        throw new ResourceNotFoundedException();
    }

    @Override
    public ResponseEntity<List<E>> findAll() {
        List<E> entities = repos.findAll();

        return new ResponseEntity<>(entities, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<E> deleteById(@RequestParam long id) throws ResourceNotFoundedException {
        var entity = repos.findById(id);
        if(entity.isPresent()){
            repos.delete(entity.get());

            return new ResponseEntity<>(entity.get(), HttpStatus.OK);
        }

        throw new ResourceNotFoundedException();
    }
    @Override
    public ResponseEntity<E> delete(@RequestBody E entity) throws ResourceNotFoundedException {

        if(repos.findById(entity.getId()).isPresent()){
            repos.delete(entity);

            return new ResponseEntity<>(entity, HttpStatus.OK);
        }

        throw new ResourceNotFoundedException();
    }
}
