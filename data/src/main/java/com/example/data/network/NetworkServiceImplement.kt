package com.example.data.network

import android.util.Log
import com.example.data.model.CategoryDataModel
import com.example.data.model.DataProductModel
import com.example.data.model.request.AddToCartRequest
import com.example.data.model.request.AddressDataModel
import com.example.data.model.request.ChangePasswordRequest
import com.example.data.model.request.LoginRequest
import com.example.data.model.request.RegisterRequest
import com.example.data.model.response.CartResponse
import com.example.data.model.response.CartSummaryResponse
import com.example.data.model.response.CategoriesListResponse
import com.example.data.model.response.OrdersListResponse
import com.example.data.model.response.PlaceOrderResponse
import com.example.data.model.response.ProductListResponse
import com.example.data.model.response.ProductResponse
import com.example.data.model.response.UserAuthResponse
import com.example.data.model.response.UserResponse
import com.example.domain.model.AddressDomainModel
import com.example.domain.model.CartItemModel
import com.example.domain.model.CartModel
import com.example.domain.model.CartSummary
import com.example.domain.model.CategoriesListModel
import com.example.domain.model.OrdersListModel
import com.example.domain.model.Product
import com.example.domain.model.ProductListModel
import com.example.domain.model.UserDomainModel
import com.example.domain.model.request.AddCartRequestModel
import com.example.domain.network.NetworkService
import com.example.domain.network.ResultWrapper
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import io.ktor.utils.io.errors.IOException

class NetworkServiceImplement(val client: HttpClient) : NetworkService {
    private val baseUrl = "http://192.168.1.15:8081"

    override suspend fun getProducts(category: Int?): ResultWrapper<ProductListModel> {
        val url ="$baseUrl/products"
//            if (category != null) "$baseUrl/products/category/$category" else "$baseUrl/products"
        return makeWebRequest(url = url,
            method = HttpMethod.Get,
            mapper = { dataModels: ProductListResponse ->
                dataModels.toProductList()
            })
    }

    override suspend fun getBestSellers(): ResultWrapper<ProductListModel> {
        val url = "$baseUrl/products/best-sellers"
        return makeWebRequest(url = url,
            method = HttpMethod.Get,
            mapper = { dataModels: ProductListResponse ->
                dataModels.toProductList()
            })
    }

    override suspend fun getProductsByCategory(categoryId: Int): ResultWrapper<ProductListModel> {
        val url = "$baseUrl/products/category/$categoryId"
        return makeWebRequest(url = url,
            method = HttpMethod.Get,
            mapper = { dataModels: ProductListResponse ->
                dataModels.toProductList()
            })
    }

    override suspend fun getCategories(): ResultWrapper<CategoriesListModel> {
        val url = "$baseUrl/categories"
        return makeWebRequest(url = url,
            method = HttpMethod.Get,
            mapper = { categories: CategoriesListResponse ->
                categories.toCategoriesList()
            })
    }

    override suspend fun addProductToCart(
        request: AddCartRequestModel,
        userId: Long
    ): ResultWrapper<CartModel> {
        val url = "$baseUrl/cart/${userId}"
        return makeWebRequest(url = url,
            method = HttpMethod.Post,
            body = AddToCartRequest.fromCartRequestModel(request),
            mapper = { cartItem: CartResponse ->
                cartItem.toCartModel()
            })
    }

    override suspend fun getCart(userId: Long): ResultWrapper<CartModel> {
        val url = "$baseUrl/cart/$userId"
        return makeWebRequest(url = url,
            method = HttpMethod.Get,
            mapper = { cartItem: CartResponse ->
                cartItem.toCartModel()
            })
    }

    override suspend fun updateQuantity(
        cartItemModel: CartItemModel,
        userId: Long
    ): ResultWrapper<CartModel> {
        val url = "$baseUrl/cart/$userId/${cartItemModel.id}"
        return makeWebRequest(url = url,
            method = HttpMethod.Put,
            body = AddToCartRequest(
                productId = cartItemModel.productId,
                quantity = cartItemModel.quantity
            ),
            mapper = { cartItem: CartResponse ->
                cartItem.toCartModel()
            })
    }

    override suspend fun deleteItem(cartItemId: Int, userId: Long): ResultWrapper<CartModel> {
        val url = "$baseUrl/cart/$userId/$cartItemId"
        return makeWebRequest(url = url,
            method = HttpMethod.Delete,
            mapper = { cartItem: CartResponse ->
                cartItem.toCartModel()
            })
    }

    override suspend fun getCartSummary(userId: Long): ResultWrapper<CartSummary> {
        val url = "$baseUrl/orders/checkout/$userId"
        return makeWebRequest(url = url,
            method = HttpMethod.Get,
            mapper = { cartSummary: CartSummaryResponse ->
                cartSummary.toCartSummary()
            })
    }

    override suspend fun placeOrder(
        address: AddressDomainModel,
        userId: Long
    ): ResultWrapper<Long> {
        val dataModel = AddressDataModel.fromDomainAddress(address)
        val url = "$baseUrl/orders/$userId"
        return makeWebRequest(url = url,
            method = HttpMethod.Post,
            body = dataModel,
            mapper = { orderRes: PlaceOrderResponse ->
                orderRes.data.id
            })
    }

    private suspend fun getProductImage(productId: Int): String {
        val productUrl = "$baseUrl/products/$productId"
        return try {
            val response = client.request(productUrl) {
                method = HttpMethod.Get
                contentType(ContentType.Application.Json)
            }
            val productResponse = response.body<ProductResponse>() 
            productResponse.data.image ?: "https://via.placeholder.com/150"
        } catch (e: Exception) {
            Log.e("getProductImage", "Failed to fetch image for Product ID: $productId", e)
            "https://via.placeholder.com/150"
        }
    }




    override suspend fun getOrderList(userId: Long): ResultWrapper<OrdersListModel> {
        val url = "$baseUrl/orders/$userId"
        return try {
            val response = client.request(url) {
                method = HttpMethod.Get
                contentType(ContentType.Application.Json)
            }

            val responseBody = response.bodyAsText()
            Log.d("BackEndHandler", "RESPONSE: $responseBody")

            val ordersResponse = response.body<OrdersListResponse>()
            ResultWrapper.Success(ordersResponse.toDomainResponse(::getProductImage))
        } catch (e: Exception) {
            Log.e("BackEndHandler", "Error fetching order list", e)
            ResultWrapper.Failure(e)
        }
    }






    override suspend fun login(email: String, password: String): ResultWrapper<UserDomainModel> {
        val url = "$baseUrl/auth/login"
        return makeWebRequest(
            url = url,
            method = HttpMethod.Post,
            body = LoginRequest(email, password),
            mapper = { user: UserAuthResponse ->
                user.data?.toDomainModel()
                    ?: throw IllegalStateException("Missing 'data' field in UserAuthResponse")
            }
        )
    }

    override suspend fun changePassword(
        email: String,
        oldPassword: String,
        newPassword: String
    ): ResultWrapper<Unit> {
        val url = "$baseUrl/auth/change-password"
        return makeWebRequest<Unit, Unit>(
            url = url,
            method = HttpMethod.Post,
            body = ChangePasswordRequest(email, oldPassword, newPassword),
            mapper = { Unit }
        )
    }


    override suspend fun register(
        email: String,
        password: String,
        name: String
    ): ResultWrapper<UserDomainModel> {
        val url = "$baseUrl/auth/register"
        return makeWebRequest(
            url = url,
            method = HttpMethod.Post,
            body = RegisterRequest(email, password, name),
            mapper = { user: UserAuthResponse ->
                user.data?.toDomainModel()
                    ?: throw IllegalStateException("Missing 'data' field in UserAuthResponse")
            }
        )
    }


    suspend inline fun <reified T, R> makeWebRequest(
        url: String,
        method: HttpMethod,
        body: Any? = null,
        headers: Map<String, String> = emptyMap(),
        parameters: Map<String, String> = emptyMap(),
        noinline mapper: suspend (T) -> R // Suspendable mapper
    ): ResultWrapper<R> {
        return try {
            val response = client.request(url) {
                this.method = method
                url {
                    this.parameters.appendAll(Parameters.build {
                        parameters.forEach { (key, value) ->
                            append(key, value)
                        }
                    })
                }
                headers.forEach { (key, value) ->
                    header(key, value)
                }
                if (body != null) {
                    setBody(body)
                }
                contentType(ContentType.Application.Json)
            }.body<T>()
            val result: R = mapper(response) // Execute mapper in the suspend context
            ResultWrapper.Success(result)
        } catch (e: ClientRequestException) {
            ResultWrapper.Failure(e)
        } catch (e: ServerResponseException) {
            ResultWrapper.Failure(e)
        } catch (e: IOException) {
            ResultWrapper.Failure(e)
        } catch (e: Exception) {
            ResultWrapper.Failure(e)
        }
    }


}