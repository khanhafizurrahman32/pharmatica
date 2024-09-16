package org.example.pharmaticb.service.product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.pharmaticb.Models.DB.Brand;
import org.example.pharmaticb.Models.DB.Category;
import org.example.pharmaticb.Models.DB.Country;
import org.example.pharmaticb.Models.DB.Product;
import org.example.pharmaticb.Models.Request.BulkProductCreateRequest;
import org.example.pharmaticb.Models.Request.ProductRequest;
import org.example.pharmaticb.Models.Response.BulkProductCreateResponse;
import org.example.pharmaticb.Models.Response.CountryResponse;
import org.example.pharmaticb.Models.Response.ProductResponse;
import org.example.pharmaticb.dto.ProductWithDetails;
import org.example.pharmaticb.exception.InternalException;
import org.example.pharmaticb.repositories.BrandRepository;
import org.example.pharmaticb.repositories.CategoryRepository;
import org.example.pharmaticb.repositories.CountryRepository;
import org.example.pharmaticb.repositories.ProductRepository;
import org.example.pharmaticb.service.country.CountryService;
import org.example.pharmaticb.service.file.FileUploadService;
import org.example.pharmaticb.utilities.DateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static org.example.pharmaticb.utilities.DateUtil.BULK_PRODUCT_INPUT_FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CountryService countryService;
    private final FileUploadService fileUploadService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final CountryRepository countryRepository;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {
        return productRepository.save(convertDtoToDb(request, Product.builder().build()))
                .flatMapMany(product -> productRepository.findAllProductDetails(product.getId(), null, null, null))
                .next()
                .map(this::convertDbToDto);
    }

    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAllProductDetails(null, null, null, null)
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<ProductResponse> getProductById(long id) {
        return productRepository.findAllProductDetails(id, null, null, null)
                .next()
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<ProductResponse> updateProduct(long id, ProductRequest request) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    var productUpdated = convertDtoToDb(request, product);
                    return productRepository.save(productUpdated);
                })
                .flatMapMany(product -> productRepository.findAllProductDetails(product.getId(), null, null, null))
                .next()
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<Void> deleteProduct(long id) {
        return productRepository.deleteById(id);
    }

    @Override
    public Flux<ProductResponse> getProductsByCategoryId(long categoryId) {
        return productRepository.findAllProductDetails(null, null, categoryId, null)
                .map(this::convertDbToDto);
    }

    @Override
    public Flux<ProductResponse> getProductsByBrandId(long brandId) {
        return productRepository.findAllProductDetails(null, null, null, brandId)
                .map(this::convertDbToDto);
    }

    @Override
    public Flux<ProductResponse> getProductsByProductName(String productName) {
        return productRepository.findAllProductDetails(null, productName, null, null)
                .map(this::convertDbToDto);
    }

    @Override
    public Mono<BulkProductCreateResponse> createBulkProduct(BulkProductCreateRequest request) {
        return fileUploadService.downloadFile(request.getFilePath())
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .flatMapMany(content -> Flux.fromStream(
                        new BufferedReader(new InputStreamReader(
                                new ByteArrayInputStream(content.getBytes())))
                                .lines()
                                .skip(1)
                ))
                .map(this::parseProductFromCsvLine)
                .flatMap(productMono -> productMono
                        .doOnNext(product -> log.debug("Product emitted: {}", product))
                        .doOnError(error -> log.error("Error in productMono: ", error))
                        .switchIfEmpty(Mono.fromRunnable(() -> log.warn("Empty productMono")))
                )
                .log("After flatMap")  // This will log all signals at this point in the stream
                .doOnNext(product -> log.debug("Parsed product: {}", product))
                .flatMap(product -> {
                    log.info("product:: {}", product.getId());
                    return insertProductIntoDatabase(product);
                })
                .collectList()
                .map(ret -> BulkProductCreateResponse.builder().success(true).build())
                .log("Final result");
    }

    private Mono<Product> insertProductIntoDatabase(Product product) {
        return productRepository.save(product);
    }

    private Mono<Product> parseProductFromCsvLine(String line) {
        List<String> parts = parseLine(line);

        if (parts.size() < 13) {
            return Mono.error(new IllegalArgumentException("CSV line does not have enough fields: " + line));
        }

        return Mono.zip(
                        getCategoryId(parts.get(3)).doOnNext(id -> log.info("Category ID: {}", id)),
                        getBrandId(parts.get(5)).doOnNext(id -> log.info("Brand ID: {}", id)),
                        getCountryId(parts.get(7)).doOnNext(id -> log.info("Country ID: {}", id))
                )
                .flatMap(tuple3 -> {
                    try {
                        Product product = Product.builder()
                                .productName(parts.get(0))
                                .price(Double.parseDouble(parts.get(1)))
                                .imageUrl(parts.get(2))
                                .categoryId(tuple3.getT1())
                                .discount(getDiscount(parts.get(1), parts.get(4)))
                                .brandId(tuple3.getT2())
                                .expires(getExpireDate(parts.get(6)))
                                .countryId(tuple3.getT3())
                                .description(parts.get(8))
                                .howToUse(parts.get(9))
                                .ingredients(parts.get(10))
                                .stock(Double.parseDouble(parts.get(11)))
                                .composition(parts.get(12))
                                .build();
                        return Mono.just(product);
                    } catch (NumberFormatException e) {
                        return Mono.error(new IllegalArgumentException("Failed to parse numeric value in line: " + line, e));
                    }
                })
                .doOnNext(product -> log.debug("Successfully parsed product: {}", product))
                .doOnError(error -> log.error("Error parsing product from line: {}. Error: {}", line, error.getMessage()));
    }

    public LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, BULK_PRODUCT_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Could not parse date: " + dateString, e);
        }
    }
    private String getExpireDate(String expiresDate) {
        try {
            LocalDate date = parseDate(expiresDate);
            return DateUtil.productExpiresFormat(date);
        } catch (DateTimeParseException e) {
            log.error("could not parse date {}", expiresDate, e);
            throw new InternalException(HttpStatus.BAD_REQUEST, "could not parse date " + expiresDate, "EXPIRE_DATE_01");
        }
    }

    private double getDiscount(String price, String discount) {
        try {
            BigDecimal priceValue = new BigDecimal(price.trim());
            BigDecimal discountPercentage = new BigDecimal(discount.trim());

            if (priceValue.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }

            if (discountPercentage.compareTo(BigDecimal.ZERO) < 0 || discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
            }

            BigDecimal discountFactor = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal result = priceValue.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
            return result.doubleValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + e.getMessage(), e);
        }
    }

    private Mono<Long> getCategoryId(String label) {
        return categoryRepository.findByLabel(label)
                .map(Category::getId)
                .switchIfEmpty(Mono.defer(() -> categoryRepository.save(Category.builder().label(label).build()))
                        .map(Category::getId));
    }

    private Mono<Long> getBrandId(String label) {
        return brandRepository.findByBrandName(label)
                .map(Brand::getId)
                .switchIfEmpty(Mono.defer(() -> brandRepository.save(Brand.builder().brandName(label).build()))
                        .map(Brand::getId));
    }

    private Mono<Long> getCountryId(String label) {
        return countryRepository.findByCountryName(label)
                .map(Country::getId)
                .switchIfEmpty(Mono.defer(() -> countryRepository.save(Country.builder().countryName(label).build()))
                        .map(Country::getId));
    }

    private Mono<CountryResponse> getCountryResponse(long countryId) {
        return countryService.getCategoryById(countryId);
    }

    private ProductResponse convertDbToDto(ProductWithDetails product) {
        return ProductResponse.builder()
                .productId(String.valueOf(product.getId()))
                .productName(product.getProductName())
                .composition(product.getComposition())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(Category.builder()
                        .id(product.getCategoryId())
                        .label(product.getCategoryLabel())
                        .iconUrl(product.getCategoryIconUrl())
                        .categorySlug(product.getCategorySlug())
                        .subCategories(product.getSubCategories())
                        .brand(product.getBrand())
                        .priceRange(product.getPriceRange())
                        .build())
                .discount(product.getDiscount())
                .brand(Brand.builder()
                        .id(product.getBrandId())
                        .brandName(product.getBrandName())
                        .build())
                .expires(product.getExpires())
                .country(Country.builder()
                        .id(product.getCountryId())
                        .countryName(product.getCountryName())
                        .build())
                .description(product.getDescription())
                .howToUse(product.getHowToUse())
                .ingredients(product.getIngredients())
                .stock(product.getStock())
                .coupons(product.getCoupons())
                .build();
    }


    private Product convertDtoToDb(ProductRequest request, Product product) {
        return Product.builder()
                .id(!ObjectUtils.isEmpty(product.getId()) ? product.getId() : null)
                .productName(request.getProductName())
                .composition(request.getComposition())
                .price(Double.parseDouble(request.getPrice()))
                .imageUrl(request.getImageUrl())
                .categoryId(Long.parseLong(request.getCategoryId()))
                .discount(Double.parseDouble(request.getDiscount()))
                .brandId(Long.parseLong(request.getBrandId()))
                .expires(request.getExpires())
                .countryId(Long.parseLong(request.getCountryId()))
                .description(request.getDescription())
                .howToUse(request.getHowToUse())
                .ingredients(request.getIngredients())
                .stock(Double.parseDouble(request.getStock()))
                .coupons(request.getCoupons())
                .build();
    }

    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // Toggle the inQuotes flag when we encounter a quote
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // If we're not in quotes and we find a comma, we've reached the end of a field
                result.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                // Otherwise, add the character to the current field
                currentField.append(c);
            }
        }

        // Add the last field
        result.add(currentField.toString().trim());

        return result;
    }
}
