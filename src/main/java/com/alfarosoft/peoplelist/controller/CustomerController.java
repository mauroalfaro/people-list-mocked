package com.alfarosoft.peoplelist.controller;

import com.alfarosoft.peoplelist.exception.AddressValidationException;
import com.alfarosoft.peoplelist.exception.PeopleListException;
import com.alfarosoft.peoplelist.model.Customer;
import com.alfarosoft.peoplelist.service.CustomerService;
import com.alfarosoft.peoplelist.validation.AddressValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;

@RestController
@RequestMapping(value = "/services/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final AddressValidation addressValidation;

    @Autowired
    public CustomerController(CustomerService customerService, AddressValidation addressValidation) {
        this.customerService = customerService;
        this.addressValidation = addressValidation;
    }

    @Operation(summary = "Adds a customer to the mocked list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Customer successfully created",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid body applied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PostMapping(value = "/add", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> addCustomer (@RequestBody Customer customer){
        addressValidation.validateAddress(customer.getAddress());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(customerService.addCustomer(customer));
    }

    @Operation(summary = "Searches a Customer by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class)) }),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> lookupCustomer (@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomer(id));
    }

    @Operation(summary = "Searches for all the Customers on the mocked list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class)) }),
            @ApiResponse(responseCode = "404", description = "Customers not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Customer>> searchCustomers(){
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomers());
    }

    @Operation(summary = "Updates Customer data after finding it by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class)) }),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @PutMapping(value = "/update/{id}" , produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Customer> updateCustomer (@PathVariable String id, @RequestBody Customer customer){
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomer(id, customer));
    }

    @Operation(summary = "Deletes a Customer from the list after finding it by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Customer.class)) }),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)})
    @DeleteMapping(value = "/delete/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteCustomer(@PathVariable String id){
        customerService.removeCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Customer successfully removed");
    }

    @ExceptionHandler(PeopleListException.class)
    public ResponseEntity<String> handleException(final PeopleListException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AddressValidationException.class)
    public ResponseEntity<String> handleException(final AddressValidationException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
