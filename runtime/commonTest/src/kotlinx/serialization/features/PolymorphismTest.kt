/*
 * Copyright 2017-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.serialization.features

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.*

class PolymorphismTest {

    @Serializable
    data class Wrapper(
        @SerialId(1) @Polymorphic val polyBase1: PolyBase,
        @SerialId(2) @Polymorphic val polyBase2: PolyBase
    )

    private val module: SerialModule = BaseAndDerivedModule + SerializersModule {
        polymorphic(
            PolyDerived::class,
            PolyDerived.serializer()
        )
    }

    private val json = Json { unquoted = true; useArrayPolymorphism = true; serialModule = module }
    private val protobuf = ProtoBuf(context = module)

    @Test
    fun testInheritanceJson() {
        val obj = Wrapper(
            PolyBase(2),
            PolyDerived("b")
        )
        val bytes = json.stringify(Wrapper.serializer(), obj)
        assertEquals(
            "{polyBase1:[kotlinx.serialization.PolyBase,{id:2}]," +
                    "polyBase2:[kotlinx.serialization.PolyDerived,{id:1,s:b}]}", bytes
        )
    }

    @Test
    fun testInheritanceProtobuf() {
        val obj = Wrapper(
            PolyBase(2),
            PolyDerived("b")
        )
        val bytes = protobuf.dumps(Wrapper.serializer(), obj)
        val restored = protobuf.loads(Wrapper.serializer(), bytes)
        assertEquals(obj, restored)
    }

    @Test
    fun testExplicit() {
        val obj = PolyDerived("b")
        val s = json.stringify(PolymorphicSerializer(PolyDerived::class), obj)
        assertEquals("[kotlinx.serialization.PolyDerived,{id:1,s:b}]", s)
    }

    @Test
    fun testElementDescriptors() {
        val serializer = PolymorphicSerializer(PolyDerived::class)
        assertFailsWith(SerializationException::class) {
            serializer.descriptor.getElementDescriptor(1)
        }
    }
}
