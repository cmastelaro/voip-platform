cat > ~/voip-platform/infra/asterisk/Dockerfile << 'EOF'
FROM debian:bookworm-slim

ARG ASTERISK_VERSION=22

RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends \
      build-essential pkg-config autoconf automake libtool libtool-bin \
      wget curl ca-certificates xmlstarlet bison flex \
      libjansson-dev libxml2-dev libsqlite3-dev uuid-dev libedit-dev \
      libssl-dev libncurses-dev libcurl4-openssl-dev \
      libsrtp2-dev libopus-dev libspeexdsp-dev libogg-dev libvorbis-dev \
      unixodbc unixodbc-dev odbc-postgresql \
      sngrep tcpdump iproute2 \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src

RUN wget -q https://downloads.asterisk.org/pub/telephony/asterisk/asterisk-${ASTERISK_VERSION}-current.tar.gz \
 && tar -xzf asterisk-${ASTERISK_VERSION}-current.tar.gz \
 && rm asterisk-${ASTERISK_VERSION}-current.tar.gz \
 && mv asterisk-${ASTERISK_VERSION}* asterisk-src

WORKDIR /usr/src/asterisk-src

RUN ./configure \
      --with-jansson-bundled \
      --with-pjproject-bundled \
      --with-libedit \
 && make menuselect.makeopts \
 && menuselect/menuselect \
      --disable BUILD_NATIVE \
      --enable CORE-SOUNDS-EN-GSM \
      --enable MOH-OPSOUND-GSM \
      menuselect.makeopts \
 && make -j"$(nproc)" \
 && make install \
 && make samples \
 && make install-logrotate \
 && rm -rf /usr/src/asterisk-src

CMD ["asterisk", "-f", "-vvv"]
EOF