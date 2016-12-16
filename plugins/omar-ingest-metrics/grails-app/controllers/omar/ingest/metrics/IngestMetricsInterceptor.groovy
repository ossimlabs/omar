package omar.ingest.metrics


class IngestMetricsInterceptor {

    boolean before() {
        //params.ingestId = params.id
        if(params.ingestId == null)
        {
            params.ingestId = params.id
        }

        params.remove("id")
        params.remove("format")

        true
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
