package io.github.frankois944.matomoKMPTracker.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ConcurrentMutableList<T> {
    private val list = mutableListOf<T>()
    private val mutex = Mutex()

    suspend fun add(item: T) {
        mutex.withLock {
            list.add(item)
        }
    }

    suspend fun remove(item: T) {
        mutex.withLock {
            list.remove(item)
        }
    }

    suspend fun removeAll(condition: (T) -> Boolean) {
        mutex.withLock {
            list.removeAll {
                condition(it)
            }
        }
    }

    suspend fun removeAtIndex(index: Int) {
        mutex.withLock {
            list.removeAt(index)
        }
    }

    fun getAll(): List<T> = list.toList()

    suspend fun indexOfFirst(condition: (T) -> Boolean): Int =
        mutex.withLock {
            list.indexOfFirst { index -> condition(index) }
        }

    suspend fun filter(condition: (T) -> Boolean): List<T> =
        mutex.withLock {
            list.filter { index -> condition(index) }
        }
}
