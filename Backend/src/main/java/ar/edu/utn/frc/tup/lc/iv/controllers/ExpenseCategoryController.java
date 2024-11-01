package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseDeleteExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.ExpenseCategoryDTO;
import ar.edu.utn.frc.tup.lc.iv.services.impl.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoCategory;
import ar.edu.utn.frc.tup.lc.iv.services.interfaces.IExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class ExpenseCategoryController {
    @Autowired

    private IExpenseCategoryService expenseCategoryService;


    @PostMapping("/postCategory")
    public ResponseEntity<ExpenseCategoryDTO> postCategory(String description){
        return ResponseEntity.ok(expenseCategoryService.postCategory(description));
    }
    @DeleteMapping("/deleteById")
    public ResponseEntity<DtoResponseDeleteExpense> deleteExpenseCategory(Integer id) {
        return  ResponseEntity.ok(expenseCategoryService.deteleCategory(id));
    }
    @PutMapping("/putById")
    public ResponseEntity<ExpenseCategoryDTO> PutExpenseCategory(Integer id, String description){
        return ResponseEntity.ok(expenseCategoryService.putCategory(id,description));
    }
    /**
     * Retrieves all available categories.
     *
     * @return a ResponseEntity containing a list of DtoCategory objects.
     */
    @GetMapping("/all")
    public ResponseEntity<List<DtoCategory>>  getAllCategories() {
        return ResponseEntity.ok( expenseCategoryService.getAllCategories());
    }
    @PutMapping("/setEnabled")
    public ResponseEntity<DtoCategory> setEnabled(Integer id){
        return ResponseEntity.ok(expenseCategoryService.enableCategory(id));
    }
}
