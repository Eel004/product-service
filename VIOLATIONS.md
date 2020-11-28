### Task list
1. ProductController refactor
- Request method for findAll and findById should use GET instead of POST
- Request method for delete should change to DELETE
- Request method for update should be PUT
- Access modifier for method: newProduct, updateProduct, deleteProduct should be public
- Should use constructor injection for productRepository
- findAll method should return ResponseEntity<List<Product>> instead of ResponseEntity
- findById method should return ResponseEntity<Product> instead of ResponseEntity<?>
- findById method should handle Optional of findById method of spring data
- updateProduct method should handle java optional instead of check null
- create a RestExceptionHandler to centralize all exception.
- Added Validation for product

2.* Added api documentation

3.* Added Lombok for entity

4.* Integration testing for productController

5.* Added Dockerfile and docker compose file to run app on docker

6.* Added production profile

