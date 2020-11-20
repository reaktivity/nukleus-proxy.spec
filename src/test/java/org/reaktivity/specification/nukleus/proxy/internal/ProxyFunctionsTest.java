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
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.INET6;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.UNIX;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressProtocol.STREAM;

import java.lang.reflect.Method;
import java.net.UnknownHostException;
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
    public void shouldGenerateInetBeginExtension() throws UnknownHostException
    {
        byte[] build = ProxyFunctions.beginEx()
                                     .typeId(0x01)
                                     .addressInet()
                                         .protocol("stream")
                                         .source("192.168.0.1")
                                         .destination("192.168.0.254")
                                         .sourcePort(32768)
                                         .destinationPort(443)
                                         .build()
                                     .build();
        DirectBuffer buffer = new UnsafeBuffer(build);
        ProxyBeginExFW beginEx = new ProxyBeginExFW().wrap(buffer, 0, buffer.capacity());
        assertNotNull(beginEx);
        assertEquals(0x01, beginEx.typeId());
        assertEquals(INET, beginEx.address().kind());
        assertEquals(STREAM, beginEx.address().inet().protocol().get());
        assertEquals(new UnsafeBuffer(fromHex("c0a80001")), beginEx.address().inet().source().value());
        assertEquals(new UnsafeBuffer(fromHex("c0a800fe")), beginEx.address().inet().destination().value());
        assertEquals(32768, beginEx.address().inet().sourcePort());
        assertEquals(443, beginEx.address().inet().destinationPort());
    }

    @Test
    public void shouldMatchInetBeginExtension() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .source("192.168.0.1")
                                                 .destination("192.168.0.254")
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

    @Test
    public void shouldMatchInetBeginExtensionTypeId() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
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

    @Test
    public void shouldMatchInetBeginExtensionProtocol() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .protocol("stream")
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

    @Test
    public void shouldMatchInetBeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .source("192.168.0.1")
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

    @Test
    public void shouldMatchInetBeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .destination("192.168.0.254")
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
    public void shouldNotMatchInetBeginExtensionProtocol() throws Exception
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
    public void shouldNotMatchInetBeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .source("192.168.0.2")
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
    public void shouldNotMatchInetBeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet()
                                                 .destination("192.168.0.253")
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
    public void shouldNotMatchInetBeginExtensionSourcePort() throws Exception
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
    public void shouldNotMatchInetBeginExtensionDestinationPort() throws Exception
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
    public void shouldGenerateInet6BeginExtension() throws UnknownHostException
    {
        byte[] build = ProxyFunctions.beginEx()
                                     .typeId(0x01)
                                     .addressInet6()
                                         .protocol("stream")
                                         .source("fd12:3456:789a:1::1")
                                         .destination("fd12:3456:789a:1::fe")
                                         .sourcePort(32768)
                                         .destinationPort(443)
                                         .build()
                                     .build();
        DirectBuffer buffer = new UnsafeBuffer(build);
        ProxyBeginExFW beginEx = new ProxyBeginExFW().wrap(buffer, 0, buffer.capacity());
        assertNotNull(beginEx);
        assertEquals(0x01, beginEx.typeId());
        assertEquals(INET6, beginEx.address().kind());
        assertEquals(STREAM, beginEx.address().inet6().protocol().get());
        assertEquals(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")),
                beginEx.address().inet6().source().value());
        assertEquals(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")),
                beginEx.address().inet6().destination().value());
        assertEquals(32768, beginEx.address().inet6().sourcePort());
        assertEquals(443, beginEx.address().inet6().destinationPort());
    }

    @Test
    public void shouldMatchInet6BeginExtension() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .source("fd12:3456:789a:1::1")
                                                 .destination("fd12:3456:789a:1::fe")
                                                 .sourcePort(32768)
                                                 .destinationPort(443)
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchInet6BeginExtensionTypeId() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchInet6BeginExtensionProtocol() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .protocol("stream")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchInet6BeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .source("fd12:3456:789a:1::1")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchInet6BeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .destination("fd12:3456:789a:1::fe")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchInet6BeginExtensionProtocol() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .protocol("datagram")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchInet6BeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .source("fd12:3456:789a:1::2")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchInet6BeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .destination("fd12:3456:789a:1::fd")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchInet6BeginExtensionSourcePort() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .sourcePort(32767)
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchInet6BeginExtensionDestinationPort() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressInet6()
                                                 .destinationPort(444)
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.inet6(i -> i.protocol(p -> p.set(STREAM))
                                        .source(new UnsafeBuffer(fromHex("fd123456789a00010000000000000001")), 0, 16)
                                        .destination(new UnsafeBuffer(fromHex("fd123456789a000100000000000000fe")), 0, 16)
                                        .sourcePort(32768)
                                        .destinationPort(443)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldGenerateUnixBeginExtension() throws UnknownHostException
    {
        byte[] build = ProxyFunctions.beginEx()
                                     .typeId(0x01)
                                     .addressUnix()
                                         .protocol("stream")
                                         .source("source-1234")
                                         .destination("destination-5678")
                                         .build()
                                     .build();
        DirectBuffer buffer = new UnsafeBuffer(build);
        ProxyBeginExFW beginEx = new ProxyBeginExFW().wrap(buffer, 0, buffer.capacity());
        assertNotNull(beginEx);
        assertEquals(0x01, beginEx.typeId());
        assertEquals(UNIX, beginEx.address().kind());
        assertEquals(STREAM, beginEx.address().unix().protocol().get());
        assertEquals(paddedUtf8("source-1234", 108), beginEx.address().unix().source().value());
        assertEquals(paddedUtf8("destination-5678", 108), beginEx.address().unix().destination().value());
    }

    @Test
    public void shouldMatchUnixBeginExtension() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .source("source-1234")
                                                 .destination("destination-5678")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchUnixBeginExtensionTypeId() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchUnixBeginExtensionProtocol() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .protocol("stream")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchUnixBeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .source("source-1234")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test
    public void shouldMatchUnixBeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .destination("destination-5678")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNotNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchUnixBeginExtensionProtocol() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .protocol("datagram")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchUnixBeginExtensionSource() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .source("source-12345")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
            .build();

        assertNull(matcher.match(byteBuf));
    }

    @Test(expected = Exception.class)
    public void shouldNotMatchUnixBeginExtensionDestination() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .addressUnix()
                                                 .destination("destination-56789")
                                                 .build()
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1024);

        new ProxyBeginExFW.Builder().wrap(new UnsafeBuffer(byteBuf), 0, byteBuf.capacity())
            .typeId(0x01)
            .address(a -> a.unix(i -> i.protocol(p -> p.set(STREAM))
                                       .source(paddedUtf8("source-1234", 108), 0, 108)
                                       .destination(paddedUtf8("destination-5678", 108), 0, 108)))
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

    @Test(expected = Exception.class)
    public void shouldFailWhenBufferIncomplete() throws Exception
    {
        BytesMatcher matcher = ProxyFunctions.matchBeginEx()
                                             .typeId(0x01)
                                             .build();

        ByteBuffer byteBuf = ByteBuffer.allocate(1);

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

    private static DirectBuffer paddedUtf8(
        String utf8,
        int length)
    {
        UnsafeBuffer padded = new UnsafeBuffer(new byte[length]);
        padded.putStringWithoutLengthUtf8(0, utf8);
        return padded;
    }
}
