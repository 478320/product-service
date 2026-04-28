ARG ES_VERSION=8.13.4
FROM docker.m.daocloud.io/library/elasticsearch:${ES_VERSION}

ARG ES_VERSION=8.13.4

RUN elasticsearch-plugin install -b "https://get.infini.cloud/elasticsearch/analysis-ik/${ES_VERSION}" \
    && elasticsearch-plugin install -b "https://get.infini.cloud/elasticsearch/analysis-pinyin/${ES_VERSION}"

COPY analysis-ik/ /usr/share/elasticsearch/config/analysis-ik/
