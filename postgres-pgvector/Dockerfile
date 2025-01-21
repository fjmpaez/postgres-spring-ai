# Usa la imagen oficial de PostgreSQL como base
FROM postgres:latest

# Instalación de las dependencias necesarias para pgvector
RUN apt-get update && apt-get install -y \
    postgresql-server-dev-17 \
    build-essential \
    git \
    && rm -rf /var/lib/apt/lists/*

# Clonamos el repositorio pgvector y lo compilamos
RUN git clone --branch v0.8.0 https://github.com/pgvector/pgvector.git /tmp/pgvector \
    && cd /tmp/pgvector \
    && make \
    && make install \
    && rm -rf /tmp/pgvector

# Habilita la extensión pgvector al iniciar el contenedor
#COPY ./init.sql /docker-entrypoint-initdb.d/

# Exponemos el puerto 5432 (el puerto por defecto de PostgreSQL)
EXPOSE 5432
