package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.comunication.FileServerRestClient;
import ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions.CustomException;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoDistribution;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoRequestExpense;
import ar.edu.utn.frc.tup.lc.iv.dtos.common.DtoResponseExpense;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseCategoryEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseDistributionEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ExpenseInstallmentEntity;
import ar.edu.utn.frc.tup.lc.iv.enums.ExpenseType;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseCategoryModel;
import ar.edu.utn.frc.tup.lc.iv.models.ExpenseModel;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseCategoryRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseDistributionRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseInstallmentRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExpenseServiceTest {
    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private ExpenseDistributionRepository expenseDistributionRepository;
    @Mock
    private ExpenseInstallmentRepository expenseInstallmentRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ExpenseCategoryService expenseCategoryService;
    @Mock
    private FileServerRestClient fileServerRestClient; //mock this when file server function

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void postExpense_ValidRequest_ReturnsOkResponse() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        when(expenseRepository.findExpenseEntitiesByInvoiceNumberAndProviderId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        ExpenseEntity validExpenseEntity = new ExpenseEntity();
        validExpenseEntity.setAmount(new BigDecimal("100.00"));
        validExpenseEntity.setCategory(new ExpenseCategoryEntity());
        validExpenseEntity.setDescription("Test Expense");
        validExpenseEntity.setExpenseDate(LocalDate.now());
        validExpenseEntity.setExpenseType(ExpenseType.COMUN);
        validExpenseEntity.setInvoiceNumber(909090);
        validExpenseEntity.setProviderId(1);

        when(modelMapper.map(any(ExpenseModel.class), eq(ExpenseEntity.class))).thenReturn(validExpenseEntity);
        when(modelMapper.map(any(), eq(ExpenseDistributionEntity.class))).thenReturn(new ExpenseDistributionEntity());
        when(modelMapper.map(any(), eq(ExpenseInstallmentEntity.class))).thenReturn(new ExpenseInstallmentEntity());

        when(expenseRepository.save(any())).thenReturn(validExpenseEntity);

        ResponseEntity<DtoResponseExpense> response = expenseService.postExpense(request, file);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(expenseRepository, times(1)).save(any());
        verify(expenseDistributionRepository, times(1)).save(any());
        verify(expenseInstallmentRepository, times(1)).save(any());
    }

    @Test
    void postExpense_DuplicateExpense_ThrowsCustomException() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        when(expenseRepository.findExpenseEntitiesByInvoiceNumberAndProviderId(anyInt(), anyInt()))
                .thenReturn(Optional.of(new ExpenseEntity()));

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.postExpense(request, file);
        });

        assertEquals("The expense have already exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void postExpense_InvalidCategory_ThrowsCustomException() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        when(expenseRepository.findExpenseEntitiesByInvoiceNumberAndProviderId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.postExpense(request, file);
        });

        assertEquals("The category does not exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void postExpense_InvalidFileType_ThrowsCustomException() {
        DtoRequestExpense request = createValidDtoRequestExpense();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/octet-stream");

        when(expenseRepository.findExpenseEntitiesByInvoiceNumberAndProviderId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        CustomException exception = assertThrows(CustomException.class, () -> {
            expenseService.postExpense(request, file);
        });

        assertEquals("the file must be an image or pdf", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void postExpense_InvalidDistributions_ThrowsIllegalArgumentException() {

        DtoRequestExpense request = createValidDtoRequestExpense();
        request.getDistributions().get(0).setProportion(new BigDecimal("11.00"));
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("image/jpeg");

        when(expenseRepository.findExpenseEntitiesByInvoiceNumberAndProviderId(anyInt(), anyInt()))
                .thenReturn(Optional.empty());

        ExpenseCategoryModel categoryModel = new ExpenseCategoryModel();
        categoryModel.setId(1);
        when(expenseCategoryService.getCategoryModel(anyInt())).thenReturn(categoryModel);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.postExpense(request, file);
        });

        assertEquals("the sum of distributions can't be less or more than 10.00", exception.getMessage());
    }

    private DtoRequestExpense createValidDtoRequestExpense() {
        DtoRequestExpense request = new DtoRequestExpense();
        request.setDescription("Test Expense");
        request.setProviderId(1);
        request.setExpenseDate(LocalDate.now());
        request.setInvoiceNumber(9243);
        request.setTypeExpense("COMUN");
        request.setCategoryId(1);
        request.setAmount(new BigDecimal("100.00"));
        request.setInstallments(1);

        DtoDistribution distribution = new DtoDistribution();
        distribution.setOwnerId(1);
        distribution.setProportion(new BigDecimal("10.00"));
        request.setDistributions(Collections.singletonList(distribution));

        return request;
    }

}