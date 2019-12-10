/*
 * Copyright 2017-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.test

import kotlinx.serialization.*
import kotlinx.serialization.internal.HexConverter
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals


inline fun <reified T : Any> assertStringForm(
    expected: String,
    original: T,
    serializer: KSerializer<T>,
    format: StringFormat = Json.plain,
    printResult: Boolean = false
) {
    val string = format.stringify(serializer, original)
    if (printResult) println("[Serialized form] $string")
    assertEquals(expected, string)
}

inline fun <reified T : Any> assertSerializedToBinaryAndRestored(
    original: T,
    serializer: KSerializer<T>,
    format: BinaryFormat,
    printResult: Boolean = false,
    hexResultToCheck: String? = null
) {
    val bytes = format.dump(serializer, original)
    val hexString = HexConverter.printHexBinary(bytes, lowerCase = true)
    if (printResult) {
        println("[Serialized form] $hexString")
    }
    if (hexResultToCheck != null) {
        assertEquals(
            hexResultToCheck.toLowerCase(),
            hexString,
            "Expected serialized binary to be equal in hex representation"
        )
    }
    val restored = format.load(serializer, bytes)
    if (printResult) println("[Restored form] $restored")
    assertEquals(original, restored)
}

inline fun <reified T : Any> assertStringFormAndRestored(
    expected: String,
    original: T,
    serializer: KSerializer<T>,
    format: StringFormat = Json.plain,
    printResult: Boolean = false
) {
    val string = format.stringify(serializer, original)
    if (printResult) println("[Serialized form] $string")
    assertEquals(expected, string)
    val restored = format.parse(serializer, string)
    if (printResult) println("[Restored form] $restored")
    assertEquals(original, restored)
}

inline fun <reified T : Any> StringFormat.assertStringFormAndRestored(
    expected: String,
    original: T,
    serializer: KSerializer<T>,
    printResult: Boolean = false
) {
    val string = this.stringify(serializer, original)
    if (printResult) println("[Serialized form] $string")
    assertEquals(expected, string)
    val restored = this.parse(serializer, string)
    if (printResult) println("[Restored form] $restored")
    assertEquals(original, restored)
}

infix fun <T> T.shouldBe(expected: T) = assertEquals(expected, this)
