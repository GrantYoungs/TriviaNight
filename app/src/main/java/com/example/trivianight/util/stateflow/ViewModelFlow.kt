package com.example.trivianight.util.stateflow

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ViewModelFlow<ViewState, Event>(initialState: ViewState) {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(initialState)
    val viewState: StateFlow<ViewState> get() = _viewState.asStateFlow()

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 1)
    val events: SharedFlow<Event> get() = _events.asSharedFlow()

    /**
     * Update this ViewState.
     */
    fun update(state: ViewState) {
        _viewState.update { state }
    }

    /**
     * Emits a new Event.
     */
    fun emit(event: Event) {
        _events.tryEmit(event)
    }
}