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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.INET4;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.INET6;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.UNIX;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoType.ALPN;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoType.AUTHORITY;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoType.IDENTITY;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoType.NAMESPACE;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoType.SECURE;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoType.CIPHER;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoType.KEY;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoType.NAME;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoType.PROTOCOL;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoType.SIGNATURE;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.collections.MutableBoolean;
import org.agrona.concurrent.UnsafeBuffer;
import org.kaazing.k3po.lang.el.BytesMatcher;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
import org.reaktivity.specification.nukleus.proxy.internal.types.Array32FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.OctetsFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressInet4FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressInet6FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressMatchFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressMatchInet4FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressMatchInet6FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressMatchUnixFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressProtocol;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressUnixFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyInfoType;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxySecureInfoType;
import org.reaktivity.specification.nukleus.proxy.internal.types.String16FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.String8FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.control.ProxyRouteExFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.stream.ProxyBeginExFW;

public final class ProxyFunctions
{
    @Function
    public static ProxyRouteExBuilder routeEx()
    {
        return new ProxyRouteExBuilder();
    }

    @Function
    public static ProxyBeginExBuilder beginEx()
    {
        return new ProxyBeginExBuilder();
    }

    @Function
    public static ProxyBeginExMatcherBuilder matchBeginEx()
    {
        return new ProxyBeginExMatcherBuilder();
    }

    public static final class ProxyRouteExBuilder
    {
        private final ProxyRouteExFW.Builder routeExRW;

        private ProxyRouteExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[1024 * 8]);
            this.routeExRW = new ProxyRouteExFW.Builder().wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public ProxyAddressMatchInet4Builder addressInet4()
        {
            return new ProxyAddressMatchInet4Builder();
        }

        public ProxyAddressMatchInet6Builder addressInet6()
        {
            return new ProxyAddressMatchInet6Builder();
        }

        public ProxyAddressMatchUnixBuilder addressUnix()
        {
            return new ProxyAddressMatchUnixBuilder();
        }

        public ProxyInfoMatchBuilder info()
        {
            return new ProxyInfoMatchBuilder();
        }

        public byte[] build()
        {
            final ProxyRouteExFW routeEx = routeExRW.build();
            final byte[] array = new byte[routeEx.sizeof()];
            routeEx.buffer().getBytes(routeEx.offset(), array);
            return array;
        }

        public final class ProxyAddressMatchInet4Builder
        {
            private final ProxyAddressMatchFW.Builder addressRW = new ProxyAddressMatchFW.Builder();

            private final ProxyAddressMatchInet4FW.Builder addressInet4RW = new ProxyAddressMatchInet4FW.Builder();

            private ProxyAddressMatchInet4Builder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[20]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressInet4RW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressMatchInet4Builder protocol(
                String protocol)
            {
                addressInet4RW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressMatchInet4Builder source(
                String source) throws UnknownHostException
            {
                return source(source, 4);
            }

            public ProxyAddressMatchInet4Builder source(
                String prefix,
                int length) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(prefix);
                final byte[] ip = inet.getAddress();
                addressInet4RW.source(s -> s.prefix(p -> p.set(ip)).length(length));
                return this;
            }

            public ProxyAddressMatchInet4Builder destination(
                String destination) throws UnknownHostException
            {
                return destination(destination, 4);
            }

            public ProxyAddressMatchInet4Builder destination(
                String prefix,
                int length) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(prefix);
                final byte[] ip = inet.getAddress();
                addressInet4RW.destination(s -> s.prefix(p -> p.set(ip)).length(length));
                return this;
            }

            public ProxyAddressMatchInet4Builder sourcePort(
                int sourcePort)
            {
                return sourcePort(sourcePort, sourcePort);
            }

            public ProxyAddressMatchInet4Builder sourcePort(
                int low,
                int high)
            {
                addressInet4RW.sourcePort(r -> r.low(low).high(high));
                return this;
            }

            public ProxyAddressMatchInet4Builder destinationPort(
                int destinationPort)
            {
                return destinationPort(destinationPort, destinationPort);
            }

            public ProxyAddressMatchInet4Builder destinationPort(
                int low,
                int high)
            {
                addressInet4RW.destinationPort(r -> r.low(low).high(high));
                return this;
            }

            public ProxyRouteExBuilder build()
            {
                routeExRW.address(addressRW.inet4(addressInet4RW.build()).build());
                return ProxyRouteExBuilder.this;
            }
        }

        public final class ProxyAddressMatchInet6Builder
        {
            private final ProxyAddressMatchFW.Builder addressRW = new ProxyAddressMatchFW.Builder();

            private final ProxyAddressMatchInet6FW.Builder addressInet6RW = new ProxyAddressMatchInet6FW.Builder();

            private ProxyAddressMatchInet6Builder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[44]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressInet6RW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressMatchInet6Builder protocol(
                String protocol)
            {
                addressInet6RW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressMatchInet6Builder source(
                String source) throws UnknownHostException
            {
                return source(source, 16);
            }

            public ProxyAddressMatchInet6Builder source(
                String prefix,
                int length) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(prefix);
                final byte[] ip = inet.getAddress();
                addressInet6RW.source(s -> s.prefix(p -> p.set(ip)).length(length));
                return this;
            }

            public ProxyAddressMatchInet6Builder destination(
                String destination) throws UnknownHostException
            {
                return destination(destination, 16);
            }

            public ProxyAddressMatchInet6Builder destination(
                String prefix,
                int length) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(prefix);
                final byte[] ip = inet.getAddress();
                addressInet6RW.destination(s -> s.prefix(p -> p.set(ip)).length(length));
                return this;
            }

            public ProxyAddressMatchInet6Builder sourcePort(
                int sourcePort)
            {
                return sourcePort(sourcePort, sourcePort);
            }

            public ProxyAddressMatchInet6Builder sourcePort(
                int low,
                int high)
            {
                addressInet6RW.sourcePort(r -> r.low(low).high(high));
                return this;
            }

            public ProxyAddressMatchInet6Builder destinationPort(
                int destinationPort)
            {
                return destinationPort(destinationPort, destinationPort);
            }

            public ProxyAddressMatchInet6Builder destinationPort(
                int low,
                int high)
            {
                addressInet6RW.destinationPort(r -> r.low(low).high(high));
                return this;
            }

            public ProxyRouteExBuilder build()
            {
                routeExRW.address(addressRW.inet6(addressInet6RW.build()).build());
                return ProxyRouteExBuilder.this;
            }
        }

        public final class ProxyAddressMatchUnixBuilder
        {
            private final ProxyAddressMatchFW.Builder addressRW = new ProxyAddressMatchFW.Builder();

            private final ProxyAddressMatchUnixFW.Builder addressUnixRW = new ProxyAddressMatchUnixFW.Builder();

            private ProxyAddressMatchUnixBuilder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[224]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressUnixRW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressMatchUnixBuilder protocol(
                String protocol)
            {
                addressUnixRW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressMatchUnixBuilder source(
                String source) throws UnknownHostException
            {
                final byte[] prefix = source.getBytes(UTF_8);
                addressUnixRW.source(s -> s.prefix(p -> p.set(prefix)));
                return this;
            }

            public ProxyAddressMatchUnixBuilder destination(
                String destination) throws UnknownHostException
            {
                final byte[] prefix = destination.getBytes(UTF_8);
                addressUnixRW.destination(s -> s.prefix(p -> p.set(prefix)));
                return this;
            }

            public ProxyRouteExBuilder build()
            {
                routeExRW.address(addressRW.unix(addressUnixRW.build()).build());
                return ProxyRouteExBuilder.this;
            }
        }

        public final class ProxyInfoMatchBuilder
        {
            private final Array32FW.Builder<ProxyInfoFW.Builder, ProxyInfoFW> infosRW =
                    new Array32FW.Builder<>(new ProxyInfoFW.Builder(), new ProxyInfoFW());

            private ProxyInfoMatchBuilder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[1024]);
                infosRW.wrap(buffer, 0, buffer.capacity());
            }

            public ProxyInfoMatchBuilder alpn(
                String alpn)
            {
                infosRW.item(i -> i.alpn(alpn));
                return this;
            }

            public ProxyInfoMatchBuilder authority(
                String authority)
            {
                infosRW.item(i -> i.authority(authority));
                return this;
            }

            public ProxyInfoMatchBuilder identity(
                byte[] identity)
            {
                infosRW.item(i -> i.identity(id -> id.value(v -> v.set(identity))));
                return this;
            }

            public ProxyInfoMatchBuilder namespace(
                String namespace)
            {
                infosRW.item(i -> i.namespace(namespace));
                return this;
            }

            public ProxySecureInfoMatchBuilder secure()
            {
                return new ProxySecureInfoMatchBuilder();
            }

            public ProxyRouteExBuilder build()
            {
                routeExRW.infos(infosRW.build());
                return ProxyRouteExBuilder.this;
            }

            public final class ProxySecureInfoMatchBuilder
            {
                private ProxySecureInfoMatchBuilder()
                {
                }

                public ProxySecureInfoMatchBuilder protocol(
                    String protocol)
                {
                    infosRW.item(i -> i.secure(s -> s.protocol(protocol)));
                    return this;
                }

                public ProxySecureInfoMatchBuilder cipher(
                    String cipher)
                {
                    infosRW.item(i -> i.secure(s -> s.cipher(cipher)));
                    return this;
                }

                public ProxySecureInfoMatchBuilder signature(
                    String signature)
                {
                    infosRW.item(i -> i.secure(s -> s.signature(signature)));
                    return this;
                }

                public ProxySecureInfoMatchBuilder name(
                    String name)
                {
                    infosRW.item(i -> i.secure(s -> s.name(name)));
                    return this;
                }

                public ProxySecureInfoMatchBuilder key(
                    String key)
                {
                    infosRW.item(i -> i.secure(s -> s.key(key)));
                    return this;
                }

                public ProxyInfoMatchBuilder build()
                {
                    return ProxyInfoMatchBuilder.this;
                }
            }
        }
    }

    public static final class ProxyBeginExBuilder
    {
        private final ProxyBeginExFW.Builder beginExRW;

        private ProxyBeginExBuilder()
        {
            MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[1024 * 8]);
            this.beginExRW = new ProxyBeginExFW.Builder().wrap(writeBuffer, 0, writeBuffer.capacity());
        }

        public ProxyBeginExBuilder typeId(
            int typeId)
        {
            beginExRW.typeId(typeId);
            return this;
        }

        public ProxyAddressInet4Builder addressInet4()
        {
            return new ProxyAddressInet4Builder();
        }

        public ProxyAddressInet6Builder addressInet6()
        {
            return new ProxyAddressInet6Builder();
        }

        public ProxyAddressUnixBuilder addressUnix()
        {
            return new ProxyAddressUnixBuilder();
        }

        public ProxyInfoBuilder info()
        {
            return new ProxyInfoBuilder();
        }

        public byte[] build()
        {
            final ProxyBeginExFW beginEx = beginExRW.build();
            final byte[] array = new byte[beginEx.sizeof()];
            beginEx.buffer().getBytes(beginEx.offset(), array);
            return array;
        }

        public final class ProxyAddressInet4Builder
        {
            private final ProxyAddressFW.Builder addressRW = new ProxyAddressFW.Builder();

            private final ProxyAddressInet4FW.Builder addressInet4RW = new ProxyAddressInet4FW.Builder();

            private ProxyAddressInet4Builder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[14]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressInet4RW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressInet4Builder protocol(
                String protocol)
            {
                addressInet4RW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressInet4Builder source(
                String source) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(source);
                final byte[] ip = inet.getAddress();
                addressInet4RW.source(s -> s.set(ip));
                return this;
            }

            public ProxyAddressInet4Builder destination(
                String destination) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(destination);
                final byte[] ip = inet.getAddress();
                addressInet4RW.destination(s -> s.set(ip));
                return this;
            }

            public ProxyAddressInet4Builder sourcePort(
                int sourcePort)
            {
                addressInet4RW.sourcePort(sourcePort);
                return this;
            }

            public ProxyAddressInet4Builder destinationPort(
                int destinationPort)
            {
                addressInet4RW.destinationPort(destinationPort);
                return this;
            }

            public ProxyBeginExBuilder build()
            {
                beginExRW.address(addressRW.inet4(addressInet4RW.build()).build());
                return ProxyBeginExBuilder.this;
            }
        }

        public final class ProxyAddressInet6Builder
        {
            private final ProxyAddressFW.Builder addressRW = new ProxyAddressFW.Builder();

            private final ProxyAddressInet6FW.Builder addressInet6RW = new ProxyAddressInet6FW.Builder();

            private ProxyAddressInet6Builder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[38]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressInet6RW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressInet6Builder protocol(
                String protocol)
            {
                addressInet6RW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressInet6Builder source(
                String source) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(source);
                final byte[] ip = inet.getAddress();
                addressInet6RW.source(s -> s.set(ip));
                return this;
            }

            public ProxyAddressInet6Builder destination(
                String destination) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(destination);
                final byte[] ip = inet.getAddress();
                addressInet6RW.destination(s -> s.set(ip));
                return this;
            }

            public ProxyAddressInet6Builder sourcePort(
                int sourcePort)
            {
                addressInet6RW.sourcePort(sourcePort);
                return this;
            }

            public ProxyAddressInet6Builder destinationPort(
                int destinationPort)
            {
                addressInet6RW.destinationPort(destinationPort);
                return this;
            }

            public ProxyBeginExBuilder build()
            {
                beginExRW.address(addressRW.inet6(addressInet6RW.build()).build());
                return ProxyBeginExBuilder.this;
            }
        }

        public final class ProxyAddressUnixBuilder
        {
            private final ProxyAddressFW.Builder addressRW = new ProxyAddressFW.Builder();

            private final ProxyAddressUnixFW.Builder addressUnixRW = new ProxyAddressUnixFW.Builder();

            private ProxyAddressUnixBuilder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[218]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressUnixRW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressUnixBuilder protocol(
                String protocol)
            {
                addressUnixRW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressUnixBuilder source(
                String source) throws UnknownHostException
            {
                MutableDirectBuffer sourceBuf = new UnsafeBuffer(new byte[108]);
                sourceBuf.putStringWithoutLengthUtf8(0, source);
                addressUnixRW.source(sourceBuf, 0, sourceBuf.capacity());
                return this;
            }

            public ProxyAddressUnixBuilder destination(
                String destination) throws UnknownHostException
            {
                MutableDirectBuffer destinationBuf = new UnsafeBuffer(new byte[108]);
                destinationBuf.putStringWithoutLengthUtf8(0, destination);
                addressUnixRW.destination(destinationBuf, 0, destinationBuf.capacity());
                return this;
            }

            public ProxyBeginExBuilder build()
            {
                beginExRW.address(addressRW.unix(addressUnixRW.build()).build());
                return ProxyBeginExBuilder.this;
            }
        }

        public final class ProxyInfoBuilder
        {
            private final Array32FW.Builder<ProxyInfoFW.Builder, ProxyInfoFW> infosRW =
                    new Array32FW.Builder<>(new ProxyInfoFW.Builder(), new ProxyInfoFW());

            private ProxyInfoBuilder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[1024]);
                infosRW.wrap(buffer, 0, buffer.capacity());
            }

            public ProxyInfoBuilder alpn(
                String alpn)
            {
                infosRW.item(i -> i.alpn(alpn));
                return this;
            }

            public ProxyInfoBuilder authority(
                String authority)
            {
                infosRW.item(i -> i.authority(authority));
                return this;
            }

            public ProxyInfoBuilder identity(
                byte[] identity)
            {
                infosRW.item(i -> i.identity(id -> id.value(v -> v.set(identity))));
                return this;
            }

            public ProxyInfoBuilder namespace(
                String namespace)
            {
                infosRW.item(i -> i.namespace(namespace));
                return this;
            }

            public ProxySecureInfoBuilder secure()
            {
                return new ProxySecureInfoBuilder();
            }

            public ProxyBeginExBuilder build()
            {
                beginExRW.infos(infosRW.build());
                return ProxyBeginExBuilder.this;
            }

            public final class ProxySecureInfoBuilder
            {
                private ProxySecureInfoBuilder()
                {
                }

                public ProxySecureInfoBuilder protocol(
                    String protocol)
                {
                    infosRW.item(i -> i.secure(s -> s.protocol(protocol)));
                    return this;
                }

                public ProxySecureInfoBuilder cipher(
                    String cipher)
                {
                    infosRW.item(i -> i.secure(s -> s.cipher(cipher)));
                    return this;
                }

                public ProxySecureInfoBuilder signature(
                    String signature)
                {
                    infosRW.item(i -> i.secure(s -> s.signature(signature)));
                    return this;
                }

                public ProxySecureInfoBuilder name(
                    String name)
                {
                    infosRW.item(i -> i.secure(s -> s.name(name)));
                    return this;
                }

                public ProxySecureInfoBuilder key(
                    String key)
                {
                    infosRW.item(i -> i.secure(s -> s.key(key)));
                    return this;
                }

                public ProxyInfoBuilder build()
                {
                    return ProxyInfoBuilder.this;
                }
            }
        }
    }

    public static final class ProxyBeginExMatcherBuilder
    {
        private final DirectBuffer bufferRO = new UnsafeBuffer();

        private final ProxyBeginExFW beginExRO = new ProxyBeginExFW();

        private Integer typeId;
        private Predicate<ProxyAddressFW> address;
        private Predicate<Array32FW<ProxyInfoFW>> infos;

        public ProxyBeginExMatcherBuilder typeId(
            int typeId)
        {
            this.typeId = typeId;
            return this;
        }

        public ProxyAddressInetMatcherBuilder addressInet4()
        {
            final ProxyAddressInetMatcherBuilder matcher = new ProxyAddressInetMatcherBuilder();

            this.address = matcher::match;
            return matcher;
        }

        public ProxyAddressInet6MatcherBuilder addressInet6()
        {
            final ProxyAddressInet6MatcherBuilder matcher = new ProxyAddressInet6MatcherBuilder();

            this.address = matcher::match;
            return matcher;
        }

        public ProxyAddressUnixMatcherBuilder addressUnix()
        {
            final ProxyAddressUnixMatcherBuilder matcher = new ProxyAddressUnixMatcherBuilder();

            this.address = matcher::match;
            return matcher;
        }

        public ProxyInfoMatcherBuilder info()
        {
            final ProxyInfoMatcherBuilder matcher = new ProxyInfoMatcherBuilder();

            this.infos = matcher::match;
            return matcher;
        }

        public BytesMatcher build()
        {
            return typeId != null ? this::match : buf -> null;
        }

        private ProxyBeginExFW match(
            ByteBuffer byteBuf) throws Exception
        {
            if (!byteBuf.hasRemaining())
            {
                return null;
            }

            bufferRO.wrap(byteBuf);
            final ProxyBeginExFW beginEx = beginExRO.tryWrap(bufferRO, byteBuf.position(), byteBuf.capacity());

            if (beginEx != null &&
                matchTypeId(beginEx) &&
                matchAddress(beginEx) &&
                matchInfos(beginEx))
            {
                byteBuf.position(byteBuf.position() + beginEx.sizeof());
                return beginEx;
            }

            throw new Exception(beginEx.toString());
        }

        private boolean matchTypeId(
            ProxyBeginExFW beginEx)
        {
            return typeId == beginEx.typeId();
        }

        private boolean matchAddress(
            ProxyBeginExFW beginEx)
        {
            return address == null || address.test(beginEx.address());
        }

        private boolean matchInfos(
            ProxyBeginExFW beginEx)
        {
            return infos == null || infos.test(beginEx.infos());
        }

        public final class ProxyAddressInetMatcherBuilder
        {
            private ProxyAddressProtocol protocol;
            private OctetsFW source;
            private OctetsFW destination;
            private Integer sourcePort;
            private Integer destinationPort;

            private ProxyAddressInetMatcherBuilder()
            {
            }

            public ProxyAddressInetMatcherBuilder protocol(
                String protocol)
            {
                this.protocol = ProxyAddressProtocol.valueOf(protocol.toUpperCase());
                return this;
            }

            public ProxyAddressInetMatcherBuilder source(
                String source) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(source);
                final byte[] ip = inet.getAddress();
                this.source = new OctetsFW().wrap(new UnsafeBuffer(ip), 0, ip.length);
                return this;
            }

            public ProxyAddressInetMatcherBuilder destination(
                String destination) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(destination);
                final byte[] ip = inet.getAddress();
                this.destination = new OctetsFW().wrap(new UnsafeBuffer(ip), 0, ip.length);
                return this;
            }

            public ProxyAddressInetMatcherBuilder sourcePort(
                int sourcePort)
            {
                this.sourcePort = sourcePort;
                return this;
            }

            public ProxyAddressInetMatcherBuilder destinationPort(
                int destinationPort)
            {
                this.destinationPort = destinationPort;
                return this;
            }

            public ProxyBeginExMatcherBuilder build()
            {
                return ProxyBeginExMatcherBuilder.this;
            }

            private boolean match(
                ProxyAddressFW address)
            {
                return address.kind() == INET4 && match(address.inet4());
            }

            private boolean match(
                ProxyAddressInet4FW inet4)
            {
                return matchProtocol(inet4) &&
                    matchSource(inet4) &&
                    matchDestination(inet4) &&
                    matchSourcePort(inet4) &&
                    matchDestinationPort(inet4);
            }

            private boolean matchProtocol(
                final ProxyAddressInet4FW inet4)
            {
                return protocol == null || protocol == inet4.protocol().get();
            }

            private boolean matchSource(
                final ProxyAddressInet4FW inet4)
            {
                return source == null || source.equals(inet4.source());
            }

            private boolean matchDestination(
                final ProxyAddressInet4FW inet4)
            {
                return destination == null || destination.equals(inet4.destination());
            }

            private boolean matchSourcePort(
                final ProxyAddressInet4FW inet4)
            {
                return sourcePort == null || sourcePort == inet4.sourcePort();
            }

            private boolean matchDestinationPort(
                final ProxyAddressInet4FW inet4)
            {
                return destinationPort == null || destinationPort == inet4.destinationPort();
            }
        }

        public final class ProxyAddressInet6MatcherBuilder
        {
            private ProxyAddressProtocol protocol;
            private OctetsFW source;
            private OctetsFW destination;
            private Integer sourcePort;
            private Integer destinationPort;

            private ProxyAddressInet6MatcherBuilder()
            {
            }

            public ProxyAddressInet6MatcherBuilder protocol(
                String protocol)
            {
                this.protocol = ProxyAddressProtocol.valueOf(protocol.toUpperCase());
                return this;
            }

            public ProxyAddressInet6MatcherBuilder source(
                String source) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(source);
                final byte[] ip = inet.getAddress();
                this.source = new OctetsFW().wrap(new UnsafeBuffer(ip), 0, ip.length);
                return this;
            }

            public ProxyAddressInet6MatcherBuilder destination(
                String destination) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(destination);
                final byte[] ip = inet.getAddress();
                this.destination = new OctetsFW().wrap(new UnsafeBuffer(ip), 0, ip.length);
                return this;
            }

            public ProxyAddressInet6MatcherBuilder sourcePort(
                int sourcePort)
            {
                this.sourcePort = sourcePort;
                return this;
            }

            public ProxyAddressInet6MatcherBuilder destinationPort(
                int destinationPort)
            {
                this.destinationPort = destinationPort;
                return this;
            }

            public ProxyBeginExMatcherBuilder build()
            {
                return ProxyBeginExMatcherBuilder.this;
            }

            private boolean match(
                ProxyAddressFW address)
            {
                return address.kind() == INET6 && match(address.inet6());
            }

            private boolean match(
                ProxyAddressInet6FW inet6)
            {
                return matchProtocol(inet6) &&
                    matchSource(inet6) &&
                    matchDestination(inet6) &&
                    matchSourcePort(inet6) &&
                    matchDestinationPort(inet6);
            }

            private boolean matchProtocol(
                final ProxyAddressInet6FW inet6)
            {
                return protocol == null || protocol == inet6.protocol().get();
            }

            private boolean matchSource(
                final ProxyAddressInet6FW inet6)
            {
                return source == null || source.equals(inet6.source());
            }

            private boolean matchDestination(
                final ProxyAddressInet6FW inet6)
            {
                return destination == null || destination.equals(inet6.destination());
            }

            private boolean matchSourcePort(
                final ProxyAddressInet6FW inet6)
            {
                return sourcePort == null || sourcePort == inet6.sourcePort();
            }

            private boolean matchDestinationPort(
                final ProxyAddressInet6FW inet6)
            {
                return destinationPort == null || destinationPort == inet6.destinationPort();
            }
        }

        public final class ProxyAddressUnixMatcherBuilder
        {
            private ProxyAddressProtocol protocol;
            private DirectBuffer source;
            private DirectBuffer destination;

            private ProxyAddressUnixMatcherBuilder()
            {
            }

            public ProxyAddressUnixMatcherBuilder protocol(
                String protocol)
            {
                this.protocol = ProxyAddressProtocol.valueOf(protocol.toUpperCase());
                return this;
            }

            public ProxyAddressUnixMatcherBuilder source(
                String source)
            {
                final MutableDirectBuffer sourceBuf = new UnsafeBuffer(new byte[108]);
                sourceBuf.putStringWithoutLengthUtf8(0, source);
                this.source = sourceBuf;
                return this;
            }

            public ProxyAddressUnixMatcherBuilder destination(
                String destination)
            {
                final MutableDirectBuffer destinationBuf = new UnsafeBuffer(new byte[108]);
                destinationBuf.putStringWithoutLengthUtf8(0, destination);
                this.destination = destinationBuf;
                return this;
            }

            public ProxyBeginExMatcherBuilder build()
            {
                return ProxyBeginExMatcherBuilder.this;
            }

            private boolean match(
                ProxyAddressFW address)
            {
                return address.kind() == UNIX && match(address.unix());
            }

            private boolean match(
                ProxyAddressUnixFW unix)
            {
                return matchProtocol(unix) &&
                    matchSource(unix) &&
                    matchDestination(unix);
            }

            private boolean matchProtocol(
                final ProxyAddressUnixFW unix)
            {
                return protocol == null || protocol == unix.protocol().get();
            }

            private boolean matchSource(
                final ProxyAddressUnixFW unix)
            {
                return source == null || source.equals(unix.source().value());
            }

            private boolean matchDestination(
                final ProxyAddressUnixFW unix)
            {
                return destination == null || destination.equals(unix.destination().value());
            }
        }

        public final class ProxyInfoMatcherBuilder
        {
            private final Map<ProxyInfoType, Predicate<ProxyInfoFW>> matchers;

            private ProxyInfoMatcherBuilder()
            {
                matchers = new EnumMap<>(ProxyInfoType.class);
            }

            public ProxyInfoMatcherBuilder alpn(
                String alpn)
            {
                final String8FW alpn8 = new String8FW(alpn);
                matchers.put(ALPN, info -> alpn8.equals(info.alpn()));
                return this;
            }

            public ProxyInfoMatcherBuilder authority(
                String authority)
            {
                final String16FW authority16 = new String16FW(authority);
                matchers.put(AUTHORITY, info -> authority16.equals(info.authority()));
                return this;
            }

            public ProxyInfoMatcherBuilder identity(
                byte[] identity)
            {
                final DirectBuffer identityBuf = new UnsafeBuffer(identity);
                matchers.put(IDENTITY, info -> identityBuf.equals(info.identity().value().value()));
                return this;
            }

            public ProxyInfoMatcherBuilder namespace(
                String namespace)
            {
                final String16FW namespace16 = new String16FW(namespace);
                matchers.put(NAMESPACE, info -> namespace16.equals(info.namespace()));
                return this;
            }

            public ProxySecureInfoMatcherBuilder secure()
            {
                final ProxySecureInfoMatcherBuilder matcher = new ProxySecureInfoMatcherBuilder();
                matchers.put(SECURE, info -> matcher.match(info.secure()));
                return matcher;
            }

            public ProxyBeginExMatcherBuilder build()
            {
                return ProxyBeginExMatcherBuilder.this;
            }

            private boolean match(
                Array32FW<ProxyInfoFW> infos)
            {
                MutableBoolean match = new MutableBoolean(true);
                infos.forEach(info -> match.value &= match(info));
                return match.value;
            }

            private boolean match(
                ProxyInfoFW info)
            {
                final Predicate<ProxyInfoFW> matcher = matchers.get(info.kind());
                return matcher == null || matcher.test(info);
            }

            public final class ProxySecureInfoMatcherBuilder
            {
                private final Map<ProxySecureInfoType, Predicate<ProxySecureInfoFW>> matchers;

                private ProxySecureInfoMatcherBuilder()
                {
                    matchers = new EnumMap<>(ProxySecureInfoType.class);
                }

                public ProxySecureInfoMatcherBuilder protocol(
                    String protocol)
                {
                    final String8FW protocol8 = new String8FW(protocol);
                    matchers.put(PROTOCOL, info -> protocol8.equals(info.protocol()));
                    return this;
                }

                public ProxySecureInfoMatcherBuilder cipher(
                    String cipher)
                {
                    final String8FW cipher8 = new String8FW(cipher);
                    matchers.put(CIPHER, info -> cipher8.equals(info.cipher()));
                    return this;
                }

                public ProxySecureInfoMatcherBuilder signature(
                    String signature)
                {
                    final String8FW signature8 = new String8FW(signature);
                    matchers.put(SIGNATURE, info -> signature8.equals(info.signature()));
                    return this;
                }

                public ProxySecureInfoMatcherBuilder name(
                    String name)
                {
                    final String16FW name16 = new String16FW(name);
                    matchers.put(NAME, info -> name16.equals(info.name()));
                    return this;
                }

                public ProxySecureInfoMatcherBuilder key(
                    String key)
                {
                    final String8FW key8 = new String8FW(key);
                    matchers.put(KEY, info -> key8.equals(info.key()));
                    return this;
                }

                public ProxyInfoMatcherBuilder build()
                {
                    return ProxyInfoMatcherBuilder.this;
                }

                private boolean match(
                    ProxySecureInfoFW secureInfo)
                {
                    final Predicate<ProxySecureInfoFW> matcher = matchers.get(secureInfo.kind());
                    return matcher == null || matcher.test(secureInfo);
                }
            }
        }
    }

    public static class Mapper extends FunctionMapperSpi.Reflective
    {
        public Mapper()
        {
            super(ProxyFunctions.class);
        }

        @Override
        public String getPrefixName()
        {
            return "proxy";
        }
    }

    private ProxyFunctions()
    {
        // utility
    }
}
