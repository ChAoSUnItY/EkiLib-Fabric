package com.chaos.eki_lib.utils

interface Cyclable<T> {
    fun next(): T

    fun previous(): T
}