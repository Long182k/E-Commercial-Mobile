package com.example.e_commercial.ui.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CartItemModel
import com.example.domain.model.CartModel
import com.example.domain.usecase.DeleteProductUseCase
import com.example.domain.usecase.GetCartUseCase
import com.example.domain.usecase.UpdateQuantityUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    val cartUseCase: GetCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val deleteItem: DeleteProductUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<CartEvent>(CartEvent.Loading)
    val uiState = _uiState.asStateFlow()
    val userDomainModel = EcommercialSession.getUser()

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartEvent.Loading
            cartUseCase.execute(userDomainModel!!.id!!.toLong()).let { result ->
                when (result) {
                    is com.example.domain.network.ResultWrapper.Success -> {
                        _uiState.value = CartEvent.Success(result.value.data)
                    }

                    is com.example.domain.network.ResultWrapper.Failure -> {
                        _uiState.value = CartEvent.Error("Something went wrong!")
                    }
                }
            }
        }
    }


    fun incrementQuantity(cartItem: CartItemModel) {
        if (cartItem.quantity >= 10) { // Prevent exceeding max quantity of 10
            _uiState.value = CartEvent.Error("Maximum quantity reached")
            return
        }

        val updatedItem = cartItem.copy(quantity = cartItem.quantity + 1)
        updateQuantityOptimistically(updatedItem)
    }

    fun decrementQuantity(cartItem: CartItemModel) {
        if (cartItem.quantity <= 1) { // Prevent going below minimum quantity of 1
            _uiState.value = CartEvent.Error("Minimum quantity is 1")
            return
        }

        val updatedItem = cartItem.copy(quantity = cartItem.quantity - 1)
        updateQuantityOptimistically(updatedItem)
    }

    private fun updateQuantityOptimistically(updatedItem: CartItemModel) {
        viewModelScope.launch {
            when (val currentState = _uiState.value) {
                is CartEvent.Success -> {
                    // Replace the updated item while preserving the order
                    val updatedCartItems = currentState.message.map { cartItem ->
                        if (cartItem.id == updatedItem.id) updatedItem else cartItem
                    }

                    // Optimistically update the UI
                    _uiState.value = CartEvent.Success(updatedCartItems)

                    // Perform the backend update
                    val result = updateQuantityUseCase.execute(updatedItem, userDomainModel!!.id!!.toLong())
                    when (result) {
                        is com.example.domain.network.ResultWrapper.Success -> {
                            // Optionally refetch the cart
                            getCart()
                        }
                        is com.example.domain.network.ResultWrapper.Failure -> {
                            // Revert to the previous state if update fails
                            _uiState.value = currentState
                            _uiState.value = CartEvent.Error("Failed to update quantity")
                        }
                    }
                }
                is CartEvent.Loading, is CartEvent.Empty, is CartEvent.Error -> {
                    // Handle other states if necessary, or simply ignore
                }
            }
        }
    }






    fun removeItem(cartItem: CartItemModel) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is CartEvent.Success) {
                val updatedCartItems = currentState.message.filter { it.id != cartItem.id }

                // Optimistically update the state
                _uiState.value = if (updatedCartItems.isEmpty()) {
                    CartEvent.Empty
                } else {
                    CartEvent.Success(updatedCartItems)
                }

                // Perform the delete operation
                val result = deleteItem.execute(cartItem.id, userDomainModel!!.id!!.toLong())
                when (result) {
                    is com.example.domain.network.ResultWrapper.Success -> {
                        // Optionally refetch the cart
                        getCart()
                    }
                    is com.example.domain.network.ResultWrapper.Failure -> {
                        // Revert to the previous state if deletion fails
                        _uiState.value = currentState
                    }
                }
            }
        }
    }


    sealed class CartEvent {
        data object Loading : CartEvent()
        data object Empty : CartEvent()
        data class Success(val message: List<CartItemModel>) : CartEvent()
        data class Error(val message: String) : CartEvent()
    }

}