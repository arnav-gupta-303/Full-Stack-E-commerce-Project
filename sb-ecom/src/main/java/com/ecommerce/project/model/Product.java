package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @NotBlank(message = "ProductName cannot be empty")
    @Size(min = 3,message = "Product must contain atleast 3 characters")
    private String productName;
    private String image;
    @NotBlank(message = "ProductName cannot be empty")
    @Size(min = 3,message = "Product must contain atleast 3 characters")
    private String description;
    private Integer quantity;
    private Double discount;
    private Double price;
    private Double SpecialPrice;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
