package org.example.backendpossystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {
    String customerId;
    String customerName;
    String customerEmail;
    String customerAddress;
    String customerPhone;
}
