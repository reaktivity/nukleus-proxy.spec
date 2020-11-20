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
import org.reaktivity.specification.nukleus.proxy.internal.types.ProxyAddressInetFW;
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
                addressRW.wrap(new UnsafeBuffer(new byte[13]), 0, 13);
                addressInetRW.wrap(addressRW.buffer(), 1, 13);
            }

            public ProxyAddressInetBuilder source(
                byte[] source)
            {
                addressInetRW.source(s -> s.set(source));
                return this;
            }

            public ProxyAddressInetBuilder destination(
                byte[] destination)
            {
                addressInetRW.destination(s -> s.set(destination));
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
            return typeId == null || typeId == beginEx.typeId();
        }

        private boolean matchAddress(
            ProxyBeginExFW beginEx)
        {
            return address == null || address.test(beginEx.address());
        }

        public final class ProxyAddressInetMatcherBuilder
        {
            private OctetsFW source;
            private OctetsFW destination;
            private Integer sourcePort;
            private Integer destinationPort;

            private ProxyAddressInetMatcherBuilder()
            {
            }

            public ProxyAddressInetMatcherBuilder source(
                byte[] source)
            {
                this.source = new OctetsFW().wrap(new UnsafeBuffer(source), 0, source.length);
                return this;
            }

            public ProxyAddressInetMatcherBuilder destination(
                byte[] destination)
            {
                this.destination = new OctetsFW().wrap(new UnsafeBuffer(destination), 0, destination.length);
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
                return matchSource(inet) &&
                    matchDestination(inet) &&
                    matchSourcePort(inet) &&
                    matchDestinationPort(inet);
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
