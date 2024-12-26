package com.example.e_commercial.ui.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.CartItemModel
import com.example.domain.usecase.DeleteProductUseCase
import com.example.domain.usecase.GetCartUseCase
import com.example.domain.usecase.UpdateQuantityUseCase
import com.example.e_commercial.EcommercialSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartUseCase: GetCartUseCase,
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val deleteItem: DeleteProductUseCase,
    private val ecommercialSession: EcommercialSession

) : ViewModel() {
    private val _uiState = MutableStateFlow<CartEvent>(CartEvent.Loading)
    val uiState = _uiState.asStateFlow()
    private val userDomainModel = ecommercialSession.getUser()

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartEvent.Loading
            val result = cartUseCase.execute(userDomainModel?.id?.toLong() ?: return@launch)
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

    fun incrementQuantity(cartItem: CartItemModel) {
        if (cartItem.quantity >= 10) {
            _uiState.value = CartEvent.Error("Maximum quantity reached")
            return
        }
        updateQuantity(cartItem, cartItem.quantity + 1)
    }

    fun decrementQuantity(cartItem: CartItemModel) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is CartEvent.Success) {
                if (cartItem.quantity <= 1) {
                    removeItem(cartItem)
                } else {
                    updateQuantity(cartItem, cartItem.quantity - 1)
                }
            }
        }
    }


    private fun updateQuantity(cartItem: CartItemModel, newQuantity: Int) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is CartEvent.Success) {
                val updatedCartItems = currentState.message.map { item ->
                    if (item.id == cartItem.id) item.copy(quantity = newQuantity) else item
                }

                _uiState.value = CartEvent.Success(updatedCartItems)

                val result = updateQuantityUseCase.execute(
                    cartItem.copy(quantity = newQuantity),
                    userDomainModel?.id?.toLong() ?: return@launch
                )

                if (result is com.example.domain.network.ResultWrapper.Failure) {
                    _uiState.value = currentState
                    _uiState.value = CartEvent.Error("Failed to update quantity")
                }
            }
        }
    }

    fun removeItem(cartItem: CartItemModel) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is CartEvent.Success) {
                val updatedCartItems = currentState.message.filter { it.id != cartItem.id }

                // Optimistically update the UI
                if (updatedCartItems.isEmpty()) {
                    _uiState.value = CartEvent.Empty("Your cart is now empty!")
                } else {
                    _uiState.value = CartEvent.Success(updatedCartItems)
                }

                // Perform the delete operation
                val result = deleteItem.execute(cartItem.id, userDomainModel?.id?.toLong() ?: return@launch)
                if (result is com.example.domain.network.ResultWrapper.Failure) {
                    // Revert to the previous state if deletion fails
                    _uiState.value = currentState
                }
            }
        }
    }


    sealed class CartEvent {
        data object Loading : CartEvent()
        data class Empty(val message: String? = null) : CartEvent()
        data class Success(val message: List<CartItemModel>) : CartEvent()
        data class Error(val message: String) : CartEvent()
    }

}
