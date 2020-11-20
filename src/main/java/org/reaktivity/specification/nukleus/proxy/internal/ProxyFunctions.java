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

import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.INET;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.INET6;
import static org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFamily.UNIX;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.function.Predicate;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.kaazing.k3po.lang.el.BytesMatcher;
import org.kaazing.k3po.lang.el.Function;
import org.kaazing.k3po.lang.el.spi.FunctionMapperSpi;
import org.reaktivity.specification.nukleus.proxy.internal.types.OctetsFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressInet6FW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressInetFW;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressProtocol;
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressUnixFW;
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

        public byte[] build()
        {
            final ProxyRouteExFW routeEx = routeExRW.build();
            final byte[] array = new byte[routeEx.sizeof()];
            routeEx.buffer().getBytes(routeEx.offset(), array);
            return array;
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

        public ProxyAddressInetBuilder addressInet()
        {
            return new ProxyAddressInetBuilder();
        }

        public ProxyAddressInet6Builder addressInet6()
        {
            return new ProxyAddressInet6Builder();
        }

        public ProxyAddressUnixBuilder addressUnix()
        {
            return new ProxyAddressUnixBuilder();
        }

        public byte[] build()
        {
            final ProxyBeginExFW beginEx = beginExRW.build();
            final byte[] array = new byte[beginEx.sizeof()];
            beginEx.buffer().getBytes(beginEx.offset(), array);
            return array;
        }

        public final class ProxyAddressInetBuilder
        {
            private final ProxyAddressFW.Builder addressRW = new ProxyAddressFW.Builder();

            private final ProxyAddressInetFW.Builder addressInetRW = new ProxyAddressInetFW.Builder();

            private ProxyAddressInetBuilder()
            {
                final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[14]);
                addressRW.wrap(buffer, 0, buffer.capacity());
                addressInetRW.wrap(buffer, 1, buffer.capacity());
            }

            public ProxyAddressInetBuilder protocol(
                String protocol)
            {
                addressInetRW.protocol(p -> p.set(ProxyAddressProtocol.valueOf(protocol.toUpperCase())));
                return this;
            }

            public ProxyAddressInetBuilder source(
                String source) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(source);
                final byte[] ip = inet.getAddress();
                addressInetRW.source(s -> s.set(ip));
                return this;
            }

            public ProxyAddressInetBuilder destination(
                String destination) throws UnknownHostException
            {
                final InetAddress inet = InetAddress.getByName(destination);
                final byte[] ip = inet.getAddress();
                addressInetRW.destination(s -> s.set(ip));
                return this;
            }

            public ProxyAddressInetBuilder sourcePort(
                int sourcePort)
            {
                addressInetRW.sourcePort(sourcePort);
                return this;
            }

            public ProxyAddressInetBuilder destinationPort(
                int destinationPort)
            {
                addressInetRW.destinationPort(destinationPort);
                return this;
            }

            public ProxyBeginExBuilder build()
            {
                beginExRW.address(addressRW.inet(addressInetRW.build()).build());
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
    }

    public static final class ProxyBeginExMatcherBuilder
    {
        private final DirectBuffer bufferRO = new UnsafeBuffer();

        private final ProxyBeginExFW beginExRO = new ProxyBeginExFW();

        private Integer typeId;
        private Predicate<ProxyAddressFW> address;

        public ProxyBeginExMatcherBuilder typeId(
            int typeId)
        {
            this.typeId = typeId;
            return this;
        }

        public ProxyAddressInetMatcherBuilder addressInet()
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
                matchAddress(beginEx))
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
                return address.kind() == INET && match(address.inet());
            }

            private boolean match(
                ProxyAddressInetFW inet)
            {
                return matchProtocol(inet) &&
                    matchSource(inet) &&
                    matchDestination(inet) &&
                    matchSourcePort(inet) &&
                    matchDestinationPort(inet);
            }

            private boolean matchProtocol(
                final ProxyAddressInetFW inet)
            {
                return protocol == null || protocol == inet.protocol().get();
            }

            private boolean matchSource(
                final ProxyAddressInetFW inet)
            {
                return source == null || source.equals(inet.source());
            }

            private boolean matchDestination(
                final ProxyAddressInetFW inet)
            {
                return destination == null || destination.equals(inet.destination());
            }

            private boolean matchSourcePort(
                final ProxyAddressInetFW inet)
            {
                return sourcePort == null || sourcePort == inet.sourcePort();
            }

            private boolean matchDestinationPort(
                final ProxyAddressInetFW inet)
            {
                return destinationPort == null || destinationPort == inet.destinationPort();
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
