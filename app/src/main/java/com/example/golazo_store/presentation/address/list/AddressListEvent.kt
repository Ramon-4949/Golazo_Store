package com.example.golazo_store.presentation.address.list

sealed class AddressListEvent {
    data class OnSearchQueryChange(val query: String) : AddressListEvent()
    data class OnDeleteAddress(val id: Int) : AddressListEvent()
    object LoadAddresses : AddressListEvent()
}
