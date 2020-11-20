/**
 * Copyright 2016-2020 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.specification.nukleus.proxy.internal;

import static org.agrona.BitUtil.fromHex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.INET;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressProtocol.STREAM;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import javax.el.ELContext;
import javax.el.FunctionMapper;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Test;
import org.kaazing.k3po.lang.el.BytesMatcher;
import org.kaazing.k3po.lang.internal.el.ExpressionContext;
import org.reaktivity.specification.nukleus.proxy.internal.types.control.ProxyRouteExFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.stream.ProxyBeginExFW;

public class ProxyFunctionsTest
{
    @Test
    public void shouldResolveFunction() throws Exception
    {
        final ELContext ctx = new ExpressionContext();
        final FunctionMapper mapper = ctx.getFunctionMapper();
        final Method function = mapper.resolveFunction("proxy", "routeEx");

        assertNotNull(function);
        assertSame(ProxyFunctions.class, function.getDeclaringClass());
    }

    @Test
    public void shouldGenerateRouteExtension()
    {
        byte[] build = ProxyFunctions.routeEx()
                                     .build();
        DirectBuffer buffer = new UnsafeBuffer(build);
        ProxyRouteExFW routeEx = new ProxyRouteExFW().wrap(buffer, 0, buffer.capacity());
        assertNotNull(routeEx);
    }

    @Test
    public void shouldGenerateBeginExtension()
    {
        byte[] build = ProxyFunctions.beginEx()
                                     .typeId(0x01)
                                     .addressInet()
                                         .protocol("stream")
                                         .source(fromHex("c0a80001"))
                                         .destination(fromHex("c0a800fe"))
                                         .sourcePort(32768)
                                         .destinationPort(443)
                                         .build()
                                     .build();
        DirectBuffer buffer = new UnsafeBuffer(build);
        ProxyBeginExFW beginEx = new ProxyBeginExFW().wrap(buffer, 0, buffer.capacity());
        assertNotNull(beginEx);
        assertEquals(0x01, beginEx.typeId());
        assertEquals(INET, beginEx.address().kind());
        assertEquals(new UnsafeBuffer(fromHex("c0a80001")), beginEx.address().inet().source().value());
        assertEquals(new UnsafeBuffer(fromHex("c0a800fe")), beginEx.address().inet().destination().value());
        assertEquals(32768, beginEx.address().inet().sourcePort());
        assertEquals(443, beginEx.address().inet().destinationPort());
    }

    @Test
    public void shouldMatchBeginExtension() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .source(fromHex("c0a80001"))
                                                 .destination(fromHex("c0a800fe"))
                                                 .sourcePort(32768)
                                                 .destinationPort(443)
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchBeginExtensionProtocol() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .protocol("datagram")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchBeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .source(fromHex("c0a80002"))
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchBeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .destination(fromHex("c0a800fd"))
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchBeginExtensionSourcePort() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .sourcePort(32767)
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchBeginExtensionDestinationPort() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .destinationPort(444)
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldFailWhenBufferEmpty() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(0);

        assertNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldFailWhenDoNotSetTypeId() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldFailWhenTypeIdDoNotMatch() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x02)
            .address(a -> a.inet(i -> i.protocol(p -> p.set(STREAM))
                                       .source(new UnsafeBuffer(fromHex("c0a80001")), 0, 4)
                                       .destination(new UnsafeBuffer(fromHex("c0a800fe")), 0, 4)
                                       .sourcePort(32768)
                                       .destinationPort(443)))
            .build();

        matcher.match(byteBuf);
    }
}
