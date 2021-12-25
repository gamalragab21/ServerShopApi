package com.example.repositories

import com.example.data.model.*
import com.example.data.tables.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ktorm.database.Database
import org.ktorm.dsl.*

class CategoryAndProductRepository(val db: Database,val userRepository: UserRepository) {


    suspend fun createCategory(category: Category) = withContext(Dispatchers.IO) {
        val result = db.insert(CategoryEntity) {
            set(it.categoryName, category.categoryName)
            set(it.restaurantId, category.restaurantId)
        }
        result
    }

    suspend fun createProduct(productRequest: Product) = withContext(Dispatchers.IO) {

        val resultInsertProduct = db.insertAndGenerateKey(ProductEntity) {
            set(it.categoryId, productRequest.categoryId)
            set(it.restaurantId, productRequest.restaurantId)
            set(it.productName, productRequest.productName)
            set(it.productPrice, productRequest.productPrice)
            set(it.productDescription, productRequest.productDescription)
            set(it.freeDelivery, productRequest.freeDelivery)
            set(it.createAt, productRequest.createAt)
            set(it.coinType, productRequest.coinType)
        }

        println("resultInsertProduct: ${resultInsertProduct}")
        if (resultInsertProduct as Int > 0) {
            uploadImageOfProduct(resultInsertProduct, productRequest)
            resultInsertProduct
        } else {
            0
        }


    }

    private suspend fun uploadImageOfProduct(resultInsertProduct: Int, productRequest: Product) =
        withContext(Dispatchers.IO) {
            productRequest.images.forEach { image ->
                val resultInsertImage = db.insert(ProductImageEntity) {
                    set(it.productId, resultInsertProduct)
                    set(it.imageProduct, image.imageProduct)
                }
            }

        }

    suspend fun getCategoryOfRestaurant(restaurantId: Int) = withContext(Dispatchers.IO) {
        val categories = db.from(CategoryEntity)
            .select()
            .where {
                CategoryEntity.restaurantId eq restaurantId
            }
            .mapNotNull {
                rowToCategories(it)
            }

        categories
    }

    private fun rowToCategories(row: QueryRowSet): Category? {
        return if (row == null) {
            null
        } else {
            Category(
                row[CategoryEntity.categoryId] ?: -1,
                row[CategoryEntity.restaurantId] ?: -1,
                row[CategoryEntity.categoryName] ?: ""
            )
        }

    }

    suspend fun getProductOfCategory(categoryId: Int, restaurantId: Int,user: User) = withContext(Dispatchers.IO) {
        val products = db.from(ProductEntity)
            .select()
            .orderBy(ProductEntity.createAt.desc())
            .where {
                (ProductEntity.categoryId eq categoryId) and (ProductEntity.restaurantId eq restaurantId)
            }
            .mapNotNull {
                rowToProducts(it,user)
            }

//        products.forEach { product ->
//            val imagesProduct = db.from(ProductImageEntity)
//                .select()
//                .where {
//                    ProductImageEntity.productId eq product.productId!!
//                }
//                .mapNotNull {
//                    rowToImageProduct(it)
//                }
//            println("List images :${imagesProduct.toString()}")
//            productsList.add(
//                ProductResponse(
//                    product, imagesProduct, getRateProduct(product.productId?:-1)
//                )
//            )
//        }



        products
    }

    suspend fun getProductForYou(restaurantId: Int,user: User) = withContext(Dispatchers.IO) {
        val products = db.from(ProductEntity)
            .select()
            .orderBy(ProductEntity.createAt.desc())
            .where {
              ProductEntity.restaurantId eq restaurantId
            }
            .mapNotNull {
                rowToProducts(it,user)
            }

        products.sortedByDescending {
            it.rateCount
        }

        products
    }

    private suspend fun getImagesProduct(productId: Int)= withContext(Dispatchers.IO) {
        val imagesProduct = db.from(ProductImageEntity)
            .select()
            .where {
                ProductImageEntity.productId eq productId
            }
            .mapNotNull {
                rowToImageProduct(it)
            }

        imagesProduct

    }

    private suspend fun getRateProduct(id: Int) = withContext(Dispatchers.IO) {
            val rateProduct = db.from(RateProductEntity)
                .select()
                .where {
                    RateProductEntity.productId eq id
                }
                .mapNotNull {
                    rowToRateProduct(it)
                }

            rateProduct
        }

    private suspend fun rowToRateProduct(row: QueryRowSet):RateProduct? {
        return if (row==null){
            null
        }else{
            val rateId=row[RateProductEntity.rateId]?:-1
            val userId=row[RateProductEntity.userId]?:-1
            val productId=row[RateProductEntity.productId]?:-1
            val countRate=row[RateProductEntity.countRate]?:0.0
            val messageRate=row[RateProductEntity.messageRate]?:""
            val createAt=row[RateProductEntity.createAt]?:0
            val user= userRepository.findUserById(userId)
            RateProduct(rateId,userId,productId,countRate,messageRate,createAt, user!!)
        }
    }

    private fun rowToImageProduct(row: QueryRowSet): ProductImage? {
        return if (row == null) {
            null
        } else {
            ProductImage(
                row[ProductImageEntity.productImageId] ?: -1,
                row[ProductImageEntity.productId] ?: -1,
                row[ProductImageEntity.imageProduct] ?: ""
            )
        }
    }

    private suspend fun rowToProducts(row: QueryRowSet, user: User): Product? {
        return if (row == null) {
            null
        } else {
            val productId=row[ProductEntity.productId] ?: -1
            val categoryId=row[ProductEntity.categoryId] ?: -1
            val restaurantId=row[ProductEntity.restaurantId] ?: -1
            val productName=row[ProductEntity.productName] ?: ""
            val coinType:String=row[ProductEntity.coinType]?:"$"
            val productPrice=row[ProductEntity.productPrice] ?: 0.0
            val createAt=row[ProductEntity.createAt] ?: 0
            val freeDelivery=row[ProductEntity.freeDelivery] ?: true
            val productDescription=row[ProductEntity.productDescription] ?:""
            val favProduct=checkProductInFav(productId, user.id!!)
            val isFav = favProduct != null

            val imageProductList=getImagesProduct(productId)
            val rateProductList=getRateProduct(productId)
            var total = 0.0
            rateProductList.forEach {
                total += it.countRate
            }
            val ratingCount =if(rateProductList.isNotEmpty()){
                total / rateProductList.size
            }else{
                0.0
            }

            Product(
                productId,
                categoryId,
                restaurantId,
                productName,
                productPrice,
                createAt,
                coinType,
                freeDelivery,
                productDescription,
                isFav,
                false,
                ratingCount,
                imageProductList,
                rateProductList,
                user
            )
        }
    }

    private suspend fun checkProductInFav(productId: Int, userId: Int)= withContext(Dispatchers.IO){
        val favProduct =
            db.from(FavProductEntity)
                .select()
                .where {
                    (FavProductEntity.productId eq productId) and (FavProductEntity.userId eq userId)
                }.map {
                    rowToFavProduct(it)
                }.firstOrNull()


        println("favProduct is :${favProduct}")
        favProduct
    }

    private  fun rowToFavProduct(row: QueryRowSet): FavProduct?{
          return   if (row==null) {
              null
       }else{
            val favProductId=row[FavProductEntity.favProductId]?:-1
            val userId=row[FavProductEntity.userId]?:-1
            val productId=row[FavProductEntity.productId]?:-1
            FavProduct(favProductId,userId,productId)
        }
    }

    suspend fun rateProduct(rateProduct: RateProduct)= withContext(Dispatchers.IO){
        val result=db.insert(RateProductEntity) {
            set(it.productId, rateProduct.productId)
            set(it.userId, rateProduct.userId)
            set(it.countRate, rateProduct.countRate)
            set(it.messageRate, rateProduct.messageRate)
            set(it.createAt, rateProduct.createAt)
        }
        result
    }
    suspend fun updateRateProduct(rateProduct: RateProduct)= withContext(Dispatchers.IO) {

        val result=db.update(RateProductEntity){
            set(it.countRate, rateProduct.countRate)
            set(it.createAt, rateProduct.createAt)
            set(it.messageRate, rateProduct.messageRate)

            where {
                (it.rateId eq rateProduct.rateId!!) and
               (it.userId eq rateProduct.userId!!) and
                (it.productId eq rateProduct.productId)
            }
        }

        result
    }

    suspend fun setInFavProduct(favRestaurant: FavProduct)= withContext(Dispatchers.IO){
        println("FavProduct: ${favRestaurant.toString()}")
        val result=db.insert(FavProductEntity) {
            set(it.productId, favRestaurant.productId)
            set(it.userId, favRestaurant.userId)
        }
        result
    }

    suspend fun getAllFavProduct(user: User) = withContext(Dispatchers.IO) {
        val favProduct = db.from(FavProductEntity)
            .select()
            .where {
                FavProductEntity.userId eq user.id!!
            }.map {row->
             //  rowToFavProduct(row)
//                val favProductId=row[FavProductEntity.favProductId]?:-1
//                val userId=row[FavProductEntity.userId]?:-1
                val productId=row[FavProductEntity.productId]?:-1
//                FavProduct(favProductId,userId,productId)

                val product=findProductById(productId,user)
               product?.inFav=true
                product
            }

        favProduct
    }

    suspend fun findProductById(productId: Int, user: User)=withContext(Dispatchers.IO){
        // this fun check if user email exist or not and if exists return user info
        val restaurant = db.from(ProductEntity)
            .select()
            .where {
                ProductEntity.productId eq productId
            }.map {
                rowToProducts(it,user)
            }.firstOrNull()

        restaurant
    }

    suspend fun getPopularProduct(user: User)= withContext(Dispatchers.IO) {
        // this fun check if user email exist or not and if exists return user info
        val popularProduct = db.from(ProductEntity)
            .select()
            .mapNotNull {
                rowToProducts(it, user)
            }

        popularProduct.sortedByDescending {
            it.rateCount
        }
    }

    suspend fun deleteFavouriteProduct(productId: Int, userId: Int?)= withContext(Dispatchers.IO) {
        val result= db.delete(FavProductEntity){
            (it.userId eq userId!!) and (it.productId eq productId)
        }
        result
    }


}