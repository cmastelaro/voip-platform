FROM python:3.12-slim

ARG ASTERISK_VERSION=22

RUN apt-get update && apt-get install -y --no-install-recommends \
      wget ca-certificates \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /src

RUN wget -q https://downloads.asterisk.org/pub/telephony/asterisk/asterisk-${ASTERISK_VERSION}-current.tar.gz \
 && tar -xzf asterisk-${ASTERISK_VERSION}-current.tar.gz --strip-components=1 \
 && rm -rf $(ls -A | grep -v contrib) asterisk-${ASTERISK_VERSION}-current.tar.gz

RUN pip install --no-cache-dir alembic psycopg2-binary

WORKDIR /src/contrib/ast-db-manage

CMD ["alembic", "-c", "config.ini", "upgrade", "head"]