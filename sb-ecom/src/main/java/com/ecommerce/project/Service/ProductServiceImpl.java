package com.ecommerce.project.Service;

import com.ecommerce.project.Exceptions.APIException;
import com.ecommerce.project.Exceptions.ResourceNotFoundException;
import com.ecommerce.project.Repository.CategoryRepository;
import com.ecommerce.project.Repository.ProductRepository;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Value("${product.image}")
    private String path;
    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","CategoryID",categoryId));
        String productName = productDTO.getProductName();
        Optional<Product> productOptional= productRepository.findByProductName(productName);
        if(productOptional.isPresent()){
            throw new APIException("Product with name "+productName+" already exists");
        }
        Product product=modelMapper.map(productDTO,Product.class);
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice=product.getPrice()-((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct=productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage=productRepository.findAll(pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("There are no Products listed.");
        }
        List<ProductDTO> ProductDTos = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(ProductDTos);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastpage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Category category = categoryRepository.findById(categoryId).
                orElseThrow(()-> new ResourceNotFoundException("Category","CategoryID",categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage=productRepository.findByCategoryCategoryId(categoryId,pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("There are no Products listed in the category "+category.getCategoryName()+" .");
        }
        List<ProductDTO> ProductDTos = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(ProductDTos);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastpage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductsByKeyword(String keyword,Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")?
                Sort.by(sortBy).ascending():
                Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage=productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("There are no Products with keyword "+keyword+" .");
        }
        List<ProductDTO> ProductDTos = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(ProductDTos);
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastpage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProductById(ProductDTO productDTO, Long productId) {

        Product oldProduct = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "ProductID", productId)
                );
        Product product=modelMapper.map(productDTO,Product.class);
        if (product.getProductName() != null) {
            oldProduct.setProductName(product.getProductName());
        }

        if (product.getDescription() != null) {
            oldProduct.setDescription(product.getDescription());
        }

        if (product.getQuantity() != null) {
            oldProduct.setQuantity(product.getQuantity());
        }

        if (product.getPrice() != null) {
            oldProduct.setPrice(product.getPrice());
        }

        if (product.getDiscount() != null) {
            oldProduct.setDiscount(product.getDiscount());
        }

        if (oldProduct.getPrice() != null && oldProduct.getDiscount() != null) {
            double specialPrice =
                    oldProduct.getPrice()
                            - (oldProduct.getPrice() * oldProduct.getDiscount() / 100);

            oldProduct.setSpecialPrice(specialPrice);
        }

        Product save = productRepository.save(oldProduct);
        return modelMapper.map(save, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product", "ProductID", productId)
                );
        productRepository.deleteById(productId);
        return modelMapper.map(product, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductByImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDB= productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductID",productId));
//        Upload file image to server
//        Get the filename of uploaded image
        String fileName=fileService.uploadImage(path, image);

//        Updating the new file name o the product
        productFromDB.setImage(fileName);
//        Save in DB
        Product updateProduct = productRepository.save(productFromDB);
        return  modelMapper.map(updateProduct, ProductDTO.class);
    }


}
