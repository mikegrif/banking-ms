package com.mikegrif.accounts.service.impl;

import com.mikegrif.accounts.dto.AccountsDto;
import com.mikegrif.accounts.dto.CustomerDetailsDto;
import com.mikegrif.accounts.dto.CardsDto;
import com.mikegrif.accounts.dto.LoansDto;
import com.mikegrif.accounts.entity.Accounts;
import com.mikegrif.accounts.entity.Customer;
import com.mikegrif.accounts.exception.ResourceNotFoundException;
import com.mikegrif.accounts.mapper.AccountsMapper;
import com.mikegrif.accounts.mapper.CustomerMapper;
import com.mikegrif.accounts.repository.AccountsRepository;
import com.mikegrif.accounts.repository.CustomerRepository;
import com.mikegrif.accounts.service.ICustomersService;
import com.mikegrif.accounts.service.client.CardsFeignClient;
import com.mikegrif.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomersServiceImpl implements ICustomersService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    /**
     * @param mobileNumber - Input Mobile Number
     * @return Customer Details based on a given mobileNumber
     */
    @Override
    public CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString())
        );

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;

    }
}
