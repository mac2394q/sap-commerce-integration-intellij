/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.inspections.util

/**
 * @author Nosov Aleksandr <nosovae.dev@gmail.com>
 */
// TODO THIS FILE JUST WORKAROUND FOR BUG KT-25125 - WHEN BUG WILL BE CLOSED - DELETE THIS

fun <T, K> Grouping<T, K>.myEachCount(): Map<K, Int> =
// fold(0) { acc, e -> acc + 1 } optimized for boxing
        myFoldTo(destination = mutableMapOf(),
                initialValueSelector = { _, _ -> kotlin.jvm.internal.Ref.IntRef() },
                operation = { _, acc, _ -> acc.apply { element += 1 } })
                .mapValuesInPlace { it.value.element }


@Suppress("UNCHECKED_CAST") // tricks with erased generics go here, do not repeat on reified platforms
internal inline fun <K, V, R> MutableMap<K, V>.mapValuesInPlace(f: (Map.Entry<K, V>) -> R): MutableMap<K, R> {
    entries.forEach {
        (it as MutableMap.MutableEntry<K, R>).setValue(f(it))
    }
    return (this as MutableMap<K, R>)
}

@SinceKotlin("1.1")
inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.myFoldTo(
        destination: M,
        initialValueSelector: (key: K, element: T) -> R,
        operation: (key: K, accumulator: R, element: T) -> R
): M =
        @Suppress("UNCHECKED_CAST")
        myAggregateTo(destination) { key, acc, e, first -> operation(key, if (first) initialValueSelector(key, e) else acc as R, e) }

public inline fun <T, K, R, M : MutableMap<in K, R>> Grouping<T, K>.myAggregateTo(
        destination: M,
        operation: (key: K, accumulator: R?, element: T, first: Boolean) -> R
): M {
    for (e in this.sourceIterator()) {
        val key = keyOf(e)
        val accumulator = destination[key]
        destination[key] = operation(key, accumulator, e, accumulator == null && !destination.containsKey(key))
    }
    return destination
}

